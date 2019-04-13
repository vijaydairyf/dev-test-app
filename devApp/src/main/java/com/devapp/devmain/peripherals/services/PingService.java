package com.devapp.devmain.peripherals.services;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.peripherals.factories.DeviceFactory;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.devmain.user.WifiUtility;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * Created by xx on 4/10/17.
 */

public class PingService extends Service {

    private String TAG = PingService.class.getSimpleName();
    //    private Handler handler;
    private Runnable pingRunnable;
    private boolean maPing, wsPing,
            rduPing, printerPing;
    private ArrayList<DeviceEntity> deviceEntities = SmartCCConstants.devicesList;
    private ArrayList<DeviceEntity> tempEntityList = new ArrayList<>();
    private State mState = State.STOPPED;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(TAG, "Service called");
        if (mState != State.RUNNING) {
//        handler = new Handler(Looper.getMainLooper());
            final WifiUtility wifiUtility = new WifiUtility();
            tempEntityList.addAll(deviceEntities);
            pingRunnable = new Runnable() {
                @Override
                public void run() {
                    mState = State.RUNNING;
                    while (true && mState == State.RUNNING) {
                        for (DeviceEntity deviceEntity : tempEntityList) {
                            if ((deviceEntity.deviceType == SmartCCConstants.WIFI_UDP || deviceEntity.deviceType == SmartCCConstants.WIFI_TCP)) {
                                if (deviceEntity.deviceName == DeviceName.MA1) {
                                    maPing = ping(SmartCCConstants.maIp);
                                    Log.v(TAG, "MA ping value: " + maPing);
                                    wifiUtility.checkWisensConnectivity(PingService.this, DeviceName.MA1, maPing);
                                }
                                if (deviceEntity.deviceName == DeviceName.WS) {
                                    wsPing = ping(SmartCCConstants.wsIp);
                                    Log.v(TAG, "WS ping value: " + wsPing);
                                    wifiUtility.checkWisensConnectivity(PingService.this, DeviceName.WS, wsPing);
                                }
                                if (deviceEntity.deviceName == DeviceName.RDU) {
                                    rduPing = ping(SmartCCConstants.rduIp);
                                    Log.v(TAG, "RDU ping value: " + rduPing);
                                    wifiUtility.checkWisensConnectivity(PingService.this, DeviceName.RDU, rduPing);
                                }
                                if (deviceEntity.deviceName == DeviceName.PRINTER) {
                                    printerPing = ping(SmartCCConstants.printerIp);
                                    Log.v(TAG, "PRINTER ping value: " + printerPing);
                                    wifiUtility.checkWisensConnectivity(PingService.this, DeviceName.PRINTER, printerPing);
                                }
                            }
                        }
                        Intent i = new Intent(SmartCCConstants.PING_STATUS);
                        i.putExtra(SmartCCConstants.MA_PING, maPing);
                        i.putExtra(SmartCCConstants.WS_PING, wsPing);
                        i.putExtra(SmartCCConstants.RDU_PING, rduPing);
                        i.putExtra(SmartCCConstants.PRINTER_PING, printerPing);
                        LocalBroadcastManager.getInstance(PingService.this).sendBroadcast(i);
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                    handler.postDelayed(this, 10000);
                    }
                }
            };
            new Thread(pingRunnable).start();
//        handler.post(pingRunnable);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mState = State.STOPPED;
//        handler.removeCallbacks(pingRunnable);
    }

    public boolean ping(String ip) {
        Runtime runtime = Runtime.getRuntime();
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
//            boolean pingResult = inetAddress.isReachable(2000);
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + ip);
            int pingResult = mIpAddrProcess.waitFor();
            return pingResult == 0;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(" Exception:" + e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.v(TAG, "APPlication is killed");
        mState = State.STOPPED;
        //closes all open socket connections when app is killed
        DeviceFactory.shutdownDevices("");

    }


    private enum State {
        RUNNING, STOPPED
    }
}
