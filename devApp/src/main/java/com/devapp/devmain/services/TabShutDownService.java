package com.devapp.devmain.services;

import android.app.IntentService;
import android.content.Intent;

import com.devapp.devmain.user.ShutDownAlertActivity;

/**
 * Created by Upendra on 7/21/2015.
 */
public class TabShutDownService extends IntentService {

    private static final String TAB_SHUT_DOWN_SERVICE = "TabShutDownService";

    public TabShutDownService() {
        super(TAB_SHUT_DOWN_SERVICE);
    }

    public static String getTaskName() {
        return TAB_SHUT_DOWN_SERVICE;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent intentSubmit = new Intent(getApplicationContext(), ShutDownAlertActivity.class);
        intentSubmit.putExtra("isShutDown", true);
        intentSubmit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intentSubmit);

    }
}
