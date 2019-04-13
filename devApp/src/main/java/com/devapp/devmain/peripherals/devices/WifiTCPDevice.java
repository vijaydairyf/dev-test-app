package com.devapp.devmain.peripherals.devices;

import android.util.Log;

import com.devapp.devmain.devicemanager.DriverConfiguration;
import com.devapp.devmain.peripherals.interfaces.DataObserver;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.peripherals.interfaces.WriteDataListener;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.devmain.user.Util;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by xx on 17/7/18.
 */

public class WifiTCPDevice implements Device {

    private final String TAG = WifiTCPDevice.class.getSimpleName();
    private final int MAXBUFFER = 200;
    private DataObserver mDataObserver;
    private WriteDataListener mDataListener;
    private DeviceEntity mDeviceEntity;
    private AmcuConfig amcuConfig;
    //    private Socket mSocket;
//    private PrintWriter mBufferOut;
//    private BufferedWriter mBufferOut;
    private DataOutputStream mBufferOut;
    private String paramCommand = "SETBAUDRATE";
    private DeviceParameters mParameters;
    private DeviceState mDeviceState = DeviceState.STOPPED;
    private ErrorListener mListener;

    public WifiTCPDevice(DeviceEntity deviceEntity, ErrorListener listener) {
        mDeviceEntity = deviceEntity;
        amcuConfig = AmcuConfig.getInstance();
//        mSocket = deviceEntity.tcpSocket;
        mListener = listener;
        initializeOutputStream();
    }

    private void initializeOutputStream() {
        try {
//            mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mDeviceEntity.tcpSocket.getOutputStream())), true);
//            mBufferOut = new BufferedWriter(new OutputStreamWriter(mDeviceEntity.tcpSocket.getOutputStream()));
            mBufferOut = new DataOutputStream(mDeviceEntity.tcpSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private long getDelay() {
        long delay = 500;
        if ("Printer".equalsIgnoreCase(mDeviceEntity.deviceName)
                && AmcuConfig.getInstance().getPrinterBaudRate() == 1200) {
            delay = 1500;
        }
        return delay;
    }

    @Override
    public void setParameters(DeviceParameters parameters) {
        mParameters = parameters;
        String paramMsg = paramCommand + "," + parameters.getBaudRate() + "," + parameters.getDataBits()
                + new DriverConfiguration().getParityForWisens(parameters.getParity()) + parameters.getStopBits();
        write(paramMsg);
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
    public void read() {
        Log.v(TAG, "Reading from TCP " + mDeviceEntity.deviceName);
        if (mDeviceState != DeviceState.RUNNING) {
            new Thread() {
                @Override
                public void run() {
                    mDeviceState = DeviceState.RUNNING;
                    try {
                        if (mDeviceEntity.tcpSocket.isConnected() && !mDeviceEntity.tcpSocket.isClosed()) {
//                            mSocket.setKeepAlive(true);
//                            BufferedReader mBufferIn = new BufferedReader(new InputStreamReader(mDeviceEntity.tcpSocket.getInputStream()));
                            BufferedInputStream inputStream = new BufferedInputStream(mDeviceEntity.tcpSocket.getInputStream());
                            while (true) {
                                if (mDeviceState != DeviceState.RUNNING) {
                                    Log.v(TAG, "Stop reading from TCP " + mDeviceEntity.deviceName);
                                    break;
                                }

                                byte[] msgArray = new byte[1024];
                                int bytesRead = inputStream.read(msgArray);
                                Log.v(TAG, "READ BYTES:" + bytesRead);
                                byte[] finalBytes = new byte[bytesRead];
                                System.arraycopy(msgArray, 0, finalBytes, 0, bytesRead);
                                if (mDataObserver != null && finalBytes.length > 0) {
                                    Log.e(TAG, "Received data in TCP device: " + new String(finalBytes));
                                    mDataObserver.onDataReceived(finalBytes);
                                }
                            }
                            if (!mDeviceEntity.tcpSocket.isClosed()) {
                                Log.v(TAG, "Closing socket for " + mDeviceEntity.deviceName);
                                mDeviceEntity.tcpSocket.close();
                            }
                        } else {
                            Log.v(TAG, "Socket disconnected");
                            if (mListener != null)
                                mListener.onError("Socket disconnected", mDeviceEntity);
                        }
                    } catch (Exception e) {
                        Log.v(TAG, "Socket exception while reading");
                        e.printStackTrace();
                        if (mListener != null)
                            mListener.onError(e, mDeviceEntity);
                    }
                }
            }.start();
        }

    }

    @Override
    public void unregisterObserver() {
        Log.v(TAG, "stop reading for " + mDeviceEntity.deviceName);
        mDataObserver = null;
    }

    @Override
    public void closeConnection() {
        Log.v(TAG, "closing connection for " + mDeviceEntity.deviceName);
        mDeviceState = DeviceState.STOPPED;

    }

    public void write(String msg) {
        Log.v(TAG, "Sending " + msg + "," + msg.getBytes().length + " bytes");
        write(msg.getBytes());
    }

    @Override
    public void write(byte[] msg) {
        Log.v(TAG, "Data to write: " + new String(msg));
        sendPacket(msg);
    }

    @Override
    public void writeAsync(final byte[] msg) {

        Log.v(TAG, "Async Data to write: " + new String(msg));
        new Thread() {
            @Override
            public void run() {

                List<byte[]> msgList = Util.divideArray(msg, MAXBUFFER);

                try {
                    for (int i = 0; i < msgList.size(); i++) {
                        sendPacket(msgList.get(i));
                        Thread.sleep(getDelay());
                    }
//                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mDataListener.onWriteComplete();
            }
        }.start();
    }

    private void sendPacket(byte[] msg) {
        Log.v(TAG, "Sending packet size: " + msg.length + ", Max buffer: " + MAXBUFFER + " , Delay: " + getDelay());

        if (mBufferOut != null) {
            try {
//                mBufferOut.write(msg);
                mBufferOut.write(msg, 0, msg.length);
                mBufferOut.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.i(TAG, "sent : " + new String(msg) + " to: " + mDeviceEntity.deviceName + ":" + mDeviceEntity.tcpSocket);
        }
    }

    private enum DeviceState {
        RUNNING, STOPPED
    }


}
