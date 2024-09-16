package com.example.app.util;

import android.text.TextUtils;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import com.example.app.data.db.DbManager;

import java.util.Objects;

public class InputValidator {

    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@!#$%^&*()_+{}\\[\\]:;<>,.?~/-]).{8,40}$";
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String NAME_REGEX = "^[a-zA-Z\\s'-]{1,20}$";
    private static final String URL_REGEX = "^(https?|ftp)://?(www\\.)?[a-zA-Z0-9-]+(\\.[a-zA-Z]{2,})(/\\S*)?$";
    static EditText editTextPassword;

	public static boolean validateName(EditText editText, TextInputLayout textInputLayout) {
		String name = getTrimmedText(editText);
        if (TextUtils.isEmpty(name)) {
            textInputLayout.setError("Please enter a name");
            return false;
        } else if (!name.matches(NAME_REGEX)) {
            textInputLayout.setError("Please enter an alphanumeric name");
            return false;
        } else if (name.length() >= 40) {
            textInputLayout.setError("The name must be fewer than 40 characters.");
            return false;
        } else if (DbManager.passwordExistsByName(name)) {
            textInputLayout.setError("A password with this name already exists.");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    public static boolean validateUsername(EditText editText, TextInputLayout textInputLayout) {
		String username = getTrimmedText(editText);
        if (TextUtils.isEmpty(username)) {
            textInputLayout.setError("Email is required.");
            return false;
        } else if (!username.matches(EMAIL_REGEX)) {
            textInputLayout.setError("Please enter a valid email address.");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    public static boolean validatePassword(EditText editText, TextInputLayout textInputLayout) {
		String password = getTrimmedText(editText);
        if (TextUtils.isEmpty(password)) {
            textInputLayout.setError("Please enter a password.");
            return false;
        } else if (!password.matches(PASSWORD_REGEX)) {
            textInputLayout.setError("Password must be 12-40 characters long with a mix of uppercase, lowercase, digits, and special characters.");
            return false;
        } else {
            textInputLayout.setError(null);
            editTextPassword = editText;
            return true;
        }
    }

    public static boolean validateConfirmPassword(EditText editText, TextInputLayout textInputLayout) {
		String confirmPassword = getTrimmedText(editText);
        if (editTextPassword != null) {
            String password = getTrimmedText(editTextPassword);
            if (TextUtils.isEmpty(confirmPassword)) {
                textInputLayout.setError("Please confirm your password.");
                return false;
            } else if (!password.equals(confirmPassword)) {
                textInputLayout.setError("Passwords do not match.");
                return false;
            } else {
                textInputLayout.setError(null);
                return true;
            }
        }
        return true;
    }

    public static boolean validateUrl(EditText editText, TextInputLayout textInputLayout) {
		String url = getTrimmedText(editText);
        if (!TextUtils.isEmpty(url) && !url.matches(URL_REGEX)) {
            textInputLayout.setError("URL format is incorrect. Ensure it includes a valid domain name.");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    public static boolean validateDescription(EditText editText, TextInputLayout textInputLayout) {
		String description = getTrimmedText(editText);
        if (description.length() > 40) {
            textInputLayout.setError("Description must be fewer than 40 characters.");
            return false;
        } else {
            textInputLayout.setError(null);
            return true;
        }
    }

    public static String getTrimmedText(EditText editText) {
        return Objects.requireNonNull(editText.getText()).toString().trim();
    }

}
