package com.devapp.devmain.devicemanager;

import com.hoho.android.usbserial.driver.UsbSerialPort;

/**
 * Created by Upendra on 5/18/2016.
 */
public class DriverConfiguration {

    public int getParity(String sessionParity) {
        int parity = UsbSerialPort.PARITY_NONE;

        switch (sessionParity) {
            case "PARITY_EVEN": {
                parity = UsbSerialPort.PARITY_EVEN;
            }
            break;
            case "PARITY_ODD": {
                parity = UsbSerialPort.PARITY_ODD;
            }
            break;
            case "PARITY_MARK": {
                parity = UsbSerialPort.PARITY_MARK;
            }
            break;
            case "PARITY_SPACE": {
                parity = UsbSerialPort.PARITY_SPACE;
            }
            break;
            default: {
                parity = UsbSerialPort.PARITY_NONE;
            }
        }
        return parity;
    }

    public String getParityForWisens(int sessionParity) {
        String parity = "N";

        switch (sessionParity) {
            case UsbSerialPort.PARITY_EVEN: {
                parity = "E";
            }
            break;
            case UsbSerialPort.PARITY_ODD: {
                parity = "O";
            }
            break;
            case UsbSerialPort.PARITY_MARK: {
                parity = "M";
            }
            break;
            case UsbSerialPort.PARITY_SPACE: {
                parity = "S";
            }
            break;
            default: {
                parity = "N";
            }

        }
        return parity;
    }

    public int getStopBits(String sessionStopBits) {
        int parity = UsbSerialPort.STOPBITS_1;
        switch (sessionStopBits) {
            case "STOP_BITS_2": {
                parity = UsbSerialPort.STOPBITS_2;
            }
            break;
            case "STOP_BITS_1.5": {
                parity = UsbSerialPort.STOPBITS_1_5;
            }
            break;
            default: {
                parity = UsbSerialPort.STOPBITS_1;
            }
        }
        return parity;
    }

    public int getDataBits(String sessionDataBits) {
        int parity = UsbSerialPort.DATABITS_8;
        switch (sessionDataBits) {
            case "DATA_BITS_5": {
                parity = UsbSerialPort.DATABITS_5;
            }
            break;
            case "DATA_BITS_6": {
                parity = UsbSerialPort.DATABITS_6;
            }
            break;

            case "DATA_BITS_7": {
                parity = UsbSerialPort.DATABITS_7;
            }
            break;
            default: {
                parity = UsbSerialPort.DATABITS_8;
            }
        }
        return parity;
    }

}
