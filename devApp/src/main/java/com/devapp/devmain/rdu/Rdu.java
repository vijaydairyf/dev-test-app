package com.devapp.devmain.rdu;


import com.devapp.devmain.entity.ReportEntity;

/**
 * Created by x on 28/12/17.
 */

public interface Rdu {
    String getReportMsg(ReportEntity reportEntity, boolean displayBonus, boolean roundOffqty);

    String getResetMsg(boolean isEight);

    String getSetDeviceIdMsg(String deviceId);

    String getChangePasswordMsg(String oldPass, String newPass);

    String getResetPasswordMsg();

    void setRduListener(RduListener listener);

    public interface RduListener {
        String getPassword();
    }

}
