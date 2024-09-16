package com.example.app;

import static com.example.app.util.InputValidator.getTrimmedText;
import static com.example.app.util.InputValidator.validatePassword;
import static com.example.app.util.InputValidator.validateUsername;
import static com.example.app.util.ShowAlertsUtility.showAlert;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.app.data.db.DbManager;
import com.example.app.util.BiometricUtils;
import com.example.app.util.HashUtility;
import com.example.app.util.InputTextWatcher;

import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
	private static final String TAG = "MainActivity";
    private DbManager dbManager;
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private TextInputLayout textInputLayoutUsername;
    private TextInputLayout textInputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        setupBiometricAuthentication();
        setupLoginButton();
        setupRegisterButton();
    }

    private void initializeUI() {
        dbManager = new DbManager(getApplicationContext());

        editTextUsername = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);
        textInputLayoutUsername = findViewById(R.id.layout_email);
        textInputLayoutPassword = findViewById(R.id.layout_password);

        // Add TextWatchers for validation feedback
        editTextUsername.addTextChangedListener(new InputTextWatcher(textInputLayoutUsername));
        editTextPassword.addTextChangedListener(new InputTextWatcher(textInputLayoutPassword));
    }

    private void setupBiometricAuthentication() {
        Button btnBiometric = findViewById(R.id.btn_fingerprint);
        SharedPreferences sharedPreferences = getSharedPreferences("Storage", Context.MODE_PRIVATE);

        boolean isBiometricEnabled = sharedPreferences.getInt("biometric", -1) == 1;
        boolean userHasBiometrics = dbManager.userWhitBiometrics();

        if (isBiometricEnabled && BiometricUtils.isBiometricPromptEnabled(MainActivity.this) && userHasBiometrics) {
            btnBiometric.setVisibility(View.VISIBLE);
            btnBiometric.setOnClickListener(view -> startBiometricAuthentication());
        } else {
            btnBiometric.setVisibility(View.GONE);
        }
    }

    private void setupLoginButton() {
        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            if (isInputValid()) {
                try {
                    performLogin();
                } catch (HashUtility.HashingException | NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void setupRegisterLink() {
        TextView linkRegister = findViewById(R.id.text_signup);
        linkRegister.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterUsersActivity.class);
            startActivity(intent);
            finish();
        });
    }
    
    /**
     * Validates user input for registering or updating a password.
     *
     * @return true if the inputs are valid; false otherwise.
     */
    private boolean isInputValid() {
    
		if (validateUsername(editTextUsername , textInputLayoutUsername)) {
			return false;
        } else return !validatePassword(editTextPassword, textInputLayoutPassword);

    }
    
    /**
     * Attempts to log in the user by validating their credentials against the database.
     *
     * @throws HashUtility.HashingException if an error occurs during password hashing.
     * @throws NoSuchAlgorithmException if the specified hashing algorithm is not found.
     */
    private void performLogin() throws HashUtility.HashingException, NoSuchAlgorithmException {
        String email = getTrimmedText(editTextUsername);
        String password = getTrimmedText(editTextPassword);

        dbManager.open();

        if (dbManager.validateUser(password, email)) {
            startActivity(new Intent(MainActivity.this, ShowPasswordsActivity.class));
            dbManager.close();
            finish();
        } else {
            showAlert(this, 3, "Invalid Credentials", "Incorrect username or password", null);
            dbManager.close();
        }
    }

    private void startBiometricAuthentication() {
        BiometricUtils.showBiometricPrompt(MainActivity.this, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (dbManager.userWhitBiometrics()) {
                    Log.i(TAG, "Biometric authentication successful.");
                    startActivity(new Intent(MainActivity.this, ShowPasswordsActivity.class));
                    Toast.makeText(MainActivity.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.e(TAG, "Biometric authentication error: " + errString);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.e(TAG, "Biometric authentication failed.");
            }
        });
    }
}
