package com.devapp.devmain.services;

import android.app.AlertDialog;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.tabs.AlertActivity;

public class LowBatteryService extends IntentService {

    SessionManager session;
    AlertDialog alertDialog;
    Context ctx;

    public LowBatteryService() {

        super("LowBatteryService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        session = new SessionManager(LowBatteryService.this);
        if (session.getBatstatus() < 15) {

            ShowAlert();

        } else {
            Toast.makeText(getApplicationContext(),
                    "Battery is " + session.getBatstatus() + " % remaining.",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void ShowAlert() {

        Intent intent = new Intent(getApplicationContext(), AlertActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

    }
}
