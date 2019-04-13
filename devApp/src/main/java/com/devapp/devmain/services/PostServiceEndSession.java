package com.devapp.devmain.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.devapp.devmain.entity.DeviceId;
import com.devapp.devmain.json.JSONParser;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.ServerResponse;
import com.devapp.devmain.user.Util;

public class PostServiceEndSession extends IntentService {

    ServerResponse response;
    AmcuConfig amcuConfig;
    DeviceId deviceId;
    String server;

    public PostServiceEndSession() {
        super("PostService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String url = null;
        amcuConfig = AmcuConfig.getInstance();
        server = amcuConfig.getURLHeader() + amcuConfig.getServer();
        getDeviceID();

        if (amcuConfig.getPostData() == 1) {
            url = server + "/amcu/collectionentries";
            response = JSONParser.postData(url, Util.poEndShift, 1);
        } else if (amcuConfig.getPostData() == 2) {
            url = server + "/smartamcu/farmercollectionentry";
            response = JSONParser.postData(url, Util.poPerFarmer, 2);
        }
    }

    public void getDeviceID() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String device_id = tm.getDeviceId();
        deviceId = new DeviceId();
        deviceId.imeiNumber = device_id;

    }

}
