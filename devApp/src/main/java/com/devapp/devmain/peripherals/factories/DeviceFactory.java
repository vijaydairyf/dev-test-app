package com.devapp.devmain.peripherals.factories;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.devicemanager.DriverConfiguration;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.ma.MAFactory;
import com.devapp.devmain.peripherals.devices.DeviceParameters;
import com.devapp.devmain.peripherals.devices.TVSDevice;
import com.devapp.devmain.peripherals.devices.WifiTCPDevice;
import com.devapp.devmain.peripherals.devices.WifiUDPDevice;
import com.devapp.devmain.peripherals.devices.WiredDevice;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.rdu.RduFactory;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.devmain.ws.WsFactory;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.util.HashMap;

/**
 * Created by xx on 29/1/18.
 * Maintain a map of devicename and device. return already instantiated device
 */

public class DeviceFactory {

    private static final String TAG = DeviceFactory.class.getSimpleName();
    private static HashMap<String, Device> deviceMap = new HashMap<>();
    private static Device.ErrorListener deviceDisconnectListener = new Device.ErrorListener() {
        @Override
        public void onError(Exception e, DeviceEntity deviceEntity) {
            sendDeviceDisconnectBroadcast(deviceEntity);
        }

        @Override
        public void onError(String errorMessage, DeviceEntity deviceEntity) {
            sendDeviceDisconnectBroadcast(deviceEntity);
        }
    };

    private static void sendDeviceDisconnectBroadcast(DeviceEntity deviceEntity) {
        Log.v(TAG, "Sending disconnect broadcast for " + deviceEntity.deviceName);
        Intent i = new Intent(SmartCCConstants.DEVICE_DISCONNECT);
        i.putExtra(SmartCCConstants.DEVICE_NAME, deviceEntity);
//        SmartCCConstants.deviceEntityMap.remove(deviceEntity.deviceName);
        LocalBroadcastManager.getInstance(DevAppApplication.getAmcuContext()).sendBroadcast(i);
    }

    public static void shutdownDevices(String deviceName) {
        /*for (String key : deviceMap.keySet()) {
            deviceMap.get(key).closeConnection();
        }*/
        if (deviceMap.get(deviceName) != null)
            deviceMap.get(deviceName).closeConnection();
        deviceMap.clear();
        switch (deviceName) {
            case DeviceName.MA1:
                MAFactory.resetManagers();
                break;
            case DeviceName.WS:
                WsFactory.resetManagers();
                break;
            case DeviceName.RDU:
                RduFactory.resetManagers();
            default:
                MAFactory.resetManagers();
                WsFactory.resetManagers();
                RduFactory.resetManagers();

        }
    }


    public static Device getDevice(Context context, String deviceName) {
        if (deviceMap.get(deviceName) != null)
            return deviceMap.get(deviceName);

        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        DeviceParameters deviceParameters = null;
        DriverConfiguration configuration = new DriverConfiguration();
        DeviceEntity deviceEntity = SmartCCConstants.deviceEntityMap.get(deviceName);
        if (deviceEntity == null)
            return null;
        Device device;
        switch (deviceName) {
            case DeviceName.MA1:
                deviceParameters = new DeviceParameters(amcuConfig.getMABaudRate(), configuration.getDataBits(amcuConfig.getMaDataBits()),
                        configuration.getParity(amcuConfig.getMaParity()), configuration.getStopBits(amcuConfig.getMaStopBits()));
                break;
           /* case DeviceName.MILK_ANALYSER:
                deviceParameters = new DeviceParameters(amcuConfig.getMABaudRate(), configuration.getDataBits(amcuConfig.getMaDataBits()),
                        configuration.getParity(amcuConfig.getMaParity()), configuration.getStopBits(amcuConfig.getMaStopBits()));
                break;*/
            case DeviceName.MA2:
                deviceParameters = new DeviceParameters(amcuConfig.getMa2Baudrate(), configuration.getDataBits(amcuConfig.getMa2DataBits()),
                        configuration.getParity(amcuConfig.getMa2Parity()), configuration.getStopBits(amcuConfig.getMa2StopBits()));
                break;
            case DeviceName.WS:
                deviceParameters = new DeviceParameters(amcuConfig.getWeighingbaudrate(), configuration.getDataBits(amcuConfig.getWSDataBits()),
                        configuration.getParity(amcuConfig.getWSParity()), configuration.getStopBits(amcuConfig.getWSStopBits()));
                break;
            case DeviceName.RDU:
                deviceParameters = new DeviceParameters(amcuConfig.getRdubaudrate(), configuration.getDataBits(amcuConfig.getRDUDataBits()),
                        configuration.getParity(amcuConfig.getRDUParity()), configuration.getStopBits(amcuConfig.getRDUStopBits()));
                break;
            case DeviceName.PRINTER:
                deviceParameters = new DeviceParameters(amcuConfig.getPrinterBaudRate(), configuration.getDataBits(amcuConfig.getPrinterDataBits()),
                        configuration.getParity(amcuConfig.getPrinterParity()), configuration.getStopBits(amcuConfig.getPrinterStopBits()));
                break;
        }
        switch (deviceEntity.deviceType) {
            case SmartCCConstants.WIFI_UDP:
                device = new WifiUDPDevice(deviceEntity);
                break;
            case SmartCCConstants.WIFI_TCP:
                device = new WifiTCPDevice(deviceEntity, deviceDisconnectListener);
                break;
            case SmartCCConstants.USB:
                device = new WiredDevice(context, deviceEntity);
                break;
            case SmartCCConstants.USB_TVS:
                device = new TVSDevice(context, deviceEntity);
                break;
            default:
                device = new WifiUDPDevice(deviceEntity);
        }
        device.setParameters(deviceParameters);
        deviceMap.put(deviceName, device);
        return device;
    }

   /* public static UsbSerialDriver getDriver(String deviceType) {
        UsbSerialDriver driver = null;
        for (DeviceEntity device : devicesList) {
            Log.v("prober", "Driver name: " + device.deviceName + ", Device type: " + deviceType +
                    "port number " + device.portAddress);
            if (device.deviceName.equals(deviceType)) {
                driver = device.driver;
                break;
            }
        }
        return driver;
    }*/

}
