package com.example.mijnboot;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class MetaDataContainer {
    private static String META_DATA_LOG = "meta-data-log";

    public static String get(Context context, String key) {
        try{
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;

            return bundle.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(META_DATA_LOG, "Failed to load meta-data, loading from defaults, NameNotFound: " + e.getMessage());
        } catch (NullPointerException e) {
            Log.w(META_DATA_LOG, "Failed to load meta-data loading from defaults, NullPointer: " + e.getMessage());
        }

        throw new RuntimeException("No value found for param: ".concat(key));
    }
}
