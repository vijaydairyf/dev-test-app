package com.devapp.devmain.util;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.devapp.devmain.peripherals.network.HotspotManager;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.util.List;

public class AppWifi {
    private static final String TAG = "AppWifi";

    public static boolean Join(Context context, WifiManager wm) {
        if (!wm.isWifiEnabled()) {
            HotspotManager.disableHotspot(context);
            wm.setWifiEnabled(true);
            List<WifiConfiguration> list = wm.getConfiguredNetworks();
            if (list != null)
                for (WifiConfiguration i : list) {
                    wm.removeNetwork(i.networkId);
                    wm.saveConfiguration();
                }

            wm.disconnect();
            return connectWifi(context, SmartCCConstants.ssidList[0], SmartCCConstants.passwordList[0], "");
        }
        return false;
    }

    private static boolean connectWifi(Context context, String networkSSID, String
            networkPass, String security) {
        Log.d(TAG, "Connecting to SSID \"" + networkSSID + "\" with password \"" + networkPass + "\" and with security \"" + security + "\" ...");

        // You need to create WifiConfiguration instance like this:
        WifiConfiguration conf = new WifiConfiguration();

        // Please note the quotes. String should contain ssid in quotes
        conf.SSID = "\"" + networkSSID + "\"";

        if (security.equals("OPEN")) {
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (security.equals("WEP")) {
            conf.wepKeys[0] = "\"" + networkPass + "\"";
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else {
            conf.preSharedKey = "\"" + networkPass + "\"";
        }

        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int networkId = wifiManager.addNetwork(conf);
        Log.d(TAG, "Network ID: " + networkId);

        wifiManager.disconnect();
        boolean result = wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
        wifiManager.saveConfiguration();
        return result;
    }
}
