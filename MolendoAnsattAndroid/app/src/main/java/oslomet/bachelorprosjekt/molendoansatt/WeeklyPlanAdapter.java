package oslomet.bachelorprosjekt.molendoansatt;

import android.content.Context;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import oslomet.bachelorprosjekt.molendoansatt.fragments.DailyPlanFragment;
import oslomet.bachelorprosjekt.molendoansatt.fragments.WeeklyPlanFragment;
import oslomet.bachelorprosjekt.molendoansatt.objects.Registration;
import oslomet.bachelorprosjekt.molendoansatt.objects.RegistrationHandler;

public class WeeklyPlanAdapter extends RecyclerView.Adapter<WeeklyPlanAdapter.WeeklyPlanViewHolder> {
    Context context;
    ArrayList<ArrayList<Registration>> weekList;
    String[] weekdays;
    LocalDate minDate;
    RegistrationHandler registrationHandler;

    public interface WeeklyPlanListener {
        void delete(int position);
    }

    WeeklyPlanListener listener;

    public WeeklyPlanAdapter(Context context, WeeklyPlanFragment fragment, ArrayList<ArrayList<Registration>> weekList, String[] weekdays, LocalDate minDate) {
        this.context = context;
        this.weekList = weekList;
        this.weekdays = weekdays;
        this.minDate = minDate;
        this.registrationHandler = MainActivity.getRegistrationHandler();
        weekList.forEach(Collections::sort);

        listener = fragment;
    }

    @NonNull
    @Override
    public WeeklyPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.weeklyplan_row, parent, false);
        return new WeeklyPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeeklyPlanViewHolder holder, int position) {
        // Sets alternating background colour
        setBackgroundcolours(holder, position);

        holder.tvDay.setText(weekdays[position]);

        //Write up to 2 shifts in cardview
        StringBuilder formattedInterval = new StringBuilder();

        for(int i = 0; i < weekList.get(position).size(); i++) {
            if(i == 2) {
                formattedInterval.append("...");
                break;
            }

            formattedInterval.append(weekList.get(position).get(i).getIntervalString());
            formattedInterval.append("\n");
        }

        holder.tvShiftTimes.setText(formattedInterval.toString());

        //Write how many shifts on current day
        switch (weekList.get(position).size()){
            case 0:
                holder.tvShift.setText("");
                break;
            case 1:
                String text = "1 " + context.getString(R.string.shift_singular);
                holder.tvShift.setText(text);
                break;
            default:
                String shifts = weekList.get(position).size() + " " + context.getString(R.string.shift_multiple);
                holder.tvShift.setText(shifts);
        }

        setOnClickListeners(holder, position);
    }

    private void setOnClickListeners(@NonNull WeeklyPlanViewHolder holder, int position) {
        if(!registrationHandler.getCurrent_date().plusHours(1).isAfter(LocalDateTime.of(
                minDate.plusDays(position+1), registrationHandler.getMax_time()))) {
            // Send to dailyplan when cardview is clicked
            holder.mainLayout.setOnClickListener(v -> {
                ArrayList<Registration> futureShifts = new ArrayList<>();

                weekList.get(position).forEach(registration -> {
                    if(!registration.isFldOpRLogOut()) futureShifts.add(registration);
                });

                DailyPlanFragment dailyPlanFragment = new DailyPlanFragment(futureShifts,
                        minDate.plusDays(position));
                ((ApplicationActivity) context).showFragment(dailyPlanFragment, null);
            });
            holder.ivDelete.setOnClickListener(v -> listener.delete(position));
        }
        else {
            holder.ivDelete.setVisibility(View.INVISIBLE);
            holder.ivEdit.setVisibility(View.INVISIBLE);
        }

    }

    private void setBackgroundcolours(@NonNull WeeklyPlanViewHolder holder, int position) {
        if(position % 2 == 0) holder.cvWeeklyPlan.setCardBackgroundColor(context.getResources().getColor(R.color.cvLight));
        else holder.cvWeeklyPlan.setCardBackgroundColor(context.getResources().getColor(R.color.cvDark));
    }

    @Override
    public int getItemCount() {
        return weekList.size();
    }

    public class WeeklyPlanViewHolder extends RecyclerView.ViewHolder {
        TextView tvDay, tvShift, tvShiftTimes;
        CardView cvWeeklyPlan;
        ConstraintLayout mainLayout;
        ImageView ivDelete, ivEdit;

        public WeeklyPlanViewHolder(@NonNull View itemView) {
            super(itemView);

            tvDay = itemView.findViewById(R.id.tv_day);
            tvShift = itemView.findViewById(R.id.tv_shift);
            tvShiftTimes = itemView.findViewById(R.id.tv_shift_times);
            cvWeeklyPlan = itemView.findViewById(R.id.cv_weekly_plan);
            mainLayout = itemView.findViewById(R.id.cl_weekly_plan_row);
            ivDelete = itemView.findViewById(R.id.iv_weekly_row_delete);
            ivEdit = itemView.findViewById(R.id.iv_weekly_row_edit);
        }
    }
}
