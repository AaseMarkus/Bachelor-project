package oslomet.bachelorprosjekt.molendoansatt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ConfirmationDialog extends DialogFragment {
    String title, message;
    int result;

    private DialogClickListener callback;

    public interface DialogClickListener {
        void onOkClick(int result);
    }

    public ConfirmationDialog(String title, String message, int result) {
        this.title = title;
        this.message = message;
        this.result = result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(title);
        if (message != null) builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            //callback = (DialogClickListener) getTargetFragment();
            callback.onOkClick(result);
        })
                .setNegativeButton(R.string.no, (dialog, which) -> dismiss());
        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        callback = (DialogClickListener) getTargetFragment();
        if (callback == null) callback = (DialogClickListener) context;
    }
}
