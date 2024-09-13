package com.example.app.util;

import android.content.Context;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.fragment.app.FragmentActivity;

public class BiometricUtils {

    /**
     * Checks if biometric authentication is available and supported on the device.
     * 
     * @param context the application context
     * @return true if biometric authentication is supported, false otherwise
     */
    public static boolean isBiometricPromptEnabled(Context context) {
        BiometricManager biometricManager = BiometricManager.from(context);
        int authenticationResult = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG);
        return authenticationResult == BiometricManager.BIOMETRIC_SUCCESS;
    }

    /**
     * Displays the biometric authentication prompt.
     * 
     * @param activity the activity from which the prompt is shown
     * @param callback the callback to handle authentication results
     */
    public static void showBiometricPrompt(FragmentActivity activity, BiometricPrompt.AuthenticationCallback callback) {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric Authentication")
                .setSubtitle("Use your fingerprint to sign in")
                .setNegativeButtonText("Cancel")
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(activity, activity.getMainExecutor(), callback);
        biometricPrompt.authenticate(promptInfo);
    }
}
