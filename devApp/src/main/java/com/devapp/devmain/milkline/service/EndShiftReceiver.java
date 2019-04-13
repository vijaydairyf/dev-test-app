package com.devapp.devmain.milkline.service;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.util.Calendar;

/**
 * Created by u_pendra on 28/12/16.
 */

public class EndShiftReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        SmartCCUtil smartCCUtil = new SmartCCUtil(context);
        String shift = Util.getCurrentShift();
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        Calendar calendar = Calendar.getInstance();

        if (amcuConfig.getEndShiftSuccess()) {
            unregisterBroadCast(context);
        } else if (!amcuConfig.getEndShiftSuccess()) {
            smartCCUtil.endShiftFunction(context);
        }

    }


    public void unregisterBroadCast(Context context) {
        // Util.displayErrorToast("Unresigter receiver",context);

        ComponentName receiver = new ComponentName(context, EndShiftReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }


}
