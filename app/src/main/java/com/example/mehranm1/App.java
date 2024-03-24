package com.example.mehranm1;

import android.app.Application;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import java.io.File;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(getApplicationContext());
        File dexOutputDir  = getCodeCacheDir();
        dexOutputDir.setReadOnly();
    }
}
