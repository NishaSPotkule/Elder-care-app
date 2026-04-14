package com.example.eldercare;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    Context context;
    ArrayList<Medicine> list;
    AppDatabase db;

    public MedicineAdapter(Context context, ArrayList<Medicine> list) {
        this.context = context;
        this.list = list;
        db = AppDatabase.getInstance(context);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, info;
        Button takeBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvName);
            info = itemView.findViewById(R.id.tvInfo);
            takeBtn = itemView.findViewById(R.id.btnTake);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_medicine, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Medicine m = list.get(position);

        holder.name.setText(m.name);


        if (m.times != null && !m.times.isEmpty()) {

            String[] timesArray = m.times.split(",");

            StringBuilder display = new StringBuilder();

            Calendar now = Calendar.getInstance();
            int currentHour = now.get(Calendar.HOUR_OF_DAY);
            int currentMinute = now.get(Calendar.MINUTE);

            boolean isAnyDue = false;

            for (String t : timesArray) {


                String[] parts = t.split("-");

                String label = parts[0];
                String time = parts[1];

                display.append(label).append(" ").append(time).append("\n");


                try {
                    String[] timeParts = time.split(" ");
                    String[] hm = timeParts[0].split(":");

                    int hour = Integer.parseInt(hm[0]);
                    int minute = Integer.parseInt(hm[1]);
                    String ampm = timeParts[1];

                    int hour24 = hour % 12;
                    if (ampm.equals("PM")) hour24 += 12;

                    if (currentHour > hour24 ||
                            (currentHour == hour24 && currentMinute >= minute)) {
                        isAnyDue = true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            holder.info.setText(m.type + "\n" + display.toString());


            if (m.taken) {
                holder.takeBtn.setText("Taken ✅");
                holder.takeBtn.setEnabled(false);

            } else if (isAnyDue) {
                holder.takeBtn.setText("Take Now 💊");
                holder.takeBtn.setEnabled(true);

                holder.takeBtn.setOnClickListener(v -> {
                    m.taken = true;
                    db.medicineDao().update(m);
                    notifyItemChanged(position);
                });

            } else {
                holder.takeBtn.setText("Upcoming ⏰");
                holder.takeBtn.setEnabled(false);
            }

        } else {
            holder.info.setText("No time set");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    private String formatTimes(String times) {

        if (times == null || times.isEmpty()) return "";

        String[] parts = times.split(",");
        StringBuilder builder = new StringBuilder();

        for (String p : parts) {
            builder.append("• ")
                    .append(p.replace("-", " : "))
                    .append("\n");
        }

        return builder.toString();
    }


    private boolean isMedicineDue(String times) {

        if (times == null) return false;

        String[] parts = times.split(",");

        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        for (String p : parts) {
            try {
                String timePart = p.split("-")[1];

                String[] t = timePart.split(" ");
                String[] hm = t[0].split(":");

                int hour = Integer.parseInt(hm[0]);
                int minute = Integer.parseInt(hm[1]);

                boolean isAM = t[1].equals("AM");

                int hour24 = isAM ? hour % 12 : (hour % 12) + 12;

                if (currentHour > hour24 ||
                        (currentHour == hour24 && currentMinute >= minute)) {
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }
}