package com.devapp.devmain.rdu;


import com.devapp.devmain.entity.ReportEntity;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by x on 28/12/17.
 */

public class HatsunRdu implements Rdu {
    @Override
    public String getReportMsg(ReportEntity reportEntity, boolean displayBonus, boolean roundOffqty) {
        String temp_bonus = String.valueOf(reportEntity.bonus);
        byte[] bytearr = {27, 64, 10, 27, 64, 10, 32, 32, 32, 32, 32};

        String byteString = new String(bytearr);

        String farmer = "0000", Qty = "00.00", fat = "00.00", snf = "00.00", rate = "00.00", amt = "0000.00", addedWater = "0000.00";

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
        rate = decimalFormat3.format(reportEntity.getRDURate());
        String bonusAmount = "00.00";
        try {
            bonusAmount = decimalFormat3.format(Double.parseDouble(temp_bonus));
        } catch (Exception e) {
            e.printStackTrace();
        }

        decimalFormat3 = new DecimalFormat("00.0");
        fat = decimalFormat3.format(reportEntity.getRDUFat());
        snf = decimalFormat3.format(reportEntity.getRDUSnf());

        decimalFormat3 = new DecimalFormat("000.00");

        String temp_amount = String.valueOf(reportEntity.getRDUAmount());
        amt = decimalFormat3.format(Double.parseDouble(temp_amount));
        //amt = decimalFormat3.format(Double.parseDouble(fullMsg.get(11)));

        addedWater = decimalFormat3.format(reportEntity.getRDUAwm());

        String fatSNFAWMFormat = "%13.1f";
        String qtyFormat = "%8.2f";
        String rateFormat = "%6.2f";

        String amountFormat = "%10.2f";


        if (displayBonus) {
            return byteString
                    + "Acknowledgement Slip"
                    + "\n"
                    + "Code: "
                    + farmer
                    + "\n"
                    + "Quantity:"
                    + String.format(qtyFormat, Double.parseDouble(Qty))
                    + "\n"
                    + "FAT:"
                    + String.format(fatSNFAWMFormat, Double.parseDouble(fat))
                    + "\n"
                    + "SNF:"
                    + String.format(fatSNFAWMFormat, Double.parseDouble(snf))
                    + "\n"
                    + "AWM:"
                    + String.format(fatSNFAWMFormat, Double.parseDouble(addedWater))
                    + "\n" + "Rate (Rs.):"
                    + String.format(rateFormat, Double.parseDouble(bonusAmount)) + "\n"
                    + "Amount:"
                    + String.format(amountFormat, Double.parseDouble(amt));
        } else {
            return byteString
                    + "Acknowledgement Slip"
                    + "\n"
                    + "Code: "
                    + farmer
                    + "\n"
                    + "Quantity:"
                    + String.format(qtyFormat, Double.parseDouble(Qty))
                    + "\n"
                    + "FAT:"
                    + String.format(fatSNFAWMFormat, Double.parseDouble(fat))
                    + "\n"
                    + "SNF:"
                    + String.format(fatSNFAWMFormat, Double.parseDouble(snf))
                    + "\n"
                    + "AWM:"
                    + String.format(fatSNFAWMFormat, Double.parseDouble(addedWater))
                    + "\n" + "Rate (Rs.):"
                    + String.format(rateFormat, Double.parseDouble(rate)) + "\n"
                    + "Amount:"
                    + String.format(amountFormat, Double.parseDouble(amt));
        }
    }

    @Override
    public String getResetMsg(boolean isEight) {
        String RDUMsg;

        byte[] bytearr = {27, 64, 10, 27, 64, 10, 32, 32, 32, 32, 32};

        String byteString = new String(bytearr);
        if (isEight) {
            String farmer = "8888", Qty = "88.88", fat = "88.8", snf = "88.8", rate = "88.88", amt = "8888.88", addedWater = "8888.88";

            DecimalFormat decimalFormat3 = new DecimalFormat("0000");

            farmer = decimalFormat3.format(Double.parseDouble(farmer));

            decimalFormat3 = new DecimalFormat("00.00");
            Qty = decimalFormat3.format(Double.parseDouble(Qty));

            decimalFormat3 = new DecimalFormat("00.00");
            rate = decimalFormat3.format(Double.parseDouble(rate));

            decimalFormat3 = new DecimalFormat("00.0");
            fat = decimalFormat3.format(Double.parseDouble(fat));
            snf = decimalFormat3.format(Double.parseDouble(snf));

            decimalFormat3 = new DecimalFormat("000.00");
            amt = decimalFormat3.format(Double.parseDouble(amt));

            addedWater = decimalFormat3.format(Double.parseDouble(addedWater));

            String fatSNFAWMFormat = "%13.1f";
            String qtyFormat = "%8.2f";
            String rateFormat = "%6.2f";

            String amountFormat = "%10.2f";

            RDUMsg = byteString
                    + "Acknowledgement Slip"
                    + "\n"
                    + "Code: "
                    + farmer
                    + "\n"
                    + "Quantity:"
                    + String.format(qtyFormat, Double.parseDouble(Qty))
                    + "\n"
                    + "FAT:"
                    + String.format(fatSNFAWMFormat, Double.parseDouble(fat))
                    + "\n"
                    + "SNF:"
                    + String.format(fatSNFAWMFormat, Double.parseDouble(snf))
                    + "\n"
                    + "AWM:"
                    + String.format(fatSNFAWMFormat,
                    Double.parseDouble(addedWater)) + "\n"
                    + "Rate (Rs.):"
                    + String.format(rateFormat, Double.parseDouble(rate))
                    + "\n" + "Amount:"
                    + String.format(amountFormat, Double.parseDouble(amt));
        } else {
            String farmer = "0000", Qty = "00.00", fat = "00.00", snf = "00.00", rate = "00.00", amt = "0000.00", addedWater = "0000.00";

            DecimalFormat decimalFormat3 = new DecimalFormat("0000");

            farmer = decimalFormat3.format(Double.parseDouble(farmer));

            decimalFormat3 = new DecimalFormat("00.00");

            Qty = decimalFormat3.format(Double.parseDouble(Qty));
            rate = decimalFormat3.format(Double.parseDouble(rate));

            decimalFormat3 = new DecimalFormat("00.00");
            fat = decimalFormat3.format(Double.parseDouble(fat));
            snf = decimalFormat3.format(Double.parseDouble(snf));

            decimalFormat3 = new DecimalFormat("000.00");
            amt = decimalFormat3.format(Double.parseDouble(amt));

            addedWater = decimalFormat3.format(Double.parseDouble(addedWater));

            String fatSNFAWMFormat = "%13.1f";
            String qtyFormat = "%8.2f";
            String rateFormat = "%6.2f";

            String amountFormat = "%10.2f";

            RDUMsg = byteString
                    + "Acknowledgement Slip"
                    + "\n"
                    + "Code: "
                    + farmer
                    + "\n"
                    + "Quantity:"
                    + String.format(qtyFormat, Double.parseDouble(Qty))
                    + "\n"
                    + "FAT:"
                    + String.format(fatSNFAWMFormat, Double.parseDouble(fat))
                    + "\n"
                    + "SNF:"
                    + String.format(fatSNFAWMFormat, Double.parseDouble(snf))
                    + "\n"
                    + "AWM:"
                    + String.format(fatSNFAWMFormat,
                    Double.parseDouble(addedWater)) + "\n"
                    + "Rate (Rs.):"
                    + String.format(rateFormat, Double.parseDouble(rate))
                    + "\n" + "Amount:"
                    + String.format(amountFormat, Double.parseDouble(amt));
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
