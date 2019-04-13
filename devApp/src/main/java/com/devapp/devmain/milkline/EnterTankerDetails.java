package com.devapp.devmain.milkline;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.TankerCollectionDao;
import com.devapp.devmain.milkline.entities.TankerCollectionEntity;
import com.devapp.devmain.milkline.service.PostTankerRecords;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by u_pendra on 21/12/16.
 */

public class EnterTankerDetails extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public String compartmentNumber1 = "CN1";
    public String compartmentNumber2 = "CN2";
    public String status = "STATUS";
    public long miliTime;
    ArrayList<String> compartmentNumbers;
    boolean isClrEnable;
    java.text.DecimalFormat decimalFormatQuantity = new java.text.DecimalFormat("#0.00");
    java.text.DecimalFormat decimalFormatFS = new java.text.DecimalFormat("#0.0");
    TankerCollectionDao tankerCollectionDao;
    private com.eevoskos.robotoviews.widget.RobotoEditText etTankerNumer,
            etBcc, etRoute, etFat, etSnf, etAlcohol, etQuantity, etComment, etTemp, etClr;
    private android.support.v7.widget.CardView cardViewHeader;
    private android.support.v7.widget.AppCompatSpinner spCompartmentNumber1, spCompartmentNumber2, spStatus, spQuantityUnit;
    private LinearLayout lnQuality, linearLayout;
    private Button btnCancel, btnSave;
    private TankerCollectionEntity lastTankerCollectionEntity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_tanker_details);
        try {
            lastTankerCollectionEntity = (TankerCollectionEntity) getIntent().getSerializableExtra("TANKER_ENTITY");
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeView();

        if (lastTankerCollectionEntity != null) {
            setDataFromIntent();
            enableOrDisable(false);
            btnSave.setText("Edit");
            btnSave.setVisibility(View.GONE);
            btnCancel.requestFocus();

        } else {
            enableOrDisable(true);
            setOnKeyClickListener();
        }

        etBcc.setText(new SessionManager(EnterTankerDetails.this).getCollectionID());
        etBcc.setEnabled(false);

        onClickAndOnSelect();
        setSpinner();
    }

    @Override
    protected void onStart() {
        super.onStart();
        tankerCollectionDao =
                (TankerCollectionDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_TANKER);

        setVisiblityForSnfOrClr();
        miliTime = Calendar.getInstance().getTimeInMillis();
    }

    public void enableOrDisable(boolean enable) {

        etTankerNumer.setEnabled(enable);
        etAlcohol.setEnabled(enable);
        etFat.setEnabled(enable);
        etSnf.setEnabled(enable);
        etComment.setEnabled(enable);
        etRoute.setEnabled(enable);
        etQuantity.setEnabled(enable);
        etClr.setEnabled(enable);
        etTemp.setEnabled(enable);
        spCompartmentNumber1.setEnabled(enable);
        spStatus.setEnabled(enable);
        spCompartmentNumber2.setEnabled(enable);
        spQuantityUnit.setEnabled(enable);

    }

    public void initializeView() {
        etTankerNumer = (com.eevoskos.robotoviews.widget.RobotoEditText) findViewById(R.id.etTankerNumer);
        etBcc = (com.eevoskos.robotoviews.widget.RobotoEditText) findViewById(R.id.etBcc);
        etRoute = (com.eevoskos.robotoviews.widget.RobotoEditText) findViewById(R.id.etRoute);
        etFat = (com.eevoskos.robotoviews.widget.RobotoEditText) findViewById(R.id.etFat);
        etSnf = (com.eevoskos.robotoviews.widget.RobotoEditText) findViewById(R.id.etSnf);
        etAlcohol = (com.eevoskos.robotoviews.widget.RobotoEditText) findViewById(R.id.etAlcohol);
        etQuantity = (com.eevoskos.robotoviews.widget.RobotoEditText) findViewById(R.id.etQuantity);
        etComment = (com.eevoskos.robotoviews.widget.RobotoEditText) findViewById(R.id.etComment);
        etClr = (com.eevoskos.robotoviews.widget.RobotoEditText) findViewById(R.id.etClr);
        etTemp = (com.eevoskos.robotoviews.widget.RobotoEditText) findViewById(R.id.etTemp);


        cardViewHeader = (android.support.v7.widget.CardView) findViewById(R.id.cardViewHeader);
        spCompartmentNumber1 = (android.support.v7.widget.AppCompatSpinner) findViewById(R.id.spCompartmentNumber1);
        spCompartmentNumber2 = (android.support.v7.widget.AppCompatSpinner) findViewById(R.id.spCompartmentNumber2);
        spStatus = (android.support.v7.widget.AppCompatSpinner) findViewById(R.id.spStatus);
        spQuantityUnit = (android.support.v7.widget.AppCompatSpinner) findViewById(R.id.spQuantityUnit);
        lnQuality = (LinearLayout) findViewById(R.id.lnQuality);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSave = (Button) findViewById(R.id.btnSave);


    }

    public void setDataFromIntent() {

        etAlcohol.setText(String.valueOf(lastTankerCollectionEntity.alcohol));
        etBcc.setText(String.valueOf(lastTankerCollectionEntity.centerId));
        etComment.setText(String.valueOf(lastTankerCollectionEntity.comments));
        etRoute.setText(lastTankerCollectionEntity.routeNumber);
        etFat.setText(String.valueOf(lastTankerCollectionEntity.fat));
        etSnf.setText(String.valueOf(lastTankerCollectionEntity.snf));
        etQuantity.setText(String.valueOf(lastTankerCollectionEntity.quantity));
        etTankerNumer.setText(String.valueOf(lastTankerCollectionEntity.tankerNumber));
        etClr.setText(String.valueOf(lastTankerCollectionEntity.clr));
        etTemp.setText(String.valueOf(lastTankerCollectionEntity.temperature));

        compartmentNumbers = new ArrayList<>();
        compartmentNumbers = lastTankerCollectionEntity.compartmentNumbers;

        if (lastTankerCollectionEntity.compartmentNumbers.size() > 1) {
            compartmentNumber2 = lastTankerCollectionEntity.compartmentNumbers.get(1);
        }
        compartmentNumber1 = lastTankerCollectionEntity.compartmentNumbers.get(0);

        status = lastTankerCollectionEntity.status;


    }

    public void onClickAndOnSelect() {
        btnCancel.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        spCompartmentNumber1.setOnItemSelectedListener(this);
        spCompartmentNumber2.setOnItemSelectedListener(this);
        spStatus.setOnItemSelectedListener(this);
    }

    public void setSpinner() {
        ArrayList<String> arrString = new ArrayList<>();
        String[] arraySpinner = getResources().getStringArray(R.array.CompartmentNumber);
        arrString = new ArrayList(Arrays.asList(arraySpinner));
        arrString.add(compartmentNumber1);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this
                , R.layout.custom_spinner_item, arrString);
        spCompartmentNumber1.setAdapter(arrayAdapter);
        spCompartmentNumber1.setSelection(arrayAdapter.getPosition(compartmentNumber1));

        arraySpinner = getResources().getStringArray(R.array.CompartmentNumber);
        arrString = new ArrayList(Arrays.asList(arraySpinner));
        arrString.add(compartmentNumber2);

        arrayAdapter = new ArrayAdapter<String>(this
                , R.layout.custom_spinner_item, arrString);
        spCompartmentNumber2.setAdapter(arrayAdapter);
        spCompartmentNumber2.setSelection(arrayAdapter.getPosition(compartmentNumber2));

        arraySpinner = getResources().getStringArray(R.array.Status);
        arrString = new ArrayList(Arrays.asList(arraySpinner));
        arrString.add(status);
        arrayAdapter = new ArrayAdapter<String>(this
                , R.layout.custom_spinner_item, arrString);
        spStatus.setAdapter(arrayAdapter);
        spStatus.setSelection(arrayAdapter.getPosition(status));


        arraySpinner = getResources().getStringArray(R.array.QuantityUnit);
        arrString = new ArrayList(Arrays.asList(arraySpinner));
        arrayAdapter = new ArrayAdapter<String>(this
                , R.layout.custom_spinner_item, arrString);
        spQuantityUnit.setAdapter(arrayAdapter);
        if (lastTankerCollectionEntity != null)
            spQuantityUnit.setSelection(arrayAdapter.getPosition(lastTankerCollectionEntity.quantityUnit));

    }

    @Override
    public void onClick(View view) {

        if (view == btnSave) {
            if (btnSave.getText().toString().equalsIgnoreCase("Edit")) {
                btnSave.setText("Save");
                enableOrDisable(true);
                etTankerNumer.requestFocus();
                etTankerNumer.setSelection(etTankerNumer.getText().toString().length());
            } else if (validateData()) {
                TankerCollectionEntity tankerCollectionEntity = getTankerEntity();
                tankerCollectionEntity.resetSentMarkers();
                try {
                    tankerCollectionDao.saveOrUpdate(tankerCollectionEntity);
                    Util.displayErrorToast("Data entered successfully!", EnterTankerDetails.this);
                    startService(new Intent(EnterTankerDetails.this, PostTankerRecords.class));
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (view == btnCancel) {
            finish();
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public boolean validateData() {
        boolean isValidate = true;


        ValidationHelper validationHelper = new ValidationHelper();

        String fat = validationHelper.getDoubleFromString(decimalFormatFS, etFat.getText().toString().trim(), -1);
        String snf = validationHelper.getDoubleFromString(decimalFormatFS, etSnf.getText().toString().trim(), -1);
        String alcohol = validationHelper.getDoubleFromString(decimalFormatFS, etAlcohol.getText().toString().trim(), -1);
        String quantity = validationHelper.getDoubleFromString(decimalFormatQuantity, etQuantity.getText().toString().trim(), -1);
        String clr = validationHelper.getDoubleFromString(
                decimalFormatQuantity, etClr.getText().toString().trim(), -1);

        String temp = validationHelper.getDoubleFromString(
                decimalFormatQuantity, etTemp.getText().toString().trim(), -30);

        if (validationHelper.getEditTextLength(etTankerNumer) <= 0 ||
                validationHelper.getEditTextLength(etTankerNumer) > 20) {
            displayToast("Please enter valid tanker number! ", etTankerNumer);
            return false;
        } else if (validationHelper.getEditTextLength(etComment) <= 0 ||
                validationHelper.getEditTextLength(etComment) > 150) {
            displayToast("Please enter valid comment! ", etComment);
            return false;

        } else if (isClrEnable && (Double.parseDouble(clr) < SmartCCConstants.MIN_CLR
                || Double.parseDouble(clr) > SmartCCConstants.MAX_CLR)) {
            displayToast("Please enter valid CLR! ", etClr);
            return false;
        } else if (Double.parseDouble(temp) < SmartCCConstants.MIN_TEMP
                || Double.parseDouble(temp) > SmartCCConstants.MAX_TEMP) {
            displayToast("Please enter valid temperature! ", etTemp);
            return false;
        } else if (validationHelper.getEditTextLength(etRoute) <= 0 ||
                validationHelper.getEditTextLength(etRoute) > 20) {
            displayToast("Please enter valid route! ", etRoute);
            return false;
        } else if (validationHelper.getEditTextLength(etBcc) <= 0 ||
                validationHelper.getEditTextLength(etBcc) > 20) {
            displayToast("Please enter valid bcc! ", etBcc);
            return false;
        } else if (Double.parseDouble(fat) < 0 || Double.parseDouble(fat) > Util.MAX_FAT_LIMIT) {
            displayToast("Please enter valid fat! ", etFat);
            return false;
        } else if (!isClrEnable && (Double.parseDouble(snf) < 0
                || Double.parseDouble(snf) > Util.MAX_SNF_LIMIT)) {
            displayToast("Please enter valid SNF! ", etSnf);
            return false;
        } else if (Double.parseDouble(alcohol) < 0 || Double.parseDouble(alcohol) > 100) {
            displayToast("Please enter valid alcohol! ", etAlcohol);
            return false;
        } else if (Double.parseDouble(quantity) < 0 || Double.parseDouble(quantity) > Util.MAX_WEIGHT_LIMIT) {
            displayToast("Please enter valid quantity! ", etQuantity);
            return false;
        } else if (spStatus.getSelectedItem().toString().equalsIgnoreCase("status")) {
            Util.displayErrorToast("Select valid status", EnterTankerDetails.this);
            spStatus.requestFocus();
            return false;
        } else if (spCompartmentNumber2.getSelectedItem().toString().equalsIgnoreCase("CN2")
                && spCompartmentNumber1.getSelectedItem().toString().equalsIgnoreCase("CN1")) {
            Util.displayErrorToast("Enter any of one valid compartment number!", EnterTankerDetails.this);
            if (spCompartmentNumber1.getSelectedItem().toString().equalsIgnoreCase("CN1"))
                spCompartmentNumber1.requestFocus();
            else
                spCompartmentNumber2.requestFocus();

            return false;
        }


        return isValidate;
    }

    public TankerCollectionEntity getTankerEntity() {
        TankerCollectionEntity tankerCollectionEntity = new TankerCollectionEntity();

        if (isClrEnable) {
            double snf = Util.getSNF(Double.parseDouble(etFat.getText().toString().trim()),
                    Double.parseDouble(etClr.getText().toString().trim()));
            tankerCollectionEntity.snf = Double.valueOf(decimalFormatFS.
                    format(Double.valueOf(snf)));
            etSnf.setText(String.valueOf(tankerCollectionEntity.snf));
        } else {
            String clr = String.valueOf(Util.getCLR(Double.valueOf(etFat.getText().toString().trim()),
                    Double.valueOf(etSnf.getText().toString().trim())));
            tankerCollectionEntity.clr = Double.valueOf(decimalFormatQuantity.
                    format(Double.valueOf(clr)));
            etClr.setText(String.valueOf(tankerCollectionEntity.clr));
        }

        tankerCollectionEntity.tankerNumber = etTankerNumer.getText().toString().trim();
        tankerCollectionEntity.centerId = etBcc.getText().toString().trim();
        tankerCollectionEntity.routeNumber = etRoute.getText().toString();
        tankerCollectionEntity.fat = Double.valueOf(decimalFormatFS.
                format(Double.valueOf(etFat.getText().toString().trim())));
        tankerCollectionEntity.snf = Double.valueOf(decimalFormatFS.
                format(Double.valueOf(etSnf.getText().toString().trim())));
        tankerCollectionEntity.alcohol = Double.valueOf(decimalFormatFS.
                format(Double.valueOf(etAlcohol.getText().toString().trim())));
        tankerCollectionEntity.quantity = Double.valueOf(decimalFormatQuantity.
                format(Double.valueOf(etQuantity.getText().toString().trim())));

        tankerCollectionEntity.clr = Double.valueOf(decimalFormatQuantity.
                format(Double.valueOf(etClr.getText().toString().trim())));
        tankerCollectionEntity.temperature = Double.valueOf(decimalFormatQuantity.
                format(Double.valueOf(etTemp.getText().toString().trim())));

        tankerCollectionEntity.supervisorCode = new SessionManager(EnterTankerDetails.this).getUserId();

        if (lastTankerCollectionEntity == null) {
            tankerCollectionEntity.collectionTime = miliTime;
        } else {
            tankerCollectionEntity.collectionTime = lastTankerCollectionEntity.collectionTime;
        }

        compartmentNumbers = new ArrayList<>();
        if (!spCompartmentNumber1.getSelectedItem().toString().equalsIgnoreCase("CN1"))
            compartmentNumbers.add(spCompartmentNumber1.getSelectedItem().toString());
        if (!spCompartmentNumber2.getSelectedItem().toString().equalsIgnoreCase("CN2"))
            compartmentNumbers.add(spCompartmentNumber2.getSelectedItem().toString());

        tankerCollectionEntity.compartmentNumbers = compartmentNumbers;
        tankerCollectionEntity.status = spStatus.getSelectedItem().toString();
        tankerCollectionEntity.comments = etComment.getText().toString().trim();
        tankerCollectionEntity.quantityUnit = spQuantityUnit.getSelectedItem().toString();

        tankerCollectionEntity.postDate = SmartCCUtil.getDateInPostFormat();
        tankerCollectionEntity.postShift = SmartCCUtil.getShiftInPostFormat(EnterTankerDetails.this);


        tankerCollectionEntity.sent = 0;

        return tankerCollectionEntity;

    }


    public void displayToast(String message, EditText editText) {
        Util.displayErrorToast(message, EnterTankerDetails.this);
        editText.requestFocus();
        editText.setSelection(editText.getText().toString().length());

    }


    public void setOnKeyClickListener() {
        etTankerNumer.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_UP &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {

                    etRoute.requestFocus();
                    etRoute.setSelection(etRoute.getText().toString().length());
                    return true;
                }
                return false;
            }
        });

        etRoute.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_UP &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    etFat.requestFocus();
                    etFat.setSelection(etFat.getText().toString().length());
                    return true;
                }
                return false;
            }
        });

        etFat.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_UP &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    if (!isClrEnable) {
                        etSnf.requestFocus();
                        etSnf.setSelection(etSnf.getText().toString().length());
                    } else {
                        etClr.requestFocus();
                        etClr.setSelection(etSnf.getText().toString().length());
                    }

                    return true;
                }
                return false;
            }
        });

        etSnf.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_UP &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    etAlcohol.requestFocus();
                    etAlcohol.setSelection(etAlcohol.getText().toString().length());
                    return true;
                }
                return false;
            }
        });


        etClr.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_UP &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    etAlcohol.requestFocus();
                    etAlcohol.setSelection(etAlcohol.getText().toString().length());
                    return true;
                }
                return false;
            }
        });

        etAlcohol.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_UP &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    etTemp.requestFocus();
                    etTemp.setSelection(etTemp.getText().toString().length());
                    return true;
                }
                return false;
            }
        });

        etTemp.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_UP &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    etQuantity.requestFocus();
                    etQuantity.setSelection(etQuantity.getText().toString().length());
                    return true;
                }
                return false;
            }
        });

        etQuantity.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_UP &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    spQuantityUnit.requestFocus();
                    return true;
                }
                return false;
            }
        });

        etComment.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_UP &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    spCompartmentNumber1.requestFocus();
                    return true;
                }
                return false;
            }
        });
    }

    public void setVisiblityForSnfOrClr() {
        if (AmcuConfig.getInstance().
                getSNFOrCLRFromTanker().equalsIgnoreCase(SmartCCConstants.CLR)) {
            isClrEnable = true;
            etClr.setVisibility(View.VISIBLE);
            etSnf.setVisibility(View.GONE);
        } else {
            isClrEnable = false;
            etSnf.setVisibility(View.VISIBLE);
            etClr.setVisibility(View.GONE);
        }
    }


}
