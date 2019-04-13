package com.devapp.devmain.peripherals.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.devapp.devmain.main.DeviceListActivity;
import com.devapp.devmain.peripherals.network.ScanResult;
import com.devapp.devmain.peripherals.services.DeviceDiscoveryService;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devApp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class DeviceDiscoveryActivity extends AppCompatActivity {

    AlertDialog progress;
    Context context = this;
    LinearLayout configLayout, scanLayout;
    TextView ipView, noIpView;
    Button doneButton, scanButton;
    Spinner deviceSpinner;
    HashMap<String, String> deviceMap;
    HashSet<String> discoveredIps;
    List<String> ipList, devicesList;
    boolean maAvailable, wsAvailable,
            printerAvailable, rduAvailable;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SmartCCConstants.DEVICE_CONNECT)) {
                ArrayList<ScanResult> clients = SmartCCConstants.clients;
                if (clients != null && clients.size() > 0) {
                    for (int i = 0; i < clients.size(); i++) {
                        discoveredIps.add(clients.get(i).getIpAddr());
                        Log.v("udp", "Devices found: " + clients.size() + " : " + clients.get(i).getIpAddr()
                                + " reachable : " + clients.get(i).isReachable());
                    }
                } else {
                    Log.v("udp", "Devices found: 0");
                }
                if (progress != null)
                    dismissProgress();
                showConfigLayout();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_devices);
        progress = new ProgressDialog(this);
        deviceSpinner = (Spinner) findViewById(R.id.device_spinner);
        configLayout = (LinearLayout) findViewById(R.id.config_layout);
        scanLayout = (LinearLayout) findViewById(R.id.scan_layout);
        ipView = (TextView) findViewById(R.id.device_ip);
        noIpView = (TextView) findViewById(R.id.no_ip_tv);
        doneButton = (Button) findViewById(R.id.done_btn);
        scanButton = (Button) findViewById(R.id.scan_btn);
        deviceMap = new HashMap<String, String>();
        discoveredIps = new HashSet<String>();
        devicesList = new ArrayList<String>(Arrays.asList(getResources().getStringArray(R.array.device_list)));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
                new IntentFilter(SmartCCConstants.DEVICE_CONNECT));
        Intent intent = getIntent();
        if (intent.hasExtra(SmartCCConstants.MA_AVAILABLE)) {
            maAvailable = intent.getBooleanExtra(SmartCCConstants.MA_AVAILABLE, false);
            wsAvailable = intent.getBooleanExtra(SmartCCConstants.WS_AVAILABLE, false);
            rduAvailable = intent.getBooleanExtra(SmartCCConstants.RDU_AVAILABLE, false);
            printerAvailable = intent.getBooleanExtra(SmartCCConstants.PRINTER_AVAILABLE, false);
        }
        if (maAvailable)
            devicesList.remove(devicesList.indexOf("MA"));
        if (wsAvailable)
            devicesList.remove(devicesList.indexOf("WS"));
        if (rduAvailable)
            devicesList.remove(devicesList.indexOf("RDU"));
        if (printerAvailable)
            devicesList.remove(devicesList.indexOf("PRINTER"));
    }

    @Override
    public void onBackPressed() {
        if (progress != null)
            dismissProgress();
        super.onBackPressed();
    }

    public void scanDevices(View v) {
        if (progress != null)
            displayProgress();
        Intent discoveryIntent = new Intent(this, DeviceDiscoveryService.class);
        startService(discoveryIntent);
    }

    public void clearDevices(View v) {

//        SmartCCConstants.setMaIp("", context);
//        SmartCCConstants.setWsIp("", context);
//        SmartCCConstants.setRduIp("", context);
//        SmartCCConstants.setPrinterIp("", context);
        Util.restartApp(context);
    }

    public void saveDevice(View v) {
        String device = deviceSpinner.getSelectedItem().toString();
        String ip = ipView.getText().toString();
        deviceMap.put(device, ip);
        devicesList.remove(devicesList.indexOf(device));
        storeDeviceIps(device, ip);
        printMap();
        showScanLayout();
    }

    private void storeDeviceIps(String device, String ip) {
        /*if (device.equals(MA))
            SmartCCConstants.setMaIp(ip, context);
        else if (device.equals(WS))
            SmartCCConstants.setWsIp(ip, context);
        else if (device.equals(RDU))
            SmartCCConstants.setRduIp(ip, context);
        else if (device.equals(PRINTER))
            SmartCCConstants.setPrinterIp(ip, context);*/
    }

    public void discardDevice(View v) {
        showScanLayout();
    }

    private void printMap() {
        Iterator it = deviceMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Log.v("Device", "" + pair.getKey() + " : " + pair.getValue());

        }
    }

    private void showScanLayout() {

        configLayout.setVisibility(View.GONE);
        doneButton.setVisibility(View.GONE);
        scanLayout.setVisibility(View.VISIBLE);
    }

    private void showConfigLayout() {

        scanButton.setText("Scan Device");
        scanLayout.setVisibility(View.GONE);
        configLayout.setVisibility(View.VISIBLE);
        doneButton.setVisibility(View.VISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, devicesList);
        deviceSpinner.setAdapter(adapter);
        ipList = new ArrayList<>(discoveredIps);
        for (int i = ipList.size() - 1; i >= 0; i--) {
            String ip = ipList.get(i);
            if (!displayIp(ip)) {
                configLayout.setVisibility(View.GONE);
                noIpView.setVisibility(View.VISIBLE);
                scanLayout.setVisibility(View.VISIBLE);
                scanButton.setText("Scan Again");
            }
        }
    }

    private boolean displayIp(String ip) {
        for (String value : deviceMap.values()) {
            if (value.equalsIgnoreCase(ip)) {
                return false;
            }
        }
        ipView.setText(ip);
        return true;
    }

    public void goToNextActivity(View v) {
        //goto devicelistactivity

        Intent intent = new Intent(context, DeviceListActivity.class);
        intent.putExtra("fromSplashActivity", true);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
        finish();
    }

    private void displayProgress() {

        try {
            progress.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissProgress() {

        try {
            progress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
