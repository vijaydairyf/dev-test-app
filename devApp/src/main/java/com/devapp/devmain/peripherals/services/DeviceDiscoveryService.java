package com.devapp.devmain.peripherals.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.devapp.devmain.peripherals.network.FinishScanListener;
import com.devapp.devmain.peripherals.network.ScanResult;
import com.devapp.devmain.peripherals.network.WifiApManager;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.util.ArrayList;


/**
 * Created by xx on 5/5/17.
 */

public class DeviceDiscoveryService extends Service {
    WifiApManager wifiApManager;
    Handler handler;
    Runnable myRunnable;
    String TAG = DeviceDiscoveryService.class.getSimpleName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        wifiApManager = new WifiApManager(this);
        handler = new Handler();
        Log.v("udp", "Inside discovery service:");

        wifiApManager.getClientList(false, 5000, new FinishScanListener() {
            @Override
            public void onFinishScan(ArrayList<ScanResult> clients) {
                SmartCCConstants.clients = clients;
                Log.v("udp", "scan completed");
                if (clients != null && clients.size() > 0)
                    Log.v("udp", "Devices scanned: " + clients.size() + " : " + clients.get(0).getIpAddr() +
                            clients.get(0).isReachable());
                sendBroadcastMsg("", SmartCCConstants.DEVICE_CONNECT);
            }
        });
//        handler.postDelayed(myRunnable, 1000);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(myRunnable);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void sendBroadcastMsg(String msg, String action) {

        Intent i = new Intent(action);
        i.putExtra("data", msg);
        LocalBroadcastManager.getInstance(DeviceDiscoveryService.this).sendBroadcast(i);
    }
}