package com.devapp.devmain.peripherals.network;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;

/**
 * Created by xx on 18/4/17.
 */

public class HotspotManager {

    //check whether wifi hotspot on or off
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        } catch (Throwable ignored) {
        }
        return false;
    }

    //worked till 14.1.7 RC2
   /* public static void enableHotspot(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = new WifiConfiguration();
        try {
//            wificonfiguration.SSID="smartudp";
//            wificonfiguration.preSharedKey="$7!Af4YvGm";
            wificonfiguration.SSID = "openUdp";
            wificonfiguration.preSharedKey = "123456789";
            wificonfiguration.allowedKeyManagement.set(4);
            // if wifi is on, turn it off
//            if (isApOn(context))
            wifimanager.setWifiEnabled(false);
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !isApOn(context));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static void enableHotspot(Context context, String ssid, String password) {
        if (!isApOn(context)) {
            WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            WifiConfiguration wificonfiguration = new WifiConfiguration();
            try {
                wificonfiguration.SSID = ssid;
                wificonfiguration.preSharedKey = password;
                wificonfiguration.allowedKeyManagement.set(4);
                wifimanager.setWifiEnabled(false);
                Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);

                method.invoke(wifimanager, wificonfiguration, !isApOn(context));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // turn off hotspot
    public static void disableHotspot(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = new WifiConfiguration();
        try {
//            wifimanager.setWifiEnabled(true);
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, false);
            Log.v(PROBER, "Turning Off hotspot");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean changeHotspot(Context context, String ssid, String password) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = new WifiConfiguration();
        try {
            wificonfiguration.SSID = ssid;
            wificonfiguration.preSharedKey = password;
            wificonfiguration.allowedKeyManagement.set(4);
            wifimanager.setWifiEnabled(false);
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            if (isApOn(context)) {
                disableHotspot(context);
                changeHotspot(context, ssid, password);
            } else
                method.invoke(wifimanager, wificonfiguration, !isApOn(context));

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
