package com.example.app;

import static com.example.app.util.InputValidator.validateDescription;
import static com.example.app.util.InputValidator.validateName;
import static com.example.app.util.InputValidator.validatePassword;
import static com.example.app.util.InputValidator.validateUrl;
import static com.example.app.util.InputValidator.validateUsername;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.example.app.util.InputValidator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.app.data.db.DbManager;
import com.example.app.data.model.request.PasswordCredentials;
import com.example.app.data.model.response.PasswordResponse;

import java.util.Objects;

public class EditPasswordsActivity extends AppCompatActivity {
	private static final String TAG = "EditPasswordsActivity";
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
    private TextInputLayout textInputLayoutUsername;
    private TextInputLayout textInputLayoutDescription;
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
        setContentView(R.layout.activity_edit_passwords);

        sharedPreferences = getSharedPreferences("Storage", MODE_PRIVATE);
        dbManager = new DbManager(this);

        initializeUI();
        retrievePasswordIdFromIntent();
        loadPasswordDetails();
        setupButtonListeners();
    }

    private void initializeUI() {
        editTextName = findViewById(R.id.input_password_name);
        editTextUsername = findViewById(R.id.input_email);
        editTextPassword = findViewById(R.id.input_password);
        editTextUrl = findViewById(R.id.input_url);
        editTextDescription = findViewById(R.id.input_password_note);
        textInputLayoutUsername = findViewById(R.id.layout_email);
        textInputLayoutName = findViewById(R.id.layout_password_name);
        textInputLayoutDescription = findViewById(R.id.layout_password_note);
        textInputLayoutUrl = findViewById(R.id.layout_url);
        textInputLayoutPassword = findViewById(R.id.layout_password);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
    }

    private void retrievePasswordIdFromIntent() {
        passwordId = getIntent().getIntExtra("passwordId", 0);
        Log.i(TAG, "Password ID: " + passwordId);
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
            Log.e(TAG, "Error: " + e.getMessage());
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
            if (isInputValid()) {
                updatePassword();
            }
        });

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(EditPasswordsActivity.this, ShowPasswordsActivity.class);
            startActivity(intent);
        });
    }

    private void updatePassword() {
        String name = InputValidator.getTrimmedText(editTextName);
        String username = InputValidator.getTrimmedText(editTextUsername);
        String password = InputValidator.getTrimmedText(editTextPassword);
        String url = InputValidator.getTrimmedText(editTextUrl);
        String description = InputValidator.getTrimmedText(editTextDescription);

        try {
            int userId = sharedPreferences.getInt("userId", -1);
            PasswordCredentials passwordCredentials = new PasswordCredentials(username, url, password, description, name, userId);

            if (dbManager.updatePassword(passwordId, passwordCredentials, userId)) {
                showToast("Password successfully updated.");
                startActivity(new Intent(EditPasswordsActivity.this, ShowPasswordsActivity.class));
                finish();
            } else {
                showToast("Failed to update password.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
        }
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

    private void showToast(String message) {
        Toast.makeText(EditPasswordsActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
