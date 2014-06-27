package com.furryfishapps.transitwear.app;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ErrorDialogFragment extends DialogFragment {
    private DialogInterface.OnCancelListener onCancelListener;
    private Dialog dialog;

    public ErrorDialogFragment() {
        super();
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return dialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        onCancelListener.onCancel(dialog);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        onCancelListener.onCancel(dialog);
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }
}