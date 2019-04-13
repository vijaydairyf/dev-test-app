package com.devapp.smartcc.test;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.rdu.RduFactory;
import com.devapp.devmain.rdu.RduManager;
import com.devapp.devmain.server.AmcuConfig;
import com.devApp.R;

import java.util.Random;

public class TestRduActivity extends AppCompatActivity {
    Button reset, start;
    boolean isEight, isStart;
    TextView status;
    TextView codeTv, qtyTv, fatTv, snfTv, rateTv, amtTv, awmTv, milkTypeTv;
    RduManager rduManager;
    Handler handler;
    Random random = new Random();
    ReportEntity reportEntity = new ReportEntity();
    private AmcuConfig amcuConfig;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (isStart && rduManager != null) {
                rduManager.openConnection();

                reportEntity = getReportEntity(reportEntity);
                rduManager.displayReport(reportEntity, amcuConfig.getEnableIncentiveRDU());
                rduManager.closePort();
                if (isStart) {
                    handler.postDelayed(runnable, 1000 * 20);
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_rdu);
        reset = (Button) findViewById(R.id.bt_reset);
        start = (Button) findViewById(R.id.bt_start);
        status = (TextView) findViewById(R.id.tv_status);
        codeTv = (TextView) findViewById(R.id.tv_id);
        qtyTv = (TextView) findViewById(R.id.tv_qty);
        fatTv = (TextView) findViewById(R.id.tv_fat);
        snfTv = (TextView) findViewById(R.id.tv_snf);
        rateTv = (TextView) findViewById(R.id.tv_rate);
        amtTv = (TextView) findViewById(R.id.tv_amt);
        awmTv = (TextView) findViewById(R.id.tv_awm);
        milkTypeTv = (TextView) findViewById(R.id.tv_milkType);
        amcuConfig = AmcuConfig.getInstance();
        rduManager = RduFactory.getRdu(amcuConfig.getRDU(), TestRduActivity.this);
        if (rduManager != null) {
//            rduManager.startRduConnection(allDeviceData);
        } else {
            Toast.makeText(TestRduActivity.this,
                    "Invalid RDU configured!", Toast.LENGTH_SHORT).show();
        }

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rduManager != null) {
                    reset();
                } else {
                    Toast.makeText(TestRduActivity.this,
                            "Invalid RDU configured!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rduManager != null) {
                    if (!isStart) {
                        isStart = true;
                        start.setText("Stop");
                        status.setText("Test Running");
                        reset.setEnabled(false);
                        start();
                    } else {
                        isStart = false;
                        start.setText("Start");
                        status.setText("Test not Running");
                        reset.setEnabled(true);
                    }

                } else {
                    Toast.makeText(TestRduActivity.this,
                            "Invalid RDU configured!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void start() {
        if (isStart) {
            handler = new Handler();
            handler.post(runnable);
        }
    }

    private void reset() {
        if (rduManager != null) {
            rduManager.openConnection();
            rduManager.resetRdu(isEight);
            isEight = !isEight;
            if (isEight) {
                reset.setText("Eight");
            } else {
                reset.setText("Zero");
            }
            rduManager.closePort();
        }
    }

    private ReportEntity getReportEntity(ReportEntity reportEntity) {

        double fat = random.nextInt(100) + random.nextFloat();
        double snf = random.nextInt(100) + random.nextFloat();
        double rate = random.nextInt(100) + random.nextFloat();
        double awm = random.nextInt(100) + random.nextFloat();
        long farmerId = random.nextInt(99999) + 1;
        double qty = random.nextInt(1000) + random.nextFloat();
        double amt = random.nextInt(10000) + random.nextFloat();
        int cattleType = random.nextInt(3);
        reportEntity.farmerId = String.valueOf(farmerId);
        reportEntity.amount = amt;
        reportEntity.rate = rate;
        reportEntity.fat = fat;
        reportEntity.snf = snf;
        reportEntity.quantity = qty;
        reportEntity.awm = awm;
        if (cattleType == 0) {
            reportEntity.milkType = "COW";
        } else if (cattleType == 1) {
            reportEntity.milkType = "BUFFALO";
        } else if (cattleType == 2) {
            reportEntity.milkType = "MIXED";
        } else {
            reportEntity.milkType = "COW";
        }
        codeTv.setText("ID   : " + reportEntity.farmerId);
        qtyTv.setText("Qty  : " + reportEntity.quantity);
        fatTv.setText("Fat  : " + reportEntity.fat);
        snfTv.setText("Snf  : " + reportEntity.snf);
        rateTv.setText("Rate : " + reportEntity.rate);
        amtTv.setText("Amt  : " + reportEntity.amount);
        awmTv.setText("Awm  : " + reportEntity.awm);
        milkTypeTv.setText("Milk Type : " + reportEntity.milkType);
        reportEntity.bonus = 0;
        return reportEntity;

    }

    @Override
    protected void onStop() {
        super.onStop();
        rduManager.closePort();
    }
}
