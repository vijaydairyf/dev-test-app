package com.devapp.smartcc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.devapp.devmain.devicemanager.ConfigurationManager;
import com.devapp.devmain.entity.ConfigurationEntity;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.helper.PortConstants;
import com.devapp.devmain.httptasks.ConfigurationPush;
import com.devapp.devmain.main.DeviceSettingAlert;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by u_pendra on 27/1/17.
 */

public class PortConfigurationActivity extends AppCompatActivity implements View.OnClickListener {

    public static PortConfigurationActivity mActivity;
    private static ConfigurationEntity configurationEntity;
    AmcuConfig amcuConfig;

    HashMap<Integer, String> hashMap = new HashMap<>();
    ConfigurationManager configurationManager;
    private TextView tvPower, tvPort1, tvData, tvPort2, tvPort3, tvScanner, tvPort4, tvKeyboard, tvPort5;
    private Button btnCancel, btnSave;

    public static PortConfigurationActivity getInstance() {
        return mActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_port);
        initializeView();
        onClickThis();
        mActivity = this;
    }

    @Override
    protected void onStart() {
        super.onStart();
        amcuConfig = AmcuConfig.getInstance();
        setDefaultDrawable();
        setSavedText();
        setDrawbleOnSavedText();
        ConfigurationManager configurationManager = new ConfigurationManager(PortConfigurationActivity.this);

        if (configurationEntity == null) {
            configurationEntity = configurationManager.addConfiguration();
        }


    }

    public void initializeView() {

        tvPower = (TextView) findViewById(R.id.tvPower);
        tvPort1 = (TextView) findViewById(R.id.tvPort1);
        tvData = (TextView) findViewById(R.id.tvData);
        tvPort2 = (TextView) findViewById(R.id.tvPort2);
        tvPort3 = (TextView) findViewById(R.id.tvPort3);
        tvScanner = (TextView) findViewById(R.id.tvScanner);
        tvPort4 = (TextView) findViewById(R.id.tvPort4);
        tvKeyboard = (TextView) findViewById(R.id.tvKeyboard);
        tvPort5 = (TextView) findViewById(R.id.tvPort5);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave = (Button) findViewById(R.id.btnSave);

        btnSave.setText("Set Default");
    }

    public void onClickThis() {

        tvPower.setOnClickListener(this);
        tvPort1.setOnClickListener(this);
        tvData.setOnClickListener(this);
        tvPort2.setOnClickListener(this);
        tvPort3.setOnClickListener(this);
        tvScanner.setOnClickListener(this);
        tvPort4.setOnClickListener(this);
        tvKeyboard.setOnClickListener(this);
        tvPort5.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

        if (v == tvPower) {
        } else if (v == tvPort1) {

            Intent intent = new Intent(PortConfigurationActivity.this, DeviceSettingAlert.class);
            intent.putExtra("portDevice", tvPort1.getText().toString());
            intent.putExtra("portNumber", "1");
            startActivity(intent);

        } else if (v == tvData) {
        } else if (v == tvPort2) {
            Intent intent = new Intent(PortConfigurationActivity.this, DeviceSettingAlert.class);
            intent.putExtra("portDevice", tvPort2.getText().toString());
            intent.putExtra("portNumber", "2");
            startActivity(intent);
        } else if (v == tvPort3) {
            Intent intent = new Intent(PortConfigurationActivity.this, DeviceSettingAlert.class);
            intent.putExtra("portDevice", tvPort3.getText().toString());
            intent.putExtra("portNumber", "3");
            startActivity(intent);
        } else if (v == tvScanner) {

        } else if (v == tvPort4) {
            Intent intent = new Intent(PortConfigurationActivity.this, DeviceSettingAlert.class);
            intent.putExtra("portDevice", tvPort4.getText().toString());
            intent.putExtra("portNumber", "4");
            startActivity(intent);
        } else if (v == tvKeyboard) {
        } else if (v == tvPort5) {
            Intent intent = new Intent(PortConfigurationActivity.this, DeviceSettingAlert.class);
            intent.putExtra("portDevice", tvPort5.getText().toString());
            intent.putExtra("portNumber", "5");
            startActivity(intent);
        } else if (v == btnCancel) {
            onFinish();
        } else if (v == btnSave) {
            Util.displayErrorToast("All device setting changed to default!", PortConfigurationActivity.this);
            setDefaultConfiguration();
            setSavedText();
            setDrawbleOnSavedText();

        }

    }


    public void setDefaultDrawable() {

        tvPort1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.milk_analyser, 0, R.drawable.one, 0);
        tvPort2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.milk_analyser, 0, R.drawable.two, 0);
        tvPort3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.rdu, 0, R.drawable.three, 0);
        tvPort4.setCompoundDrawablesWithIntrinsicBounds(R.drawable.weighing_scale, 0, R.drawable.four, 0);
        tvPort5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.printer_fill, 0, R.drawable.five, 0);

    }

    public void setDefaultText() {
        amcuConfig.setKeyDevicePort1(DeviceName.MILK_ANALYSER);
        amcuConfig.setKeyDevicePort2(DeviceName.NO_CONNECTION);
        amcuConfig.setKeyDevicePort3(DeviceName.RDU);
        amcuConfig.setKeyDevicePort4(DeviceName.WS);
        amcuConfig.setKeyDevicePort5(DeviceName.PRINTER);
    }

    public void setSavedText() {
        tvPort1.setText(amcuConfig.getKeyDevicePort1());
        tvPort2.setText(amcuConfig.getKeyDevicePort2());
        tvPort3.setText(amcuConfig.getKeyDevicePort3());
        tvPort4.setText(amcuConfig.getKeyDevicePort4());
        tvPort5.setText(amcuConfig.getKeyDevicePort5());
    }


    public void setDrawbleOnSavedText() {
        tvPort1.setCompoundDrawablesWithIntrinsicBounds(getDeviceDrawable(amcuConfig.getKeyDevicePort1())
                , 0, getNumberDrawable(1), 0);
        tvPort2.setCompoundDrawablesWithIntrinsicBounds(getDeviceDrawable(amcuConfig.getKeyDevicePort2())
                , 0, getNumberDrawable(2), 0);
        tvPort3.setCompoundDrawablesWithIntrinsicBounds(getDeviceDrawable(amcuConfig.getKeyDevicePort3())
                , 0, getNumberDrawable(3), 0);
        tvPort4.setCompoundDrawablesWithIntrinsicBounds(getDeviceDrawable(amcuConfig.getKeyDevicePort4())
                , 0, getNumberDrawable(4), 0);
        tvPort5.setCompoundDrawablesWithIntrinsicBounds(getDeviceDrawable(amcuConfig.getKeyDevicePort5())
                , 0, getNumberDrawable(5), 0);

    }


    public int getNumberDrawable(int port) {

        int drawable;

        if (port == 1) {
            drawable = R.drawable.one;
        } else if (port == 2) {
            drawable = R.drawable.two;
        } else if (port == 3) {
            drawable = R.drawable.three;
        } else if (port == 4) {
            drawable = R.drawable.four;
        } else {
            drawable = R.drawable.five;
        }

        return drawable;
    }

    public int getDeviceDrawable(String device) {

        int drawable = 0;

        if (device.equalsIgnoreCase(DeviceName.MILK_ANALYSER)
                || device.equalsIgnoreCase(DeviceName.MILK_ANALYSER)
                || device.equalsIgnoreCase(DeviceName.MA2)) {
            drawable = R.drawable.milk_analyser;
        } else if (device.equalsIgnoreCase(DeviceName.NO_CONNECTION)) {
            drawable = R.drawable.no_connection;
        } else if (device.equalsIgnoreCase(DeviceName.RDU)) {
            drawable = R.drawable.rdu;
        } else if (device.equalsIgnoreCase(DeviceName.WS)) {
            drawable = R.drawable.weighing_scale;
        } else if (device.equalsIgnoreCase(DeviceName.PRINTER)) {
            drawable = R.drawable.printer_fill;
        }

        return drawable;
    }


    public void onSave() {
        hashMap.put(1, tvPort1.getText().toString());
        hashMap.put(2, tvPort2.getText().toString());
        hashMap.put(3, tvPort3.getText().toString());
        hashMap.put(4, tvPort4.getText().toString());
        hashMap.put(5, tvPort5.getText().toString());

        Collection<String> lsString = hashMap.values();
        HashSet<String> hashSet = new HashSet<>(hashMap.values());

        int noConnection = 0;

        for (int key : hashMap.keySet()) {
            if (hashMap.get(key).equalsIgnoreCase(DeviceName.NO_CONNECTION)) {
                noConnection++;
            }

        }


        if ((lsString.size() - hashSet.size()) == (noConnection - 1)) {

        } else if (lsString.size() != hashSet.size()) {
            Util.displayErrorToast("Duplicate device entry!", PortConfigurationActivity.this);
            return;
        }

        amcuConfig.setKeyDevicePort1(tvPort1.getText().toString().trim());
        amcuConfig.setKeyDevicePort2(tvPort2.getText().toString().trim());
        amcuConfig.setKeyDevicePort3(tvPort3.getText().toString().trim());
        amcuConfig.setKeyDevicePort4(tvPort4.getText().toString().trim());
        amcuConfig.setKeyDevicePort5(tvPort5.getText().toString().trim());

        Util.displayErrorToast("Device saved successfully!", PortConfigurationActivity.this);
        finish();

    }

    public void setDefaultConfiguration() {
        setDefaultText();

        amcuConfig.setMA(PortConstants.DEFAULT_MA);
        amcuConfig.saveMA1Details(PortConstants.DEFAULT_MA
                , Integer.parseInt(PortConstants.DEFAULT_BAUDRATE));
        amcuConfig.saveMA2Details(PortConstants.DEFAULT_MA
                , Integer.parseInt(PortConstants.DEFAULT_BAUDRATE));


        amcuConfig.setWeighingbaudrate(Integer.parseInt(PortConstants.DEFAULT_BAUDRATE));
        amcuConfig.setRdubaudrate(Integer.parseInt(PortConstants.DEFAULT_BAUDRATE));
        amcuConfig.setPrinterBaudRate(Integer.parseInt(PortConstants.DEFAULT_BAUDRATE));


        amcuConfig.setMaparity(PortConstants.DEFAULT_PARITY);
        amcuConfig.setMaDataBits(PortConstants.DEFAULT_DATA_BITS);
        amcuConfig.setMaStopBits(PortConstants.DEFAULT_STOP_BITS);

        amcuConfig.setMa1parityStopAndDataBits(PortConstants.DEFAULT_PARITY,
                PortConstants.DEFAULT_STOP_BITS, PortConstants.DEFAULT_DATA_BITS);

        amcuConfig.setMa2parityStopAndDataBits(PortConstants.DEFAULT_PARITY,
                PortConstants.DEFAULT_STOP_BITS, PortConstants.DEFAULT_DATA_BITS);

        amcuConfig.setWSparityStopAndDataBits(PortConstants.DEFAULT_PARITY,
                PortConstants.DEFAULT_STOP_BITS, PortConstants.DEFAULT_DATA_BITS);

        amcuConfig.setRDUparityStopAndDataBits(PortConstants.DEFAULT_PARITY,
                PortConstants.DEFAULT_STOP_BITS, PortConstants.DEFAULT_DATA_BITS);

        amcuConfig.setPrinterParityStopAndDataBits(PortConstants.DEFAULT_PARITY,
                PortConstants.DEFAULT_STOP_BITS, PortConstants.DEFAULT_DATA_BITS);

    }

    @Override
    public void onBackPressed() {
        onFinish();
    }


    public void onFinish() {
        if (new SmartCCUtil(getApplicationContext()).checkPortValidation()) {
            configurationManager = new ConfigurationManager(getApplicationContext());
            configurationManager.getSavedConfigurationList(configurationEntity);
            configurationEntity = null;
            startService(new Intent(PortConfigurationActivity.this, ConfigurationPush.class));
            finish();
        }
    }
}
