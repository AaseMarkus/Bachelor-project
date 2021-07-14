package oslomet.bachelorprosjekt.molendoansatt.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import oslomet.bachelorprosjekt.molendoansatt.ApplicationActivity;
import oslomet.bachelorprosjekt.molendoansatt.ConfirmationDialog;
import oslomet.bachelorprosjekt.molendoansatt.DailyPlanAdapter;
import oslomet.bachelorprosjekt.molendoansatt.MainActivity;
import oslomet.bachelorprosjekt.molendoansatt.NewTimeDialog;
import oslomet.bachelorprosjekt.molendoansatt.R;
import oslomet.bachelorprosjekt.molendoansatt.objects.DailyPlanTime;
import oslomet.bachelorprosjekt.molendoansatt.objects.Registration;
import oslomet.bachelorprosjekt.molendoansatt.objects.RegistrationHandler;

public class DailyPlanFragment extends Fragment implements NewTimeDialog.NewTimeDialogListener,
        DailyPlanAdapter.DailyPlanListener, ConfirmationDialog.DialogClickListener {
    LocalTime min_hour;
    LocalTime max_hour;

    RecyclerView rvDailyPlan;
    ArrayList<Registration> registeredShifts;
    ArrayList<DailyPlanTime> shifts = new ArrayList<>();
    LocalDate currentDate;
    Button btnSave, btnReset;
    String[] weekdays, months;
    RegistrationHandler registrationHandler;

    public DailyPlanFragment(ArrayList<Registration> registeredShifts, LocalDate currentDate) {
        this.registeredShifts = registeredShifts;
        this.currentDate = currentDate;

        registrationHandler = MainActivity.getRegistrationHandler();
        min_hour = registrationHandler.getMin_time();
        max_hour = registrationHandler.getMax_time();

        LocalTime pointer = min_hour;
        int extra_days = 0;
        while(pointer.getHour() != max_hour.getHour()) {
            if(pointer.getHour() == 0) extra_days++;
            LocalDateTime start = LocalDateTime.of(currentDate.getYear(), currentDate.getMonth(),
                    currentDate.getDayOfMonth(), pointer.getHour(), 0);
            start = start.plusDays(extra_days);
            shifts.add(new DailyPlanTime(start, start.plusHours(1), false));
            pointer = pointer.plusHours(1);
        }

        registeredShifts.forEach(this::addShift);

        Collections.sort(shifts);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_daily_plan, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weekdays = getResources().getStringArray(R.array.weekdays);
        months = getResources().getStringArray(R.array.months);

        String title = weekdays[currentDate.getDayOfWeek().getValue()-1] + " "
                + currentDate.getDayOfMonth() + ". " + months[currentDate.getMonthValue()-1];

        ((ApplicationActivity) getActivity()).setToolbarTitle(title);

        initializeVariables();
        updatePage();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        ((ApplicationActivity) getActivity()).setToolbarTitle("");
    }

    private void initializeVariables() {
        /* --- Buttons --- */
        btnSave = getActivity().findViewById(R.id.btn_daily_save);
        btnSave.setOnClickListener(v -> save());

        btnReset = getActivity().findViewById(R.id.btn_daily_reset);
        btnReset.setOnClickListener(v -> reset());

    }


    private void updatePage() {
        Collections.sort(shifts);

        /* --- Adapter --- */
        DailyPlanAdapter dailyPlanAdapter = new DailyPlanAdapter(getContext(), this, shifts);

        /* --- Recycler Views --- */
        rvDailyPlan = getActivity().findViewById(R.id.rv_daily_plan);
        rvDailyPlan.setAdapter(dailyPlanAdapter);
        rvDailyPlan.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    public void applyNewTime(LocalDateTime start_time, LocalDateTime end_time, LocalDateTime start_pointer_time, LocalDateTime end_pointer_time) {
        for(int i = 0; i < shifts.size(); i++) {
            //Remove old time
            if(shifts.get(i).getStart_time().isEqual(start_time)
                    && shifts.get(i).getEnd_time().isEqual(end_time)) {
                shifts.remove(i);
                break;
            }
        }

        if(end_pointer_time != null) {
            // Create interval before new interval
            if(start_pointer_time.isAfter(start_time))
                shifts.add(new DailyPlanTime(start_time,
                        start_pointer_time, false));

            // Create interval after new interval
            if(end_pointer_time.isBefore(end_time)) shifts.add(new DailyPlanTime(end_pointer_time,
                    end_time, false));

            DailyPlanTime newTime = new DailyPlanTime(start_pointer_time, end_pointer_time, true);
            if(!(start_time.isEqual(start_pointer_time) && end_time.isEqual(end_pointer_time))) {
                newTime.setCustom(true);
                newTime.setDelete_start_time(start_time);
                newTime.setDelete_end_time(end_time);
            }

            shifts.add(newTime);
        } else {
            if(!start_time.plusMinutes(1).isEqual(end_time))
                shifts.add(new DailyPlanTime(start_time.plusMinutes(1), end_time, false));

            DailyPlanTime newTime = new DailyPlanTime(start_time.plusSeconds(1), start_time.plusMinutes(1),
                    true, true);
            newTime.setCustom(true);
            newTime.setDelete_start_time(start_time);
            newTime.setDelete_end_time(end_time);
            shifts.add(newTime);
        }

        updatePage();
    }

    @Override
    public void changeSelected(int position, boolean status) {
        shifts.get(position).setSelected(status);
    }

    @Override
    public void delete(int position) {
        LocalDateTime start, end;
        start = shifts.get(position).getDelete_start_time();
        end = shifts.get(position).getDelete_end_time();

        if(position < shifts.size()-1) {
            if(end.isEqual(shifts.get(position+1).getEnd_time()))
                shifts.remove(position+1);
        }

        shifts.remove(position);

        if(position > 0) {
            if(start.isEqual(shifts.get(position-1).getStart_time()))
                shifts.remove(position-1);
        }

        shifts.add(new DailyPlanTime(start, end, false));

        updatePage();
    }

    private void addShift(Registration registration) {
        LocalDateTime registration_start = LocalDateTime.of(registration.getFldOpRStartDate(),
                registration.getFldOpRStartTime());
        LocalDateTime registration_end = null;

        LocalDateTime pointer = registration_start;

        // if open ended function
        if(registration.getFldOpREndTime() == null) {
            for(int i = 0; i < shifts.size(); i++) {
                if(addOpenendedExists(registration_start, i)) break;
                else if(addOpenendedNotExists(registration_start, i)) break;
            }
        } else {
            registration_end = LocalDateTime.of(registration.getFldOpREndDate(),
                    registration.getFldOpREndTime());

            for(int i = 0; i < shifts.size(); i++) {
                // Exit out of loop when registration times are passed
                if(shifts.get(i).getStart_time().isAfter(registration_end)) break;

                addIntervalIfStartNotExists(registration_start, i);
                addIntervalIfEndNotExists(registration_end, i);
            }
        }

        // Select all times in interval
        for(int i = 0; i < shifts.size(); i++) {
            if (pointer.isEqual(shifts.get(i).getStart_time())) {
                if(registration_end == null) {
                    shifts.get(i).setSelected(true);
                    return;
                }

                while(pointer.isBefore(registration_end)) {
                    shifts.get(i).setSelected(true);
                    pointer = shifts.get(i).getEnd_time();
                    i++;
                }
                break;
            }
        }
    }

    // Tries to add an openended shift to list if shift start already exists
    private boolean addOpenendedExists(LocalDateTime registration_start, int i) {
        if(registration_start.isEqual(shifts.get(i).getStart_time())) {
            DailyPlanTime newTime = new DailyPlanTime(shifts.get(i).getStart_time(),
                    shifts.get(i).getStart_time().plusMinutes(1), false, true);
            DailyPlanTime afterTime = new DailyPlanTime(shifts.get(i).getStart_time().plusMinutes(1),
                    shifts.get(i).getEnd_time(), false);

            newTime.setCustom(true);
            newTime.setDelete_start_time(shifts.get(i).getStart_time());
            newTime.setDelete_end_time(shifts.get(i).getEnd_time());

            shifts.remove(i);
            shifts.add(newTime);

            if (afterTime.getStart_time().getHour() != afterTime.getEnd_time().getHour() ||
                    afterTime.getStart_time().getMinute() != afterTime.getEnd_time().getMinute())
                shifts.add(afterTime);

            Collections.sort(shifts);
            return true;
        }
        return false;
    }

    // Tries to add an openended shift that does not have an already existing start time
    private boolean addOpenendedNotExists(LocalDateTime registration_start, int i) {
        if(registration_start.isAfter(shifts.get(i).getStart_time()) &&
                registration_start.isBefore(shifts.get(i).getEnd_time())) {

            DailyPlanTime beforeTime = new DailyPlanTime(shifts.get(i).getStart_time(),
                    registration_start, false);
            DailyPlanTime newTime = new DailyPlanTime(registration_start,
                    registration_start.plusMinutes(1), false, true);
            DailyPlanTime afterTime = new DailyPlanTime(registration_start.plusMinutes(1),
                    shifts.get(i).getEnd_time(), false);

            newTime.setCustom(true);
            newTime.setDelete_start_time(shifts.get(i).getStart_time());
            newTime.setDelete_end_time(shifts.get(i).getEnd_time());

            shifts.remove(i);

            if(beforeTime.getStart_time().getHour() != beforeTime.getEnd_time().getHour() ||
                    beforeTime.getStart_time().getMinute() != beforeTime.getEnd_time().getMinute())
                shifts.add(beforeTime);

            shifts.add(newTime);

            if(afterTime.getStart_time().getHour() != afterTime.getEnd_time().getHour() ||
                    afterTime.getStart_time().getMinute() != afterTime.getEnd_time().getMinute())
                shifts.add(afterTime);
            Collections.sort(shifts);
            return true;
        }
        return false;
    }

    // Creates new interval if start time does not exist as an interval
    private void addIntervalIfStartNotExists(LocalDateTime registration_start, int i) {
        // If start time of registration is between the start and end times of an interval,
        // create a new interval from the start time of the registration
        if(registration_start.isAfter(shifts.get(i).getStart_time()) &&
                registration_start.isBefore(shifts.get(i).getEnd_time())) {

            DailyPlanTime beforeTime = new DailyPlanTime(shifts.get(i).getStart_time(),
                    registration_start, false);
            DailyPlanTime newTime = new DailyPlanTime(registration_start,
                    shifts.get(i).getEnd_time(), false);
            newTime.setCustom(true);
            newTime.setDelete_start_time(shifts.get(i).getStart_time());
            newTime.setDelete_end_time(shifts.get(i).getEnd_time());

            shifts.remove(i);
            if(beforeTime.getStart_time().getHour() != beforeTime.getEnd_time().getHour() ||
                    beforeTime.getStart_time().getMinute() != beforeTime.getEnd_time().getMinute())
                shifts.add(beforeTime);
            shifts.add(newTime);
            Collections.sort(shifts);
        }
    }

    // Creates new interval if end time does not exist as an interval
    private void addIntervalIfEndNotExists(LocalDateTime registration_end, int i) {
        // If end time of registration is between the start and end time of an interval,
        // create a new interval from the end time of the registration
        if(registration_end.isAfter(shifts.get(i).getStart_time()) &&
                registration_end.isBefore(shifts.get(i).getEnd_time())) {
            DailyPlanTime newTime = new DailyPlanTime(shifts.get(i).getStart_time(),
                    registration_end, false);
            DailyPlanTime afterTime = new DailyPlanTime(registration_end,
                    shifts.get(i).getEnd_time(), false);

            newTime.setCustom(true);
            newTime.setDelete_start_time(shifts.get(i).getStart_time());
            newTime.setDelete_end_time(shifts.get(i).getEnd_time());

            shifts.remove(i);
            shifts.add(newTime);

            if(afterTime.getStart_time().getHour() != afterTime.getEnd_time().getHour() ||
                    afterTime.getStart_time().getMinute() != afterTime.getEnd_time().getMinute())
                shifts.add(afterTime);
            Collections.sort(shifts);
        }
    }

    private void save() {
        ArrayList<ArrayList<LocalDateTime>> newIntervals = new ArrayList<>();
        for(int i = 0; i < shifts.size(); i++) {
            if(shifts.get(i).isSelected() && !shifts.get(i).isOpenended()) {
                LocalDateTime start_time = shifts.get(i).getStart_time();
                while(shifts.get(i).isSelected() && !shifts.get(i).isOpenended()) {
                    if(i==shifts.size()-1) {
                        i++;
                        break;
                    }
                    i++;
                }
                LocalDateTime end_time = shifts.get(i-1).getEnd_time();
                ArrayList<LocalDateTime> interval = new ArrayList<>();
                interval.add(start_time);
                interval.add(end_time);
                newIntervals.add(interval);
            }

            if(shifts.get(i).isOpenended()) {
                LocalDateTime start_time = shifts.get(i).getStart_time();
                ArrayList<LocalDateTime> interval = new ArrayList<>();
                interval.add(start_time);
                interval.add(null);
                newIntervals.add(interval);
            }
        }

        MainActivity.getRegistrationHandler().addNewDailyPlans(newIntervals, currentDate);

        ((ApplicationActivity) getContext()).getSupportFragmentManager().
                popBackStack("weeklyPlan", 0);
    }

    private void reset() {
        ConfirmationDialog dialog = new ConfirmationDialog(getString(R.string.reset),
                getString(R.string.reset_dialog_text_day), 0);
        dialog.setTargetFragment(this, 300);
        dialog.show(((ApplicationActivity) Objects.requireNonNull(getContext())).getSupportFragmentManager(),
                "New confirmation dialog");
    }

    @Override
    public void onOkClick(int result) {
        shifts.forEach(s -> s.setSelected(false));
        updatePage();
    }
}