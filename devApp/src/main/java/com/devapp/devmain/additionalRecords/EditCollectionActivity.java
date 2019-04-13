package com.devapp.devmain.additionalRecords;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eevoskos.robotoviews.widget.RobotoButton;
import com.devapp.devmain.additionalRecords.Database.AddtionalDatabase;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.AdditionalParamsDao;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.AdvanceUtil;
import com.devApp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class EditCollectionActivity extends AppCompatActivity {

    EditText snfET, fatET, qtyET;
    TextView tvFarmerId;
    RobotoButton submit;
    ArrayList<com.devapp.devmain.additionalRecords.FormViewHolder> fVHList = new ArrayList<>();
    LinearLayout container;
    ReportEntity reportEntity;
    long refSeqNum;
    private AdvanceUtil advanceUtil;
    private AdditionalParamsDao additionalParamsDao;
    private CollectionRecordDao collectionRecordDao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_collection);
        advanceUtil = new AdvanceUtil(this);
        additionalParamsDao =
                (AdditionalParamsDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_EDITED_ADDITIONAL);
        collectionRecordDao =
                (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);

        fVHList = getCustomViews();
        refSeqNum = getIntent().getLongExtra(EditableRecordList.SEQ_NUM, -1);

        AdditionalParamsEntity ePE = additionalParamsDao.findByReferenceSequenceNumber(refSeqNum);

        long collId = getIntent().getLongExtra(EditableRecordList.COLUMN_ID, -1);
        reportEntity = (ReportEntity) collectionRecordDao.findById(collId);

        initViews();
        populateViews(ePE);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void populateViews(AdditionalParamsEntity entity) {
        snfET.setText(String.valueOf(reportEntity.getDisplaySnf()));
        fatET.setText(String.valueOf(reportEntity.getDisplayFat()));
        qtyET.setText(String.valueOf(reportEntity.getDisplayQuantity()));
        tvFarmerId.setText("Member Id: " + reportEntity.farmerId);
        if (entity != null) {
            try {
                JSONObject jsonObject = new JSONObject(entity.extraParameters);
                for (int i = 0; i < fVHList.size(); i++) {
                    com.devapp.devmain.additionalRecords.FormViewHolder fVH = fVHList.get(i);
                    if (jsonObject.has(fVH.getId())) {
                        String value = jsonObject.getString(fVH.getId());
                        if (value != null) {
                            fVH.setValue(value);
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void initViews() {
        snfET = (EditText) findViewById(R.id.et_snf);
        fatET = (EditText) findViewById(R.id.et_fat);
        qtyET = (EditText) findViewById(R.id.et_qty);
        tvFarmerId = (TextView) findViewById(R.id.tvFarmerId);

        snfET.setEnabled(false);
        fatET.setEnabled(false);
        qtyET.setEnabled(false);

        container = (LinearLayout) findViewById(R.id.ll_container);
        submit = (RobotoButton) findViewById(R.id.btnNext);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    onSubmit();
                }
            }
        });
        if (fVHList.size() > 0) {
            for (int i = 0; i < fVHList.size(); i++) {
                container.addView(fVHList.get(i).getView());
            }
        }
        setLables();
    }

    private boolean isValid() {
        for (int i = 0; i < fVHList.size(); i++) {
            try {
                fVHList.get(i).validate();
            } catch (com.devapp.devmain.additionalRecords.FormViewHolder.FieldValidationException e) {

                Util.displayErrorToast(e.getMessage(), this);
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private void onSubmit() {

        HashMap<String, Object> map = new HashMap<>();
        for (int i = 0; i < fVHList.size(); i++) {
            com.devapp.devmain.additionalRecords.FormViewHolder fvh = fVHList.get(i);
            if (fvh.getValue() != null && !fvh.getValue().trim().equalsIgnoreCase("")) {
                map.put(fvh.getId(), fvh.getValue());
            }
        }

        if (map.keySet().size() == 0) {
            Util.displayErrorToast("No value saved!", EditCollectionActivity.this);
            return;
        }
        String json = new JSONObject(map).toString();
        AdditionalParamsEntity ePE = new AdditionalParamsEntity();
        ePE.key = "";
        ePE.extraParameters = json;
        // ePE.collectionType = Util.REPORT_TYPE_EDITED_RECORD;
        ePE.updatedTime = System.currentTimeMillis();
        ePE.refSeqNum = refSeqNum;
        ePE.sentStatus = AddtionalDatabase.SERVER_UNSENT_STATUS;

        ePE.smsSentStatus = CollectionConstants.UNSENT;
        ePE.postDate = reportEntity.postDate;
        ePE.postShift = reportEntity.postShift;
        ePE.collectionTime = reportEntity.miliTime;
        ePE.milkType = reportEntity.milkType;

        ePE.resetSentMarkers();
        try {
            additionalParamsDao.saveOrUpdate(ePE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        onFinish();
    }

    private ArrayList<FormViewHolder> getCustomViews() {
        AddtionalDatabase addtionalDatabase =
                DatabaseHandler.getDatabaseInstance().getAdditionallDatabase();
        ArrayList<CustomFieldEntity> list =
                addtionalDatabase.getCustomFieldsFromQuery(AddtionalDatabase.getAllCustomFieldQuery());
        ArrayList<com.devapp.devmain.additionalRecords.CustomFieldEntity> cFEList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).applicableFor.contains("farmerEdit")) {
                cFEList.add(list.get(i));
            }
        }
        fVHList.clear();
        for (int i = 0; i < cFEList.size(); i++) {
            com.devapp.devmain.additionalRecords.CustomFieldEntity cFE = cFEList.get(i);
            com.devapp.devmain.additionalRecords.FormViewHolder fvh = new com.devapp.devmain.additionalRecords.FormViewHolder(this, cFE);
            fVHList.add(fvh);
        }

        return fVHList;
    }

    private void setLables() {
        advanceUtil.toSetDrawableOnText(fatET, "FAT %: ", AppConstants.COLLECTION_HINT
                , AppConstants.DRAWABLE_PADDING_TANKER);
        advanceUtil.toSetDrawableOnText(snfET, "SNF %: ", AppConstants.COLLECTION_HINT
                , AppConstants.DRAWABLE_PADDING_TANKER);
        advanceUtil.toSetDrawableOnText(qtyET, "Qty : ", AppConstants.COLLECTION_HINT
                , AppConstants.DRAWABLE_PADDING_TANKER);
        for (int i = 0; i < fVHList.size(); i++) {
          /*  advanceUtil.toSetDrawableOnText((EditText)fVHList.get(i).getView(), fVHList.get(i).getName()+" : ", AppConstants.COLLECTION_HINT
                    ,AppConstants.DRAWABLE_PADDING_TANKER);*/
        }
    }

    private void onFinish() {
        startActivity(new Intent(EditCollectionActivity.this, com.devapp.devmain.additionalRecords.EditableRecordList.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        onFinish();
    }
}
