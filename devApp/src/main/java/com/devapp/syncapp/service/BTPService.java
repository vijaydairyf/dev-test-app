package com.devapp.syncapp.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.devapp.devmain.peripherals.network.HotspotManager;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.syncapp.SyncAppConstants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.passwordList;
import static com.devapp.smartcc.entityandconstants.SmartCCConstants.ssidList;

public class BTPService extends Service {


    public static final String START = "com.bt.ping.start";
    public static final String STOP = "com.bt.ping.stop";
    public static final int MIN = 60 * 1000;
    public static final long HANDSET_BT_TOGGLE_TIME = 4 * MIN;
    public static final String MAC_ADDRESS = "mac_address";
    public static final int SEC = 1000;
    private static final long SWITCH_ON_DELAY = 10 * SEC;
    private static final String TAG = "BTPService";
    private static final long DELAY_4_SEC = 4 * SEC;
    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> btDeviceList;
    private SharedPreferences pref;
    private BluetoothSocket socket = null;
    private CountDownTimer timer;
    private boolean shouldRestartBT;
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent == null || intent.getAction() == null)
                return;

            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    OnBTStateChange(intent);
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    Log.i(TAG, "onReceive: ACTION_FOUND");
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    Add(device);
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.i(TAG, "onReceive: ACTION_DISCOVERY_FINISHED");
                    if (shouldRestartBT && bluetoothAdapter.isEnabled()) {
                        setTimer();
                        bluetoothAdapter.disable();
                    } else
                        OnDiscoveryFinished();
                    break;
            }

        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: ");
        initialize();
        if (bluetoothAdapter == null)
            Log.e(TAG, "No bluetooth hardware present");
        else
            StartDiscovery();
        setTimer();

    }

    private void setTimer() {
        if (timer != null)
            timer.cancel();

        shouldRestartBT = false;
        timer = new CountDownTimer(HANDSET_BT_TOGGLE_TIME, SEC) {
            @Override
            public void onTick(long millisUntilFinished) {
                shouldRestartBT = false;
            }

            @Override
            public void onFinish() {
                shouldRestartBT = true;
            }
        };
        timer.start();
    }

    private void initialize() {
        if (new SessionManager(getApplicationContext()).getKeyBluetoothEnable()) {
            this.context = this;
            this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            this.pref = getSharedPreferences(SyncAppConstants.PREF.PREF_NAME, Context.MODE_PRIVATE);
            this.btDeviceList = new ArrayList<BluetoothDevice>();

            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_FOUND);
            filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(bluetoothReceiver, filter);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");

        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(START)) {

                if (this.context == null || bluetoothAdapter == null || btDeviceList == null || pref == null) {
                    Log.i(TAG, "onStartCommand: Re-Initialized()");
                    initialize();
                }
            } else if (intent.getAction().equals(STOP)) {
                stopSelf();
            }

        }
        return START_REDELIVER_INTENT;
    }


    private void OnBTStateChange(Intent intent) {
        int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

        switch (state) {

            case BluetoothAdapter.STATE_TURNING_ON:
                Log.i(TAG, "BT STATE_TURNING_ON I DON'T CARE");
                break;
            case BluetoothAdapter.STATE_ON:
                Log.i(TAG, "BT STATE_TURN_ON");
                StartDiscovery();
                break;
            case BluetoothAdapter.STATE_TURNING_OFF:
                Log.i(TAG, "BT STATE_TURNING_OFF I DON'T CARE");
                break;
            case BluetoothAdapter.STATE_OFF:
                Log.i(TAG, "BT STATE_TURN_OFF");
                Log.i(TAG, "BT will turn on after " + SWITCH_ON_DELAY / 1000 + " secs.");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!bluetoothAdapter.isEnabled())
                                bluetoothAdapter.enable();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, BTPService.SWITCH_ON_DELAY);
                break;
        }
    }

    private void Add(BluetoothDevice device) {
        if (device != null
                && device.getName() != null
                && device.getAddress() != null
                && !device.getAddress().isEmpty()
                && !device.getName().isEmpty() &&
                (device.getBluetoothClass().getDeviceClass() == BluetoothClass.Device.PHONE_SMART || device.getName().equals("V70"))
                && !btDeviceList.contains(device)) {
            btDeviceList.add(device);
        }
    }

    private synchronized void OnDiscoveryFinished() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PingDevices();
                try {
                    Thread.sleep(DELAY_4_SEC);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                StartDiscovery();
            }

            private void PingDevices() {
                try {
                    Set<String> stringSet = pref.getStringSet(MAC_ADDRESS, new TreeSet<String>());

                    for (String s : stringSet) {
                        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(s);
                        if (!btDeviceList.contains(device))
                            btDeviceList.add(device);
                    }

                    Log.i(TAG, "Complete DEVICE LIST: Thread " + Thread.currentThread().getId() + "\t" + btDeviceList);
                    if (btDeviceList != null && btDeviceList.size() > 0) {

                        for (BluetoothDevice device : btDeviceList) {
                            String deviceName = device.getName();
                            String deviceAddress = device.getAddress();
                            try {
                                socket = device.createInsecureRfcommSocketToServiceRecord(SyncAppConstants.BT_UUID);
                                socket.connect();
                                Log.i(TAG, "Attempting socket: PASSED for " + deviceName + "\t" + deviceAddress);
                                stringSet.add(device.getAddress());
//                                pref.edit().putStringSet(MAC_ADDRESS, stringSet).commit();
                                Set<String> newMac = new HashSet<>();
                                newMac.addAll(stringSet);
                                newMac.add(device.getAddress());
                                pref.edit().putStringSet(MAC_ADDRESS, newMac).commit();

                                if (!AmcuConfig.getInstance().getWifiValue())
                                    HotspotManager.enableHotspot(context, ssidList[0], passwordList[0]);
                                else
                                    Log.w(TAG, "WiFi is on, cannot turn on TAB hotspot");
                            } catch (Exception e) {
                                Log.i(TAG, "Attempting socket: FAILED for " + deviceName + "\t" + deviceAddress);
                                //e.printStackTrace();
                            }
                            closeSocket();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void StartDiscovery() {
        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        } else if (!bluetoothAdapter.isDiscovering()) {
            boolean searchingStarted = bluetoothAdapter.startDiscovery();
            Log.i(TAG, "onReceive: DISCOVERY RESCHEDULED " + searchingStarted);
            if (!searchingStarted)
                bluetoothAdapter.disable();
        }
    }

    void closeSocket() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        try {
            unregisterReceiver(bluetoothReceiver);
            if (bluetoothAdapter.isDiscovering())
                bluetoothAdapter.cancelDiscovery();
            closeSocket();
            if (bluetoothAdapter.isEnabled())
                bluetoothAdapter.disable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
