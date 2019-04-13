package com.devapp.devmain.main;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.entity.PostEndShift;
import com.devapp.devmain.helper.OnStartTask;
import com.devapp.devmain.json.JSONParser;
import com.devapp.devmain.license.LicenseActivity;
import com.devapp.devmain.license.deviceInfo.BuildInfo;
import com.devapp.devmain.license.deviceInfo.DeviceInfoEntity;
import com.devapp.devmain.license.deviceInfo.DeviceInfoManager;
import com.devapp.devmain.license.deviceInfo.KernalInfo;
import com.devapp.devmain.license.deviceInfo.TelephoneInfo;
import com.devapp.devmain.peripherals.network.HotspotManager;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.AppWifi;
import com.devapp.devmain.util.logger.LogWrapper;
import com.devapp.devmain.util.logger.MessageOnlyLogFilter;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.syncapp.service.APService;
import com.devapp.syncapp.service.BTPService;
import com.devApp.R;

import org.apache.commons.lang3.ArrayUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;


public class SplashActivity extends Activity implements AnimationListener {

    private static final String TAG = "SplashActivity";

    public static int sendCount;
    public static PostEndShift serVerPoEndShift = null;
    public static int deviceRefreshActivityCount;
    public static boolean isClearActivity;
    private final Handler splashHandler = new Handler();
    public boolean networkReceiverForLic = false;
    public boolean networkReceiverForSundayLic = false;
    private boolean isServiceRunning = false;
    private boolean licenseDataReset = false;
    private Runnable updateRunnable;
    private SessionManager session;
    private AmcuConfig amcuConfig;
    private boolean validLicense = false;
    private int statusCode;
    private int licenseStatusCode;
    private String isValid;
    private String valid;
    private String message;
    private long requestDate;
    private long startDate;
    private long endDate;
    private String licenseJson;
    private String deviceInfoManagerString;
    private ProgressBar progressBar;
    private Context context = this;
    private boolean waitCall = false;
    private int day;
    private Dialog syncDialog;
    private boolean isBootComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        try {
            String str = getIntent().getStringExtra(LauncherActivity.BOOT_COMPLETE);
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Not from launcher");
        }

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        syncDialog = Util.showSyncing(this);
        if (syncDialog != null) {
            syncDialog.show();
            if (syncDialog.isShowing()) {
                progressBar.setVisibility(View.GONE);
                syncDialog.dismiss();
            }
        }


        // Initialize the logging framework.

        initializeLogging();
        afterOnCreateServiceCall();


    }

    private void TurnOnBTAndAP4SmartAmcuLite() {
        try {
            startService(new Intent(context, APService.class));
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null) {
                Toast.makeText(context, "Bluetooth not supported on device", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(context, BTPService.class);
            intent.setAction(BTPService.START);
            startService(intent);
        } catch (Exception e) {
            if (e != null)
                e.printStackTrace();
        }
    }

    /**
     * Create a chain of targets that will receive log data
     */
    private void initializeLogging() {

        // Using Log, front-end to the logging chain, emulates
        // android.util.log method signatures.

        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        com.devapp.devmain.util.logger.Log.setLogNode(logWrapper);

        // A filter that strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);
        Log.i(TAG, "logging initialized...");
    }

    private void enableWiFiOrHotspot() {
        boolean enableHotspot = amcuConfig.getHotspotValue();
        boolean enableWifi = amcuConfig.getWifiValue();
        if (enableHotspot) {

            String hotspotSsid = amcuConfig.getHotspotSsid();
            String previousSsid = amcuConfig.getPreviousHotspotSsid();

            int pos = ArrayUtils.indexOf(SmartCCConstants.ssidList, hotspotSsid);
            Log.v(PROBER, "SSID pos is: " + pos);
            if (previousSsid == null || hotspotSsid.equalsIgnoreCase(previousSsid)) {
                if (pos != -1)
                    HotspotManager.enableHotspot(context, SmartCCConstants.ssidList[pos],
                            SmartCCConstants.passwordList[pos]);
                else
                    HotspotManager.enableHotspot(context, SmartCCConstants.ssidList[0],
                            SmartCCConstants.passwordList[0]);
            } else {
                amcuConfig.setPreviousHotspotSsid(hotspotSsid);
                if (pos != -1)
                    HotspotManager.changeHotspot(context, SmartCCConstants.ssidList[pos],
                            SmartCCConstants.passwordList[pos]);
                else
                    HotspotManager.changeHotspot(context, SmartCCConstants.ssidList[0],
                            SmartCCConstants.passwordList[0]);
            }
        } else if (enableWifi) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    AppWifi.Join(SplashActivity.this, wifiManager);
                    try {
                        Thread.sleep(2500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    public void afterOnCreateServiceCall() {
        SharedPreferences sp = getSharedPreferences(AmcuConfig.PREF_NAME, 0);
        sp.edit().putBoolean(AmcuConfig.KEY_FOR_SHUTDOWN_ALERT_FLAG, false).apply();
        if (sp.getBoolean(AmcuConfig.KEY_SET_BOOT_COMPLETED, false)
                || Util.isNetworkAvailable(this)) {

            amcuConfig = AmcuConfig.getInstance();
            JSONParser.jObj = null;
            sendCount = 0;
            session = new SessionManager(SplashActivity.this);
            DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
            //This read to create the database
            dbh.handleAppUpgrade(DatabaseHandler.getPrimaryDatabase(), 0);
            Cursor cursor1 = dbh.getSocieties();
            Cursor cursor = dbh.getLicenseData();
            enableWiFiOrHotspot();
            TextView tvversion = (TextView) findViewById(R.id.tvversionName);
            String versionName = null;
            try {
                versionName = getApplicationContext()
                        .getPackageManager()
                        .getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
                versionName = String.valueOf(versionName);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
            if (versionName != null) {
                tvversion.setText("Version" + " " + versionName);
            } else {
                tvversion.setText("1.0.0");
            }

            new OnStartTask(SplashActivity.this).startGetDeviceStatusService();
            //Licence check commented
            if (amcuConfig.getFirstTimeLicenseEntry()) {
                httpLicenceRequest();
            } else {
                checkForExpiryDateLicense();
            }
            if (cursor != null)
                cursor.close();

            if (cursor1 != null)
                cursor1.close();
        } else {
            // Util.displayErrorToast("Please re-boot the tab.",getApplicationContext());
            finish();
        }
        isServiceRunning = false;
    }

    public void checkForExpiryDateLicense() {
        long currenttime, endDate = 0, startDate = 0;
        Calendar calendar = Calendar.getInstance();
        currenttime = calendar.getTimeInMillis();

        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        Cursor cursor = dbh.getLicenseData();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    endDate = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_LICENSE_END_DATE));
                    startDate = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_LICENSE_START_DATE));
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null)
            cursor.close();

        if (endDate != 0 && currenttime > endDate) {
            changeLicenseDataAfterExpire();
            gotoLicenseScreen();
        } else if (startDate != 0 && currenttime < startDate) {
            changeLicenseDataAfterExpire();
            gotoLicenseScreen();
        } else {
            day = calendar.get(Calendar.DAY_OF_WEEK);
            if (day != 1) {
                amcuConfig.setSundayLicenseCheck(false);
                amcuConfig.setCalledForSuday(false);
            }
            if (day == 1) {
                if (!amcuConfig.getSundayLicenseCheck() && Util.isNetworkAvailable(this)) {
                    waitCall = true;
                    Intent intent = new Intent(this, SplashActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    amcuConfig.setFirstTimeLicenseEntry(true);
                    amcuConfig.setSundayLicenseCheck(true);
                    amcuConfig.setonCreateDBCall(true);
                    finish();

                }
            }
            if (gotoValidLicense() && !waitCall) {
                UpdateUI();
            } else {

                if (gotoValidLicense() && waitCall) {

                } else {
                    gotoLicenseScreen();
                }

            }
        }

    }

    public void httpLicenceRequest() {
        String LICENSE_URL = Util.LICENSE_URL;
        if (Util.isNetworkAvailable(this)) {
            new AsyncHttpLicense().execute(LICENSE_URL);
        } else if (!amcuConfig.getonCreateDbCall()) {
            //upgrade apk
            UpdateUI();
        } else {
            // frsh install if network not available
            new AsyncHttpLicense().execute(LICENSE_URL);
        }
    }

    public void networkReceiverLicAsyncTask(Context contextNetwork) {
        context = contextNetwork;
        amcuConfig = AmcuConfig.getInstance();
        String LICENSE_URL = Util.LICENSE_URL;
        new AsyncHttpLicense().execute(LICENSE_URL);
    }

    /**
     * To parse the licensed Json,
     * need to add enhanced whitelist url feature
     *
     * @param licenseJson
     */
    public void parseLicenseJson(String licenseJson) {

        //  licenseJson = "{\"result\": {\"statuscode\" : 400,\"validitystartdate\": 1467112955345,\"validityenddate\": 1467112955345,\"validity\": \"Yes\",\"message\": \"this is valid\"}}";
        try {
            if (!Util.isNetworkAvailable(context)) {

                Util.displayErrorToast("No internet connection", context);
            }
            JSONObject reader = new JSONObject(licenseJson);
            JSONObject result = reader.getJSONObject("result");
            requestDate = System.currentTimeMillis();
            if (!result.isNull("validityStartDate") && !result.isNull("validityEndDate")) {
                startDate = result.getLong("validityStartDate");
                endDate = result.getLong("validityEndDate");
            } else {
                startDate = requestDate;
                endDate = requestDate;
            }
            licenseStatusCode = result.getInt("statusCode");
            valid = result.getString("validity");
            message = result.getString("message");
            amcuConfig.setLicenceJson(licenseJson);
            amcuConfig.setWhiteListURL("NA");

            setLicenseDataToSP();
        } catch (JSONException e) {
            e.printStackTrace();
            //  UpdateUI();

        }

    }


    public void setLicenseDataToSP() {
        amcuConfig.setLicRequestTime(requestDate);
        amcuConfig.setLicStatusCode(licenseStatusCode);
        amcuConfig.setLicStartDate(startDate);
        amcuConfig.setLicEndDate(endDate);
        amcuConfig.setLicVaidityStatus(valid);
        amcuConfig.setLicMessage(message);
        insertInLicenseTabale();
    }

    public void insertInLicenseTabale() {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        long stat = dbh.insertLicenseData();
        amcuConfig.setFirstTimeLicenseEntry(false);
        //now onupgrade as on create
        amcuConfig.setonCreateDBCall(true);
        Cursor cursor = dbh.getLicenseData();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    endDate = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_LICENSE_END_DATE));
                    startDate = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_LICENSE_START_DATE));
                } while (cursor.moveToNext());
            }
        }
        if (cursor != null)
            cursor.close();

        if (networkReceiverForSundayLic) {
            amcuConfig.setSundayLicenseCheck(true);
            amcuConfig.setonCreateDBCall(true);
        }

        if (!networkReceiverForLic) {
            if (licenseDataReset) {
                gotoLicenseScreen();
            } else {
                checkForExpiryDateLicense();
            }
            if (stat == -1) {
                Toast.makeText(context, "Problem in License value", Toast.LENGTH_LONG).show();
                setLicenseDataToSP();
            }
        }

    }

    public boolean gotoValidLicense() {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        Cursor cursor = dbh.getLicenseData();

        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    statusCode = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_LICENSE_STATUS_CODE));
                    isValid = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_LICENSE_VALID));
                    endDate = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_LICENSE_END_DATE));

                } while (cursor.moveToNext());
            }
        }
        if (statusCode == 200 && isValid.equalsIgnoreCase("yes")) {
            validLicense = true;
        } else {
            validLicense = false;
        }
        if (cursor != null)
            cursor.close();
        return validLicense;
    }

    public void gotoLicenseScreen() {
        Intent intent = new Intent(this, LicenseActivity.class);
        startActivity(intent);
    }

    public void UpdateUI() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                splashHandler.post(updateRunnable);
            }
        }).start();
        updateRunnable = new Runnable() {

            @Override
            public void run() {

                Intent intent;
                intent = new Intent(SplashActivity.this,
                        DeviceListActivity.class);
                intent.putExtra("fromSplashActivity", true);
                deviceRefreshActivityCount = 0;
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);
                finish();
            }
        };
    }

    @Override
    public void onAnimationEnd(Animation arg0) {

    }

    @Override
    public void onAnimationRepeat(Animation arg0) {

    }

    @Override
    public void onAnimationStart(Animation arg0) {

    }

    @Override
    protected void onResume() {
        //new SaveSession(SplashActivity.this).setShutDownAlertFlag(false);
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        deviceRefreshActivityCount = 0;
        if (!isServiceRunning) {
            progressBar.setVisibility(View.VISIBLE);
           /* Intent intent = new Intent(this, CheckDBService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);*/
        } else {
            progressBar.setVisibility(View.GONE);
        }

        if (session.getKeyBluetoothEnable()) {
            TurnOnBTAndAP4SmartAmcuLite();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        splashHandler.removeCallbacks(updateRunnable);
    }

    private String toJson(Object obj) {

        String jsonString = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonString = mapper.writeValueAsString(obj);

        } catch (JsonGenerationException ex) {
            ex.printStackTrace();
        } catch (JsonMappingException ex) {
            ex.printStackTrace();

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return jsonString;
    }

    public void changeLicenseDataAfterExpire() {
        requestDate = System.currentTimeMillis();
        amcuConfig.setLicRequestTime(requestDate);
        amcuConfig.setLicStatusCode(204);
        amcuConfig.setLicStartDate(requestDate);
        amcuConfig.setLicEndDate(requestDate);
        amcuConfig.setLicVaidityStatus("No");
        amcuConfig.setLicMessage("This is not a valid device,License has expired");
        licenseDataReset = true;
        insertInLicenseTabale();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_DEL:
                Util.restartTab(SplashActivity.this);

                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public class AsyncHttpLicense extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            StringBuilder responseOutput = null;
            try {

                Calendar calendar = Calendar.getInstance();
                day = calendar.get(Calendar.DAY_OF_WEEK);
                if (day == 1 && amcuConfig.getSundayLicenseCheck()) {
                    amcuConfig.setFirstTimeLicenseEntry(false);
                }
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);
            /*

                  List<NameValuePair> obj = new ArrayList<NameValuePair>();
                  obj.add(new BasicNameValuePair("postData", params[0]));
            */

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

                DeviceInfoManager.getInstance(context).setAllData();
                DeviceInfoEntity deviceInfoEntity = new DeviceInfoEntity();
                deviceInfoEntity.buildInfosList = BuildInfo.getInstance();
                deviceInfoEntity.kernalInfoList = KernalInfo.getInstance();
                deviceInfoEntity.telephoneInfoList = TelephoneInfo.getInstance(context);
                deviceInfoManagerString = toJson(deviceInfoEntity);

                writer.write(deviceInfoManagerString);
                //writer.write(getQuery(obj));
                writer.flush();
                writer.close();
                os.close();
                conn.connect();

                int responseCode = conn.getResponseCode(); // getting the response code

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    final StringBuilder output = new StringBuilder("Request URL " + url);
                    output.append(System.getProperty("line.separator") + "Request Parameters " + params[0]);
                    output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line = "";
                    responseOutput = new StringBuilder();
                    while ((line = br.readLine()) != null) {
                        responseOutput.append(line);
                    }
                    br.close();
                }
            } catch (javax.net.ssl.SSLHandshakeException e1) {
                Util.displayErrorToast("Please check the Tab time! ", SplashActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (responseOutput != null) {
                return responseOutput.toString();
            } else {
                return null;
            }

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                licenseJson = result;
                parseLicenseJson(licenseJson);
            }
            //UpdateUI();
        }


    }

}




