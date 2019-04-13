package com.devapp.kmfcommon;

import android.app.Activity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.main.CustomRateChartUtil;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devApp.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class ShowMyRateChart extends Activity {
    String ratechartName, startShift, endShift;
    long startDate, endDate;
    double startSnf, endSnf, startFat, endFat, kgFAT, kgSNF;
    TextView tv, kgfat, kgsnf;
    ListView list;
    AmcuConfig amcuConfig;
    String createDate;
    private ArrayAdapter<String> adapter;
    private List liste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_rate_chart);
        amcuConfig = AmcuConfig.getInstance();
        list = (ListView) findViewById(R.id.list);
        tv = (TextView) findViewById(R.id.text);
        kgfat = (TextView) findViewById(R.id.kgfat);
        kgsnf = (TextView) findViewById(R.id.kgsnf);

        liste = new ArrayList();
        ratechartName = getIntent().getStringExtra("RN");
        if (ratechartName == null) {
            Toast.makeText(ShowMyRateChart.this, "OOps..Something went wrong !!", Toast.LENGTH_SHORT).show();
            finish();
        }
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        Cursor cursor = dbh.getSelectedMyRateChartList(ratechartName);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    startFat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_FAT)));
                    endFat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_FAT)));
                    startSnf = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_SNF)));
                    endSnf = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_SNF)));
                    kgFAT = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_KG_FAT_RATE)));
                    kgSNF = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_KG_SNF_RATE)));
                    kgfat.setText("( Rs." + String.valueOf(kgFAT) + "/Kg Fat");
                    kgsnf.setText("Rs." + String.valueOf(kgSNF) + "/Kg Snf )");
                    createDate = String.valueOf(cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_MILIDATE)));
                    tv.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_NAME)));
                    startDate = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_DATE));
                    endDate = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_DATE));
                    startShift = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_SHIFT));
                    endShift = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_SHIFT));
                } while (cursor.moveToNext());
            }
        }

        gotoFatPointer(startFat, endFat, startSnf, endSnf);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, liste);
        list.setAdapter(adapter);
    }

    public void gotoFatPointer(double startFat, double endFat, double startSnf, double endSnf) {
        NumberFormat numberFormat = new DecimalFormat("#0.0#");
        double pointer = 0.1, amount = 0.0, pointer2 = 0.1;

        String temp_endsnf;
   /*  endFat = endFat;
     endSnf = endSnf;
     temp_endsnf = numberFormat.format(endSnf);
     endSnf = Double.parseDouble(temp_endsnf);*/
        //for(double i=a;i<=b;i=Double.parseDouble(format.format(i))+0.1){
        for (double i = startFat; i <= endFat; i = Double.parseDouble(numberFormat.format(i)) + 0.1) {
            for (double j = startSnf; j <= endSnf; j = Double.parseDouble(numberFormat.format(j)) + 0.1) {
                amount = (i * kgFAT + j * kgSNF) / 100;
                //   Toast.makeText(getApplicationContext(),"FAT:: "+numberFormat.format(i)+ " SNF:: " + numberFormat.format(j), Toast.LENGTH_SHORT).show();
                liste.add("            FAT :: " + numberFormat.format(i) + "            SNF ::  " + numberFormat.format(j) + "            RATE :: " + numberFormat.format(amount));
            }
        }
    }

    public void gotoDelete(View view) {
      /* if((saveSession.getCollectionEndShift()|| Util.isThisNewShift(ShowMyRateChart.this))|| (!saveSession.getCurrentSessionStartedWithCow() && !saveSession.getCurrentSessionStartedWithBuff() && !saveSession.getCurrentSessionStartedWithMix())) {
           CustomRateChartUtil.getInstance(ShowMyRateChart.this).writeMyRatechartLogs(ratechartName, startDate, endDate, startShift, endShift, ShowMyRateChart.this, "DELETE");
           Toast.makeText(ShowMyRateChart.this, "Ratechart deleted sucessfully", Toast.LENGTH_LONG).show();
           DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance(ShowMyRateChart.this);
           dbh.deleteMyExpireRateChart(ratechartName);
           finish();

       }else{
           openDeleteDialog();
       }*/
        openDeleteDialog();
    }

    public void openDeleteDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure want to delete ? \n Please click ok and do end shift.");

        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                CustomRateChartUtil.getInstance(ShowMyRateChart.this).writeMyRatechartLogs(ratechartName, startDate, endDate, startShift, endShift, ShowMyRateChart.this, "DELETE", System.currentTimeMillis());
                Toast.makeText(ShowMyRateChart.this, "Ratechart deleted sucessfully", Toast.LENGTH_LONG).show();
                DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
                dbh.deleteMyExpireRateChart(ratechartName);
                finish();

            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public String converOneDecimalPlace(String input) {
        String output;
        DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
        output = decimalFormatFS.format(Double.parseDouble(input));
        return output;
    }
}
