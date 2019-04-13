package com.devapp.devmain.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;

import java.io.DataOutputStream;
import java.io.InputStream;

/**
 * Created by yyy on 25/2/16.
 */
public class TabAlertSwitchOffDelayService extends Service {
    Context context;
    String timeDelay;
    int time;
    private Handler handler;
    private int startId;

    public static boolean isConnected() {

        //if status true = 1 then its ON , if status false = 0 then its OFF
        boolean status = true;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "cat /sys/class/power_supply/ac/online"});
            DataOutputStream stdin = new DataOutputStream(p.getOutputStream());
            stdin.writeBytes("ls /data\n"); // \n executes the command
            InputStream stdout = p.getInputStream();
            byte[] buffer = new byte[1024];
            int read;
            String out = new String();
            while (true) {
                read = stdout.read(buffer);
                out += new String(buffer, 0, read);

                if (read < 1024) {
                    break;
                }
            }
            if (out.contains("0")) {
                status = false;
            } else if (out.contains("1")) {
                status = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public static void commadsForShutDown(Context mContext) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();

        try {
            amcuConfig.setBootCompleted(false);
            amcuConfig.setFirstConnection(true);


            Thread.sleep(100);
            // displayErrorToast(String.valueOf(saveSession.getFirstTimeConnection()), mContext);
            mContext.startActivity(new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN"));
        } catch (Exception e) {

            e.printStackTrace();
            try {
                //su-c "cat /sys/class/power_supply/ac/online" => 1(powersupply),0 (diconned)
                amcuConfig.setBootCompleted(false);
                Process proc = Runtime.getRuntime().exec(
                        new String[]{"su", "-c", "reboot -p"});
                proc.waitFor();

            } catch (Exception ex) {
                Log.i("Shutdown", "Could not shutdown", ex);
            }
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = this;
        this.startId = startId;
        try {
            timeDelay = intent.getStringExtra("delay_time");
        } catch (Exception e) {
            timeDelay = Util.getDelayTime(getApplicationContext());
            e.printStackTrace();
        }
        time = Integer.valueOf(timeDelay) * 60000;

        final Handler h = new Handler();
        final Handler h2 = new Handler();

        final int delay = 500;

        h.postDelayed(new Runnable() {
            public void run() {

                //  Toast.makeText(context, "Service Started", Toast.LENGTH_LONG).show();
                boolean powerStatus = isConnected();
                //   Toast.makeText(context, "Service Started timedelay::"+timeDelay, Toast.LENGTH_LONG).show();
                if (!powerStatus) {

                    try {
                        h2.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //Thread.sleep(time);
                                commadsForShutDown(context);
                            }
                        }, time);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                h.postDelayed(this, delay);
            }
        }, delay);


        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf(startId);
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

}