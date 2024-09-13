package com.example.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.app.data.db.DbManager;
import com.example.app.data.model.request.PasswordCredentials;
import com.example.app.data.model.response.PasswordResponse;

import java.util.Objects;

public class EditPasswordsActivity extends AppCompatActivity {

    private Button btnBack;
    private Button btnSave;

    private DbManager dbManager;
    private TextInputEditText editTextName;
    private TextInputEditText editTextUsername;
    private TextInputEditText editTextPassword;
    private TextInputEditText editTextUrl;
    private TextInputEditText editTextDescription;
    private SharedPreferences sharedPreferences;
    private TextInputLayout textInputLayoutName;
    private TextInputLayout textInputLayoutUrl;
    private TextInputLayout textInputLayoutPassword;
    private int passwordId; // Stores the ID of the password being edited

    /**
     * Called when the activity is first created. Initializes and configures the user interface.
     *
     * @param savedInstanceState The previously saved state of the activity, or null if none.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        sharedPreferences = getSharedPreferences("Storage", MODE_PRIVATE);
        dbManager = new DbManager(this);

        initializeUI();
        retrievePasswordIdFromIntent();
        loadPasswordDetails();
        setupButtonListeners();
    }

    private void initializeUI() {
        editTextName = findViewById(R.id.editTextName);
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextUrl = findViewById(R.id.editTextUrl);
        editTextDescription = findViewById(R.id.editTextDescription);
        textInputLayoutName = findViewById(R.id.textInputLayoutName);
        textInputLayoutUrl = findViewById(R.id.textInputLayoutUrl);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
    }

    private void retrievePasswordIdFromIntent() {
        passwordId = getIntent().getIntExtra("idColumn", 0);
        Log.i("TAG", "Password ID: " + passwordId);
    }

    private void loadPasswordDetails() {
        int userId = sharedPreferences.getInt("userId", -1);
        try {
            PasswordResponse passwordDetails = dbManager.getPasswordDetails(passwordId, userId);
            if (passwordDetails != null) {
                populateFields(passwordDetails);
            } else {
                showToast("Failed to load password details.");
            }
        } catch (Exception e) {
            Log.e("TAG", "Error: " + e.getMessage());
        }
    }

    private void populateFields(PasswordResponse passwordDetails) {
        editTextName.setText(passwordDetails.getName());
        editTextUsername.setText(passwordDetails.getUsername());
        editTextPassword.setText(passwordDetails.getKeyword());
        editTextUrl.setText(passwordDetails.getUrl());
        editTextDescription.setText(passwordDetails.getDescription());
    }

    private void setupButtonListeners() {
        btnSave.setOnClickListener(view -> {
            if (validateInput()) {
                updatePassword();
            }
        });

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(EditPasswordsActivity.this, ShowPasswordsActivity.class);
            startActivity(intent);
        });
    }

    private void updatePassword() {
        String name = Objects.requireNonNull(editTextName.getText()).toString();
        String username = Objects.requireNonNull(editTextUsername.getText()).toString();
        String password = Objects.requireNonNull(editTextPassword.getText()).toString();
        String url = Objects.requireNonNull(editTextUrl.getText()).toString();
        String description = Objects.requireNonNull(editTextDescription.getText()).toString();

        try {
            int userId = sharedPreferences.getInt("userId", -1);
            PasswordCredentials passwordCredentials = new PasswordCredentials(username, url, password, description, name, userId);

            if (dbManager.updatePassword(passwordId, passwordCredentials, userId)) {
                showToast("Password successfully updated.");
                startActivity(new Intent(EditPassworddActivity.this, ShowPasswordsActivity.class));
                finish();
            } else {
                showToast("Failed to update password.");
            }
        } catch (Exception e) {
            Log.e("TAG", "Error: " + e.getMessage());
        }
    }

    /**
     * Validates user input for updating the password.
     *
     * @return true if inputs are valid, false otherwise.
     */
    private boolean validateInput() {
        String url = Objects.requireNonNull(editTextUrl.getText()).toString();
        String password = Objects.requireNonNull(editTextPassword.getText()).toString();
        String name = Objects.requireNonNull(editTextName.getText()).toString();

        if (password.isEmpty()) {
            textInputLayoutPassword.setError("Please enter a password.");
            clearOtherErrors(textInputLayoutPassword);
            return false;
        } else if (name.isEmpty()) {
            textInputLayoutName.setError("Please enter a name.");
            clearOtherErrors(textInputLayoutName);
            return false;
        } else if (!url.isEmpty() && !url.matches("((https?://)?(www\\.)?[a-zA-Z0-9-]+(\\.[a-z]{2,})+(/\\S*)?)")) {
            textInputLayoutUrl.setError("Please enter a valid URL.");
            clearOtherErrors(textInputLayoutUrl);
            return false;
        } else {
            clearAllErrors();
            return true;
        }
    }

    private void clearOtherErrors(TextInputLayout layoutToSkip) {
        if (layoutToSkip != textInputLayoutName) textInputLayoutName.setError(null);
        if (layoutToSkip != textInputLayoutUrl) textInputLayoutUrl.setError(null);
    }

    private void clearAllErrors() {
        textInputLayoutName.setError(null);
        textInputLayoutUrl.setError(null);
        textInputLayoutPassword.setError(null);
    }

    private void showToast(String message) {
        Toast.makeText(EditPasswordsActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
