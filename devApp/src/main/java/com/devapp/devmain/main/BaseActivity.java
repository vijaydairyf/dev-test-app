package com.devapp.devmain.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devApp.R;

public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F10:
                Util.restartApp(this);
                return true;
            case KeyEvent.KEYCODE_F11:
                Util.restartTab(this);
                return true;
            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(this, null);
                return true;
//            case KeyEvent.KEYCODE_NUMPAD_ENTER:
//                return false;
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                return true;
//            case KeyEvent.KEYCODE_ENTER:
//                Util.displayErrorToast(" enter pressed!", AmcuApplication.getAmcuContext());
//
//                return true;
//            default:
//                return super.onKeyUp(keyCode, event);
            default:
                return false;

        }
    }


    public void onBackPressed() {

        if (AmcuConfig.getInstance().getKeyEscapeEnableCollection()
                && !new SessionManager(this).getIsChillingCenter()) {
            DatabaseHandler.getDatabaseInstance().deleteFromDb();
            onFinish();
        }
    }


    public void onFinish() {

        startActivity(new Intent(this, FarmerScannerActivity.class));
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
        finish();
    }
}
