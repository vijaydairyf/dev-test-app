package com.devapp.devmain.main;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.smartcc.PortConfigurationActivity;
import com.devApp.R;


/**
 * Created by u_pendra on 3/2/17.
 */

public class DeviceSettingAlert extends Activity {

    public Context mContext;
    public Activity activity;
    public String lastDevice = "";
    Spinner spData;
    AmcuConfig amcuConfig;
    String portNumber, portDevice;
    private com.eevoskos.robotoviews.widget.RobotoTextView tvheader;
    private Button btnCancel, btnSave;
    private TextView tvPort1;
    private Spinner spDeviceType, spPort1DeviceType, spPort1Baudrate, spPort1Parity, spPort1databits, spPort1Stopbits;
    private RelativeLayout rlHeader;
    private LinearLayout lnDPS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_configuration);
        mContext = DeviceSettingAlert.this;
        amcuConfig = AmcuConfig.getInstance();
        initializeView();
        getIntentData();
        setSpinner(portDevice);
        onSpinnerSelected();
        tvPort1.setText("Port " + portNumber + ": ");
        spDeviceType.requestFocus();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSave(spDeviceType.getSelectedItem().toString());
                PortConfigurationActivity.getInstance().setSavedText();
                PortConfigurationActivity.getInstance().setDrawbleOnSavedText();
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });
    }

    public void getIntentData() {

        this.portDevice = getIntent().getStringExtra("portDevice");
        this.portNumber = getIntent().getStringExtra("portNumber");


    }

    public void initializeView() {
        tvPort1 = (TextView) findViewById(R.id.tvPort1);
        spDeviceType = (Spinner) findViewById(R.id.spDeviceType);
        spPort1DeviceType = (Spinner) findViewById(R.id.spPort1DeviceType);
        spPort1Baudrate = (Spinner) findViewById(R.id.spPort1Baudrate);
        spPort1Parity = (Spinner) findViewById(R.id.spPort1Parity);
        spPort1databits = (Spinner) findViewById(R.id.spPort1databits);
        spPort1Stopbits = (Spinner) findViewById(R.id.spPort1Stopbits);
        rlHeader = (RelativeLayout) findViewById(R.id.rlHeader);
        lnDPS = (LinearLayout) findViewById(R.id.lnDPS);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave = (Button) findViewById(R.id.btnSave);
    }

    public void setSpinner(String deviceType) {
        final String[] items = {DeviceName.MILK_ANALYSER, DeviceName.MA2,
                DeviceName.RDU, DeviceName.WS, DeviceName.PRINTER, DeviceName.NO_CONNECTION};
        ArrayAdapter<String> spinnerArrayAdapter;
        spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, items);
        spDeviceType.setAdapter(spinnerArrayAdapter);
        int spinnerPosition = spinnerArrayAdapter.getPosition(deviceType);
        spDeviceType.setSelection(spinnerPosition);


        if (deviceType.equalsIgnoreCase(DeviceName.MILK_ANALYSER) || deviceType.equalsIgnoreCase(DeviceName.MA2)
                || deviceType.equalsIgnoreCase(DeviceName.MILK_ANALYSER)) {
            String[] types = mContext.getResources().getStringArray(R.array.Milk_Analyser);
            spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_spinner_item, types);

            spPort1DeviceType.setAdapter(spinnerArrayAdapter);
            spinnerPosition = spinnerArrayAdapter.getPosition(amcuConfig.getMA());
            spPort1DeviceType.setSelection(spinnerPosition);
            setDeviceBaudRate(spPort1Baudrate, amcuConfig.getMABaudRate());
            setDeviceParity(spPort1Parity, amcuConfig.getMaParity());
            setDatabits(spPort1databits, amcuConfig.getMaDataBits());
            setDeviceStopBits(spPort1Stopbits, amcuConfig.getMaStopBits());

            if (deviceType == DeviceName.MA2) {
                spinnerPosition = spinnerArrayAdapter.getPosition(amcuConfig.getMa2Name());
                setDeviceBaudRate(spPort1Baudrate, amcuConfig.getMa2Baudrate());
                setDeviceParity(spPort1Parity, amcuConfig.getMa2Parity());
                setDeviceStopBits(spPort1Stopbits, amcuConfig.getMa2StopBits());
                setDatabits(spPort1databits, amcuConfig.getMa2DataBits());
            } else {
                spinnerPosition = spinnerArrayAdapter.getPosition(amcuConfig.getMa1Name());
                setDeviceBaudRate(spPort1Baudrate, amcuConfig.getMa1Baudrate());
                setDeviceParity(spPort1Parity, amcuConfig.getMa1Parity());
                setDeviceStopBits(spPort1Stopbits, amcuConfig.getMa1StopBits());
                setDatabits(spPort1databits, amcuConfig.getMa1DataBits());

            }
            spPort1DeviceType.setSelection(spinnerPosition);


        } else if (deviceType == DeviceName.RDU) {
            String[] types = mContext.getResources().getStringArray(R.array.RDU);
            spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_spinner_item, types);
            spPort1DeviceType.setAdapter(spinnerArrayAdapter);

            spinnerPosition = spinnerArrayAdapter.getPosition(amcuConfig.getRDU());
            spPort1DeviceType.setSelection(spinnerPosition);
            setDeviceBaudRate(spPort1Baudrate, amcuConfig.getRdubaudrate());
            setDeviceParity(spPort1Parity, amcuConfig.getRDUParity());
            setDatabits(spPort1databits, amcuConfig.getRDUDataBits());
            setDeviceStopBits(spPort1Stopbits, amcuConfig.getRDUStopBits());
        } else if (deviceType.equalsIgnoreCase(DeviceName.WS)) {
            String[] types = mContext.getResources().getStringArray(R.array.WEIGHING);
            spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_spinner_item, types);
            spPort1DeviceType.setAdapter(spinnerArrayAdapter);

            spinnerPosition = spinnerArrayAdapter.getPosition(amcuConfig.getWeighingScale());
            spPort1DeviceType.setSelection(spinnerPosition);
            setDeviceBaudRate(spPort1Baudrate, amcuConfig.getWeighingbaudrate());
            setDeviceParity(spPort1Parity, amcuConfig.getWSParity());
            setDatabits(spPort1databits, amcuConfig.getWSDataBits());
            setDeviceStopBits(spPort1Stopbits, amcuConfig.getWSStopBits());
        } else if (deviceType.equalsIgnoreCase(DeviceName.PRINTER)) {
            String[] types = mContext.getResources().getStringArray(R.array.Printer);
            spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                    android.R.layout.simple_spinner_item, types);
            spPort1DeviceType.setAdapter(spinnerArrayAdapter);

            spinnerPosition = spinnerArrayAdapter.getPosition(amcuConfig.getPrinter());
            spPort1DeviceType.setSelection(spinnerPosition);
            setDeviceBaudRate(spPort1Baudrate, amcuConfig.getPrinterBaudRate());
            setDeviceParity(spPort1Parity, amcuConfig.getPrinterParity());
            setDatabits(spPort1databits, amcuConfig.getPrinterDataBits());
            setDeviceStopBits(spPort1Stopbits, amcuConfig.getPrinterStopBits());
        } else if (deviceType.equalsIgnoreCase(DeviceName.NO_CONNECTION)) {
            spPort1DeviceType.setVisibility(View.GONE);
            spPort1Baudrate.setVisibility(View.GONE);
            spPort1Parity.setVisibility(View.GONE);
            spPort1databits.setVisibility(View.GONE);
            spPort1Stopbits.setVisibility(View.GONE);
        }

    }

    public void setDeviceBaudRate(Spinner spBaudrate, int baudrate) {
        //Setting baudrate
        String[] types = mContext.getResources().getStringArray(R.array.Set_baudrate);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, types);
        spBaudrate.setAdapter(spinnerArrayAdapter);
        int spinnerPos = spinnerArrayAdapter.getPosition(String.valueOf(baudrate));
        spBaudrate.setSelection(spinnerPos);

    }

    public void setDeviceParity(Spinner spBaudrate, String parity) {
        //Setting baudrate
        String[] types = mContext.getResources().getStringArray(R.array.MA_parity);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, types);
        spBaudrate.setAdapter(spinnerArrayAdapter);
        int spinnerPos = spinnerArrayAdapter.getPosition(String.valueOf(parity));
        spBaudrate.setSelection(spinnerPos);

    }

    public void setDeviceStopBits(Spinner spBaudrate, String stopbits) {
        //Setting baudrate
        String[] types = mContext.getResources().getStringArray(R.array.MA_stop_bits);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, types);
        spBaudrate.setAdapter(spinnerArrayAdapter);
        int spinnerPos = spinnerArrayAdapter.getPosition(String.valueOf(stopbits));
        spBaudrate.setSelection(spinnerPos);

    }

    public void setDatabits(Spinner spBaudrate, String databits) {
        //Setting baudrate
        String[] types = mContext.getResources().getStringArray(R.array.MA_data_bits);
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, types);
        spBaudrate.setAdapter(spinnerArrayAdapter);
        int spinnerPos = spinnerArrayAdapter.getPosition(String.valueOf(databits));
        spBaudrate.setSelection(spinnerPos);

    }

    public void onSave(String deviceType) {
        if (Integer.valueOf(portNumber) == 1) {
            amcuConfig.setKeyDevicePort1(spDeviceType.getSelectedItem().toString());
        } else if (Integer.valueOf(portNumber) == 2) {
            amcuConfig.setKeyDevicePort2(spDeviceType.getSelectedItem().toString());
        } else if (Integer.valueOf(portNumber) == 3) {
            amcuConfig.setKeyDevicePort3(spDeviceType.getSelectedItem().toString());
        } else if (Integer.valueOf(portNumber) == 4) {
            amcuConfig.setKeyDevicePort4(spDeviceType.getSelectedItem().toString());
        } else if (Integer.valueOf(portNumber) == 5) {
            amcuConfig.setKeyDevicePort5(spDeviceType.getSelectedItem().toString());
        }

        if (deviceType.equalsIgnoreCase(DeviceName.MILK_ANALYSER) || deviceType.equalsIgnoreCase(DeviceName.MA2)
                || deviceType.equalsIgnoreCase(DeviceName.MILK_ANALYSER)) {

            if (deviceType.equalsIgnoreCase(DeviceName.MA2)) {
                amcuConfig.saveMA2Details(spPort1DeviceType.getSelectedItem().toString(),
                        Integer.valueOf(spPort1Baudrate.getSelectedItem().toString()));

                amcuConfig.setMa2parityStopAndDataBits(spPort1Parity.getSelectedItem().toString()
                        , spPort1Stopbits.getSelectedItem().toString(), spPort1databits.getSelectedItem().toString());
            } else {
                amcuConfig.setMA(spPort1DeviceType.getSelectedItem().toString());
                amcuConfig.setMABaudRate(Integer.valueOf(spPort1Baudrate.getSelectedItem().toString()));
                amcuConfig.setMaparity(spPort1Parity.getSelectedItem().toString());
                amcuConfig.setMaDataBits(spPort1databits.getSelectedItem().toString());
                amcuConfig.setMaStopBits(spPort1Stopbits.getSelectedItem().toString());

                amcuConfig.saveMA1Details(spPort1DeviceType.getSelectedItem().toString(),
                        Integer.valueOf(spPort1Baudrate.getSelectedItem().toString()));
                amcuConfig.setMa1parityStopAndDataBits(spPort1Parity.getSelectedItem().toString()
                        , spPort1Stopbits.getSelectedItem().toString(), spPort1databits.getSelectedItem().toString());
            }

        } else if (deviceType.equalsIgnoreCase(DeviceName.RDU)) {
            amcuConfig.setRDU(spPort1DeviceType.getSelectedItem().toString());
            amcuConfig.setRdubaudrate(Integer.valueOf(spPort1Baudrate.getSelectedItem().toString()));
            amcuConfig.setRDUparityStopAndDataBits(spPort1Parity.getSelectedItem().toString()
                    , spPort1Stopbits.getSelectedItem().toString(), spPort1databits.getSelectedItem().toString());
        } else if (deviceType.equalsIgnoreCase(DeviceName.WS)) {
            amcuConfig.setWeighingScale(spPort1DeviceType.getSelectedItem().toString());
            amcuConfig.setWeighingbaudrate(Integer.valueOf(spPort1Baudrate.getSelectedItem().toString()));
            amcuConfig.setWSparityStopAndDataBits(spPort1Parity.getSelectedItem().toString()
                    , spPort1Stopbits.getSelectedItem().toString(), spPort1databits.getSelectedItem().toString());


        } else if (deviceType.equalsIgnoreCase(DeviceName.PRINTER)) {
            amcuConfig.setPrinter(spPort1DeviceType.getSelectedItem().toString());
            amcuConfig.setPrinterBaudRate(Integer.valueOf(spPort1Baudrate.getSelectedItem().toString()));
            amcuConfig.setPrinterParityStopAndDataBits(spPort1Parity.getSelectedItem().toString()
                    , spPort1Stopbits.getSelectedItem().toString(), spPort1databits.getSelectedItem().toString());

        } else if (deviceType.equalsIgnoreCase(DeviceName.NO_CONNECTION)) {
            spPort1DeviceType.setVisibility(View.GONE);
            spPort1Baudrate.setVisibility(View.GONE);
            spPort1Parity.setVisibility(View.GONE);
            spPort1databits.setVisibility(View.GONE);
            spPort1Stopbits.setVisibility(View.GONE);
        }

    }

    public void onSpinnerSelected() {
        spDeviceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spPort1DeviceType.setVisibility(View.VISIBLE);
                spPort1Baudrate.setVisibility(View.VISIBLE);
                spPort1Parity.setVisibility(View.VISIBLE);
                spPort1databits.setVisibility(View.VISIBLE);
                spPort1Stopbits.setVisibility(View.VISIBLE);

                if (!lastDevice.equalsIgnoreCase(spDeviceType.getSelectedItem().toString())) {
                    setSpinner(spDeviceType.getSelectedItem().toString());
                    lastDevice = spDeviceType.getSelectedItem().toString();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


}