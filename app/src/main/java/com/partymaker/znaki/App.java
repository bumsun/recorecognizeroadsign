package com.partymaker.znaki;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class App extends Application {
    public static Context ctx;

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}