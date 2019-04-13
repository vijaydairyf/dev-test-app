package com.devapp.devmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.devicemanager.ConfigurationManager;
import com.devapp.devmain.entity.ConfigurationConstants;
import com.devapp.devmain.entity.UserEntity;
import com.devapp.devmain.helper.UserRole;
import com.devapp.devmain.httptasks.ConfigurationPush;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.Locale;

public class AddUserActivity extends Activity implements OnClickListener {

    public boolean isSaved = true, isEmailValid;
    EditText etUserName, etPassword, etEmailId, etRole, etMobile, etSocietyId,
            etUserId, etRetypePass;
    ListView lvUsers;
    Button btnSave, btnNext, btnEdit;
    Spinner spRole;
    String Role;
    TableRow trRetypePass;
    SessionManager session;
    AlertDialog alertDialog;
    boolean isEnable, isEdit, isNew, isFromNew, isSuccess;
    TextView tvHeader;
    long regDate, weekDate, monthDate;
    UserEntity userEnt = new UserEntity();
    SmartCCUtil smartCCUtil;
    DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_society_user);
        smartCCUtil = new SmartCCUtil(this);

        etUserName = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etEmailId = (EditText) findViewById(R.id.etEmailid);
        etMobile = (EditText) findViewById(R.id.etMobile);
        etSocietyId = (EditText) findViewById(R.id.etSocietyId);
        etRetypePass = (EditText) findViewById(R.id.etRetypePassword);
        trRetypePass = (TableRow) findViewById(R.id.trRetypePass);

        spRole = (Spinner) findViewById(R.id.spRole);
        etUserId = (EditText) findViewById(R.id.etUserid);
        tvHeader = (TextView) findViewById(R.id.tvheader);

        tvHeader.setText("User details");
        userEnt = (UserEntity) getIntent().getSerializableExtra("UserEntity");
        etSocietyId.setEnabled(false);
        session = new SessionManager(AddUserActivity.this);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnNext = (Button) findViewById(R.id.btnCancel);
        btnEdit = (Button) findViewById(R.id.btnEdit);

        btnSave.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnEdit.setOnClickListener(this);

        btnEdit.setVisibility(View.GONE);
        // btnNext.setText("Cancel");

        trRetypePass.setVisibility(View.GONE);

        etMobile.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 10) {
                    Util.MobileToast(AddUserActivity.this);
                    etMobile.setText("");
                } else if (s.length() > 9) {
                    if (!Util.phoneNumberValidation(s.toString())) {
                        Util.MobileToast(AddUserActivity.this);
                        etMobile.setText("");
                    }
                }
            }
        });

        try {
            isFromNew = getIntent().getBooleanExtra("isNew", false);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (isFromNew) {
            isEnable = true;
            btnSave.setText("Save");
        } else {
            isEdit = false;
            isEnable = false;
            etUserId.setEnabled(false);
            btnSave.setText("Edit");

        }

        setUserEntity(userEnt);
        spRole.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {


                if (userEnt != null && !userEnt.role.equalsIgnoreCase(UserRole.MANAGER)) {
                    if (spRole.getItemAtPosition(arg2).toString().equalsIgnoreCase(
                            UserRole.MANAGER)) {
                        smartCCUtil.selectSpinnerPosition(spRole, userEnt.role);
                        Util.displayErrorToast("Can not change to Manager", AddUserActivity.this);
                    } else {
                        Role = spRole.getItemAtPosition(arg2).toString();
                    }
                } else {
                    Role = spRole.getItemAtPosition(arg2).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etPassword.getText().toString().length() > 0) {
                    trRetypePass.setVisibility(View.VISIBLE);
                }
            }
        });

        editValidityCheck();
        setEnable();
    }

    public void editValidityCheck() {
        Util.alphabetValidation(etUserName, Util.ONLY_ALPHABET, AddUserActivity.this, 0);
        Util.alphabetValidation(etMobile, Util.ONLY_NUMERIC, AddUserActivity.this, 0);
        Util.alphabetValidation(etSocietyId, Util.ONLY_ALPHANUMERIC, AddUserActivity.this, 0);
        Util.alphabetValidation(etUserId, Util.ONLY_ALPHANUMERIC, AddUserActivity.this, 0);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSave:

                if (isEdit) {
                    if (Validation()) {
                        Save();

                    }

                } else {
                    if (isFromNew) {
                        if (Validation()) {
                            Save();

                        }
                    } else {
                        btnSave.setText("Update");
                        isEdit = true;
                        isEnable = true;
                        setEnable();
                    }
                }

                break;

            case R.id.btnCancel: {
                finish();
            }

            break;

            default:
                break;
        }

    }

    public void Save() {
        UserEntity userEntity = new UserEntity();

        userEntity.emailId = etEmailId.getText().toString();
        userEntity.mobile = etMobile.getText().toString();
        userEntity.name = etUserName.getText().toString();
        userEntity.password = etPassword.getText().toString();
        userEntity.centerId = String.valueOf(session.getSocietyColumnId());
        userEntity.userId = etUserId.getText().toString()
                .toUpperCase(Locale.ENGLISH);
        userEntity.role = Role;


        if (btnSave.getText().toString().equalsIgnoreCase("Update")) {
            userEntity.regDate = regDate;
            userEntity.weekDate = weekDate;
            userEntity.monthDate = monthDate;

        } else {
            userEntity.regDate = System.currentTimeMillis();

        }

        try {
            String message = "name: " + userEntity.name + "\n" + "userId: "
                    + userEntity.userId + "\n" + "mobile: " + userEntity.mobile
                    + "\n";
            db.insertUser(userEntity);
            isSuccess = true;

            if (Role.equalsIgnoreCase(UserRole.MANAGER)
                    && etEmailId.getText().toString().replace(" ", "").length() > 0) {
                session.setManagerEmailID(etEmailId.getText().toString());
                session.setManagerName(etUserName.getText().toString());
                session.setManagerMobile(etMobile.getText().toString());
            }

            if (btnSave.getText().toString().equalsIgnoreCase("Update")) {
                Toast.makeText(AddUserActivity.this,
                        "User details updated Successfully! ", Toast.LENGTH_SHORT)
                        .show();
                message = message + "\n" + "User info updated!";
                Util.sendMessage(AddUserActivity.this, userEntity.mobile,
                        message, true);

            } else {
                Toast.makeText(AddUserActivity.this,
                        "User details added Successfully! ", Toast.LENGTH_SHORT).show();

                message = message + "\n" + "User created!";
                // Function for sending message
                Util.sendMessage(AddUserActivity.this, userEntity.mobile,
                        message, true);

            }

            addConfigurationForManagerAndOperator(userEntity);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //DB close removed;
        if (isSuccess) {
            finish();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        db = DatabaseHandler.getDatabaseInstance();

    }

    public boolean Validation() {

        if (isFromNew && db.isDuplicateUsers(etUserId.getText().toString().toUpperCase(Locale.ENGLISH))) {
            Util.displayErrorToast(" Duplicate user Id", AddUserActivity.this);
            return false;
        }


        if (isFromNew &&
                spRole.getSelectedItem().toString().equalsIgnoreCase(UserRole.MANAGER)) {
            Util.displayErrorToast(" Multiple manager not allowed for a center! ", AddUserActivity.this);
            return false;
        }

        if (etMobile.getText().toString().length() > 0
                && etPassword.getText().toString().length() > 0
                && etUserName.getText().toString().length() > 0
                && etUserId.getText().toString().length() > 0) {

            if (etEmailId.getText().toString().replace(" ", "").length() > 0) {
                isEmailValid = Util.EmailValidation(etEmailId,
                        AddUserActivity.this);

                if (!isEmailValid) {
                    return false;
                }
            }

            if (etMobile.getText().toString().length() < 10) {
                Toast.makeText(AddUserActivity.this,
                        " Invalid mobile number!!", Toast.LENGTH_SHORT).show();
            }

            if (etPassword.getText().toString()
                    .equalsIgnoreCase(etRetypePass.getText().toString())) {
                return true;
            } else {

                Toast.makeText(AddUserActivity.this, "password mismatch!!",
                        Toast.LENGTH_SHORT).show();
                etRetypePass.setText("");
                return false;
            }

        } else {
            Toast.makeText(AddUserActivity.this,
                    "Please enter all the details!", Toast.LENGTH_SHORT).show();
            return false;
        }

    }

    public void setUserEntity(UserEntity user) {

        if (user == null) {
            ClearValues();
            etSocietyId.setText(new SessionManager(AddUserActivity.this)
                    .getCollectionID());
            isNew = true;
            btnSave.setText("Save");
        } else {
            btnNext.setEnabled(true);
            etEmailId.setText(user.emailId);
            etMobile.setText(user.mobile);
            etPassword.setText(user.password);
            etSocietyId.setText(new SessionManager(AddUserActivity.this)
                    .getCollectionID());
            etUserName.setText(user.name);
            etUserId.setText(user.userId);
            etRetypePass.setText(user.password);

            regDate = user.regDate;
            weekDate = user.weekDate;
            monthDate = user.monthDate;
            smartCCUtil.selectSpinnerPosition(spRole, user.role);
        }

    }

    public void setEnable() {
        etEmailId.setEnabled(isEnable);
        etMobile.setEnabled(isEnable);
        etPassword.setEnabled(isEnable);
        etUserName.setEnabled(isEnable);

        if (spRole.getSelectedItem().toString().equalsIgnoreCase("Manager")) {
            spRole.setEnabled(false);
        } else {
            spRole.setEnabled(isEnable);
        }

    }

    public void ClearValues() {
        etUserName.requestFocus();
        etEmailId.setText("");
        etMobile.setText("");
        etPassword.setText("");
        etUserId.setText("");
        etUserName.setText("");
        spRole.setSelection(0);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_DOWN: {
                btnNext.requestFocus();
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!isSaved) {

        }
    }


    public void addConfigurationForManagerAndOperator(UserEntity userEntity) {
        ConfigurationManager configurationManager = new ConfigurationManager(AddUserActivity.this);
        //Add manager details

        if (userEnt == null) {
            userEnt = new UserEntity();
        }

        if (etUserId.getText().toString().equalsIgnoreCase(UserDetails.MANAGER)) {
            configurationManager.updateConfigurationData(ConfigurationConstants.managerEmailID,
                    etEmailId.getText().toString(), userEnt.emailId);
            configurationManager.updateConfigurationData(ConfigurationConstants.managerMobileNumber,
                    etMobile.getText().toString().trim(), userEnt.mobile);
            configurationManager.updateConfigurationData(ConfigurationConstants.managerPassword,
                    etPassword.getText().toString().trim(), userEnt.password);

        } else if (etUserId.getText().toString().equalsIgnoreCase("SMARTOPERATOR")) {
            configurationManager.updateConfigurationData(ConfigurationConstants.operatorMobileNumber,
                    etMobile.getText().toString(), userEnt.mobile);
            configurationManager.updateConfigurationData(ConfigurationConstants.operatorPassword,
                    etPassword.getText().toString().trim(), userEnt.password);
        } else {
//			ArrayList<UserEntity> allUserEnt= new ArrayList<>();
//			allUserEnt.add(userEntity);
//
//			String newValue = Util.getJsonFromObject(allUserEnt);
//
//			allUserEnt = new ArrayList<>();
//			allUserEnt.add(userEnt);
//			String oldValue = Util.getJsonFromObject(allUserEnt);
//
//			configurationManager.updateConfigurationData(ConfigurationConstants.userConfig,
//					newValue,oldValue);
        }

        startService(new Intent(AddUserActivity.this, ConfigurationPush.class));
    }

}
