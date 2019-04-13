package com.devapp.smartcc.report;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.devapp.devmain.dao.CollectionCenterDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.tabs.DatePickerFragment;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.adapters.AutoTextAdapter;
import com.devapp.smartcc.adapters.MultiAutoTextAdapter;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * To select the Center related parametrs to show the report
 * Created by u_pendra on 28/3/17.
 */

public class FilterMccReportActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, DatePickerDialog.OnDateSetListener {

    SmartCCUtil smartCCUtil;
    AutoTextAdapter autoTextAdapter;
    MultiAutoTextAdapter multiAutoTextAdapter;
    int FROMDATE = 0;
    int TODATE = 1;
    int SETDATE = 0;
    private com.eevoskos.robotoviews.widget.RobotoTextView tvheader;
    private RelativeLayout header;
    private MultiAutoCompleteTextView tvSelectRoute, tvSelectMCC;
    private LinearLayout linearLayout;
    private Button btCancel, btnNext;
    private AutoCompleteTextView tvStartDate, tvEndDate, tvSelectMilkType, tvSelectShift;
    private ArrayList<String> allCenterList;
    private ArrayList<String> allRouteList;
    private int thresHold = 0;
    private ArrayList<String> allCattleType;
    private ArrayList<String> allShift;
    private DatabaseHandler databaseHandler;
    private int adapterLayoutId;
    private DatabaseEntity databaseEntity;
    private RadioGroup rgStatus;
    private RadioButton rbComplete;
    private RadioButton rbInComplete;
    private SessionManager sessionManager;


    // Item Click events
    private long startMilliDate, endMilliDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filter_report);
        DatePickerFragment.mActivity = 0;

        adapterLayoutId = getResources().getIdentifier("auto_text_item", "res", getPackageName());
        sessionManager = new SessionManager(FilterMccReportActivity.this);
        initializeView();
        onTextChange();

    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseEntity = new DatabaseEntity(FilterMccReportActivity.this);
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        smartCCUtil = new SmartCCUtil(FilterMccReportActivity.this);
        smartCCUtil.toSetDrawableOnEdit(tvSelectRoute, ReportHintConstant.ROUTE);
        smartCCUtil.toSetDrawableOnEdit(tvSelectMCC, ReportHintConstant.MCC);
        smartCCUtil.toSetDrawableOnEdit(tvEndDate, ReportHintConstant.DATE_TO);
        smartCCUtil.toSetDrawableOnEdit(tvStartDate, ReportHintConstant.DATE_FROM);
        smartCCUtil.toSetDrawableOnEdit(tvSelectMilkType, ReportHintConstant.CATTLE_TYPE);
        smartCCUtil.toSetDrawableOnEdit(tvSelectShift, ReportHintConstant.SHIFT);

        setOnKeyClickListerner();
        setDateFromIntent();
        tvSelectRoute.requestFocus();
        tvSelectRoute.setSelection(tvSelectRoute.getText().toString().length());
    }

    private void initializeView() {

        tvheader = (com.eevoskos.robotoviews.widget.RobotoTextView) findViewById(R.id.tvheader);
        header = (RelativeLayout) findViewById(R.id.header);
        tvSelectRoute = (MultiAutoCompleteTextView) findViewById(R.id.tvSelectRoute);
        tvSelectMCC = (MultiAutoCompleteTextView) findViewById(R.id.tvSelectMCC);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        btCancel = (Button) findViewById(R.id.btCancel);
        btnNext = (Button) findViewById(R.id.btnNext);
        tvStartDate = (AutoCompleteTextView) findViewById(R.id.tvStartDate);
        tvEndDate = (AutoCompleteTextView) findViewById(R.id.tvEndDate);
        tvSelectMilkType = (AutoCompleteTextView) findViewById(R.id.tvSelectMilkType);
        tvSelectShift = (AutoCompleteTextView) findViewById(R.id.tvSelectShift);

        rgStatus = (RadioGroup) findViewById(R.id.rgStatus);
        rbComplete = (RadioButton) findViewById(R.id.rbComplete);
        rbInComplete = (RadioButton) findViewById(R.id.rbIncomplete);

        rgStatus.setVisibility(View.VISIBLE);
        rgStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.rbComplete) {
                    sessionManager.setRecordStatusComplete(true);
                } else if (checkedId == R.id.rbIncomplete) {
                    sessionManager.setRecordStatusComplete(false);

                }
            }
        });
        if (sessionManager.getMCCStatus()) {

            if (sessionManager.getRecordStatusComplete()) {
                rbComplete.setChecked(true);
            } else {
                rbInComplete.setChecked(true);
            }
        }

        btCancel.setOnClickListener(this);
        btnNext.setOnClickListener(this);

//        tvStartDate.setOnItemSelectedListener(this);
//        tvEndDate.setOnItemSelectedListener(this);
        tvSelectMilkType.setOnItemSelectedListener(this);
        tvSelectShift.setOnItemSelectedListener(this);

        tvStartDate.setOnItemClickListener(this);
        tvEndDate.setOnItemClickListener(this);
        tvSelectMilkType.setOnItemClickListener(this);
        tvSelectShift.setOnItemClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);

        tvStartDate.setClickable(true);
        tvEndDate.setClickable(true);
    }

    public void onClick(View v) {
        if (v == btCancel) {
            finish();
        } else if (v == btnNext) {
            if (checkForValidation()) {
                startActivity(getIntentFromParams());
                finish();
            }

        } else if (v == tvStartDate) {
            SETDATE = FROMDATE;
            showFragment();
        } else if (v == tvEndDate) {
            SETDATE = TODATE;
            showFragment();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (view == tvStartDate) {
        } else if (view == tvEndDate) {
        } else if (view == tvSelectMilkType) {
        } else if (view == tvSelectShift) {
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
        if (view == tvStartDate) {
        } else if (view == tvEndDate) {
        } else if (view == tvSelectMilkType) {
        } else if (view == tvSelectShift) {
        }
    }

    private Intent getIntentFromParams() {
        Intent intent = new Intent(FilterMccReportActivity.this, MccReportActivity.class);
        intent.putExtra(ReportHintConstant.CATTLE_TYPE, tvSelectMilkType.getText().toString().toUpperCase());
        intent.putExtra(ReportHintConstant.DATE_FROM, tvStartDate.getText().toString().toUpperCase());
        intent.putExtra(ReportHintConstant.DATE_TO, tvEndDate.getText().toString().toUpperCase());
        intent.putExtra(ReportHintConstant.MCC, tvSelectMCC.getText().toString().toUpperCase());
        intent.putExtra(ReportHintConstant.SHIFT, tvSelectShift.getText().toString().toUpperCase());
        intent.putExtra(ReportHintConstant.ROUTE, tvSelectRoute.getText().toString().toUpperCase());
        intent.putExtra(ReportHintConstant.IS_COMPLETE,
                (rgStatus.getCheckedRadioButtonId() == R.id.rbComplete) ? Util.RECORD_STATUS_COMPLETE : Util.RECORD_STATUS_INCOMPLETE);

        return intent;
    }

    public void setAdapterForEditText() {

        CollectionCenterDao collectionCenterDao = (CollectionCenterDao) DaoFactory.getDao(CollectionConstants.COLLECTION_CENTER);
        allRouteList = collectionCenterDao.getAllRoutes();
//       tvSelectRoute.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        multiAutoTextAdapter = new MultiAutoTextAdapter(FilterMccReportActivity.this,
                getApplicationContext(), adapterLayoutId, allRouteList);
        tvSelectRoute.setAdapter(multiAutoTextAdapter);

//        tvSelectRoute.setAdapter(
//                new ArrayAdapter<String>(this,
//                        android.R.layout.simple_dropdown_item_1line, allRouteList));

        tvSelectRoute.setTokenizer(
                new MultiAutoCompleteTextView.CommaTokenizer());


        if (allRouteList.size() > 0) {
            tvSelectRoute.setText(allRouteList.get(0));
        }
        tvSelectRoute.setSelection(tvSelectRoute.getText().toString().length());
        tvSelectRoute.setThreshold(thresHold);


        allCenterList = collectionCenterDao.findActiveCenterByRoute(tvSelectRoute.getText().toString());
        multiAutoTextAdapter = new MultiAutoTextAdapter(FilterMccReportActivity.this,
                getApplicationContext(), adapterLayoutId, allCenterList);
        tvSelectMCC.setAdapter(multiAutoTextAdapter);

        tvSelectMCC.setTokenizer(
                new MultiAutoCompleteTextView.CommaTokenizer());

        tvSelectMCC.setThreshold(thresHold);
        //To set cattle type
        autoTextAdapter = new AutoTextAdapter(FilterMccReportActivity.this, adapterLayoutId, allCattleType);
        tvSelectMilkType.setThreshold(thresHold);
        tvSelectMilkType.setAdapter(autoTextAdapter);
        tvSelectMilkType.setText(allCattleType.get(0));

        //To select the session
        autoTextAdapter = new AutoTextAdapter(FilterMccReportActivity.this, adapterLayoutId, allShift);
        tvSelectShift.setThreshold(thresHold);
        tvSelectShift.setAdapter(autoTextAdapter);
        tvSelectShift.setText(allShift.get(0));
    }

    public void setShiftAndMilkType() {
        allCattleType = new ArrayList<>();
        allShift = new ArrayList<>();

        allCattleType.add("COW");
        allCattleType.add("BUFFALO");
        allCattleType.add(SmartCCConstants.SELECT_ALL);

        allShift.add("M");
        allShift.add("E");
        allShift.add(SmartCCConstants.SELECT_ALL);
    }

    public void showFragment() {
        DatePickerFragment dpf = new DatePickerFragment();
        dpf.show(getFragmentManager(), "FilterDateFragment");
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

        if (SETDATE == FROMDATE) {
            startMilliDate = smartCCUtil.getDateInMilli(i2, i1, i);
            tvStartDate.setText(Util.padding(i2) + "-" + Util.padding(i1 + 1) + "-" + i);

        } else if (SETDATE == TODATE) {
            endMilliDate = smartCCUtil.getDateInMilli(i2, i1, i);
            tvEndDate.setText(Util.padding(i2) + "-" + Util.padding(i1 + 1) + "-" + i);
        }
    }


    public void setCenterField() {
        String[] routeList = tvSelectRoute.getText().toString().trim().split(",");
        String query = databaseEntity.createQueryToGetCenterFromRoutes(routeList);
        //   Util.displayErrorToast(query,FilterCCReportActivity.this);
        allCenterList = databaseHandler.getCenterListFromQuery(query);
        multiAutoTextAdapter = new MultiAutoTextAdapter(FilterMccReportActivity.this,
                getApplicationContext(), adapterLayoutId, allCenterList);
        tvSelectMCC.setAdapter(multiAutoTextAdapter);
        tvSelectMCC.setTokenizer(
                new MultiAutoCompleteTextView.CommaTokenizer());
        tvSelectMCC.setThreshold(thresHold);

    }

    public void onTextChange() {

        tvSelectRoute.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //  Util.displayErrorToast("On Item selected",FilterCCReportActivity.this);
                setCenterField();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        tvSelectRoute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                setCenterField();

            }
        });
    }

    public boolean checkForValidation() {
        boolean isValid = true;
        long lnStart = 0;
        long lnEndDate = 0;

        setMultiEditText(tvSelectRoute);
        setMultiEditText(tvSelectMCC);

        if (!tvSelectRoute.getText().toString().trim().equalsIgnoreCase("")
                && !tvSelectRoute.getText().toString().trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            isValid = databaseEntity.checkForIfValueExist(DatabaseHandler.TABLE_CHILLING_CENTER,
                    DatabaseHandler.KEY_CHILLING_ROUTE, tvSelectRoute.getText().toString());
        }

        if (isValid == false) {
            Util.displayErrorToast("Please check the Route entries!", FilterMccReportActivity.this);
            return isValid;
        }


        if (!tvSelectMCC.getText().toString().trim().equalsIgnoreCase("")
                && !tvSelectMCC.getText().toString().trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            isValid = databaseEntity.checkForIfValueExist(DatabaseHandler.TABLE_CHILLING_CENTER,
                    DatabaseHandler.KEY_CHILLING_CENTER_ID, tvSelectMCC.getText().toString());
        }
        if (isValid == false) {
            Util.displayErrorToast("Please check the MCC entries!", FilterMccReportActivity.this);
            return isValid;
        }

        if (!tvSelectShift.getText().toString().trim().equalsIgnoreCase("") &&
                !allShift.contains(tvSelectShift.getText().toString().toUpperCase().trim())) {
            setFocus(tvSelectShift);
            isValid = false;
            Util.displayErrorToast("Please select the valid shift!", FilterMccReportActivity.this);
            return isValid;
        }

        tvSelectShift.setText(tvSelectShift.getText().toString().toUpperCase().trim());

        if (!tvSelectMilkType.getText().toString().trim().equalsIgnoreCase("") &&
                !allCattleType.contains(
                        tvSelectMilkType.getText().toString().toUpperCase().trim())) {
            setFocus(tvSelectMilkType);
            isValid = false;
            Util.displayErrorToast("Please select the valid Cattle type!", FilterMccReportActivity.this);
            return isValid;
        }
        tvSelectMilkType.setText(tvSelectMilkType.getText().toString().toUpperCase().trim());

        if (tvStartDate.getText().toString().length() > 0 &&
                tvStartDate.getText().toString().trim().length() <= 10
                &&
                smartCCUtil.dateValidator(tvStartDate.getText().toString().trim(),
                        "dd-MM-yyyy")) {
            lnStart = Util.getDateInLongFormat(tvStartDate.getText().toString().trim());
        } else if (tvStartDate.getText().toString().trim().length() > 0) {
            setFocus(tvStartDate);
            isValid = false;
            Util.displayErrorToast("Please select the valid date!", FilterMccReportActivity.this);
            return isValid;
        }

        if (tvEndDate.getText().toString().trim().length() > 0 &&
                tvEndDate.getText().toString().trim().length() <= 10 &&
                smartCCUtil.dateValidator(tvEndDate.getText().toString().trim(),
                        "dd-MM-yyyy")) {

            lnEndDate = Util.getDateInLongFormat(tvEndDate.getText().toString().trim());
        } else if (tvEndDate.getText().toString().trim().length() > 0) {
            setFocus(tvEndDate);
            isValid = false;
            Util.displayErrorToast("Please select the valid date!", FilterMccReportActivity.this);
            return isValid;
        }

        if (lnStart > Util.getDateInLongFormat(smartCCUtil.getReportFormatDate())) {
            Util.displayErrorToast("Please enter the valid date!", FilterMccReportActivity.this);
            isValid = false;
        }

        if (lnEndDate < lnStart) {
            Util.displayErrorToast("Please enter the valid date!", FilterMccReportActivity.this);
            isValid = false;
        }

        if (lnEndDate > Util.getDateInLongFormat(smartCCUtil.getReportFormatDate())) {
            Util.displayErrorToast("Please enter the valid date!", FilterMccReportActivity.this);
            isValid = false;
        }

        return isValid;
    }


    public void setMultiEditText(MultiAutoCompleteTextView textView) {
        String[] valueList = textView.getText().toString().trim().split(",");
        TreeSet<String> treeSet = new TreeSet<>();
        for (int i = 0; i < valueList.length; i++) {
            treeSet.add(valueList[i]);
        }

        String data = "";
        if (treeSet.size() > 0) {
            Iterator<String> iterator = treeSet.iterator();

            int count = 0;
            while (iterator.hasNext()) {
                if (count == 0) {
                    data = iterator.next().toString().trim();
                    count = count + 1;
                } else {
                    data = data + "," + iterator.next().toString().trim();
                }
            }
        }
        textView.setText(data.toUpperCase());
    }


    public void setOnKeyClickListerner() {
        tvStartDate.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    if (keyEvent.getAction() == keyEvent.ACTION_UP) {
                        tvStartDate.setSelection(tvStartDate.getText().toString().trim().length());
                        SETDATE = FROMDATE;
                        showFragment();
                    }
                }

                return false;
            }
        });

        tvEndDate.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    if (keyEvent.getAction() == keyEvent.ACTION_UP) {
                        tvEndDate.setSelection(tvEndDate.getText().toString().trim().length());
                        SETDATE = TODATE;
                        showFragment();
                    }
                }

                return false;
            }
        });
    }

    public void setFocus(AutoCompleteTextView textView) {
        textView.requestFocus();
        textView.setSelection(textView.getText().toString().length());
    }


    public void setDateFromIntent() {

        try {
            setShiftAndMilkType();
            setAdapterForEditText();
            tvStartDate.setText(Util.getDateDDMMYY(smartCCUtil.getReportDate(0), 11));
            tvEndDate.setText(Util.getDateDDMMYY(smartCCUtil.getReportDate(0), 11));
            tvSelectRoute.setText(getIntent().getStringExtra(ReportHintConstant.ROUTE));
            tvSelectMCC.setText(getIntent().getStringExtra(ReportHintConstant.MCC));
            tvStartDate.setText(getIntent().getStringExtra(ReportHintConstant.DATE_FROM));
            tvEndDate.setText(getIntent().getStringExtra(ReportHintConstant.DATE_TO));
            tvSelectMilkType.setText(getIntent().getStringExtra(ReportHintConstant.CATTLE_TYPE));
            tvSelectShift.setText(getIntent().getStringExtra(ReportHintConstant.SHIFT));
            if (getIntent().hasExtra(ReportHintConstant.IS_COMPLETE)) {
                if (getIntent().getStringExtra(ReportHintConstant.IS_COMPLETE).equalsIgnoreCase(Util.RECORD_STATUS_COMPLETE)) {
                    rbComplete.setChecked(true);
                    rbInComplete.setChecked(false);
                } else {
                    rbComplete.setChecked(false);
                    rbInComplete.setChecked(true);
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        if (tvSelectMCC.getText().toString().trim().length() < 1 &&
                tvStartDate.getText().toString().trim().length() < 1 &&
                tvSelectRoute.getText().toString().trim().length() < 1) {
            setShiftAndMilkType();
            setAdapterForEditText();
            tvStartDate.setText(Util.getDateDDMMYY(smartCCUtil.getReportDate(0), 11));
            tvEndDate.setText(Util.getDateDDMMYY(smartCCUtil.getReportDate(0), 11));
        }

    }


}
