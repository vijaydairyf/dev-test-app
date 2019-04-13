package com.devapp.devmain.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import com.devapp.devmain.user.Util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Upendra on 5/20/2016.
 */
public class WriteRateChartLogs extends IntentService {

    Context mContext;

    public WriteRateChartLogs() {
        super("WriteRateLogs");
    }

    private void writeRatechartLogsSystem(String rateChartData) {
        ArrayList<String> commands = new ArrayList<>();

        commands.add("mount -o rw,remount,rw /system");
        commands.add("mkdir /system/smartAmcuLogs");
        commands.add("touch /system/smartAmcuLogs/ratechartLogs.txt");
        commands.add("echo " + rateChartData + " >>/system/smartAmcuLogs/ratechartLogs.txt");

        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            for (String tmpCmd : commands) {
                os.writeBytes(tmpCmd + "\n");
                os.flush();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            int i = process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String rateChartName = intent.getStringExtra("RATECHARTNAME");
        String usbOrCloud = intent.getStringExtra("USBORCLOUDORMANUAL");
        long modifiedTime = intent.getLongExtra("MODIFIEDTIME", 0);
        String validityFrom = intent.getStringExtra("VALIDITYFROM");
        String validityTo = intent.getStringExtra("VALIDITYTO");
        String addOrDelete = intent.getStringExtra("ADDORDELETE");
        String sShift = intent.getStringExtra("S_SHIFT");
        String eShift = intent.getStringExtra("E_SHIFT");

        String rateChartData = addOrDelete + "," + usbOrCloud + "," + modifiedTime + "," + rateChartName
                + "," + validityFrom + "," + validityTo + "," + sShift + "," + eShift + "\n";
        mContext = getApplicationContext();

        writeRatechartLogs(rateChartData);

        rateChartData = addOrDelete + "," + usbOrCloud + "," + modifiedTime + "," + rateChartName
                + "," + validityFrom + "," + validityTo + "," + sShift + "," + eShift + "\n";
        writeRatechartLogsSystem(rateChartData);


    }

    public void writeRatechartLogs(String rateChartData) {


        try {
            Util.generateNoteOnSD("RatechartLogs",
                    rateChartData, mContext, ".smartAmcuLogs");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
