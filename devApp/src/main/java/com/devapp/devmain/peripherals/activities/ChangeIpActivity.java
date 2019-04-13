package com.devapp.devmain.peripherals.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.peripherals.factories.DeviceFactory;
import com.devapp.devmain.peripherals.interfaces.DataObserver;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devApp.R;

public class ChangeIpActivity extends Activity implements DataObserver {
    private Context context = this;
    private Spinner deviceSpinner, moduleSpinner;
    private Device device;
    private String ipCommand = "SETIPADDRESS";
    private String subnetMask = "255.255.255.0,192.168.43.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_ip);
        initializeComponents();
    }

    private void initializeComponents() {
        deviceSpinner = (Spinner) findViewById(R.id.from_sp);
        moduleSpinner = (Spinner) findViewById(R.id.to_sp);
        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DeviceName.deviceNames);
        ArrayAdapter<String> moduleAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, DeviceName.modules);
        deviceSpinner.setAdapter(deviceAdapter);
        moduleSpinner.setAdapter(moduleAdapter);
    }

    public void changeIp(View v) {
        String fromIp = getIpFromDevice(deviceSpinner.getSelectedItem().toString());
        String toIp = getIpFromModule(moduleSpinner.getSelectedItem().toString());

        if (!fromIp.equals(toIp)) {
            device = getDeviceInstance(deviceSpinner.getSelectedItem().toString());
            device.registerObserver(this);
            device.read();
            String changeIpMsg = ipCommand + "," + toIp + "," + subnetMask;
            device.write(changeIpMsg);
        } else
            Util.displayErrorToast("Please select a different module", context);
    }

    private Device getDeviceInstance(String deviceName) {
        return DeviceFactory.getDevice(context, deviceName);
    }

    private String getIpFromDevice(String device) {
        String ip = null;
        switch (device) {
            case DeviceName.MA1:
                ip = SmartCCConstants.maIp;
                break;
            case DeviceName.WS:
                ip = SmartCCConstants.wsIp;
                break;
            case DeviceName.RDU:
                ip = SmartCCConstants.rduIp;
                break;
            case DeviceName.PRINTER:
                ip = SmartCCConstants.printerIp;
                break;
        }
        return ip;
    }

    private String getIpFromModule(String module) {
        String ip = null;
        switch (module) {
            case DeviceName.YELLOW:
                ip = SmartCCConstants.maIp;
                break;
            case DeviceName.RED:
                ip = SmartCCConstants.wsIp;
                break;
            case DeviceName.BLUE:
                ip = SmartCCConstants.rduIp;
                break;
            case DeviceName.GREEN:
                ip = SmartCCConstants.printerIp;
                break;
        }
        return ip;
    }

    @Override
    public void onDataReceived(byte[] data) {
        String msg = new String(data);
        if (msg.startsWith(SmartCCConstants.ACK))
            Util.restartApp(context);
    }
}
