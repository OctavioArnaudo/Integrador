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

import com.example.app.R;
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
	private static final String TAG = "ShowPasswordsActivity";
    private ImageView menuIcon;
    private ImageButton eyeIcon;
    private ImageButton editIcon;
    private ImageButton deleteIcon;
    private DbManager dbManager;
    private ScrollView scrollView;
    private List<PasswordResponse> passwords;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_passwords);

        // Initialize DbManager and UI elements
        dbManager = new DbManager(this);
        TextInputLayout textInputLayoutName = findViewById(R.id.layout_password_name);
        TextInputEditText editTextName = findViewById(R.id.input_password_name);

        // Set up the TextWatcher to filter passwords
        editTextName.addTextChangedListener(new InputTextWatcher(textInputLayoutName) {
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
            Log.i(TAG, "Displaying passwords for user ID: " + userId);
            passwords = dbManager.getPasswordsListForUserId(userId);
            Log.i(TAG, "Password list size: " + passwords.size());
            displayPasswords(passwords);
        } else {
            Log.e(TAG, "User ID not found in SharedPreferences");
        }

        // Set up the add button
        FloatingActionButton addButton = findViewById(R.id.btn_add);
        addButton.setOnClickListener(view -> {
            Intent intent = new Intent(ShowPasswordsActivity.this, RegisterPasswordsActivity.class);
            startActivity(intent);
        });

        // Set up the menu icon
        menuIcon = findViewById(R.id.img_menu);
        menuIcon.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(ShowPasswordsActivity.this, menuIcon);
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.about) {
                    Intent aboutIntent = new Intent(ShowPasswordsActivity.this, AboutActivity.class);
                    startActivity(aboutIntent);
                    return true;
                } else if (itemId == R.id.logout) {
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
        scrollView = findViewById(R.id.scroll_view);
        ImageView scrollIndicator = findViewById(R.id.img_scroll_indicator);
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
            TableLayout passwordTable = findViewById(R.id.table_layout);
            TextView noPasswordsText = findViewById(R.id.text_password_no);
            ImageView noPasswordsImage = findViewById(R.id.img_password_no);
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
                    TableRow row = (TableRow) inflater.inflate(R.layout.row_table,null);

                    eyeIcon = row.findViewById(R.id.btn_password_view);
                    editIcon = row.findViewById(R.id.btn_password_edit);
                    deleteIcon = row.findViewById(R.id.btn_password_delete);

                    TextView nameTextView = row.findViewById(R.id.text_password_name);
                    String name = password.getName();
                    if (name.length() > 10) {
                        nameTextView.setText(name.substring(0, 10));
                    } else {
                        nameTextView.setText(name);
                    }
                    setupActionIcons(password.getId());

                    passwordTable.addView(row);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error: " + e.getMessage());
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
            Intent viewIntent = new Intent(ShowPasswordsActivity.this, ViewPasswordsActivity.class);
            viewIntent.putExtra("passwordId", passwordId);
            viewIntent.putExtra("userId", userId);
            startActivity(viewIntent);
        });

        editIcon.setOnClickListener(v -> {
            Intent editIntent = new Intent(ShowPasswordsActivity.this, EditPasswordsActivity.class);
            editIntent.putExtra("passwordId", passwordId);
            editIntent.putExtra("userId", userId);
            startActivity(editIntent);
        });

        deleteIcon.setOnClickListener(v -> {
            int idToDelete = (int) v.getTag();
            ShowAlertsUtility.showAlertWithOptions(this, 3,
                    "Are you sure you want to delete this password?",
                    "This action cannot be undone.",
                    sweetAlertDialog -> {
                        try {
                            dbManager.deletePassword(idToDelete);
                            passwords = dbManager.getPasswordsListForUserId(userId);
                            displayPasswords(passwords);
                            ShowAlertsUtility.showAlert(this, 2, "Success", "Password deleted successfully.", SweetAlertDialog::dismissWithAnimation);
                        } catch (Exception e) {
                            Log.e(TAG, "Error deleting password: " + e.getMessage());
                            ShowAlertsUtility.showAlert(this, 1, "Error", "An error occurred while deleting the password.", SweetAlertDialog::dismissWithAnimation);
                        }
                    },
                    sweetAlertDialog -> ShowAlertsUtility.showAlert(this, 1, "Cancelled", "Operation cancelled.", SweetAlertDialog::dismissWithAnimation));
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
