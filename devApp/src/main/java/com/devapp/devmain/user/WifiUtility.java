package com.devapp.devmain.user;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by xx on 8/5/18.
 */

public class WifiUtility {

    private final String TAG = WifiUtility.class.getSimpleName();
    public ArrayList<Boolean> maPingList = new ArrayList<>(),
            wsPingList = new ArrayList<>(),
            rduPingList = new ArrayList<>(), printerPingList = new ArrayList<>();
    public int maxSize = 3;
    private Context mContext;

    public void checkWisensConnectivity(Context context, String module, boolean pingValue) {
        mContext = context;
        switch (module) {
            case DeviceName.MA1:
                maPingList.add(pingValue);
                break;
            case DeviceName.WS:
                wsPingList.add(pingValue);
                break;
            case DeviceName.RDU:
                rduPingList.add(pingValue);
                break;
            case DeviceName.PRINTER:
                printerPingList.add(pingValue);
                break;
        }
        if (maPingList.size() > maxSize)
            maPingList.subList(0, maxSize - 1).clear();
        if (wsPingList.size() > maxSize)
            wsPingList.subList(0, maxSize - 1).clear();
        if (rduPingList.size() > maxSize)
            rduPingList.subList(0, maxSize - 1).clear();
        if (printerPingList.size() > maxSize)
            printerPingList.subList(0, maxSize - 1).clear();
        if (moduleDown(Collections.frequency(maPingList, false))) {
            resetValues();
            sendDeviceDisconnectBroadcast(module);
        }
        if (moduleDown(Collections.frequency(wsPingList, false))) {
            resetValues();
            sendDeviceDisconnectBroadcast(module);
        }
        if (moduleDown(Collections.frequency(rduPingList, false))) {
            resetValues();
            sendDeviceDisconnectBroadcast(module);
        }
        if (moduleDown(Collections.frequency(printerPingList, false))) {
            resetValues();
            sendDeviceDisconnectBroadcast(module);
        }
    }

    private void resetValues() {
        maPingList.clear();
        wsPingList.clear();
    }

    private boolean moduleDown(int occurences) {
        return occurences == maxSize;
    }

    private void sendDeviceDisconnectBroadcast(String deviceName) {
        DeviceEntity deviceEntity = SmartCCConstants.deviceEntityMap.get(deviceName);
        if (deviceEntity != null) {
            Intent i = new Intent(SmartCCConstants.DEVICE_DISCONNECT);
            i.putExtra(SmartCCConstants.DEVICE_NAME, deviceEntity);
            Log.v(TAG, "Sending disconnect broadcast for " + deviceName);
            LocalBroadcastManager.getInstance(DevAppApplication.getAmcuContext()).sendBroadcast(i);
        }
    }
}
