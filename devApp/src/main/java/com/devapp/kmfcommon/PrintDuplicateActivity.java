package com.devapp.kmfcommon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class PrintDuplicateActivity extends Activity {

    final ArrayList<HashMap<String, Object>> m_data = new ArrayList<HashMap<String, Object>>();
    Context context;
    String coming_from_screen;
    Cursor cursor;
    String cid;
    SessionManager session;
    String shift, s_day, s_month, s_year, s_date;
    Button btnfindSelected;
    PrinterManager printManager;
    UsbManager mUsbManager;

    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print_duplicate);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        session = new SessionManager(this);
        printManager = new PrinterManager(PrintDuplicateActivity.this);
        context = this;
        coming_from_screen = getIntent().getStringExtra("COMING_FROM");
        cid = session.getFarmerID();

        btnfindSelected = (Button) findViewById(R.id.findSelected);
        btnfindSelected.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    btnfindSelected.setBackgroundColor(getResources().getColor(R.color.focused));

                } else {
                    btnfindSelected.setBackgroundColor(getResources().getColor(R.color.blue));
                }
            }
        });
        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseInstance();

        if (null != coming_from_screen && coming_from_screen.equalsIgnoreCase("FarmerScannerActivity")) {
            cursor = databaseHandler.getAllActiveCenterId();
            btnfindSelected.setText("Tap After Selecting Center Id");
            setListData();

        }
        if (null != coming_from_screen &&
                coming_from_screen.equalsIgnoreCase("PrintDuplicateActivity")) {
            cid = getIntent().getStringExtra("CID");
            shift = getIntent().getStringExtra("S_SHIFT");
            s_date = getIntent().getStringExtra("S_DATE");

            //  Toast.makeText(getApplicationContext(), "CID::" + cid + " SHIFT:: " + shift + "DATE:: " + s_date, Toast.LENGTH_LONG).show();
            Drawable img = getApplicationContext().getResources().getDrawable(R.drawable.print);
            img.setBounds(0, 0, 30, 30);
            btnfindSelected.setCompoundDrawables(null, null, img, null);
            btnfindSelected.setText("Print");
            cursor = databaseHandler.getChillingCenterRecords(cid, shift, s_date);
            setListData();

        }


    }

    public void setListData() {
        HashMap<String, Object> map1;
        int count = cursor.getCount();
        if (count == 0) {
            Toast.makeText(getApplicationContext(),
                    "No Data Found!!",
                    Toast.LENGTH_LONG).show();
            finish();
        }
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                do {
                    map1 = new HashMap<String, Object>();
                    if (null != coming_from_screen &&
                            coming_from_screen.equalsIgnoreCase(
                                    "FarmerScannerActivity")) {
                        map1.put("CID",
                                cursor.getString(
                                        0));
                    } else if (null != coming_from_screen &&
                            coming_from_screen.equalsIgnoreCase("PrintDuplicateActivity")) {
                        map1.put("CID", cursor.getString(cursor.getColumnIndex(
                                FarmerCollectionTable.KEY_REPORT_FARMERID
                        )) + " (Quantity:: " + cursor.getString(
                                cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_QUANTITY))
                                + " " + Util.getTheUnit(getApplicationContext(), 0) + ")");
                    }
                    m_data.add(map1);
                    map1 = null;
                } while (cursor.moveToNext());
            }
        }


        for (HashMap<String, Object> m : m_data)
            m.put("checked", false);


        final ListView lv = (ListView) findViewById(R.id.listView1);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);


        final SimpleAdapter adapter = new SimpleAdapter(this,
                m_data,
                R.layout.row_print_duplicate,
                new String[]{"CID", "checked"},
                new int[]{R.id.collectionID, R.id.rb_Choice});

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {
                RadioButton rb = (RadioButton) v.findViewById(R.id.rb_Choice);
                if (!rb.isChecked()) //OFF->ON
                {
                    for (HashMap<String, Object> m : m_data) //clean previous selected
                        m.put("checked", false);

                    m_data.get(arg2).put("checked", true);
                    adapter.notifyDataSetChanged();
                }
            }
        });

        //show result
        ((Button) findViewById(R.id.findSelected)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int r = -1;
                for (int i = 0; i < m_data.size(); i++) //clean previous selected
                {
                    HashMap<String, Object> m = m_data.get(i);
                    Boolean x = (Boolean) m.get("checked");

                    if (x == true) {
                        r = i;
                        cid = (String) m.get("CID");
                        break; //break, since it's a single choice list
                    }
                }
                if (r == -1) {
                    new AlertDialog.Builder(context).setMessage("No Data Selected. \nPlease select and then tap.").show();
                } else {
                    // new AlertDialog.Builder(context).setMessage("you selected:" + r).show();
                    if (null != coming_from_screen && coming_from_screen.equalsIgnoreCase("FarmerScannerActivity")) {
                        gotoShiftAndDateAlert();
                    }
                    if (null != coming_from_screen && coming_from_screen.equalsIgnoreCase("PrintDuplicateActivity")) {
                        // new AlertDialog.Builder(context).setMessage("Print the data:" + cid).show();
                        if (null != cursor) {
                            cursor.moveToPosition(r);
                            prepareDataAndPrint(cursor, context);
                        }
                    }
                }
            }
        });
    }

    public void gotoShiftAndDateAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(PrintDuplicateActivity.this);

        LayoutInflater inflater = PrintDuplicateActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.shift_dialogbox, null);


        RadioGroup radioMilkStateGroup = (RadioGroup) dialogView.findViewById(R.id.radio_shift);
        shift = AppConstants.Shift.MORNING;
        RadioButton radioMorning = (RadioButton) dialogView.findViewById(R.id.radio_morning);
        RadioButton radioEvening = (RadioButton) dialogView.findViewById(R.id.radio_evening);

        if (shift.equalsIgnoreCase(AppConstants.Shift.MORNING)) {
            radioMorning.setChecked(true);
        } else {
            radioEvening.setChecked(true);
        }
        radioMilkStateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_morning:
                        shift = AppConstants.Shift.MORNING;
                        break;
                    case R.id.radio_evening:
                        shift = AppConstants.Shift.EVENING;
                        break;

                }
            }
        });

        Spinner spinnerDate = (Spinner) dialogView.findViewById(R.id.dateDD);
        Spinner spinnerMonth = (Spinner) dialogView.findViewById(R.id.monthMON);
        Spinner spinnerYear = (Spinner) dialogView.findViewById(R.id.yearYYYY);

        mCalendar = Calendar.getInstance();
        int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        int mMonth = mCalendar.get(Calendar.MONTH);
        int mYear = mCalendar.get(Calendar.YEAR);

        List<String> dayData = new ArrayList<String>();

        for (int day = 1; day <= 31; day++) {
            if (day < 10) {
                dayData.add("0" + day);
            } else {
                dayData.add(String.valueOf(day));
            }
        }

        List<String> monthData = new ArrayList<String>();
        monthData.add("Jan");
        monthData.add("Feb");
        monthData.add("Mar");
        monthData.add("Apr");
        monthData.add("May");
        monthData.add("Jun");
        monthData.add("Jul");
        monthData.add("Aug");
        monthData.add("Sep");
        monthData.add("Oct");
        monthData.add("Nov");
        monthData.add("Dec");

        List<Integer> yearData = new ArrayList<Integer>();
        for (int i = 2016; i < 2036; i++) {
            yearData.add(Integer.valueOf(i));
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dayData);
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, monthData);
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_spinner_item, yearData);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner

        spinnerDate.setAdapter(dataAdapter);
        spinnerMonth.setAdapter(monthAdapter);
        spinnerYear.setAdapter(yearAdapter);

        spinnerDate.setSelection(mDay - 1);
        spinnerMonth.setSelection(mMonth);
        spinnerYear.setSelection(yearData.indexOf(mYear));


        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                s_year = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // Spinner click listener
        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                s_day = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                s_month = item;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        builder.setView(dialogView);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String date = s_day + "-" + s_month + "-" + s_year;
                        s_date = date;
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), PrintDuplicateActivity.class);
                        intent.putExtra("COMING_FROM", "PrintDuplicateActivity");
                        intent.putExtra("CID", cid);
                        intent.putExtra("S_DATE", SmartCCUtil.changeDateFormat(
                                date, "dd-MMM-yyyy", "yyyy-MM-dd"));
                        intent.putExtra("S_SHIFT", shift);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                });


        builder.setInverseBackgroundForced(true);
        builder.setCancelable(false);
        AlertDialog levelDialog = builder.create();

        levelDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        levelDialog.show();

        Window window = levelDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    public void prepareDataAndPrint(Cursor cursor, Context context) {
        ReportEntity repEntity = new ReportEntity();
        repEntity.farmerId = cursor.getString(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_FARMERID));
        repEntity.rate = cursor.getDouble(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_RATE));
        repEntity.farmerName = cursor.getString(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_NAME));
        repEntity.milkType = cursor.getString(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_MILKTYPE));
        repEntity.quantity = cursor.getDouble(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_QUANTITY));
        repEntity.fat = cursor.getDouble(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_FAT));
        repEntity.snf = cursor.getDouble(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_SNF));
        repEntity.amount = cursor.getDouble(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_AMOUNT));
        repEntity.postShift = cursor.getString(cursor.getColumnIndex(FarmerCollectionTable.POST_SHIFT));
        repEntity.time = cursor.getString(cursor.getColumnIndex(FarmerCollectionTable.POST_DATE)) + " "
                + cursor.getString(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_TIME));
        repEntity.collectionType = cursor.getString(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_TYPE));
        repEntity.centerRoute = cursor.getString(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_ROUTE));
        String printData = Util.printReceiptMessage(repEntity, context);
        printReceipt(printData);
    }

    private void printReceipt(String message) {
        printManager.print(message, PrinterManager.printReciept, null, null, null);

    }
}
