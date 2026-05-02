package com.example.eldercare;

import android.app.AlarmManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class AddMedicineActivity extends AppCompatActivity {

    EditText nameInput;
    RadioButton dailyRadio;
    MaterialButton saveBtn;

    CheckBox checkMorning, checkAfternoon, checkNight;
    TextView tvMorningTime, tvAfternoonTime, tvNightTime;

    AppDatabase db;

    ArrayList<String> selectedTimes = new ArrayList<>();

    int mHour=-1,mMinute=-1,aHour=-1,aMinute=-1,nHour=-1,nMinute=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        nameInput = findViewById(R.id.medicineName);
        saveBtn = findViewById(R.id.saveBtn);

        dailyRadio = findViewById(R.id.radioDaily);

        checkMorning = findViewById(R.id.checkMorning);
        checkAfternoon = findViewById(R.id.checkAfternoon);
        checkNight = findViewById(R.id.checkNight);

        tvMorningTime = findViewById(R.id.tvMorningTime);
        tvAfternoonTime = findViewById(R.id.tvAfternoonTime);
        tvNightTime = findViewById(R.id.tvNightTime);

        db = AppDatabase.getInstance(this);

        requestNotificationPermission();

        tvMorningTime.setOnClickListener(v -> openTimePicker("MORNING"));
        tvAfternoonTime.setOnClickListener(v -> openTimePicker("AFTERNOON"));
        tvNightTime.setOnClickListener(v -> openTimePicker("NIGHT"));

        saveBtn.setOnClickListener(v -> saveMedicine());
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(new String[]{
                    android.Manifest.permission.POST_NOTIFICATIONS
            }, 101);
        }
    }

    private boolean checkExactAlarmPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            AlarmManager alarmManager =
                    (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            if (!alarmManager.canScheduleExactAlarms()) {

                Toast.makeText(this, "Please allow exact alarm permission", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);

                return false;
            }
        }
        return true;
    }

    private void openTimePicker(String type) {

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {

                    String ampm = (hourOfDay >= 12) ? "PM" : "AM";
                    int displayHour = hourOfDay % 12;
                    if (displayHour == 0) displayHour = 12;

                    String time = String.format("%02d:%02d %s", displayHour, minute, ampm);

                    if (type.equals("MORNING")) {
                        tvMorningTime.setText(time);
                        mHour = hourOfDay;
                        mMinute = minute;
                    } else if (type.equals("AFTERNOON")) {
                        tvAfternoonTime.setText(time);
                        aHour = hourOfDay;
                        aMinute = minute;
                    } else {
                        tvNightTime.setText(time);
                        nHour = hourOfDay;
                        nMinute = minute;
                    }
                },
                8, 0, false
        );

        dialog.show();
    }

    private void saveMedicine() {


        if (!checkExactAlarmPermission()) {
            return;
        }

        String name = nameInput.getText().toString().trim();

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter name", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!checkMorning.isChecked() && !checkAfternoon.isChecked() && !checkNight.isChecked()) {
            Toast.makeText(this, "Select at least one time", Toast.LENGTH_SHORT).show();
            return;
        }

        selectedTimes.clear();

        if (checkMorning.isChecked()) {
            if (mHour == -1) {
                Toast.makeText(this, "Select Morning time", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedTimes.add("Morning-" + tvMorningTime.getText());
        }

        if (checkAfternoon.isChecked()) {
            if (aHour == -1) {
                Toast.makeText(this, "Select Afternoon time", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedTimes.add("Afternoon-" + tvAfternoonTime.getText());
        }

        if (checkNight.isChecked()) {
            if (nHour == -1) {
                Toast.makeText(this, "Select Night time", Toast.LENGTH_SHORT).show();
                return;
            }
            selectedTimes.add("Night-" + tvNightTime.getText());
        }

        Medicine med = new Medicine();
        med.name = name;
        med.type = "Daily";
        med.times = TextUtils.join(",", selectedTimes);
        med.taken = false;

        long id = db.medicineDao().insert(med);
        med.id = (int) id;


        if (checkMorning.isChecked())
            AlarmHelper.setDailyAlarm(this, med.id, mHour, mMinute);

        if (checkAfternoon.isChecked())
            AlarmHelper.setDailyAlarm(this, med.id, aHour, aMinute);

        if (checkNight.isChecked())
            AlarmHelper.setDailyAlarm(this, med.id, nHour, nMinute);

        Toast.makeText(this, "Saved ", Toast.LENGTH_SHORT).show();
        finish();
    }
}