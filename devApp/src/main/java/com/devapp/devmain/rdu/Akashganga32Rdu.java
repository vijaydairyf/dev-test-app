package com.devapp.devmain.rdu;


import com.devapp.devmain.entity.ReportEntity;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by x on 28/12/17.
 */

public class Akashganga32Rdu implements Rdu {
    @Override
    public String getReportMsg(ReportEntity reportEntity, boolean displayBonus, boolean roundOffqty) {
        String RDUMsg, milkType = "C";

        String farmer = "0000", qty = "000.0", fat = "00.00", snf = "00.00",
                rate = "0000.00", amt = "0000.00", addedWater = "00.0";

        DecimalFormat decimalFormat3 = new DecimalFormat("000000");
        String farmerId = reportEntity.farmerId;
        farmer = farmerId;
        try {
            Integer.parseInt(farmerId);
            if (farmerId.length() > 4) {
                farmer = farmerId.substring((farmerId.length() - 4), farmerId.length());
            } else if (farmerId.length() < 4) {
                farmer = String.format("%04d", Integer.parseInt(farmerId));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            farmer = "0000";
        }

        decimalFormat3 = new DecimalFormat("00.00");

        fat = decimalFormat3.format(reportEntity.getRDUFat()).replace(".", "");
        snf = String.valueOf(reportEntity.getRDUSnf());
        snf = decimalFormat3.format(reportEntity.getRDUSnf()).replace(".", "");
        fat = ("0000" + fat).substring(fat.length());
        snf = ("0000" + snf).substring(snf.length());


        decimalFormat3 = new DecimalFormat("000.0");
        if (!roundOffqty) {
            decimalFormat3.setRoundingMode(RoundingMode.DOWN);
        }
        qty = decimalFormat3.format(reportEntity.getRDUQuantity()).replace(".", "");
        qty = ("0000" + qty).substring(qty.length());

        String temp_amount = String.valueOf(reportEntity.getRDUAmount());
        decimalFormat3 = new DecimalFormat("0000.00");
        amt = decimalFormat3.format(Double.parseDouble(temp_amount));
        amt = amt.replace(".", "");
        amt = ("000000" + amt).substring(amt.length());

        //  amt = decimalFormat3.format(Double.parseDouble(fullMsg.get(11)));

        decimalFormat3 = new DecimalFormat("0000.00");
        rate = decimalFormat3.format(reportEntity.getRDURate()).replace(".", "");
        rate = ("000000" + rate).substring(rate.length());


        decimalFormat3 = new DecimalFormat("00.0");
        decimalFormat3.setRoundingMode(RoundingMode.DOWN);
        addedWater = decimalFormat3.format(reportEntity.getRDUAwm()).replace(".", "");
        addedWater = ("000" + addedWater).substring(addedWater.length());

        if (reportEntity.milkType.equalsIgnoreCase("COW")) {
            milkType = "C";
        } else if (reportEntity.milkType.equalsIgnoreCase("BUFFALO")) {
            milkType = "B";
        } else if (reportEntity.milkType.equalsIgnoreCase("MIXED")) {
            milkType = "M";
        } else {
            milkType = "C";
        }

        RDUMsg = "$%" + farmer + milkType + qty + fat + snf + addedWater + rate + amt + "\r\n";

        return RDUMsg;
    }

    @Override
    public String getResetMsg(boolean isEight) {
        String RDUMsg;
        byte[] start = {0x02};
        byte[] end = {0x03};
        String milkType = "C";
        if (isEight) {
            String farmer = "8888", Qty = "8888", fat = "8888", snf = "8888", rate = "888888", amt = "888888", addedWater = "888";
            milkType = "B";
            RDUMsg = "$%" + farmer + milkType + Qty + fat + snf + addedWater
                    + rate + amt + "\r";
        } else {
            String farmer = "0000", Qty = "0000", fat = "0000", snf = "0000", rate = "000000",
                    amt = "000000", addedWater = "000";
            RDUMsg = "$%" + farmer + milkType + Qty + fat + snf + addedWater
                    + rate + amt + "\r";
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
