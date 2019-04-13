package com.devapp.devmain.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.devapp.devmain.dao.AdditionalParamsDao;
import com.devapp.devmain.dao.AgentSplitDao;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.DispatchRecordDao;
import com.devapp.devmain.dao.EditRecordDao;
import com.devapp.devmain.dao.SalesRecordDao;
import com.devapp.devmain.dao.TankerCollectionDao;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.user.Util;
import com.devApp.R;

public class UnsentRecordsActivity extends AppCompatActivity {
    TextView farmerTV, agentSplitTV, additionalParamsTV, sampleTV,
            editedFarmerTV, editedMccTV, mccTV, mccIncompleteTV,
            salesTV, tankerTV, dispatchTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Make this activity, full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Hide the Title bar of this activity screen
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_unsent_records);
        farmerTV = (TextView) findViewById(R.id.tv_unsentFarmer);
        agentSplitTV = (TextView) findViewById(R.id.tv_unsentAgentSplit);
        additionalParamsTV = (TextView) findViewById(R.id.tv_unsentAdditionalParams);
        sampleTV = (TextView) findViewById(R.id.tv_unsentSample);
        editedFarmerTV = (TextView) findViewById(R.id.tv_unsentEditedFarmer);
        editedMccTV = (TextView) findViewById(R.id.tv_unsentEditedMCC);
        mccTV = (TextView) findViewById(R.id.tv_unsentMCC);
        mccIncompleteTV = (TextView) findViewById(R.id.tv_unsentIncompleteMCC);
        salesTV = (TextView) findViewById(R.id.tv_unsentSales);
        dispatchTV = (TextView) findViewById(R.id.tv_unsent_dispatch);
        tankerTV = (TextView) findViewById(R.id.tv_unsentTanker);
        populateViews();
    }

    private void populateViews() {
        int farmer, agentSplit, additionalParams, sample, editedFarmer,
                editedMcc, mcc, mccIncomplete, sales, tanker, dispatch;
        CollectionRecordDao collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        EditRecordDao editRecordDao = (EditRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_EDITED);
        SalesRecordDao salesRecordDao = (SalesRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_SALES);
        DispatchRecordDao dispatchRecordDao = (DispatchRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_DISPATCH);
        TankerCollectionDao tankerCollectionDao = (TankerCollectionDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_TANKER);
        AgentSplitDao agentSplitDao = (AgentSplitDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_AGENT_SPLIT);
        AdditionalParamsDao additionalParamsDao = (AdditionalParamsDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_EDITED_ADDITIONAL);

        farmer = collectionRecordDao.findAllUnsentByCollectionTypeAndCompletionStatus(CollectionConstants.REPORT_TYPE_COLLECTION, null).size();
        sample = collectionRecordDao.findAllUnsentByCollectionTypeAndCompletionStatus(CollectionConstants.REPORT_TYPE_SAMPLE, null).size();
        mcc = collectionRecordDao.findAllUnsentByCollectionTypeAndCompletionStatus(CollectionConstants.REPORT_TYPE_CHILLING, Util.RECORD_STATUS_COMPLETE).size();
        mccIncomplete = collectionRecordDao.findAllUnsentByCollectionTypeAndCompletionStatus(CollectionConstants.REPORT_TYPE_CHILLING, Util.RECORD_STATUS_INCOMPLETE).size();
        agentSplit = agentSplitDao.findAllUnsent().size();
        additionalParams = additionalParamsDao.findAllUnsent().size();
        editedFarmer = editRecordDao.findAllUnsentByCollectionType(CollectionConstants.REPORT_TYPE_COLLECTION).size();
        editedMcc = editRecordDao.findAllUnsentByCollectionType(CollectionConstants.REPORT_TYPE_CHILLING).size();
        sales = salesRecordDao.findAllUnsent().size();
        dispatch = dispatchRecordDao.findAllUnsent().size();
        tanker = tankerCollectionDao.findAllUnsent().size();

        farmerTV.setText(String.valueOf(farmer));
        sampleTV.setText(String.valueOf(sample));
        mccTV.setText(String.valueOf(mcc));
        mccIncompleteTV.setText(String.valueOf(mccIncomplete));
        agentSplitTV.setText(String.valueOf(agentSplit));
        additionalParamsTV.setText(String.valueOf(additionalParams));
        editedFarmerTV.setText(String.valueOf(editedFarmer));
        editedMccTV.setText(String.valueOf(editedMcc));
        salesTV.setText(String.valueOf(sales));
        dispatchTV.setText(String.valueOf(dispatch));
        tankerTV.setText(String.valueOf(tanker));

    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F9:
                Util.Logout(UnsentRecordsActivity.this);
                finish();
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(UnsentRecordsActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(UnsentRecordsActivity.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(UnsentRecordsActivity.this, null);
                return true;
            case KeyEvent.KEYCODE_DEL:
                finish();
                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }
}
