package com.example.app;

import static com.example.app.util.InputValidator.getTrimmedText;
import static com.example.app.util.InputValidator.validateDescription;
import static com.example.app.util.InputValidator.validateName;
import static com.example.app.util.InputValidator.validatePassword;
import static com.example.app.util.InputValidator.validateUrl;
import static com.example.app.util.InputValidator.validateUsername;
import static com.example.app.util.ShowAlertsUtility.showAlert;

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

import com.example.app.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.app.data.db.DbManager;
import com.example.app.data.model.request.PasswordCredentials;
import com.example.app.util.HashUtility;
import com.example.app.util.InputTextWatcher;
import com.example.app.util.ShowAlertsUtility;

import java.util.Objects;

public class RegisterPasswordsActivity extends AppCompatActivity {
	private static final String TAG = "RegisterPasswordsActivity";
    // Instance variables for managing the database and UI elements
    private DbManager dbManager;
    private TextInputEditText editTextName;
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private TextInputEditText editTextUrl;
    private TextInputEditText editTextDescription;
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutUsername;
    private TextInputLayout textInputLayoutPassword;
    private TextInputLayout textInputLayoutUrl;
    private TextInputLayout textInputLayoutDescription;

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
        editTextName = findViewById(R.id.input_password_name);
        editTextUsername = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);
        editTextUrl = findViewById(R.id.input_url);
        editTextDescription = findViewById(R.id.input_password_note);
        textInputLayoutName = findViewById(R.id.layout_password_name);
        textInputLayoutUsername = findViewById(R.id.layout_email);
        textInputLayoutUrl = findViewById(R.id.layout_url);
        textInputLayoutPassword = findViewById(R.id.layout_password);
        textInputLayoutDescription = findViewById(R.id.layout_password_note);
        ImageView imageViewGenerate = findViewById(R.id.img_password_generate);

        // Add TextWatchers to EditTexts for real-time validation
        editTextName.addTextChangedListener(new InputTextWatcher(textInputLayoutName));
        editTextPassword.addTextChangedListener(new InputTextWatcher(textInputLayoutPassword));
        editTextUrl.addTextChangedListener(new InputTextWatcher(textInputLayoutUrl));

        // Set up the button to generate a random password
        imageViewGenerate.setOnClickListener(v -> {
            String randomPassword = HashUtility.generateRandomPassword(12);
            editTextPassword.setText(randomPassword);
            editTextPassword.setSelection(Objects.requireNonNull(editTextPassword.getText()).length());
            Toast.makeText(RegisterPasswordsActivity.this, "Password generated successfully", Toast.LENGTH_SHORT).show();
        });

        // Set up the button to navigate back to ShowPasswordsActivity
        Button btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(RegisterPasswordsActivity.this, ShowPasswordsActivity.class);
            startActivity(intent);
        });

        // Set up the button to save the password
        Button btnSave = findViewById(R.id.btn_save);
        btnSave.setOnClickListener(view -> {
            if (isInputValid()) {
                savePassword();
            }
        });
    }

    /**
     * Validates user input for registering or updating a password.
     *
     * @return true if the inputs are valid; false otherwise.
     */
    private boolean isInputValid() {
    
        if (validateName(editTextName, textInputLayoutName)) {
			return false;
        } else if (validateUsername(editTextUsername , textInputLayoutUsername)) {
			return false;
        } else if (validatePassword(editTextPassword, textInputLayoutPassword)) {
			return false;
        } else if (validateUrl(editTextUrl, textInputLayoutUrl)) {
			return false;
        } else if (validateDescription(editTextDescription, textInputLayoutDescription)) {
			return false;
        } else {
			return true;
		}
    
    }

    /**
     * Registers a new password in the database.
     */
    private void savePassword() {
        try {
            dbManager.open();

            // Retrieve the logged-in user's ID
            SharedPreferences sharedPreferences = getSharedPreferences("Storage", Context.MODE_PRIVATE);
            int userId = sharedPreferences.getInt("userId", -1);
            Log.i(TAG, "UserId from savePassword: " + userId);

            PasswordCredentials passwordCredentials = null;
            if (userId != -1) {
                passwordCredentials = new PasswordCredentials(
                        getTrimmedText(editTextUsername),
                        getTrimmedText(editTextUrl),
                        getTrimmedText(editTextPassword),
                        getTrimmedText(editTextDescription),
                        getTrimmedText(editTextName),
                        userId
                );
            }

            if (dbManager.passwordRegister(passwordCredentials)) {
                // Display success message
                ShowAlertsUtility.showAlert(this, 2, "Registration Successful", "The password has been successfully registered", sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    // Redirect to ShowPasswordsActivity
                    Intent intent = new Intent(RegisterPasswordsActivity.this, ShowPasswordsActivity.class);
                    startActivity(intent);
                    finish();
                });
            }
        } catch (SQLiteException e) {
            // Display error message for database errors
            showAlert(this, 1, "Error registering password", "Database registration failed", null);
        } catch (HashUtility.HashingException e) {
            showAlert(this, 1, "Error registering password", "Failed to encrypt the password", null);
        } catch (Exception e) {
            // Display generic error message
            showAlert(this, 1, "Error", "An unexpected error occurred.", null);
        } finally {
            dbManager.close();
        }
    }
}
