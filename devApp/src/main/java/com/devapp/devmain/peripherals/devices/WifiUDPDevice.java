package com.devapp.devmain.peripherals.devices;

import android.util.Log;

import com.devapp.devmain.devicemanager.DriverConfiguration;
import com.devapp.devmain.peripherals.interfaces.DataObserver;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.peripherals.interfaces.WriteDataListener;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.devmain.user.Util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by xx on 2/2/18.
 */

public class WifiUDPDevice implements Device {

    private final String TAG = WifiUDPDevice.class.getSimpleName();
    private final int MAXBUFFER = 150;
    private DataObserver mDataObserver;
    private WriteDataListener mDataListener;
    private DeviceEntity mDeviceEntity;
    private DatagramSocket mSocket;
    private String paramCommand = "SETBAUDRATE";
    private DeviceParameters mParameters;
    private DeviceState mDeviceState = DeviceState.STOPPED;

    public WifiUDPDevice(DeviceEntity deviceEntity) {
        mDeviceEntity = deviceEntity;
        mSocket = mDeviceEntity.deviceSocket;
    }

    @Override
    public void setParameters(DeviceParameters parameters) {
        mParameters = parameters;
        final String paramMsg = paramCommand + "," + parameters.getBaudRate() + "," + parameters.getDataBits()
                + new DriverConfiguration().getParityForWisens(parameters.getParity()) + parameters.getStopBits();

        new Thread(new Runnable() {
            @Override
            public void run() {
                write(paramMsg);
            }
        }).start();

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
        if (mDeviceState != DeviceState.RUNNING) {
            new Thread() {
                @Override
                public void run() {
                    mDeviceState = DeviceState.RUNNING;
                    ByteBuffer mReadBuffer = ByteBuffer.allocate(1000);
                    DatagramPacket maDatagramPacket = new DatagramPacket(mReadBuffer.array(), mReadBuffer.array().length);
                    try {

                        Log.v(TAG, "Reading from UDP " + mDeviceEntity.deviceName);
                        while (true) {
                            if (mDeviceState != DeviceState.RUNNING)
                                break;
                            mSocket.setSoTimeout(3000);
                            try {

                                mSocket.receive(maDatagramPacket);
                                byte[] receivedBytes = new byte[maDatagramPacket.getLength()];
                                mReadBuffer.get(receivedBytes, 0, receivedBytes.length);
                                if (mDataObserver != null)
                                    mDataObserver.onDataReceived(receivedBytes);
                                Log.v(TAG, "Received data in UDP device " + new String(receivedBytes));
                                mReadBuffer.clear();
                            } catch (SocketTimeoutException e) {

                            } catch (Exception exp) {

                            }
                        }
                    } catch (SocketException e) {
//                e.printStackTrace();
                    } catch (IOException e) {
//                e.printStackTrace();
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
        Log.v(TAG, "Sending " + msg + "," + msg.getBytes().length + " bytes to: " + mDeviceEntity.deviceName);
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
                for (int i = 0; i < msgList.size(); i++) {

                    try {
                        sendPacket(msgList.get(i));
                        Thread.sleep(getDelay());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mDataListener.onWriteComplete();
            }
        }.start();
    }

    private void sendPacket(final byte[] msg) {
        Log.v(TAG, "Sending packet size: " + msg.length + ", Max buffer: " + MAXBUFFER);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InetAddress inetAddress = InetAddress.getByName(mDeviceEntity.ipAddress);
                    DatagramPacket maDatagramPacket = new DatagramPacket(msg, msg.length, inetAddress, 5683);
                    mSocket.send(maDatagramPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private enum DeviceState {
        RUNNING, STOPPED
    }
}
