package com.devapp.devmain.ma.parser;


import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Upendra on 9/7/2016.
 */
public abstract class Ma {

    public List<String> paramsRead = new ArrayList<>();
    DecimalFormat decimalFormatClr = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatFat = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatSNF = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatAw = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatProtein = new DecimalFormat("#0.0");
    MaParserListener mListener;

    public abstract void displayAlert(View.OnClickListener onClickListener, boolean mandatory);

    public abstract MilkAnalyserEntity parse(String str, ByteArrayOutputStream baos) throws NonAnalysisDataException;

    public abstract String validateData(String str, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException;

    public void setMaParserListener(MaParserListener listener) {
        mListener = listener;
    }

    public DecimalFormat getDecimalFormatClr() {
        return decimalFormatClr;
    }

    public void setDecimalFormatClr(DecimalFormat decimalFormatClr) {
        this.decimalFormatClr = decimalFormatClr;
    }

    public DecimalFormat getDecimalFormatFat() {
        return decimalFormatFat;
    }

    public void setDecimalFormatFat(DecimalFormat decimalFormatFat) {
        this.decimalFormatFat = decimalFormatFat;
    }

    public DecimalFormat getDecimalFormatSNF() {
        return decimalFormatSNF;
    }

    public void setDecimalFormatSNF(DecimalFormat decimalFormatSNF) {
        this.decimalFormatSNF = decimalFormatSNF;
    }

    public DecimalFormat getDecimalFormatAw() {
        return decimalFormatAw;
    }

    public void setDecimalFormatAw(DecimalFormat decimalFormatAw) {
        this.decimalFormatAw = decimalFormatAw;
    }

    public DecimalFormat getDecimalFormatProtein() {
        return decimalFormatProtein;
    }

    public void setDecimalFormatProtein(DecimalFormat decimalFormatProtein) {
        this.decimalFormatProtein = decimalFormatProtein;
    }

    public double getOnlyDecimalFromString(String record) {
        record = record.replace("%", "");
        record = record.trim();
        char[] chars = record.toCharArray();
        String result = "";
        if (record.startsWith("-")) {
            result = result + "-";
        }
        for (char c : chars) {

            if (Character.isDigit(c) || Character.toString(c).equals(".")) {
                result = result + c;
            }
        }
        return !result.equalsIgnoreCase("") ? Double.parseDouble(result.trim()) : 0;
    }

    public float binaryToDecimal(byte s1, byte s2) {

        int p1 = ((s1 & 0xf0) >> 4);
        int p2 = (s1 & 0x0f);

        int n1 = p1 * 10 + p2;

        int b3 = ((s2 & 0xf0) >> 4);
        int b4 = (s2 & 0x0f);

        int n2 = b3 * 10 + b4;

        System.out.println(" data value" + n1 + "," + n2);


        float result = (float) n1 + ((float) n2 / 100);

        System.out.println(" data value " + result);

        return result;

    }

    public String bytesToHexString(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for (byte b : in) {
            builder.append(String.format("%02x", b));
            builder.append(" ");
        }
        return builder.toString();
    }

    public double convertHexToDecimal(String hex) {
        hex = "0x" + hex;
        int intHex = Integer.decode(hex);
        float f = Float.intBitsToFloat(intHex);
        return Double.valueOf(f);
    }

    public int convertHexToInt(String hex) {
        hex = "0x" + hex;
        int intHex = Integer.decode(hex);
        return intHex;
    }

    public interface MaParserListener {
        String getPreviousData();

        void setPreviousData(String message);

        int getClrPosition();

        String getLastDataTimestamp();

        void setLastDataTimestamp(String timestamp);

        void onOtherDataReceived(String data);

        void resetConnection(int delay);
    }

    public interface Params {
        String FAT = "FAT";
        String SNF = "SNF";
        String CLR = "CLR";
        String TEMPERATURE = "TEMPERATURE";
        String PROTEIN = "PROTEIN";
        String ADDED_WATER = "ADDED_WATER";
        String LACTOSE = "LACTOSE";
        String SALT = "SALT";
        String FREEZING_POINT = "FREEZING_POINT";
        String PH = "PH";
        String CONDUCTIVITY = "CONDUCTIVITY";
        String DENSITY = "DENSITY";
        String SOLIDS = "SOLIDS";
    }
}
