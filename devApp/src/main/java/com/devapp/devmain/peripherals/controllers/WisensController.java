package com.devapp.devmain.peripherals.controllers;

import java.net.Socket;

/**
 * Created by xx on 26/6/18.
 */

public interface WisensController {
    Socket startConnection(String msg, String ip);

    void stopConnection();
}
