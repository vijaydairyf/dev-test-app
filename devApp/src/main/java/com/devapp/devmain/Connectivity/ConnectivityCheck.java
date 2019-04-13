package com.devapp.devmain.Connectivity;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.ServerAPI;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.FocusForwardKeyListener;
import com.devapp.devmain.util.RegexInputFilter;
import com.devApp.R;

public class ConnectivityCheck extends Activity {
    SMSContentObserver smsContentObserver;
    SmsManager smsManager;
    String url;
    Uri uri;
    SessionManager sessionManager;
    AmcuConfig amcuConfig;
    EditText phonenum;
    ProgressBar progress, progress2;
    ImageView donesms, nosms;
    Button btnSendSms, btnCheckConnectivity;
    Handler handler;
    boolean status = false;
    // network check
    boolean isAuthenticated;
    boolean isConnected;
    Context mContext;
    Handler myHandler;
    Runnable updateRunnable;
    TextView txtNetwork;
    ImageView gNetwork, rNetwork, oNetwork;

    String responseCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectivity_check);

        mContext = getApplicationContext();
        phonenum = (EditText) findViewById(R.id.smsNumber);
        progress = (ProgressBar) findViewById(R.id.progress);
        progress2 = (ProgressBar) findViewById(R.id.progress2);
        donesms = (ImageView) findViewById(R.id.donesms);
        nosms = (ImageView) findViewById(R.id.nosms);
        btnSendSms = (Button) findViewById(R.id.btnSignIn);
        btnCheckConnectivity = (Button) findViewById(R.id.btnnetwork);
        smsContentObserver = new SMSContentObserver(handler);
        sessionManager = new SessionManager(ConnectivityCheck.this);
        amcuConfig = AmcuConfig.getInstance();
        smsManager = SmsManager.getDefault();
        url = "content://sms/";
        uri = Uri.parse(url);
        phonenum.setOnKeyListener(new FocusForwardKeyListener(btnSendSms));
        phonenum.setFilters(new InputFilter[]{new RegexInputFilter("[0-9]*")});
        handler = new Handler();
        myHandler = new Handler();
        gNetwork = (ImageView) findViewById(R.id.gNetwork);
        rNetwork = (ImageView) findViewById(R.id.rNetwork);
        oNetwork = (ImageView) findViewById(R.id.oNetwork);
        txtNetwork = (TextView) findViewById(R.id.txtNetwork);
        btnCheckConnectivity.requestFocus();

    }

    @Override
    protected void onPause() {
        if (null != smsContentObserver)
            getContentResolver().unregisterContentObserver(smsContentObserver);
        super.onPause();
    }

    public void gotoSMS(View view) {

        status = false;
        progress.setVisibility(View.VISIBLE);
        nosms.setVisibility(View.GONE);
        donesms.setVisibility(View.GONE);
        String countryISOCode = "NA";
        TelephonyManager teleMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (teleMgr != null) {
            countryISOCode = teleMgr.getSimCountryIso();
            if (phonenum.getText().toString().trim().length() == 10 && countryISOCode.equalsIgnoreCase("in")) {
                sendSMS(phonenum.getText().toString());
            } else {
                progress.setVisibility(View.GONE);
                nosms.setVisibility(View.VISIBLE);
                donesms.setVisibility(View.GONE);
            }
        }/*else{
            progress.setVisibility(View.GONE);
            nosms.setVisibility(View.GONE);
            donesms.setVisibility(View.VISIBLE);
        }*/

    }

    public boolean basicCheck(String number) {
        String countryISOCode = "NA";
        TelephonyManager teleMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (teleMgr != null) {
            countryISOCode = teleMgr.getSimCountryIso();
        }
        if (number.length() == 10 && countryISOCode.equalsIgnoreCase("in")) {
            return true;
        } else {
            donesms.setVisibility(View.VISIBLE);
        }
        return false;
    }

    public String getContentForSms() {
        String sms = Util.getTodayDateAndTime(7, mContext, false) + "/"
                + Util.getTodayDateAndTime(3, mContext, false) + "\n" +
                "smartAMCU SMS Test \n CenterID: "
                + sessionManager.getCollectionID() + "\n"
                + Util.getFooterUrl(ConnectivityCheck.this);

        return sms;

    }


    public void sendSMS(String phoneNo) {

        smsManager.sendTextMessage(phoneNo, null, getContentForSms(), null, null);

        getContentResolver().registerContentObserver(uri, true, smsContentObserver);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!status) {
                    progress.setVisibility(View.GONE);
                    nosms.setVisibility(View.VISIBLE);
                }

            }
        }, 8000);
    }

    /// internet check
    public void gotoNetwork(View view) {
        isAuthenticated = false;
        progress2.setVisibility(View.VISIBLE);

        gNetwork.setVisibility(View.GONE);
        rNetwork.setVisibility(View.GONE);
        oNetwork.setVisibility(View.GONE);
        txtNetwork.setText("Checking Connectivity");
        checkConnectivity();
    }

    public void checkConnectivity() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    checkCredentials();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
        updateRunnable = new Runnable() {

            @Override
            public void run() {
                if (isAuthenticated) {
                    progress2.setVisibility(View.GONE);
                    gNetwork.setVisibility(View.VISIBLE);
                    txtNetwork.setTextColor(Color.parseColor("#088A08"));
                    txtNetwork.setText("Data connectivity available.");

                    // Toast.makeText(ConnectivityCheck.this, "Successfully logged In.", Toast.LENGTH_SHORT).show();
                } else {
                    isAuthenticated = false;
                    if (!isConnected) {
                        //"Please check the connectivity!";
                        progress2.setVisibility(View.GONE);
                        rNetwork.setVisibility(View.VISIBLE);
                        txtNetwork.setTextColor(Color.parseColor("#DF0101"));
                        txtNetwork.setText("Data connectivity not available.");
                        // Toast.makeText(ConnectivityCheck.this, "Please check the connectivity", Toast.LENGTH_SHORT).show();

                    } else {
                        progress2.setVisibility(View.GONE);
                        oNetwork.setVisibility(View.VISIBLE);
                        txtNetwork.setTextColor(Color.parseColor("#B18904"));
                        txtNetwork.setText(responseCode);
                        //  Toast.makeText(ConnectivityCheck.this, "Log in failed!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
    }

    public void checkCredentials() {

        if (ServerAPI.isNetworkAvailable(mContext)) {
            if (!isAuthenticated) {
                isConnected = true;
                responseCode = ServerAPI.userConnectivity(mContext);
                if (responseCode.contains("Success"))
                    isAuthenticated = true;
                myHandler.post(updateRunnable);

            } else if (isAuthenticated) {

                myHandler.post(updateRunnable);
            } else {
                isAuthenticated = false;
                myHandler.post(updateRunnable);
            }
        } else {
            isConnected = false;
            myHandler.post(updateRunnable);
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F9:
                Util.Logout(ConnectivityCheck.this);
                finish();
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(ConnectivityCheck.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(ConnectivityCheck.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(ConnectivityCheck.this, null);
                return true;
            case KeyEvent.KEYCODE_DEL:
                finish();
                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    class SMSContentObserver extends ContentObserver {


        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public SMSContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return false;
        }

        @Override
        public void onChange(boolean arg0) {
            super.onChange(arg0);
            String p = "";
            // Create Sent box URI
            Uri sentURI = Uri.parse("content://sms/sent");

            // List required columns
            String[] reqCols = new String[]{"_id", "address", "body"};

            // Fetch Sent SMS Message from Built-in Content Provider
            Cursor c = getContentResolver().query(sentURI, reqCols, null, null, null);
            if (null != c) {
                c.moveToFirst();
                p = c.getString(c.getColumnIndex("BODY")) + c.getString(c.getColumnIndex("ADDRESS"));
                status = true;
                ConnectivityCheck.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                        donesms.setVisibility(View.VISIBLE);
                        if (handler != null) {
                            handler.removeCallbacksAndMessages(null);
                        }
                    }
                });

            }
        }

    }
}
