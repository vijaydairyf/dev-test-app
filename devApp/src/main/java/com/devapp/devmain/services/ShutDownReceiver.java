package com.devapp.devmain.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.devapp.devmain.server.AmcuConfig;

/**
 * Created by Upendra on 1/22/2016.
 */


public class ShutDownReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_SHUTDOWN)) {
            //  Util.displayErrorToast("ShutDownCompleted receiver",context);
            AmcuConfig.getInstance().setBootCompleted(false);
            AmcuConfig.getInstance().setFirstConnection(true);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}

