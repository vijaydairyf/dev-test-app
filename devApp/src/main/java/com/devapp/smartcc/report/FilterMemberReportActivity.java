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
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.helper.CattleType;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.tabs.DatePickerFragment;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.CursorEndOnFocusChangeListener;
import com.devapp.smartcc.adapters.AutoTextAdapter;
import com.devapp.smartcc.adapters.MultiAutoTextAdapter;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;


/**
 * This activity to select the various parameters like
 * Farmer id's,date from, date to, cattle type ,shift to generate different type of reports
 * Created by u_pendra on 28/3/17.
 */

public class FilterMemberReportActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener, DatePickerDialog.OnDateSetListener, RadioGroup.OnCheckedChangeListener {

    CheckedTextView checkAllowHeader;
    SmartCCUtil smartCCUtil;
    AutoTextAdapter autoTextAdapter;
    MultiAutoTextAdapter multiAutoTextAdapter;
    RelativeLayout rlCheckBox;
    CheckBox checkBox;
    boolean checkEnable = true;
    RadioGroup radioGroup, radioGroup2;
    int FROMDATE = 0;
    int TODATE = 1;
    int SETDATE = 0;
    SessionManager sessionManager;
    AmcuConfig amcuConfig;
    private com.eevoskos.robotoviews.widget.RobotoTextView tvheader;
    private RelativeLayout header;
    private MultiAutoCompleteTextView tvSelectFarmer, tvSelectMCC;
    private LinearLayout linearLayout;
    private Button btCancel, btnNext;
    private EditText tvStartDate, tvEndDate;
    private AutoCompleteTextView tvSelectMilkType, tvSelectShift;
    private ArrayList<String> allCenterList;
    private ArrayList<String> allFarmerList;
    private int thresHold = 0;
    private ArrayList<String> allCattleType;
    private ArrayList<String> allShift;
    private DatabaseHandler databaseHandler;
    private int adapterLayoutId;
    private DatabaseEntity databaseEntity;
    private boolean isAggerateFarmer;
    private RadioButton mRbMember, mRbMemberEdited;
    private RadioButton mRbAggerateMember, mRbSales, mRbSample;
    private RadioGroup rgStatus;
    private RadioButton rbMcc, rbDispatch;
    private boolean isMCC;
    private RadioButton rbComplete;
    private RadioButton rbInComplete;
    // Item Click events
    private long startMilliDate, endMilliDate;
    private FarmerDao farmerDao;
    private String mCollectionType = Util.REPORT_TYPE_FARMER;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_filter_report);
        DatePickerFragment.mActivity = 1;
        amcuConfig = AmcuConfig.getInstance();
        adapterLayoutId = getResources().getIdentifier("auto_text_item", "res", getPackageName());
        initializeView();
        onTextChange();

    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseEntity = new DatabaseEntity(FilterMemberReportActivity.this);
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        smartCCUtil = new SmartCCUtil(FilterMemberReportActivity.this);
        smartCCUtil.toSetDrawableOnEdit(tvSelectFarmer, ReportHintConstant.MEMBER_ID);
        // smartCCUtil.toSetDrawableOnEdit(tvSelectMCC,ReportHintConstant.MCC);
        smartCCUtil.toSetDrawableOnEdit(tvEndDate, ReportHintConstant.DATE_TO);
        smartCCUtil.toSetDrawableOnEdit(tvStartDate, ReportHintConstant.DATE_FROM);
        smartCCUtil.toSetDrawableOnEdit(tvSelectMilkType, ReportHintConstant.CATTLE_TYPE);
        smartCCUtil.toSetDrawableOnEdit(tvSelectShift, ReportHintConstant.SHIFT);

        setOnKeyClickListerner();
        setDateFromIntent();
        tvSelectFarmer.requestFocus();
        tvSelectFarmer.setSelection(tvSelectFarmer.getText().toString().length());
    }

    private void initializeView() {

        tvheader = (com.eevoskos.robotoviews.widget.RobotoTextView) findViewById(R.id.tvheader);
        header = (RelativeLayout) findViewById(R.id.header);
        tvSelectFarmer = (MultiAutoCompleteTextView) findViewById(R.id.tvSelectRoute);
        tvSelectMCC = (MultiAutoCompleteTextView) findViewById(R.id.tvSelectMCC);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        btCancel = (Button) findViewById(R.id.btCancel);
        btnNext = (Button) findViewById(R.id.btnNext);
        tvStartDate = (EditText) findViewById(R.id.tvStartDate);
        tvEndDate = (EditText) findViewById(R.id.tvEndDate);
        tvSelectMilkType = (AutoCompleteTextView) findViewById(R.id.tvSelectMilkType);
        tvSelectShift = (AutoCompleteTextView) findViewById(R.id.tvSelectShift);
        mRbMember = (RadioButton) findViewById(R.id.radioFarmerReport);
        mRbMemberEdited = (RadioButton) findViewById(R.id.radioFarmerEditedReport);
        mRbAggerateMember = (RadioButton) findViewById(R.id.radioAggregate);
        mRbSales = (RadioButton) findViewById(R.id.radioSales);
        mRbSample = (RadioButton) findViewById(R.id.rbSample);
        rbMcc = (RadioButton) findViewById(R.id.rbMCC);

        rbComplete = (RadioButton) findViewById(R.id.rbComplete);
        rbInComplete = (RadioButton) findViewById(R.id.rbIncomplete);
        rbDispatch = (RadioButton) findViewById(R.id.rbDispatch);

        rlCheckBox = (RelativeLayout) findViewById(R.id.rlCheckBox);
        checkBox = (CheckBox) findViewById(R.id.checkBox);

        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        radioGroup2 = (RadioGroup) findViewById(R.id.radioGroup2);
        rgStatus = (RadioGroup) findViewById(R.id.rgStatus);
        sessionManager = new SessionManager(FilterMemberReportActivity.this);


        rgStatus.setOnCheckedChangeListener(this);

        btCancel.setOnClickListener(this);
        btnNext.setOnClickListener(this);

//        tvStartDate.setOnItemSelectedListener(this);
//        tvEndDate.setOnItemSelectedListener(this);
        tvSelectMilkType.setOnItemSelectedListener(this);
        tvSelectShift.setOnItemSelectedListener(this);
        tvSelectMilkType.setOnItemClickListener(this);
        tvSelectShift.setOnItemClickListener(this);
        tvStartDate.setOnClickListener(this);
        tvEndDate.setOnClickListener(this);

        tvSelectFarmer.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        tvStartDate.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        tvEndDate.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        tvSelectMilkType.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        tvSelectShift.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());

        tvStartDate.setClickable(true);
        tvEndDate.setClickable(true);

        tvSelectMCC.setVisibility(View.GONE);
        tvSelectFarmer.setHint(" Enter member id..");


//        rlCheckBox.setVisibility(View.VISIBLE);

        /*if (AppConstants.IS_SELECTED_AGGERATE_FARMER) {
            mRbMember.setChecked(false);
            mRbAggerateMember.setChecked(true);
            isAggerateFarmer = true;
        } else {
            mRbMember.setChecked(true);
            mRbAggerateMember.setChecked(false);
            isAggerateFarmer = false;

        }*/

        if (sessionManager.getMCCStatus()) {
           /* rbMcc.setChecked(true);
            rgStatus.setVisibility(View.VISIBLE);
            rlCheckBox.setVisibility(View.GONE);
*/

        } else {
//            rbMcc.setChecked(false);
            rgStatus.setVisibility(View.GONE);
//            rlCheckBox.setVisibility(View.VISIBLE);


        }
        if (sessionManager.getMCCStatus()) {

            if (sessionManager.getRecordStatusComplete()) {
                rbComplete.setChecked(true);
            } else {
                rbInComplete.setChecked(true);
            }
        }
        visibleOption();

        checkBox.setChecked(true);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });


        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                //      Util.displayErrorToast(i+"",FilterReportActivity.this);

                RadioButton rb = (RadioButton) radioGroup.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    // checkedId is the RadioButton selected
                    switch (checkedId) {
                        case R.id.radioFarmerReport:
                        case R.id.radioFarmerEditedReport:
                            isAggerateFarmer = false;
                            sessionManager.setMCCStatus(false);
                            rgStatus.setVisibility(View.GONE);
//                            rlCheckBox.setVisibility(View.VISIBLE);
                            tvSelectMilkType.setVisibility(View.VISIBLE);
                            if (checkedId == R.id.radioFarmerEditedReport) {
                                mCollectionType = Util.REPORT_TYPE_FARMER_EDITED;
                            } else {
                                mCollectionType = Util.REPORT_TYPE_FARMER;
                            }
                            tvSelectFarmer.setVisibility(View.VISIBLE);
                            setCursor();
                            setAdapterForEditText();
                            tvSelectFarmer.requestFocus();

                            break;

                        case R.id.radioAggregate:
                            isAggerateFarmer = true;
                            sessionManager.setMCCStatus(false);
                            rgStatus.setVisibility(View.GONE);
//                            rlCheckBox.setVisibility(View.GONE);
                            tvSelectMilkType.setVisibility(View.VISIBLE);
                            mCollectionType = Util.REPORT_TYPE_AGENT_SPLIT;
                            tvSelectFarmer.setVisibility(View.VISIBLE);
                            setCursor();
                            setAdapterForEditText();
                            tvSelectFarmer.requestFocus();

                            break;
                        case R.id.rbMCC:
                            isAggerateFarmer = false;
                            sessionManager.setMCCStatus(true);
                            sessionManager.setRecordStatusComplete(true);
//                            rlCheckBox.setVisibility(View.GONE);
                            rgStatus.setVisibility(View.VISIBLE);
                            tvSelectMilkType.setVisibility(View.VISIBLE);
                            tvSelectFarmer.setVisibility(View.VISIBLE);
                            tvSelectFarmer.requestFocus();
                            setCursor();
                            break;
                        case R.id.radioSales:
                            isAggerateFarmer = false;
                            sessionManager.setMCCStatus(false);
                            rgStatus.setVisibility(View.GONE);
//                            rlCheckBox.setVisibility(View.GONE);
                            tvSelectMilkType.setVisibility(View.VISIBLE);
                            mCollectionType = Util.REPORT_TYPE_SALES;
                            tvSelectFarmer.setVisibility(View.GONE);
                            setCursor();
                            tvStartDate.requestFocus();
                            break;
                        case R.id.rbSample:
                            isAggerateFarmer = false;
                            sessionManager.setMCCStatus(false);
                            rgStatus.setVisibility(View.GONE);
//                            rlCheckBox.setVisibility(View.VISIBLE);
                            tvSelectMilkType.setVisibility(View.GONE);
                            mCollectionType = Util.REPORT_TYPE_SAMPLE;
                            tvSelectFarmer.setVisibility(View.VISIBLE);
                            setCursor();
                            setAdapterForEditText();
                            tvSelectFarmer.requestFocus();
                            break;

                        case R.id.rbDispatch:
                            isAggerateFarmer = false;
                            sessionManager.setMCCStatus(false);
                            rgStatus.setVisibility(View.GONE);
//                            rlCheckBox.setVisibility(View.GONE);
                            tvSelectMilkType.setVisibility(View.VISIBLE);
                            mCollectionType = Util.REPORT_TYPE_DISPATCH;
                            tvSelectFarmer.setVisibility(View.GONE);
                            setCursor();
                            break;


                    }
                }

            }
        });


    }


    public void onClick(View v) {
        if (v == btCancel) {
            AppConstants.IS_SELECTED_AGGERATE_FARMER = false;
            finish();
        } else if (v == btnNext) {
            if (checkForValidation()) {
                startActivity(getIntentFromParams());
                finish();
            }
        } else if (v == tvStartDate) {
            SETDATE = FROMDATE;
//            showFragment();
        } else if (v == tvEndDate) {
            SETDATE = TODATE;
//            showFragment();
        } else if (v == checkAllowHeader) {
            if (checkAllowHeader.isChecked()) {
                checkAllowHeader.setChecked(false);
                checkAllowHeader.setCheckMarkDrawable(R.drawable.uncheck_box);
            } else {
                checkAllowHeader.setChecked(true);
                checkAllowHeader.setCheckMarkDrawable(R.drawable.check_box);
            }

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
        Intent intent = new Intent(FilterMemberReportActivity.this, MemberReportActivity.class);
        if (radioGroup2.getCheckedRadioButtonId() == R.id.rbSample) {
            intent.putExtra(ReportHintConstant.CATTLE_TYPE, "");
        } else {
            String cattleType = tvSelectMilkType.getText().toString().toUpperCase();
            if (!cattleType.equals("")) {
                intent.putExtra(ReportHintConstant.CATTLE_TYPE, cattleType.substring(2, cattleType.length()));
            } else {
                intent.putExtra(ReportHintConstant.CATTLE_TYPE, "");
            }

        }
        intent.putExtra(ReportHintConstant.DATE_FROM, tvStartDate.getText().toString().toUpperCase());
        intent.putExtra(ReportHintConstant.DATE_TO, tvEndDate.getText().toString().toUpperCase());
        //   intent.putExtra(ReportHintConstant.MCC,tvSelectMCC.getText().toString().toUpperCase());
        String shift = tvSelectShift.getText().toString().toUpperCase();
        if (!shift.equals("")) {
            intent.putExtra(ReportHintConstant.SHIFT, shift.substring(2, shift.length()));
        } else {
            intent.putExtra(ReportHintConstant.SHIFT, "");
        }

        intent.putExtra(ReportHintConstant.MEMBER_ID, tvSelectFarmer.getText().toString().toUpperCase());
        intent.putExtra(ReportHintConstant.ALLOW_HEADER, checkBox.isChecked());

        intent.putExtra(ReportHintConstant.RADIO_BUTTON, radioGroup2.getCheckedRadioButtonId());
        intent.putExtra(ReportHintConstant.IS_AGGERATE_FARMER, isAggerateFarmer);
        intent.putExtra(ReportHintConstant.COLLECTION_TYPE, mCollectionType);


        return intent;
    }

    public void setAdapterForEditText() {

        if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_FARMER)) {
            allFarmerList = farmerDao.getAllFarmerIds();

        } else if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_AGENT_SPLIT)) {
            allFarmerList = farmerDao.getAllSplitFarmerIds();
        } else if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
            allFarmerList = databaseHandler.getAllSampleIds();
        } else {
            allFarmerList = new ArrayList<>();
        }


        allFarmerList.add(0, "ALL");
//       tvSelectFarmer.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        multiAutoTextAdapter = new MultiAutoTextAdapter(FilterMemberReportActivity.this,
                getApplicationContext(), adapterLayoutId, allFarmerList);
        tvSelectFarmer.setAdapter(multiAutoTextAdapter);

//        tvSelectFarmer.setAdapter(
//                new ArrayAdapter<String>(this,
//                        android.R.layout.simple_dropdown_item_1line, allFarmerList));

        tvSelectFarmer.setTokenizer(
                new MultiAutoCompleteTextView.CommaTokenizer());

        tvSelectFarmer.setText(allFarmerList.get(0));
        tvSelectFarmer.setSelection(tvSelectFarmer.getText().toString().length());
        tvSelectFarmer.setThreshold(thresHold);


        //To set cattle type
        autoTextAdapter = new AutoTextAdapter(FilterMemberReportActivity.this, adapterLayoutId, allCattleType);
        tvSelectMilkType.setThreshold(thresHold);
        tvSelectMilkType.setAdapter(autoTextAdapter);
//        tvSelectMilkType.setText(allCattleType.get(0));

        //To select the session
        autoTextAdapter = new AutoTextAdapter(FilterMemberReportActivity.this, adapterLayoutId, allShift);
        tvSelectShift.setThreshold(thresHold);
        tvSelectShift.setAdapter(autoTextAdapter);
        if (!getIntent().hasExtra(ReportHintConstant.SHIFT)) {
            tvSelectShift.setText(Util.getCurrentShift().equalsIgnoreCase("MORNING") ? allShift.get(0) : allShift.get(1));
        }
    }

    public void setShiftAndMilkType() {
        allCattleType = new ArrayList<>();
        allShift = new ArrayList<>();

        allCattleType.add("1-COW");
        allCattleType.add("2-BUFFALO");
        allCattleType.add("3-" + CattleType.MIXED);
        allCattleType.add("4-" + SmartCCConstants.SELECT_ALL);

        allShift.add("1-M");
        allShift.add("2-E");
        allShift.add("3-" + SmartCCConstants.SELECT_ALL);
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


    public void onTextChange() {

        tvSelectFarmer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //  Util.displayErrorToast("On Item selected",FilterReportActivity.this);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        tvSelectFarmer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
    }

    public boolean checkForValidation() {
        boolean isValid = true;
        long lnStart = 0;
        long lnEndDate = 0;

        setMultiEditText(tvSelectFarmer);


        if (tvStartDate.getText().toString().length() < 5 ||
                tvEndDate.getText().toString().length() < 5) {
            Util.displayErrorToast("Invalid date", FilterMemberReportActivity.this);
            isValid = false;
            return isValid;
        }


        if (!tvSelectFarmer.getText().toString().trim().equalsIgnoreCase("")
                && !tvSelectFarmer.getText().toString().trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            isValid = databaseEntity.checkForIfValueExist(DatabaseHandler.TABLE_FARMER,
                    DatabaseHandler.KEY_FARMER_ID, tvSelectFarmer.getText().toString());
        }

        if (isValid == false) {
            Util.displayErrorToast("Please check the member id!", FilterMemberReportActivity.this);
            return isValid;
        }


        if (!tvSelectShift.getText().toString().trim().equalsIgnoreCase("") &&
                !allShift.contains(tvSelectShift.getText().toString().toUpperCase().trim())) {
            setFocus(tvSelectShift);
            isValid = false;
            Util.displayErrorToast("Please select the valid shift!", FilterMemberReportActivity.this);
            return isValid;
        }

        tvSelectShift.setText(tvSelectShift.getText().toString().toUpperCase().trim());

        if (!tvSelectMilkType.getText().toString().trim().equalsIgnoreCase("") &&
                !allCattleType.contains(
                        tvSelectMilkType.getText().toString().toUpperCase().trim())) {
            setFocus(tvSelectMilkType);
            isValid = false;
            Util.displayErrorToast("Please select the valid Cattle type!", FilterMemberReportActivity.this);
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
            Util.displayErrorToast("Please select the valid date!", FilterMemberReportActivity.this);
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
            Util.displayErrorToast("Please select the valid date!", FilterMemberReportActivity.this);
            return isValid;
        }

        if (lnStart > Util.getDateInLongFormat(smartCCUtil.getReportFormatDate())) {
            Util.displayErrorToast("Please enter the valid date!", FilterMemberReportActivity.this);
            isValid = false;
        }

        if (lnEndDate < lnStart) {
            Util.displayErrorToast("Please enter the valid date!", FilterMemberReportActivity.this);
            isValid = false;
        }

        if (lnEndDate > Util.getDateInLongFormat(smartCCUtil.getReportFormatDate())) {
            Util.displayErrorToast("Please enter the valid date!", FilterMemberReportActivity.this);
            isValid = false;
        }

        if (!oneMonthDateValidation()) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_NUMPAD_ENTER: {
                dispatchKeyEvent(new KeyEvent(0, 0,
                        KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0));
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_UP: {
                mRbMember.requestFocus();
                break;
            }
            case KeyEvent.KEYCODE_PAGE_DOWN: {
                btnNext.requestFocus();
                break;
            }
            case KeyEvent.KEYCODE_NUMPAD_ENTER: {
                dispatchKeyEvent(new KeyEvent(0, 0,
                        KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0));
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    public void setOnKeyClickListerner() {
        tvStartDate.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        /*tvStartDate.setSelection(tvStartDate.getText().toString().trim().length());
                        SETDATE = FROMDATE;
                        showFragment();*/
                        tvEndDate.requestFocus();
                    }
                    return true;
                }

                return false;
            }
        });

        tvEndDate.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    /*if (keyEvent.getAction() == keyEvent.ACTION_UP) {
                        tvEndDate.setSelection(tvEndDate.getText().toString().trim().length());
                        SETDATE = TODATE;
                        showFragment();
                    }*/
                    if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        if (radioGroup2.getCheckedRadioButtonId() == R.id.rbSample) {
                            tvSelectShift.requestFocus();
                        } else {
                            tvSelectMilkType.requestFocus();
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        tvSelectFarmer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (tvSelectFarmer.isPopupShowing()) {
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_ADD) {
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            tvSelectFarmer.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                        } else {
                            tvSelectFarmer.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_SUBTRACT) {
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            tvSelectFarmer.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP, 0));
                        } else {
                            tvSelectFarmer.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP, 0));
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            tvSelectFarmer.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0));
                        } else {
                            tvSelectFarmer.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0));
                        }
                        return true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        tvStartDate.requestFocus();
                    }
                    return true;
                }

                return false;
            }
        });


        tvSelectMCC.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    if (event.getAction() == KeyEvent.ACTION_UP) {
                        tvSelectMCC.dispatchKeyEvent(new KeyEvent(0, 0,
                                KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0));
                    } else {
                        tvSelectMCC.dispatchKeyEvent(new KeyEvent(0, 0,
                                KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0));
                    }
                    return true;
                }
                return false;
            }
        });

        tvSelectMilkType.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (tvSelectMilkType.isPopupShowing()) {
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_ADD) {
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            tvSelectMilkType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                        } else {
                            tvSelectMilkType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_SUBTRACT) {
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            tvSelectMilkType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP, 0));
                        } else {
                            tvSelectMilkType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP, 0));
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            tvSelectMilkType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0));
                        } else {
                            tvSelectMilkType.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0));
                        }
                        return true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        tvSelectShift.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });
        tvSelectShift.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (tvSelectShift.isPopupShowing()) {
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_ADD) {
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            tvSelectShift.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                        } else {
                            tvSelectShift.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN, 0));
                        }
                        return true;
                    }
                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_SUBTRACT) {
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            tvSelectShift.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_UP, 0));
                        } else {
                            tvSelectShift.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP, 0));
                        }
                        return true;
                    }

                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                        if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                            tvSelectShift.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0));
                        } else {
                            tvSelectShift.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0));
                        }
                        return true;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        btnNext.requestFocus();
                    }
                    return true;
                }
                return false;
            }
        });

    }

    public void setFocus(EditText textView) {
        textView.requestFocus();
        textView.setSelection(textView.getText().toString().length());
    }


    /**
     * Set the UI field with intent incoming data
     */

    public void setDateFromIntent() {
        try {
            setShiftAndMilkType();
            setAdapterForEditText();
            tvStartDate.setText(Util.getDateDDMMYY(smartCCUtil.getReportDate(0), 11));
            tvEndDate.setText(Util.getDateDDMMYY(smartCCUtil.getReportDate(0), 11));
            tvSelectFarmer.setText(getIntent().getStringExtra(ReportHintConstant.ROUTE));
            //  tvSelectMCC.setText(getIntent().getStringExtra(ReportHintConstant.MCC));
            if (tvSelectFarmer.getText().toString().trim().length() < 1 &&
                    tvSelectFarmer.getText().toString().trim().length() < 1) {
                setShiftAndMilkType();
                setAdapterForEditText();
                tvStartDate.setText(Util.getDateDDMMYY(smartCCUtil.getReportDate(0), 11));
                tvEndDate.setText(Util.getDateDDMMYY(smartCCUtil.getReportDate(0), 11));
            }
            if (getIntent().hasExtra(ReportHintConstant.DATE_FROM)) {
                tvStartDate.setText(getIntent().getStringExtra(ReportHintConstant.DATE_FROM));
            }
            if (getIntent().hasExtra(ReportHintConstant.DATE_TO)) {
                tvEndDate.setText(getIntent().getStringExtra(ReportHintConstant.DATE_TO));
            }
            String cattleType = getIntent().getStringExtra(ReportHintConstant.CATTLE_TYPE);
            for (String type : allCattleType) {
                if (cattleType.equalsIgnoreCase(type.substring(2, type.length()))) {
                    tvSelectMilkType.setText(type);
                    break;
                }
            }
//            tvSelectMilkType.setText(getIntent().getStringExtra(ReportHintConstant.CATTLE_TYPE));
            String shift = getIntent().getStringExtra(ReportHintConstant.SHIFT);
            for (String s : allShift) {
                if (shift.equalsIgnoreCase(s.substring(2, s.length()))) {
                    tvSelectShift.setText(s);
                    break;
                }
            }
//            tvSelectShift.setText(getIntent().getStringExtra(ReportHintConstant.SHIFT));
            if (getIntent().hasExtra(ReportHintConstant.COLLECTION_TYPE)) {
                mCollectionType = getIntent().getStringExtra(ReportHintConstant.COLLECTION_TYPE);
            }
            if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_AGENT_SPLIT)) {
                mRbAggerateMember.setChecked(true);
            } else if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_SALES)) {
                mRbSales.setChecked(true);
            } else if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
                mRbSample.setChecked(true);
            } else if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_DISPATCH)) {
                rbDispatch.setChecked(true);
            } else if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_FARMER_EDITED)) {
                mRbMemberEdited.setChecked(true);
            } else {
                mRbMember.setChecked(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void onSelectFarmer() {
        smartCCUtil.toSetDrawableOnEdit(tvSelectFarmer, ReportHintConstant.MEMBER_ID);
        tvSelectMCC.setVisibility(View.GONE);
    }


    private void onSelectSplitRecord() {
        smartCCUtil.toSetDrawableOnEdit(tvSelectFarmer, ReportHintConstant.AGENT);
        tvSelectMCC.setVisibility(View.GONE);
    }

    private void onSelectCenter() {
        tvSelectMCC.setVisibility(View.VISIBLE);
    }

    private void visibleOption() {
        mRbSample.setVisibility(View.VISIBLE);
        mRbMember.setVisibility(View.VISIBLE);
        mRbMemberEdited.setVisibility(View.VISIBLE);

        if (amcuConfig.getKeyAllowAgentFarmerCollection()) {
            mRbAggerateMember.setVisibility(View.VISIBLE);
        } else {
            mRbAggerateMember.setVisibility(View.GONE);
        }

       /* if (amcuConfig.getEnableCenterCollection()) {
//            rbMcc.setVisibility(View.VISIBLE);
            mRbMember.setVisibility(View.VISIBLE);
        } else {
            rbMcc.setVisibility(View.INVISIBLE);
            mRbMember.setVisibility(View.GONE);

        }*/

        if (amcuConfig.getEnableSales()) {
            mRbSales.setVisibility(View.VISIBLE);
        } else {
            mRbSales.setVisibility(View.GONE);
        }

       /* if (amcuConfig.getDispatchValue()) {
            rbDispatch.setVisibility(View.VISIBLE);
        }*/

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppConstants.IS_SELECTED_AGGERATE_FARMER = false;
    }


    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

        if (checkedId == R.id.rbComplete) {
            sessionManager.setRecordStatusComplete(true);
        } else if (checkedId == R.id.rbIncomplete) {
            sessionManager.setRecordStatusComplete(false);

        }
    }

    private void setCursor() {
        tvSelectFarmer.clearFocus();
        tvSelectShift.clearFocus();
        tvSelectMilkType.clearFocus();
        tvStartDate.clearFocus();
        tvEndDate.clearFocus();
        btnNext.requestFocus();

    }

    private boolean oneMonthDateValidation() {
        long diff = -31;
        try {
            Date startDate = smartCCUtil.getDateFromFormat(tvStartDate.getText().toString(),
                    "dd-MM-yyyy");
            Date endDate = smartCCUtil.getDateFromFormat(tvEndDate.getText().toString(),
                    "dd-MM-yyyy");
            diff = TimeUnit.DAYS.convert((endDate.getTime() - startDate.getTime()), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (diff == -31) {
            Util.displayErrorToast("Please enter valid start and end date!",
                    FilterMemberReportActivity.this);
            return false;

        } else if (diff > 30) {
            Util.displayErrorToast("Maximum 30 days data allowed!",
                    FilterMemberReportActivity.this);
            return false;

        } else {
            return true;
        }
    }



}