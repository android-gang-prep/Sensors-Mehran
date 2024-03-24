package com.example.mehranm1.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.mehranm1.RecordModel;

import java.util.List;

@Dao
public interface RecordDAO {

    @Insert
    void insertAll(RecordModel... recordModel);

    @Insert
    long insert(RecordModel recordModel);

    @Update
    void updateAll(RecordModel... recordModel);


    @Query("SELECT * FROM records where status=1 ORDER BY id DESC")
    List<RecordModel> getAll();


    @Query("SELECT * FROM records WHERE id=:id LIMIT 1")
    RecordModel getRecord(long id);
}
