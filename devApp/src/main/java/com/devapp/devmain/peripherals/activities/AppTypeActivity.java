package com.devapp.devmain.peripherals.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devApp.R;

public class AppTypeActivity extends AppCompatActivity implements View.OnClickListener {

    static final int COUNTDOWN = 99;
    RadioGroup radioGroup;
    RadioButton rbWired, rbWireless;
    Context context = this;
    Button nextButton;
    TextView defaultVal;
    int count = 10;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case COUNTDOWN:
                    if (count == 0) {
                        setButton(true);
                        goToNextActivity();
                        break;
                    } else if (count > 0) {
                        defaultVal.setText("Selected option will be finalized in " + count + " seconds.");
                        count--;
                        handler.sendEmptyMessageDelayed(COUNTDOWN, 1000);
                        break;
                    }
                default:
                    super.handleMessage(msg);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_type);
        initializeComponents();
    }

    private void initializeComponents() {
        radioGroup = (RadioGroup) findViewById(R.id.rg);
        rbWired = (RadioButton) findViewById(R.id.rb_wired);
        rbWireless = (RadioButton) findViewById(R.id.rb_wireless);
        defaultVal = (TextView) findViewById(R.id.default_val);
        nextButton = (Button) findViewById(R.id.next_btn);
        nextButton.setOnClickListener(this);
        setDefaultSelection();
        /*radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rb_wired:
                        SmartCCConstants.wirelessMode = false;
                        break;
                    case R.id.rb_wireless:
                        SmartCCConstants.wirelessMode = true;
                        break;
                    default:
                        SmartCCConstants.wirelessMode = true;
                }
            }
        });*/
//        handler.sendEmptyMessage(COUNTDOWN);
    }

    private void setDefaultSelection() {
        if (SmartCCConstants.getApptype(context).equals(SmartCCConstants.USB)) {
            rbWired.setChecked(true);
        } else {
            rbWireless.setChecked(true);
        }


    }

    public void setButton(boolean enable) {

        if (!enable) {
            nextButton.setBackgroundColor(getResources().getColor(R.color.btnblueNormal));

        } else {
            int back = getResources().getColor(R.color.focused);
            nextButton.setBackgroundColor(back);
            nextButton.setEnabled(enable);
            nextButton.requestFocus();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.next_btn) {
            saveAppType();
        }
    }

    private void goToNextActivity() {
//        handler.removeMessages(COUNTDOWN);
//        saveAppType();
//        Intent intent = new Intent(context, DeviceListActivity.class);
//        intent.putExtra("fromSplashActivity", true);
//        DeviceListActivity.onCreate = 0;
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_left,
//                R.anim.slide_out_left);
    }

    private void saveAppType() {
        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_wired) {
            SmartCCConstants.setApptype(SmartCCConstants.USB, context);
            Toast.makeText(context, "App mode changed to USB", Toast.LENGTH_SHORT).show();
//            HotspotManager.disableHotspot(this);
        } else {
            SmartCCConstants.setApptype(SmartCCConstants.WIFI, context);
            Toast.makeText(context, "App mode changed to WIFI", Toast.LENGTH_SHORT).show();
//            HotspotManager.setHotspotName("smartUdp", this);
        }
        Util.restartApp(context);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                saveAppType();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }
}
