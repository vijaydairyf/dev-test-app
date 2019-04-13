package com.devapp.devmain.tabs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.devapp.devmain.server.SessionManager;

public class AlertActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Critical battery");

        builder.setMessage(
                "Phone battery is " + new SessionManager(this).getBatstatus()
                        + " remaining." + "Please charge the mobile")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                finish();
                            }
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
