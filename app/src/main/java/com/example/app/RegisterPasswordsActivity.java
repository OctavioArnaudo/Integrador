package com.example.app;

import static com.example.app.util.ShowAlertsUtility.mostrarSweetAlert;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.app.data.db.DbManager;
import com.example.app.data.model.request.PasswordCredentials;
import com.example.app.util.HashUtility;
import com.example.app.util.InputTextWatcher;
import com.example.app.util.ShowAlertsUtility;

import java.util.Objects;

public class RegisterPasswordsActivity extends AppCompatActivity {

    // Instance variables for managing the database and UI elements
    private DbManager dbManager;
    private TextInputEditText editTextName;
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private TextInputEditText editTextUrl;
    private TextInputEditText editTextDescription;
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutUrl;
    private TextInputLayout textInputLayoutPassword;

    private Button btnBack;
    private Button btnSave;

    /**
     * Called when the activity is first created. Initializes UI elements and sets up event listeners.
     *
     * @param savedInstanceState Bundle containing the activity's previously saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_passwords);

        // Initialize variables
        dbManager = new DbManager(getApplicationContext());
        editTextName = findViewById(R.id.editTextName);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUrl = findViewById(R.id.editTextUrl);
        editTextDescription = findViewById(R.id.editTextDescription);
        textInputLayoutName = findViewById(R.id.textInputLayoutName);
        textInputLayoutUrl = findViewById(R.id.textInputLayoutUrl);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        ImageView imageViewGenerate = findViewById(R.id.imageViewGenerate);

        // Add TextWatchers to EditTexts for real-time validation
        editTextName.addTextChangedListener(new InputTextWatcher(textInputLayoutName));
        editTextPassword.addTextChangedListener(new InputTextWatcher(textInputLayoutPassword));
        editTextUrl.addTextChangedListener(new InputTextWatcher(textInputLayoutUrl));

        // Set up the button to generate a random password
        imageViewGenerate.setOnClickListener(v -> {
            String randomPassword = HashUtility.generateRandomPassword(12);
            editTextPassword.setText(randomPassword);
            editTextPassword.setSelection(Objects.requireNonNull(editTextPassword.getText()).length());
            Toast.makeText(RegisterPasswordActivity.this, "Password generated successfully", Toast.LENGTH_SHORT).show();
        });

        // Set up the button to navigate back to ShowPasswordsActivity
        btnBack = findViewById(R.id.btnBackSave);
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterPasswordsActivity.this, ShowPasswordsActivity.class);
            startActivity(intent);
        });

        // Set up the button to save the password
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(view -> {
            if (isInputValid()) {
                savePassword();
            }
        });
    }

    /**
     * Validates user inputs when registering a new password.
     *
     * @return true if the inputs are valid; false otherwise.
     */
    private boolean isInputValid() {
        String url = Objects.requireNonNull(editTextUrl.getText()).toString();
        String password = Objects.requireNonNull(editTextPassword.getText()).toString();
        String name = Objects.requireNonNull(editTextName.getText()).toString();

        if (password.isEmpty()) {
            textInputLayoutPassword.setError("Please enter a password");
            return false;
        } else if (name.isEmpty()) {
            textInputLayoutName.setError("Please enter a name");
            return false;
        } else if (!url.isEmpty() && !url.matches("((https?://)?(www\\.)?[a-zA-Z0-9-]+(\\.[a-z]{2,})+(/\\S*)?)")) {
            textInputLayoutUrl.setError("Please enter a valid URL");
            return false;
        }
        return true;
    }

    /**
     * Registers a new password in the database.
     */
    private void savePassword() {
        final String ERROR_MESSAGE = "Error registering password";
        try {
            dbManager.open();

            // Retrieve the logged-in user's ID
            SharedPreferences sharedPreferences = getSharedPreferences("Storage", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userId", -1);
            Log.i("TAG", "UserId from savePassword: " + userId);

            PasswordCredentials passwordCredentials = null;
            if (userId != -1) {
                passwordCredentials = new PasswordCredentials(
                        Objects.requireNonNull(editTextUsername.getText()).toString(),
                        Objects.requireNonNull(editTextUrl.getText()).toString(),
                        Objects.requireNonNull(editTextPassword.getText()).toString(),
                        Objects.requireNonNull(editTextDescription.getText()).toString(),
                        Objects.requireNonNull(editTextName.getText()).toString(),
                        userId
                );
            }

            if (dbManager.registerPassword(passwordCredentials)) {
                // Display success message
                ShowAlertsUtility.mostrarSweetAlert(this, 2, "Registration Successful", "The password has been successfully registered", sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    // Redirect to ShowPasswordsActivity
                    Intent intent = new Intent(RegisterPasswordActivity.this, ShowPasswordsActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        } catch (SQLiteException e) {
            // Display error message for database errors
            mostrarSweetAlert(this, 1, ERROR_MESSAGE, "Database registration failed", null);
            e.printStackTrace();
        } catch (HashUtility.HashingException e) {
            mostrarSweetAlert(this, 1, ERROR_MESSAGE, "Failed to encrypt the password", null);
        } catch (Exception e) {
            // Display generic error message
            e.printStackTrace();
            mostrarSweetAlert(this, 1, "Error", "An unexpected error occurred.", null);
        } finally {
            dbManager.close();
        }
    }
}
