package com.devapp.smartcc.entityandconstants;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.peripherals.network.ScanResult;
import com.devapp.devmain.usb.DeviceEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Upendra on 10/5/2016.
 */
public class SmartCCConstants {

    public static final int SERVER_PORT = 5683;
    public static final String SDCARD_PATH = "/mnt/extsd";
    public static final String DEFAULT_AGENT_ID = "NA";
    public static final int DEFAULT_MILK_STATUS = 1;
    public static final String RATECHART_TYPE_SOUR = "SOUR";
    public static final String LIST_SEPARATOR = "#";
    public static final String LIST_SUB_SEPERATOR = "|";
    public static final String SELECT_ALL = "ALL";
    public static final int DEFAULT_RATE_CALC_DEVICE = 1;
    public static final String DEFAULT_MA_NAME = "NA";
    public static final String KG_WEIGHT = "KG";
    public static final String LTR_WEIGHT = "LTR";
    public static final String ACTUAL_WEIGHT = "KGORLTR";
    public static final String NO_RATECHART = "NO_DEVICE_RATE_CHART";
    public static final int HTTP_PORT = 80;
    public static final int HTTPS_PORT = 443;
    public static final String MA = "MILK ANALYZER";
    public static final String WS = "WEIGHING MACHINE";
    public static final String RDU = "RDU";
    public static final String PRINTER = "PRINTER";
    public static final String MA_AVAILABLE = "maAvailable";
    public static final String WS_AVAILABLE = "wsAvailable";
    public static final String RDU_AVAILABLE = "rduAvailable";
    public static final String PRINTER_AVAILABLE = "printerAvailable";
    public static final String APPTYPE = "APPTYPE";
    public static final String USB_TVS = "USB_TVS";
    public static final String USB = "USB";
    public static final String WIFI = "WIFI";
    public static final String MIXED = "MIXED";
    public static final String PROBER = "PROBER";
    public static final String ESSAE = "ESSAE";
    public static final String CLEANING = "CLEANING";
    public static final String CALIBRATION = "CALIBRATION";
    public static final String CORRECTION = "CORRECTION";
    public static final String ESSAE_PASS = "<PASS";
    public static final String CLEANHISTORYALL = ">CLEANHIST ALL\r\n";
    public static final String CLEANRINSE = ">CLEAN RINSE\r\n";
    public static final String CLEANHISTORYLAST = ">CLEANHIST LAST\r\n";
    public static final String CALIBHISTORYALL = ">CALIBHIST ALL\r\n";
    public static final String CALIBHISTORYLAST = ">CALIBHIST LAST\r\n";
    public static final String CON = "CON";
    public static final String ACK = "ACK";
    public static final String maIp = "192.168.43.101";
    public static final String wsIp = "192.168.43.111";
    public static final String rduIp = "192.168.43.121";
    public static final String printerIp = "192.168.43.131";
    public static final String ACCEPT = "Accept";
    public static final String REJECT = "Reject";
    public static final String AUTO = "Auto";
    public static final String MANUAL = "Manual";
    public static final int COLLECTION_STARTED = 0;
    public static final int QUALITY_DONE = 1;
    public static final int QUANTITY_DONE = 2;
    public static final int NEXT_CAN_STARTED = 3;
    public static final int PRINTER_DONE = 4;
    public static final int RDU_DONE = 5;
    public static final int SAVED_IN_DB = 6;
    public static final int SAVED_UNCOMMITTED_RECORD = 7;
    public static final int RATE_FROM_CLOUD = 0;
    public static final int RATE_FROM_DEVICE = 1;
    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static final String DEVICE_DISCONNECT = "device.disconnected";
    public static final String DEVICE_CONNECT = "device.connected";
    public static final String MA_CONNECTED = "ma.connected";
    public static final String WS_CONNECTED = "ws.connected";
    public static final String RDU_CONNECTED = "rdu.connected";
    public static final String PRINTER_CONNECTED = "printer.connected";
    public static final String DEVICES_DISCOVERED = "device.discovered";
    public static final String DEVICE_LIST = "deviceList";
    public static final String WIFI_UDP = "WIFI_UDP";
    public static final String WIFI_TCP = "WIFI_TCP";
    public static final String ALL_DEVICES = "ALL_DEVICES";
    public static final String PING_STATUS = "ping.status";
    public static final String MA_PING = "maPing";
    public static final String WS_PING = "wsPing";
    public static final String RDU_PING = "rduPing";
    public static final String PRINTER_PING = "printerPing";
    public static String AGENT_UPDATE_URI;
    public static String TRUCK_UPDATE_URI;
    public static String USER_UPDATE_URI;
    public static long TRUCK_TABLE_SIZE = 999999l;
    public static int MIN_NUM_OF_CANS = 0;
    public static int MAX_NUM_OF_CANS = 200;
    public static String KEY_OLD_VALUE = "old";
    public static String KEY_NEW_VALUE = "new";
    public static String KEY_REAL_VALUE = "REAL";
    public static double MIN_TEMP = -10.0;
    public static double MAX_TEMP = 120.0;
    public static double MIN_CLR = 0;
    //Newly added constants
    public static double MAX_CLR = 100;
    public static String SNF = "SNF";
    public static String CLR = "CLR";
    public static String RATECHART_TYPE_PROTEIN = "PROTEIN";
    public static String appType = "";
    public static String unsentCleaningData = "";
    public static boolean shouldContinue = true;
    public static String DEVICE_NAME = "deviceName";
    public static String FINISH_ACTIVITY = "finish.activity";
    public static String TIMEOUT = "timeout";
    public static ArrayList<ScanResult> clients;
    public static ArrayList<DeviceEntity> devicesList = new ArrayList<>();
    public static ReportEntity reportEntity;
    public static HashMap<String, DeviceEntity> deviceEntityMap = new HashMap<>();
    public static String[] ssidList = {"smartiot", "smartudp",
            "smartrak", "smartmilk", "smartsens", "smartmoo"
            , "smartcol", "cloudtrak", "SmartCC7"};
    public static String[] passwordList = {"V2M*GgG6R#", "$7!Af4YvGm", "WRe9Cd6$%K",
            "DmYdcF@7zf",
            "g?8FwAy%79", "44w?Q%4dVJ"
            , "cu8*Uh2*Z7", "dA&zbC4FrT", "63KUajN3"};

    public static HashMap<String, Integer> getRejectMap() {
        HashMap<String, Integer> mapReject = new HashMap<>();
        mapReject.put("GOOD", 1);
        mapReject.put("SOUR", 2);
        mapReject.put("CURD", 3);
        mapReject.put("SOUR VEHICLE FAULT", 4);

        return mapReject;

    }

    public static int getRejectValue(String key) {
        HashMap<String, Integer> mapReject = getRejectMap();

        Iterator mapIterator = mapReject.entrySet().iterator();

        while (mapIterator.hasNext()) {
            Map.Entry entry = (Map.Entry) mapIterator.next();

            if (entry.getKey().equals(key)) {
                return (int) entry.getValue();
            }
        }

        return 1;
    }

    public static String getApptype(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String type = prefs.getString(APPTYPE, USB);
        return type;
    }

    public static void setApptype(String type, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(APPTYPE, type).commit();
    }


}
