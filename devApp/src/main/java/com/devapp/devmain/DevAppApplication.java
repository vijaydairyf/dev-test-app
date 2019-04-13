package com.devapp.devmain;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.multidex.MultiDex;
import android.telephony.TelephonyManager;

import com.crashlytics.android.Crashlytics;
import com.devapp.devmain.server.SessionManager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import io.fabric.sdk.android.Fabric;

/**
 * Created by u_pendra on 15/2/18.
 */

public class DevAppApplication extends Application {
    private static final String TAG = "DevAppApplication";

    private static Context mContext;
    private static TreeMap<Date, List<Long>> map;                                                               //Key will be long timestamp,& value will be Sequence List

    public static Context getAmcuContext() {
        return mContext;
    }

    public static void clearSyncSeqList() {
        if (map != null) {
            map.clear();
            map = null;
        }

    }

    public static void addToSyncSeqMap(long seq, long postDate, String postShift) {
        if (map == null)
            map = new TreeMap<Date, List<Long>>();

        Date newCollectionDate = new Date(postDate);
        if (map.get(newCollectionDate) == null) {
            ArrayList<Long> temp = new ArrayList<Long>();
            temp.add(seq);
            map.put(newCollectionDate, temp);
        } else
            map.get(newCollectionDate).add(seq);
    }

    public static TreeMap<Date, List<Long>> getSyncTimeStampMap() {
        return map;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Fabric.with(this, new Crashlytics());
        setCrashlyticsParams(mContext);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void setCrashlyticsParams(Context vContext) {

        String imei = null;
        TelephonyManager tm = (TelephonyManager) vContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null)
            imei = tm.getDeviceId();
        if (imei == null || imei.length() == 0)
            imei = Settings.Secure.getString(vContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        System.out.println("AndroidID: " + imei);
        SharedPreferences
                pref = getSharedPreferences(SessionManager.PREF_NAME,
                Context.MODE_PRIVATE);
        String centerName =
                pref.getString(SessionManager.KEY_SOCIETY_NAME, "SocName");
        Crashlytics.setUserIdentifier(String.valueOf(imei));
        Crashlytics.setUserName(centerName);
        Crashlytics.setUserEmail("smartAMCU.care@stellapps.com");

    }

}
