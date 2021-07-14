package oslomet.bachelorprosjekt.molendoansatt.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import oslomet.bachelorprosjekt.molendoansatt.ApplicationActivity;
import oslomet.bachelorprosjekt.molendoansatt.ConfirmationDialog;
import oslomet.bachelorprosjekt.molendoansatt.MainActivity;
import oslomet.bachelorprosjekt.molendoansatt.R;
import oslomet.bachelorprosjekt.molendoansatt.objects.Registration;
import oslomet.bachelorprosjekt.molendoansatt.objects.RegistrationHandler;
import oslomet.bachelorprosjekt.molendoansatt.WeeklyPlanAdapter;
import oslomet.bachelorprosjekt.molendoansatt.requests.DeleteRequestTask;
import oslomet.bachelorprosjekt.molendoansatt.requests.PostRequestTask;
import oslomet.bachelorprosjekt.molendoansatt.requests.RequestManager;

public class WeeklyPlanFragment extends Fragment implements ConfirmationDialog.DialogClickListener,
        WeeklyPlanAdapter.WeeklyPlanListener, PostRequestTask.PostRequestListener,
        DeleteRequestTask.DeleteRequestListener {
    String[] weekdays, months;
    RecyclerView rvWeeklyPlan;
    TextView tvDate;
    LocalDateTime currentDate;
    LocalDate currentMinDate;
    ImageView ivLeftArrow, ivRightArrow;
    RegistrationHandler registrationHandler;
    Button btnSave, btnReset;
    int pointer;

    public WeeklyPlanFragment() {
        registrationHandler = MainActivity.getRegistrationHandler();
        this.currentDate = registrationHandler.getCurrent_date();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.currentDate = registrationHandler.getCurrent_date();
        this.currentMinDate = registrationHandler.getMin_date();

        initializeVariables();

        updatePage();
    }

    private void initializeVariables() {
        pointer = 0;

        /* --- String arrays --- */
        weekdays = getResources().getStringArray(R.array.weekdays);
        months = getResources().getStringArray(R.array.months);

        /* --- TextView --- */
        tvDate = getActivity().findViewById(R.id.tv_date);

        /* --- ImageView --- */
        ivLeftArrow = getActivity().findViewById(R.id.iv_left_arrow);
        ivRightArrow = getActivity().findViewById(R.id.iv_right_arrow);

        ivLeftArrow.setOnClickListener(v -> moveLeft());
        ivRightArrow.setOnClickListener(v -> moveRight());

        /* --- Buttons --- */
        btnSave = getActivity().findViewById(R.id.btn_weekly_save);
        btnSave.setOnClickListener(v -> saveRegistrations());

        btnReset = getActivity().findViewById(R.id.btn_weekly_reset);
        btnReset.setOnClickListener(v -> resetRegistrations());
    }

    private void moveLeft() {
        pointer--;
        currentMinDate = currentMinDate.minusDays(7);
        updatePage();
    }

    private void moveRight() {
        pointer++;
        currentMinDate = currentMinDate.plusDays(7);
        updatePage();
    }

    private void updatePage() {
        if(pointer == 0) ivLeftArrow.setVisibility(View.INVISIBLE);
        else ivLeftArrow.setVisibility(View.VISIBLE);

        if(pointer == 3) ivRightArrow.setVisibility(View.INVISIBLE);
        else ivRightArrow.setVisibility(View.VISIBLE);

        /* --- Adapter --- */
        WeeklyPlanAdapter weeklyPlanAdapter = new WeeklyPlanAdapter(getContext(), this,
                registrationHandler.getWeeklyRegistrationArray(currentMinDate), weekdays, currentMinDate);

        /* --- Recycler Views --- */
        rvWeeklyPlan = getActivity().findViewById(R.id.rv_weekly_plan);
        rvWeeklyPlan.setAdapter(weeklyPlanAdapter);
        rvWeeklyPlan.setLayoutManager(new LinearLayoutManager(getActivity()));

        /* --- Date ---*/
        LocalDate sunday = currentMinDate.plusDays(6);
        String date = currentMinDate.getDayOfMonth() + ". " + months[currentMinDate.getMonthValue()-1] +
                " - " + sunday.getDayOfMonth() + ". " + months[sunday.getMonthValue()-1];
        tvDate.setText(date);
    }

    private void saveRegistrations() {
        ConfirmationDialog dialog = new ConfirmationDialog(getString(R.string.save),
                getString(R.string.save_dialog_text), 100);
        dialog.setTargetFragment(this, 300);
        dialog.show(((ApplicationActivity) getContext()).getSupportFragmentManager(),
                "New confirmation dialog");
    }

    @Override
    public void onOkClick(int result) {
        switch (result) {
            case 100:
                JSONArray jsonArray = new JSONArray();
                ArrayList<Registration> registrations = registrationHandler
                        .getFutureRegistrations();
                Log.d("registrationsavelist", registrations.toString());

                registrations.forEach(r -> jsonArray.put(r.getJSONObject()));

                if(registrations.size() == 0) {
                    String[] request = new String[]{"0"};
                    DeleteRequestTask task = new DeleteRequestTask(this, getContext(), request);
                    RequestManager.getManagerInstance().runTask(task);
                } else {
                    String[] request = new String[]{"0", jsonArray.toString()};
                    PostRequestTask task = new PostRequestTask(this, getContext(), request);
                    RequestManager.getManagerInstance().runTask(task);
                }
                break;
            case 200:
                break;
            case 300:
                registrationHandler.resetRegistrations();
                updatePage();
                break;
        }
    }

    @Override
    public void delete(int position) {
        ArrayList<Registration> registrations = registrationHandler.
                getRegistrations(currentMinDate.plusDays(position));
        registrations.forEach(r -> registrationHandler.delete(r));
        updatePage();
    }

    public void resetRegistrations() {
        ConfirmationDialog dialog = new ConfirmationDialog(getString(R.string.reset),
                getString(R.string.reset_dialog_text_week), 300);
        dialog.setTargetFragment(this, 300);
        dialog.show(((ApplicationActivity) getContext()).getSupportFragmentManager(),
                "New confirmation dialog");
    }

    public void onSaveResult(int result_code) {
        AlertDialogFragment dialog;
        if (result_code == 200 | result_code == 201) {
            dialog = new AlertDialogFragment(getString(R.string.successful),
                    getString(R.string.successful_text));
            dialog.show(getActivity().getSupportFragmentManager(), "saveResult");
        } else {
            dialog = new AlertDialogFragment(getString(R.string.save_unsuccessful),
                    getString(R.string.status_code) + " " + result_code);
            dialog.show(getActivity().getSupportFragmentManager(), "SaveResult");
        }
    }
}