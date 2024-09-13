package com.example.app;

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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.app.data.db.DbManager;
import com.example.app.data.model.response.PasswordResponse;

import java.util.Objects;

public class ViewPassActivity extends AppCompatActivity {

    private Button backButton;
    private ImageView copyPasswordIcon;
    private TextInputEditText passwordEditText;
    private TextInputLayout passwordInputLayout;
    private DbManager dbManager;
    private int userId;
    private int passwordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pass);

        // Initialize UI elements
        backButton = findViewById(R.id.botonYouPassViewPass);
        copyPasswordIcon = findViewById(R.id.imageViewCopy);
        passwordEditText = findViewById(R.id.editTextPassword);
        passwordInputLayout = findViewById(R.id.textInputLayout3);

        // Initialize DbManager
        dbManager = new DbManager(this);

        // Retrieve user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Storage", MODE_PRIVATE);
        userId = sharedPreferences.getInt("userId", -1);

        // Get the password ID from the Intent
        Intent intent = getIntent();
        passwordId = intent.getIntExtra("idColumna", 0);

        // Log the retrieved password ID
        Log.i("ViewPassActivity", "Password ID: " + passwordId);

        // Load and display password details
        loadPasswordDetails();

        // Set up the copy password button
        copyPasswordIcon.setOnClickListener(v -> copyPasswordToClipboard());

        // Set up the back button to return to the previous activity
        backButton.setOnClickListener(view -> {
            Intent backIntent = new Intent(ViewPassActivity.this, ShowPasswordsActivity.class);
            startActivity(backIntent);
        });
    }

    private void loadPasswordDetails() {
        try {
            PasswordResponse passwordDetails = dbManager.getPasswordDetails(passwordId, userId);
            if (passwordDetails != null) {
                // Populate the UI with password details
                ((TextView) findViewById(R.id.editTextName)).setText(passwordDetails.getName());
                ((TextView) findViewById(R.id.editTextUsuario)).setText(passwordDetails.getUsername());
                passwordEditText.setText(passwordDetails.getKeyword());
                ((TextView) findViewById(R.id.editTextUrl)).setText(passwordDetails.getUrl());
                ((TextView) findViewById(R.id.editTextDescripcion)).setText(passwordDetails.getDescription());
            } else {
                // Show error message if details cannot be retrieved
                Toast.makeText(this, "Error retrieving password details", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("ViewPassActivity", "Error: " + e.getMessage());
            Toast.makeText(this, "An error occurred while retrieving password details", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyPasswordToClipboard() {
        String password = Objects.requireNonNull(passwordInputLayout.getEditText()).getText().toString();
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
