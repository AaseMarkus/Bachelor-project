package oslomet.bachelorprosjekt.molendoansatt.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import oslomet.bachelorprosjekt.molendoansatt.ApplicationActivity;
import oslomet.bachelorprosjekt.molendoansatt.MainActivity;
import oslomet.bachelorprosjekt.molendoansatt.R;
import oslomet.bachelorprosjekt.molendoansatt.objects.Registration;
import oslomet.bachelorprosjekt.molendoansatt.objects.RegistrationHandler;
import oslomet.bachelorprosjekt.molendoansatt.requests.DeleteRequestTask;
import oslomet.bachelorprosjekt.molendoansatt.requests.GetRequestTask;
import oslomet.bachelorprosjekt.molendoansatt.requests.PostRequestTask;
import oslomet.bachelorprosjekt.molendoansatt.requests.RequestManager;

public class HomeFragment extends Fragment implements PostRequestTask.PostRequestListener,
        GetRequestTask.GetRequestListener, DeleteRequestTask.DeleteRequestListener {
    //Button sendSMS;
    //EditText etPhoneInput;
    RegistrationHandler registrationHandler;
    ArrayList<Registration> rosters;
    Registration currentOngoing = null;
    TextView tvHomeText;
    Button btnStartStop;
    RelativeLayout rlLoading;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeVariables();

        loadRosters();
    }

    public void loadRosters() {
        btnStartStop.setEnabled(true);

        rosters = registrationHandler.getRegistrations();
        currentOngoing = null;

        rosters.forEach(roster -> {
            if(roster.isFldOpRLogOn() && !roster.isFldOpRLogOut()) currentOngoing = roster;
        });

        updatePage(currentOngoing);
    }

    private void updatePage(Registration currentOngoing) {
        if(currentOngoing == null) {
            tvHomeText.setText(getString(R.string.not_on_shift));
            btnStartStop.setText(getString(R.string.start_shift));
            btnStartStop.setOnClickListener(v -> startShift());
        } else {
            if(currentOngoing.getFldOpREndTime() == null)
                tvHomeText.setText(getString(R.string.on_shift_undecided_end));
            else {
                String endHour = currentOngoing.getFldOpREndTime().getHour() > 9 ?
                        Integer.toString(currentOngoing.getFldOpREndTime().getHour())
                        : "0" + currentOngoing.getFldOpREndTime().getHour();

                String endMinute = currentOngoing.getFldOpREndTime().getMinute() > 9 ?
                        Integer.toString(currentOngoing.getFldOpREndTime().getMinute())
                        : "0" + currentOngoing.getFldOpREndTime().getMinute();

                String text = getString(R.string.on_shift_set_end) + " " + endHour + ":" + endMinute;
                tvHomeText.setText(text);
            }
            btnStartStop.setText(getString(R.string.end_shift));
            btnStartStop.setOnClickListener(v -> endShift());
        }

        btnStartStop.setVisibility(View.VISIBLE);
        rlLoading.setVisibility(View.INVISIBLE);
    }

    private void startShift() {
        btnStartStop.setVisibility(View.INVISIBLE);
        rlLoading.setVisibility(View.VISIBLE);

        btnStartStop.setEnabled(false);

        Registration registration = new Registration(RequestManager.userID,
                registrationHandler.getCurrent_date().toLocalDate(),
                registrationHandler.getCurrent_date().toLocalDate(),
                registrationHandler.getCurrent_date().toLocalTime(), null, false,
                false, false);

        registrationHandler.add(registration);
        saveRegistrations();
    }

    private void endShift() {
        btnStartStop.setVisibility(View.INVISIBLE);
        rlLoading.setVisibility(View.VISIBLE);

        btnStartStop.setEnabled(false);

        registrationHandler.delete(currentOngoing);
        saveRegistrations();
        updateRosterList();
    }

    private void saveRegistrations() {
        JSONArray jsonArray = new JSONArray();
        ArrayList<Registration> registrations = registrationHandler
                .getFutureRegistrations();
        registrations.forEach(r -> jsonArray.put(r.getJSONObject()));

        if(registrations.size() != 0) {
            String[] request = new String[]{"0", jsonArray.toString()};
            PostRequestTask task = new PostRequestTask(this, getContext(), request);
            RequestManager.getManagerInstance().runTask(task);
        } else {
            String[] request = new String[]{"0"};
            DeleteRequestTask task = new DeleteRequestTask(this, getContext(), request);
            RequestManager.getManagerInstance().runTask(task);
        }

        updateRosterList();
    }

    private void updateRosterList() {
        GetRequestTask task = new GetRequestTask(this, getContext(), "0");
        RequestManager.getManagerInstance().runTask(task);
    }

    private void initializeVariables() {
        registrationHandler = MainActivity.getRegistrationHandler();

        tvHomeText = getActivity().findViewById(R.id.tv_start_stop);
        btnStartStop = getActivity().findViewById(R.id.btn_start_stop);

        rlLoading = getActivity().findViewById(R.id.homeLoadingPanel);
    }

    @Override
    public void onSaveResult(int resultCode) {
        updateRosterList();
    }

    @Override
    public void onGetRostersResult(boolean result, ArrayList<Registration> registrations) {
        if(result) {
            registrationHandler.resetRegistrations();
            registrations.forEach(r -> registrationHandler.add(r));

            loadRosters();
        }
    }
}