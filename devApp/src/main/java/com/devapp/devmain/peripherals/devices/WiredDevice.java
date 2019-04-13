package com.devapp.devmain.peripherals.devices;

import android.content.Context;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.devapp.devmain.peripherals.interfaces.DataObserver;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.peripherals.interfaces.WriteDataListener;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.devmain.user.Util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by xx on 2/2/18.
 */

public class WiredDevice implements Device {
    private static final int MAXBUFFER = 20;
    private final String TAG = "WIRED_DEVICE";
    Future future;
    private Context mContext;
    private WriteDataListener mDataListener;
    private DataObserver mDataObserver;
    private final SerialInputOutputManager.Listener mListener = new SerialInputOutputManager.Listener() {

        @Override
        public void onRunError(Exception e) {
            Log.d(TAG, "Runner stopped.");
        }

        @Override
        public void onNewData(final byte[] data) {
//            Log.v(PROBER, "Data in Device:" + new String(data));
            if (mDataObserver != null)
                mDataObserver.onDataReceived(data);
        }
    };
    private int counter = 0;
    private UsbSerialPort usbSerialPort;
    private UsbManager mUsbManager;
    private DeviceParameters mParameters;
    private ExecutorService mExecutor;
    private UsbSerialDriver mDriver;
    private DeviceEntity mDeviceEntity;
    private SerialInputOutputManager mSerialIoManager;

    public WiredDevice(Context context,
                       DeviceEntity deviceEntity) {
        mContext = context;
        mDeviceEntity = deviceEntity;
        mDriver = deviceEntity.driver;
        Log.v(TAG, "Initializing Driver for : " + mDeviceEntity.deviceType + ", : " + mDriver);
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
    }

    @Override
    public void registerObserver(DataObserver dataObserver) {
        mDataObserver = dataObserver;
    }

    @Override
    public void registerListener(WriteDataListener dataListener) {
        mDataListener = dataListener;
    }

    @Override
    public DeviceEntity getDeviceEntity() {
        return mDeviceEntity;
    }

    @Override
    public DeviceParameters getDeviceParams() {
        return mParameters;
    }

    @Override
    public void setParameters(DeviceParameters parameters) {
        mParameters = parameters;
    }

    @Override
    public void read() {
        try {
            openWiredConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openWiredConnection() throws IOException {
        //To avoid creating multiple instances of serialIOManager
        if (usbSerialPort == null) {
            try {
                openDeviceConnection();
                if (mDeviceEntity.deviceMode != CollectionConstants.WRITE_ONLY) {
                    startIoManager(usbSerialPort);
                }
                counter = 0;
            } catch (Exception e) {
                e.printStackTrace();
                Log.v(TAG, "opening wired device connection failed");
                Crashlytics.logException(e);
                Util.displayErrorToast("Press F10 to restart the app!", mContext);

            }
        }
    }

    public void openDeviceConnection() throws IOException {
        if (usbSerialPort == null) {

            Log.v(TAG, "Opening device connection:" + mDriver.getDevice().getDeviceName() +
                    ",device type" + mDeviceEntity.deviceName);
            UsbDeviceConnection usbDeviceConnection = mUsbManager
                    .openDevice(mDriver.getDevice());
            Log.v(TAG, "Device open successfully:" + mDriver.getDevice().getDeviceName() +
                    ",device type" + mDeviceEntity.deviceName);

            if (usbDeviceConnection != null) {

                usbSerialPort = mDriver.getPorts().get(0);
                Log.v(TAG, "Opening port connection:" + mDriver.getDevice().getDeviceName() +
                        ",port " + usbSerialPort);
                usbSerialPort.open(usbDeviceConnection);
                Log.v(TAG, "Port open successfully:" + mDriver.getDevice().getDeviceName() +
                        ",port " + usbSerialPort);
                if (mDriver.getDevice().getProductId() == 29987) {
                    // No parameter set for weighing (CH340)
                    usbSerialPort.setParameters(mParameters.getBaudRate(), 0, 1, 0);
                } else {
                    usbSerialPort.setParameters(mParameters.getBaudRate(), mParameters.getDataBits(),
                            mParameters.getStopBits(), mParameters.getParity());
                }


            }
        }
    }

    private void startIoManager(UsbSerialPort sPort) {

        if (sPort != null) {
            mExecutor = Executors.newSingleThreadExecutor();
            Log.i(TAG, "Starting io manager for " + mDeviceEntity.deviceName);
            mSerialIoManager = new SerialInputOutputManager(sPort, mListener);
            future = (Future) mExecutor.submit(mSerialIoManager);

        } else {
            stopIoManager();
            Log.i(TAG, "Stopped io manager for " + mDeviceEntity.deviceName);
            Util.displayErrorToast("Unable to open connection!, Restart the tab!", mContext);


        }
    }


    private void stopIoManager() {

        if (mDeviceEntity.deviceMode == CollectionConstants.WRITE_ONLY && usbSerialPort != null) {
            try {
                usbSerialPort.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                usbSerialPort = null;
            }
        }
        if (mSerialIoManager != null) {
            try {
                Log.i(TAG, "Stopping io manager for " + mDeviceEntity.deviceName);
                mSerialIoManager.stop();
                future.get(10, TimeUnit.SECONDS);
                usbSerialPort.close();
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            } finally {
                mSerialIoManager = null;
                usbSerialPort = null;
//                    future.cancel(true);
                if (!mExecutor.isShutdown()) {
                    mExecutor.shutdownNow();
                }
            }
        }
    }


    @Override
    public void unregisterObserver() {
        mDataObserver = null;
        //only in case of wired device, shuts down the serial IO.(closes the USB connection)
        stopIoManager();
    }

    @Override
    public void closeConnection() {

    }

    @Override
    public void write(String msg) {
        byte[] tempBytes = msg.getBytes(Charset.forName("UTF-8"));
        write(tempBytes);
    }

    @Override
    public void writeAsync(final byte[] data) {

        Log.v("prober", "Async Data to write: " + new String(data));

        new Thread() {

            @Override
            public void run() {
                List<byte[]> msgList = Util.divideArray(data, MAXBUFFER);
                for (int i = 0; i < msgList.size(); i++) {
                    sendPacket(msgList.get(i));

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //This delay to support Amrita dairy printer, As for 1200 baudrate
                //onWriteComplete was getting called before writing the last packet
                doPrinterDelay(3000);

                if (mDataListener != null) {
                    mDataListener.onWriteComplete();
                }
            }
        }.start();
    }


    private void doPrinterDelay(long delay) {
        try {
            if ("Printer".equalsIgnoreCase(mDeviceEntity.deviceName)
                    && AmcuConfig.getInstance().getPrinterBaudRate() == 1200) {
                Thread.sleep(delay);
            }
        } catch (Exception e) {

        }
    }


    @Override
    public void write(final byte[] data) {
        Log.v(TAG, "Data to write: " + new String(data));

        sendPacket(data);

    }

    //need to handle exception appropriately
    private void sendPacket(byte[] msg) {
        Log.v(TAG, "Sending packet size: " + msg.length + ", Max buffer: " + MAXBUFFER);
        if (usbSerialPort != null) {
            String exceptionMessage = "exp";

            try {
                usbSerialPort.write(msg, 3000);
                Thread.sleep(50);
            } catch (Exception e) {
                if (e != null) {
                    exceptionMessage = e.getMessage();
                }
            } finally {
                if (exceptionMessage.contains("java.io.IOException: Error writing")) {
                    Util.displayErrorToast("Please restart the attached devices", mContext);
                }
            }
        } else {
//            Util.displayErrorToast("Please connect with device " + mDeviceEntity.deviceName, mContext);
        }

    }
}
