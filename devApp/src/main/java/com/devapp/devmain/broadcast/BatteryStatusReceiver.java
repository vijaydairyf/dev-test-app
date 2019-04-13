package com.devapp.devmain.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

import com.devapp.devmain.helper.LowBatteryActivity;

/**
 * Created by u_pendra on 6/5/17.
 */


public class BatteryStatusReceiver extends BroadcastReceiver {

    private BroadcastReceiver mBatInfoReceiver;

    @Override
    public void onReceive(final Context context, Intent intent) {

        int batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int batteryStatus = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);


//        float batteryPercentage = batteryLevel / (float) batteryScale;
////        Util.displayErrorToast("Battery status: " + batteryStatus, context.getApplicationContext());
////        Util.displayErrorToast("Battery level " + batteryLevel, context.getApplicationContext());
////        Util.displayErrorToast(" Battery scale " + batteryScale, context.getApplicationContext());
////        Util.displayErrorToast("Battery percentage  " + batteryPercentage, context.getApplicationContext());


        Intent mStartActivity = new Intent(context.getApplicationContext(), LowBatteryActivity.class);

        mStartActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mStartActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(mStartActivity);
    }

}
