package com.example.app;

import static com.example.app.util.InputValidator.getTrimmedText;
import static com.example.app.util.InputValidator.validateConfirmPassword;
import static com.example.app.util.InputValidator.validatePassword;
import static com.example.app.util.InputValidator.validateUsername;

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

import com.example.app.R;
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
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private TextInputEditText editTextConfirmPassword;
    private TextInputLayout textInputLayoutUsername ;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutConfirmPassword;

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
        editTextUsername = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);
        editTextConfirmPassword = findViewById(R.id.input_password_confirm);
        textInputLayoutUsername = findViewById(R.id.layout_email);
        textInputLayoutPassword = findViewById(R.id.layout_password);
        textInputLayoutConfirmPassword = findViewById(R.id.layout_password_confirm);
        biometricSwitch = findViewById(R.id.switch_fingerprint);

        // Add text watchers for real-time validation
        editTextUsername.addTextChangedListener(new InputTextWatcher(textInputLayoutUsername));
        editTextPassword.addTextChangedListener(new InputTextWatcher(textInputLayoutPassword));
        editTextConfirmPassword.addTextChangedListener(new InputTextWatcher(textInputLayoutConfirmPassword));
    }

    /**
     * Configures the visibility of the biometric switch based on device support and existing user configuration.
     */
    private void configureBiometricSwitch() {
        isBiometricSupported = BiometricUtils.isBiometricPromptEnabled(this);
        isBiometricUserExisting = databaseManager.userWhitBiometrics();

        Log.i(TAG, "Biometric support status: " + isBiometricSupported);
        Log.i(TAG, "Existing user with biometrics: " + isBiometricUserExisting);

        biometricSwitch.setVisibility(isBiometricSupported && !isBiometricUserExisting ? View.VISIBLE : View.GONE);
    }

    /**
     * Sets up event listeners for the activity's UI elements.
     */
    private void setUpEventListeners() {
        // Link to login activity
        TextView loginLinkTextView = findViewById(R.id.text_login);
        loginLinkTextView.setOnClickListener(view -> navigateToMainActivity());

        // Button to navigate to home activity
        MaterialButton homeButton = findViewById(R.id.btn_save);
        homeButton.setOnClickListener(view -> navigateToMainActivity());

        // Button to handle user registration
        Button registerButton = findViewById(R.id.btn_signup);
        registerButton.setOnClickListener(v -> {
            if (isInputValid()) {
                registerNewUser();
            }
        });
    }

    /**
     * Navigates to a specified activity.
     */
    private void navigateToMainActivity() {
        Intent intent = new Intent(RegisterUsersActivity.this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Validates user input for registering or updating a password.
     *
     * @return true if the inputs are valid; false otherwise.
     */
    private boolean isInputValid() {

        if (!isBiometricSupported || isBiometricUserExisting) {
            biometricSwitch.setChecked(false);
        } else if (validateUsername(editTextUsername, textInputLayoutUsername)) {
			return false;
        } else if (validatePassword(editTextPassword, textInputLayoutPassword)) {
			return false;
        } else if (validateConfirmPassword(editTextConfirmPassword, textInputLayoutConfirmPassword)) {
			return false;
        }

        return true;
    }

    /**
     * Registers the user with the provided credentials.
     */
    private void registerNewUser() {
        try {
            databaseManager.open();
            UserCredentials userCredentials = new UserCredentials(
                    getTrimmedText(editTextUsername),
                    getTrimmedText(editTextPassword)
            );

            boolean isRegistrationSuccessful = databaseManager.userRegister(userCredentials, biometricSwitch.isChecked() ? 1 : 0);

            if (isRegistrationSuccessful) {
                ShowAlertsUtility.showAlert(
                        this,
                        2,
                        "Registration Successful",
                        "User has been successfully registered.",
                        sweetAlertDialog -> {
                            sweetAlertDialog.dismissWithAnimation();
							Intent intent = new Intent(RegisterUsersActivity.this, ShowPasswordsActivity.class);
							startActivity(intent);
							finish();
                        }
                );
            } else {
                ShowAlertsUtility.showAlert(
                        this,
                        1,
                        "Registration Error",
                        "The email " + userCredentials.getEmail() + " is already registered.",
                        null
                );
            }
        } catch (SQLiteException e) {
            ShowAlertsUtility.showAlert(
                    this,
                    1,
                    "Error registering user.",
                    "Failed to register the user in the database.",
                    null
            );
            Log.e(TAG, "Database error: ", e);
        } catch (HashUtility.SaltException e) {
            ShowAlertsUtility.showAlert(
                    this,
                    1,
                    "Error registering user.",
                    "Error generating salt for password.",
                    null
            );
            Log.e(TAG, "Salt generation error: ", e);
        } catch (HashUtility.HashingException e) {
            ShowAlertsUtility.showAlert(
                    this,
                    1,
                    "Error registering user.",
                    "Error hashing the password.",
                    null
            );
            Log.e(TAG, "Hashing error: ", e);
        } catch (Exception e) {
            ShowAlertsUtility.showAlert(
                    this,
                    1,
                    "Unexpected Error",
                    "An unexpected error occurred.",
                    null
            );
            Log.e(TAG, "Unexpected error: ", e);
        } finally {
            databaseManager.close();
        }
    }
}
