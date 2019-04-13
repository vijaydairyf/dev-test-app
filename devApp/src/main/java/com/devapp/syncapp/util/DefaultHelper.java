package com.devapp.syncapp.util;

import android.app.ActivityManager;
import android.content.Context;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Pankaj on 6/15/2018.
 */

public class DefaultHelper {
    public static boolean isMyServiceRunning(Context context, Class serviceClass) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
