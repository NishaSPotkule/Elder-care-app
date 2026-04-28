package com.example.eldercare;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AddMedicineActivity extends AppCompatActivity {

    EditText nameInput;
    RadioButton dailyRadio, weeklyRadio;
    MaterialButton saveBtn;

    CheckBox checkMorning, checkAfternoon, checkNight;
    TextView tvMorningTime, tvAfternoonTime, tvNightTime;

    AppDatabase db;

    ArrayList<String> selectedTimes = new ArrayList<>();

    int mHour = -1, mMinute = -1;
    int aHour = -1, aMinute = -1;
    int nHour = -1, nMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        nameInput = findViewById(R.id.medicineName);
        saveBtn = findViewById(R.id.saveBtn);

        dailyRadio = findViewById(R.id.radioDaily);
        weeklyRadio = findViewById(R.id.radioWeekly);

        checkMorning = findViewById(R.id.checkMorning);
        checkAfternoon = findViewById(R.id.checkAfternoon);
        checkNight = findViewById(R.id.checkNight);

        tvMorningTime = findViewById(R.id.tvMorningTime);
        tvAfternoonTime = findViewById(R.id.tvAfternoonTime);
        tvNightTime = findViewById(R.id.tvNightTime);

        db = AppDatabase.getInstance(this);

        tvMorningTime.setOnClickListener(v -> {
            if (!checkMorning.isChecked()) {
                Toast.makeText(this, "Select Morning first", Toast.LENGTH_SHORT).show();
                return;
            }
            openTimePicker("MORNING");
        });

        tvAfternoonTime.setOnClickListener(v -> {
            if (!checkAfternoon.isChecked()) {
                Toast.makeText(this, "Select Afternoon first", Toast.LENGTH_SHORT).show();
                return;
            }
            openTimePicker("AFTERNOON");
        });

        tvNightTime.setOnClickListener(v -> {
            if (!checkNight.isChecked()) {
                Toast.makeText(this, "Select Night first", Toast.LENGTH_SHORT).show();
                return;
            }
            openTimePicker("NIGHT");
        });

        saveBtn.setOnClickListener(v -> saveMedicine());
    }

    private void openTimePicker(String type) {

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {

                    int displayHour = hourOfDay % 12;
                    if (displayHour == 0) displayHour = 12;

                    String ampm = (hourOfDay >= 12) ? "PM" : "AM";

                    String time = String.format("%02d:%02d %s", displayHour, minute, ampm);

                    switch (type) {
                        case "MORNING":
                            tvMorningTime.setText(time);
                            mHour = hourOfDay;
                            mMinute = minute;
                            break;

                        case "AFTERNOON":
                            tvAfternoonTime.setText(time);
                            aHour = hourOfDay;
                            aMinute = minute;
                            break;

                        case "NIGHT":
                            tvNightTime.setText(time);
                            nHour = hourOfDay;
                            nMinute = minute;
                            break;
                    }
                },
                8,
                0,
                false
        );

        dialog.show();
    }

    private void saveMedicine() {

        String name = nameInput.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter medicine name", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!checkMorning.isChecked() && !checkAfternoon.isChecked() && !checkNight.isChecked()) {
            Toast.makeText(this, "Select at least one time", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedTimes.clear();

        int earliestTime = Integer.MAX_VALUE;

        if (checkMorning.isChecked()) {
            if (mHour == -1) {
                Toast.makeText(this, "Select Morning time", Toast.LENGTH_SHORT).show();
                return;
            }

            String time = tvMorningTime.getText().toString();
            selectedTimes.add("Morning-" + time);

            int totalMin = mHour * 60 + mMinute;
            earliestTime = Math.min(earliestTime, totalMin);


            AlarmHelper.setDailyAlarm(this, name + "_MORNING", mHour, mMinute);
        }

        if (checkAfternoon.isChecked()) {
            if (aHour == -1) {
                Toast.makeText(this, "Select Afternoon time", Toast.LENGTH_SHORT).show();
                return;
            }

            String time = tvAfternoonTime.getText().toString();
            selectedTimes.add("Afternoon-" + time);

            int totalMin = aHour * 60 + aMinute;
            earliestTime = Math.min(earliestTime, totalMin);

            AlarmHelper.setDailyAlarm(this, name + "_AFTERNOON", aHour, aMinute);
        }

        if (checkNight.isChecked()) {
            if (nHour == -1) {
                Toast.makeText(this, "Select Night time", Toast.LENGTH_SHORT).show();
                return;
            }

            String time = tvNightTime.getText().toString();
            selectedTimes.add("Night-" + time);

            int totalMin = nHour * 60 + nMinute;
            earliestTime = Math.min(earliestTime, totalMin);

            AlarmHelper.setDailyAlarm(this, name + "_NIGHT", nHour, nMinute);
        }


        Medicine med = new Medicine();
        med.name = name;
        med.type = dailyRadio.isChecked() ? "Daily" : "Weekly";
        med.times = TextUtils.join(",", selectedTimes);
        med.sortTime = earliestTime;
        med.taken = false;

        db.medicineDao().insert(med);

        Toast.makeText(this, "Medicine Saved ✅", Toast.LENGTH_SHORT).show();
        finish();
    }
}