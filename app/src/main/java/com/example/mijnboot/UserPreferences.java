package com.example.mijnboot;

import android.content.Context;
import android.content.SharedPreferences;

public class UserPreferences {
    static private String FILE_NAME = "rc_bluetooth";
    private String TRANSMIT_INTERVAL_KEY = "transmit_interval";
    private int DEFAULT_TRANSMIT_INTERVAL = 2000;

    private Context context;

    public UserPreferences(Context context) {
        this.context = context;
    }

    public int getTransmitInterval() {
        return get(TRANSMIT_INTERVAL_KEY, DEFAULT_TRANSMIT_INTERVAL);
    }

    public void setTransmitInterval(int value) {
        set(TRANSMIT_INTERVAL_KEY, value);
    }

    private int get(String key, int defaultValue) {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        return sharedPref.getInt(key, defaultValue);
    }

    private void set(String key, int value) {
        SharedPreferences sharedPref = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(key, value);
        editor.commit();
    }
}
