package com.devapp.smartcc;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.CollectionCenterDao;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.macollection.MilkRejectActivity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Upendra on 10/18/2016.
 */
public class EnterRejectDetails extends Activity implements View.OnClickListener {

    private static int mYear, mMonth, mDay, mHour, mMin;
    Spinner spShift, spCattleType, spRejectReason;
    EditText etCenterId, etSampleId;
    EditText etDate;
    AmcuConfig amcuConfig;
    String cattleType;
    int lastSid;
    int iSID;
    ValidationHelper validationHelper;
    SmartCCUtil smartCCutil;
    long dateInMillis;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_reject_details);

    }

    @Override
    protected void onStart() {
        super.onStart();

        validationHelper = new ValidationHelper();
        smartCCutil = new SmartCCUtil(EnterRejectDetails.this);

        initializeView();
        setDateField();
        initializeShift();
        initializeDateField();
        getCurrentSid();
    }

    public void initializeView() {
        amcuConfig = AmcuConfig.getInstance();
        etDate = (EditText) findViewById(R.id.etDate);
        etCenterId = (EditText) findViewById(R.id.etCenterId);
        etSampleId = (EditText) findViewById(R.id.etSampleId);
        spShift = (Spinner) findViewById(R.id.spShift);

        spCattleType = (Spinner) findViewById(R.id.spCattleType);
        spRejectReason = (Spinner) findViewById(R.id.spReject);

        Util.alphabetValidation(etSampleId, Util.ONLY_NUMERIC, this, 4);
        Util.alphabetValidation(etCenterId, Util.ONLY_ALPHANUMERIC, this, 10);
        etDate.setOnClickListener(this);
        etSampleId.setText("" + iSID);
        etSampleId.setSelection(etSampleId.length());
        etCenterId.requestFocus();
        ArrayAdapter myAdap = (ArrayAdapter) spCattleType.getAdapter();
        int spinnerPosition = myAdap.getPosition("COW");
        spCattleType.setSelection(spinnerPosition);


        etCenterId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                CenterEntity centerEntity = validateCenterId();

                if (centerEntity != null && centerEntity.cattleType != null) {
                    cattleType = centerEntity.cattleType;
                    if (centerEntity.cattleType.equalsIgnoreCase("both")) {
                        spCattleType.setEnabled(true);
                    } else {
                        spCattleType.setEnabled(false);
                    }

                    ArrayAdapter myAdap = (ArrayAdapter) spCattleType.getAdapter();
                    int spinnerPosition = myAdap.getPosition(centerEntity.cattleType);
                    spCattleType.setSelection(spinnerPosition);

                }

            }
        });


        spShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getCurrentSid();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        resetCenterId();

    }

    public void OnNext(View view) {

        SmartCCUtil smartCCUtil = new SmartCCUtil(EnterRejectDetails.this);

        if (dateInMillis > smartCCUtil.getDateInMillis(0, true)) {
            Util.displayErrorToast("Date should be less than today date!", EnterRejectDetails.this);
            return;
        }
        if (dateInMillis < smartCCUtil.getDateInMillis(-1, true)) {
            Util.displayErrorToast("Please check the date!", EnterRejectDetails.this);
            return;
        }

        if (!validateSID()) {
            return;
        }


        if (validateCenterId() == null) {
            Util.displayErrorToast("Please enter valid center Id!", EnterRejectDetails.this);
            return;
        }
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        CollectionCenterDao collectionCenterDao = (CollectionCenterDao) DaoFactory.getDao(CollectionConstants.COLLECTION_CENTER);
        CenterEntity centerEntity = collectionCenterDao.findByCenterId(etCenterId.getText().toString().toUpperCase().trim());
        if (centerEntity != null && !centerEntity.activeStatus.equalsIgnoreCase("1")) {
            Util.displayErrorToast("Inactive center id", EnterRejectDetails.this);
            return;
        }


        if (dateInMillis == smartCCUtil.getDateInMillis(0, true)) {
//            if (spShift.getSelectedItem().toString().equalsIgnoreCase("Morning")) {
//                saveSession.setMorningSID(Integer.parseInt(etSampleId.getText().toString().trim())+1);
//            } else {
//                saveSession.setEveningSID(Integer.parseInt(etSampleId.getText().toString().trim())+1);
//            }
        }

        Intent intent = new Intent(EnterRejectDetails.this, MilkRejectActivity.class);
        intent.putExtra("CENTERID", etCenterId.getText().toString());
        intent.putExtra("SID", etSampleId.getText().toString());
        intent.putExtra("SHIFT", spShift.getSelectedItem().toString());
        intent.putExtra("REJECT_REASON", spRejectReason.getSelectedItem().toString());
        intent.putExtra("CATTLE_TYPE", spCattleType.getSelectedItem().toString());
        intent.putExtra("DATE", etDate.getText().toString().trim());
        if (spShift.getSelectedItem().toString().equalsIgnoreCase("Morning")) {
            intent.putExtra("MILLI_TIME", smartCCUtil.getCalendarTime(mYear, mDay, mMonth, mHour, mMin));
        } else {
            intent.putExtra("MILLI_TIME", smartCCUtil.getCalendarTime(mYear, mDay, mMonth, mHour, mMin));
        }
        startActivity(intent);
        finish();
    }

    public void OnCancel(View view) {

        finish();
    }

    @Override
    public void onClick(View v) {

        if (v == etDate) {
            datePickerDialog.show();
        }

    }

    private void setDateField() {

        Calendar newCalendar = smartCCutil.getReportCalendarInstance(0);
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = smartCCutil.getReportCalendarInstance(0);
                newDate.set(year, monthOfYear, dayOfMonth);

                mYear = newDate.get(Calendar.YEAR);
                mMonth = newDate.get(Calendar.MONTH);
                mDay = newDate.get(Calendar.DAY_OF_MONTH);
                mMin = newDate.get(Calendar.MINUTE);
                mHour = newDate.get(Calendar.HOUR_OF_DAY);

                newDate.set(Calendar.HOUR_OF_DAY, 0);
                newDate.set(Calendar.MINUTE, 0);
                newDate.set(Calendar.SECOND, 0);
                newDate.set(Calendar.MILLISECOND, 0);
                dateInMillis = newDate.getTimeInMillis();
                etDate.setText(dateFormatter.format(newDate.getTime()));
                getCurrentSid();
                enableOrDisableShift();


            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }


    public boolean validateSID() {

        int sid = 0;
        try {
            sid = Integer.parseInt(etSampleId.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sid <= 0) {
            Toast.makeText(this, "Please Enter Valid Serail Id", Toast.LENGTH_SHORT).show();
            resetEtTextSID();
            return false;
        }

        if ((etSampleId.getText().toString().trim().length() > 7) || (Integer.valueOf(etSampleId.getText().toString().trim()) > Util.LAST_SEQ_NUM)) {
            resetEtTextSID();
            Toast.makeText(this, "SID should be less than " + Util.LAST_SEQ_NUM, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etSampleId.getText().toString().trim().equalsIgnoreCase("0")) {
            resetEtTextSID();
            Toast.makeText(this, "Serail Id can't be 0", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etSampleId.getText().toString().trim().equalsIgnoreCase("")) {
            resetEtTextSID();
            Toast.makeText(this, "Please Enter Valid Serail Id", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Integer.valueOf(etSampleId.getText().toString().trim()) < lastSid) {
            resetEtTextSID();
            Toast.makeText(this, "SID should be greater than or same as " + lastSid, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public void resetEtTextSID() {
        // etSId.setText("");
        etSampleId.setFocusable(true);
    }

    public CenterEntity validateCenterId() {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        CenterEntity centerEntity = dbh.getCenterEntity(etCenterId.getText().toString()
                .trim(), Util.CHECK_DUPLICATE_CENTERCODE);

        if (centerEntity == null) {
            etCenterId.requestFocus();
            return centerEntity;
        } else {
            return centerEntity;
        }
    }


    public void initializeDateField() {
        Calendar newCalendar = smartCCutil.getReportCalendarInstance(0);
        etDate.setText(dateFormatter.format(newCalendar.getTime()));

        mYear = newCalendar.get(Calendar.YEAR);
        mMonth = newCalendar.get(Calendar.MONTH);
        mDay = newCalendar.get(Calendar.DAY_OF_MONTH);
        mMin = newCalendar.get(Calendar.MINUTE);
        mHour = newCalendar.get(Calendar.HOUR_OF_DAY);

        newCalendar.set(Calendar.HOUR_OF_DAY, 0);
        newCalendar.set(Calendar.MINUTE, 0);
        newCalendar.set(Calendar.SECOND, 0);
        newCalendar.set(Calendar.MILLISECOND, 0);

        dateInMillis = newCalendar.getTimeInMillis();
    }


    public void initializeShift() {
        if (Util.getCurrentShift().equalsIgnoreCase(AppConstants.Shift.MORNING)) {
            spShift.setSelection(0);
        } else {
            spShift.setSelection(1);
        }

        enableOrDisableShift();
    }


    public void enableOrDisableShift() {
        if (dateInMillis == smartCCutil.getDateInMillis(0, true)) {
            if (Util.getCurrentShift().equalsIgnoreCase(AppConstants.Shift.MORNING)) {
                spShift.setSelection(0);
                spShift.setEnabled(false);
            } else {
                spShift.setEnabled(true);
            }
        } else {
            spShift.setEnabled(true);
        }
    }


    public void getCurrentSid() {
        CollectionRecordDao collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        String selectedDate = new SmartCCUtil(EnterRejectDetails.this).changeDateFormat(etDate.getText().toString().trim(),
                "dd-MMM-yyyy", "yyyy-MM-dd");
        lastSid = collectionRecordDao.getNextSampleNumber(selectedDate,
                spShift.getSelectedItem().toString());
        etSampleId.setText(String.valueOf(lastSid));
    }

    public void resetCenterId() {
        etCenterId.setText("");
    }

}
