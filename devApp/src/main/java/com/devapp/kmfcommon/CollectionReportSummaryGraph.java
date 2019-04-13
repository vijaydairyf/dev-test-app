package com.devapp.kmfcommon;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.devApp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.view.ColumnChartView;

public class CollectionReportSummaryGraph extends FragmentActivity {

    static String sUserDate, sUserShift;
    static TextView coolection_summary1, total_collection_milk, total_collection_amount, local_collection_milk, local_collection_amount, truck_collection_milk, truck_collection_amount, summary_collection_milk, summary_collection_amount;
    FrameLayout frameLayout;
    Button btnTruck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_column_chart);
        sUserDate = getIntent().getStringExtra("s_date");
        sUserShift = getIntent().getStringExtra("s_shift");
        btnTruck = (Button) findViewById(R.id.alltruck);
        coolection_summary1 = (TextView) findViewById(R.id.coolection_summary1);

        total_collection_milk = (TextView) findViewById(R.id.total_collection_milk);
        total_collection_amount = (TextView) findViewById(R.id.total_collection_amount);
        local_collection_milk = (TextView) findViewById(R.id.local_collection_milk);
        local_collection_amount = (TextView) findViewById(R.id.local_collection_amount);
        truck_collection_milk = (TextView) findViewById(R.id.truck_collection_milk);
        truck_collection_amount = (TextView) findViewById(R.id.truck_collection_amount);
        summary_collection_milk = (TextView) findViewById(R.id.summary_collection_milk);
        summary_collection_amount = (TextView) findViewById(R.id.summary_collection_amount);


        coolection_summary1.setText("Collection Summary " + sUserDate + " (" + sUserShift + ")");
        frameLayout = (FrameLayout) findViewById(R.id.container);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        frameLayout.startAnimation(animation);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    public void gotoAllTruckEventData(View view) {
        // summary of All TruckEvent data
        Intent intent = new Intent(this, AllTruckEventActivity.class);
        startActivity(intent);
        /*setContentView(R.layout.activity_truck_summary_report);
        TextView textView = (TextView)findViewById(R.id.coolection_summary1);
        textView.setText("All Truck Event Data");
        otherRecyclerView = (RecyclerView) findViewById(R.id.data);
        setOtherData();*/
    }


    /**
     * A fragment containing a column chart.
     */
    public static class PlaceholderFragment extends Fragment {

        public final static String[] collection_type = new String[]{"Collection", "Local Sales", "Truck Entry"};
        private static final int STACKED_DATA = 2;
        private ColumnChartView chart;
        private ColumnChartData data;
        private boolean hasAxes = false;
        private boolean hasAxesNames = false;
        private boolean hasLabels = false;
        private boolean hasLabelForSelected = false;
        private int dataType = STACKED_DATA;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            setHasOptionsMenu(true);
            View rootView = inflater.inflate(R.layout.fragment_column_chart, container, false);

            chart = (ColumnChartView) rootView.findViewById(R.id.chart);
            chart.setOnValueTouchListener(new ValueTouchListener());

            generateData();

            return rootView;
        }


        /**
         * Generates columns with stacked subcolumns.
         */
        private void generateStackedData() {
            int numSubcolumns = 2;
            int numColumns = collection_type.length;
            int SEA_GREEN = Color.parseColor("#708090");
            int COLOR_ORANGE = Color.parseColor("#FFBB33");
            int[] COLORS = {SEA_GREEN, COLOR_ORANGE};
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            // DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance(getActivity());
            ArrayList<Float> graphData = new ArrayList<>();
            TruckDetailCommonReport truckDetailCommonReport = new TruckDetailCommonReport();

            // for total collection
            String graphStringDataForTotalCollection = truckDetailCommonReport.setTotalCollectionReportData(getActivity(), sUserDate, sUserShift);
            StringTokenizer st = new StringTokenizer(graphStringDataForTotalCollection, ",");
            while (st.hasMoreTokens()) {
                graphData.add(Float.valueOf(st.nextToken().trim()));
            }
            //for local sales
            String graphStringDataForLocalCollection = truckDetailCommonReport.setLocalSalesReport(getActivity(), sUserDate, sUserShift, "SALES");
            StringTokenizer stLocal = new StringTokenizer(graphStringDataForLocalCollection, ",");
            while (stLocal.hasMoreTokens()) {
                graphData.add(Float.valueOf(stLocal.nextToken().trim()));
            }
            //for truck event report
            String graphStringDataForTruckEvent = truckDetailCommonReport.setLocalSalesReport(getActivity(), sUserDate, sUserShift, "COOPERATIVE");
            StringTokenizer stTruck = new StringTokenizer(graphStringDataForTruckEvent, ",");
            while (stTruck.hasMoreTokens()) {
                graphData.add(Float.valueOf(stTruck.nextToken().trim()));
            }

           /*graphData.add(23.0f);
            graphData.add(10.0f);*/
           /* graphData.add(43.8f);
            graphData.add(20.10f);*/
            /*graphData.add(63.10f);
            graphData.add(26.4f);*/
            int arrayIndex = 0;
            List<Column> columns = new ArrayList<Column>();
            List<AxisValue> axisValues = new ArrayList<AxisValue>();
            List<SubcolumnValue> values;
            for (int i = 0; i < numColumns; ++i) {

                values = new ArrayList<SubcolumnValue>();
                for (int j = 0; j < numSubcolumns; ++j) {
                    values.add(new SubcolumnValue(graphData.get(arrayIndex), COLORS[j]));
                    arrayIndex++;
                }

                Column column = new Column(values);
                column.setHasLabels(hasLabels);
                column.setHasLabelsOnlyForSelected(hasLabelForSelected);
                axisValues.add(new AxisValue(i).setLabel(collection_type[i]));
                columns.add(column);
            }

            data = new ColumnChartData(columns);
            data.setAxisXBottom(new Axis(axisValues).setHasLines(true));
            //yyy comment for y level
            //  data.setAxisYLeft(new Axis().setHasLines(true).setMaxLabelChars(2));
            // Set stacked flag.
            data.setStacked(true);
            chart.setColumnChartData(data);
            setUIData(graphData);
        }

        public void setUIData(ArrayList<Float> graphData) {

            ArrayList<Float> uiData = graphData;
            total_collection_milk.setText("Collected Milk: " + uiData.get(0));
            total_collection_amount.setText("Total Amount: " + uiData.get(1));
            local_collection_milk.setText("Local Sales: " + uiData.get(2));
            local_collection_amount.setText("Amount: " + uiData.get(3));
            truck_collection_milk.setText("Truck Event: " + uiData.get(4));
            truck_collection_amount.setText("Amount: " + uiData.get(5));
            float tot_milk = uiData.get(0);
            float tot_amt = uiData.get(1) - (uiData.get(3) + uiData.get(5));
            summary_collection_milk.setText("Total Milk: " + tot_milk);
            summary_collection_amount.setText("Total Amount: " + tot_amt);
        }

        private void generateData() {
            switch (dataType) {

                case STACKED_DATA:
                    generateStackedData();
                    break;

                default:
                    generateStackedData();
                    break;
            }
        }


        private class ValueTouchListener implements ColumnChartOnValueSelectListener {

            @Override
            public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
                //      Toast.makeText(getActivity(), "Selected: " + value, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onValueDeselected() {
                // TODO Auto-generated method stub

            }

        }

    }
}
