package com.devapp.devmain.rdu;


import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.user.Util;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by x on 28/12/17.
 */

public class DevAppV2Rdu implements Rdu {
    private RduListener listener;

    @Override
    public String getReportMsg(ReportEntity reportEntity, boolean displayBonus, boolean roundOffqty) {
        String temp_bonus = String.valueOf(reportEntity.bonus);
        String farmer = "00000", Qty = "000.00", fat = "00.00", snf = "00.00",
                rate = "00.00", amt = "0000.00", addedWaterR = "0000.00";
        String pass = "1111";
        pass = listener.getPassword();
        DecimalFormat decimalFormat3 = new DecimalFormat("0000");
        String farmerId = reportEntity.farmerId;
        try {
            Integer.parseInt(farmerId);
            farmer = ("00000" + farmerId).substring(farmerId.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        decimalFormat3 = new DecimalFormat("000.00");
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
        try {
            amt = decimalFormat3.format(Double.parseDouble(temp_amount));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        String milkType = "C";
        if (reportEntity.milkType.equalsIgnoreCase("COW")) {
            milkType = "C";
        } else if (reportEntity.milkType.equalsIgnoreCase("BUFFALO")) {
            milkType = "B";
        } else if (reportEntity.milkType.equalsIgnoreCase("MIXED")) {
            milkType = "M";
        } else {
            milkType = "C";
        }
        String message = "";
        if (displayBonus) {
            message = farmer + "S" + Qty + "F" + fat + "R" + snf + "W" + bonusAmount + "A" + amt + "T" + milkType;
        } else {
            message = farmer + "S" + Qty + "F" + fat + "R" + snf + "W" + rate + "A" + amt + "T" + milkType;
        }
        String checksum = getCheckSum(message + "" + pass);
        message = "STXC" + message + "" + checksum + "ETX\r";

        try {
            Util.generateNoteOnSD("StellappsV2RDU", message,
                    DevAppApplication.getAmcuContext(), "smartAmcuReports");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return message;
    }


    @Override
    public String getResetMsg(boolean isEight) {
        String pass = "1111";
        pass = listener.getPassword();
        String message = "";
        if (isEight) {
            String farmer = "88888", Qty = "888.88", fat = "88.88", snf = "88.88",
                    rate = "88.88", amt = "8888.88", milkType = "B";
            message = farmer + "S" + Qty + "F" + fat + "R" + snf + "W" + rate + "A" + amt + "T" + milkType;
        } else {
            String farmer = "00000", Qty = "000.00", fat = "00.00", snf = "00.00",
                    rate = "00.00", amt = "0000.00", milkType = "C";
            message = farmer + "S" + Qty + "F" + fat + "R" + snf + "W" + rate + "A" + amt + "T" + milkType;
        }
        String checksum = getCheckSum(message + "" + pass);
        message = "STXC" + message + "" + checksum + "ETX\r";
        return message;
//        return "STXC12345S004.30F04.10R17.50W10.56A0184.806098ETX\r";
    }

    @Override
    public String getSetDeviceIdMsg(String deviceId) {
        String message = "STXSIDRDU" + deviceId + "ETX\r";
        return message;
    }

    @Override
    public String getChangePasswordMsg(String oldPass, String newPass) {
        String messsage = "STXSP" + oldPass + "" + newPass + "ETX\r";
        return messsage;
    }

    @Override
    public String getResetPasswordMsg() {
        String message = "STXRPETX\r";
        return message;
    }

    @Override
    public void setRduListener(RduListener listener) {
        this.listener = listener;
    }

    private String getCheckSum(String message) {
        byte[] newArray = {0x02, 0x43, 0x03, (byte) 0xff};
        byte[] byteBuff = message.getBytes();
        byte[] buf = new byte[byteBuff.length + newArray.length];
        System.arraycopy(byteBuff, 0, buf, 0, byteBuff.length);

        System.arraycopy(newArray, 0, buf, byteBuff.length, newArray.length);
        int length = buf.length;
        int i = 0;

        long sum = 0;
        long data;

        // Handle all pairs
        while (length > 1) {
            data = (((buf[i] << 8) & 0xFF00) | ((buf[i + 1]) & 0xFF));
            System.out.println(Long.toHexString(data));
            sum += data;
            // 1's complement carry bit correction in 16-bits (detecting sign extension)
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum += 1;
            }

            i += 2;
            length -= 2;
        }

        System.out.println(Long.toHexString(sum));
        // Handle remaining byte in odd length buffers
        if (length > 0) {
            sum += (buf[i] << 8 & 0xFF00);
            // 1's complement carry bit correction in 16-bits (detecting sign extension)
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum += 1;
            }
        }
        System.out.println(Long.toHexString(sum));

        // Final 1's complement value correction to 16-bits
        sum = ~sum;
        sum = sum & 0xFFFF;
        return Long.toHexString(sum).toUpperCase();
    }
}
