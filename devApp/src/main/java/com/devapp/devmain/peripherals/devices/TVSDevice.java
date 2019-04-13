package com.devapp.devmain.peripherals.devices;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.andprn.port.android.USBPortConnection;
import com.devapp.devmain.peripherals.interfaces.DataObserver;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.peripherals.interfaces.WriteDataListener;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.tvsprinter.ConnectionInfo;
import com.devapp.devmain.tvsprinter.CustomizedUsbPort;
import com.devapp.devmain.tvsprinter.Sample;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.AppConstants;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.io.UnsupportedEncodingException;
import java.util.List;


/**
 * Created by xx on 12/2/18.
 */

public class TVSDevice implements Device {
    private static final int MAXBUFFER = 200;
    private CustomizedUsbPort port;
    private DeviceEntity mDeviceEntity;
    private DeviceParameters mParameters;
    private WriteDataListener mDataListener;
    private USBPortConnection printerConnection;
    private int prn;
    private Context mContext;
    private Sample sample;

    public TVSDevice(Context context, DeviceEntity deviceEntity) {
        mContext = context;
        mDeviceEntity = deviceEntity;
    }

    @Override
    public void registerListener(WriteDataListener dataListener) {
        mDataListener = dataListener;
    }

    @Override
    public DeviceEntity getDeviceEntity() {
        return mDeviceEntity;
    }

    @Override
    public DeviceParameters getDeviceParams() {
        return mParameters;
    }

    @Override
    public void read() {
        if (printerConnection == null) {
            Log.v(SmartCCConstants.PROBER, "Opening TVS Printer");
            prn = CustomizedUsbPort.POS_PRINTER;

            port = new CustomizedUsbPort((UsbManager) mContext.getSystemService(Context.USB_SERVICE), mContext);
            // Retry
            for (int i = 0; (i < 10) && (printerConnection == null); i++) {
                UsbDevice usbDevice = port.search_device(prn);
                if (usbDevice != null) {
                    Log.v(SmartCCConstants.PROBER, "Trying for TVS Connection");
//                Util.displayErrorToast("Trying for TVS connection ", mContext);
                    printerConnection = port.connect_device(usbDevice);
                    break;
                }
            }
            (ConnectionInfo.getInstance())
                    .setConnection(printerConnection);
        }
    }

    @Override
    public void unregisterObserver() {
        printerConnection = null;
        sample = null;
        ConnectionInfo.getInstance().setConnection(null);

    }

    @Override
    public void closeConnection() {

    }

    @Override
    public void writeAsync(final byte[] data) {
        final String msg = new String(data) + "\n";

        Log.v("prober", "TVS Async Data to write: \n" + msg);

//        int size =2000;
//
//        final ArrayList<String> split = new ArrayList<>();
//        for (int i = 0; i <= msg.length() / size; i++) {
//            split.add(msg.substring(i * size, Math.min((i + 1) * size, msg.length())));
//        }

        new Thread() {

            @Override
            public void run() {
                if (AmcuConfig.getInstance().getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
                        || AmcuConfig.getInstance().getPrinter().equalsIgnoreCase("SmartMoo") ||
                        AmcuConfig.getInstance().getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
                    sendPacket(msg);
                } else {
                    List<byte[]> msgList = Util.divideArray(data, MAXBUFFER);
                    for (int i = 0; i < msgList.size(); i++) {
                        sendPacket(new String(msgList.get(i)));
                        doPrinterDelay(1000);
                    }
                    if (mDataListener != null)
                        mDataListener.onWriteComplete();

                }
            }
        }.start();
    }

    private void doPrinterDelay(long delay) {
        try {
            Thread.sleep(delay);
        } catch (Exception e) {

        }
    }

    @Override
    public void setParameters(DeviceParameters parameters) {
        mParameters = parameters;

    }

    @Override
    public void registerObserver(DataObserver dataObserver) {

    }

    @Override
    public void write(byte[] msg) {
        sendPacket(new String(msg));
    }

    @Override
    public void write(String msg) {

        sendPacket(msg);
    }

    private void sendPacket(String msg) {
        Log.v("prober", "TVS Data after fragmenting: \n" + msg);

        try {
            if (sample == null)
                sample = new Sample(ConnectionInfo.getInstance().getConnection());
//            Log.v(SmartCCConstants.PROBER, "TVS printer data: " + msg);
            sample.samplePrint(msg);
        } catch (NullPointerException e1) {

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
