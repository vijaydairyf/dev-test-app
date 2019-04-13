package com.devapp.devmain.main;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.gsm.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.Connectivity.NetworkTest;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.UserEntity;
import com.devapp.devmain.helper.AfterLogInTask;
import com.devapp.devmain.helper.DeviceDataActivity;
import com.devapp.devmain.helper.UserRole;
import com.devapp.devmain.helper.UserType;
import com.devapp.devmain.macollection.CollectionActivity;
import com.devapp.devmain.macollection.MCCCollectionActivity;
import com.devapp.devmain.macollection.SampleCollectionActivity;
import com.devapp.devmain.milkline.DispatchSupervisorActivity;
import com.devapp.devmain.milkline.SupervisorActivity;
import com.devapp.devmain.peripherals.services.PingService;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.LogInService;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.PolicyManager;
import com.devapp.devmain.user.AllSocietyActivity;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.FocusForwardKeyListener;
import com.devapp.devmain.util.MoblieDataDialog;
import com.devapp.smartcc.PortConfigurationActivity;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import org.apache.http.client.CookieStore;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Locale;

public class LoginActivity extends Activity implements MoblieDataDialog.MobileData {

    public static Object mLock = new Object();
    public static CookieStore mCookie = null;
    public static String clientName = "MILMA";

    private final Handler logInHandler = new Handler();
    ProgressDialog pd;
    private boolean isRemember = false;
    private boolean isMobileEnable;
    private Button _btnLogin;
    private Button _btnCancel;
    private TextView _txtforgotPwd;
    private TextView _txtRememberMe;
    private SessionManager session;
    private Runnable updateRunnable;
    private boolean isAuthenticated;
    private EditText _editPassword;
    private AutoCompleteTextView _editEmail;
    private CheckBox checkRemember;
    private CheckBox checkPassword;
    private UserEntity userEntity;
    private AmcuConfig amcuConfig;
    private Handler mobileDataHandler = new Handler();
    private boolean isEnable = false;
    private Runnable mobileRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //To make the screen awake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Util.isShutDown = false;
//        cbKeyboard = (CheckBox) findViewById(R.id.cb_keyboard);
        _editEmail = (AutoCompleteTextView) findViewById(R.id.editEmail);
        _editPassword = (EditText) findViewById(R.id.editPwd);
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(LoginActivity.this);
        // UnderLine forget password
        _txtforgotPwd = (TextView) findViewById(R.id.txtForgotPassword);
        _txtforgotPwd.setPaintFlags(_txtforgotPwd.getPaintFlags()
                | Paint.UNDERLINE_TEXT_FLAG);
        _txtforgotPwd.setText(getResources()
                .getString(R.string.forget_password));
        _txtforgotPwd.setTextSize(17);
        // Forgot password not required
        _txtforgotPwd.setVisibility(View.GONE);

        _txtRememberMe = (TextView) findViewById(R.id.tvRemember);
//        _txtShowPassword = (TextView) findViewById(R.id.txtShowpass);

        _editEmail.setOnKeyListener(new FocusForwardKeyListener(_editPassword));
        _btnLogin = (Button) findViewById(R.id.btnSignIn);
        checkRemember = (CheckBox) findViewById(R.id.checkremember);
        checkPassword = (CheckBox) findViewById(R.id.checkpassword);
        checkRemember.setChecked(true);
        checkPassword.setChecked(false);

        //To set timezone and disable the unknown source
        Util.setTimeAndTimeZone(LoginActivity.this);
        Util.setNonMarketAppDisable(LoginActivity.this);

        _editPassword.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                    return true;
                }
                if ((event.getAction() == KeyEvent.ACTION_UP)
                        && ((keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    logIn();
                    return true;
                }
                return false;
            }
        });

        _txtforgotPwd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_editEmail.getText().toString() != null) {
                    getMobileNumer();
                } else {
                    Toast.makeText(LoginActivity.this,
                            "Please enter user name", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        checkPassword.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (!isChecked) {
                    _editPassword
                            .setTransformationMethod(new PasswordTransformationMethod());
                    _editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.invisible, 0);
                } else {
                    _editPassword.setTransformationMethod(null);
                    _editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visible, 0);
                }
            }
        });

        checkRemember.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    isRemember = true;
                } else
                    isRemember = false;
            }
        });

        _btnLogin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    logIn();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        });
        _btnCancel = (Button) findViewById(R.id.btnCancel);
        _btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Util.Logout(LoginActivity.this);

//                finish();
            }
        });
      /*  cbKeyboard.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(LoginActivity.this, "Keyboard:" + getResources().getConfiguration().hardKeyboardHidden,
                            Toast.LENGTH_SHORT).show();
                    if (getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {

                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                                .showInputMethodPicker();
                    }
                }
            }
        });*/

        Util.alphabetValidation(_editEmail, Util.ONLY_ALPHANUMERIC, LoginActivity.this, 0);
        if (session.getSelectedLanguage().equalsIgnoreCase("Kannada")) {
            setKannada();

        }
        _editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isAuthenticated = passwordAuthentication(_editEmail
                        .getText().toString()
                        .toUpperCase(Locale.ENGLISH));

                if (isAuthenticated) {
                    _editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visible, 0);
                } else if (!checkPassword.isChecked()) {
                    _editPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.invisible, 0);
                }
            }
        });
        _editEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_user, 0);
        startPingService();
    }

    @Override
    protected void onResume() {
//        startNetworkCheckService();

        AmcuConfig.getInstance().setShutDownAlertFlag(false);
        switchOnOrOffMobileData();
        NetworkTest netWorkTest = new NetworkTest(getApplicationContext());
        boolean checkForMobileDataEnableOrDisable = netWorkTest.toCheckForMobileData();

        if (checkForMobileDataEnableOrDisable) {
            if (!amcuConfig.getMobileData() && !isMobileEnable) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTrans = fragmentManager.beginTransaction();
                MoblieDataDialog dialog = new MoblieDataDialog();
                dialog.show(fragmentTrans, "D");
            } else if (!isMobileEnable) {
                enableMoblieData();
            }
        }

        super.onResume();

    }

    private void startPingService() {
        Intent intent = new Intent(LoginActivity.this, PingService.class);
        startService(intent);

    }

    private void getMobileNumer() {

        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();

        try {
            userEntity = db.getPassword(_editEmail.getText().toString()
                    .toUpperCase());

            if (userEntity != null) {
                String passWord = userEntity.password;
                String mobileNum = userEntity.mobile;
                sendSMS(mobileNum, passWord);
            } else {
                Toast.makeText(LoginActivity.this, "Incorrect UserID!!",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        if ((phoneNumber == null || phoneNumber.length() < 9)
                && message == null) {
            Toast.makeText(LoginActivity.this,
                    getResources().getString(R.string.contact_admin) + " !",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(LoginActivity.this, "message sending..",
                    Toast.LENGTH_SHORT).show();

            message = "Your password is " + message;

            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(SENT), 0);

            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(DELIVERED), 0);

            // ---when the SMS has been sent---
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS sent",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(getBaseContext(), "Generic failure",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(getBaseContext(), "No service",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(getBaseContext(), "Null PDU",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(getBaseContext(), "Radio off",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(SENT));

            // ---when the SMS has been delivered---
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS delivered",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(getBaseContext(), "SMS not delivered",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(DELIVERED));

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        }
    }

    private boolean passwordAuthentication(String userId) {
        UserEntity userEntity = new UserEntity();

        String password = null;
        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();

        try {
            userEntity = db.getPassword(userId);
            password = userEntity.password;

            session.setUserMobile(userEntity.mobile);
            session.setUserRole(userEntity.role);
            try {
                session.setSocietyColumnId(Integer
                        .parseInt(userEntity.centerId));
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (password != null
                && password
                .equalsIgnoreCase(_editPassword.getText().toString())) {

            db.insertOrReplaceUserHistory(userEntity.userId, userEntity.role);

            return true;
        } else {
            return false;
        }

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStop() {
        SplashActivity.deviceRefreshActivityCount = 0;
        logInHandler.removeCallbacks(updateRunnable);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    /*    try {
            Util.doCmdsForIptables(LoginActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        /*Test test = new Test(LoginActivity.this);
        test.allowTestConfiguration();*/
//Configuration enabled for NDDB
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        amcuConfig.setManualDisplay(
                true);
        AfterLogInTask afterLogInTask = new AfterLogInTask(getApplicationContext());
        afterLogInTask.doAfterLogInTasks();
        AfterLogInTask.startPurgeDataService(LoginActivity.this);
        mobileDataStatus();
        logInServiceForRatechart();
//        if (Util.settingEntity.onlyManual != null
//                && !Util.settingEntity.onlyManual.equalsIgnoreCase("true")) {
//            showKeyboard();
//        }

        setLoginAdapter();

    }


    private void showKeyboard() {
        if (getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showInputMethodPicker();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();


    }

    private void setKannada() {
        _editEmail.setHint(getResources().getString(R.string.user_namek));
        _editPassword.setHint(getResources().getString(R.string.passwordk));
        _txtRememberMe.setText(getResources().getString(R.string.remember_mek));
//        _txtShowPassword.setText(getResources().getString(R.string.show_passk));

        _btnLogin.setText(getResources().getString(R.string.sign_ink));
        _btnCancel.setText(getResources().getString(R.string.cancelk));

        _txtforgotPwd.setText(getResources().getString(
                R.string.forget_passwordk));

    }

    private void logIn() {

        SmartCCUtil smartCCUtil = new SmartCCUtil(getApplicationContext());
        boolean isValidTabDate = smartCCUtil.isValidTabTime();

        if (!isValidTabDate) {
            return;
        } else if (checkForTerminalActivity()) {
            openTerminalActivity();
        } else if ((_editEmail.getText().toString().trim().length() > 0
                && _editPassword.length() > 0)) {
//            pd = ProgressDialog.show(LoginActivity.this, "", "Loading...",
//                    true, false);
            Util.hideSoftKeyboard(LoginActivity.this);

            final String email = _editEmail.getText().toString();
            final String password = _editPassword.getText().toString();

            if (email.length() > 0 && password.length() > 0) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        isAuthenticated = passwordAuthentication(_editEmail
                                .getText().toString()
                                .toUpperCase(Locale.ENGLISH));
                        logInHandler.post(updateRunnable);

                    }
                }).start();

                updateRunnable = new Runnable() {
                    @Override
                    public void run() {

                        //          pd.dismiss();
                        if (isAuthenticated) {
                            session.createLoginSession(email, password);
                            if (isRemember) {
                                session.setIsRememberPassword();
                            }

                            Util.setSociety(LoginActivity.this,
                                    email.toUpperCase(Locale.ENGLISH));
                            Util.setManagerDetails(LoginActivity.this);
                            //To post shift data on cloud
                            // We dont want to be sending this data again to Cloud now
                            // PostCollectionRecords Service will take care of this from now on
                            Log.d("LoginActivity", "No Longer Invoking Collection Records Posting for Login from LoginActivity");
                            // Util.postDataToCloud(LoginActivity.this);
                            if (session.getUserRole().equalsIgnoreCase(
                                    "Manager")) {
                                Intent intent = new Intent(LoginActivity.this,
                                        AllSocietyActivity.class);
                                goToNextActivity(intent);

                            } else if (session.getUserRole().equalsIgnoreCase(UserRole.TANKER_SUPERVISOR)) {
                                Intent intent = new Intent(LoginActivity.this,
                                        SupervisorActivity.class);
                                goToNextActivity(intent);
                            } else if (session.getUserRole().equalsIgnoreCase(UserRole.DISPATCH_SUPERVISOR)) {
                                Intent intent = new Intent(LoginActivity.this,
                                        DispatchSupervisorActivity.class);
                                goToNextActivity(intent);
                            } else {
                                checkForOperatorActivity();

                            }

                        } else {

                            _editPassword.setText("");
                            _editPassword.requestFocus();
                            Toast.makeText(LoginActivity.this,
                                    "Invalid user name or password!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };
            }
        } else {

            if (_editPassword.getText().toString().equalsIgnoreCase("")) {
                _editPassword.requestFocus();

                Toast.makeText(LoginActivity.this, "Enter password!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F10:
                Util.restartApp(LoginActivity.this);
                return true;
            case KeyEvent.KEYCODE_F11:
                Util.restartTab(LoginActivity.this);
                return true;
            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(LoginActivity.this, null);
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void logInServiceForRatechart() {
        amcuConfig.setLogInFor(Util.LOGINRATECHART);
        startService(new Intent(LoginActivity.this, LogInService.class));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 111) {
            Toast.makeText(LoginActivity.this, "Change pin", Toast.LENGTH_SHORT).show();
        } else if (resultCode == Activity.RESULT_OK
                && requestCode == PolicyManager.DPM_ACTIVATION_REQUEST_CODE) {

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void checkForOperatorActivity() {
        session.setFarmOrBarcode(true);
        Intent intent = new Intent(LoginActivity.this,
                FarmerScannerActivity.class);
        CollectionRecordDao collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        ReportEntity repEnt = null;
        SmartCCUtil smartCCUtil = new SmartCCUtil(LoginActivity.this);

        String[] strArray = smartCCUtil.getReportFormatDate().split("-");
        StringBuilder yyyymmddDate = new StringBuilder(strArray[0]).
                append(strArray[1]).append(strArray[2]);
        try {
            repEnt = collectionRecordDao.findUncommitedRecordByDateAndShift(
                    yyyymmddDate.toString(), SmartCCUtil.getFullShift(Util.getCurrentShift()));
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (repEnt != null && repEnt.milkType != null
                && (!repEnt.recordStatus.equalsIgnoreCase(Util.RECORD_STATUS_INCOMPLETE))) {

            session.setFarmerID(repEnt.farmerId);
            session.setFarmerName(repEnt.farmerName);
            if (!repEnt.milkType.equalsIgnoreCase("TEST")) {
                session.setMilkType(repEnt.milkType);
            }

            if (repEnt.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
                intent = new Intent(LoginActivity.this, SampleCollectionActivity.class);
            } else if (repEnt.sampleNumber == 0) {
                intent = new Intent(LoginActivity.this, CollectionActivity.class);
            } else {
                intent = new Intent(LoginActivity.this, MCCCollectionActivity.class);
                intent.putExtra("COMING_FROM", "UMCA");
                ArrayList<ReportEntity> reportEntities = new ArrayList<>();
                reportEntities.add(repEnt);
                intent.putExtra("SELECTED_DATA", reportEntities);
            }
        }
        goToNextActivity(intent);
    }

    private void switchOnOrOffMobileData() {

        NetworkTest netWorkTest = new NetworkTest(getApplicationContext());
        boolean checkForMobileDataEnableOrDisable = netWorkTest.toCheckForMobileData();

        if (checkForMobileDataEnableOrDisable) {
            if (!amcuConfig.getMobileData() && !isMobileEnable) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTrans = fragmentManager.beginTransaction();
                MoblieDataDialog dialog = new MoblieDataDialog();
                dialog.show(fragmentTrans, "D");

            } else if (!isMobileEnable) {
                enableMoblieData();
            }
        }

    }

    private boolean mobileDataStatus() {

        mobileRunnable = new Runnable() {
            @Override
            public void run() {
                isMobileEnable = Util.mobileDataStatus(LoginActivity.this);
            }
        };
        new Thread(mobileRunnable).start();
        return isMobileEnable;
    }

    private void enableMoblieData() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                isEnable = Util.mobileDataStatus(LoginActivity.this);
                if (!isEnable)
                    mobileDataHandler.post(mobileRunnable);
            }
        }).start();

        mobileRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (amcuConfig.getKeyCloudSupport()) {
                        Util.setMobileDataEnabled(true, LoginActivity.this);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    @Override
    public void switchOn() {
        enableMoblieData();
    }

    @Override
    public void switchOff() {

    }


    private void openTerminalActivity() {
        goToNextActivity(new Intent(LoginActivity.this, DeviceDataActivity.class));
    }


    private boolean checkForTerminalActivity() {
        if (_editEmail.getText().toString().equalsIgnoreCase(UserType.SUPPORT_USERID)
                && _editPassword.getText().toString().equalsIgnoreCase(UserType.SUPPORT_PASSWORD)) {
            return true;
        } else {
            return false;
        }
    }

    private void goToNextActivity(Intent intent) {

        if (new SmartCCUtil(LoginActivity.this).checkPortValidation()) {

        } else {
            intent = new Intent(LoginActivity.this, PortConfigurationActivity.class);
        }


        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
        finish();
    }


    private void setLoginAdapter() {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        ArrayList<String> allHistoryUser = dbh.getUserHistoryList(UserRole.OPERATOR);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this
                , R.layout.custom_spinner_item, allHistoryUser);
        _editEmail.setThreshold(0);
        _editEmail.setAdapter(arrayAdapter);
        if (allHistoryUser != null && allHistoryUser.size() > 0) {
            _editEmail.setText(allHistoryUser.get(0));
        } else if (session.getUserId() != null) {
            _editEmail.setText(session.getUserId());
        }

        if (_editEmail.getText().toString().trim().length() > 0) {
            _editPassword.requestFocus();
        } else {
            _editEmail.requestFocus();
            _editEmail.setSelection(_editEmail.getText().toString().length());
        }
    }


}
