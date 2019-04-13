package com.devapp.syncapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import com.devapp.syncapp.service.ListenApService;

/**
 * Created by Pankaj on 5/1/2018.
 */

public class HotspotReceiver extends BroadcastReceiver {
    public static final int AP_DISABLING = 10;
    public static final int AP_DISABLED = 11;
    public static final int AP_ENABLING = 12;
    public static final int AP_ENABLED = 13;
    private static final String TAG = "HotspotReceiver";
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        String action = intent.getAction();
        if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            try {
                switch (state) {
                    case AP_DISABLING:
                        break;
                    case AP_DISABLED:
                        Log.i(TAG, "onReceive: AP_DISABLED");
                        CancelPreviousListen();
                        break;
                    case AP_ENABLING:
                        break;
                    case AP_ENABLED:
                        Log.i(TAG, "onReceive: AP_ENABLED");
                        Listen();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void logExtra(Intent data) {
        Bundle bundle = data.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d(TAG, "LIST...." + String.format("KEY-> %s VALUE-> %s ", key,
                        value.toString()));
            }
        }
    }


    private void Listen() {
        if (!ListenApService.IS_RUNNING)
            context.startService(new Intent(context, ListenApService.class));
    }

    private void CancelPreviousListen() {
        Log.i(TAG, "CancelPreviousListen: ");
    }
}
