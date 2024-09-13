package com.example.app;

import static com.example.app.util.ShowAlertsUtility.mostrarSweetAlert;

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
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.app.data.db.DbManager;
import com.example.app.util.BiometricUtils;
import com.example.app.util.HashUtility;
import com.example.app.util.InputTextWatcher;

import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private DbManager dbManager;
    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("TAG", "Application Started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        setupBiometricAuthentication();
        setupLoginButton();
        setupRegisterLink();
    }

    private void initializeUI() {
        SharedPreferences sharedPreferences = getSharedPreferences("Storage", Context.MODE_PRIVATE);
        dbManager = new DbManager(getApplicationContext());

        editTextEmail = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        textInputLayoutEmail = findViewById(R.id.textInputLayoutEmail);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

        // Add TextWatchers for validation feedback
        editTextEmail.addTextChangedListener(new InputTextWatcher(textInputLayoutEmail));
        editTextPassword.addTextChangedListener(new InputTextWatcher(textInputLayoutPassword));
    }

    private void setupBiometricAuthentication() {
        Button btnBiometric = findViewById(R.id.fingerprint);
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
        Button btnLogin = findViewById(R.id.btnLogin);
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
        TextView linkRegister = findViewById(R.id.linkRegister);
        linkRegister.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RegisterUserActivity.class);
            startActivity(intent);
        });
    }

    private boolean isInputValid() {
        String email = Objects.requireNonNull(editTextEmail.getText()).toString().trim();
        String password = Objects.requireNonNull(editTextPassword.getText()).toString().trim();

        if (TextUtils.isEmpty(email)) {
            textInputLayoutEmail.setError("Email is required.");
            return false;
        } else if (!email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            textInputLayoutEmail.setError("Please enter a valid email address.");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            textInputLayoutPassword.setError("Password is required.");
            return false;
        }

        return true;
    }

    /**
     * Attempts to log in the user by validating their credentials against the database.
     *
     * @throws HashUtility.HashingException if an error occurs during password hashing.
     * @throws NoSuchAlgorithmException if the specified hashing algorithm is not found.
     */
    private void performLogin() throws HashUtility.HashingException, NoSuchAlgorithmException {
        String email = Objects.requireNonNull(editTextEmail.getText()).toString();
        String password = Objects.requireNonNull(editTextPassword.getText()).toString();

        dbManager.open();

        if (dbManager.validateUser(password, email)) {
            startActivity(new Intent(MainActivity.this, ShowPasswordsActivity.class));
            dbManager.close();
        } else {
            String title = "Invalid Credentials";
            String message = "Incorrect username or password.";
            mostrarSweetAlert(this, 3, title, message, null);
            dbManager.close();
        }
    }

    private void startBiometricAuthentication() {
        BiometricUtils.showBiometricPrompt(MainActivity.this, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (dbManager.userWhitBiometrics()) {
                    Log.i("TAG", "Biometric authentication successful.");
                    startActivity(new Intent(MainActivity.this, ShowPasswordsActivity.class));
                    Toast.makeText(MainActivity.this, "Authentication successful.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Log.e("TAG", "Biometric authentication error: " + errString);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Log.e("TAG", "Biometric authentication failed.");
            }
        });
    }
}
