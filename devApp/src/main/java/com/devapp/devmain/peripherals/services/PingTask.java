package com.devapp.devmain.peripherals.services;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.io.IOException;
import java.net.InetAddress;

import static android.os.Looper.getMainLooper;
import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;

/**
 * Created by xx on 26/2/18.
 */

public class PingTask extends AsyncTask {
    @Override
    protected Object doInBackground(Object[] params) {
        final Handler handler = new Handler(getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                ping(SmartCCConstants.maIp);
                ping(SmartCCConstants.wsIp);
                ping(SmartCCConstants.rduIp);
                ping(SmartCCConstants.printerIp);
                handler.postDelayed(this, 10000);
            }
        });

/*
                    maPing.add(ping(SmartCCConstants.maIp));
                    wsPing.add(ping(SmartCCConstants.wsIp));
                    rduPing.add(ping(SmartCCConstants.rduIp));
                    printerPing.add(ping(SmartCCConstants.printerIp));
                    count++;
                    if (count == 5) {
                        for (int i = 0; i < count; i++) {
                            if (!maPing.get(i))
                                maCt++;
                            if (!wsPing.get(i))
                                wsCt++;
                            if (!rduPing.get(i))
                                rduCt++;
                            if (!printerPing.get(i))
                                printerCt++;
                        }
                        if (maCt == 5 || wsCt == 5 || rduCt == 5 || printerCt == 5)
                            (PingService.this).sendBroadcast(i);
                        reInitialize();
                    }*/
        return null;
    }


    private boolean ping(String ip) {
        Runtime runtime = Runtime.getRuntime();
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
//            boolean pingResult = inetAddress.isReachable(2000);
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + ip);
            int pingResult = mIpAddrProcess.waitFor();
            Log.v(PROBER, " PING VALUE for " + ip + " : " + pingResult);
            if (pingResult == 0)
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(" Exception:" + e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
