package com.example.eldercare;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicines")
public class Medicine {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String type;      // Daily / Weekly
    public String times;     // Morning-08:00 AM,Afternoon-02:00 PM

    public int sortTime;     // used for sorting

    public boolean taken;
    public long alarmTime;   // for missed detection
}