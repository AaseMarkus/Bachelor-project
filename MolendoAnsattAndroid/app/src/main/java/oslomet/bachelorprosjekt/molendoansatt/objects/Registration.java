package oslomet.bachelorprosjekt.molendoansatt.objects;

import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Registration implements Comparable<Registration> {


    private String fldOpRId;
    private String fldOpServId;
    private LocalDate fldOpRStartDate;
    private LocalDate fldOpREndDate;
    private LocalTime fldOpRStartTime;
    private boolean fldOpRLogOn;
    private boolean fldOpRLogOut;
    private LocalTime fldOpREndTime;
    private boolean fldOpRDropIn;
    private String fldOpRComment = "";

    public Registration(String fldOpRId, String fldOpServId, LocalDate fldOpRStartDate,
                        LocalDate fldOpREndDate, LocalTime fldOpRStartTime, LocalTime fldOpREndTime,
                        boolean fldOpRDropIn, boolean fldOpRLogOn, boolean fldOpRLogOut) {
        this.fldOpRId = fldOpRId;
        this.fldOpServId = fldOpServId;
        this.fldOpRStartDate = fldOpRStartDate;
        this.fldOpREndDate = fldOpREndDate;
        this.fldOpRStartTime = fldOpRStartTime;
        this.fldOpREndTime = fldOpREndTime;
        this.fldOpRDropIn = fldOpRDropIn;
        this.fldOpRLogOn = fldOpRLogOn;
        this.fldOpRLogOut = fldOpRLogOut;
    }

    public Registration(String fldOpServId, LocalDate fldOpRStartDate, LocalDate fldOpREndDate,
                        LocalTime fldOpRStartTime, LocalTime fldOpREndTime, boolean fldOpRDropIn,
                        boolean fldOpRLogOn, boolean fldOpRLogOut) {
        this.fldOpServId = fldOpServId;
        this.fldOpRStartDate = fldOpRStartDate;
        this.fldOpREndDate = fldOpREndDate;
        this.fldOpRStartTime = fldOpRStartTime;
        this.fldOpREndTime = fldOpREndTime;
        this.fldOpRDropIn = fldOpRDropIn;
        this.fldOpRLogOn = fldOpRLogOn;
        this.fldOpRLogOut = fldOpRLogOut;
    }

    public String getIntervalString() {
        if(getFldOpRStartTime() == null || getFldOpRStartDate() == null || getFldOpREndDate() == null)
            return "";

        int start_hour = getFldOpRStartTime().getHour();
        int start_minute = getFldOpRStartTime().getMinute();

        String start = (start_hour > 9 ? String.valueOf(start_hour) : "0" + start_hour) + ":" +
                (start_minute > 9 ? String.valueOf(start_minute) : "0" + start_minute) + " - ";
        String interval = "";

        if(getFldOpREndTime() == null) interval = start + "<>";
        else {
            int end_hour = getFldOpREndTime().getHour();
            int end_minute = getFldOpREndTime().getMinute();

            interval = start +
                    (end_hour > 9 ? String.valueOf(end_hour) : "0" + end_hour) + ":" +
                    (end_minute > 9 ? String.valueOf(end_minute) : "0" + end_minute);
        }

        return interval;
    }

    public boolean timesAreNull() {
        return getFldOpRStartDate() == null || getFldOpREndDate() == null ||
                getFldOpRStartTime() == null;
    }

    public boolean isFldOpRLogOn() {
        return fldOpRLogOn;
    }

    public void setFldOpRLogOn(boolean fldOpRLogOn) {
        this.fldOpRLogOn = fldOpRLogOn;
    }

    public boolean isFldOpRLogOut() {
        return fldOpRLogOut;
    }

    public void setFldOpRLogOut(boolean fldOpRLogOut) {
        this.fldOpRLogOut = fldOpRLogOut;
    }

    public LocalDate getFldOpRStartDate() {
        return fldOpRStartDate;
    }

    public void setFldOpRStartDate(LocalDate fldOpRStartDate) { this.fldOpRStartDate = fldOpRStartDate; }

    public LocalDate getFldOpREndDate() {
        return fldOpREndDate;
    }

    public void setFldOpREndDate(LocalDate fldOpREndDate) {
        this.fldOpREndDate = fldOpREndDate;
    }

    public LocalTime getFldOpRStartTime() {
        return fldOpRStartTime;
    }

    public void setFldOpRStartTime(LocalTime fldOpRStartTime) { this.fldOpRStartTime = fldOpRStartTime; }

    public LocalTime getFldOpREndTime() {
        return fldOpREndTime;
    }

    public void setFldOpREndTime(LocalTime fldOpREndTime) {
        this.fldOpREndTime = fldOpREndTime;
    }

    public String getFldOpRId() {
        return fldOpRId;
    }

    public void setFldOpRId(String fldOpRId) {
        this.fldOpRId = fldOpRId;
    }

    public String getFldOpServId() {
        return fldOpServId;
    }

    public void setFldOpServId(String fldOpServId) {
        this.fldOpServId = fldOpServId;
    }

    public boolean isFldOpRDropIn() {
        return fldOpRDropIn;
    }

    public void setFldOpRDropIn(boolean fldOpRDropIn) {
        this.fldOpRDropIn = fldOpRDropIn;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();
        try {
            String startTime = fldOpRStartTime.toString() + ":00";
            String endTime = fldOpREndTime == null ? null : fldOpREndTime.toString() + ":00";
            String startDate = fldOpREndDate.toString();
            String endDate = fldOpREndDate.toString();

            if(fldOpRId != null) obj.put("fldOpRId", fldOpRId);
            obj.put("fldOpServId", fldOpServId);
            obj.put("fldOpRStartTime", fldOpRStartTime);
            obj.put("fldOpREndTime", fldOpREndTime);
            obj.put("fldOpRStartDate", fldOpRStartDate);
            obj.put("fldOpREndDate", fldOpREndDate);
            obj.put("fldOpRLogOn", fldOpRLogOn);
            obj.put("fldOpRLogOut", fldOpRLogOut);
            obj.put("fldOpRDropIn", fldOpRDropIn);
            obj.put("fldOpRComment", fldOpRComment);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public int compareTo(Registration compareRegistration) {
        return LocalDateTime.of(this.getFldOpRStartDate(), this.getFldOpRStartTime())
                .compareTo(LocalDateTime.of(compareRegistration.getFldOpRStartDate(),
                        compareRegistration.getFldOpRStartTime()));
    }

    @Override
    public String toString() {
        return "Registration{" +
                "fldOpRId='" + fldOpRId + '\'' +
                ", fldOpServId='" + fldOpServId + '\'' +
                ", fldOpRStartDate=" + fldOpRStartDate +
                ", fldOpREndDate=" + fldOpREndDate +
                ", fldOpRStartTime=" + fldOpRStartTime +
                ", fldOpRLogOn=" + fldOpRLogOn +
                ", fldOpRLogOut=" + fldOpRLogOut +
                ", fldOpREndTime=" + fldOpREndTime +
                ", fldOpRDropIn=" + fldOpRDropIn +
                ", fldOpRComment='" + fldOpRComment + '\'' +
                '}';
    }
}