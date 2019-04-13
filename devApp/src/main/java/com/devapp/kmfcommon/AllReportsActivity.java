package com.devapp.kmfcommon;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.macollection.UpdatePastRecord;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.ArrayList;
import java.util.Locale;

public class AllReportsActivity extends Activity {


    public static final String dateToShiftSeparater = " ";
    EditText etCollectionId;
    Spinner spShift, spSelectDateAndShift;
    AmcuConfig amcuConfig;
    ArrayList<String> allPossibleDateShift = new ArrayList<>();

    String selectedDate;
    String selectedShift;
    String selectedId;
    ListView lvList;

    private Cursor mCursor = null;


    private Handler editRecordHandler = new Handler();


    public void getSpinnerlistItem() {

        int MAX_SHIFT = amcuConfig.getNumberShiftCanBeEdited();
        SmartCCUtil smartCCUtil = new SmartCCUtil(AllReportsActivity.this);

        String currentDate = smartCCUtil.getReportFormatDate(0);
        String currentShift = Util.getCurrentShift();

        switch (MAX_SHIFT) {

            case 0: {
                break;
            }
            case 1: {
                allPossibleDateShift.add(currentDate + dateToShiftSeparater + currentShift);
                break;
            }
            case 2: {
                if (currentShift.equalsIgnoreCase(AppConstants.Shift.MORNING)) {

                    allPossibleDateShift.add(currentDate + dateToShiftSeparater + currentShift);
                    currentDate = smartCCUtil.getReportFormatDate(-1);
                    allPossibleDateShift.add(currentDate + dateToShiftSeparater + AppConstants.Shift.EVENING);
                } else {
                    allPossibleDateShift.add(currentDate + dateToShiftSeparater + currentShift);
                    allPossibleDateShift.add(currentDate + dateToShiftSeparater + AppConstants.Shift.MORNING);
                }
                break;
            }

            default: {

                if (currentShift.equalsIgnoreCase(AppConstants.Shift.MORNING)) {

                    int shiftCheck = MAX_SHIFT - 1;
                    int reminderCheck = shiftCheck % 2;
                    shiftCheck = shiftCheck / 2;
                    int totalDayToCheck = 0;

                    allPossibleDateShift.add(currentDate + dateToShiftSeparater + currentShift);

                    for (int i = 1; i <= shiftCheck; i++) {
                        String changeDate = smartCCUtil.getReportFormatDate(-i);
                        allPossibleDateShift.add(changeDate + dateToShiftSeparater + AppConstants.Shift.EVENING);
                        allPossibleDateShift.add(changeDate + dateToShiftSeparater + AppConstants.Shift.MORNING);
                        totalDayToCheck = totalDayToCheck + 1;
                    }

                    if (reminderCheck == 1) {
                        String changeDate = smartCCUtil.getReportFormatDate(-(totalDayToCheck + 1));
                        allPossibleDateShift.add(changeDate + dateToShiftSeparater + AppConstants.Shift.EVENING);
                    }

                } else {

                    int shiftCheck = MAX_SHIFT - 2;
                    int reminderCheck = shiftCheck % 2;
                    shiftCheck = shiftCheck / 2;
                    int totalDayToCheck = 0;


                    allPossibleDateShift.add(currentDate + dateToShiftSeparater + currentShift);
                    allPossibleDateShift.add(currentDate + dateToShiftSeparater + AppConstants.Shift.MORNING);

                    for (int i = 1; i <= shiftCheck; i++) {
                        String changeDate = smartCCUtil.getReportFormatDate(-i);
                        allPossibleDateShift.add(changeDate + dateToShiftSeparater + AppConstants.Shift.EVENING);
                        allPossibleDateShift.add(changeDate + dateToShiftSeparater + AppConstants.Shift.MORNING);
                        totalDayToCheck = totalDayToCheck + 1;
                    }

                    if (reminderCheck == 1) {
                        String changeDate = smartCCUtil.getReportFormatDate(-(totalDayToCheck + 1));
                        allPossibleDateShift.add(changeDate + dateToShiftSeparater + AppConstants.Shift.EVENING);
                    }

                }

                break;
            }
        }


    }


    public void setSpinnerList() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, allPossibleDateShift);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSelectDateAndShift.setAdapter(dataAdapter);
    }

    private Runnable editRecordRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_records);

        etCollectionId = (EditText) findViewById(R.id.etCollectionId);
        spSelectDateAndShift = (Spinner) findViewById(R.id.spSelectDate);
        spShift = (Spinner) findViewById(R.id.spSelectShift);
        lvList = (ListView) findViewById(R.id.lvReports);

        //choice = getIntent().getStringExtra("Choice");
        //Toast.makeText(AllReportsActivity.this, "Choice"+choice, Toast.LENGTH_SHORT).show();

        amcuConfig = AmcuConfig.getInstance();
        getSpinnerlistItem();
        setSpinnerList();

        Util.alphabetValidation(etCollectionId, Util.ONLY_ALPHANUMERIC, this, 0);
        spSelectDateAndShift.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String dateAndShift = spSelectDateAndShift.getItemAtPosition(position).toString();
                String[] dsArray = dateAndShift.split(dateToShiftSeparater);

                selectedDate = dsArray[0];
                selectedShift = dsArray[1];


                setAdapter();


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        etCollectionId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (etCollectionId.getText().length() <= 0) {
                    mCursor = DatabaseHandler.getDatabaseInstance().getEditableShiftRecord(null, selectedDate, selectedShift);
                    AllReportsRecordAdapter reportAdapter = new AllReportsRecordAdapter(AllReportsActivity.this, mCursor, 0);
                    lvList.setAdapter(reportAdapter);
                } else {
                    selectedId = etCollectionId.getText().toString();
                    setAdapter();
                }


            }
        });
        etCollectionId.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                switch (i) {
                    case KeyEvent.KEYCODE_NUMPAD_ENTER:
                    case KeyEvent.KEYCODE_ENTER: {
                        return true;
                    }
                    case KeyEvent.KEYCODE_DEL: {
                        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                            if (!etCollectionId.getText().toString().equals("")) {
                                return false;
                            } else {
                                finish();
                                return true;
                            }
                        }
                    }
                }
                return false;
            }
        });
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mCursor.moveToPosition(position);
                Intent intent = new Intent(AllReportsActivity.this, UpdatePastRecord.class);
                intent.putExtra("ID", mCursor.getString(0));
                startActivity(intent);
            }
        });

        int size = allPossibleDateShift.size();
        if (size > 0) {
            String first = allPossibleDateShift.get(0);
            String[] startArray = first.split(dateToShiftSeparater);
            selectedDate = startArray[0];
            selectedShift = startArray[1];
        }
    }

    public void setAdapter() {
        selectedId = etCollectionId.getText().toString().trim();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (etCollectionId.getText().length() <= 0) {
                    mCursor = DatabaseHandler.getDatabaseInstance().getEditableShiftRecord(null, selectedDate, selectedShift);

                } else {
                    mCursor = DatabaseHandler.getDatabaseInstance().getEditableShiftRecord(selectedId.toUpperCase(Locale.ENGLISH), selectedDate, selectedShift);

                }
                editRecordHandler.post(editRecordRunnable);
            }
        }).start();


        editRecordRunnable = new Runnable() {
            @Override
            public void run() {
                AllReportsRecordAdapter reportAdapter =
                        new AllReportsRecordAdapter(AllReportsActivity.this, mCursor, 0);
                lvList.setAdapter(reportAdapter);
            }
        };


    }

    @Override
    protected void onResume() {
        super.onResume();
            setAdapter();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

        editRecordHandler.removeCallbacks(editRecordRunnable);
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F9:
                Util.Logout(AllReportsActivity.this);
                finish();
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(AllReportsActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(AllReportsActivity.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(AllReportsActivity.this, null);
                return true;
            case KeyEvent.KEYCODE_DEL:
                if (etCollectionId.hasFocus()) {
                    return false;
                }
                finish();
                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }
}
