package com.example.eldercare;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface MedicineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Medicine medicine);

    @Query("SELECT * FROM medicines ORDER BY hour, minute")
    List<Medicine> getAll();

    @Delete
    void delete(Medicine medicine);

    @Update
    void update(Medicine medicine);
}