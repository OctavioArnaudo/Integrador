package com.example.app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.app.data.db.DbManager;
import com.example.app.data.model.response.PasswordResponse;
import com.example.app.util.InputTextWatcher;
import com.example.app.util.ShowAlertsUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ShowPasswordsActivity extends AppCompatActivity {

    private FloatingActionButton addButton;
    private ImageView menuIcon;
    private ImageButton eyeIcon;
    private ImageButton editIcon;
    private ImageButton deleteIcon;
    private DbManager dbManager;
    private ScrollView scrollView;
    private TextInputEditText searchEditText;
    private List<PasswordResponse> passwords;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_passwords);

        // Initialize DbManager and UI elements
        dbManager = new DbManager(this);
        TableLayout passwordTable = findViewById(R.id.tableLayout);
        TextInputLayout searchInputLayout = findViewById(R.id.textInputLayoutName);
        searchEditText = findViewById(R.id.editTextSearch);

        // Set up the TextWatcher to filter passwords
        searchEditText.addTextChangedListener(new InputTextWatcher(searchInputLayout) {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchText = s.toString().trim();
                List<PasswordResponse> filteredPasswords = filterPasswords(passwords, searchText);
                displayPasswords(filteredPasswords);
            }
        });

        // Retrieve user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Storage", MODE_PRIVATE);
        if (sharedPreferences.contains("userId")) {
            userId = sharedPreferences.getInt("userId", -1);
            Log.i("TAG", "Displaying passwords for user ID: " + userId);
            passwords = dbManager.getPasswordsListForUserId(userId);
            Log.i("TAG", "Password list size: " + passwords.size());
            displayPasswords(passwords);
        } else {
            Log.e("ShowPasswordsActivity", "User ID not found in SharedPreferences");
        }

        // Set up the add button
        addButton = findViewById(R.id.btn_add);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(ShowPasswordsActivity.this, RegisterPasswordActivity.class);
            startActivity(intent);
        });

        // Set up the menu icon
        menuIcon = findViewById(R.id.menu_view);
        menuIcon.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(ShowPasswordsActivity.this, menuIcon);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.option_1) {
                    Intent aboutIntent = new Intent(ShowPasswordsActivity.this, AboutActivity.class);
                    startActivity(aboutIntent);
                    return true;
                } else if (itemId == R.id.option_2) {
                    Intent mainIntent = new Intent(ShowPasswordsActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    return true;
                } else {
                    return false;
                }
            });
            popupMenu.show();
        });

        // Set up scroll view indicator
        scrollView = findViewById(R.id.scrollView);
        ImageView scrollIndicator = findViewById(R.id.scrollIndicator);
        scrollView.post(() -> {
            if (scrollView.getChildAt(0).getHeight() > scrollView.getHeight()) {
                scrollIndicator.setVisibility(View.VISIBLE);
            }
        });
        scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            if (scrollView.getChildAt(0).getBottom() <= (scrollView.getHeight() + scrollView.getScrollY())) {
                scrollIndicator.animate().rotation(180).start();
            } else {
                scrollIndicator.setVisibility(View.VISIBLE);
                scrollIndicator.animate().rotation(0).start();
            }
        });
    }

    private void displayPasswords(List<PasswordResponse> passwords) {
        try {
            TableLayout passwordTable = findViewById(R.id.tableLayout);
            TextView noPasswordsText = findViewById(R.id.txtNoPassword);
            ImageView noPasswordsImage = findViewById(R.id.imageView);
            passwordTable.removeAllViews();

            if (passwords.isEmpty()) {
                noPasswordsText.setVisibility(View.VISIBLE);
                noPasswordsImage.setVisibility(View.VISIBLE);
                passwordTable.setVisibility(View.GONE);
            } else {
                noPasswordsText.setVisibility(View.GONE);
                noPasswordsImage.setVisibility(View.GONE);
                passwordTable.setVisibility(View.VISIBLE);

                LayoutInflater inflater = LayoutInflater.from(this);
                for (PasswordResponse password : passwords) {
                    TableRow row = (TableRow) inflater.inflate(R.layout.row_password, null);

                    eyeIcon = row.findViewById(R.id.icon_eye);
                    editIcon = row.findViewById(R.id.icon_pen);
                    deleteIcon = row.findViewById(R.id.icon_trash);

                    TextView nameTextView = row.findViewById(R.id.textView);
                    String name = password.getName();
                    if (name.length() > 10) {
                        nameTextView.setText(name.substring(0, 10) + "...");
                    } else {
                        nameTextView.setText(name);
                    }
                    setupActionIcons(password.getId());

                    passwordTable.addView(row);
                }
            }
        } catch (SQLException e) {
            Log.e("ShowPasswordsActivity", "Database error: " + e.getMessage());
            ShowAlertsUtility.showErrorAlert(this, "Database Error", "An error occurred while fetching passwords.");
        } catch (Exception e) {
            Log.e("ShowPasswordsActivity", "Unexpected error: " + e.getMessage());
            ShowAlertsUtility.showErrorAlert(this, "Error", "An unexpected error occurred.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.close(); // Close database when activity is destroyed
    }

    private void setupActionIcons(int passwordId) {
        deleteIcon.setTag(passwordId);

        eyeIcon.setOnClickListener(v -> {
            Intent viewIntent = new Intent(ShowPasswordsActivity.this, ViewPassActivity.class);
            viewIntent.putExtra("passwordId", passwordId);
            viewIntent.putExtra("userId", userId);
            startActivity(viewIntent);
        });

        editIcon.setOnClickListener(v -> {
            Intent editIntent = new Intent(ShowPasswordsActivity.this, EditPasswordActivity.class);
            editIntent.putExtra("passwordId", passwordId);
            editIntent.putExtra("userId", userId);
            startActivity(editIntent);
        });

        deleteIcon.setOnClickListener(v -> {
            int idToDelete = (int) v.getTag();
            ShowAlertsUtility.showDeleteConfirmationAlert(this,
                    "Are you sure you want to delete this password?",
                    "This action cannot be undone.",
                    sweetAlertDialog -> {
                        try {
                            dbManager.deletePassword(idToDelete);
                            passwords = dbManager.getPasswordsListForUserId(userId);
                            displayPasswords(passwords);
                            ShowAlertsUtility.showSuccessAlert(this, "Success", "Password deleted successfully.");
                        } catch (Exception e) {
                            Log.e("ShowPasswordsActivity", "Error deleting password: " + e.getMessage());
                            ShowAlertsUtility.showErrorAlert(this, "Error", "An error occurred while deleting the password.");
                        }
                    },
                    sweetAlertDialog -> ShowAlertsUtility.showInfoAlert(this, "Cancelled", "Operation cancelled."));
        });
    }

    private List<PasswordResponse> filterPasswords(List<PasswordResponse> passwords, String query) {
        List<PasswordResponse> filteredList = new ArrayList<>();
        String queryLower = query.toLowerCase();

        for (PasswordResponse password : passwords) {
            if (password.getName().toLowerCase().contains(queryLower)) {
                filteredList.add(password);
            }
        }
        return filteredList;
    }
}
