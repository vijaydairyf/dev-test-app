package com.devapp.devmain.peripherals.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by xx on 15/9/17.
 */

public class IpController {

    public static boolean ipAckReceived;
    static Context mContext;
    static IpController instance;
    int bufferSize = 1000;
    int maxBuffer = 999;
    int wisensPort = 5683;
    int wisensTimeOut = 1000;
    DatagramSocket wisensSocket;
    AckReceiveListener ackReceiveListener;
    ChangeIpTask changeIpTask;
    String newWisensIp, oldWisensIp;

    private IpController() {

        try {
            wisensSocket = new DatagramSocket();
        } catch (SocketException e) {
//            e.printStackTrace();
        }
    }

    public static IpController getInstance(Context context) {
        mContext = context;
        if (instance == null)
            instance = new IpController();
        return instance;
    }

    public void setAckListener(AckReceiveListener ackListener) {
        this.ackReceiveListener = ackListener;
    }

    public void stopAckListener() {
        this.ackReceiveListener = null;
    }

    public void stopWisensMsg() {
        if (changeIpTask != null)
            changeIpTask.cancel(true);
        ipAckReceived = true;
    }

    public void changeWisensIp(String oldIp, String newIp) {
        ipAckReceived = false;
        this.oldWisensIp = oldIp;
        this.newWisensIp = newIp;
        changeIpTask = new ChangeIpTask();
        changeIpTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                "SETIPADDRESS," + newIp + ",255.255.255.0,192.168.43.1", oldIp);
    }

    public interface AckReceiveListener {
        void onWisensAckReceived();
    }

    class ChangeIpTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            //udp sender code
            String msg = params[0];
            String deviceIp = params[1];
            Log.v("UDP", "Sending " + msg + " to : " + deviceIp);
            byte[] message = msg.getBytes();
            DatagramPacket datagramPacket = null;
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getByName(deviceIp);
                datagramPacket = new DatagramPacket(message, message.length,
                        inetAddress, wisensPort);
                wisensSocket.send(datagramPacket);
            } catch (SocketException e) {
//                e.printStackTrace();
            } catch (IOException e) {
//                e.printStackTrace();
            }
            if (datagramPacket != null)
                msg = new String(message, 0, datagramPacket.getLength());
            return msg;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new AckReceiverTask().execute();
        }
    }

    class AckReceiverTask extends AsyncTask<Void, String, String> {

        @Override
        protected String doInBackground(Void... params) {
            //udp receiver code
            String text = "";
            byte[] bytes = new byte[1000];
            DatagramPacket p = new DatagramPacket(bytes, bytes.length);
            try {
                wisensSocket.setSoTimeout(wisensTimeOut);
                wisensSocket.receive(p);

                if (isCancelled())
                    return "";
                text = new String(bytes, 0, p.getLength());
            } catch (SocketException e) {
//                e.printStackTrace();
            } catch (IOException e) {
//                e.printStackTrace();
            }
            return text;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && s.startsWith("ACK")) {
                //executed only when ACK is received from hardware
                Log.v("udp", "Wisens ACK received for : " + newWisensIp);
                ipAckReceived = true;
                ackReceiveListener.onWisensAckReceived();
            } else {
                //when unwanted data is received or connection times out, command is sent again to the device and
                // a new connection is established with the hardware
                if (!ipAckReceived) {
                    Log.v("udp", "Wisens ACK NOT received for: " + newWisensIp);
                    changeWisensIp(oldWisensIp, newWisensIp);
                }
            }

        }
    }

}
