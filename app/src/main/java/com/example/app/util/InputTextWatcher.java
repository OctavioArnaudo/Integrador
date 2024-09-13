package com.example.app.util;

import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputLayout;

/**
 * This class implements the TextWatcher interface to validate text fields in real time.
 * It shows an error message if the field is empty and clears the error message if the field is not empty.
 */
public class InputTextWatcher implements TextWatcher {

    private final TextInputLayout textInputLayout;

    /**
     * Constructs an InputTextWatcher instance.
     *
     * @param textInputLayout The TextInputLayout associated with this TextWatcher.
     */
    public InputTextWatcher(TextInputLayout textInputLayout) {
        this.textInputLayout = textInputLayout;
    }

    /**
     * Called when the text in the EditText changes.
     *
     * @param s      The current character sequence in the EditText.
     * @param start  The start index of the text that was changed.
     * @param before The length of the text that was replaced.
     * @param count  The length of the new text.
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // Check if the input is empty and show or clear the error message accordingly
        String input = s.toString().trim();
        if (input.isEmpty()) {
            textInputLayout.setError("Please enter a valid option.");
        } else {
            textInputLayout.setError(null); // Clear the error message if the field is not empty
        }
    }

    /**
     * Called to notify that some text in the EditText is about to be replaced.
     *
     * @param s      The current character sequence in the EditText.
     * @param start  The start index of the text that will be replaced.
     * @param count  The length of the text that will be replaced.
     * @param after  The length of the new text that will replace the old text.
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Not used in this implementation
    }

    /**
     * Called to notify that some text in the EditText has been replaced.
     *
     * @param s The current character sequence in the EditText after the changes.
     */
    @Override
    public void afterTextChanged(Editable s) {
        // Not used in this implementation
    }
}
