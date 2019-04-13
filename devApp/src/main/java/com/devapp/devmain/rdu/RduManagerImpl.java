package com.devapp.devmain.rdu;

import android.content.Context;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

/**
 * Created by x on 28/12/17.
 */

public class RduManagerImpl implements RduManager {

    AmcuConfig amcuConfig;
    SessionManager session;
    Rdu rdu;
    private Device mDevice;
    private Context mContext;
    private Rdu.RduListener rduListener = new Rdu.RduListener() {
        @Override
        public String getPassword() {
            return amcuConfig.getRduPassword();
        }
    };

    public RduManagerImpl(Context context, Rdu rdu, Device device) {
        this.mContext = context;
        session = new SessionManager(mContext);
        amcuConfig = AmcuConfig.getInstance();
        this.rdu = rdu;
        mDevice = device;
        rdu.setRduListener(rduListener);
    }

    @Override
    public void openConnection() {
        if (mDevice != null)
            mDevice.read();
    }

    @Override
    public void openDeviceConnectionRDU(UsbSerialPort usbPort, int productId) {

    }

    @Override
    public void writeOnRDU(String message) {

        if (mDevice != null)
            mDevice.write(message.getBytes());
        try {
//            mDevice.getDeviceEntity().driver.getPorts().get(0).write(message.getBytes(),3000);
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        closePort();
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(300);
                    closePort();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();*/
    }

    @Override
    public void closePort() {
        if (mDevice != null)
            mDevice.unregisterObserver();
    }

    @Override
    public void displayReport(ReportEntity reportEntity, boolean displayBonus) {
        ReportEntity entity = new ReportEntity(reportEntity);
        String message = rdu.getReportMsg(getModifiedReportEnt(entity), displayBonus, shouldRoundOffQuantity());
        writeOnRDU(message);
    }

    @Override
    public void resetRdu(boolean isEight) {
        writeOnRDU(rdu.getResetMsg(isEight));
    }

    @Override
    public void setDeviceId(String deviceId) {
        writeOnRDU(rdu.getSetDeviceIdMsg(deviceId));
    }

    @Override
    public void changePassword(String oldPass, String newPass) {
        writeOnRDU(rdu.getChangePasswordMsg(oldPass, newPass));
    }

    @Override
    public void resetPassword() {
        writeOnRDU(rdu.getResetPasswordMsg());
    }

    @Override
    public ReportEntity getModifiedReportEnt(ReportEntity reportEnt) {

        if (reportEnt.getStatus().equalsIgnoreCase(SmartCCConstants.REJECT)) {
            reportEnt.setQuantity(0.00);
        }

        if (!amcuConfig.getKeyDisplayAmount()) {
            reportEnt.amount = 0;
            reportEnt.bonus = 0;
        }

        if (!amcuConfig.getKeyDisplayQuantity()) {
            reportEnt.quantity = 0;
        }

        if (!amcuConfig.getKeyDisplayRate()) {
            reportEnt.rate = 0;
        }
        reportEnt.snf = getClrOrSnf(reportEnt);
//        To display added water instead of amount
        if (amcuConfig.getIsAddedWater()) {
            reportEnt.amount = reportEnt.awm;
        } else {
            if (amcuConfig.getKeyAllowProteinValue() && amcuConfig.getBonusEnable()) {
                reportEnt.amount = reportEnt.getTotalAmount();

            } else if (amcuConfig.getKeyAllowProteinValue()) {
                reportEnt.amount = reportEnt.getAmountWithIncentive();
            } else {
                reportEnt.amount = Util.getAmount(mContext, reportEnt.getAmountWithBonus(), reportEnt.bonus);
            }
        }


        return reportEnt;
    }

    @Override
    public boolean shouldRoundOffQuantity() {
        if (amcuConfig.getRDUWeightRoundOff()) {
            return true;
        } else {
            return false;
        }
    }

    public double getClrOrSnf(ReportEntity repEntity) {

        double returnSnf = repEntity.snf;

        if ((amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC") && !session.getIsChillingCenter()) ||
                (amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") && session.getIsChillingCenter())) {
          /*  DecimalFormat decimalFormat;
            if (amcuConfig.getClrRoundOffUpto() == 0) {
                decimalFormat = new DecimalFormat("#0");
            } else {
                decimalFormat = new DecimalFormat("#0.0");
            }
*/

            returnSnf = repEntity.getRDUClr();

        } else {
            returnSnf = repEntity.snf;
        }

        return returnSnf;
    }

}
