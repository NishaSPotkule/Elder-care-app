package com.example.eldercare;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "medicines")
public class Medicine {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public int hour;
    public int minute;
    public boolean isAM;
    public String type;
    public String times;
    public int sortTime;

    public boolean taken;
}