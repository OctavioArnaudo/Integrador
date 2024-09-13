package com.example.app.util;

import android.content.Context;
import android.util.Log;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Utility class for displaying SweetAlertDialogs.
 */
public class ShowAlertsUtility {

    private static final String TAG = "ShowAlertsUtility";

    /**
     * Shows a SweetAlertDialog with a single confirmation button.
     *
     * @param context  The context in which the dialog should be shown.
     * @param type     The type of SweetAlertDialog (e.g., SweetAlertDialog.ERROR_TYPE).
     * @param title    The title of the dialog.
     * @param message  The message to be displayed in the dialog.
     * @param listener Optional listener for the confirmation button click.
     */
    public static void showAlert(Context context, int type, String title, String message, SweetAlertDialog.OnSweetClickListener listener) {
        if (context == null) {
            Log.e(TAG, "Context is null in showAlert");
            return;
        }

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, type);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.setConfirmText("Accept");

        sweetAlertDialog.setConfirmClickListener(dialog -> {
            dialog.dismissWithAnimation();
            if (listener != null) {
                listener.onClick(dialog);
            }
        });

        sweetAlertDialog.show();
    }

    /**
     * Shows a SweetAlertDialog with both confirm and cancel buttons.
     *
     * @param context         The context in which the dialog should be shown.
     * @param type            The type of SweetAlertDialog (e.g., SweetAlertDialog.WARNING_TYPE).
     * @param title           The title of the dialog.
     * @param message         The message to be displayed in the dialog.
     * @param confirmListener Listener for the confirm button click.
     * @param cancelListener  Listener for the cancel button click.
     * @return The SweetAlertDialog instance.
     */
    public static SweetAlertDialog showAlertWithOptions(Context context, int type, String title, String message,
                                                        SweetAlertDialog.OnSweetClickListener confirmListener,
                                                        SweetAlertDialog.OnSweetClickListener cancelListener) {
        if (context == null) {
            Log.e(TAG, "Context is null in showAlertWithOptions");
            return null;
        }

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(context, type);
        sweetAlertDialog.setTitleText(title);
        sweetAlertDialog.setContentText(message);
        sweetAlertDialog.setConfirmText("Accept");
        sweetAlertDialog.setCancelText("Cancel");

        sweetAlertDialog.setConfirmClickListener(dialog -> {
            dialog.dismissWithAnimation();
            if (confirmListener != null) {
                confirmListener.onClick(dialog);
            }
        });

        sweetAlertDialog.setCancelClickListener(dialog -> {
            dialog.dismissWithAnimation();
            if (cancelListener != null) {
                cancelListener.onClick(dialog);
            }
        });

        sweetAlertDialog.show();
        return sweetAlertDialog;
    }
}
