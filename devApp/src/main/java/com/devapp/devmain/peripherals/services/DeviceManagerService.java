package com.devapp.devmain.peripherals.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.peripherals.factories.DeviceFactory;
import com.devapp.devmain.peripherals.interfaces.Prober;
import com.devapp.devmain.peripherals.probers.WifiTCPProber;
import com.devapp.devmain.peripherals.probers.WifiUDPProber;
import com.devapp.devmain.peripherals.probers.WiredProber;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;

/**
 * Created by xx on 1/2/18.
 */

public class DeviceManagerService extends Service {
    private final String TAG = DeviceManagerService.class.getSimpleName();
    private Prober wiredProber, wifiUdpProber, wifiTcpProber;
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case SmartCCConstants.DEVICE_DISCONNECT:
                    DeviceEntity deviceEntity = (DeviceEntity) intent.getSerializableExtra(SmartCCConstants.DEVICE_NAME);
                    if (deviceEntity != null && SmartCCConstants.deviceEntityMap.get(deviceEntity.deviceName) != null) {
                        SmartCCConstants.deviceEntityMap.remove(deviceEntity.deviceName);
                        startProbing(deviceEntity);
                    }
            }
        }
    };
    private Context mContext = this;
    private ArrayList<String> foundDevices;
    private ArrayList<DeviceEntity> wifiDevices, wiredDevices;
    private Handler deviceHandler;
    private Runnable deviceRunnable;
    private State mState = State.CONNECTING;

    private void initializeVariables() {
        wiredProber = WiredProber.getInstance(mContext);
        if (AmcuConfig.getInstance().getHotspotValue() ||
                AmcuConfig.getInstance().getWifiValue()) {
            wifiUdpProber = WifiUDPProber.getInstance(mContext);
            wifiTcpProber = WifiTCPProber.getInstance(mContext);
        }
        foundDevices = new ArrayList<String>();
        wifiDevices = new ArrayList<DeviceEntity>();
        wiredDevices = new ArrayList<DeviceEntity>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("TCP", "Inside device probing service");
        initializeVariables();
        LocalBroadcastManager.getInstance(DeviceManagerService.this).registerReceiver(receiver,
                new IntentFilter(SmartCCConstants.DEVICE_DISCONNECT));
        new Thread(new Runnable() {
            @Override
            public void run() {
                startProbing();
            }
        }).start();
        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startProbing() {
        DeviceFactory.shutdownDevices("");
        wiredProber.startProbing(SmartCCConstants.ALL_DEVICES);
        if (wifiUdpProber != null) {
            wifiUdpProber.startProbing(SmartCCConstants.ALL_DEVICES);
        }
        if (wifiTcpProber != null)
            wifiTcpProber.startProbing(SmartCCConstants.ALL_DEVICES);
        getDiscoveredDevices();
    }

    private void startProbing(DeviceEntity deviceEntity) {
        DeviceFactory.shutdownDevices(deviceEntity.deviceName);
        if (deviceEntity != null) {
            getProber(deviceEntity).startProbing(deviceEntity.deviceName);
        }
    }

    private Prober getProber(DeviceEntity deviceEntity) {
        switch (deviceEntity.deviceType) {
            case SmartCCConstants.WIFI_TCP:
                if (wifiTcpProber != null)
                    return wifiTcpProber;
            case SmartCCConstants.WIFI_UDP:
                if (wifiUdpProber != null)
                    return wifiUdpProber;
            case SmartCCConstants.USB:
            case SmartCCConstants.USB_TVS:
                if (wiredProber != null)
                    return wiredProber;
            default:
                return wiredProber;
        }
    }

    private void stopProbing() {
        if (wiredProber != null)
            wiredProber.stopProbing();
        if (wifiUdpProber != null)
            wifiUdpProber.stopProbing();
        if (wifiTcpProber != null)
            wifiTcpProber.stopProbing();
    }

    private void getDiscoveredDevices() {
        deviceHandler = new Handler(Looper.getMainLooper());
        deviceRunnable = new Runnable() {
            @Override
            public void run() {

                ArrayList<DeviceEntity> deviceList = new ArrayList<>();
                try {
                    deviceList = getDevices();
                } catch (NullPointerException e) {

                }
                if (deviceList != null) {
                    if (deviceList.size() != 4 && mState != State.STOPPED) {
                        deviceHandler.postDelayed(this, 3000);
                    }
                    Intent i = new Intent(SmartCCConstants.DEVICES_DISCOVERED);
                    i.putExtra(SmartCCConstants.DEVICE_LIST, deviceList);
                    LocalBroadcastManager.getInstance(DeviceManagerService.this)
                            .sendBroadcast(i);
                }
            }
        };
        deviceHandler.post(deviceRunnable);
    }

    private ArrayList<DeviceEntity> getDevices() throws NullPointerException {
        foundDevices.clear();
        wifiDevices.clear();
        wiredDevices.clear();
        if (wifiUdpProber != null) {
//            wifiDevices = wifiUdpProber.getDevices();
            wifiDevices.addAll(wifiUdpProber.getDevices());
        }
        if (wifiTcpProber != null)
            wifiDevices.addAll(wifiTcpProber.getDevices());
        Log.v(PROBER, "Wifi devices found: " + wifiDevices.size());
        wiredDevices.addAll(wiredProber.getDevices());
        if ((wifiDevices != null && wifiDevices.size() > 0) || wiredDevices.size() > 0) {

            //reshuffling wired devices
            if (wifiDevices != null) {
                for (DeviceEntity device : wifiDevices) {
                    foundDevices.add(device.deviceName);
                }
            }


            for (int i = 0; i < wiredDevices.size(); i++) {
                Log.v("prober", "Wired device name: " + wiredDevices.get(i).deviceName);
                reshuffleWiredDevices(i);
                if (i > 0 &&
                        wiredDevices.get(i).deviceName.equals(wiredDevices.get(i - 1).deviceName)) {
                    changeWiredDeviceName(i);
                    reshuffleWiredDevices(i);
                }
            }
            SmartCCConstants.devicesList.clear();
            SmartCCConstants.devicesList.addAll(wiredDevices);
            if (wifiDevices != null) {
                SmartCCConstants.devicesList.addAll(wifiDevices);
            }
            for (DeviceEntity deviceEntity : SmartCCConstants.devicesList) {
                if (deviceEntity.deviceName != null &&
                        (deviceEntity.deviceName.equalsIgnoreCase(SmartCCConstants.RDU) ||
                                deviceEntity.deviceName.equalsIgnoreCase(SmartCCConstants.PRINTER))) {
                    deviceEntity.deviceMode = CollectionConstants.WRITE_ONLY;
                }
                SmartCCConstants.deviceEntityMap.put(deviceEntity.deviceName, deviceEntity);
            }
            Log.v("prober", "total devices found: " + SmartCCConstants.devicesList.size());
            setAppType();
        } else {
            // to avoid showing previously detected devices if currently no device is found
            SmartCCConstants.devicesList = new ArrayList<>();
        }
        return SmartCCConstants.devicesList;
    }

    private void reshuffleWiredDevices(int pos) {
        while (foundDevices.contains(wiredDevices.get(pos).deviceName)) {
            Log.v("prober", "Reshuffling devices: " + wiredDevices.get(pos).deviceName + " , pos: " + pos);
            changeWiredDeviceName(pos);
        }
    }

    private void changeWiredDeviceName(int pos) {
        int position = ArrayUtils.indexOf(DeviceName.deviceNames, wiredDevices.get(pos).deviceName);
        if (position < DeviceName.deviceNames.length - 1)
            wiredDevices.get(pos).deviceName = DeviceName.deviceNames[++position];

    }

    private void setAppType() {
        if (SmartCCConstants.devicesList.size() > 0) {
            if (wiredDevices.size() == DeviceName.deviceNames.length)
                SmartCCConstants.appType = SmartCCConstants.USB;
            else if ((wifiDevices != null) && (wifiDevices.size() == DeviceName.deviceNames.length))
                SmartCCConstants.appType = SmartCCConstants.WIFI_UDP;
            else
                SmartCCConstants.appType = SmartCCConstants.MIXED;
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "On destroy");
        mState = State.STOPPED;
        stopProbing();
        if (deviceHandler != null) {
            deviceHandler.removeCallbacks(deviceRunnable);
        }
    }

    private enum State {
        CONNECTING, STOPPED
    }

}
