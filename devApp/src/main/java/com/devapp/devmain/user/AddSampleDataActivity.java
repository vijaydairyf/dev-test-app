package com.devapp.devmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.SampleDataEntity;
import com.devapp.devmain.entity.UserEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devApp.BuildConfig;
import com.devApp.R;

import java.util.ArrayList;

public class AddSampleDataActivity extends Activity implements OnClickListener {

    public String SocietyCode;
    public boolean isSaved = true, isEmailValid;
    EditText etSampleId, etsocId, etName, etBarcode;
    ListView lvUsers;
    Button btnSave, btnNext;
    SessionManager session;
    AlertDialog alertDialog;
    boolean isEnable, isEdit, isNew, isFromNew, isSuccess;
    TextView tvHeader;
    long regDate, weekDate, monthDate;
    CheckBox checkMA, checkWeigh;
    String isMA = "true", isWeigh = "true";
    ArrayList<UserEntity> allUsers = new ArrayList<UserEntity>();
    ArrayList<String> allFarmId = new ArrayList<>();
    ArrayList<String> allFarmBarcodes = new ArrayList<>();
    SampleDataEntity sampleEnt = new SampleDataEntity();
    SampleDataEntity sample;
    private AmcuConfig amcuConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.add_smaple_data);

        etSampleId = (EditText) findViewById(R.id.etTestId);
        etName = (EditText) findViewById(R.id.etTestName);
        etsocId = (EditText) findViewById(R.id.etSocietyId);
        etBarcode = (EditText) findViewById(R.id.etTestBarCode);

        tvHeader = (TextView) findViewById(R.id.tvheader);

        tvHeader.setText("Sample details");
        try {
            sampleEnt = (SampleDataEntity) getIntent().getSerializableExtra(
                    "SampleDataEntity");
        } catch (Exception e) {
            e.printStackTrace();
        }
        etsocId.setEnabled(false);
        session = new SessionManager(AddSampleDataActivity.this);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnNext = (Button) findViewById(R.id.btnCancel);

        checkMA = (CheckBox) findViewById(R.id.checkIsMA);
        checkWeigh = (CheckBox) findViewById(R.id.CheckIsWeigh);

        btnSave.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        amcuConfig = AmcuConfig.getInstance();

        checkMA.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    isMA = "true";
                } else {
                    isMA = "false";
                }

            }
        });

        checkWeigh.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    isWeigh = "true";
                } else {
                    isWeigh = "false";
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
        }
//        else if (sampleEnt != null && sampleEnt.sampleId != null && (sampleEnt.sampleId.equalsIgnoreCase("9997") || sampleEnt.sampleId.equalsIgnoreCase(("9998")))) {
//
//            isEdit = false;
//            isEnable = false;
//            etsocId.setEnabled(false);
//            btnSave.setText("Edit");
//            setEnable();
//            btnSave.setVisibility(View.GONE);
//
//        }
        else {
            isEdit = false;
            isEnable = false;
            etsocId.setEnabled(false);
            btnSave.setText("Edit");

        }

        setUserEntity(sampleEnt);
        if (BuildConfig.FLAVOR.equalsIgnoreCase("shivprasad")) {
            Util.alphabetValidation(etSampleId, Util.ONLY_NUMERIC, AddSampleDataActivity.this, 5);
        } else {
            Util.alphabetValidation(etSampleId, Util.ONLY_NUMERIC, AddSampleDataActivity.this, amcuConfig.getFarmerIdDigit());
        }

        setEnable();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        getAllFarmIdAndBarcodes();
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

        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();

        try {
            db.insertSampleData(sample);
            isSuccess = true;
            if (btnSave.getText().toString().equalsIgnoreCase("Update")) {
                Toast.makeText(AddSampleDataActivity.this,
                        "Sample details updated Successfully! ", Toast.LENGTH_SHORT)
                        .show();

            } else {
                Toast.makeText(AddSampleDataActivity.this,
                        "Sample details added Successfully! ", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //DB close removed;
        if (isSuccess) {
            finish();
        }

    }

    public boolean Validation() {

        if (etSampleId.getText().toString().length() > 0
                && etsocId.getText().toString().length() > 0
                && etName.getText().toString().length() > 0) {

            sample = new SampleDataEntity();

            sample.sampleSocId = String.valueOf(new SessionManager(
                    AddSampleDataActivity.this).getSocietyColumnId());
            sample.sampleId = Util.paddingFarmerId(etSampleId.getText()
                    .toString().trim(), AmcuConfig.getInstance().getFarmerIdDigit()).trim();
            sample.sampleMode = etName.getText().toString().trim();
            sample.sampleBarcode = etBarcode.getText().toString().replaceAll(" ", "").trim();
            sample.sampleIsFat = isMA;

            sample.sampleIsWeigh = isWeigh;

            if (sampleEnt != null && sampleEnt.sampleId != null && sampleEnt.sampleId.length() > 0) {

                try {
                    String duplicateMessage = null;

                    if (sampleEnt.sampleBarcode != null && !(sampleEnt.sampleBarcode.equalsIgnoreCase(sample.sampleBarcode))) {
                        duplicateMessage = Util.getDuplicateIdOrBarCode("0", sample.sampleBarcode, AddSampleDataActivity.this);
                    } else if (sampleEnt.sampleBarcode == null) {
                        duplicateMessage = Util.getDuplicateIdOrBarCode("0", sample.sampleBarcode, AddSampleDataActivity.this);
                    }

                    if (duplicateMessage != null) {
                        alertForDuplicateEntries(duplicateMessage, true);
                        return false;
                    } else {
                        return true;
                    }
                } catch (NullPointerException e) {
                    alertForDuplicateEntries("Invalid sample data.", false);
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    alertForDuplicateEntries("Invalid sample data.", false);
                    return false;
                }

            } else if (sample != null)

            {
                int farmId = 0;
                try {
                    farmId = Integer.parseInt(sample.sampleId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    alertForDuplicateEntries("Invalid sample Id.", false);
                    return false;

                }

                if (farmId < 1) {
                    alertForDuplicateEntries("Sample Id should be greator than 0.", false);
                    return false;
                } else {
                    String getErrMsg = Util.checkForRegisteredCode(sample.sampleId,
                            AmcuConfig.getInstance().getFarmerIdDigit(), false);
                    if (getErrMsg != null) {
                        alertForDuplicateEntries(getErrMsg, false);
                        return false;
                    }
                }

                String duplicateMessage = null;

                duplicateMessage = Util.getDuplicateIdOrBarCode(sample.sampleId, sample.sampleBarcode, AddSampleDataActivity.this);

                if (duplicateMessage != null) {
                    alertForDuplicateEntries(duplicateMessage, true);
                    return false;
                } else {
                    return true;
                }

            } else {
                alertForDuplicateEntries("Invalid sample details.", false);
                return false;
            }

        } else {
            alertForDuplicateEntries("Please enter all mandatory details.", false);
            return false;
        }

    }

    public void setUserEntity(SampleDataEntity sampleEnt) {

        if (sampleEnt == null) {
            ClearValues();
            etsocId.setText(new SessionManager(AddSampleDataActivity.this)
                    .getCollectionID());
            isNew = true;
            btnSave.setText("Save");
        } else {
            btnNext.setEnabled(true);
            etsocId.setText(new SessionManager(AddSampleDataActivity.this)
                    .getCollectionID());
            etName.setText(sampleEnt.sampleMode);
            etSampleId.setText(sampleEnt.sampleId);
            etBarcode.setText(sampleEnt.sampleBarcode);

            if (sampleEnt.sampleIsFat != null
                    && !sampleEnt.sampleIsFat.equalsIgnoreCase("false")) {
                checkMA.setChecked(true);
                isMA = "true";
            } else {
                checkMA.setChecked(false);
                isMA = "false";
            }

            if (sampleEnt.sampleIsWeigh != null
                    && !sampleEnt.sampleIsWeigh.equalsIgnoreCase("false")) {
                checkWeigh.setChecked(true);
                isWeigh = "true";
            } else {
                checkWeigh.setChecked(false);
                isWeigh = "false";
            }

        }

    }

    public void setEnable() {
        etName.setEnabled(isEnable);

        if (isEdit) {
            etSampleId.setEnabled(false);
        } else {
            etSampleId.setEnabled(isEnable);
        }

        etBarcode.setEnabled(isEnable);
        checkMA.setFocusable(isEnable);
        checkWeigh.setFocusable(isEnable);
        checkMA.setEnabled(isEnable);
        checkWeigh.setEnabled(isEnable);

    }

    public void ClearValues() {
        etName.requestFocus();
        etName.setText("");
        etSampleId.setText("");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!isSaved) {

        }
    }

    public void getAllFarmIdAndBarcodes() {
        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseInstance(
        );

        FarmerDao farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        ArrayList<FarmerEntity> farmerEntities = (ArrayList<FarmerEntity>) farmerDao.findAll();
        for (int i = 0, len = farmerEntities.size(); i < len; i++) {
            allFarmId.add(farmerEntities.get(i).farmer_id);
            allFarmBarcodes.add(farmerEntities.get(i).farmer_barcode);
        }


    }

    public void alertForDuplicateEntries(String msg, boolean isDuplicate) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                AddSampleDataActivity.this);
        // set title
        if (isDuplicate) {
            alertDialogBuilder.setTitle("Duplicate entry!");
        } else {
            alertDialogBuilder.setTitle("Invalid format!");
        }
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false);

//        if(isDuplicate)
//        {
//            alertDialogBuilder .setPositiveButton("Insert",
//                    new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int id) {
//
//                            dialog.cancel();
//
//                        }
//                    });
//        }

        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

}
