package com.devapp.devmain.helper;

import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.devapp.devmain.devicemanager.DriverConfiguration;
import com.devapp.devmain.main.LoginActivity;
import com.devapp.devmain.peripherals.devices.DeviceParameters;
import com.devapp.devmain.peripherals.devices.TVSDevice;
import com.devapp.devmain.peripherals.devices.WifiUDPDevice;
import com.devapp.devmain.peripherals.devices.WiredDevice;
import com.devapp.devmain.peripherals.interfaces.DataObserver;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devApp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by u_pendra on 9/12/16.
 */

public class DeviceDataActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private final String TAG = "DeviceData";
    //    public ArrayList<DeviceEntity> allDeviceData = DeviceListActivity.allDeviceEntry;
    public HashMap<String, DeviceEntity> deviceMap = SmartCCConstants.deviceEntityMap;
    Handler handler = new Handler();
    Runnable updateRunnable;
    DriverConfiguration driverConfiguration = new DriverConfiguration();
    StringBuilder message = new StringBuilder();
    StringBuilder bufferMessage = new StringBuilder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private TextView textResponse;
    private final SerialInputOutputManager.Listener mListenerWS = new SerialInputOutputManager.Listener() {

        @Override
        public void onRunError(Exception e) {
            Log.d(TAG, "Runner stopped.");
        }

        @Override
        public void onNewData(final byte[] data) {

            DeviceDataActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateUIwithData(data);
                }
            });
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Spinner spDevice, spBaudrate, spStopbits, spParity, spDatabits;
    private LinearLayout lnData;
    private Button btnStart, btnStop, btnClear, btnBack, btnSave, btnHexOrText;
    private UsbSerialPort usbSerialPort = null;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private SerialInputOutputManager mSerialIoManager;
    private UsbManager mUsbManager;
    private DeviceEntity mDeviceEntity;
    private Device mDevice = null;
    private DeviceParameters mDeviceParams;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUsbManager = (UsbManager) getSystemService(USB_SERVICE);
        setContentView(R.layout.layout);
        initializeView();
        onClickAndSelect();
        setDeviceList();


    }

    public void initializeView() {
        textResponse = (TextView) findViewById(R.id.textResponse);
        spDevice = (Spinner) findViewById(R.id.spDevice);
        spBaudrate = (Spinner) findViewById(R.id.spBaudrate);
        spStopbits = (Spinner) findViewById(R.id.spStopbits);
        spParity = (Spinner) findViewById(R.id.spParity);
        spDatabits = (Spinner) findViewById(R.id.spDatabits);
        lnData = (LinearLayout) findViewById(R.id.lnData);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnHexOrText = (Button) findViewById(R.id.btnhexOrText);
    }

    public void setDeviceList() {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this
                , R.layout.support_simple_spinner_dropdown_item, getAllDevice());
        spDevice.setAdapter(arrayAdapter);

    }

    public ArrayList<String> getAllDevice() {
        ArrayList<String> allList = new ArrayList<>();
        for (String key : deviceMap.keySet()) {
            allList.add(key);
        }
       /* for (int i = 0; i < allDeviceData.size(); i++) {
            allList.add(allDeviceData.get(i).driverName);
        }*/
        return allList;

    }

    public void onClickAndSelect() {
        spDevice.setOnItemSelectedListener(DeviceDataActivity.this);
        spBaudrate.setOnItemSelectedListener(this);
        spStopbits.setOnItemSelectedListener(this);
        spParity.setOnItemSelectedListener(this);
        spDatabits.setOnItemSelectedListener(this);

        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnHexOrText.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v == btnStart) {
            setConnection();
        } else if (v == btnStop) {
            onStopConnection();
        } else if (v == btnClear) {
            message = new StringBuilder();
            baos.reset();
            textResponse.setText("");
        } else if (v == btnBack) {
            Intent intent = new Intent(DeviceDataActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (v == btnSave) {
            try {
                Util.generateNoteOnSD(spDevice.getSelectedItem().toString(),
                        textResponse.getText().toString(), DeviceDataActivity.this, "DeviceDump");
                displayToast("File saved in sdcard");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (v == btnHexOrText) {
            if (btnHexOrText.getText().toString().trim().equalsIgnoreCase("HEX")) {
                btnHexOrText.setText("TEXT");
            } else {
                btnHexOrText.setText("HEX");
            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {

        if (parent == spDevice) {
        } else if (parent == spBaudrate) {
        } else if (parent == spStopbits) {
        } else if (parent == spParity) {
        } else if (parent == spDatabits) {
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setConnection() {
        mDeviceEntity = deviceMap.get(spDevice.getSelectedItem().toString());

        mDeviceParams = new DeviceParameters(Integer.parseInt(spBaudrate.getSelectedItem().toString()),
                driverConfiguration.getDataBits(spDatabits.getSelectedItem().toString()),
                driverConfiguration.getParity(spParity.getSelectedItem().toString()),
                driverConfiguration.getStopBits(spStopbits.getSelectedItem().toString()));
        if (mDevice != null) {
            mDevice.unregisterObserver();
            mDevice.closeConnection();
        }
        switch (mDeviceEntity.deviceType) {
            case SmartCCConstants.WIFI:
                mDevice = new WifiUDPDevice(mDeviceEntity);
                break;
            case SmartCCConstants.USB:
                mDevice = new WiredDevice(this, mDeviceEntity);
                break;
            case SmartCCConstants.USB_TVS:
                mDevice = new TVSDevice(this, mDeviceEntity);
                break;
            default:
                mDevice = new WifiUDPDevice(mDeviceEntity);
        }
        mDevice.registerObserver(new DataObserver() {
            @Override
            public void onDataReceived(final byte[] data) {
                DeviceDataActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateUIwithData(data);
                    }
                });
            }
        });
        mDevice.setParameters(mDeviceParams);
        mExecutor = Executors.newSingleThreadExecutor();
        mUsbManager = (UsbManager) getSystemService(USB_SERVICE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mDevice.read();
            }
        }).start();

        /*updateRunnable = new Runnable() {
            @Override
            public void run() {

                if (usbSerialPort != null) {
                    try {
                        startIoManager(usbSerialPort, spDevice.getSelectedItem().toString());
                    } catch (Exception e) {

                    }
                }
            }
        };*/

    }

    private void startIoManager(UsbSerialPort sPort, String name) throws Exception {

        if (sPort != null) {

            mSerialIoManager = new SerialInputOutputManager(sPort, mListenerWS);
            mExecutor.submit(mSerialIoManager);
            displayToast("Opening " + spDevice.getSelectedItem().toString() + " port");


        } else {

        }
    }

    public synchronized void updateUIwithData(byte[] data) {

        if (data != null) {
            baos.write(data, 0, data.length);
        }
        String s = new String(data);
        message.append(s);
        String str1 = message.toString();
        bufferMessage = new StringBuilder();


        if (btnHexOrText.getText().toString().equalsIgnoreCase("HEX")) {
            textResponse.setText(str1);
        } else {
            textResponse.setText("");
            for (byte b : baos.toByteArray()) {
                bufferMessage.append(String.format("%02X ", b));
                if (baos.toByteArray().length > 900) {
                    baos.reset();
                    break;
                }
            }
            textResponse.setText(bufferMessage.toString());
        }


    }


    public void openDeviceConnection(UsbSerialDriver driver,
                                     int productId, String name) {
        if (name.equalsIgnoreCase(spDevice.getSelectedItem().toString())) {
            if (usbSerialPort == null) {
                UsbDeviceConnection usbDeviceConn = mUsbManager
                        .openDevice(driver.getDevice());
                if (usbDeviceConn != null) {

                    try {
                        usbSerialPort = driver.getPorts().get(0);
                        usbSerialPort.open(usbDeviceConn);
                        usbSerialPort.setParameters(Integer.parseInt(spBaudrate.getSelectedItem().toString()),
                                driverConfiguration.getDataBits(spDatabits.getSelectedItem().toString())
                                , driverConfiguration.getStopBits(spStopbits.getSelectedItem().toString()),
                                driverConfiguration.getParity(spParity.getSelectedItem().toString()));
                        displayToast("Open " + spDevice.getSelectedItem().toString() + " connection");
                    } catch (IOException e) {
                        e.printStackTrace();

                        // showToastMessage("Please check the connectivity! or restart the tab!");
                    }
                }
            }
        }


    }


    public void onStopConnection() {
        try {
            /*if (mSerialIoManager != null) {

                mSerialIoManager.stop();
                mSerialIoManager = null;
                mExecutor.shutdownNow();

                usbSerialPort.close();
                mUsbManager = null;
                usbSerialPort = null;
                mExecutor.shutdownNow();
                displayToast("Stopped " + spDevice.getSelectedItem().toString() + " manager");

            }*/
            mDevice.unregisterObserver();
            mExecutor.shutdownNow();
            displayToast("Stopped " + spDevice.getSelectedItem().toString() + " manager");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void displayToast(String message) {
        Util.displayErrorToast(message, DeviceDataActivity.this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDevice != null) {
            mDevice.unregisterObserver();
            mDevice.closeConnection();
        }
    }
}
