package com.devapp.devmain.additionalRecords;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devApp.R;

import java.util.ArrayList;

/**
 * Created by u_pendra on 28/7/17.
 */

public class EditableRecordList extends AppCompatActivity {


    public final static String COLUMN_ID = "colId";
    public final static String SEQ_NUM = "seqNum";
    EditText editSearch;
    EditableAdapter editableAdapter;
    ListView editListView;
    TextView unsent;
    ArrayList<ReportEntity> allReportEntity = new ArrayList<>();
    DatabaseHandler dbh;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editable_list);
        editSearch = (EditText) findViewById(R.id.editSearch);
        editListView = (ListView) findViewById(R.id.list_view);
        unsent = (TextView) findViewById(R.id.tv_unsent);

        editListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ReportEntity reportEntity = allReportEntity.get(i);
                if (reportEntity != null) {
                    long sequenceNumber = allReportEntity.get(i).sequenceNum;

                    Intent intent = new Intent(EditableRecordList.this, EditCollectionActivity.class);
                    intent.putExtra(COLUMN_ID, reportEntity.columnId);
                    intent.putExtra(SEQ_NUM, sequenceNumber);
                    startActivity(intent);
                    finish();
                }

            }
        });

        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editableAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        String query = DatabaseEntity.getQueryForEditedFarmerList(EditableRecordList.this);
        dbh = DatabaseHandler.getDatabaseInstance();
        allReportEntity = dbh.getAllReportEntityFromQuery(query);
        editableAdapter = new EditableAdapter(EditableRecordList.this, 0, allReportEntity);
        editListView.setAdapter(editableAdapter);
        editListView.setTextFilterEnabled(true);
        unsent.setText(String.valueOf(dbh.getAdditionallDatabase().getExtraParamUnsentCount()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }


}
