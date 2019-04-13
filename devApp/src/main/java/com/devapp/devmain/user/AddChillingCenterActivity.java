package com.devapp.devmain.user;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devApp.R;

import java.util.Locale;


public class AddChillingCenterActivity extends Activity implements OnClickListener {

    private CenterEntity centerEntity;
    private boolean duplicateEntry;
    private Button btnSubmit;
    private Button btnCancel;
    private Button btnDetails;
    private EditText etChillingCenterId;
    private EditText etCenterName;
    private EditText etCenterId;
    private EditText etContactName;
    private EditText etContactNumber;
    private EditText etContactEmail;
    private EditText etCenterBarcode;
    private EditText etCenterRoute;
    private CenterEntity selectedEntity;
    private TextView tvHeader;
    private SessionManager session;
    private boolean isNew;
    private boolean isEdit;
    private boolean isEnable;
    private boolean isSuccess;
    private Spinner spSingleOrMultiple;
    private Spinner spCattleType;
    private Context mContext;
    private String newCenter;
    private TextView tvMCCStatus;
    private TableRow trMCCStatus;
    private String activeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.actvity_chilling_details);
        mContext = AddChillingCenterActivity.this;
        session = new SessionManager(mContext);
        initializeView();

        btnDetails.setVisibility(View.GONE);
        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnDetails.setOnClickListener(this);

        etChillingCenterId.setText(session.getCollectionID());
        etChillingCenterId.setFocusable(false);
        tvHeader.setText("Center detail");
        btnCancel.setText("Cancel");

        try {
            newCenter = getIntent().getExtras().getString("NewFarmer");
        } catch (Exception e) {
            e.printStackTrace();
        }
        selectedEntity = (CenterEntity) getIntent().getSerializableExtra(
                "SelectedList");

        if (newCenter != null && newCenter.equalsIgnoreCase("NewFarmer")) {
            btnSubmit.setText("Save");
            isEnable = true;
            isNew = true;
            trMCCStatus.setVisibility(View.GONE);
            activeStatus = "1";

        } else {
            etCenterId.setEnabled(false);
            btnSubmit.setText("Edit");
            isEnable = false;
            isEdit = false;
            isNew = false;
            trMCCStatus.setVisibility(View.VISIBLE);
            activeStatus = selectedEntity.activeStatus;

        }
        selectedEntity = (CenterEntity) getIntent().getSerializableExtra(
                "SelectedList");
        Util.alphabetValidation(etCenterId, Util.ONLY_ALPHANUMERIC, mContext, 0);
        Util.alphabetValidation(etCenterName, Util.ALPHANUMERIC_SPACE, mContext, 0);
        Util.alphabetValidation(etContactNumber, Util.ONLY_NUMERIC, mContext, 0);
        Util.alphabetValidation(etContactName, Util.ONLY_ALPHABET, mContext, 0);
        Util.alphabetValidation(etCenterRoute, Util.ALPHANUMERIC_SPACE, mContext, 0);

        etCenterBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etCenterBarcode.length() > 5) {
                    if (!Util.validPrefixForBarcode(etCenterBarcode.getText().toString(), AddChillingCenterActivity.this)) {
                        etCenterBarcode.setText("");
                        etCenterBarcode.requestFocus();
                    }
                }

            }
        });


        etCenterName.requestFocus();
        spCattleType.setFocusable(true);


        etCenterBarcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    etCenterBarcode.clearFocus();
                    spCattleType.requestFocus();
                }
                return true;
            }
        });

        setValues();
        setEnable();
    }


    private void initializeView() {
        etChillingCenterId = (EditText) findViewById(R.id.etChillCenterId);
        etCenterId = (EditText) findViewById(R.id.etCenterId);
        etCenterName = (EditText) findViewById(R.id.etCenterName);
        etContactName = (EditText) findViewById(R.id.etContactName);
        etContactNumber = (EditText) findViewById(R.id.etContactNumber);
        etContactEmail = (EditText) findViewById(R.id.etEmail);
        etCenterBarcode = (EditText) findViewById(R.id.etCenterBarcode);
        etCenterRoute = (EditText) findViewById(R.id.etCenterRoute);
        spCattleType = (Spinner) findViewById(R.id.spMilk);
        tvHeader = (TextView) findViewById(R.id.tvheader);
        tvMCCStatus = (TextView) findViewById(R.id.tvMCCStatus);
        trMCCStatus = (TableRow) findViewById(R.id.trMCCStatus);
        spSingleOrMultiple = (Spinner) findViewById(R.id.spAllowCollection);
        btnSubmit = (Button) findViewById(R.id.btnSave);
        btnCancel = (Button) findViewById(R.id.btnClear);
        btnDetails = (Button) findViewById(R.id.btnDetails);


    }


    @Override
    protected void onStart() {
        super.onStart();
        setForOperator();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSave:

                if (isEdit) {
                    saveDataOnDatabase();
                } else {
                    if (isNew) {
                        saveDataOnDatabase();
                    } else {
                        isEdit = true;
                        isEnable = true;
                        btnSubmit.setText("Update");
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

    private void saveDataOnDatabase() {

        if (checkForValidation()) {
            DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance(
            );
            // centerEntity.centerRoute=Util.getRouteFromChillingCenter(AddChillingCenterActivity.this,centerEntity.centerId);

            try {
                dbHandler.insertChillingCenter(centerEntity);


                isSuccess = true;
                if (isEdit) {
                    Toast.makeText(mContext,
                            "Center details successfully updated!", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(mContext,
                            "Center details successfully added!", Toast.LENGTH_SHORT)
                            .show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            //Removed database close;

        } else {
            if (duplicateEntry) {
                Toast.makeText(mContext,
                        "Duplicate Center Id!", Toast.LENGTH_LONG)
                        .show();
            }
        }
        if (isSuccess) {
            finish();
        }
    }

    public void Cancel() {
        finish();
    }

    private void setValues() {

        if (selectedEntity == null || selectedEntity.centerId == null) {

            btnSubmit.setText("Save");
            isNew = true;
            isEnable = true;
            setEnable();
            ClearValues();
            setSpinnerForSingleOrMultiple(AmcuConfig.getInstance().getCriteriaSingleOrMultiple());
            setSpinnerForCattleType("COW");

        } else {

            etCenterId.setText(selectedEntity.centerId);
            etContactName.setText(selectedEntity.contactPerson);
            etCenterName.setText(selectedEntity.centerName);
            etContactNumber.setText(selectedEntity.contactNumber);
            etContactEmail.setText(selectedEntity.contactEmailId);

            etCenterRoute.setText(selectedEntity.centerRoute);

            if (selectedEntity.centerBarcode == null) {
                etCenterBarcode.setText("");
            } else {
                etCenterBarcode.setText(selectedEntity.centerBarcode);
            }
            setSpinnerForSingleOrMultiple(selectedEntity.singleOrMultiple);
            setSpinnerForCattleType(selectedEntity.cattleType);

        }

        if (selectedEntity != null && selectedEntity.activeStatus.equalsIgnoreCase("1")) {
            tvMCCStatus.setText(AddChillingCenterActivity.this.getResources().getString(R.string.active));
        } else {
            tvMCCStatus.setText(AddChillingCenterActivity.this.getResources().getString(R.string.in_active));
        }

    }

    private void setEnable() {

        etContactEmail.setEnabled(isEnable);
        etCenterName.setEnabled(isEnable);
        etContactName.setEnabled(isEnable);
        etContactNumber.setEnabled(isEnable);
        etChillingCenterId.setEnabled(isEnable);
        etCenterBarcode.setEnabled(isEnable);
        etCenterRoute.setEnabled(isEnable);
        spSingleOrMultiple.setEnabled(isEnable);
        spCattleType.setEnabled(isEnable);

    }

    private void ClearValues() {
        etCenterName.requestFocus();
        etContactName.setText("");
        etCenterName.setText("");
        etCenterId.setText("");
        etContactNumber.setText("");
        etContactEmail.setText("");
        etCenterBarcode.setText("");
        etCenterRoute.setText("");

        etChillingCenterId.setText(session.getCollectionID());
        setSpinnerForSingleOrMultiple(AmcuConfig.getInstance().getCriteriaSingleOrMultiple());
        setSpinnerForCattleType("COW");


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private boolean checkForValidation() {
        boolean validation = true;
        duplicateEntry = false;
        centerEntity = new CenterEntity();
        if (etCenterId.getText().toString() == null
                || etCenterId.getText().toString().length() < 1) {
            etCenterId.requestFocus();
            Toast.makeText(mContext,
                    "Please enter valid center Id!", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if (((etCenterBarcode.getText().toString().length() < 11)
                && !etCenterBarcode.getText().toString().equalsIgnoreCase(""))) {
            etCenterBarcode.requestFocus();
            Toast.makeText(mContext,
                    "Please enter valid center barcode!", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if (etCenterName.getText().toString() == null
                || etCenterName.getText().toString().length() < 1) {
            etCenterName.requestFocus();
            Toast.makeText(mContext,
                    "Please enter valid center name!", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if ((etContactNumber.getText().toString() != null && etContactNumber.getText().toString().length() > 0)
                && !Util.validateMobileNumber(AddChillingCenterActivity.this, etContactNumber.getText().toString())) {
            etContactNumber.requestFocus();
            Toast.makeText(mContext, "Invalid phone number",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (AmcuConfig.getInstance().getSmartCCFeature()
                && etCenterRoute.getText().toString().trim().length() <= 0) {

            Toast.makeText(mContext, "Please enter the route code",
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {

            String centerId = null;
            centerEntity.chillingCenterId = etChillingCenterId.getText().toString().replaceAll(" ", "").toUpperCase(Locale.ENGLISH);
            centerEntity.centerId = etCenterId.getText().toString().toUpperCase(Locale.ENGLISH);
            centerEntity.centerName = etCenterName.getText().toString();
            centerEntity.contactPerson = etContactName.getText().toString();
            centerEntity.contactNumber = etContactNumber.getText().toString();
            centerEntity.contactEmailId = etContactEmail.getText().toString();
            centerEntity.centerRoute = etCenterRoute.getText().toString();
            centerEntity.singleOrMultiple = spSingleOrMultiple.getSelectedItem().toString();
            centerEntity.cattleType = spCattleType.getSelectedItem().toString();
            centerEntity.activeStatus = activeStatus;
            if (etCenterBarcode.getText().toString().replace(" ", "").equalsIgnoreCase("")) {
                centerEntity.centerBarcode = null;
            } else {
                centerEntity.centerBarcode = etCenterBarcode.getText().toString().toUpperCase(Locale.ENGLISH);
            }

            centerEntity.registerDate = System.currentTimeMillis();

            if (centerEntity.centerId.length() > 0) {
                int id = 0;
                try {
                    id = Integer.parseInt(centerEntity.centerId);
                } catch (NumberFormatException e) {
                    id = -1;
                    e.printStackTrace();
                }
                if ((id < 1) && (id != -1)) {
                    alertForDuplicateEntries("Center Id should be greater than 0.", false);
                    etCenterId.requestFocus();
                    return false;
                } else if (id == -1) {
                    String getErrMsg = Util.checkForRegisteredCode(centerEntity.centerId
                            , AmcuConfig.getInstance().getFarmerIdDigit(), true);
                    if (getErrMsg != null) {
                        alertForDuplicateEntries(getErrMsg, false);
                        return false;
                    }
                } else {

                    centerId = Util.paddingFarmerId(etCenterId.getText()
                            .toString().replaceAll(" ", "").toUpperCase(Locale.ENGLISH), AmcuConfig.getInstance().getFarmerIdDigit());
                    String getErrMsg = Util.checkForRegisteredCode(centerId
                            , AmcuConfig.getInstance().getFarmerIdDigit(), true);
                    if (getErrMsg != null) {
                        alertForDuplicateEntries(getErrMsg, false);
                        return false;
                    }

                }
            }

            if (selectedEntity == null) {
                String duplicateMessage = null;

                if (centerId == null) {
                    duplicateMessage = Util.getDuplicateIdOrBarCode(centerEntity.centerId, centerEntity.centerBarcode,
                            AddChillingCenterActivity.this);
                } else {
                    duplicateMessage = Util.getDuplicateIdOrBarCode(centerId, centerEntity.centerBarcode,
                            AddChillingCenterActivity.this);
                }


                if (duplicateMessage != null) {
                    alertForDuplicateEntries(duplicateMessage, true);
                    return false;
                } else {
                    return true;
                }

            } else if (selectedEntity != null) {

                String duplicateMessage = null;

                if ((selectedEntity.centerBarcode != null && centerEntity.centerBarcode != null) &&
                        !(selectedEntity.centerBarcode.equalsIgnoreCase(centerEntity.centerBarcode))) {
                    duplicateMessage = Util.getDuplicateIdOrBarCode("0", centerEntity.centerBarcode,
                            AddChillingCenterActivity.this);
                } else if (selectedEntity.centerBarcode == null && centerEntity.centerBarcode != null) {
                    duplicateMessage = Util.getDuplicateIdOrBarCode("0", centerEntity.centerBarcode, AddChillingCenterActivity.this);
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


    private void alertForDuplicateEntries(String msg, boolean isDuplicate) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                mContext);
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

    private void setForOperator() {
        if (Util.isOperator(AddChillingCenterActivity.this)) {
            btnSubmit.setEnabled(false);
        }

    }

    private void setSpinnerForSingleOrMultiple(String spValue) {
        ArrayAdapter adapter = (ArrayAdapter) spSingleOrMultiple.getAdapter();
        int spinnerPosition = adapter.getPosition(spValue);
        spSingleOrMultiple.setSelection(spinnerPosition);
    }

    private void setSpinnerForCattleType(String cattleType) {
        ArrayAdapter myAdap = (ArrayAdapter) spCattleType.getAdapter();
        int spinnerPosition = myAdap.getPosition(cattleType);
        spCattleType.setSelection(spinnerPosition);
    }

}
