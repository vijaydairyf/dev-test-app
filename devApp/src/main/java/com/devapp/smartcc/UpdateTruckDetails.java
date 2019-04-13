package com.devapp.smartcc;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ListPopupWindow;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.database.TruckCCDatabase;
import com.devapp.smartcc.entities.TrucKInfo;
import com.devapp.smartcc.entities.TruckDetailsEntity;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.service.PostTruckRecords;
import com.devApp.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Upendra on 10/3/2016.
 */
public class UpdateTruckDetails extends Activity implements View.OnClickListener, AdapterView.OnItemClickListener,
        View.OnTouchListener {

    private static int mYear, mMonth, mDay, mHour, mMins;
    long dateInMillis, timeInMillis;
    Button btnSubmit, btnCancel;
    AmcuConfig amcuConfig;
    Spinner spShift;
    TrucKInfo truckInfo;
    ListPopupWindow lpw;
    ArrayList<String> list = new ArrayList<>();
    SmartCCUtil smartCCUtil;
    TruckCCDatabase truckCCDatabase;
    int defalutShift = 0;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private EditText etDate, etTime, etShift, etAmount, etMaterial, etCansIn, etCansOut, etRemarks, etTruckSheetId, etcenterId;
    private TruckDetailsEntity truckDetailsEntity;
    private String truckSheetId;
    private SimpleDateFormat dateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_truck_details);

        truckInfo = (TrucKInfo) getIntent().getSerializableExtra("TRUCK_DETAILS");
        InitiallizeView();
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        amcuConfig = AmcuConfig.getInstance();
        etTruckSheetId.setFocusable(false);
        etTruckSheetId.setEnabled(true);
        etTruckSheetId.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.truck_ready_edit, 0);
        smartCCUtil = new SmartCCUtil(UpdateTruckDetails.this);

        setDateField();
        setTimeField();

        initializeDateField();
        initializeTime();
        initializeShift();
        setTruckdetails();

    }

    public void setList(long date, int shift) {
        lpw = new ListPopupWindow(this);
        list = truckCCDatabase.getUncommitedTruckIds(date, shift);
        if (list == null) {
            list = new ArrayList<>();
            list.add("No id found!");
        }
        lpw.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list));
        lpw.setAnchorView(etTruckSheetId);
        lpw.setModal(true);
        lpw.setOnItemClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int DRAWABLE_RIGHT = 2;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getX() >= (v.getWidth() - ((EditText) v)
                    .getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                lpw.show();
                return true;
            }
        }
        return false;
    }

    public void InitiallizeView() {
        etDate = (EditText) findViewById(R.id.etDate);
        etTime = (EditText) findViewById(R.id.etTime);
        etAmount = (EditText) findViewById(R.id.etRecoveryAmout);
        etMaterial = (EditText) findViewById(R.id.etMaterial);
        etCansIn = (EditText) findViewById(R.id.etCansIn);
        etCansOut = (EditText) findViewById(R.id.etCansOut);
        etRemarks = (EditText) findViewById(R.id.etRemarks);
        spShift = (Spinner) findViewById(R.id.spShift);
        etTruckSheetId = (EditText) findViewById(R.id.etTruckID);
        etcenterId = (EditText) findViewById(R.id.etCenterId);

        etcenterId.setFocusable(false);

        Util.alphabetValidation(etCansIn, Util.ONLY_NUMERIC, this, 4);
        Util.alphabetValidation(etCansOut, Util.ONLY_NUMERIC, this, 4);
        Util.alphabetValidation(etAmount, Util.ONLY_DECIMAL, this, 9);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnSubmit.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        etTruckSheetId.setOnClickListener(this);

        etDate.setFocusable(false);
        etTime.setClickable(true);
        etTruckSheetId.setClickable(true);

    }

    private void setTruckdetails() {
        etMaterial.setText("");
        DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();
        truckCCDatabase = dbHandler.getTruckCCDatabase();
        setList(smartCCUtil.getDateInMillis(0, false), defalutShift);
        etcenterId.setText(new SessionManager(UpdateTruckDetails.this).getCollectionID());
        etMaterial.setText(amcuConfig.getMaterialCode());

    }

    public void initializeDateField() {
        dateInMillis = smartCCUtil.getDateInMillis(0, false);
        Calendar newCalendar = Calendar.getInstance();
        mYear = newCalendar.get(Calendar.YEAR);
        mMonth = newCalendar.get(Calendar.MONTH);
        mDay = newCalendar.get(Calendar.DAY_OF_MONTH);
        etDate.setText(dateFormatter.format(newCalendar.getTime()));
    }

    private void setDateField() {
        etDate.setOnClickListener(UpdateTruckDetails.this);
        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();

                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;

                calendar.set(year, monthOfYear, dayOfMonth);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                etDate.setText(dateFormatter.format(calendar.getTime()));
                dateInMillis = calendar.getTimeInMillis();
                setList(dateInMillis, getShift());
                enableOrDisableShift();
            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    public void initializeTime() {
        Calendar mcurrentTime = Calendar.getInstance();
        mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        mMins = mcurrentTime.get(Calendar.MINUTE);
        etTime.setText(Util.padding(mHour) + ":" + Util.padding(mMins));
        timeInMillis = mcurrentTime.getTimeInMillis();

    }

    private void setTimeField() {
        etTime.setOnClickListener(UpdateTruckDetails.this);

        Calendar mcurrentTime = Calendar.getInstance();
        mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        mMins = mcurrentTime.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar mcurrentTime = Calendar.getInstance();
                mcurrentTime.set(mYear, mMonth, mDay);
                mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                mMins = mcurrentTime.get(Calendar.MINUTE);
                mcurrentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mcurrentTime.set(Calendar.MINUTE, minute);
                timeInMillis = mcurrentTime.getTimeInMillis();
                etTime.setText(Util.padding(hourOfDay) + ":" + Util.padding(minute));
            }
        }, mHour, mMins, true);
    }

    public TruckDetailsEntity getTruckDetails() {
        int cansIn = Integer.parseInt(etCansIn.getText().toString().trim());
        int cansOut = Integer.parseInt(etCansOut.getText().toString().trim());
        String centerId = new SessionManager(UpdateTruckDetails.this).getCollectionID();

        long date = dateInMillis;
        long securityTime = timeInMillis;
        String material = etMaterial.getText().toString();
        String amount = new ValidationHelper().getDoubleFromString(new DecimalFormat("##.00")
                , etAmount.getText().toString().trim());
        double recoveryAmount = Double.parseDouble(amount);
        int shift = getShift();
        truckSheetId = etTruckSheetId.getText().toString();

        int smsSentStatus = CollectionConstants.UNSENT;
        String postDate = SmartCCUtil.getDateInPostFormat();
        String postShift = SmartCCUtil.getShiftInPostFormat(UpdateTruckDetails.this);


        truckDetailsEntity = new TruckDetailsEntity(0, centerId, date
                , shift, truckSheetId, securityTime
                , recoveryAmount, material, cansIn, cansOut, amcuConfig.getDeviceID(), 0
                , etRemarks.getText().toString().trim(), smsSentStatus, postDate, postShift);

        return truckDetailsEntity;

    }

    @Override
    public void onClick(View v) {

        if (v == btnCancel) {
            finish();
        } else if (v == etTruckSheetId) {

            lpw.show();
        } else if (v == etDate) {
            datePickerDialog.show();
        } else if (v == etTime) {
            timePickerDialog.show();
        } else if (v == spShift) {
            setList(dateInMillis, getShift());
        } else if (v == btnSubmit) {
            if (doValidation()) {
                TruckDetailsEntity truckDetailsEnt = getTruckDetails();
                try {
                    truckCCDatabase.updateTruckDetails(truckDetailsEnt, TruckCCDatabase.SERVER_UNSENT_STATUS, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startService(new Intent(this, PostTruckRecords.class));
                finish();
            }
        }

    }

    public boolean doValidation() {
        ValidationHelper validationHelper = new ValidationHelper();
        int cansIn = validationHelper.getIntegerFromString(etCansIn.getText().toString().trim());
        int cansOut = validationHelper.getIntegerFromString(etCansOut.getText().toString().trim());
        String amount = validationHelper.getDoubleFromString(new DecimalFormat("##.00"), etAmount.getText().toString().trim());

        if (etTruckSheetId.getText().toString().trim().length() <= 0) {
            Util.displayErrorToast("Enter valid truck sheet Id!", UpdateTruckDetails.this);
            return false;
        }
        if (dateInMillis > smartCCUtil.getDateInMillis(0, false)) {
            Util.displayErrorToast("Date should be less than today date!", UpdateTruckDetails.this);
            return false;
        }
        if (dateInMillis < smartCCUtil.getDateInMillis(-1, false)) {
            Util.displayErrorToast("Please check the date!", UpdateTruckDetails.this);
            return false;
        } else if (cansIn < 1 || cansIn > 500) {
            Util.displayErrorToast("Please enter valid Cans In!", UpdateTruckDetails.this);
            return false;
        } else if (cansOut < 0 || cansOut > cansIn) {
            Util.displayErrorToast("Please enter valid Cans Out!", UpdateTruckDetails.this);
            return false;
        } else if (etTime.getText().toString().trim().length() < 1 || !checkTimeValidation()) {
            Util.displayErrorToast("Please enter valid time!", UpdateTruckDetails.this);
            return false;
        } else if (etMaterial.getText().toString().trim().length() < 1) {
            Util.displayErrorToast("Please enter valid material id!", UpdateTruckDetails.this);
            return false;
        } else if (Double.valueOf(amount) < 0) {
            etAmount.setText("0.00");
        }

        return true;

    }

    public int getShift() {
        if (spShift.getSelectedItem().toString().equalsIgnoreCase("Morning")) {
            return 0;
        } else {
            return 1;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String item = list.get(position);
        etTruckSheetId.setText(item);
        lpw.dismiss();
    }

    public boolean checkTimeValidation() {
        if (etTime.getText().toString().length() > 0) {
            if (getShift() == 0) {
                if (Integer.parseInt(etTime.getText().toString().replace(":", "")) >

                        Integer.parseInt(amcuConfig.getEveningStart()
                                .replace(":", ""))) {
                    return false;
                }
            } else if (Integer.parseInt(etTime.getText().toString().replace(":", "")) <

                    Integer.parseInt(amcuConfig.getEveningStart()
                            .replace(":", ""))) {
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    public void initializeShift() {
        String shift = Util.getCurrentShift();
        if (shift == "M") {
            spShift.setSelection(0);
            defalutShift = 0;
        } else {
            spShift.setSelection(1);
            defalutShift = 1;
        }
        enableOrDisableShift();
    }

    public void enableOrDisableShift() {
        if (dateInMillis == new SmartCCUtil(UpdateTruckDetails.this).getDateInMillis(0, false)) {
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
}
