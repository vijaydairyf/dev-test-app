package com.devapp.devmain.main;

/**
 * Created by u_pendra on 28/1/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.helper.OnStartTask;
import com.devapp.devmain.peripherals.services.DeviceManagerService;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DeviceListActivity extends Activity {

    private final long BUTTON_REFRESH_DELAY = 6000;
    private final String TAG = DeviceListActivity.class.getSimpleName();
    private final Context context = this;
    private final Handler btnHandler = new Handler();
    private final String[] strDevices = new String[5];
    private final Map<String, CheckedTextView> map = new HashMap<>();
    private boolean isButtonNextEnable;
    private String simPass;
    private AlertDialog alertDialog;
    private Button btnNext;
    private RelativeLayout progressLayout;
    private String strZeroDevice;
    private String refresh;
    private boolean isPort1;
    private boolean isPort2;
    private boolean isPort3;
    private boolean isPort4;
    private boolean isPort5;
    private int refreshCount = 0;
    private AmcuConfig amcuConfig;
    private CheckedTextView port1;
    private CheckedTextView port2;
    private CheckedTextView port3;
    private CheckedTextView port4;
    private CheckedTextView chPort5;
    private TextView tvNoDeviceFound;
    private AlertDialog.Builder alertBuilder;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action) ||
                    UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
//                Toast.makeText(DeviceListActivity.this,
//                        "Connected device detached", Toast.LENGTH_SHORT).show();
                unRegisterReciever();

                if (!amcuConfig.getShutDownAlertFlag())
                    restartApplication();
            } else if (SmartCCConstants.DEVICE_DISCONNECT.equals(action)) {
                Toast.makeText(DeviceListActivity.this, "One of the Wisens modules have got disconnected. " +
                        "Please restart the app.", Toast.LENGTH_LONG).show();
            }
        }
    };
    private RelativeLayout rlDeviceListContainer, rlSetupPogressContainer;
    private Handler handler = new Handler();
    private boolean isSimLockChecked = false;
    private final Runnable simLockRunnable = new Runnable() {

        @Override
        public void run() {

            if (!amcuConfig.getAttemptFortSimlock()) {
                //Trying to unlock the sim
                isSimLockChecked = Util.lockorUnlockTheSimPin(DeviceListActivity.this, simPass);
                if (isSimLockChecked) {
                    try {
                        //releasing the handler
                        //    Util.disableOrEnableNetwork();
                        handler.removeCallbacks(simLockRunnable);
                        handler = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        //rescheduling the handler
                        handler.postDelayed(this, 5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    };
    private TextView mProgressBarTitle, tvHeader;
    private ProgressBar mProgressBar;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SmartCCConstants.DEVICES_DISCOVERED)) {
                ArrayList<DeviceEntity> deviceList = (ArrayList<DeviceEntity>) intent.getSerializableExtra(SmartCCConstants.DEVICE_LIST);
                for (int i = 0; i < deviceList.size(); i++) {
                    Log.v("prober", "Device: " + deviceList.get(i).deviceName + " , IP: " + deviceList.get(i).ipAddress +
                            ", Port: " + deviceList.get(i).portAddress);
                    onAckReceived(deviceList.get(i).deviceName);
                }
                displayDiscoveredDevicesCount(deviceList.size());
                if (deviceList.size() == 4)
                    hideProgressBar();
            }
        }
    };
    private Runnable logInRunnable;
    private Handler loginHandler = new Handler();
    private Intent probingIntent;
    private long CURRENT_TIME;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_device_list);
        Util.downLoadFilePath = null;
        // ///////////////////////////////////
        progressLayout = (RelativeLayout) findViewById(R.id.progress_layout);
        progressLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        progressLayout.setVisibility(View.GONE);
        // ///////////////////////////////////
        amcuConfig = AmcuConfig.getInstance();
        //To make the screen awake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBarTitle = (TextView) findViewById(R.id.progressBarTitle);
        tvHeader = (TextView) findViewById(R.id.demoTitle);
        tvNoDeviceFound = (TextView) findViewById(R.id.tvNoDeviceFound);
        rlDeviceListContainer = (RelativeLayout) findViewById(R.id.rl_deviceListContainer);
        rlSetupPogressContainer = (RelativeLayout) findViewById(R.id.rl_setUpProgressContainer);
        Intent intent = getIntent();
        if (intent.hasExtra("showDevices") && intent.getBooleanExtra("showDevices", false)) {
            rlDeviceListContainer.setVisibility(View.VISIBLE);
            rlSetupPogressContainer.setVisibility(View.GONE);
        }
        tvHeader.setText(getResources().getString(R.string.usb_devices));
        strZeroDevice = getResources().getString(R.string.zero_device);
        refresh = getResources().getString(R.string.refreshing);
        btnNext = (Button) findViewById(R.id.btnNext);
        // hideProgressBar();
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextActivity();
            }
        });
        if (new SessionManager(DeviceListActivity.this).getSelectedLanguage()
                .equalsIgnoreCase("Kannada")) {
            setKannada();
        }
        Util.setDefaultRateChart(DeviceListActivity.this);
        simPass = amcuConfig.getSimUnlockPassword();
        boolean isRooted = Util.checkForRootedTab();
        if (simPass != null && isRooted && amcuConfig.getAllowSimlockPassword()) {
            try {
                openSimLock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        initializeEntity();
        setPortNames();

        LocalBroadcastManager.getInstance(context).registerReceiver(receiver,
                new IntentFilter(SmartCCConstants.DEVICES_DISCOVERED));


    }

    private void displayDiscoveredDevicesCount(int count) {
        mProgressBarTitle.setText(String.format("%s " + strZeroDevice, Integer.valueOf(count)));
    }

    private void setPortNames() {

        port1.setText("Milk Analyser");
        port2.setText("Weighing Scale");
        port3.setText("RDU");
        port4.setText("Printer");
//        chPort5.setVisibility(DeviceName.NO_CONNECTION);
        tvNoDeviceFound.setVisibility(View.GONE);
        //  mProgressBarTitle.setVisibility(View.GONE);

        setDeviceList();
        setDeviceName(port1, 0);
        setDeviceName(port2, 1);
        setDeviceName(port3, 2);
        setDeviceName(port4, 3);
        setDeviceName(chPort5, 4);
    }


    private void onAckReceived(String device) {

        for (Map.Entry entry : map.entrySet()) {
            if (entry.getKey().equals(device)) {
                CheckedTextView view = (CheckedTextView) entry.getValue();
                if (view != null)
                    view.setCheckMarkDrawable(R.drawable.check_box);
            }
        }

        if (device.equalsIgnoreCase(DeviceName.MA1)) {
            CheckedTextView view = map.get(DeviceName.MA1);
            if (view != null) {
                view.setCheckMarkDrawable(R.drawable.check_box);
            }

        }
    }

    private void goToNextActivity() {
        if (SmartCCConstants.appType.equals(SmartCCConstants.USB)) {
//            HotspotManager.disableHotspot(context);
//            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//            if (bluetoothAdapter.isEnabled()) {
//                bluetoothAdapter.disable();
//            }
        } else if (SmartCCConstants.appType.equals(SmartCCConstants.WIFI_UDP))
            unRegisterReciever();
        startActivity(new Intent(context, LoginActivity.class));
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startProbingService();
        CURRENT_TIME = System.currentTimeMillis();
        isButtonNextEnable = false;
        refreshCount = 0;
        setButton(false);
        uncheckAllBoxes();
        startButtonRunnable();
        new Thread(new Runnable() {
            @Override
            public void run() {

                loginHandler.postDelayed(logInRunnable, 1000);
            }
        }).start();

        logInRunnable = new Runnable() {
            @Override
            public void run() {

                startActivity(new Intent(DeviceListActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);
                finish();

            }
        };
    }

    private void startProbingService() {
        Log.v(TAG, "Starting probing service");
        probingIntent = new Intent(DeviceListActivity.this, DeviceManagerService.class);
        startService(probingIntent);
    }

    private void uncheckAllBoxes() {

        port1.setCheckMarkDrawable(R.drawable.uncheck_box);
        port2.setCheckMarkDrawable(R.drawable.uncheck_box);
        port3.setCheckMarkDrawable(R.drawable.uncheck_box);
        port4.setCheckMarkDrawable(R.drawable.uncheck_box);
        chPort5.setCheckMarkDrawable(R.drawable.uncheck_box);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loginHandler.removeCallbacks(logInRunnable);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopService(probingIntent);
        //loginHandler.removeCallbacks(logInRunnable);
    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBarTitle.setText(refresh);
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        DeviceName.setDeviceNames();
        showProgressBar();
        getDeviceID();
        SmartCCUtil smartCCUtil = new SmartCCUtil(DeviceListActivity.this);
        if (smartCCUtil.checkForSupervisor()) {
            smartCCUtil.updateSupervisorToTankerSupervisor();
        }
        try {
            Util.validateServerName(DeviceListActivity.this);
            IntentFilter filter = new IntentFilter();
            filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
            filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
            filter.addAction(SmartCCConstants.DEVICE_DISCONNECT);
            registerReceiver(mUsbReceiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        OnStartTask onStartTask = new OnStartTask(DeviceListActivity.this);
        onStartTask.disableRegister();


    }

    private void unRegisterReciever() {
        try {
            unregisterReceiver(mUsbReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setKannada() {

        tvHeader.setText(getResources().getString(R.string.usb_devicesk));
        strZeroDevice = getResources().getString(R.string.zero_devicek);
        btnNext.setText(getResources().getString(R.string.nextk));
        refresh = getResources().getString(R.string.refreshingk).toString();

    }

    private void RefreshCheckList() {
        setCheckBox(port1, isPort1);
        setCheckBox(port2, isPort2);
        setCheckBox(port3, isPort3);
        setCheckBox(port4, isPort4);
        setCheckBox(chPort5, isPort5);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(DeviceListActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(DeviceListActivity.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(DeviceListActivity.this, null);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER: {
                if (isButtonNextEnable) {
                    goToNextActivity();
                }

            }
            return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        SplashActivity.isClearActivity = true;
        Intent intent = new Intent(getApplicationContext(),
                SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

    private void restartApplication() {

        Intent i = new Intent(this, DeviceListActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i);
        if (alertBuilder != null) {
            alertBuilder = new AlertDialog.Builder(DeviceListActivity.this);
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        alertBuilder = new AlertDialog.Builder(DeviceListActivity.this);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alert_sendingmail, null);

        TextView tvMailHeader = (TextView) view.findViewById(R.id.tvheader);
        TextView tvAlertText = (TextView) view.findViewById(R.id.tvAlert);
        ImageView ivLogo = (ImageView) view.findViewById(R.id.ivsplash);
        ivLogo.setVisibility(View.GONE);
        RelativeLayout ProgressL = (RelativeLayout) view
                .findViewById(R.id.progress_layout);
        ProgressL.setVisibility(View.GONE);
        tvAlertText.setVisibility(View.VISIBLE);

        tvMailHeader.setText("Tab Restart Required!");
        // TODO
        String alertText = "Application has detected connection issues from one of the connected machines."
                + "\n" + "Please connect all machines with the gateway and then restart the tab";

        tvAlertText.setText(alertText);
        tvAlertText.setTextSize(23f);

        final Button btnResend = (Button) view.findViewById(R.id.btnResend);
        final Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        btnCancel.setVisibility(View.GONE);
        btnResend.setText("Re-boot");

        btnResend.requestFocus();

        btnResend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Util.restartTab(DeviceListActivity.this);
                alertDialog.dismiss();

            }
        });


        // create alert dialog
        alertDialog = alertBuilder.create();
        alertDialog.setView(view);
        // show it
        alertDialog.show();

        // To display the alert dialog in center

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();


        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 450;
        lp.height = 450;
        // lp.x = -170;
        // lp.y = 100;
        lp.gravity = Gravity.CENTER | Gravity.CENTER;
        alertDialog.getWindow().setAttributes(lp);

    }

    public void nextActivity() {
        long time = System.currentTimeMillis();
        if (time > (CURRENT_TIME + BUTTON_REFRESH_DELAY)) {
            isButtonNextEnable = true;
        }
    }

    private void setButton(boolean enable) {
        btnNext.setEnabled(enable);
        if (enable) {
            btnNext.setBackgroundColor(getResources().getColor(R.color.focused));
            btnNext.requestFocus();
        } else {
            btnNext.setBackgroundColor(getResources().getColor(R.color.btnblueNormal));
            unCheckList();
        }
    }

    private void openSimLock() {
        if (!amcuConfig.getAttemptFortSimlock()) {
            handler.postDelayed(simLockRunnable, 3000);
        }
    }

/*
    //Adding of support multiple MilkAnalyser feature

    public void setDeviceNameAndList() {

//        if(saveSession.getCheckboxMultipleMA()&&saveSession.getMultipleMA()&&
//                (saveSession.getFirstMaSerialPort()>0&&saveSession.getSecondMASerialPort()>0)
//                &&(saveSession.getFirstMaSerialPort()!=saveSession.getSecondMASerialPort()))
//        {
//            stringDeviceArray[saveSession.getFirstMaSerialPort()-1]="MA1";
//            stringDeviceArray[saveSession.getSecondMASerialPort()-1]="MA2";
//
//            stringDeviceArray[saveSession.getFirstMaSerialPort()-1]="MA1";
//            stringDeviceArray[saveSession.getSecondMASerialPort()-1]="MA2";
//
//            chPort1.setText(stringDeviceArray[0]);
//            chPort2.setText(stringDeviceArray[2]);
//            chPort3.setText(stringDeviceArray[1]);
//            chPort4.setText(stringDeviceArray[3]);
//            chPort5.setText(stringDeviceArray[4]);
//        }

    }*/

    private void initializeEntity() {
        port1 = (CheckedTextView) findViewById(R.id.port1);
        port2 = (CheckedTextView) findViewById(R.id.port2);
        port3 = (CheckedTextView) findViewById(R.id.port3);
        port4 = (CheckedTextView) findViewById(R.id.port4);
        chPort5 = (CheckedTextView) findViewById(R.id.port5);
    }

    private void setCheckBox(CheckedTextView chBox, boolean isEnable) {
        if (chBox.getText().toString().contains(DeviceName.NO_CONNECTION)) {
            chBox.setVisibility(View.GONE);
        } else if (chBox.getText().toString().contains("port")) {
            chBox.setVisibility(View.GONE);
        } else if (!isEnable) {
            chBox.setCheckMarkDrawable(R.drawable.uncheck_box);
        } else {
            chBox.setVisibility(View.VISIBLE);
            chBox.setCheckMarkDrawable(R.drawable.check_box);
        }
    }

    private void getDeviceID() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String device_id = tm.getDeviceId();
        if (device_id != null && device_id.length() > 5) {
            amcuConfig.setDeviceId(device_id);
        }
    }

    private void startButtonRunnable() {
        btnHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                isButtonNextEnable = true;
                setButton(isButtonNextEnable);
            }
        }, 3000);
    }


    private void unCheckList() {
        isPort1 = false;
        isPort2 = false;
        isPort3 = false;
        isPort4 = false;
        isPort5 = false;
        RefreshCheckList();
    }

    private void setDeviceList() {

        strDevices[0] = amcuConfig.getKeyDevicePort1();
        strDevices[1] = amcuConfig.getKeyDevicePort2();
        strDevices[2] = amcuConfig.getKeyDevicePort3();
        strDevices[3] = amcuConfig.getKeyDevicePort4();
        strDevices[4] = amcuConfig.getKeyDevicePort5();
    }

    private void setDeviceName(CheckedTextView checkedTextView, int i) {

        map.put(strDevices[i], checkedTextView);
        if (strDevices[i].equalsIgnoreCase(DeviceName.NO_CONNECTION)) {
            checkedTextView.setVisibility(View.GONE);
        } else {
            checkedTextView.setText(strDevices[i]);
            if (strDevices[i].equalsIgnoreCase(DeviceName.MA1)) {
                checkedTextView.setText(DeviceName.MA1);
            }

        }
    }


}


