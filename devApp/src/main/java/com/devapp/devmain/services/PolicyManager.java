package com.devapp.devmain.services;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.devApp.R;


/**
 * Created by Upendra on 7/7/2015.
 */


public class PolicyManager {

    public static final int DPM_ACTIVATION_REQUEST_CODE = 100;
    public static boolean status;
    AlertDialog alertDialog;
    TextView tvDeviceHeader;
    private Context mContext;
    private DevicePolicyManager mDPM;
    private ComponentName adminComponent;

    public PolicyManager(Context context) {
        // TODO Auto-generated constructor stub
        this.mContext = context;
        mDPM = (DevicePolicyManager) mContext
                .getSystemService(Context.DEVICE_POLICY_SERVICE);
        adminComponent = new ComponentName(mContext.getPackageName(),
                "com.stellapps.smartamcu.services.SmartAmcuAdminReceiver");
    }

    public boolean isAdminActive() {
        return mDPM.isAdminActive(adminComponent);
    }

    public ComponentName getAdminComponent() {
        return adminComponent;
    }

    public void disableAdmin() {
        setConfigPassword(mContext);

    }

    public void setConfigPassword(Context ctx) {


        status = false;
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                ctx);

        LayoutInflater inflater = (LayoutInflater) ctx.getApplicationContext()
                .getSystemService(ctx.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.alert_authentication, null);

        final Button btnSave = (Button) view.findViewById(R.id.btnSubmit);
        btnSave.setText("Save");
        final Button btnCancel = (Button) view.findViewById(R.id.btnCanc);
        btnCancel.setText("Test");

        EditText etDeviceName = (EditText) view.findViewById(R.id.editEmail);
        final EditText etPassword = (EditText) view.findViewById(R.id.editPwd);
        tvDeviceHeader = (TextView) view.findViewById(R.id.txtLogo);
        final EditText etServer = (EditText) view
                .findViewById(R.id.edithyperLink);
        etServer.setVisibility(View.GONE);

        tvDeviceHeader.setText("Authenticate user");

        // etServer.setVisibility(View.GONE);
        etDeviceName.setEnabled(false);
        etDeviceName.setFocusable(false);
        etDeviceName.setVisibility(View.GONE);
        etPassword.requestFocus();
        btnSave.setText("Cancel");
        btnCancel.setText("Enter");

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                alertDialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                status = true;
                mDPM.removeActiveAdmin(adminComponent);

            }
        });

        alertDialog = alertBuilder.create();
        alertDialog.setView(view);
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.show();

    }


}

