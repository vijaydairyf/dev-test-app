package com.devapp.devmain.peripherals.probers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.peripherals.interfaces.Prober;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;


/**
 * Created by xx on 22/1/18.
 */

public class WiredProber implements Prober {
    private static final int MESSAGE_REFRESH = 101;
    private static final long REFRESH_TIMEOUT_MILLIS = 3000;
    static Context mContext;
    static WiredProber instance;
    private UsbManager mUsbManager;
    private List<DeviceEntity> deviceEntities;
    private LinkedHashMap<Integer, String> deviceMap;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_REFRESH:
                    refreshDeviceList();
                    mHandler.sendEmptyMessageDelayed(MESSAGE_REFRESH,
                            REFRESH_TIMEOUT_MILLIS);

                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private WiredProber() {
        initializeVariables();
    }

    public static WiredProber getInstance(Context context) {
        mContext = context;
        if (instance == null)
            instance = new WiredProber();
        return instance;
    }

    private void initializeVariables() {
        deviceEntities = new ArrayList<DeviceEntity>();
        deviceMap = new LinkedHashMap<>();
        mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);
        setConfiguredDevicesMap();
    }

    private void setConfiguredDevicesMap() {

        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        String[] stringDeviceArrayTemp = {DeviceName.MA1,
                DeviceName.NO_CONNECTION, DeviceName.RDU,
                DeviceName.WS, DeviceName.PRINTER};
        stringDeviceArrayTemp[0] = amcuConfig.getKeyDevicePort1();
        stringDeviceArrayTemp[1] = amcuConfig.getKeyDevicePort2();
        stringDeviceArrayTemp[2] = amcuConfig.getKeyDevicePort3();
        stringDeviceArrayTemp[3] = amcuConfig.getKeyDevicePort4();
        stringDeviceArrayTemp[4] = amcuConfig.getKeyDevicePort5();
        int k = 0;
        for (int i = 0; i < stringDeviceArrayTemp.length; i++) {
            if (!stringDeviceArrayTemp[i].equalsIgnoreCase(DeviceName.NO_CONNECTION)) {
                deviceMap.put(k++, stringDeviceArrayTemp[i]);
            }
        }
    }

    @Override
    public void stopProbing() {
        mHandler.removeMessages(MESSAGE_REFRESH);
    }

    @Override
    public ArrayList<DeviceEntity> getDevices() {
        Log.v("prober", "Wired devices found: " + deviceEntities.size());
        return (ArrayList<DeviceEntity>) deviceEntities;
    }

    @Override
    public void startProbing(String deviceName) {
        Log.v(PROBER, "starting wired probing");
        mHandler.sendEmptyMessage(MESSAGE_REFRESH);
    }

    private void refreshDeviceList() {
        new AsyncTask<Void, Void, List<DeviceEntity>>() {
            @Override
            protected List<DeviceEntity> doInBackground(Void... params) {

                SystemClock.sleep(500);

                final List<DeviceEntity> result = new ArrayList<DeviceEntity>();

                final List<UsbSerialDriver> drivers = UsbSerialProber
                        .getDefaultProber().findAllDrivers(mUsbManager);

                for (final UsbSerialDriver driver : drivers) {
                    DeviceEntity deviceEntity = new DeviceEntity();
                    Log.v(PROBER, "Vendor ID: " + driver.getDevice().getVendorId());
                    Log.v(PROBER, "Product ID: " + driver.getDevice().getProductId());
                    if (driver.getDevice().getVendorId() == 3701)
                        deviceEntity.deviceType = SmartCCConstants.USB_TVS;
                    else
                        deviceEntity.deviceType = SmartCCConstants.USB;
                    requestPermission(driver.getDevice());
                    deviceEntity.driver = driver;
                    String portNum = driver.getDevice().getDeviceName();
                    portNum = portNum.substring(portNum.lastIndexOf("/") + 1);

                    deviceEntity.portAddress = Integer.parseInt(portNum);
                    Log.v("prober", "WP, Port number: " + deviceEntity.portAddress +
                            " Driver name: " + driver.getDevice().getDeviceName()
                            + " Device :" + deviceEntity.deviceName);
                    result.add(deviceEntity);
                }
                return result;
            }

            @Override
            protected void onPostExecute(List<DeviceEntity> result) {
                Collections.sort(result);

                if (result.size() > 4) {
                    result.remove(result.size() - 2);
                }

                deviceEntities.clear();
                deviceEntities.addAll(result);

                if (result != null && result.size() > 0)
                    setDeviceNames();
            }
        }.execute((Void) null);
    }

    private void setDeviceNames() {
        Collections.sort(deviceEntities);

        for (int i = 0; i < deviceEntities.size(); i++) {
            deviceEntities.get(i).deviceName = deviceMap.get(i);
        }
    }

    private void requestPermission(UsbDevice device) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext, 0, new Intent(SmartCCConstants.ACTION_USB_PERMISSION),
                0);
        mUsbManager.requestPermission(device, pendingIntent);
    }

}
