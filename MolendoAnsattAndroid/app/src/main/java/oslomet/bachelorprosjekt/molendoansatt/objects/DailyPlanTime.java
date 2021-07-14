package oslomet.bachelorprosjekt.molendoansatt.objects;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class DailyPlanTime implements Comparable<DailyPlanTime>{
    private LocalDateTime start_time;
    private LocalDateTime end_time;
    private LocalDateTime delete_start_time;
    private LocalDateTime delete_end_time;
    private boolean selected;
    private boolean isCustom = false;
    private boolean isOpenended = false;

    public DailyPlanTime(LocalDateTime start_time, LocalDateTime end_time, boolean selected) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.selected = selected;
    }

    public DailyPlanTime(LocalDateTime start_time, LocalDateTime end_time, boolean selected, boolean openended) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.selected = selected;
        this.isOpenended = openended;
    }

    public LocalDateTime getDelete_start_time() {
        return delete_start_time;
    }

    public void setDelete_start_time(LocalDateTime delete_start_time) {
        this.delete_start_time = delete_start_time;
    }

    public LocalDateTime getDelete_end_time() {
        return delete_end_time;
    }

    public void setDelete_end_time(LocalDateTime delete_end_time) {
        this.delete_end_time = delete_end_time;
    }

    public boolean isOpenended() {
        return isOpenended;
    }

    public void setOpenended(boolean openended) {
        isOpenended = openended;
    }

    public LocalDateTime getStart_time() {
        return start_time;
    }

    public void setStart_time(LocalDateTime start_time) {
        this.start_time = start_time;
    }

    public LocalDateTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(LocalDateTime end_time) {
        this.end_time = end_time;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int compareTo(DailyPlanTime compareTime) {
        return this.start_time.compareTo(compareTime.start_time);
    }

    public boolean isCustom() { return isCustom; }

    public void setCustom(boolean custom) { isCustom = custom; }

    @Override
    public String toString() {
        return "DailyPlanTime{" +
                "start_time=" + start_time +
                ", end_time=" + end_time +
                ", selected=" + selected +
                ", isCustom=" + isCustom +
                '}';
    }
}
