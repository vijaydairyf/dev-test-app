package com.devapp.devmain.services;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


/**
 * Created by Upendra on 7/7/2015.
 */


public class SmartAmcuAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onDisabled(Context context, Intent intent) {

//        PolicyManager policyManager=new PolicyManager(context);
//        policyManager.setConfigPassword(context);
//        Toast.makeText(context, "Disable device policy", Toast.LENGTH_SHORT).show();
//        super.onDisabled(context, intent);
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, "Enable device policy");
        super.onEnabled(context, intent);
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {

//        showToast(context,"Disable smartAmcu");
//
        return super.onDisableRequested(context, intent);
//        PolicyManager policyManager = new PolicyManager(context);
//        try {
//
//                Intent activateDeviceAdmin = new Intent(
//                        DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//                activateDeviceAdmin.putExtra(
//                        DevicePolicyManager.EXTRA_DEVICE_ADMIN,
//                        policyManager.getAdminComponent());
//                activateDeviceAdmin
//                        .putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
//                                "After activating admin, you will be able to block application uninstallation.");
//            Intent intentLog=new Intent(context, LoginActivity.class);
//            intentLog.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                context.startActivity(intentLog);
//                LoginActivity.mLoginActivity.startActivityForResult(activateDeviceAdmin,
//                        PolicyManager.DPM_ACTIVATION_REQUEST_CODE);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    @Override
    public void onPasswordChanged(Context context, Intent intent) {
        showToast(context, "Password change");
        super.onPasswordChanged(context, intent);
    }

    public void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT)
                .show();
    }


}
