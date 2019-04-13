package com.devapp.devmain.peripherals.probers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.peripherals.controllers.WisensTCPController;
import com.devapp.devmain.peripherals.interfaces.Prober;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.net.Socket;
import java.util.ArrayList;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;


/**
 * Created by xx on 22/1/18.
 */

public class WifiTCPProber implements Prober {

    private static Context mContext;
    private static WifiTCPProber instance;
    private final String TAG = WifiTCPProber.class.getSimpleName();
    private WisensTCPController maController, wsController,
            rduController, printerController;
    private ArrayList<DeviceEntity> deviceList;
    private Socket maSocket, wsSocket, rduSocket,
            printerSocket;

    private WifiTCPProber() {
        initializeVariables();
    }

    public static WifiTCPProber getInstance(Context context) {
        mContext = context;
        if (instance == null)
            instance = new WifiTCPProber();
        return instance;
    }

    private void initializeVariables() {
        if (maController == null)
            maController = new WisensTCPController();
        if (wsController == null)
            wsController = new WisensTCPController();
        if (rduController == null)
            rduController = new WisensTCPController();
        if (printerController == null)
            printerController = new WisensTCPController();
    }

    @Override
    public void startProbing(String deviceName) {
        Log.v(PROBER, "starting TCP probing");
//        clearAllSockets();
        switch (deviceName) {
            case SmartCCConstants.ALL_DEVICES:
                probeAllDevices();
                break;
            case DeviceName.MA1:
                new Thread() {

                    @Override
                    public void run() {
                        maSocket = null;
                        maController.reinitializeConnection();
                        maSocket = maController.startConnection(SmartCCConstants.CON, SmartCCConstants.maIp);
                        updateDeviceEntityMap(getDeviceEntity(maSocket, DeviceName.MA1, SmartCCConstants.maIp));
                        sendDeviceConnectedBroadcast(SmartCCConstants.MA_CONNECTED);
                    }
                }.start();
                break;
            case DeviceName.WS:
                new Thread() {

                    @Override
                    public void run() {
                        wsSocket = null;
                        wsController.reinitializeConnection();
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
                        rduSocket = null;
                        rduController.reinitializeConnection();
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
                        printerSocket = null;
                        printerController.reinitializeConnection();
                        printerSocket = printerController.startConnection(SmartCCConstants.CON, SmartCCConstants.printerIp);
                        updateDeviceEntityMap(getDeviceEntity(printerSocket, DeviceName.PRINTER, SmartCCConstants.printerIp));
                        sendDeviceConnectedBroadcast(SmartCCConstants.PRINTER_CONNECTED);
                    }
                }.start();
                break;
        }
    }

    private void updateDeviceEntityMap(DeviceEntity deviceEntity) {
        Log.v("TCP", "Updating device entity map for " + deviceEntity.deviceName);
        SmartCCConstants.deviceEntityMap.put(deviceEntity.deviceName, deviceEntity);

    }

    private void sendDeviceConnectedBroadcast(String action) {
        Log.v("TCP", "Sending broadcast for " + action);
        Intent i = new Intent(action);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(i);

    }

    private void probeAllDevices() {

        new Thread() {

            @Override
            public void run() {
                if (SmartCCConstants.deviceEntityMap.get(DeviceName.WS) == null) {
                    wsController.reinitializeConnection();
                    wsSocket = wsController.startConnection(SmartCCConstants.CON, SmartCCConstants.wsIp);
                }
            }
        }.start();
        new Thread() {

            @Override
            public void run() {
                if (SmartCCConstants.deviceEntityMap.get(DeviceName.RDU) == null) {
                    rduController.reinitializeConnection();
                    rduSocket = rduController.startConnection(SmartCCConstants.CON, SmartCCConstants.rduIp);
                }
            }
        }.start();
        new Thread() {

            @Override
            public void run() {
                if (SmartCCConstants.deviceEntityMap.get(DeviceName.PRINTER) == null) {
                    printerController.reinitializeConnection();
                    printerSocket = printerController.startConnection(SmartCCConstants.CON, SmartCCConstants.printerIp);
                }
            }
        }.start();
        new Thread() {

            @Override
            public void run() {
                if (SmartCCConstants.deviceEntityMap.get(DeviceName.MA1) == null) {
                    maController.reinitializeConnection();
                    maSocket = maController.startConnection(SmartCCConstants.CON, SmartCCConstants.maIp);
                }
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
        Log.v(TAG, "TCP devices found: " + deviceList.size());
        return deviceList;
    }

    private DeviceEntity getDeviceEntity(Socket socket, String deviceName,
                                         String ipAddress) {
        DeviceEntity entity = new DeviceEntity();
        entity.deviceType = SmartCCConstants.WIFI_TCP;
        entity.deviceName = deviceName;
        entity.tcpSocket = socket;
        entity.ipAddress = ipAddress;
        return entity;
    }

}
