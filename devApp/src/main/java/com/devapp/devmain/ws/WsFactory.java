package com.devapp.devmain.ws;

import android.content.Context;

import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.peripherals.factories.DeviceFactory;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.server.AmcuConfig;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by x on 13/2/18.
 */

public class WsFactory {

    static Map<String, WsManager> wsManagerMap;

    static {
        wsManagerMap = new HashMap<>();
    }

    public static WsManager getWs(String deviceName, Context context) {
        if (wsManagerMap.get(deviceName) != null) {
            return wsManagerMap.get(deviceName);
        } else {
            WsManager wsManager = null;
            Device device = DeviceFactory.getDevice(context, deviceName);
            WsParams wsParams = getWsParams(deviceName);
            switch (deviceName) {
                case DeviceName.WS:
                default: {
                    wsManager = new WsManagerImpl(context, device, wsParams);
                }
            }
            return wsManager;
        }
    }

    //    TODO: Make litre command and kg command configurable
    private static WsParams getWsParams(String deviceName) {
        WsParams wsParams = null;
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        switch (deviceName) {
            case DeviceName.WS:
            default: {
                wsParams = new WsParams();
                wsParams.setModel(amcuConfig.getWeighingScale());
                wsParams.setPrefix(amcuConfig.getWeighingPrefix());
                wsParams.setSuffix(amcuConfig.getWeighingSuffix());
                wsParams.setSeparator(amcuConfig.getWeighingSeperator());
                wsParams.setTareCommand(StringEscapeUtils.unescapeJava(amcuConfig.getTareCommand()));
                wsParams.setLitreCommand(amcuConfig.getWsLitreCommand());
                wsParams.setKgCommand(amcuConfig.getWsKgCommand());
                wsParams.setIgnoreThreshold(amcuConfig.getWsIgnoreThreshold());
                break;
            }
        }
        return wsParams;
    }

    public static void resetManagers() {
        wsManagerMap.clear();
    }
}
