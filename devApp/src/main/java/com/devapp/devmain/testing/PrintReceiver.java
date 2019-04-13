package com.devapp.devmain.testing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.peripherals.factories.DeviceFactory;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.util.logger.Log;

/**
 * Created by x on 23/3/18.
 */

public class PrintReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Device device = DeviceFactory.getDevice(context, DeviceName.PRINTER);
        String printData = intent.getStringExtra("printData");
        Log.d("bcp", "Received Data :" + printData);
        printData = printData + "\n";
        if (device != null) {
            device.read();
            device.writeAsync(printData.getBytes());
        }
    }
}
