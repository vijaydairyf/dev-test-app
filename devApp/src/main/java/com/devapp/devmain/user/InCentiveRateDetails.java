package com.devapp.devmain.user;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devApp.R;

import java.util.ArrayList;

/**
 * Created by y on 13/12/17.
 */

public class InCentiveRateDetails extends Activity implements View.OnClickListener {

    ArrayList<RateChartEntity> allDatabaseRatechart;
    RelativeLayout progressLayout;
    private DatabaseHandler databaseHelper;
    private ListView lvRateChart;
    private Button btnCreateRate, btnDelete;
    private Activity mActivity;
    private SessionManager session;
    private AmcuConfig amcuConfig;
    private String rateChartname;
    private TextView tvFat;
    private TextView tvSnf;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert_rate);
        mActivity = InCentiveRateDetails.this;

        initaliztionView();
    }

    private void initaliztionView() {
        lvRateChart = (ListView) findViewById(R.id.lvFSnf);
        btnCreateRate = (Button) findViewById(R.id.btnCreateRateChart);
        tvFat = (TextView) findViewById(R.id.tvFat);
        tvSnf = (TextView) findViewById(R.id.tvSnf);

        allDatabaseRatechart = new ArrayList<RateChartEntity>();
        amcuConfig = AmcuConfig.getInstance();
        btnDelete = (Button) findViewById(R.id.btnDelete);

        rateChartname = amcuConfig.getRateChartName();
        progressLayout = (RelativeLayout) findViewById(R.id.progress_layout);
        progressLayout.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);

        tvFat.setText("Point");
        tvSnf.setVisibility(View.INVISIBLE);
        btnCreateRate.setText("Back");

        btnCreateRate.requestFocus();
        btnCreateRate.setOnClickListener(this);
        getAllProteinPoint();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreateRateChart:
                finish();
                break;

        }
    }

    private void getAllProteinPoint() {

        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();

        try {
            allDatabaseRatechart = db.getAllProteinRateData(String
                    .valueOf(new SessionManager(mActivity)
                            .getSocietyColumnId()), rateChartname, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (allDatabaseRatechart != null && allDatabaseRatechart.size() > 0) {
            loadListView(allDatabaseRatechart);
        }
    }

    private void loadListView(ArrayList<RateChartEntity> rateChartEntities) {
        IncentiveAdapter adapter = new IncentiveAdapter(mActivity, 0, rateChartEntities);
        lvRateChart.setAdapter(adapter);

    }
}
