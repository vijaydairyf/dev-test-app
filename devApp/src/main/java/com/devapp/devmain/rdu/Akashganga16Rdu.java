package com.devapp.devmain.rdu;


import com.devapp.devmain.entity.ReportEntity;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by x on 28/12/17.
 */

public class Akashganga16Rdu implements Rdu {
    @Override
    public String getReportMsg(ReportEntity reportEntity, boolean displayBonus, boolean roundOffqty) {
        String RDUMsg, milkType = "C";

        String farmer = "000000", Qty = "00000.00", fat = "00.0", snf = "00.0",
                rate = "00.00", amt = "0000.00", addedWater = "00.00";

        DecimalFormat decimalFormat3 = new DecimalFormat("000000");
        String farmerId = reportEntity.farmerId;
        try {
            Integer.parseInt(farmerId);
            farmer = ("000000" + farmerId).substring(farmerId.length());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        decimalFormat3 = new DecimalFormat("00.0");

        fat = decimalFormat3.format(reportEntity.getRDUFat());
        snf = String.valueOf(reportEntity.getRDUSnf());
        snf = decimalFormat3.format(Double.parseDouble(snf));

        decimalFormat3 = new DecimalFormat("000.0");
        if (!roundOffqty) {
            decimalFormat3.setRoundingMode(RoundingMode.DOWN);
        }
        Qty = decimalFormat3.format(reportEntity.getRDUQuantity());


        decimalFormat3 = new DecimalFormat("0000.00");
        String temp_amount = String.valueOf(reportEntity.getRDUAmount());
        amt = decimalFormat3.format(Double.parseDouble(temp_amount));
        //  amt = decimalFormat3.format(Double.parseDouble(fullMsg.get(11)));

        fat = fat.replace(".", "");
        snf = snf.replace(".", "");
        Qty = Qty.replace(".", "");


        RDUMsg = "$%" + farmer + fat + Qty + snf + "\r";

        return RDUMsg;
    }

    @Override
    public String getResetMsg(boolean isEight) {
        String RDUMsg;
        byte[] start = {0x02};
        byte[] end = {0x03};
        String milkType = "C";

        if (isEight) {
            String farmer = "888888", Qty = "8888", fat = "888", snf = "888", rate = "888888",
                    amt = "888888", addedWater = "888";
            RDUMsg = "$%" + farmer + fat + Qty + snf + "\r";
        } else {

            String farmer = "000000", Qty = "0000", fat = "000", snf = "000", rate = "000000",
                    amt = "000000", addedWater = "000";
            RDUMsg = "$%" + farmer + fat + Qty + snf + "\r";
        }

        return RDUMsg;
    }

    @Override
    public String getSetDeviceIdMsg(String deviceId) {
        return null;
    }

    @Override
    public String getChangePasswordMsg(String oldPass, String newPass) {
        return null;
    }

    @Override
    public String getResetPasswordMsg() {
        return null;
    }

    @Override
    public void setRduListener(RduListener listener) {

    }
}
