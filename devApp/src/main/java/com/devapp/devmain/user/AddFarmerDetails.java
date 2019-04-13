package com.devapp.devmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.helper.CattleType;
import com.devapp.devmain.httptasks.PostFarmerRecords;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.util.CursorEndOnFocusChangeListener;
import com.devapp.devmain.util.FocusForwardKeyListener;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.adapters.AutoTextAdapter;
import com.devApp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class AddFarmerDetails extends Activity implements OnClickListener {

    public ArrayList<String> allSampleId, allSampleBarcode;
    public FarmerEntity farmEntity;
    public boolean duplicateEntry;
    Button btnSubmit, btnCancel, btnDetails;
    String[] listMilkType = new String[10];
    String[] listFarmerType;
    //    EditText getEtSocCode;
    EditText etBarcode, etFarmerName, etFarmerId, etFarmerCans, etCattletype,
            etEmail, etMobile, etNumCow, etNumBuff, etNumCattle;
    //    Spinner spCattleType;
    Spinner spFarmerType;
    AutoCompleteTextView tvMilkType;
    AutoTextAdapter autoTextAdapter;
    Spinner spinAgentList;
    FarmerEntity selectedEntity;
    String cattleType = "COW", NewFarmer;
    long regDate, weekDate, monthDate;
    TextView tvHeader;
    String message;
    TableRow trEmail;
    SessionManager session;
    int formarIncrement;
    boolean isNew, isEdit, isEnable, isSuccess, isEmailValid, isMulitipleCans;
    ArrayList<FarmerEntity> agentEntities;
    TableRow trAgent, trFarmerType;
    private ArrayList<String> allCattleType;
    private int adapterLayoutId;
    private AmcuConfig amcuConfig;
    private String agentID = AppConstants.NA;

    private String agentCattleType;
    private FarmerDao farmerDao;

    private boolean onCreate = true;
    private boolean isCheckSameCattleTypeFarmerAgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.addfarmerdata);

        session = new SessionManager(AddFarmerDetails.this);
        amcuConfig = AmcuConfig.getInstance();
        farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);

        etFarmerName = (EditText) findViewById(R.id.etFarmerName);
        etFarmerId = (EditText) findViewById(R.id.etFarmerid);
        etBarcode = (EditText) findViewById(R.id.etBarcode);
        etFarmerCans = (EditText) findViewById(R.id.etNumberofcans);
//        etSocCode = (EditText) findViewById(R.id.etSocCode);
        etNumCow = (EditText) findViewById(R.id.etNumCow);
        etNumBuff = (EditText) findViewById(R.id.etNumberofBuff);
        etNumCattle = (EditText) findViewById(R.id.etCattles);

//        spCattleType = (Spinner) findViewById(R.id.spMilk);
        tvMilkType = findViewById(R.id.tvSelectMilkType);
        spFarmerType = (Spinner) findViewById(R.id.spFarmerType);

//        etSocCode = (EditText) findViewById(R.id.etSocCode);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etMobile = (EditText) findViewById(R.id.etMobile);
        tvHeader = (TextView) findViewById(R.id.tvheader);
        trEmail = (TableRow) findViewById(R.id.trEmail);
        spinAgentList = (Spinner) findViewById(R.id.spinAgentList);

        trAgent = (TableRow) findViewById(R.id.trSelectAgent);
        trFarmerType = (TableRow) findViewById(R.id.trFarmerType);

        trEmail.setVisibility(View.GONE);

//        etSocCode.setText(session.getCollectionID());
//        etSocCode.setFocusable(false);
        btnSubmit = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnClear);
        btnDetails = (Button) findViewById(R.id.btnDetails);
        btnDetails.setVisibility(View.GONE);

        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDetails.setOnClickListener(this);

        tvHeader.setText("Farmer details");

        btnCancel.setText("Cancel");

        etFarmerName.setOnKeyListener(new FocusForwardKeyListener(etFarmerId));
        etFarmerId.setOnKeyListener(new FocusForwardKeyListener(etBarcode));
        etBarcode.setOnKeyListener(new FocusForwardKeyListener(etFarmerCans));
        etFarmerCans.setOnKeyListener(new FocusForwardKeyListener(tvMilkType));
        etMobile.setOnKeyListener(new FocusForwardKeyListener(etNumCow));
        etNumCow.setOnKeyListener(new FocusForwardKeyListener(etNumBuff));
        etNumBuff.setOnKeyListener(new FocusForwardKeyListener(btnSubmit));


        etFarmerName.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etFarmerId.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etBarcode.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etFarmerCans.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etMobile.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etNumCow.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etNumBuff.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());

        allCattleType = new ArrayList<>();
        allCattleType.add("1-" + CattleType.COW);
        allCattleType.add("2-" + CattleType.BUFFALO);
        allCattleType.add("3-" + CattleType.MIXED);
        allCattleType.add("4-" + CattleType.BOTH);
        adapterLayoutId = getResources().getIdentifier("auto_text_item", "res", getPackageName());
        autoTextAdapter = new AutoTextAdapter(AddFarmerDetails.this, adapterLayoutId, allCattleType);
        tvMilkType.setThreshold(0);
        tvMilkType.setAdapter(autoTextAdapter);

        tvMilkType.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (tvMilkType.isPopupShowing()) {
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_ADD) {
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            tvMilkType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                        } else {
                            tvMilkType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_SUBTRACT) {
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            tvMilkType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP, 0));
                        } else {
                            tvMilkType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP, 0));
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            tvMilkType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0));
                        } else {
                            tvMilkType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0));
                        }
                        return true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        if (tvMilkType.equals("")) {
                            tvMilkType.setText("1-COW");
                        }
                        etMobile.requestFocus();
                    }
                    return true;
                }

                return false;
            }
        });


        /*spCattleType.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                switch (i){
                    case KeyEvent.KEYCODE_NUMPAD_ADD:{
                        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN){

                            spCattleType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                        }else if (keyEvent.getAction() == KeyEvent.ACTION_UP){
                            spCattleType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                        }
                        *//*new Thread(new Runnable() {
                            @Override
                            public void run() {
                                new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
                            }
                        }).start();*//*
                        return true;

                    }
                    case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:{
                        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                            spCattleType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP, 0));
                        }else if (keyEvent.getAction() == KeyEvent.ACTION_UP){
                            spCattleType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP, 0));
                        }
                       *//* new Thread(new Runnable() {
                            @Override
                            public void run() {
                                new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
                            }
                        }).start();*//*
                       return true;
                    }
                }
                return false;
            }
        });*/


        try {
            NewFarmer = getIntent().getExtras().getString("NewFarmer");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (NewFarmer != null && NewFarmer.equalsIgnoreCase("NewFarmer")) {
            btnSubmit.setText("Save");
            isEnable = true;
            isNew = true;

        } else {
            etFarmerId.setEnabled(false);
            btnSubmit.setText("Edit");
            isEnable = false;
            isEdit = false;
            isNew = false;

            if (Util.isOperator(AddFarmerDetails.this)) {
                inVisibleSubmitButton();
            } else {
                btnSubmit.setVisibility(View.VISIBLE);

            }
        }

        selectedEntity = (FarmerEntity) getIntent().getSerializableExtra(
                "SelectedList");

        listMilkType = getApplicationContext().getResources().getStringArray(
                R.array.Milk_type_farmer);

        listFarmerType = getApplicationContext().getResources().getStringArray(R.array.farmer_type);

        etNumCow.addTextChangedListener(new TextWatcher() {

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
                getTotalNumofCattle();
            }
        });

        etNumBuff.addTextChangedListener(new TextWatcher() {

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
                getTotalNumofCattle();

            }
        });

        etBarcode.addTextChangedListener(new TextWatcher() {

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

                if (etBarcode.length() > 5) {
                    if (!Util.validPrefixForBarcode(etBarcode.getText().toString(), AddFarmerDetails.this)) {
                        etBarcode.setText("");
                        etBarcode.requestFocus();
                    }
                }

            }
        });

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
                    etMobile.setText("");
                    Util.MobileToast(AddFarmerDetails.this);
                } else if (s.length() > 9) {
                    if (!Util.phoneNumberValidation(s.toString())) {
                        etMobile.setText("");
                        Util.MobileToast(AddFarmerDetails.this);
                    }

                }

            }
        });


        //  Util.alphabetValidation(etFarmerName, Util.ONLY_ALPHABET, AddFarmerDetails.this, 0);
        Util.alphabetValidation(etBarcode, Util.ONLY_ALPHANUMERIC, AddFarmerDetails.this, 0);
//        Util.alphabetValidation(etSocCode, Util.ONLY_ALPHANUMERIC, AddFarmerDetails.this, 0);
        Util.alphabetValidation(etFarmerCans, Util.ONLY_NUMERIC, AddFarmerDetails.this, 0);
        Util.alphabetValidation(etMobile, Util.ONLY_NUMERIC, AddFarmerDetails.this, 0);
        Util.alphabetValidation(etNumCow, Util.ONLY_NUMERIC, AddFarmerDetails.this, 0);
        Util.alphabetValidation(etNumBuff, Util.ONLY_NUMERIC, AddFarmerDetails.this, 0);
        Util.alphabetValidation(etNumCattle, Util.ONLY_NUMERIC, AddFarmerDetails.this, 0);
        Util.alphabetValidation(etFarmerId, Util.ONLY_NUMERIC, AddFarmerDetails.this,
                AmcuConfig.getInstance().getFarmerIdDigit());


        updatAgentList();

        setValues();
        setEnable();

        if (amcuConfig.getKeyAllowFarmerIncrement()) {
            if (NewFarmer != null && NewFarmer.equalsIgnoreCase("NewFarmer")) {
                etFarmerId.setText(getFarmerId());
                etFarmerId.setEnabled(false);
            }
        }
//        etSocCode.setEnabled(false);
        etFarmerId.requestFocus();

    }

    private String getFarmerId() {
        formarIncrement = farmerDao.getlastFamerId();

        updatFarmerId();

        ValidationHelper validationHelper = new ValidationHelper();

        DecimalFormat decimalFormat = validationHelper.getFormatForFarmerId(
                AmcuConfig.getInstance().getFarmerIdDigit());

        String farmerId = validationHelper.getAppendedFarmerId(formarIncrement, decimalFormat);

        return farmerId;


    }

    private void updatAgentList() {
        agentEntities = farmerDao.findAllByMemberAndCattleType(AppConstants.FARMER_TYPE_AGENT, cattleType);

        FarmerEntity naEntity = new FarmerEntity();
        naEntity.farmer_id = AppConstants.NA;
        naEntity.farmer_name = AppConstants.NA;

        if (agentEntities == null || agentEntities.size() == 0) {
            agentEntities = new ArrayList<>();
            agentEntities.add(naEntity);
        } else {
            agentEntities.add(0, naEntity);
        }

        if (agentEntities != null && agentEntities.size() > 0) {
            AgentListAdapter agentListAdapter =
                    new AgentListAdapter(AddFarmerDetails.this, R.layout.item_agent_list, agentEntities);
            spinAgentList.setAdapter(agentListAdapter);

        }

        try {
            if (selectedEntity.agentId != null) {
                for (int j = 0; j < agentEntities.size(); j++) {
                    if (selectedEntity.agentId
                            .equalsIgnoreCase(agentEntities.get(j).farmer_id)) {
                        spinAgentList.setSelection(j);
                    }
                }
            }
        } catch (Exception e) {

        }


    }

    private void updatFarmerId() {

        formarIncrement++;
        ValidationHelper validationHelper = new ValidationHelper();

        DecimalFormat decimalFormat = validationHelper.getFormatForFarmerId(
                AmcuConfig.getInstance().getFarmerIdDigit());

        String farmerId = validationHelper.getAppendedFarmerId(formarIncrement, decimalFormat);

        if (checkIfSample(farmerId) || ifReserveFarmerId(farmerId)) {
            updatFarmerId();
        }
    }

    public boolean ifReserveFarmerId(String id) {
        return id.equals(DatabaseHandler.DEFAULT_FARMER_ID);

    }

    public boolean checkIfSample(String id) {
        boolean isDuplicate = false;
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        try {
            isDuplicate = dbh.checkForSampleId(id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            {
                //Removed database close;
            }
            return isDuplicate;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        getSampleIdAndBarcodes();
        showAgentVisiblity();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSave:


                if (isEdit) {
                    if (cattleType.equalsIgnoreCase(agentCattleType) ||
                            cattleType.equalsIgnoreCase("BOTH") || cattleType.equalsIgnoreCase("MIXED") ||
                            agentID.equalsIgnoreCase(AppConstants.NA) ||
                            agentCattleType.equalsIgnoreCase("BOTH") ||
                            agentCattleType.equalsIgnoreCase("MIXED")) {
                        SaveDataOnDatabase();
                        startFarmerSentService();
                    } else {
                        Util.displayErrorToast("Agent and farmer not same cattle type", AddFarmerDetails.this);

                    }

                } else {
                    if (isNew) {

                        SaveDataOnDatabase();
                        startFarmerSentService();

                    } else {
                        isEdit = true;
                        isEnable = true;
                        btnSubmit.setText("Update");
                        etFarmerName.requestFocus();
                        etFarmerName.setOnKeyListener(new FocusForwardKeyListener(etBarcode));
                        setEnable();
                    }
                }
                break;

            case R.id.btnClear:
                finish();
                break;

            default:
                break;
        }

    }

    public void SaveDataOnDatabase() {

        if (checkForValidation()) {
            try {
                farmerDao.saveOrUpdate(farmEntity);

                Util.getJsonFromObject(farmEntity);
                isSuccess = true;
                if (isEdit) {
                    Toast.makeText(AddFarmerDetails.this,
                            spFarmerType.getSelectedItem().toString() + " details successfully updated!", Toast.LENGTH_SHORT)
                            .show();

                    message = spFarmerType.getSelectedItem().toString() + " name: " + farmEntity.farmer_name + "\n"
                            + spFarmerType.getSelectedItem().toString() + " id: " + farmEntity.farmer_id + "\n"
                            + "Mobile: " + farmEntity.farm_mob + "\n"
                            + spFarmerType.getSelectedItem().toString() + " details updated!";

                    Util.sendMessage(AddFarmerDetails.this,
                            farmEntity.farm_mob, message, false);

                } else {
                    Toast.makeText(AddFarmerDetails.this,
                            spFarmerType.getSelectedItem().toString() + " details successfully added!", Toast.LENGTH_SHORT)
                            .show();

                    message = spFarmerType.getSelectedItem().toString() + " name: " + farmEntity.farmer_name + "\n"
                            + spFarmerType.getSelectedItem().toString() + " id: " + farmEntity.farmer_id + "\n"
                            + "Mobile: " + farmEntity.farm_mob + "\n"
                            + spFarmerType.getSelectedItem().toString() + " details created!";

                    Util.sendMessage(AddFarmerDetails.this,
                            farmEntity.farm_mob, message, false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //Removed database close;

        } else {
            if (duplicateEntry) {
                Toast.makeText(AddFarmerDetails.this,
                        "Duplicate " + spFarmerType.getSelectedItem().toString()
                                + " Id or barcode!", Toast.LENGTH_LONG)
                        .show();
            }
        }
        if (isSuccess) {
            finish();
        }
    }

    public void Cancel() {
        AddFarmerDetails.this.finish();
    }

    public void setValues() {

        if (selectedEntity == null || selectedEntity.farmer_id == null) {

            etFarmerName.setText("New Farmer");
            btnSubmit.setText("Save");
            isNew = true;
            isEnable = true;
            setEnable();
            ClearValues();

        } else {
            if (selectedEntity.farmer_barcode != null &&
                    !selectedEntity.farmer_barcode.equalsIgnoreCase("null")) {
                etBarcode.setText(selectedEntity.farmer_barcode);
            }

            etFarmerCans.setText(selectedEntity.farmer_cans);
            etFarmerId.setText(selectedEntity.farmer_id);
            etFarmerName.setText(selectedEntity.farmer_name);
//            etSocCode.setText(session.getCollectionID());
            etEmail.setText(selectedEntity.farm_email);
            if (selectedEntity.farm_mob != null && !selectedEntity.farm_mob.equalsIgnoreCase("null")) {
                etMobile.setText(selectedEntity.farm_mob);
            }

            regDate = selectedEntity.farmer_regDate;
            isMulitipleCans = selectedEntity.isMultipleCans;

            int pos = 0;
            int cow = 0, buff = 0, total = 0;

            try {
                cow = Integer.parseInt(selectedEntity.numCow);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {

            }
            try {
                buff = Integer.parseInt(selectedEntity.numBuff);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {

            }

            total = cow + buff;

            etNumBuff.setText(String.valueOf(buff));
            etNumCow.setText(String.valueOf(cow));
            etNumCattle.setText(String.valueOf(total));


            if (selectedEntity.farmer_cattle == null) {
                selectedEntity.farmer_cattle = "Cow";
            }

            /*for (int i = 0; i < listMilkType.length; i++) {
                if (selectedEntity.farmer_cattle
                        .equalsIgnoreCase(listMilkType[i])) {
                    pos = i;
                    cattleType = spCattleType.getItemAtPosition(i).toString();
                    updatAgentList();
                }
            }*/
            for (String ct : allCattleType) {
                if (selectedEntity.farmer_cattle.equals(ct.substring(2, ct.length()))) {
                    tvMilkType.setText(ct);
                    cattleType = ct.substring(2, ct.length());
                    updatAgentList();
                    break;
                }
            }
//            spCattleType.setSelection(pos);

            if (selectedEntity.farmerType != null) {
                for (int i = 0; i < listFarmerType.length; i++) {
                    if (listFarmerType[i].equalsIgnoreCase(selectedEntity.farmerType)) {
                        spFarmerType.setSelection(i);
                    }
                }
            }


        }
//TODO: Fix this. This part is missing when using AutoTextView instead of Spinner for Cattle Type
       /* spCattleType.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                cattleType = spCattleType.getItemAtPosition(arg2).toString();
                updatAgentList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });*/

        spFarmerType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (spFarmerType.getItemAtPosition(i).toString().
                        equalsIgnoreCase(AppConstants.FARMER_TYPE_FARMER)) {
                    trAgent.setVisibility(View.VISIBLE);
                    spFarmerType.clearFocus();
                    if (!onCreate) {
                        spinAgentList.requestFocus();
                    }
                    onCreate = false;


                } else {
                    trAgent.setVisibility(View.GONE);
                    spFarmerType.clearFocus();
                    btnSubmit.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        spinAgentList.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                isCheckSameCattleTypeFarmerAgent = true;
                agentID = agentEntities.get(arg2).farmer_id;
                agentCattleType = agentEntities.get(arg2).farmer_cattle;

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

    }

    public void setEnable() {
        etBarcode.setEnabled(isEnable);
        etEmail.setEnabled(isEnable);
        etFarmerCans.setEnabled(isEnable);
        etFarmerName.setEnabled(isEnable);
        etMobile.setEnabled(isEnable);
        etNumBuff.setEnabled(isEnable);
        etNumCow.setEnabled(isEnable);
        tvMilkType.setEnabled(isEnable);
//        spCattleType.setEnabled(isEnable);
        spinAgentList.setEnabled(isEnable);
        spFarmerType.setEnabled(isEnable);
    }

    public void ClearValues() {
        //  etFarmerName.requestFocus();
        etFarmerId.requestFocus();
        etBarcode.setText("");
        etEmail.setText("");
        etFarmerCans.setText("1");
        etFarmerId.setText("");
        // etFarmerName.setText("");
        etMobile.setText("");
        etNumBuff.setText("");
        etNumCow.setText("");
        etNumCattle.setText("");
//        etSocCode.setText(session.getCollectionID());
        tvMilkType.setText("");
//        spCattleType.setSelection(0);
        spinAgentList.setSelection(0);
        spFarmerType.setSelection(0);
    }

    public void getTotalNumofCattle() {
        if (!etNumCow.getText().toString().equalsIgnoreCase("")
                && !etNumBuff.getText().toString().equalsIgnoreCase("")) {

            int i = Integer.parseInt(etNumCow.getText().toString()
                    .replace(" ", ""));
            int j = Integer.parseInt(etNumBuff.getText().toString()
                    .replace(" ", ""));
            etNumCattle.setText(String.valueOf(i + j));

        } else if (etNumCow.getText().toString().equalsIgnoreCase("")) {
            etNumCattle.setText(etNumBuff.getText().toString());

        } else if (etNumBuff.getText().toString().equalsIgnoreCase("")) {
            etNumCattle.setText(etNumCow.getText().toString());

        } else {
            etNumCattle.setText("0");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public boolean checkForValidation() {
        boolean validation = true;
        ValidationHelper validationHelper = new ValidationHelper();
        duplicateEntry = false;
        farmEntity = new FarmerEntity();
        if (etFarmerId.getText().toString() == null
                || etFarmerId.getText().toString().length() < 1) {
            etFarmerId.requestFocus();
            Toast.makeText(AddFarmerDetails.this,
                    "Please enter valid Member Id!", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if (!ValidationHelper.isReserveCode(etFarmerId.getText().toString().trim(), AddFarmerDetails.this)) {
            etFarmerId.requestFocus();
            return false;
        } else if (!validationHelper.isValidName(etFarmerName.getText().toString())) {
            etFarmerName.requestFocus();
            return false;
        } else if ((etMobile.getText().toString() != null && etMobile.getText()
                .toString().length() > 0)
                && etMobile.getText().toString().length() < 10) {
            etMobile.requestFocus();
            Toast.makeText(AddFarmerDetails.this, "Invalid phone number",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (!etBarcode.getText().toString().replace(" ", "").equalsIgnoreCase("") && etBarcode.getText().toString().length() < 10) {
            etBarcode.requestFocus();
            Toast.makeText(AddFarmerDetails.this, "Invalid barcode",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            String milkType = tvMilkType.getText().toString();
            if (!milkType.equals("")) {
                if (allCattleType.contains(milkType)) {
                    for (String cT : allCattleType) {
                        if (milkType.equals(cT)) {
                            cattleType = cT.substring(2, cT.length());
                            break;
                        }
                    }
                } else {
                    tvMilkType.requestFocus();
                    Toast.makeText(AddFarmerDetails.this, "Invalid",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }

            } else {
                cattleType = CattleType.COW;
            }
            if (etBarcode.getText().toString()
                    .replaceAll(" ", "").length() < 1 || etBarcode.getText().toString()
                    .replaceAll(" ", "").equalsIgnoreCase("")) {
                farmEntity.farmer_barcode = null;
            } else {
                farmEntity.farmer_barcode = etBarcode.getText().toString()
                        .trim();
            }


            farmEntity.farmer_id = Util.paddingFarmerId(etFarmerId.getText()
                    .toString().trim(), AmcuConfig.getInstance().getFarmerIdDigit());
            farmEntity.farmer_name = etFarmerName.getText().toString().trim();
            farmEntity.farmer_cans = etFarmerCans.getText().toString().trim();
            farmEntity.farm_mob = etMobile.getText().toString().trim();
            farmEntity.soc_code = String.valueOf(session.getSocietyColumnId());
            farmEntity.farm_email = etEmail.getText().toString().trim();
            farmEntity.farmer_cattle = cattleType;
            farmEntity.numCow = etNumCow.getText().toString().trim();
            farmEntity.numBuff = etNumBuff.getText().toString().trim();
            farmEntity.numCattle = etNumCattle.getText().toString()
                    .trim();

            farmEntity.isMultipleCans = isMulitipleCans;
            farmEntity.farmerType = spFarmerType.getSelectedItem().toString().trim();

            if (!agentID.equalsIgnoreCase(AppConstants.NA)) {
                farmEntity.agentId = agentID;
            }

            if (farmEntity.farmer_id.length() > 0) {
                int id = 0;
                try {
                    id = Integer.parseInt(farmEntity.farmer_id);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (id < 1) {
                    alertForDuplicateEntries("Member Id should be greator than 0.", false);
                    etFarmerId.requestFocus();
                    return false;
                } else {
                    String getErrMsg = Util.checkForRegisteredCode(farmEntity.farmer_id
                            , AmcuConfig.getInstance().getFarmerIdDigit(), false);
                    if (getErrMsg != null) {
                        alertForDuplicateEntries(getErrMsg, false);
                        return false;
                    }

                }
            }
            if (isEdit) {
                farmEntity.farmer_regDate = regDate;
            } else {

                farmEntity.farmer_regDate = System.currentTimeMillis();
            }

            if (selectedEntity == null) {


                String duplicateMessage = null;

                duplicateMessage = Util.getDuplicateIdOrBarCode(farmEntity.farmer_id, farmEntity.farmer_barcode,
                        AddFarmerDetails.this);

                if (duplicateMessage != null) {
                    alertForDuplicateEntries(duplicateMessage, true);
                    return false;
                } else {
                    return true;
                }

            } else if (selectedEntity != null
                    && farmEntity.farmer_barcode != null
                    && farmEntity.farmer_barcode.length() > 0
                    ) {


                String duplicateMessage = null;

                if (selectedEntity.farmer_barcode != null && !(selectedEntity.farmer_barcode.equalsIgnoreCase(farmEntity.farmer_barcode))) {
                    duplicateMessage = Util.getDuplicateIdOrBarCode("0", farmEntity.farmer_barcode, AddFarmerDetails.this);
                } else if (selectedEntity.farmer_barcode == null) {
                    duplicateMessage = Util.getDuplicateIdOrBarCode("0", farmEntity.farmer_barcode, AddFarmerDetails.this);
                }

                if (duplicateMessage != null) {
                    alertForDuplicateEntries(duplicateMessage, true);
                    return false;
                } else {
                    return true;
                }
            }

        }

        return validation;
    }

    public void getSampleIdAndBarcodes() {
        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseInstance(
        );
        try {
            allSampleId = databaseHandler.getAllSampleIdorBarcodes(
                    String.valueOf(session.getSocietyColumnId()), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close statement
        databaseHandler = DatabaseHandler.getDatabaseInstance();

        try {
            allSampleBarcode = databaseHandler.getAllSampleIdorBarcodes(
                    String.valueOf(session.getSocietyColumnId()), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close statement
    }


    public void alertForDuplicateEntries(String msg, boolean isDuplicate) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AddFarmerDetails.this);
        if (isDuplicate) {
            alertDialogBuilder.setTitle("Duplicate entry!");
        } else {
            alertDialogBuilder.setTitle("Invalid format!");
        }
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false);
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void startFarmerSentService() {
        startService(new Intent(AddFarmerDetails.this, PostFarmerRecords.class));
    }

    private void showAgentVisiblity() {

        if (!amcuConfig.getKeyAllowAgentFarmerCollection()) {
            trAgent.setVisibility(View.GONE);
            trFarmerType.setVisibility(View.GONE);
            spFarmerType.setVisibility(View.GONE);
            spinAgentList.setVisibility(View.GONE);
        }

    }
/*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_NUMPAD_ADD: {
                dispatchKeyEvent(new KeyEvent(0, 0,
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                return true;
            }
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT: {
                dispatchKeyEvent(new KeyEvent(0, 0,
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP, 0));
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }*/

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_NUMPAD_ADD: {
                dispatchKeyEvent(new KeyEvent(0, 0,
                        KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                return true;
            }
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT: {
                dispatchKeyEvent(new KeyEvent(0, 0,
                        KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP, 0));
                return true;
            }
            case KeyEvent.KEYCODE_PAGE_DOWN: {
                btnSubmit.requestFocus();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void inVisibleSubmitButton() {
        if (!AmcuConfig.getInstance().getKeyAllowFarmerEdit()) {
            btnSubmit.setVisibility(View.GONE);
        } else {
            btnSubmit.setVisibility(View.VISIBLE);

        }

    }

}
