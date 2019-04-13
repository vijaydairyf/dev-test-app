package com.devapp.devmain.deviceinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.widget.TextView;

import com.devapp.devmain.user.Util;
import com.devApp.R;

import java.text.DecimalFormat;

public class DeviceInfoActivity extends AppCompatActivity {

    TextView batteryStatusTV, networkInfoTV, dataInfoTV;
    Intent batteryStatus;
    BatteryReceiver mBatteryReceiver;
    String operatorName = "";
    String dataState = "";
    DecimalFormat percentageDF = new DecimalFormat("###.##");
    private SensorManager mSensorManager;
    private Sensor mTempSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);
        batteryStatusTV = (TextView) findViewById(R.id.tv_batteryStatus);
        networkInfoTV = (TextView) findViewById(R.id.tv_networkInfo);
        dataInfoTV = (TextView) findViewById(R.id.tv_dataInfo);
        mBatteryReceiver = new BatteryReceiver();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
       /* mSensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                Log.d("myDebug", "Temperature :" +sensorEvent.values[0]);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        }, mTempSensor, SensorManager.SENSOR_DELAY_FASTEST);*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        setStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBatteryReceiver);
    }

    private void setStatus() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = this.registerReceiver(mBatteryReceiver, ifilter);

        String chargeStatus;
        String chargePercent;

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL) {
            chargeStatus = "Charging";
        } else {
            chargeStatus = "Not Charging";
        }

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        float temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0f;

        chargePercent = percentageDF.format((level / (float) scale) * 100);

        //  batteryStatusTV.setText(chargePercent+"% ("+chargeStatus+") " + temperature+"C");
        batteryStatusTV.setText(chargePercent + "% (" + chargeStatus + ")");

        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

        if (telephonyManager.getNetworkOperatorName() != null) {
            operatorName = telephonyManager.getSimOperatorName().toUpperCase();

        }
        dataState = getDataState(telephonyManager.getDataState()) + " ( " + getNetworkType(telephonyManager.getNetworkType()) + " )";
        dataInfoTV.setText(dataState);
        telephonyManager.listen(new PhoneStateListener() {

            @Override
            public void onDataConnectionStateChanged(int state, int networkType) {
                super.onDataConnectionStateChanged(state, networkType);
                dataState = getDataState(state) + " ( " + getNetworkType(networkType) + " )";
                dataInfoTV.setText(dataState);

            }

            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                int signalStrengthValue;
                int dBm;
                String strength = "Unknown";
                super.onSignalStrengthsChanged(signalStrength);
                if (signalStrength.isGsm()) {
                    if (signalStrength.getGsmSignalStrength() != 99) {
                        signalStrengthValue = signalStrength.getGsmSignalStrength();
                        dBm = signalStrength.getGsmSignalStrength() * 2 - 113;
                    } else {
                        signalStrengthValue = signalStrength.getGsmSignalStrength();
                        dBm = 0;
                    }

                } else {
                    dBm = signalStrength.getCdmaDbm();
                    signalStrengthValue = (dBm + 113) / 2;
                }
                if (signalStrengthValue == 99) {
                    strength = "Unknown";
                } else if (signalStrengthValue > 25) {
                    strength = "Good";
                } else if (signalStrengthValue < 26 && signalStrengthValue > 20) {
                    strength = "Average";
                } else if (signalStrengthValue < 21 && signalStrengthValue > 3) {
                    strength = "Weak";
                } else if (signalStrengthValue < 4) {
                    strength = "Very Weak";
                }
                networkInfoTV.setText(strength + " ( " + dBm + "dBm ) " + operatorName);
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

    }

    private String getDataState(int state) {
        String data;
        switch (state) {
            case TelephonyManager.DATA_DISCONNECTED:
                data = "Disconnected";
                break;
            case TelephonyManager.DATA_CONNECTING:
                data = "Connecting";
                break;
            case TelephonyManager.DATA_CONNECTED:
                data = "Connected";
                break;
            case TelephonyManager.DATA_SUSPENDED:
                data = "Suspended";
                break;
            default:
                data = "Unknown";
                break;
        }
        return data;
    }

    private String getNetworkType(int networkType) {
        String type;
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_CDMA:
                type = "CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                type = "EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                type = "EVDO_0";
                break;
            case TelephonyManager.NETWORK_TYPE_GPRS:
                type = "GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                type = "HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                type = "HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                type = "IDEN";
                break;
            case TelephonyManager.NETWORK_TYPE_LTE:
                type = "LTE";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                type = "UTMS";
                break;
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                type = "Unknown";
                break;
            default:
                type = "Unknown";
                break;
        }
        return type;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F9:
                Util.Logout(DeviceInfoActivity.this);
                finish();
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(DeviceInfoActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(DeviceInfoActivity.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(DeviceInfoActivity.this, null);
                return true;
            case KeyEvent.KEYCODE_DEL:
                finish();

                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private class BatteryReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String chargeStatus;
            String chargePercent;
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            if (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL) {
                chargeStatus = "Charging";
            } else {
                chargeStatus = "Not Charging";
            }

            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0f;

            chargePercent = percentageDF.format((level / (float) scale) * 100);

            //  batteryStatusTV.setText(chargePercent+"% ("+chargeStatus+") "+ temperature+"C");
            batteryStatusTV.setText(chargePercent + "% (" + chargeStatus + ") ");
        }
    }


}
