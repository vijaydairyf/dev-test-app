package com.devapp.devmain.rdu;


import com.devapp.devmain.entity.ReportEntity;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by x on 28/12/17.
 */

public class Smart2Rdu implements Rdu {
    @Override
    public String getReportMsg(ReportEntity reportEntity, boolean displayBonus, boolean roundOffqty) {
        String RDUMsg;
        String temp_bonus = String.valueOf(reportEntity.bonus);
        String farmer = "0000", Qty = "00.00", fat = "00.00", snf = "00.00", rate = "00.00", amt = "0000.00";

        DecimalFormat decimalFormat3 = new DecimalFormat("0000");


        String farmerId = reportEntity.farmerId;
        try {
            Integer.parseInt(farmerId);
            farmer = ("0000" + farmerId).substring(farmerId.length());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        decimalFormat3 = new DecimalFormat("00.00");
        if (!roundOffqty) {
            decimalFormat3.setRoundingMode(RoundingMode.DOWN);
        }
        Qty = decimalFormat3.format(reportEntity.getRDUQuantity());

        decimalFormat3 = new DecimalFormat("00.00");
        fat = decimalFormat3.format(reportEntity.getRDUFat());
        snf = decimalFormat3.format(reportEntity.getRDUSnf());

        rate = decimalFormat3.format(reportEntity.getRDURate());
        String bonusAmount = "00.00";
        try {
            bonusAmount = decimalFormat3.format(Double.parseDouble(temp_bonus));
        } catch (Exception e) {
            e.printStackTrace();
        }

        decimalFormat3 = new DecimalFormat("0000.00");

        String temp_amount = String.valueOf(reportEntity.getRDUAmount());
        amt = decimalFormat3.format(Double.parseDouble(temp_amount));
        //amt = decimalFormat3.format(Double.parseDouble(fullMsg.get(11)));

        RDUMsg = "#@" + farmer + "," + Qty + "," + fat + "," + snf + "," + rate
                + "," + amt;
        if (displayBonus) {
            return "#@" + farmer + "," + Qty + "," + fat + "," + snf + "," + bonusAmount
                    + "," + amt;
        } else {
            return "#@" + farmer + "," + Qty + "," + fat + "," + snf + "," + rate
                    + "," + amt;
        }
    }

    @Override
    public String getResetMsg(boolean isEight) {
        String RDUMsg;
        if (isEight) {

            String farmer = "88888", Qty = "88.88", fat = "88.88", snf = "88.88", rate = "88.88", amt = "8888.88";
            RDUMsg = "#@" + farmer + "," + Qty + "," + fat + "," + snf + ","
                    + rate + "," + amt;
        } else {
            String farmer = "00000", Qty = "00.00", fat = "00.00", snf = "00.00", rate = "00.00", amt = "0000.00";

            RDUMsg = "#@" + farmer + "," + Qty + "," + fat + "," + snf + ","
                    + rate + "," + amt;
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
