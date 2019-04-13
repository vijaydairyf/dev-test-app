package com.devapp.devmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.devicemanager.ConfigurationManager;
import com.devapp.devmain.entity.AssocSocietyData;
import com.devapp.devmain.entity.ConfigurationConstants;
import com.devapp.devmain.entity.SocietyEntity;
import com.devapp.devmain.httptasks.ConfigurationPush;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.util.ValidationHelper;
import com.devApp.R;

import java.util.ArrayList;
import java.util.Locale;

public class EnrollSocietyActivity extends Activity implements OnClickListener {

    public String SocietyCode;
    public boolean isUpdate, isNew, isSuccess, isEmailValid;
    EditText etName, etCode, etRoute, etAddress, etContactNum1, etLocation,
            etBmcId, etEmail1, etContactPerson1, etAssocSoc, etContactNum2,
            etContactPerson2, etEmail2;
    Spinner spAssoc;
    TextView tvHeader, tvSocName;
    long regDate, weekDate, monthDate;
    Button btnSave, btnNext;
    ArrayList<String> listSocname, listSocId, listAssocName;
    ArrayList<SocietyEntity> arrSocEntity = new ArrayList<SocietyEntity>();
    SocietyEntity SocEntity = new SocietyEntity();
    SessionManager session;
    String message;
    AlertDialog alertDialog;
    ArrayAdapter<String> mSocietyAdapter;
    ListView mSocList;

    ArrayList<AssocSocietyData> addAllSoc = new ArrayList<AssocSocietyData>();

    String mandatory = "*";

    ArrayList<AssocSocietyData> allassocData = new ArrayList<AssocSocietyData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.enroll_society_activity);
        session = new SessionManager(EnrollSocietyActivity.this);
        etName = (EditText) findViewById(R.id.etSocietyName);
        etCode = (EditText) findViewById(R.id.etSocietycode);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etBmcId = (EditText) findViewById(R.id.etBmcid);
        etLocation = (EditText) findViewById(R.id.etLocation);
        etContactNum1 = (EditText) findViewById(R.id.etContactNumber1);
        etContactNum2 = (EditText) findViewById(R.id.etContactNumber2);
        etContactPerson1 = (EditText) findViewById(R.id.etContactPerson1);
        etContactPerson2 = (EditText) findViewById(R.id.etContactPerson2);
        etEmail2 = (EditText) findViewById(R.id.etEmail2);
        etEmail1 = (EditText) findViewById(R.id.etEmail1);
        etRoute = (EditText) findViewById(R.id.etRoute);
        spAssoc = (Spinner) findViewById(R.id.spAssocScociety);
        etAssocSoc = (EditText) findViewById(R.id.etAssocsoc);
        tvHeader = (TextView) findViewById(R.id.tvheader);
        tvSocName = (TextView) findViewById(R.id.tvSocietyName);

        btnSave = (Button) findViewById(R.id.btnSave);
        btnNext = (Button) findViewById(R.id.btnNext);

        btnSave.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        mandatory = getResources().getString(R.string.mandatory);

        try {
            isNew = getIntent().getExtras().getBoolean("isNew");
            SocEntity = (SocietyEntity) getIntent().getSerializableExtra(
                    "SocietyEntity");
            SocietyCode = SocEntity.socCode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (SocEntity != null) {
            tvHeader.setText(SocEntity.name);

        }

        etContactNum1.addTextChangedListener(new TextWatcher() {

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
                    Util.MobileToast(EnrollSocietyActivity.this);
                    etContactNum1.setText("");
                } else if (s.length() > 9) {
                    if (!Util.phoneNumberValidation(s.toString())) {
                        Util.MobileToast(EnrollSocietyActivity.this);
                        etContactNum1.setText("");
                    }
                }

            }
        });

        etContactNum2.addTextChangedListener(new TextWatcher() {

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
                    Util.MobileToast(EnrollSocietyActivity.this);
                    etContactNum2.setText("");
                } else if (s.length() > 9) {
                    if (!Util.phoneNumberValidation(s.toString())) {
                        Util.MobileToast(EnrollSocietyActivity.this);
                        etContactNum2.setText("");
                    }
                }

            }
        });

        etAssocSoc.setOnClickListener(this);
        getAllAssoc(String.valueOf(session.getSocietyColumnId()));

        setSpinner();

        if (isNew) {
            btnSave.setText("Save");
            CleanView();
            EditEnable();
        } else {
            btnSave.setText("Edit");

            setViews();
            EditDisable();
        }
        btnNext.setText("Cancel");
        getValidation();

    }

    public void getValidation() {
        Util.alphabetValidation(etName, Util.ALPHANUMERIC_SPACE, EnrollSocietyActivity.this, 0);
        Util.alphabetValidation(etContactPerson1, Util.ONLY_ALPHABET, EnrollSocietyActivity.this, 0);
        Util.alphabetValidation(etContactPerson2, Util.ONLY_ALPHABET, EnrollSocietyActivity.this, 0);

        Util.alphabetValidation(etCode, Util.ONLY_ALPHANUMERIC, EnrollSocietyActivity.this, 0);
        Util.alphabetValidation(etLocation, Util.ONLY_ALPHABET, EnrollSocietyActivity.this, 0);
        Util.alphabetValidation(etRoute, Util.ALPHANUMERIC_SPACE, EnrollSocietyActivity.this, 0);
        Util.alphabetValidation(etBmcId, Util.ONLY_ALPHANUMERIC, EnrollSocietyActivity.this, 0);
        Util.alphabetValidation(etAddress, Util.ALPHANUMERIC_SPACE, EnrollSocietyActivity.this, 0);
        Util.alphabetValidation(etContactNum1, Util.ONLY_NUMERIC, EnrollSocietyActivity.this, 0);
        Util.alphabetValidation(etContactNum2, Util.ONLY_NUMERIC, EnrollSocietyActivity.this, 0);

    }

    public void setSpinner() {
        getSocietyData();

        allassocData = new ArrayList<AssocSocietyData>();

    }

    public void setViews() {
        listSocId = new ArrayList<String>();

        getAllAssoc(String.valueOf(session.getSocietyColumnId()));
        etName.setText(SocEntity.name);
        etAddress.setText(SocEntity.address);
        etBmcId.setText(SocEntity.bmcId);
        etContactPerson1.setText(SocEntity.conPerson1);
        etContactPerson2.setText(SocEntity.conPerson2);
        etContactNum1.setText(SocEntity.contactNum1);
        etContactNum2.setText(SocEntity.contactNum2);
        etEmail1.setText(SocEntity.socEmail1);
        etEmail2.setText(SocEntity.socEmail2);

        etCode.setText(SocEntity.socCode);
        etRoute.setText(SocEntity.route);
        etLocation.setText(SocEntity.location);

        regDate = SocEntity.society_regDate;
        weekDate = SocEntity.society_weekly;
        monthDate = SocEntity.society_monthly;

        if (listAssocName != null) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    EnrollSocietyActivity.this,
                    android.R.layout.simple_spinner_item, listAssocName);
            dataAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spAssoc.setAdapter(dataAdapter);

            spAssoc.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1,
                                           int arg2, long arg3) {

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {

                }
            });
        }

    }

    public void CleanView() {

        etName.requestFocus();
        etName.setText("");
        etAddress.setText("");
        etBmcId.setText("");
        etCode.setText("");
        etContactNum1.setText("");
        etContactNum2.setText("");
        etEmail1.setText("");
        etEmail2.setText("");
        etContactPerson1.setText("");
        etContactPerson2.setText("");
        etRoute.setText("");
        etLocation.setText("");
        etAssocSoc.setText("");

    }

    public void EditDisable() {

        etName.setEnabled(false);
        etCode.setEnabled(false);
        etAddress.setEnabled(false);
        etBmcId.setEnabled(false);
        etLocation.setEnabled(false);
        etContactNum1.setEnabled(false);
        etContactNum2.setEnabled(false);
        etContactPerson1.setEnabled(false);
        etContactPerson2.setEnabled(false);
        etEmail1.setEnabled(false);
        etEmail2.setEnabled(false);
        etRoute.setEnabled(false);
        etAssocSoc.setEnabled(false);

    }

    public void EditEnable() {

        etName.setEnabled(true);
        etCode.setEnabled(true);
        etAddress.setEnabled(true);
        etBmcId.setEnabled(true);
        etLocation.setEnabled(true);
        etRoute.setEnabled(true);
        etAssocSoc.setEnabled(true);
        etContactNum1.setEnabled(true);
        etContactNum2.setEnabled(true);
        etContactPerson1.setEnabled(true);
        etContactPerson2.setEnabled(true);
        etEmail1.setEnabled(true);
        etEmail2.setEnabled(true);
        etName.requestFocus();

    }

    public void SaveSociety() {
        SocietyEntity societyEntity = new SocietyEntity();

        societyEntity.address = etAddress.getText().toString();
        societyEntity.bmcId = etBmcId.getText().toString();
        societyEntity.socCode = etCode.getText().toString();
        societyEntity.conPerson1 = etContactPerson1.getText().toString();
        societyEntity.conPerson2 = etContactPerson2.getText().toString();

        societyEntity.contactNum1 = etContactNum1.getText().toString();
        societyEntity.contactNum2 = etContactNum2.getText().toString();

        societyEntity.location = etLocation.getText().toString();
        societyEntity.name = etName.getText().toString();
        societyEntity.route = etRoute.getText().toString();
        societyEntity.socEmail1 = etEmail1.getText().toString();
        societyEntity.socEmail2 = etEmail2.getText().toString();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSave:
                if (!isUpdate) {
                    if (isNew) {
                        CheckValidation();

                    } else {
                        isUpdate = true;
                        btnSave.setText("Update");
                        EditEnable();
                    }
                } else {
                    CheckValidation();
                }
                break;

            case R.id.btnNext:
                finish();
                break;

            case R.id.etAssocsoc:
                alertAssocSociety();
                break;

            default:
                break;
        }

    }

    public void getSocietyData() {
        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();
        listSocname = new ArrayList<String>();

        try {
            arrSocEntity = db.getSocietyEntity(0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //DB close removed;

        if (arrSocEntity != null && arrSocEntity.size() > 0) {

            for (int i = 0; i < arrSocEntity.size(); i++) {
                listSocname.add(arrSocEntity.get(i).name);
            }

        }
    }

    public void saveSocietyData() {
        SocietyEntity socEntity = new SocietyEntity();

        socEntity.name = etName.getText().toString();
        socEntity.socCode = etCode.getText().toString().toUpperCase(Locale.ENGLISH);
        socEntity.address = etAddress.getText().toString();
        socEntity.conPerson1 = etContactPerson1.getText().toString();
        socEntity.conPerson2 = etContactPerson2.getText().toString();

        socEntity.contactNum1 = etContactNum1.getText().toString();
        socEntity.contactNum2 = etContactNum2.getText().toString();

        socEntity.location = etLocation.getText().toString();
        socEntity.bmcId = etBmcId.getText().toString();
        socEntity.route = etRoute.getText().toString();
        socEntity.socEmail1 = etEmail1.getText().toString();
        socEntity.socEmail2 = etEmail2.getText().toString();

        if (isUpdate) {
            socEntity.society_regDate = regDate;
            socEntity.society_weekly = weekDate;
            socEntity.society_monthly = monthDate;
        } else {

            socEntity.society_regDate = Util.getDateInLongFormat(Util
                    .getOperationDate(0, 1));

        }
        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();
        try {
            String message = "name: " + socEntity.name + "\n" + "Society Id: "
                    + socEntity.socCode + "\n";

            socEntity.colId = session.getSocietyColumnId();

            long colId = db.insertSociety(socEntity);
            addConfigurationForCenter();

            session.setSocietyName(socEntity.name);
            session.setSocietyColumnId(colId);
            session.setCollectionID(socEntity.socCode);
            session.setSocManagerEmail(socEntity.socEmail1);

            isSuccess = true;

            if (isUpdate) {
                Toast.makeText(EnrollSocietyActivity.this,
                        "Society updated successfully", Toast.LENGTH_SHORT)
                        .show();
                message = message + "\n" + "Society info updated!";
                Util.sendMessage(EnrollSocietyActivity.this,
                        socEntity.contactNum1, message, true);

            } else {
                Toast.makeText(EnrollSocietyActivity.this,
                        "Society created successfully", Toast.LENGTH_SHORT)
                        .show();

                message = message + "\n" + "Society info created!";
                Util.sendMessage(EnrollSocietyActivity.this,
                        socEntity.contactNum1, message, true);

            }

            if (socEntity.colId != session.getSocietyColumnId()) {
                Util.addUsers(EnrollSocietyActivity.this);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        //DB close removed;

        addSocData(addAllSoc);

        setSpinner();

        if (isSuccess) {
            finish();
        }

    }

    public void CheckValidation() {

        if (etName.getText().toString().replace(" ", "").length() > 0
                && etCode.getText().toString().replace(" ", "").length() > 0) {
            ValidationHelper checkVal = new ValidationHelper();
            if (etContactNum1.getText().toString().length() > 0) {
                if (!checkVal.validateMobileNumber(etContactNum1.getText().toString())) {
                    displayToast("Invalid mobile number");
                    return;
                }
                if (etContactNum1.getText().toString().equalsIgnoreCase(etContactNum2.getText().toString())) {
                    displayToast("Duplicate mobile number");
                    return;
                }
            }

            if (etContactNum2.getText().toString().length() > 0) {
                if (!checkVal.validateMobileNumber(etContactNum2.getText().toString())) {
                    displayToast("Invalid mobile number");
                    return;
                }
                if (etContactNum1.getText().toString().equalsIgnoreCase(etContactNum2.getText().toString())) {
                    displayToast("Duplicate mobile number");
                    return;
                }
            }

            if (etEmail1.getText().toString().length() > 0) {
                if (!checkVal.validateEmailId(etEmail1.getText().toString())) {
                    displayToast("Invalid email id");
                    return;
                }
                if (etEmail1.getText().toString().equalsIgnoreCase(etEmail2.getText().toString())) {
                    displayToast("duplicate email id");
                    return;
                }
            }

            if (etEmail2.getText().toString().length() > 0) {
                if (!checkVal.validateEmailId(etEmail2.getText().toString())) {
                    displayToast("Invalid email id");
                    return;
                }

                if (etEmail1.getText().toString().equalsIgnoreCase(etEmail2.getText().toString())) {
                    displayToast("duplicate email id");
                    return;
                }
            }
            new Handler().post(new Runnable() {

                @Override
                public void run() {
                    saveSocietyData();
                }
            });
        } else {
            Toast.makeText(EnrollSocietyActivity.this,
                    "Please fill all the details.", Toast.LENGTH_SHORT).show();
        }
    }

    public void getAllAssoc(String socID) {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        listAssocName = new ArrayList<String>();

        try {
            allassocData = dbh.getAssocData(socID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < allassocData.size(); i++) {
            listAssocName.add(allassocData.get(i).assoSocName);
        }
        //Removed database close;
    }

    public void alertAssocSociety() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                EnrollSocietyActivity.this);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alert_assocdata, null);

        mSocList = (ListView) view.findViewById(R.id.listAssoc);
        mSocietyAdapter = new ArrayAdapter<String>(EnrollSocietyActivity.this,
                android.R.layout.simple_list_item_1, listSocname);

        mSocList.setAdapter(mSocietyAdapter);

        mSocList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {

                if (arrSocEntity != null && arrSocEntity.size() > 0) {

                    AssocSocietyData assdata = new AssocSocietyData();
                    SocEntity = arrSocEntity.get(arg2);
                    SocietyCode = SocEntity.socCode;
                    assdata.assocId = SocietyCode;
                    assdata.socId = String.valueOf(session.getSocietyColumnId());
                    assdata.assoSocName = SocEntity.name;

                    if (addAllSoc.size() <= 0) {
                        addAllSoc.add(assdata);
                    } else {
                        for (int i = 0; i < addAllSoc.size(); i++) {
                            if (addAllSoc.get(i).assocId
                                    .equalsIgnoreCase(SocietyCode)) {
                                addAllSoc.remove(i);
                                break;
                            } else {
                                addAllSoc.add(assdata);
                                break;
                            }
                        }
                    }

                    String str = "";
                    for (int i = 0; i < addAllSoc.size(); i++) {
                        str = str + addAllSoc.get(i).assoSocName + ",";

                    }
                    etAssocSoc.setText(str);

                    alertDialog.dismiss();
                }

            }
        });

        alertDialog = builder.create();
        alertDialog.setView(view);
        alertDialog.show();

    }

    public void addSocData(ArrayList<AssocSocietyData> allasoc) {

        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        for (int i = 0; i < allasoc.size(); i++) {
            dbh.InsertAssociateData(allasoc.get(i));
        }
        //Removed database close;

    }

    public void displayToast(String message) {
        Util.displayErrorToast(message, EnrollSocietyActivity.this);
    }


    public void addConfigurationForCenter() {
        ConfigurationManager configurationManager = new ConfigurationManager(EnrollSocietyActivity.this);
        if (SocEntity == null) {
            return;
        } else {
            configurationManager.updateConfigurationData(ConfigurationConstants.centerCode,
                    etCode.getText().toString().trim(),
                    SocEntity.socCode);
            configurationManager.updateConfigurationData(ConfigurationConstants.centerContactMobile1,
                    etContactNum1.getText().toString().trim(),
                    SocEntity.contactNum1);
            configurationManager.updateConfigurationData(ConfigurationConstants.centerContactMobile2,
                    etContactNum2.getText().toString().trim(),
                    SocEntity.contactNum2);
            configurationManager.updateConfigurationData(ConfigurationConstants.centerContactEmail1,
                    etEmail1.getText().toString().trim(),
                    SocEntity.socEmail1);
            configurationManager.updateConfigurationData(ConfigurationConstants.centerContactEmail2,
                    etEmail2.getText().toString().trim(),
                    SocEntity.socEmail2);
            configurationManager.updateConfigurationData(ConfigurationConstants.centerContactPerson2,
                    etContactPerson2.getText().toString().trim(),
                    SocEntity.conPerson2);
            configurationManager.updateConfigurationData(ConfigurationConstants.centerContactPerson1,
                    etContactPerson1.getText().toString().trim(),
                    SocEntity.conPerson1);
            configurationManager.updateConfigurationData(ConfigurationConstants.centerName,
                    etName.getText().toString().trim(),
                    SocEntity.name);

            configurationManager.updateConfigurationData(ConfigurationConstants.centerAddress,
                    etAddress.getText().toString().trim(),
                    SocEntity.address);
            configurationManager.updateConfigurationData(ConfigurationConstants.centerLocation,
                    etLocation.getText().toString().trim(),
                    SocEntity.location);

            configurationManager.updateConfigurationData(ConfigurationConstants.centerBMCID,
                    etBmcId.getText().toString().trim(),
                    SocEntity.bmcId);
            configurationManager.updateConfigurationData(ConfigurationConstants.centerRoute,
                    etRoute.getText().toString().trim(),
                    SocEntity.route);
            startService(new Intent(EnrollSocietyActivity.this, ConfigurationPush.class));
        }

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
}
