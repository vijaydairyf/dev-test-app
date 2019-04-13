package com.devapp.devmain.json;

import android.app.IntentService;
import android.content.Intent;

public class PostDataService extends IntentService {

    public PostDataService() {
        super("Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        postData();
    }

    public void postData() {

        // String msg;
        //
        // JSONParser.postData(SplashActivity.SERVER
        // + "/contrak/bmc/postedvolume/", msg);

    }


}
