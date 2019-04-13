package com.devapp.devmain.milkline.service;

import android.app.IntentService;
import android.content.Intent;

import com.devapp.devmain.main.DeviceListActivity;
import com.devapp.devmain.user.Util;


/**
 * Created by u_pendra on 28/12/16.
 */

public class EndShiftService extends IntentService {

    static String END_SHIFT_SERVICE = "END_SHIFT_SERVICE";


    public EndShiftService() {
        super(END_SHIFT_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Util.displayErrorToast("Service called", getApplicationContext());
        getApplicationContext().startActivity(new Intent(getApplicationContext(), DeviceListActivity.class));

    }
}
