package com.devapp.devmain.license;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.devapp.devmain.main.SplashActivity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devApp.R;


public class LicenseActivity extends Activity {

    AmcuConfig amcuConfig;
    DatabaseHandler dbh;
    TextView status, reason;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amcuConfig = AmcuConfig.getInstance();
        setContentView(R.layout.activity_license);
        // status = (TextView)findViewById(R.id.status);
        reason = (TextView) findViewById(R.id.reasonText);

        dbh = DatabaseHandler.getDatabaseInstance();
        cursor = dbh.getLicenseData();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    //    status.setText("    Status: "+cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_LICENSE_STATUS_CODE)));
                    reason.setText("    Reason: " + cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_LICENSE_MESSAGE)));
                } while (cursor.moveToNext());
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(0);
    }

    public void gotoSubmit(View view) {
        Intent intent = new Intent(LicenseActivity.this, SplashActivity.class);
        startActivity(intent);
        amcuConfig.setFirstTimeLicenseEntry(true);
        amcuConfig.setSundayLicenseCheck(false);
        amcuConfig.setonCreateDBCall(true);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cursor != null)
            cursor.close();
    }

}
