package com.devapp.devmain.services;


import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.widget.Toast;

import com.devapp.devmain.dbbackup.Backup;
import com.devapp.devmain.main.LauncherActivity;
import com.devapp.devmain.main.SplashActivity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.util.logger.Log;


public class BootReceiver extends BroadcastReceiver {
    final String TAG = "BootReceiver";

    private SharedPreferences sp;
    public static OnBootCompleteReceiver onBootCompleteReceiver;
    Handler handler = new Handler();


    private void EnableBTForSyncApp(Context context) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Bluetooth not supported on device", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!bluetoothAdapter.isEnabled())
            bluetoothAdapter.enable();
    }

    Runnable runnable;

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG, "Inside onReceive of BootReceiver");
        System.out.println("BootOnReceive");
        // startLauncherActivity(context);
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    sp = context.getSharedPreferences(AmcuConfig.PREF_NAME, 0);
                    Backup backup = new Backup();
                    boolean primaryExists = backup.checkPrimaryDbExists();
                    boolean secondaryExists = backup.checkSecondaryDbExists();
                    boolean isPrimarySane = backup.checkPrimaryDbIntegrity();
                    boolean isSecondarySane = backup.checkSecondaryDbIntegrity();
                    Log.d(TAG, "PrimaryExists " + primaryExists);
                    Log.d(TAG, "SecondaryExists " + secondaryExists);
                    Log.d(TAG, "PrimarySane " + isPrimarySane);
                    Log.d(TAG, "SecondarySane " + isSecondarySane);
                    System.out.println("Inside thread");
                    if (primaryExists && isPrimarySane) {
                        if (!(secondaryExists && isSecondarySane)) {
                            Log.d("BootReceiver", "Copying primary to secondary db");
                            backup.backUpDatabase(Backup.PRIMARY);
                        }
                    } else if (secondaryExists && isSecondarySane) {
                        if (DatabaseHandler.DATABASE_VERSION >= backup.getSecondaryDbVersion()) {
                            Log.d("BootReceiver", "Copying secondary to primary db");
                            backup.backUpDatabase(Backup.SECONDARY);
                        }
                    }
                    handler.postDelayed(runnable, 2000);
                }
            }).start();

            runnable = new Runnable() {
                @Override
                public void run() {
                    todoAfterBootComplete(context);
                }
            };

        }
        EnableBTForSyncApp(context);

    }

    private void startSplashActivity(Context context) {
        try {
            Intent splashIntent = new Intent(context, SplashActivity.class);
            splashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(splashIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void todoAfterBootComplete(Context context) {

        System.out.println("Boot task done");
        //Util.displayErrorToast("Boot Completed",context);
        // AmcuConfig amcuConfig = AmcuConfig.getInstance();
        //   amcuConfig.setBootCompleted(true);
        sp.edit().putBoolean(AmcuConfig.KEY_SET_BOOT_COMPLETED, true).apply();
        sp.edit().putBoolean(AmcuConfig.KEY_ATTEMPT_FOR_SIM_UNLOCK, false).apply();
        sp.edit().putBoolean(AmcuConfig.KEY_IS_FIRST_CONNECTION, true).apply();
        sp.edit().putBoolean(AmcuConfig.KEY_SENT_UPDATED_RECORDS, false).apply();
//        amcuConfig.setAttemptForSimlock(false);
//        //Usded for Networkreceiver
//        amcuConfig.setFirstConnection(true); // As per old image network comes before boot
//        //Setting updated sent records
//        amcuConfig.setUpdatedRecordsStatus(false);

        // sendLocalBroadcast(context);
        //TODO
//        if (sp.getString() != null) {
//            amcuConfig.setServer(amcuConfig.getTemporaryServer());
//            amcuConfig.setTemporaryServer(null);
//        }

      /*  if (onBootCompleteReceiver != null) {
            onBootCompleteReceiver.onBootComplete();
        } else {
            Util.displayErrorToast("On boot receiver not found!", context.getApplicationContext());
        }*/


        startSplashActivity(context);

        //TODO
        // Util.validateServerName(context);
    }

    private void startLauncherActivity(Context context) {
        try {
            Intent launcherIntent = new Intent(context, LauncherActivity.class);
            launcherIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launcherIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface OnBootCompleteReceiver {

        void onBootComplete();
    }

}
