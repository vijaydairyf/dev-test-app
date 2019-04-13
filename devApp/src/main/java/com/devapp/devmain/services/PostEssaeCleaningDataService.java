package com.devapp.devmain.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.devapp.devmain.httptasks.AuthenticationParameters;
import com.devapp.devmain.httptasks.CommunicationService;
import com.devapp.devmain.httptasks.HttpResponse;
import com.devapp.devmain.httptasks.IncompatibleProtocolException;
import com.devapp.devmain.httptasks.SSLContextCreationException;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by xx on 14/3/18.
 */

public class PostEssaeCleaningDataService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private static final String TAG = "ESSAE_HISTORY";
    private static final String POST_CLEANING_URI = "/amcu/devices/logs/";
    private AmcuConfig amcuConfig;
    private Handler mHandler;

    public PostEssaeCleaningDataService() {
        super("post essae records");
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String jsonData = intent.getStringExtra("data");
        Log.v(TAG, "JSON Data in service:" + jsonData.length() + "," + jsonData);

        amcuConfig = AmcuConfig.getInstance();
        AuthenticationParameters authParams =
                new AuthenticationParameters(null, amcuConfig.getDeviceID(),
                        amcuConfig.getDevicePassword());
        CommunicationService cs = null;
        try {
            cs = new CommunicationService(amcuConfig.getURLHeader() + amcuConfig.getServer(),
                    authParams);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (SSLContextCreationException e) {
            Log.d(TAG, e.getMessage());
        }
        try {
            HttpResponse response = cs.doPost(POST_CLEANING_URI,
                    jsonData, true);
            if (response.getResponseCode() == HttpURLConnection.HTTP_CREATED
                    || response.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.v(TAG, "Posting data success");
//                SmartCCConstants.unsentCleaningData = "";
                amcuConfig.setUnsentCleaningData("");
                DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseInstance();
                final int recordsUpdated = databaseHandler.updateCleaningDataIntoEssaeTable();
                Log.v(TAG, recordsUpdated + " MA cleaning records uploaded");
               /* mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast t = Toast.makeText(getApplicationContext(), recordsUpdated + " MA cleaning records uploaded", Toast.LENGTH_LONG);
                        t.setGravity(Gravity.CENTER, 0, 0);
                        t.show();
                    }
                });*/
            } else
                Log.v(TAG, "Posting data failed");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IncompatibleProtocolException e) {
            e.printStackTrace();
        }
    }
}
