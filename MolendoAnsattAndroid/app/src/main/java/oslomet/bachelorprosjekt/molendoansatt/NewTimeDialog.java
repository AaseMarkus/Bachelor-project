package oslomet.bachelorprosjekt.molendoansatt;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import oslomet.bachelorprosjekt.molendoansatt.objects.DailyPlanTime;
import oslomet.bachelorprosjekt.molendoansatt.objects.Registration;

public class NewTimeDialog extends AppCompatDialogFragment {
    private LocalDateTime start_time, end_time, start_pointer_time, end_pointer_time;
    private TextView tvStartHour, tvEndHour;
    private EditText etStartMinute, etEndMinute;
    private ImageView ivStartUp, ivStartDown, ivEndUp, ivEndDown;
    private TextWatcher startWatcher, endWatcher;
    private NewTimeDialogListener listener;

    public NewTimeDialog(LocalDateTime start_time, LocalDateTime end_time) {
        this.start_time = start_pointer_time = start_time;
        this.end_time = end_pointer_time = end_time;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_new_time, null);

        builder.setView(view)
                .setTitle(getString(R.string.edit_times))
                .setNegativeButton(getString(R.string.cancel), (dialog, which) -> { })
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    listener = (NewTimeDialogListener) getTargetFragment();
                    listener.applyNewTime(start_time, end_time, start_pointer_time, end_pointer_time);
                });

        initializeVariables(view);

        updateTimes();

        return builder.create();
    }

    public interface NewTimeDialogListener{
        void applyNewTime(LocalDateTime start_time, LocalDateTime end_time, LocalDateTime start_pointer_time,
                          LocalDateTime end_pointer_time);
    }

    private void initializeVariables(View view) {
        tvStartHour = view.findViewById(R.id.tv_new_time_start_hour);
        tvEndHour = view.findViewById(R.id.tv_new_time_end_hour);
        etStartMinute = view.findViewById(R.id.et_new_time_start_minute);
        etEndMinute = view.findViewById(R.id.et_new_time_end_minute);

        ivStartUp = view.findViewById(R.id.iv_start_up);
        ivEndUp = view.findViewById(R.id.iv_end_up);
        ivStartDown = view.findViewById(R.id.iv_start_down);
        ivEndDown = view.findViewById(R.id.iv_end_down);

        ivStartUp.setOnClickListener(v -> {
            if(start_pointer_time.plusMinutes(1).isBefore(end_pointer_time)) {
                start_pointer_time = start_pointer_time.plusMinutes(1);
                updateTimes();
            }
        });

        ivStartDown.setOnClickListener(v -> {
            if(!start_pointer_time.minusMinutes(1).isBefore(start_time)) {
                start_pointer_time = start_pointer_time.minusMinutes(1);
                updateTimes();
            }
        });

        ivEndDown.setOnClickListener(v -> {
            if(end_pointer_time.minusMinutes(1).isAfter(start_pointer_time)) {
                end_pointer_time = end_pointer_time.minusMinutes(1);
                updateTimes();
            }
        });

        ivEndUp.setOnClickListener(v -> {
            if(!end_pointer_time.plusMinutes(1).isAfter(end_time)) {
                end_pointer_time = end_pointer_time.plusMinutes(1);
                updateTimes();
            }
        });
    }

    private void updateTimes() {
        String startHour = (start_pointer_time.getHour() > 9
                ? String.valueOf(start_pointer_time.getHour()) : "0" + start_pointer_time.getHour()) + ":";
        String startMinute = (start_pointer_time.getMinute() > 9
                ? String.valueOf(start_pointer_time.getMinute()) : "0" + start_pointer_time.getMinute());

        String endHour = (end_pointer_time.getHour() > 9
                ? String.valueOf(end_pointer_time.getHour()) : "0" + end_pointer_time.getHour()) + ":";
        String endMinute = (end_pointer_time.getMinute() > 9
                ? String.valueOf(end_pointer_time.getMinute()) : "0" + end_pointer_time.getMinute());

        tvStartHour.setText(startHour);
        etStartMinute.setText(startMinute);
        tvEndHour.setText(endHour);
        etEndMinute.setText(endMinute);

        if(!start_pointer_time.isAfter(start_time)) ivStartDown.setVisibility(View.INVISIBLE);
        else ivStartDown.setVisibility(View.VISIBLE);

        if(!end_pointer_time.isBefore(end_time)) ivEndUp.setVisibility(View.INVISIBLE);
        else ivEndUp.setVisibility(View.VISIBLE);

        if(!start_pointer_time.plusMinutes(1).isBefore(end_pointer_time)) {
            ivStartUp.setVisibility(View.INVISIBLE);
            ivEndDown.setVisibility(View.INVISIBLE);
        } else {
            ivStartUp.setVisibility(View.VISIBLE);
            ivEndDown.setVisibility(View.VISIBLE);
        }
    }
}
