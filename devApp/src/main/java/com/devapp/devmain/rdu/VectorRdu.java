package com.devapp.devmain.rdu;

import com.devapp.devmain.entity.ReportEntity;

import java.text.DecimalFormat;

public class VectorRdu implements Rdu {

    /*
    * Data Format: $A00000000000000000000000000000000B
1. $A:	      Start of Data
2. 0000:      Member code	[0000]
3. 0000:      CLR			[00.00]
4. 0000:      FAT			[00.00]
5. 00000:    RATE			[000.00]
6. 0000:      SNF			[00.00]
7. 00000:    QTY			[000.00]
8. 000000:  Amount		[0000.00]

    * */


    @Override
    public String getReportMsg(ReportEntity reportEntity, boolean displayBonus, boolean roundOffqty) {
        String rduMsg, milkType = "0", code = "0000", clr = "00.00", fat = "00.00", rate = "000.00",
                snf = "00.00", qty = "000.00", amt = "0000.00";
        DecimalFormat df = new DecimalFormat("0000");
        String farmerId = reportEntity.farmerId;
        code = farmerId;
        try {
            Integer.parseInt(farmerId);
            code = ("0000" + code).substring(farmerId.length());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            code = "0000";
        }
        df = new DecimalFormat("00.00");
        clr = df.format(reportEntity.getRDUClr());
        clr = clr.replace(".", "");
        clr = ("0000" + clr).substring(clr.length());

        fat = df.format(reportEntity.getRDUFat());
        fat = fat.replace(".", "");
        fat = ("0000" + fat).substring(fat.length());

        snf = df.format(reportEntity.getRDUSnf());
        snf = snf.replace(".", "");
        snf = ("0000" + snf).substring(snf.length());

        df = new DecimalFormat("000.00");
        rate = df.format(reportEntity.getRDURate());
        rate = rate.replace(".", "");
        rate = ("00000" + rate).substring(rate.length());

        qty = df.format(reportEntity.getRDUQuantity());
        qty = qty.replace(".", "");
        qty = ("00000" + qty).substring(qty.length());

        df = new DecimalFormat("0000.00");
        amt = df.format(reportEntity.getRDUAmount());
        amt = amt.replace(".", "");
        // amt = ("000000"+amt).substring(amt.length());

        if (reportEntity.milkType.equalsIgnoreCase("COW")) {
            milkType = "0";
        } else if (reportEntity.milkType.equalsIgnoreCase("BUFFALO")) {
            milkType = "1";
        } else if (reportEntity.milkType.equalsIgnoreCase("MIXED")) {
            milkType = "2";
        } else {
            milkType = "0";
        }

        rduMsg = "$A" + code + clr + fat + rate + snf + qty + milkType + amt;

        return rduMsg;
    }

    @Override
    public String getResetMsg(boolean isEight) {
        if (isEight) {
            return "$A888888888888888888888888888888888";
        } else {
            return "$A000000000000000000000000000000000";
        }
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
