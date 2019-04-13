package com.devapp.devmain.helper;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.devapp.devmain.devicemanager.DriverConfiguration;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;

import java.io.IOException;

/**
 * Created by u_pendra on 18/4/17.
 */

public class DeviceConnection {

    private final String TAG = "DEVICE_CONNECTION";
    DriverConfiguration driverConfiguration;
    private Context mContext;
    private UsbManager mUsbManager;
    private UsbSerialPort mUsbSerialPort = null;
    private int mBaudrate = 9600;
    private int mStopBits = 1;
    private int mDataBits = 8;
    private int mParity = 0;
    private AmcuConfig amcuConfig;

    public DeviceConnection(Context context, String deviceType) {
        this.mContext = context;
        this.mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        amcuConfig = AmcuConfig.getInstance();
        driverConfiguration = new DriverConfiguration();

        setParameters(deviceType);
    }


    private void setParameters(String deviceType) {
        if (deviceType == DeviceName.MA1 || deviceType == DeviceName.MA1) {
            mBaudrate = amcuConfig.getMa1Baudrate();
            mDataBits = driverConfiguration.getDataBits(amcuConfig.getMa1DataBits());
            mStopBits = driverConfiguration.getStopBits(amcuConfig.getMa1StopBits());
            mParity = driverConfiguration.getParity(amcuConfig.getMa1Parity());
        } else if (deviceType == DeviceName.MA2) {
            mBaudrate = amcuConfig.getMa2Baudrate();
            mDataBits = driverConfiguration.getDataBits(amcuConfig.getMa2DataBits());
            mStopBits = driverConfiguration.getStopBits(amcuConfig.getMa2StopBits());
            mParity = driverConfiguration.getParity(amcuConfig.getMa2Parity());
        } else if (deviceType == DeviceName.WS) {
            mBaudrate = amcuConfig.getWeighingbaudrate();
            mDataBits = driverConfiguration.getDataBits(amcuConfig.getWSDataBits());
            mStopBits = driverConfiguration.getStopBits(amcuConfig.getWSStopBits());
            mParity = driverConfiguration.getParity(amcuConfig.getWSParity());
        } else if (deviceType == DeviceName.PRINTER) {
            mBaudrate = amcuConfig.getPrinterBaudRate();
            mDataBits = driverConfiguration.getDataBits(amcuConfig.getPrinterDataBits());
            mStopBits = driverConfiguration.getStopBits(amcuConfig.getPrinterStopBits());
            mParity = driverConfiguration.getParity(amcuConfig.getPrinterParity());
        } else if (deviceType == DeviceName.RDU) {
            mBaudrate = amcuConfig.getRdubaudrate();
            mDataBits = driverConfiguration.getDataBits(amcuConfig.getRDUDataBits());
            mStopBits = driverConfiguration.getStopBits(amcuConfig.getRDUStopBits());
            mParity = driverConfiguration.getParity(amcuConfig.getRDUParity());
        }
    }


    public synchronized void openDeviceConnection(UsbSerialDriver usbSerialDriver, String name) {
        if (mUsbSerialPort == null) {
            UsbDeviceConnection usbDeviceConnection = getUsbDeviceConnection(usbSerialDriver);
            mUsbSerialPort = usbSerialDriver.getPorts().get(0);
            try {
                mUsbSerialPort.open(usbDeviceConnection);
                mUsbSerialPort.setParameters(mBaudrate,
                        mDataBits
                        , mStopBits,
                        mParity);
            } catch (IOException e) {
                Log.v(TAG, " Unable to read " + name + ": " + e.getMessage().toString());
                Util.displayErrorToast("Unable to read " + name + "," +
                        "Please restart the tab and try again!", mContext);
                e.printStackTrace();
            }
        }


    }

    private UsbDeviceConnection getUsbDeviceConnection(UsbSerialDriver driver) {
        UsbDeviceConnection usbDeviceConnection = mUsbManager
                .openDevice(driver.getDevice());
        return usbDeviceConnection;
    }


    public void onStopConnection(SerialInputOutputManager serialIOManager) {
        try {
            if (serialIOManager != null) {
                Log.i(TAG, "Stopping io manager ..");
                serialIOManager.stop();
                serialIOManager = null;
                try {
                    mUsbSerialPort.close();
                    mUsbSerialPort = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
