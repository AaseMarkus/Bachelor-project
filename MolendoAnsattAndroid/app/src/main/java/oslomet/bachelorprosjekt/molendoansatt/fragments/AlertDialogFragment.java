package oslomet.bachelorprosjekt.molendoansatt.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

public class AlertDialogFragment extends DialogFragment {
    String title, alertText;

    public AlertDialogFragment(String title, String alertText) {
        this.title = title;
        this.alertText = alertText;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(title)
                .setMessage(alertText)
                .setNeutralButton("OK", (dialog, which) -> {});

        return builder.create();
    }
}
