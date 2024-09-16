package com.example.app;

import static com.example.app.util.InputValidator.getTrimmedText;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.app.data.db.DbManager;
import com.example.app.data.model.response.PasswordResponse;

import java.util.Objects;

public class ViewPasswordsActivity extends AppCompatActivity {
	private static final String TAG = "ViewPasswordsActivity";
    private TextInputEditText editTextPassword;
    private TextInputLayout textInputLayoutPassword;
    private DbManager dbManager;
    private int userId;
    private int passwordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_passwords);

        // Initialize UI elements
        Button backButton = findViewById(R.id.btn_back);
        ImageView copyIcon = findViewById(R.id.view_copy);
        editTextPassword = findViewById(R.id.input_password);
        textInputLayoutPassword = findViewById(R.id.layout_password);

        // Initialize DbManager
        dbManager = new DbManager(this);

        // Retrieve user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Storage", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // Get the password ID from the Intent
        Intent intent = getIntent();
        passwordId = intent.getIntExtra("passwordId", 0);

        // Log the retrieved password ID
        Log.i(TAG, "Password ID: " + passwordId);

        // Load and display password details
        loadPasswordDetails();

        // Set up the copy password button
        copyIcon.setOnClickListener(v -> copyPasswordToClipboard());

        // Set up the back button to return to the previous activity
        backButton.setOnClickListener(view -> {
            Intent backIntent = new Intent(ViewPasswordsActivity.this, ShowPasswordsActivity.class);
            startActivity(backIntent);
        });
    }

    private void loadPasswordDetails() {
        try {
            PasswordResponse passwordDetails = dbManager.getPasswordDetails(passwordId, userId);
            if (passwordDetails != null) {
                // Populate the UI with password details
                ((TextView) findViewById(R.id.text_password_name)).setText(passwordDetails.getName());
                ((TextView) findViewById(R.id.input_email)).setText(passwordDetails.getUsername());
                editTextPassword.setText(passwordDetails.getKeyword());
                ((TextView) findViewById(R.id.input_url)).setText(passwordDetails.getUrl());
                ((TextView) findViewById(R.id.input_password_note)).setText(passwordDetails.getDescription());
            } else {
                // Show error message if details cannot be retrieved
                Toast.makeText(this, "Error retrieving password details", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.getMessage());
            Toast.makeText(this, "An error occurred while retrieving password details", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyPasswordToClipboard() {
        String password = getTrimmedText(textInputLayoutPassword.getEditText());
        if (!password.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("password", password);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No password to copy", Toast.LENGTH_SHORT).show();
        }
    }
}
