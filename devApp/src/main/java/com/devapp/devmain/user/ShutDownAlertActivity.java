package com.devapp.devmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;

import com.devapp.devmain.httptasks.BackgroundTasksMgr;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.services.TabAlertSwitchOffDelayService;

public class ShutDownAlertActivity extends Activity {

    boolean isShutDown;
    //As per china new tab

    boolean isCharging;
    AlertDialog alertDialog;
    String comingFrom;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = new Intent(this, TabAlertSwitchOffDelayService.class);
        intent.putExtra("delay_time", Util.getDelayTime(this));
        startService(intent);
        try {
            isShutDown = getIntent().getBooleanExtra("isShutDown", false);
        } catch (Exception e) {
            Util.commadsForShutDown(getApplicationContext());
        }

    }

    @Override
    protected void onStart() {

        isCharging = Util.isConnected();
        if (!isCharging) {
            alertWithoutCharging();
        } else if (isCharging) {
            alertForShutDown();
        }
        super.onStart();
    }


    public void alertWithoutCharging() {
        //   new SaveSession(ShutDownAlertActivity.this).setShutDownAlertFlag(true);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ShutDownAlertActivity.this);
        // set title
        String delay = Util.getStringFromTime(AmcuConfig.getInstance().getShutDownDelay());
        alertDialogBuilder.setTitle("Shutdown alert!");
        // set dialog message

        if (isShutDown) {
            alertDialogBuilder
                    .setMessage("Tab is shutting down....")
                    .setCancelable(false);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Util.commadsForShutDown(ShutDownAlertActivity.this);
            }
        } else {

            alertDialogBuilder
                    .setMessage("Please Turn Off Power Supply: \n - Press OK for shutdown after " + delay + " Minute(s).\n - Press Switch Off for immediate shutdown.")
                    .setCancelable(false);

            alertDialogBuilder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  stopService(new Intent(getApplicationContext(), TabAlertSwitchOffDelayService.class));
                            Util.isShutDown = true;
                            AmcuConfig.getInstance().setShutDownAlertFlag(false);
                            BackgroundTasksMgr bgTaskMgr = BackgroundTasksMgr.getInstance(ShutDownAlertActivity.this);
                            dialog.cancel();
                            finish();

                        }
                    });
            alertDialogBuilder.setNegativeButton("Switch Off",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AmcuConfig.getInstance().setShutDownAlertFlag(false);
                            Util.commadsForShutDown(ShutDownAlertActivity.this);
                            dialog.cancel();
                            finish();
                        }
                    });
        }


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void alertForShutDown() {

        isCharging = Util.isConnected();
        AmcuConfig.getInstance().setShutDownAlertFlag(true);

        // startService(new Intent(ShutDownAlertActivity.this, TabAlertSwitchOffDelayService.class));
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ShutDownAlertActivity.this);
        // set title
        String delay = Util.getStringFromTime(AmcuConfig.getInstance().getShutDownDelay());
        alertDialogBuilder.setTitle("Shutdown alert!");
        // set dialog message

        if (isShutDown) {
            alertDialogBuilder
                    .setMessage("Tab is shutting down....")
                    .setCancelable(false);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                Util.commadsForShutDown(ShutDownAlertActivity.this);
            }
            // create alert dialog
            alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        } else {

            if (!isCharging) {
                if (null != comingFrom && comingFrom.equalsIgnoreCase("OK")) {
                    /*if(null != intent)
                    stopService(intent);*/
                    //testing


                    //testing end
                    Util.isShutDown = true;
                    // new SaveSession(ShutDownAlertActivity.this).setShutDownAlertFlag(false);
                    BackgroundTasksMgr bgTaskMgr = BackgroundTasksMgr.getInstance(ShutDownAlertActivity.this);

                    finish();
                } else if (null != comingFrom && comingFrom.equalsIgnoreCase("CAN")) {
                    // new SaveSession(ShutDownAlertActivity.this).setShutDownAlertFlag(false);
                    Util.commadsForShutDown(ShutDownAlertActivity.this);
                    finish();
                }
            } else {

                // display when it is in charging mode
                alertDialogBuilder
                        .setMessage("Please Turn Off Power Supply: \n - Press OK for shutdown after " + delay + " Minute(s).\n - Press Switch Off for immediate shutdown.")
                        .setCancelable(false);

                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                comingFrom = "OK";
                                if (!isCharging) {
                                    Util.isShutDown = true;
                                   /* intent = new Intent(ShutDownAlertActivity.this, TabAlertSwitchOffDelayService.class);
                                    intent.putExtra("delay_time",Util.getDelayTime(ShutDownAlertActivity.this));
                                    startService(intent);*/
                                    AmcuConfig.getInstance().setShutDownAlertFlag(false);
                                    BackgroundTasksMgr bgTaskMgr = BackgroundTasksMgr.getInstance(ShutDownAlertActivity.this);
                                    dialog.cancel();
                                    finish();
                                } else {

                                    alertForShutDown();
                                }

                            }
                        });
                alertDialogBuilder.setNegativeButton("Switch Off",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                comingFrom = "CAN";
                                if (!isCharging) {
                                    AmcuConfig.getInstance().setShutDownAlertFlag(false);
                                    Util.commadsForShutDown(ShutDownAlertActivity.this);
                                    dialog.cancel();
                                    finish();
                                } else {

                                    alertForShutDown();
                                }
                            }
                        });


                alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        }


    }

    public boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB;
    }
}