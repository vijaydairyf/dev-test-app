package com.devapp.devmain.services;

import android.app.IntentService;
import android.content.Intent;

import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.user.Util;

import java.util.ArrayList;

public class EnterRCEntity extends IntentService {

    ArrayList<RateChartEntity> allRcEnt;

    public EnterRCEntity() {
        super("EnterRcEnt");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        allRcEnt = Util.ReadRateChartEntity(getApplicationContext(), 0);

        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        try {
            dbh.insertValidationTableR(allRcEnt, DatabaseHandler.isPrimary);
            dbh.insertValidationTableR(allRcEnt, DatabaseHandler.isSecondary);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
