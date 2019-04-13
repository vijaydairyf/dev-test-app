package com.devapp.devmain.agentfarmersplit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.APIConstants;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by y on 17/10/17.
 */

public class AgentFarmerActivity extends Activity implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    public final static String PARENT_ID = "parentId";
    ArrayList<String> farmerIdList;
    ArrayList<String> mCenterEntities = new ArrayList<>();
    TextView tvDateShift;
    TextView tvTime;
    int POS = 0;
    ArrayList<ReportEntity> allReportEntity;
    SmartCCUtil smartCCUtil;
    private Activity mActivity;
    private AutoCompleteTextView mAtvAgent;
    private AutoCompleteTextView mAtvFarmer;
    private DatabaseHandler mDatabaseHandler;
    private Button btnCancel;
    private Button mBtnNext;
    private String agentid;
    private String farmerId;
    private TextView tvAgentSummary;
    private TextView tvFarmerSummary;
    private FarmerDao farmerDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_farmer);
        mActivity = AgentFarmerActivity.this;
        farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        initalizationView();
    }

    private void initalizationView() {

        tvDateShift = (TextView) findViewById(R.id.tvDateShift);
        tvTime = (TextView) findViewById(R.id.tvTime);

        tvDateShift.setText(
                new SmartCCUtil(AgentFarmerActivity.this).getReportDate(0) + "/" +
                        SmartCCUtil.getAlternateShift(Util.getCurrentShift()));

        mAtvAgent = (AutoCompleteTextView) findViewById(R.id.tvAgents);
        mAtvFarmer = (AutoCompleteTextView) findViewById(R.id.tvFarmers);
        tvAgentSummary = (TextView) findViewById(R.id.tvAgentSummary);
        tvFarmerSummary = (TextView) findViewById(R.id.tvFarmerSummary);

        mDatabaseHandler = DatabaseHandler.getDatabaseInstance();
        smartCCUtil = new SmartCCUtil(AgentFarmerActivity.this);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnNext = (Button) findViewById(R.id.btnNext);
        btnCancel.setOnClickListener(this);
        mBtnNext.setOnClickListener(this);

        loadAgentList();

        mAtvAgent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
                agentid = parent.getItemAtPosition(position).toString();
                APIConstants.AgentID = agentid;
                if (agentid != null)
                    loadFarmerlist(agentid);


            }

        });
        mAtvFarmer.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {

                farmerId = parent.getItemAtPosition(position).toString();

            }

        });

        mAtvAgent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mAtvAgent.getText().toString().length() == 0) {
                    mAtvFarmer.setText("");
                    if (farmerIdList != null && farmerIdList.size() > 0) {
                        farmerIdList.clear();
                        loadFarmerlist("");
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();

        setTheAgentSummary();
        setTheFarmerSummary();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnNext:

                if (validateAgentId()) {

                    if (validateFarmerId()) {

                        if (mAtvAgent.getText().toString() != null && mAtvAgent.getText().toString().length() > 0 && mAtvFarmer.getText().toString() != null
                                && mAtvFarmer.getText().toString().length() > 0) {
                            String farmerID = mAtvFarmer.getText().toString().trim();
                            String agentID = mAtvAgent.getText().toString();
                            String milkType = ((FarmerEntity) farmerDao.findById(farmerID)).farmer_cattle;
                            if (!DatabaseHandler.getDatabaseInstance().isFarmerCollectionDone(farmerID, agentID, SmartCCUtil.getFullShift(Util.getCurrentShift()), milkType, smartCCUtil.getReportFormatDate())) {
                                Intent intent = new Intent(mActivity, AggregateFarmerCollection.class);
                                intent.putExtra("farmerId", mAtvFarmer.getText().toString().trim()).
                                        putExtra("agentId", mAtvAgent.getText().toString());
                                intent.putExtra(PARENT_ID, getParentSeqNum(mAtvAgent.getText().toString().trim()));
                                startActivity(intent);
                                finish();
                            } else {
                                Util.displayErrorToast("For " + farmerID
                                                + " procurment is already done!"
                                        , AgentFarmerActivity.this);

                            }
                        }
                    } else {
                        Util.displayErrorToast("Select Valid Farmer Id", mActivity);

                    }
                } else {
                    Util.displayErrorToast("Select Valid Agent Id", mActivity);

                }

                break;
        }
    }

    private boolean validateFarmerId() {
        boolean isValidFarmerId = false;
        if (farmerIdList == null || farmerIdList.size() == 0) {
            return isValidFarmerId;
        }
        for (int i = 0; i < farmerIdList.size(); i++) {
            if (farmerIdList.get(i).equalsIgnoreCase(mAtvFarmer.getText().toString())) {
                isValidFarmerId = true;
            }
        }

        return isValidFarmerId;
    }

    private boolean validateAgentId() {
        boolean isValidFarmerId = false;
        for (int i = 0; i < mCenterEntities.size(); i++) {
            if (mCenterEntities.get(i).equalsIgnoreCase(mAtvAgent.getText().toString())) {
                isValidFarmerId = true;
            }
        }

        return isValidFarmerId;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //load AgentID  from DB
    private void loadAgentList() {
        if (APIConstants.AgentID != null) {
            mAtvAgent.setText(APIConstants.AgentID);
            mAtvFarmer.requestFocus();
            mAtvFarmer.setSelection(0);
            loadFarmerlist(APIConstants.AgentID);

        }

        String query = DatabaseEntity.getQueryToGetAgentIdForDateAndShift(AgentFarmerActivity.this);
        allReportEntity = mDatabaseHandler.getALLAggreateFarmerIDCursor(query);

        for (int i = 0; i < allReportEntity.size(); i++) {

            if (mDatabaseHandler.checkIdAggerateFarmerOrNot(allReportEntity.get(i).farmerId)) {
                mCenterEntities.add(allReportEntity.get(i).farmerId);
            }
        }

        if (mCenterEntities != null && mCenterEntities.size() > 0) {
            AgentAutoAdapter agentListAdapter = new AgentAutoAdapter(
                    mActivity, R.layout.item_agent_list, mCenterEntities);
            mAtvAgent.setAdapter(agentListAdapter);
        }
    }

    //load FarmerId  from DB
    private void loadFarmerlist(String agentId) {

        farmerIdList = farmerDao.getAllFarmerIdByAgentId(agentId);
        if (farmerIdList != null && farmerIdList.size() > 0) {
            AgentAutoAdapter adapter = new AgentAutoAdapter(mActivity, R.layout.item_agent_list, farmerIdList);
            mAtvFarmer.setAdapter(adapter);
        } else {
            mAtvFarmer.setAdapter(null);
        }

    }

    private int getParentSeqNum(String Id) {

        for (int i = 0; i < allReportEntity.size(); i++) {
            if (Id.equalsIgnoreCase(allReportEntity.get(i).farmerId)) {
                POS = i;
                break;
            }

        }

        ReportEntity reportEntity = allReportEntity.get(POS);
        int num = reportEntity.sampleNumber;

        return num;
    }


    private void setTheAgentSummary() {

        String totalAgent = "0", totalCollection = "0",
                totalUnsent = "0";

        DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();
        String returnValue = dbHandler.getShiftAgentDetails();


        if (returnValue != "" && returnValue != null) {
            String[] splitArray = returnValue.split(AppConstants.DB_SEPERATOR);

            if (splitArray != null && splitArray.length > 0) {
                totalAgent = splitArray[0];
                totalCollection = splitArray[1];
            }

        }


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%-14s : %s\n", "Total Agents", totalAgent));
        stringBuilder.append(String.format("%-18s : %s\n", "Total Qty", new ValidationHelper().getDoubleFromString(
                new DecimalFormat("#0.00"), totalCollection)));
        //  stringBuilder.append(String.format("%-18s : %s\n", "Unsent ", totalUnsent));
        tvAgentSummary.setText(stringBuilder.toString());

    }

    private void setTheFarmerSummary() {

        String totalAgent = "0", totalCollection = "0",
                totalUnsent = "0";

        DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();
        String returnValue = dbHandler.getShiftSplitFamrerDetails();
        totalUnsent = dbHandler.getTotalSplitFarmerUnSentRecords();

        if (returnValue != "" && returnValue != null) {
            String[] splitArray = returnValue.split(AppConstants.DB_SEPERATOR);

            if (splitArray != null && splitArray.length > 0) {
                totalAgent = splitArray[0];
                totalCollection = splitArray[1];
            }

        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("%-14s : %s\n", "Total Farmer", totalAgent));
        stringBuilder.append(String.format("%-18s : %s\n", "Total Qty", new ValidationHelper().getDoubleFromString(
                new DecimalFormat("#0.00"), totalCollection)));
        stringBuilder.append(String.format("%-14s : %s\n", "Total Unsent", totalUnsent));

        //  stringBuilder.append(String.format("%-18s : %s\n", "Unsent ", totalUnsent));
        tvFarmerSummary.setText(stringBuilder.toString());

    }


}
