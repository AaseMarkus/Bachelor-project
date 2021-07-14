package oslomet.bachelorprosjekt.molendoansatt;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.util.ArrayList;

import oslomet.bachelorprosjekt.molendoansatt.fragments.DailyPlanFragment;
import oslomet.bachelorprosjekt.molendoansatt.objects.DailyPlanTime;
import oslomet.bachelorprosjekt.molendoansatt.objects.RegistrationHandler;

public class DailyPlanAdapter extends RecyclerView.Adapter<DailyPlanAdapter.DailyPlanViewHolder> {
    public interface DailyPlanListener {
        void changeSelected(int position, boolean status);
        void delete(int position);
        void applyNewTime(LocalDateTime start_time, LocalDateTime end_time,
                          LocalDateTime start_pointer_time, LocalDateTime end_pointer_time);
    }

    ArrayList<DailyPlanTime> shifts;
    Context context;
    DailyPlanFragment fragment;
    DailyPlanListener listener;
    RegistrationHandler registrationHandler;

    public DailyPlanAdapter(Context context, DailyPlanFragment fragment,
                            ArrayList<DailyPlanTime> shifts) {
        this.context = context;
        this.fragment = fragment;
        this.shifts = shifts;
        listener = fragment;
        registrationHandler = MainActivity.getRegistrationHandler();
    }

    @NonNull
    @Override
    public DailyPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dailyplan_row, parent, false);
        return new DailyPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyPlanViewHolder holder, int position) {
        updateColours(holder, position);
        boolean disabled = false;

        if(shifts.get(position).getStart_time().isBefore(registrationHandler.getCurrent_date())) {
            disabled = true;
        }

        // Format time
        int start_hour = shifts.get(position).getStart_time().getHour();
        int start_minute = shifts.get(position).getStart_time().getMinute();
        int end_hour = shifts.get(position).getEnd_time().getHour();
        int end_minute = shifts.get(position).getEnd_time().getMinute();

        String time = (start_hour > 9 ? String.valueOf(start_hour) : "0" + start_hour) +
                ":" + (start_minute > 9 ? String.valueOf(start_minute) : "0" + start_minute) +
                " - ";

        if(!shifts.get(position).isOpenended()) {
            time += (end_hour > 9 ? String.valueOf(end_hour) : "0" + end_hour) +
                    ":" + (end_minute > 9 ? String.valueOf(end_minute) : "0" + end_minute);
        } else time += "<>";

        holder.tvTime.setText(time);



        if(!disabled) {
            holder.ivAddTime.setOnClickListener(v -> {
                NewTimeDialog newTimeDialog = new NewTimeDialog(
                        shifts.get(position).getStart_time(),
                        shifts.get(position).getEnd_time());
                newTimeDialog.setTargetFragment(fragment, 300);
                newTimeDialog.show(((ApplicationActivity) context).getSupportFragmentManager(),
                        "New Time Dialog");
            });

            holder.cbSelected.setOnCheckedChangeListener(null);

            holder.cbSelected.setChecked(shifts.get(position).isSelected());

            holder.cbSelected.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        listener.changeSelected(position, isChecked);
                        updateColours(holder, position);
                    }
            );

            holder.ivDelete.setOnClickListener(v -> listener.delete(position));

            // Adds remove button for custom times
            if(shifts.get(position).isCustom()){
                holder.ivAddTime.setVisibility(View.INVISIBLE);
                holder.ivDelete.setVisibility(View.VISIBLE);
            }

            // Changes value of checkbox when row is clicked
            if(!shifts.get(position).isOpenended()) {
                holder.mainLayout.setOnClickListener(v ->
                        holder.cbSelected.setChecked(!holder.cbSelected.isChecked()));
                holder.mainLayout.setOnLongClickListener(v -> {
                    listener.applyNewTime(shifts.get(position).getStart_time(),
                            shifts.get(position).getEnd_time(), shifts.get(position).getStart_time(),
                            null);
                    return true;
                });

                holder.ivAddTime.setVisibility(View.VISIBLE);
                holder.cbSelected.setVisibility(View.VISIBLE);
            } else {
                holder.ivAddTime.setVisibility(View.INVISIBLE);
                holder.cbSelected.setVisibility(View.INVISIBLE);
            }
        } else {
            holder.ivAddTime.setVisibility(View.INVISIBLE);
            holder.cbSelected.setVisibility(View.INVISIBLE);
        }
    }

    private void updateColours(@NonNull DailyPlanViewHolder holder, int position) {
        // Sets alternating background colour
        if(position % 2 == 0) {
            if(shifts.get(position).isOpenended())
                holder.cvDailyPlan.setCardBackgroundColor(context.getResources().getColor(R.color.cvLightOpenEnded));
            else if(shifts.get(position).isSelected())
                holder.cvDailyPlan.setCardBackgroundColor(context.getResources().getColor(R.color.cvLightSelected));
            else holder.cvDailyPlan.setCardBackgroundColor(context.getResources().getColor(R.color.cvLight));
        }
        else {
            if(shifts.get(position).isOpenended())
                holder.cvDailyPlan.setCardBackgroundColor(context.getResources().getColor(R.color.cvDarkOpenEnded));
            else if(shifts.get(position).isSelected())
                holder.cvDailyPlan.setCardBackgroundColor(context.getResources().getColor(R.color.cvDarkSelected));
            else holder.cvDailyPlan.setCardBackgroundColor(context.getResources().getColor(R.color.cvDark));
        }
    }

    @Override
    public int getItemCount() {
        return shifts.size();
    }

    public class DailyPlanViewHolder extends RecyclerView.ViewHolder {
        CardView cvDailyPlan;
        TextView tvTime;
        ImageView ivAddTime;
        CheckBox cbSelected;
        ImageView ivDelete;
        ConstraintLayout mainLayout;

        public DailyPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            cvDailyPlan = itemView.findViewById(R.id.cv_daily_plan);
            tvTime = itemView.findViewById(R.id.tv_daily_plan_time);
            ivAddTime = itemView.findViewById(R.id.iv_add_time);
            cbSelected = itemView.findViewById(R.id.iv_daily_plan_selected);
            ivDelete = itemView.findViewById(R.id.iv_daily_plan_delete);
            mainLayout = itemView.findViewById(R.id.cl_daily_plan_row);
        }
    }
}
