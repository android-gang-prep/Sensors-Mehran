package com.example.mehranm1.database;

import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import com.example.mehranm1.RecordItemModel;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

public class Converters {


    @TypeConverter
    public static List<RecordItemModel> toList(String json) {
        Gson gson = new Gson();
        return Arrays.asList(gson.fromJson(json, RecordItemModel[].class));
    }
    @TypeConverter
    public static String fromList(List<RecordItemModel> recordItemModels) {
        return new Gson().toJson(recordItemModels);
    }
}
