package com.example.app;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.app.data.db.DbManager;
import com.example.app.data.model.request.UserCredentials;
import com.example.app.util.BiometricUtils;
import com.example.app.util.HashUtility;
import com.example.app.util.InputTextWatcher;
import com.example.app.util.ShowAlertsUtility;

import java.util.Objects;

/**
 * Activity to handle user registration.
 */
public class RegisterUsersActivity extends AppCompatActivity {

    private static final String TAG = "RegisterUsersActivity";
    private DbManager databaseManager;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private TextInputLayout emailInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout confirmPasswordInputLayout;

    private SwitchCompat biometricSwitch;
    private boolean isBiometricSupported;
    private boolean isBiometricUserExisting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_users);

        initializeUIComponents();
        configureBiometricSwitch();
        setUpEventListeners();
    }

    /**
     * Initializes UI components and database manager.
     */
    private void initializeUIComponents() {
        databaseManager = new DbManager(getApplicationContext());
        emailEditText = findViewById(R.id.editTextUsernameReg);
        passwordEditText = findViewById(R.id.editPasswordReg);
        confirmPasswordEditText = findViewById(R.id.editPasswordReg2);
        emailInputLayout = findViewById(R.id.textInputLayoutReg);
        passwordInputLayout = findViewById(R.id.textInputLayout2Reg);
        confirmPasswordInputLayout = findViewById(R.id.textInputLayout2Reg2);
        biometricSwitch = findViewById(R.id.switch1);

        // Add text watchers for real-time validation
        emailEditText.addTextChangedListener(new InputTextWatcher(emailInputLayout));
        passwordEditText.addTextChangedListener(new InputTextWatcher(passwordInputLayout));
        confirmPasswordEditText.addTextChangedListener(new InputTextWatcher(confirmPasswordInputLayout));
    }

    /**
     * Configures the visibility of the biometric switch based on device support and existing user configuration.
     */
    private void configureBiometricSwitch() {
        isBiometricSupported = BiometricUtils.isBiometricPromptEnabled(this);
        isBiometricUserExisting = databaseManager.userWithBiometrics();

        Log.i(TAG, "Biometric support status: " + isBiometricSupported);
        Log.i(TAG, "Existing user with biometrics: " + isBiometricUserExisting);

        biometricSwitch.setVisibility(isBiometricSupported && !isBiometricUserExisting ? View.VISIBLE : View.GONE);
    }

    /**
     * Sets up event listeners for the activity's UI elements.
     */
    private void setUpEventListeners() {
        // Link to login activity
        TextView loginLinkTextView = findViewById(R.id.linkLogin);
        loginLinkTextView.setOnClickListener(view -> navigateToActivity(MainActivity.class));

        // Button to navigate to home activity
        MaterialButton homeButton = findViewById(R.id.btn_home);
        homeButton.setOnClickListener(view -> navigateToActivity(MainActivity.class));

        // Button to handle user registration
        Button registerButton = findViewById(R.id.btnRegistrar);
        registerButton.setOnClickListener(v -> {
            if (isInputValid()) {
                registerNewUser();
            }
        });
    }

    /**
     * Navigates to a specified activity.
     *
     * @param activityClass The class of the activity to navigate to.
     */
    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(RegisterUsersActivity.this, activityClass);
        startActivity(intent);
    }

    /**
     * Validates user input fields.
     *
     * @return true if input is valid, false otherwise.
     */
    private boolean isInputValid() {
        String email = Objects.requireNonNull(emailEditText.getText()).toString();
        String password = Objects.requireNonNull(passwordEditText.getText()).toString();
        String confirmPassword = Objects.requireNonNull(confirmPasswordEditText.getText()).toString();

        if (!isBiometricSupported || isBiometricUserExisting) {
            biometricSwitch.setChecked(false);
        }

        // Email validation
        if (TextUtils.isEmpty(email)) {
            emailInputLayout.setError("Email is required.");
            return false;
        } else if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            emailInputLayout.setError("Please enter a valid email address.");
            return false;
        }

        // Password validation
        if (TextUtils.isEmpty(password)) {
            passwordInputLayout.setError("Password is required.");
            return false;
        } else if (password.length() < 8) {
            passwordInputLayout.setError("Password must be at least 8 characters.");
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordInputLayout.setError("Please confirm your password.");
            return false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordInputLayout.setError("Passwords do not match.");
            return false;
        }

        return true;
    }

    /**
     * Registers the user with the provided credentials.
     */
    private void registerNewUser() {
        String registrationErrorMessage = "Error registering user.";
        try {
            databaseManager.open();
            UserCredentials userCredentials = new UserCredentials(
                    Objects.requireNonNull(emailEditText.getText()).toString(),
                    Objects.requireNonNull(passwordEditText.getText()).toString()
            );

            boolean isRegistrationSuccessful = databaseManager.userRegister(userCredentials, biometricSwitch.isChecked() ? 1 : 0);

            if (isRegistrationSuccessful) {
                ShowAlertsUtility.showSuccessAlert(
                        this,
                        "Registration Successful",
                        "User has been successfully registered.",
                        sweetAlertDialog -> {
                            sweetAlertDialog.dismissWithAnimation();
                            navigateToActivity(MainActivity.class);
                        }
                );
            } else {
                ShowAlertsUtility.showErrorAlert(
                        this,
                        "Registration Error",
                        "The email " + userCredentials.getEmail() + " is already registered."
                );
            }
        } catch (SQLiteException e) {
            ShowAlertsUtility.showErrorAlert(
                    this,
                    registrationErrorMessage,
                    "Failed to register the user in the database."
            );
            Log.e(TAG, "Database error: ", e);
        } catch (HashUtility.SaltException e) {
            ShowAlertsUtility.showErrorAlert(
                    this,
                    registrationErrorMessage,
                    "Error generating salt for password."
            );
            Log.e(TAG, "Salt generation error: ", e);
        } catch (HashUtility.HashingException e) {
            ShowAlertsUtility.showErrorAlert(
                    this,
                    registrationErrorMessage,
                    "Error hashing the password."
            );
            Log.e(TAG, "Hashing error: ", e);
        } catch (Exception e) {
            ShowAlertsUtility.showErrorAlert(
                    this,
                    "Unexpected Error",
                    "An unexpected error occurred."
            );
            Log.e(TAG, "Unexpected error: ", e);
        } finally {
            databaseManager.close();
        }
    }
}
