package com.devapp.kmfcommon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.user.Util;
import com.devApp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yyy on 4/2/16.
 */
public class TruckDetailCommonReport extends Activity {
    String head;
    TextView tvheader;
    TextView row2_text2, row2_text3, row2_text4, row2_text5, row2_text7;
    TextView row3_text2, row3_text3, row3_text4, row3_text5, row3_text7;
    TextView row4_text2, row4_text3, row4_text4, row4_text5, row4_text7, summary_quan1, summary_rate1, cob_qantity, cob_amount;
    DecimalFormat decimalFormat;
    double cow_avg_fat = 0, cow_avg_snf = 0, cow_avg_quantity = 0, cow_avg_amount = 0, cob_avg_amount = 0, cob_avg_quantity = 0;
    double buf_avg_fat = 0, buf_avg_snf = 0, buf_avg_quantity = 0, buf_avg_amount = 0;
    double mix_avg_fat = 0, mix_avg_snf = 0, mix_avg_quantity = 0, mix_avg_amount = 0;
    StringBuilder stringBuilder = new StringBuilder();
    private RecyclerView otherRecyclerView;
    private ArrayList<ListAllDataItem> dataItems;
    private ListAllDashboardAdapter listAllDashboardAdapter;
    private String user_date, user_shift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        head = getIntent().getStringExtra("HEAD");
        user_date = getIntent().getStringExtra("S_DATE");
        user_shift = getIntent().getStringExtra("S_SHIFT");

        if (!head.equalsIgnoreCase("COLLECTION_SUMMARY")) {
            setContentView(R.layout.activity_truck_entry_common_report);
            decimalFormat = new DecimalFormat("#0.00");
            tvheader = (TextView) findViewById(R.id.tvheader);

            // gettting tex view
            row2_text2 = (TextView) findViewById(R.id.row2_text2);
            row2_text3 = (TextView) findViewById(R.id.row2_text3);
            row2_text4 = (TextView) findViewById(R.id.row2_text4);
            row2_text5 = (TextView) findViewById(R.id.row2_text5);
            row2_text7 = (TextView) findViewById(R.id.row2_text7);

            row3_text2 = (TextView) findViewById(R.id.row3_text2);
            row3_text3 = (TextView) findViewById(R.id.row3_text3);
            row3_text4 = (TextView) findViewById(R.id.row3_text4);
            row3_text5 = (TextView) findViewById(R.id.row3_text5);
            row3_text7 = (TextView) findViewById(R.id.row3_text7);

            row4_text2 = (TextView) findViewById(R.id.row4_text2);
            row4_text3 = (TextView) findViewById(R.id.row4_text3);
            row4_text4 = (TextView) findViewById(R.id.row4_text4);
            row4_text5 = (TextView) findViewById(R.id.row4_text5);
            row4_text7 = (TextView) findViewById(R.id.row4_text7);

            summary_quan1 = (TextView) findViewById(R.id.summary_quan1);
            summary_rate1 = (TextView) findViewById(R.id.summary_rate1);
            cob_qantity = (TextView) findViewById(R.id.cob_qantity);
            cob_amount = (TextView) findViewById(R.id.cob_amount);
        }
        if (head.equalsIgnoreCase("TOTAL_COLLECTION")) {
            tvheader.setText("Total Collection Summary Report " + user_date);
            Toast.makeText(getApplicationContext(), "DATA::" + user_date + "shift:: " + user_shift, Toast.LENGTH_LONG).show();
            gotoTotalCollectionReport();

        } else if (head.equalsIgnoreCase("LOCAL_SALES")) {
            tvheader.setText("Local Sales Report " + user_date);
            Toast.makeText(getApplicationContext(), "DATA::" + user_date + "shift:: " + user_shift, Toast.LENGTH_LONG).show();
            gotoLocalSalesReport();

        } else if (head.equalsIgnoreCase("TRUCK_EVENT")) {
            tvheader.setText("Truck Evet Report " + user_date);
            gotoTotalTruckCollection();
        } else if (head.equalsIgnoreCase("COLLECTION_SUMMARY")) {
            Intent intent = new Intent(getApplicationContext(), CollectionReportSummaryGraph.class);
            intent.putExtra("s_date", user_date);
            intent.putExtra("s_shift", user_shift);
            startActivity(intent);
            finish();

        }
    }

    // yyy @ for Truck Event collection data
    public void gotoTotalTruckCollection() {
        setLocalSalesReport(getApplicationContext(), user_date, user_shift, "COOPERATIVE");
        System.out.print("==============KUMAR" + stringBuilder.toString());
        row2_text2.setText("" + decimalFormat.format(cow_avg_quantity));
        row2_text3.setText("" + decimalFormat.format(cow_avg_amount / cow_avg_quantity));// average rate
        row2_text4.setText("" + decimalFormat.format((cow_avg_fat * 100) / cow_avg_quantity)); // avg fat
        row2_text5.setText("" + decimalFormat.format((cow_avg_snf * 100) / cow_avg_quantity)); // avg snf
        row2_text7.setText("" + decimalFormat.format(cow_avg_amount));
        ///
        row3_text2.setText("" + decimalFormat.format(buf_avg_quantity));
        row3_text3.setText("" + decimalFormat.format(buf_avg_amount / buf_avg_quantity));// average rate
        row3_text4.setText("" + decimalFormat.format((buf_avg_fat * 100) / buf_avg_quantity)); // avg fat
        row3_text5.setText("" + decimalFormat.format((buf_avg_snf * 100) / buf_avg_quantity)); // avg snf
        row3_text7.setText("" + decimalFormat.format(buf_avg_amount));
        ///
        row4_text2.setText("" + decimalFormat.format(mix_avg_quantity));
        row4_text3.setText("" + decimalFormat.format(mix_avg_amount / mix_avg_quantity));// average rate
        row4_text4.setText("" + decimalFormat.format((mix_avg_fat * 100) / mix_avg_quantity)); // avg fat
        row4_text5.setText("" + decimalFormat.format((mix_avg_snf * 100) / mix_avg_quantity)); // avg snf
        row4_text7.setText("" + decimalFormat.format(mix_avg_amount));


        summary_quan1.setText("Total Milk: " + decimalFormat.format(cow_avg_quantity + buf_avg_quantity + mix_avg_quantity) + " lts");
        summary_rate1.setText("Total Amount: Rs. " + decimalFormat.format(cow_avg_amount + buf_avg_amount + mix_avg_amount));
        cob_qantity.setVisibility(View.VISIBLE);
        cob_amount.setVisibility(View.VISIBLE);
        cob_qantity.setText("Total COB Milk: " + cob_avg_quantity);
        cob_amount.setText("Total COB Amount: Rs. " + cob_avg_amount);
    }

    // yyy @ get data from db for local sales and truck event collection
    public String setLocalSalesReport(Context context, String user_date, String user_shift, String txnType) {
        cow_avg_quantity = 0;
        buf_avg_quantity = 0;
        mix_avg_quantity = 0;
        cow_avg_amount = 0;
        buf_avg_amount = 0;
        mix_avg_amount = 0;

        StringBuilder stringBuilder = new StringBuilder();
        DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();

        Cursor cursor = dbHandler.getTruckTotalCollection(user_date, user_shift, txnType);

        if (cursor.moveToFirst()) {
            do {

                if (cursor.getString(cursor.getColumnIndex("salesTxnSubType")).equals(Util.SALES_TXN_SUBTYPE_COB)) {
                    cob_avg_amount = cob_avg_amount + Double.valueOf(cursor.getString(cursor.getColumnIndex("amount")));
                    cob_avg_quantity = cob_avg_quantity + Double.valueOf(cursor.getString(cursor.getColumnIndex("quantity")));
                }
                if (cursor.getString(cursor.getColumnIndex("milkType")).equals("COW")) {

                    cow_avg_quantity = cow_avg_quantity + Double.valueOf(cursor.getString(cursor.getColumnIndex("quantity")));
                    cow_avg_amount = cow_avg_amount + Double.valueOf(cursor.getString(cursor.getColumnIndex("amount")));
                    stringBuilder.append("COWsnf \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("snf")));
                    stringBuilder.append("COWfat \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("fat")));
                    cow_avg_fat = cow_avg_fat + (Double.valueOf(cursor.getString(cursor.getColumnIndex("fat"))) * Double.valueOf(cursor.getString(cursor.getColumnIndex("quantity")))) / 100;
                    cow_avg_snf = cow_avg_snf + (Double.valueOf(cursor.getString(cursor.getColumnIndex("snf"))) * Double.valueOf(cursor.getString(cursor.getColumnIndex("quantity")))) / 100;


                }
                if (cursor.getString(cursor.getColumnIndex("milkType")).equals("BUFFALO")) {

                    buf_avg_quantity = buf_avg_quantity + Double.valueOf(cursor.getString(cursor.getColumnIndex("quantity")));
                    buf_avg_amount = buf_avg_amount + Double.valueOf(cursor.getString(cursor.getColumnIndex("amount")));
                    stringBuilder.append("BUFsnf \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("snf")));
                    stringBuilder.append("BUFfat \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("fat")));
                    buf_avg_fat = buf_avg_fat + (Double.valueOf(cursor.getString(cursor.getColumnIndex("fat"))) * Double.valueOf(cursor.getString(cursor.getColumnIndex("quantity")))) / 100;
                    buf_avg_snf = buf_avg_snf + (Double.valueOf(cursor.getString(cursor.getColumnIndex("snf"))) * Double.valueOf(cursor.getString(cursor.getColumnIndex("quantity")))) / 100;


                }
                if (cursor.getString(cursor.getColumnIndex("milkType")).equals("MIXED")) {

                    mix_avg_quantity = mix_avg_quantity + Double.valueOf(cursor.getString(cursor.getColumnIndex("quantity")));
                    mix_avg_amount = mix_avg_amount + Double.valueOf(cursor.getString(cursor.getColumnIndex("amount")));
                    stringBuilder.append("MXDsnf \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("snf")));
                    stringBuilder.append("MXDfat \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("fat")));
                    mix_avg_fat = mix_avg_fat + (Double.valueOf(cursor.getString(cursor.getColumnIndex("fat"))) * Double.valueOf(cursor.getString(cursor.getColumnIndex("quantity")))) / 100;
                    mix_avg_snf = mix_avg_snf + (Double.valueOf(cursor.getString(cursor.getColumnIndex("snf"))) * Double.valueOf(cursor.getString(cursor.getColumnIndex("quantity")))) / 100;

                }

            } while (cursor.moveToNext());
        }
        DecimalFormat dFormat = new DecimalFormat("#0.00");
        String avgQuantity = dFormat.format(cow_avg_quantity + buf_avg_quantity + mix_avg_quantity);
        String avgAmountData = dFormat.format(cow_avg_amount + buf_avg_amount + mix_avg_amount);
        return avgQuantity + "," + avgAmountData;
    }

    // yyy @ set display  in ui for local sales
    public void gotoLocalSalesReport() {
        setLocalSalesReport(getApplicationContext(), user_date, user_shift, "SALES");
        // System.out.print("==============KUMAR" + stringBuilder.toString());
        row2_text2.setText("" + decimalFormat.format(cow_avg_quantity));
        row2_text3.setText("" + decimalFormat.format(cow_avg_amount / cow_avg_quantity));// average rate
        if (cow_avg_fat == 0.00) {
            row2_text4.setText("NA"); // avg fat
            row2_text5.setText("NA");
        } else {
            row2_text4.setText("" + decimalFormat.format((cow_avg_fat * 100) / cow_avg_quantity)); // avg fat
            row2_text5.setText("" + decimalFormat.format((cow_avg_snf * 100) / cow_avg_quantity)); // avg snf
        }
        row2_text7.setText("" + decimalFormat.format(cow_avg_amount));
        ///
        row3_text2.setText("" + decimalFormat.format(buf_avg_quantity));
        row3_text3.setText("" + decimalFormat.format(buf_avg_amount / buf_avg_quantity));// average rate
        if (buf_avg_fat == 0.00) {
            row3_text4.setText("NA"); // avg fat
            row3_text5.setText("NA"); // avg snf
        } else {
            row3_text4.setText("" + decimalFormat.format((buf_avg_fat * 100) / buf_avg_quantity)); // avg fat
            row3_text5.setText("" + decimalFormat.format((buf_avg_snf * 100) / buf_avg_quantity)); // avg snf
        }
        row3_text7.setText("" + decimalFormat.format(buf_avg_amount));
        ///
        row4_text2.setText("" + decimalFormat.format(mix_avg_quantity));
        row4_text3.setText("" + decimalFormat.format(mix_avg_amount / mix_avg_quantity));// average rate
        if (mix_avg_fat == 0.00) {
            row4_text4.setText("NA"); // avg fat
            row4_text5.setText("NA"); // avg snf
        } else {
            row4_text4.setText("" + decimalFormat.format((mix_avg_fat * 100) / mix_avg_quantity)); // avg fat
            row4_text5.setText("" + decimalFormat.format((mix_avg_snf * 100) / mix_avg_quantity)); // avg snf
        }
        row4_text7.setText("" + decimalFormat.format(mix_avg_amount));

        summary_quan1.setText("Total Milk: " + decimalFormat.format(cow_avg_quantity + buf_avg_quantity + mix_avg_quantity) + " lts");
        summary_rate1.setText("Total Amount: Rs. " + decimalFormat.format(cow_avg_amount + buf_avg_amount + mix_avg_amount));
    }

    // yyy@ setting Toatal Collection Report Data for View
    public void gotoTotalCollectionReport() {
        setTotalCollectionReportData(getApplicationContext(), user_date, user_shift);
        System.out.print("==============KUMAR" + stringBuilder.toString());
        row2_text2.setText("" + decimalFormat.format(cow_avg_quantity));
        row2_text3.setText("" + decimalFormat.format(cow_avg_amount / cow_avg_quantity));// average rate
        row2_text4.setText("" + decimalFormat.format((cow_avg_fat * 100) / cow_avg_quantity)); // avg fat
        row2_text5.setText("" + decimalFormat.format((cow_avg_snf * 100) / cow_avg_quantity)); // avg snf
        row2_text7.setText("" + decimalFormat.format(cow_avg_amount));
        ///
        row3_text2.setText("" + decimalFormat.format(buf_avg_quantity));
        row3_text3.setText("" + decimalFormat.format(buf_avg_amount / buf_avg_quantity));// average rate
        row3_text4.setText("" + decimalFormat.format((buf_avg_fat * 100) / buf_avg_quantity)); // avg fat
        row3_text5.setText("" + decimalFormat.format((buf_avg_snf * 100) / buf_avg_quantity)); // avg snf
        row3_text7.setText("" + decimalFormat.format(buf_avg_amount));
        ///
        row4_text2.setText("" + decimalFormat.format(mix_avg_quantity));
        row4_text3.setText("" + decimalFormat.format(mix_avg_amount / mix_avg_quantity));// average rate
        row4_text4.setText("" + decimalFormat.format((mix_avg_fat * 100) / mix_avg_quantity)); // avg fat
        row4_text5.setText("" + decimalFormat.format((mix_avg_snf * 100) / mix_avg_quantity)); // avg snf
        row4_text7.setText("" + decimalFormat.format(mix_avg_amount));

        summary_quan1.setText("Total Milk: " + decimalFormat.format(cow_avg_quantity + buf_avg_quantity + mix_avg_quantity) + " lts");
        summary_rate1.setText("Total Collection: Rs. " + decimalFormat.format(cow_avg_amount + buf_avg_amount + mix_avg_amount));
    }

    // yyy@ setting Toatal Collection Report Data from database
    public String setTotalCollectionReportData(Context context, String user_date, String user_shift) {
        DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();
        CollectionRecordDao collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);

        List<ReportEntity> reportEntities = collectionRecordDao.findAllByDateShiftAndMilkType(user_date, user_shift, null);
        for (int i = 0, len = reportEntities.size(); i < len; i++) {
            ReportEntity reportEntity = reportEntities.get(i);
            if (reportEntity.status.equalsIgnoreCase("Accept") && reportEntity.collectionType.equalsIgnoreCase(Util.RATECHART_TYPE_COLLECTION)) {
                if (reportEntity.milkType.equalsIgnoreCase("COW")) {

                    cow_avg_quantity = cow_avg_quantity + Double.valueOf(reportEntity.quantity);
                    cow_avg_amount = cow_avg_amount + Double.valueOf(reportEntity.amount);
                    cow_avg_fat = cow_avg_fat + (Double.valueOf(reportEntity.fat) * Double.valueOf(reportEntity.quantity)) / 100;
                    cow_avg_snf = cow_avg_snf + (Double.valueOf(reportEntity.snf) * Double.valueOf(reportEntity.quantity)) / 100;
                }
                if (reportEntity.milkType.equalsIgnoreCase("BUFFALO")) {

                    buf_avg_quantity = buf_avg_quantity + Double.valueOf(reportEntity.quantity);
                    buf_avg_amount = buf_avg_amount + Double.valueOf(reportEntity.amount);
                    buf_avg_fat = buf_avg_fat + (Double.valueOf(reportEntity.fat) * Double.valueOf(reportEntity.quantity)) / 100;
                    buf_avg_snf = buf_avg_snf + (Double.valueOf(reportEntity.snf) * Double.valueOf(reportEntity.quantity)) / 100;
                }
                if (reportEntity.milkType.equalsIgnoreCase("MIXED")) {

                    mix_avg_quantity = mix_avg_quantity + Double.valueOf(reportEntity.quantity);
                    mix_avg_amount = mix_avg_amount + Double.valueOf(reportEntity.amount);
                    mix_avg_fat = mix_avg_fat + (Double.valueOf(reportEntity.fat) * Double.valueOf(reportEntity.quantity)) / 100;
                    mix_avg_snf = mix_avg_snf + (Double.valueOf(reportEntity.snf) * Double.valueOf(reportEntity.quantity)) / 100;
                }
            }
        }
        DecimalFormat dFormat = new DecimalFormat("#0.00");
        String avgQuantity = dFormat.format(cow_avg_quantity + buf_avg_quantity + mix_avg_quantity);
        String avgAmountData = dFormat.format(cow_avg_amount + buf_avg_amount + mix_avg_amount);
        return avgQuantity + "," + avgAmountData;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void parseCursorDataFromSalesRecordTable(Cursor cursor) {
        StringBuilder stringBuilder = new StringBuilder();
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                do {
                    /*stringBuilder.append("salesId \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("salesId")));
                    stringBuilder.append("name \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("name")));
                    stringBuilder.append("snf \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("snf")));
                    stringBuilder.append("fat \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("fat")));
                    stringBuilder.append("shift \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("shift")));
                    stringBuilder.append("amount \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("amount")));
                    stringBuilder.append("Quantity\n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("quantity")));

                    stringBuilder.append("milkType \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("milkType")));*/
                    stringBuilder.append("date\n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("date")));
                    stringBuilder.append("rate \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("rate")));
                   /* stringBuilder.append("socId \n");
                    SessionManager session = new SessionManager(getApplicationContext());
                   String socid =  session.getCollectionID();
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("socID")));
                    stringBuilder.append("time \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("time")));
                    stringBuilder.append("miliTime\n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("timeMillis")));
                    stringBuilder.append("txnType \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("salesTxnType")));
                    stringBuilder.append("txnSubType \n");
                    stringBuilder.append(cursor.getString(cursor.getColumnIndex("salesTxnSubType")));*/

                } while (cursor.moveToNext());
            }
            Toast.makeText(getApplicationContext(), stringBuilder.toString(), Toast.LENGTH_LONG).show();
            //  System.out.print("==============yyy" + stringBuilder.toString());
        } else {
            Toast.makeText(getApplicationContext(), "cursor null nodata", Toast.LENGTH_LONG).show();
        }
    }
}
