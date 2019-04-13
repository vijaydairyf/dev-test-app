package com.devapp.devmain.services;

import android.app.IntentService;
import android.content.Intent;

import com.devapp.devmain.server.DatabaseHandler;

/**
 * Created by yyy on 27/4/16.
 */
public class DeleteOldRecordService extends IntentService {

    private static final int DAYS_PRIOR_TO_CURRENT = 120;


    public DeleteOldRecordService() {
        super("Delete");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();

        try {
            db.deleteOlderRecords(DAYS_PRIOR_TO_CURRENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //DB close removed;
    }
}