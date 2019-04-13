package com.devapp.devmain.tvsprinter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.andprn.port.android.USBPort;

import java.util.HashMap;
import java.util.Iterator;

public class CustomizedUsbPort extends USBPort {
    public static final int POS_PRINTER = 0;
    public static final int MOBILE_PRINTER = 2;
    private static final String TAG = "USBPORT";
    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private static final int VID = 3701;
    private static final int[] PID = {4353};
    public boolean isUsb;
    UsbDevice usbCust;
    private UsbManager mUsbManager;
    private PendingIntent mPermissionIntent;

    /**
     * @deprecated
     */
    public CustomizedUsbPort(UsbManager usbManager) {
        super(usbManager);
        this.mUsbManager = usbManager;
    }

    public CustomizedUsbPort(UsbManager usbManager, Context context) {
        super(usbManager, context);
        this.mUsbManager = usbManager;
        this.mPermissionIntent = PendingIntent.getBroadcast(context, 0,
                new Intent("com.android.example.USB_PERMISSION"), 268435456);
    }

    /**
     * @deprecated
     */
    public UsbDevice search_device(int model) {
        HashMap usblist = this.mUsbManager.getDeviceList();
        Iterator iterator = usblist.keySet().iterator();
        UsbDevice usbDev = null;

        if ((model < 0) || (model > 2))
            model = 0;
        while (iterator.hasNext()) {
            usbDev = (UsbDevice) usblist.get(iterator.next());
            if ((usbDev.getVendorId() == 3701)
                    && (usbDev.getProductId() == PID[model])) {
                Log.i("USBPORT",
                        "USB Connected. VID "
                                + Integer.toHexString(usbDev.getVendorId())
                                + ", PID "
                                + Integer.toHexString(usbDev.getProductId()));
                usbCust = usbDev;
                usbDev = null;
                isUsb = true;

            } else if (!isUsb) {
                usbDev = null;
                usbCust = null;
            }

        }
        return usbCust;
    }

}
