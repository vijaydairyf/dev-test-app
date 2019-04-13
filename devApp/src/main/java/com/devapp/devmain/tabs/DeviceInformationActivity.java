package com.devapp.devmain.tabs;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ftdi.j2xx.D2xxManager;
import com.ftdi.j2xx.D2xxManager.D2xxException;
import com.ftdi.j2xx.FT_Device;
import com.devApp.R;


public class DeviceInformationActivity extends Activity {
    static Context DeviceInformationContext;
    D2xxManager ftdid2xx;
    FT_Device ftDevice;
    int devCount = 0;
    TextView NumberDeviceValue;
    TextView DeviceName;
    TextView DeviceSerialNo;
    TextView DeviceDescription;
    TextView DeviceID;
    TextView DeviceLocation;
    TextView Error_Information;
    TextView Library;
    Button btnRefreshDevice;

    FT_Device ftDev = null;
    int DevCount = -1;
    int currentIndex = -1;
    /**
     * Hot plug for plug out solution
     */

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String TAG = "FragL";
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                Log.i(TAG, "DETACHED...");

                notifyUSBDeviceDetach();

            }
        }
    };
    int openIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        try {
            ftdid2xx = D2xxManager.getInstance(this);
        } catch (D2xxException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_login);

        DeviceInformationContext = DeviceInformationActivity.this;

        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.setPriority(500);
        this.registerReceiver(mUsbReceiver, filter);

    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            GetDeviceInformation();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void GetDeviceInformation() throws InterruptedException {

        int devCount = 0;

        devCount = ftdid2xx
                .createDeviceInfoList(DeviceInformationActivity.this);

        Log.i("FtdiModeControl",
                "Device number = " + Integer.toString(devCount));
        if (devCount > 0) {
            D2xxManager.FtDeviceInfoListNode[] deviceList = new D2xxManager.FtDeviceInfoListNode[devCount];
            ftdid2xx.getDeviceInfoList(devCount, deviceList);

            ftDevice = ftdid2xx.openByIndex(DeviceInformationContext, 0);


            // deviceList[0] = ftdid2xx.getDeviceInfoListDetail(0);

            Toast.makeText(
                    DeviceInformationContext,
                    "Number of Devices: " + Integer.toString(devCount)
                            + "Device Serial Number: "
                            + deviceList[0].serialNumber, Toast.LENGTH_SHORT)
                    .show();

        } else {

            Toast.makeText(DeviceInformationContext, "Number of devices: 0",
                    Toast.LENGTH_SHORT).show();

        }

    }

    public void RefrestDeviceInformation(View view) {
        try {
            GetDeviceInformation();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            String s = e.getMessage();
            if (s != null) {
                Error_Information.setText(s);
            }
            e.printStackTrace();
        }
    }

    /**
     * Hot plug for plug in solution This is workaround before android 4.2 .
     * Because BroadcastReceiver can not receive ACTION_USB_DEVICE_ATTACHED
     * broadcast
     */
    @Override
    public void onResume() {
        super.onResume();

        Intent intent = this.getIntent();
        Log.d("Paris ::", "intent: " + intent);
        String action = intent.getAction();

        String hotplug = "android.intent.action.MAIN";
        if (hotplug.equals(action)) {

            try {
                GetDeviceInformation();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                String s = e.getMessage();
                if (s != null) {
                    Error_Information.setText(s);
                }
                e.printStackTrace();
            }
        }
    }

    public void notifyUSBDeviceDetach() {
        disconnectFunction();
    }

    public void disconnectFunction() {
        DevCount = -1;
        currentIndex = -1;

        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (ftDev != null) {
            synchronized (ftDev) {
                if (true == ftDev.isOpen()) {
                    ftDev.close();
                }
            }
        }
    }

}
