package com.devapp.devmain.peripherals.probers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.peripherals.controllers.WisensUDPController;
import com.devapp.devmain.peripherals.interfaces.Prober;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.net.DatagramSocket;
import java.util.ArrayList;


/**
 * Created by xx on 22/1/18.
 */

public class WifiUDPProber implements Prober {

    static Context mContext;
    static WifiUDPProber instance;
    private final String TAG = WifiUDPProber.class.getSimpleName();
    DatagramSocket maSocket, wsSocket,
            rduSocket, printerSocket;
    WisensUDPController maController, wsController,
            rduController, printerController;
    ArrayList<DeviceEntity> deviceList;

    private WifiUDPProber() {
        initializeVariables();
    }

    public static WifiUDPProber getInstance(Context context) {
        mContext = context;
        if (instance == null)
            instance = new WifiUDPProber();
        return instance;
    }

    private void initializeVariables() {
        maController = new WisensUDPController();
        wsController = new WisensUDPController();
        rduController = new WisensUDPController();
        printerController = new WisensUDPController();
    }

    @Override
    public void startProbing(String deviceName) {
        Log.v(TAG, "starting UDP probing");
        switch (deviceName) {
            case SmartCCConstants.ALL_DEVICES:
                probeAllDevices();
                break;
            case DeviceName.WS:
                new Thread() {

                    @Override
                    public void run() {
                        wsSocket = wsController.startConnection(SmartCCConstants.CON, SmartCCConstants.wsIp);
                        updateDeviceEntityMap(getDeviceEntity(wsSocket, DeviceName.WS, SmartCCConstants.wsIp));
                        sendDeviceConnectedBroadcast(SmartCCConstants.WS_CONNECTED);
                    }
                }.start();
                break;
            case DeviceName.RDU:
                new Thread() {

                    @Override
                    public void run() {
                        rduSocket = rduController.startConnection(SmartCCConstants.CON, SmartCCConstants.rduIp);
                        updateDeviceEntityMap(getDeviceEntity(rduSocket, DeviceName.RDU, SmartCCConstants.rduIp));
                        sendDeviceConnectedBroadcast(SmartCCConstants.RDU_CONNECTED);
                    }
                }.start();
                break;
            case DeviceName.PRINTER:
                new Thread() {

                    @Override
                    public void run() {
                        printerSocket = printerController.startConnection(SmartCCConstants.CON, SmartCCConstants.printerIp);
                        updateDeviceEntityMap(getDeviceEntity(printerSocket, DeviceName.PRINTER, SmartCCConstants.printerIp));
                        sendDeviceConnectedBroadcast(SmartCCConstants.PRINTER_CONNECTED);
                    }
                }.start();
            case DeviceName.MA1:
                new Thread() {

                    @Override
                    public void run() {
                        maSocket = maController.startConnection(SmartCCConstants.CON, SmartCCConstants.maIp);
                        updateDeviceEntityMap(getDeviceEntity(maSocket, DeviceName.MA1, SmartCCConstants.maIp));
                        sendDeviceConnectedBroadcast(SmartCCConstants.MA_CONNECTED);
                    }
                }.start();
                break;
        }
    }

    private void updateDeviceEntityMap(DeviceEntity deviceEntity) {
        Log.v(TAG, "Updating device entity map for " + deviceEntity.deviceName);
        SmartCCConstants.deviceEntityMap.put(deviceEntity.deviceName, deviceEntity);

    }

    private void sendDeviceConnectedBroadcast(String action) {
        Log.v(TAG, "Sending broadcast for " + action);
        Intent i = new Intent(action);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(i);

    }

    private void probeAllDevices() {


        new Thread() {

            @Override
            public void run() {
                wsSocket = wsController.startConnection(SmartCCConstants.CON, SmartCCConstants.wsIp);
            }
        }.start();
        new Thread() {

            @Override
            public void run() {
                rduSocket = rduController.startConnection(SmartCCConstants.CON, SmartCCConstants.rduIp);
            }
        }.start();
        new Thread() {

            @Override
            public void run() {
                printerSocket = printerController.startConnection(SmartCCConstants.CON, SmartCCConstants.printerIp);
            }
        }.start();
        new Thread() {

            @Override
            public void run() {
                maSocket = maController.startConnection(SmartCCConstants.CON, SmartCCConstants.maIp);
            }
        }.start();
    }

    @Override
    public void stopProbing() {
        maController.stopConnection();
        wsController.stopConnection();
        rduController.stopConnection();
        printerController.stopConnection();
    }

    @Override
    public ArrayList<DeviceEntity> getDevices() {
        deviceList = new ArrayList<>();
        if (maSocket != null)
            deviceList.add(getDeviceEntity(maSocket, DeviceName.MA1, SmartCCConstants.maIp));
        if (wsSocket != null)
            deviceList.add(getDeviceEntity(wsSocket, DeviceName.WS, SmartCCConstants.wsIp));
        if (rduSocket != null)
            deviceList.add(getDeviceEntity(rduSocket, DeviceName.RDU, SmartCCConstants.rduIp));
        if (printerSocket != null)
            deviceList.add(getDeviceEntity(printerSocket, DeviceName.PRINTER, SmartCCConstants.printerIp));
        return deviceList;
    }

    private DeviceEntity getDeviceEntity(DatagramSocket socket, String deviceName,
                                         String ipAddress) {
        DeviceEntity entity = new DeviceEntity();
        entity.deviceSocket = socket;
        entity.deviceType = SmartCCConstants.WIFI_UDP;
        entity.deviceName = deviceName;
        entity.ipAddress = ipAddress;
        return entity;
    }
}
