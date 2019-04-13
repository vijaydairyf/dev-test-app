package com.devapp.devmain.peripherals.controllers;

import android.util.Log;

import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by xx on 15/9/17.
 */

public class WisensUDPController {

    private final int TIMEOUT = 3000;
    private final String TAG = WisensUDPController.class.getSimpleName();
    private int port = 5683;
    private State mState = State.STOPPED;

    private State getState() {
        return mState;
    }

    public void stopConnection() {
        mState = State.STOPPED;
    }

    public DatagramSocket startConnection(final String msg, final String ip) {
        mState = State.CONNECTING;
        byte[] message = msg.getBytes();
        DatagramPacket datagramPacket = null;
        InetAddress inetAddress;
        Log.v(TAG, "Creating UDP connection with : " + ip);
        try {
            inetAddress = InetAddress.getByName(ip);
            datagramPacket = new DatagramPacket(message, message.length, inetAddress, port);
            DatagramSocket socket = new DatagramSocket();
            while (true) {
                if (getState() != State.CONNECTING)
                    return null;
                try {

                    Log.v(TAG, "Sending " + msg + " to : " + ip);
                    socket.send(datagramPacket);
                    socket.setSoTimeout(TIMEOUT);
                    socket.receive(datagramPacket);
                    String dataReceived = new String(message, 0, datagramPacket.getLength());
                    if (dataReceived.startsWith(SmartCCConstants.ACK)) {
                        Log.v(TAG, "ACK received for " + ip);
                        return socket;
                    }
                } catch (SocketException e) {
//                    e.printStackTrace();
                } catch (IOException e) {
//                    e.printStackTrace();
                }

            }

        } catch (SocketException e) {
//                e.printStackTrace();
        } catch (IOException e) {
//                e.printStackTrace();
        }
        return null;
    }

    private enum State {
        CONNECTING, CONNECTED, STOPPED
    }

}
