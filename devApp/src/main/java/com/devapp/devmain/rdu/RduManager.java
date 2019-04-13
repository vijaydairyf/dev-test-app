package com.devapp.devmain.rdu;


import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.devapp.devmain.entity.ReportEntity;

/**
 * Created by x on 28/12/17.
 */

public interface RduManager {
    void openConnection();

    void openDeviceConnectionRDU(UsbSerialPort usbPort, int productId);

    void writeOnRDU(String RDUMsg);

    void closePort();

    void displayReport(ReportEntity reportEntity, boolean displayBonus);

    void resetRdu(boolean isEight);

    void setDeviceId(String deviceId);

    void changePassword(String oldPass, String newPass);

    void resetPassword();

    ReportEntity getModifiedReportEnt(ReportEntity reportEnt);

    boolean shouldRoundOffQuantity();

}
