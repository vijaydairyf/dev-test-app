package com.devapp.devmain.deviceinfo;

/**
 * Created by u_pendra on 22/12/17.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import com.devapp.devmain.server.DatabaseHandler;


public class GetDeviceStatusNew extends Service {

    Intent batteryStatus;

    DeviceStatusEntity deviceStatusEntity;
    TelephonyManager telephonyManager;

    float batteryPercent;
    boolean isCharging;
    String chargingType;
    float batteryTemperature;
    String batteryHealth;
    int batteryVoltage;
    float ambientTemperature;
    int networkAsu;
    String networkOperator;
    String dataStatus;
    String networkType;
    String simSerialNumber;
    DatabaseHandler dbHandler;
    HandlerThread handlerThread = new HandlerThread("GET DEVICE STATUS");
    Handler getDataHandler;
    private SensorManager mSensorManager;
    private Sensor mTempSensor;
    private int PERIODIC_CHECK_TIME = 10;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getStatus();
            postStatus();
            getDataHandler.postDelayed(runnable, PERIODIC_CHECK_TIME * 60 * 1000);
        }
    };

    public GetDeviceStatusNew() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        initialise();
        if (handlerThread.getState().equals(Thread.State.NEW)) {
            handlerThread.start();
            getDataHandler = new Handler(handlerThread.getLooper());
            getDataHandler.post(runnable);
        }
        // return super.onStartCommand(intent, flags, startId);
        stopSelf();
        return START_NOT_STICKY;
    }


    public void initialise() {
        dbHandler = DatabaseHandler.getDatabaseInstance();
        telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {

            @Override
            public void onDataConnectionStateChanged(int state, int networkType) {
                super.onDataConnectionStateChanged(state, networkType);

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
//                        dBm = 0;
                    }

                } else {
                    signalStrengthValue = signalStrength.getCdmaDbm();
//                    signalStrengthValue = (dBm+113)/2;
                }

                networkAsu = signalStrengthValue;
            }
        }, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mTempSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
//                Log.d("myDebug", "Temperature :" +sensorEvent.values[0]);
                ambientTemperature = sensorEvent.values[0];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        }, mTempSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    public void getStatus() {
        deviceStatusEntity = new DeviceStatusEntity();
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = this.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        batteryPercent = (level / (float) scale) * 100;
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        if (status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL) {
            isCharging = true;
        } else {
            isCharging = false;
        }
        int plugged = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        if (plugged == BatteryManager.BATTERY_PLUGGED_AC) {
            chargingType = "ac";
        } else if (plugged == BatteryManager.BATTERY_PLUGGED_USB) {
            chargingType = "usb";
        } else {
            chargingType = "na";
        }
        batteryTemperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0f;
        batteryHealth = getBatteryHealth(batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1));
        batteryVoltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        if (telephonyManager.getNetworkOperatorName() != null) {
            networkOperator = telephonyManager.getSimOperatorName().toUpperCase();
        } else {
            networkOperator = "";
        }

        GsmCellLocation gsmCellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
        /*gsmCellLocation.getLac();
        gsmCellLocation.getCid();*/


        dataStatus = getDataState(telephonyManager.getDataState());
        networkType = getNetworkType(telephonyManager.getNetworkType());
        simSerialNumber = telephonyManager.getSimSerialNumber();
        deviceStatusEntity.batteryPercent = batteryPercent;
        deviceStatusEntity.charging = isCharging;
        deviceStatusEntity.chargingType = chargingType;
        deviceStatusEntity.batteryTemperature = batteryTemperature;
        deviceStatusEntity.batteryHealth = batteryHealth;
        deviceStatusEntity.batteryVoltage = batteryVoltage;
        deviceStatusEntity.ambientTemperature = ambientTemperature;
        deviceStatusEntity.networkAsu = networkAsu;
        deviceStatusEntity.networkOperator = networkOperator;
        deviceStatusEntity.dataStatus = dataStatus;
        deviceStatusEntity.networkType = networkType;
        deviceStatusEntity.simSerialNumber = simSerialNumber;
        if (gsmCellLocation != null) {
            deviceStatusEntity.lac = gsmCellLocation.getLac();
            deviceStatusEntity.cellId = gsmCellLocation.getCid();
        }

        deviceStatusEntity.time = System.currentTimeMillis();
        deviceStatusEntity.sent = DatabaseHandler.COL_REC_NW_UNSENT;
       /* try {
            deviceStatusEntity.apkVersion = this.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            deviceStatusEntity.apkVersion = "null";
        }*/
    }

    public void postStatus() {
        if (deviceStatusEntity != null) {
            dbHandler.insertOrUpdateDeviceStatus(deviceStatusEntity);
        }
    }

    public String getBatteryHealth(int health) {
        String bh;
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_COLD: {
                bh = "cold";
                break;
            }
            case BatteryManager.BATTERY_HEALTH_DEAD: {
                bh = "dead";
                break;
            }
            case BatteryManager.BATTERY_HEALTH_GOOD: {
                bh = "good";
                break;
            }
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: {
                bh = "overvoltage";
                break;
            }
            case BatteryManager.BATTERY_HEALTH_OVERHEAT: {
                bh = "overheat";
                break;
            }
            case BatteryManager.BATTERY_HEALTH_UNKNOWN: {
                bh = "unknown";
                break;
            }
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE: {
                bh = "unspecifiedfailure";
                break;
            }
            default: {
                bh = "unknown";
                break;
            }

        }
        return bh;
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

}