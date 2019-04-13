package com.devapp.devmain.peripherals.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.io.IOException;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;

/**
 * Created by xx on 26/2/18.
 */

public class PingIntentService extends IntentService {
    Handler handler = new Handler();

    public PingIntentService() {
        super("Starting Ping Service");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        while (SmartCCConstants.shouldContinue) {
            ping(SmartCCConstants.maIp);
            ping(SmartCCConstants.wsIp);
            ping(SmartCCConstants.rduIp);
            ping(SmartCCConstants.printerIp);
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
/*
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (SmartCCConstants.shouldContinue) {
                    ping(SmartCCConstants.maIp);
                    ping(SmartCCConstants.wsIp);
                    ping(SmartCCConstants.rduIp);
                    ping(SmartCCConstants.printerIp);
                    handler.postDelayed(this, 10000);
                }
            }
        });*/
    }

    private boolean ping(String ip) {
        Runtime runtime = Runtime.getRuntime();
        try {
//            InetAddress inetAddress = InetAddress.getByName(ip);
            /*long ipAsLong = 0;
            for (String byteString : ip.split("\\."))
            {
                ipAsLong = (ipAsLong << 8) | Integer.parseInt(byteString);
            }
            InetAddress inetAddress2 = InetAddress.getByAddress(ip);*/
//            Log.v(PROBER, "Inet Address for " + ip + " is " + inetAddress.toString());
//            boolean pingResult = inetAddress.isReachable(2000);
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + ip);
            int pingResult = mIpAddrProcess.waitFor();
            Log.v(PROBER, " PING VALUE for " + ip + " : " + pingResult);
            if (pingResult == 0)
                return true;
            else
                return false;
//            return pingResult;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(" Exception:" + e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}
