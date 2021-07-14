package oslomet.bachelorprosjekt.molendoansatt.objects;

import android.util.Log;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import oslomet.bachelorprosjekt.molendoansatt.requests.RequestManager;

public class RegistrationHandler {
    // Interval for when a daily plan should start and stop
    private LocalTime min_time = LocalTime.of(9,0,0);
    private LocalTime max_time = LocalTime.of(1,0,0);

    private ArrayList<Registration> registrations;
    private LocalDate min_date;
    private LocalDateTime current_date;
    private LocalTime current_time;

    public RegistrationHandler() { registrations = new ArrayList<>(); }

    public void setMin_date(LocalDate min_date) {
        this.min_date = min_date;
    }

    public LocalDate getMin_date() {
        return min_date;
    }

    public LocalTime getCurrent_time() {
        return current_time;
    }

    public void setCurrent_time(LocalTime current_time) {
        this.current_time = current_time;
    }

    public LocalTime getMin_time() {
        return min_time;
    }

    public void setMin_time(LocalTime min_time) {
        this.min_time = min_time;
    }

    public LocalTime getMax_time() {
        return max_time;
    }

    public void setMax_time(LocalTime max_time) {
        this.max_time = max_time;
    }

    public LocalDateTime getCurrent_date() {
        return current_date;
    }

    // Sets the current date to the same date as the server uses, sets monday to monday of the same week
    public void setCurrent_date(LocalDateTime current_date) {
        this.current_date = current_date;
        this.min_date = current_date.toLocalDate();

        if(!min_date.getDayOfWeek().equals(DayOfWeek.MONDAY))
            min_date = current_date.with(TemporalAdjusters.previous(DayOfWeek.MONDAY)).toLocalDate();
    }

    public int getSize() {
        return registrations.size();
    }

    public ArrayList<Registration> getRegistrations() {
        return registrations;
    }

    // Return registration with a set start time
    public ArrayList<Registration> getRegistrations(LocalDate date) {
        ArrayList<Registration> validRegistrations = new ArrayList<>();

        if(date == null) return validRegistrations;

        LocalDateTime start_time = LocalDateTime.of(date, min_time);

        registrations.forEach(registration -> {
            if(registration.getFldOpRStartDate() != null && registration.getFldOpRStartTime() != null) {
                if (isSameDay(start_time, LocalDateTime.of(registration.getFldOpRStartDate(),
                        registration.getFldOpRStartTime()))) validRegistrations.add(registration);
            }

        });

        return validRegistrations;
    }

    public ArrayList<Registration> getRegistrationsAfterTime(LocalDateTime datetime) {
        ArrayList<Registration> validRegistrations = new ArrayList<>();
        if(datetime == null) return validRegistrations;

        registrations.forEach(registration -> {
            if(LocalDateTime.of(registration.getFldOpRStartDate(),
                    registration.getFldOpRStartTime()).isAfter(datetime))
                validRegistrations.add(registration);
        });

        return validRegistrations;
    }

    public ArrayList<Registration> getFutureRegistrations() {
        ArrayList<Registration> validRegistrations = new ArrayList<>();
        removeOpenEndedConflicts();

        registrations.forEach(registration -> {
            if(!(registration.isFldOpRLogOn() && registration.isFldOpRLogOut()))
                validRegistrations.add(registration);
        });

        return validRegistrations;
    }

    // Removes rosters following active (logout is false) open ended rosters
    private void removeOpenEndedConflicts() {
        // First, find which dates have one or more active open ended rosters
        Set<LocalDate> dates = new HashSet<>();
        registrations.forEach(r -> {
            if (r.getFldOpREndTime() == null && !r.isFldOpRLogOut())
                dates.add(r.getFldOpRStartDate());
        });
        // Second, going through each of these dates and sorting rosters by start time
        dates.forEach(date -> {
            List<Registration> regs = getRegistrations(date);
            if (regs.size() <= 1) return;
            regs.sort((r1, r2) -> r1.getFldOpRStartTime().
                compareTo(r2.getFldOpRStartTime()));
            boolean remove = false;
            // when the first open ended roster is found the rest of the day is cleared
            for (int i = 0; i < regs.size(); i++) {
                Registration reg = regs.get(i);
                if (remove) {
                    registrations.remove(reg);
                    continue;
                }
                if (reg.getFldOpREndTime() == null && !reg.isFldOpRLogOut())
                    remove = true;
            };
        });
    }

    public boolean add(Registration registration) {
        if(registration == null) return false;

        if(registration.timesAreNull()) return false;

        if(registration.getFldOpREndTime() != null) {
            if(!isSameDay(LocalDateTime.of(registration.getFldOpRStartDate(), registration.getFldOpRStartTime()),
                    LocalDateTime.of(registration.getFldOpREndDate(), registration.getFldOpREndTime())))
                return false;
        }

        registrations.add(registration);
        return true;
    }

    public boolean delete(Registration inRegistration) {
        return registrations.removeIf(r -> r.equals(inRegistration));
    }

    public ArrayList<ArrayList<Registration>> getWeeklyRegistrationArray(LocalDate start_time) {
        ArrayList<ArrayList<Registration>> weekList = new ArrayList<>();

        LocalDateTime pointer_time = LocalDateTime.of(start_time, min_time);
        for(int i = 0; i < 7; i++) {
            ArrayList<Registration> dailyRegistrations = new ArrayList<>();
            for(int j = 0; j < registrations.size(); j++) {
                if(registrations.get(j).getFldOpRStartDate() != null
                        && registrations.get(j).getFldOpRStartTime() != null) {
                    if(isSameDay(LocalDateTime.of(registrations.get(j).getFldOpRStartDate(),
                            registrations.get(j).getFldOpRStartTime()), pointer_time))
                        dailyRegistrations.add(registrations.get(j));
                }
            }
            weekList.add(dailyRegistrations);
            pointer_time = pointer_time.plusDays(1);
        }

        return weekList;
    }

    // Same day means that the given intervals are in the same min_time and max_time interval,
    // not necessarily the same date. (example both dates are between 9am - 1am)
    public boolean isSameDay(LocalDateTime d1, LocalDateTime d2) {
        if(d1 == null || d2 == null) return false;
        if(!timeIsInValidRange(d1.toLocalTime()) || !timeIsInValidRange(d2.toLocalTime())) return false;

        LocalDate current_date;
        LocalDateTime earliest_time = d1.isAfter(d2) ? d2 : d1;
        LocalDateTime latest_time = d1.isAfter(d2) ? d1 : d2;

        if(earliest_time.getHour() >= 0 && earliest_time.getHour() <= max_time.getHour())
            current_date = earliest_time.toLocalDate().minusDays(1);
        else current_date = earliest_time.toLocalDate();

        while(!earliest_time.isAfter(LocalDateTime.of(current_date.plusDays(1), max_time))) {
            if(earliest_time.getYear() == latest_time.getYear()
            && earliest_time.getMonthValue() == latest_time.getMonthValue()
            && earliest_time.getDayOfMonth() == latest_time.getDayOfMonth()
            && earliest_time.getHour() == latest_time.getHour()) return true;
            earliest_time = earliest_time.plusHours(1);
        }
        return false;
    }

    public boolean timeIsInValidRange(LocalTime time) {
        if(time == null) return false;
        return !(time.isAfter(max_time) && time.isBefore(min_time));
    }


    public boolean deleteRegistrationsOnDate(LocalDate date) {
        if(date == null) return false;

        ArrayList<Registration> deleteRegistrations = new ArrayList<>();
        LocalDateTime start_time = LocalDateTime.of(date, min_time);

        registrations.forEach(registration -> {
            if(registration.getFldOpRStartDate() != null
                    && registration.getFldOpRStartTime() != null) {
                if(isSameDay(LocalDateTime.of(registration.getFldOpRStartDate(),
                        registration.getFldOpRStartTime()), start_time))
                    deleteRegistrations.add(registration);
            }

        });

        if(deleteRegistrations.size() == 0) return false;

        deleteRegistrations.forEach(this::delete);
        return true;
    }

    public boolean addNewDailyPlans(ArrayList<ArrayList<LocalDateTime>> newIntervals, LocalDate new_date) {
        if(newIntervals == null || new_date == null) return false;
        if(newIntervals.size() == 0) {
            deleteRegistrationsOnDate(new_date);
            return false;
        }

        int registrationSize = getRegistrations().size();
        deleteRegistrationsOnDate(new_date);
        ArrayList<Registration> oldRegistrations = getRegistrations(new_date);

        for(int i = 0; i < newIntervals.size(); i++) {
            LocalDateTime new_start = newIntervals.get(i).get(0);
            LocalDateTime new_end = newIntervals.get(i).get(1);

            if(new_end == null) {
                registrations.add(new Registration(
                        RequestManager.userID,
                        new_start.toLocalDate(),
                        new_start.toLocalDate(),
                        new_start.toLocalTime(),
                        null,
                        false,
                        false,
                        false
                ));
            } else {
                registrations.add(new Registration(
                        RequestManager.userID,
                        new_start.toLocalDate(),
                        new_end.toLocalDate(),
                        new_start.toLocalTime(),
                        new_end.toLocalTime(),
                        false,
                        false,
                        false
                ));
            }
        }

        // Returns true if new registrations are added
        return registrationSize != getRegistrations().size();
    }

    public void resetRegistrations() {
        registrations = new ArrayList<>();
    }
}
