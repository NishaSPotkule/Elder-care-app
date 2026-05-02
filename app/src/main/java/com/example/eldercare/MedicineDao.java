package com.example.eldercare;

import androidx.room.*;
import java.util.List;

@Dao
public interface MedicineDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Medicine medicine);

    @Query("SELECT * FROM medicines ORDER BY sortTime ASC")
    List<Medicine> getAll();

    @Update
    void update(Medicine medicine);

    @Delete
    void delete(Medicine medicine);

    @Query("SELECT * FROM medicines WHERE id = :id LIMIT 1")
    Medicine getById(int id);
}