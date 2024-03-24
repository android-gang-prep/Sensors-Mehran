package com.example.mehranm1;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.mehranm1.database.Converters;

import java.util.ArrayList;
import java.util.List;


@Entity(tableName = "records")
public class RecordModel {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private int time;
    @TypeConverters(Converters.class)
    private List<RecordItemModel> checkPoints;
    private int status;


    public RecordModel(int time, List<RecordItemModel> checkPoints, int status) {
        this.time = time;
        this.checkPoints = checkPoints;
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public List<RecordItemModel> getCheckPoints() {
        return checkPoints;
    }

    public void addCheckPoint(RecordItemModel recordItemModel) {
        List<RecordItemModel> checkPoints2 = new ArrayList<>(checkPoints);
        checkPoints2.add(recordItemModel);
        checkPoints = new ArrayList<>(checkPoints2);
    }

    public void setCheckPoints(List<RecordItemModel> checkPoints) {
        this.checkPoints = checkPoints;
    }
}
