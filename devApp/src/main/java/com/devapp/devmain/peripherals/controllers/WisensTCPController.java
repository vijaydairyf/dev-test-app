package com.devapp.devmain.peripherals.controllers;

import android.util.Log;

import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by xx on 25/6/18.
 */

public class WisensTCPController implements WisensController {

    public static final String TAG = WisensTCPController.class.getSimpleName();
    private BufferedWriter mBufferOut;
    private BufferedReader mBufferIn;
    private boolean ackReceived;
    private State mState = State.CONNECTING;


    private State getState() {
        return mState;
    }

    private void sendConMessage(Socket socket, String ip) {
        char[] buffer = new char[1024];
        try {
            mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            mBufferOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            while (!ackReceived) {
                if (mBufferOut != null) {
                    mBufferOut.write(SmartCCConstants.CON);
                    mBufferOut.flush();
                    Log.i(TAG, "TCP sent : " + SmartCCConstants.CON + " to " + ip);
                }

                Log.e(TAG, "Waiting for ACK from " + ip);
                int charsRead = mBufferIn.read(buffer);
                String serverMessage = new String(buffer).substring(0, charsRead);
                if (serverMessage != null) {
                    Log.e(TAG, "Received message: " + serverMessage + " from " + ip);
                    if (serverMessage.contains(SmartCCConstants.ACK)) {
                        ackReceived = true;
                        return;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reinitializeConnection() {
        mState = State.CONNECTING;
    }

    @Override
    public Socket startConnection(final String msg, String ip) {
        ackReceived = false;
        while (true) {
            if (getState() == State.STOPPED)
                return null;
            else {
                try {
                    InetAddress serverAddr = InetAddress.getByName(ip);
                    Log.v(TAG, "TCP Connecting to " + ip);
                    Socket socket = new Socket(serverAddr, SmartCCConstants.SERVER_PORT);
                    if (socket.isConnected() && !socket.isClosed()) {
                        Log.v(TAG, "TCP Connected to " + ip);
//                        socket.setKeepAlive(true);
                        sendConMessage(socket, ip);
                        return socket;
                    } else {
                        Log.v(TAG, "TCP socket not connected");
                    }
                } catch (IOException e) {
                    Log.v(TAG, "Unable to connect to TCP socket");
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void stopConnection() {
        Log.i(TAG, "TCP stopping probing");
        mState = State.STOPPED;

     /*   if (mBufferOut != null) {
            try {
                mBufferOut.flush();
                mBufferOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mBufferIn = null;
        mBufferOut = null;*/
    }

    private enum State {
        CONNECTING, STOPPED
    }
}
