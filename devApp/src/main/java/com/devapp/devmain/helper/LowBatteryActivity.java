package com.devapp.devmain.helper;

import android.app.Activity;
import android.os.Bundle;

import com.devapp.smartcc.entityandconstants.SmartCCUtil;

/**
 * Created by u_pendra on 6/5/17.
 */

public class LowBatteryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SmartCCUtil smartCCUtil = new SmartCCUtil(LowBatteryActivity.this);
        SmartCCUtil.FORCE_SHUTDOWN = true;
        smartCCUtil.lowBatteryAlert();
    }
}
