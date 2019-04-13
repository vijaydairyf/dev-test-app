package com.devapp.devmain.user;

import android.app.Activity;
import android.os.Bundle;

import com.devapp.devmain.server.AmcuConfig;

/**
 * Created by Upendra on 9/30/2015.
 */
public class SimLockActivity extends Activity {

    AmcuConfig amcuConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amcuConfig = AmcuConfig.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        String newPin = amcuConfig.getTempSimPin();
        if (newPin != null) {
            String oldPin = amcuConfig.getSimUnlockPassword();
            amcuConfig.setSimlockPassword(newPin);
            amcuConfig.setTempSimPin(null);
            String pass1 = amcuConfig.getSimUnlockPassword();
            Util.changeSimLockPassword(oldPin, newPin, SimLockActivity.this);
        }

    }
}
