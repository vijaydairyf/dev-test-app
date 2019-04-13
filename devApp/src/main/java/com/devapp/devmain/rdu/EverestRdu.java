package com.devapp.devmain.rdu;


import com.devapp.devmain.entity.ReportEntity;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by x on 28/12/17.
 */

public class EverestRdu implements Rdu {
    @Override
    public String getReportMsg(ReportEntity reportEntity, boolean displayBonus, boolean roundOffqty) {
        String RDUMsg, milkType = "C";

        String farmer = "0000", Qty = "00000.00", fat = "00.0", snf = "00.0",
                rate = "00.00", amt = "0000.00", addedWater = "00.00";

        DecimalFormat decimalFormat3 = new DecimalFormat("0000");

        String farmerId = reportEntity.farmerId;
        try {
            Integer.parseInt(farmerId);
            farmer = ("0000" + farmerId).substring(farmerId.length());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        decimalFormat3 = new DecimalFormat("00.0");

        fat = decimalFormat3.format(reportEntity.getRDUFat());

        // sen clr or snf for rdu display

        snf = decimalFormat3.format(reportEntity.getRDUSnf());

        decimalFormat3 = new DecimalFormat("00000.00");
        if (!roundOffqty) {
            decimalFormat3.setRoundingMode(RoundingMode.DOWN);
        }
        Qty = decimalFormat3.format(reportEntity.getRDUQuantity());

        decimalFormat3 = new DecimalFormat("0000.00");
        amt = decimalFormat3.format(reportEntity.getRDUAmount());
        //  amt = decimalFormat3.format(Double.parseDouble(fullMsg.get(11)));

        decimalFormat3 = new DecimalFormat("00.00");
        rate = decimalFormat3.format(reportEntity.getRDURate());
        addedWater = decimalFormat3.format(reportEntity.getRDUAwm());
        if (reportEntity.milkType.equalsIgnoreCase("COW")) {
            milkType = "C";
        } else if (reportEntity.milkType.equalsIgnoreCase("BUFFALO")) {
            milkType = "B";
        } else if (reportEntity.milkType.equalsIgnoreCase("MIXED")) {
            milkType = "M";
        } else {
            milkType = "C";
        }

        RDUMsg = "(" + farmer + Qty + milkType + fat + snf + addedWater + amt
                + ")";

        return RDUMsg;
    }

    @Override
    public String getResetMsg(boolean isEight) {
        String RDUMsg;
        byte[] start = {0x02};
        byte[] end = {0x03};
        String milkType = "C";

        if (isEight) {
            String farmer = "8888", Qty = "88888.88", fat = "88.8", snf = "88.8", rate = "88.88", amt = "8888.88", addedWater = "88.88";
            RDUMsg = "(" + farmer + Qty + milkType + fat + snf + addedWater
                    + amt + ")";
        } else {
            String farmer = "0000", Qty = "00000.00", fat = "00.0", snf = "00.0", rate = "00.00", amt = "0000.00", addedWater = "00.00";

            RDUMsg = "(" + farmer + Qty + milkType + fat + snf + addedWater
                    + amt + ")";
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
