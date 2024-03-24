package com.example.mehranm1.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.mehranm1.RecordModel;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


@Database(entities = {RecordModel.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase appDatabase;

    public abstract RecordDAO recordDAO();

    public static AppDatabase getAppDatabase(Context context) {
        if (appDatabase == null)
            appDatabase = Room.databaseBuilder(context, AppDatabase.class, "sport").allowMainThreadQueries().build();
        return appDatabase;
    }

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void insertAll(RecordModel... recordModels) {

        executorService.execute(() ->recordDAO().insertAll(recordModels));
    }
    public void updateAll(RecordModel... recordModels) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() ->recordDAO().updateAll(recordModels));

        Log.i("TAG", "updateAll: "+recordDAO().getAll().size());
    }
    public long insert(RecordModel recordModel) {
        Callable<Long> insertCallable = () -> recordDAO().insert(recordModel);
        long rowId = 0;

        Future<Long> future = executorService.submit(insertCallable);
        try {
            rowId = future.get();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return rowId;
    }
}
