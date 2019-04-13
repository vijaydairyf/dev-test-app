package com.devapp.devmain.ma.parser;

import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;


/**
 * Created by Upendra on 9/7/2016.
 */
public class LM2 extends Ma {


    public LM2() {
        decimalFormatFat = new DecimalFormat("#0.00");
        decimalFormatSNF = new DecimalFormat("#0.0");

    }

    public static String getOnlyDecimalFromStringLm2(String record) {
        char[] chars = record.toCharArray();
        String result = "";
        for (char c : chars) {
            if (result.equalsIgnoreCase("")) {
                if (Character.isDigit(c)) {
                    result = result + c;
                }
            } else if (Character.isDigit(c) || Character.toString(c).equals(".")
                    || Character.toString(c).equals("-")) {
                result = result + c;
            }
        }
        return result;
    }

    @Override
    public String validateData(String strData, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException {

        CharSequence start = "Milk Analyzer MASTER";
        CharSequence end = "Freezing point";
        strData = new String(baos.toByteArray());
        if (strData.contains(start) && strData.contains(end)) {

            return strData;
        }
        return null;
    }

    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {

    }

    @Override
    public MilkAnalyserEntity parse(String strData, ByteArrayOutputStream baos) throws NonAnalysisDataException {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();

        int fromIndex, toIndex;
        fromIndex = strData.indexOf("Temp");
        toIndex = strData.indexOf("Fat");
        maEntity.temp = Double.parseDouble(
                getOnlyDecimalFromStringLm2(strData.substring(fromIndex, toIndex)));
        paramsRead.add(Params.TEMPERATURE);

        fromIndex = strData.indexOf("Fat");
        toIndex = strData.indexOf("SNF");
        String fat = getOnlyDecimalFromStringLm2(strData.substring(fromIndex, toIndex));

        fromIndex = strData.indexOf("SNF");
        toIndex = strData.indexOf("Density");
        String snf = getOnlyDecimalFromStringLm2(strData.substring(fromIndex, toIndex));

        fromIndex = strData.indexOf("Added water");
        toIndex = strData.indexOf("Freezing point");
        String awm = getOnlyDecimalFromStringLm2(strData.substring(fromIndex, toIndex));

        fromIndex = strData.indexOf("Density");
        if (fromIndex != -1) {
            String density = strData.substring(fromIndex + 18, fromIndex + 23);
            density = density.replace("%", "");
            density = density.replace(" ", "");
            maEntity.density = Double.parseDouble(density);
            paramsRead.add(Params.DENSITY);
        }

        fromIndex = strData.indexOf("Protein");
        if (fromIndex != -1) {
            String protein = strData.substring(fromIndex + 18, fromIndex + 23);
            protein = protein.replace("%", "");
            protein = protein.replace(" ", "");
            maEntity.protein = Double.parseDouble(protein);
            paramsRead.add(Params.PROTEIN);
        }

        fromIndex = strData.indexOf("Lactose");
        if (fromIndex != -1) {
            String lactose = strData.substring(fromIndex + 18, fromIndex + 23);
            lactose = lactose.replace("%", "");
            lactose = lactose.replace(" ", "");
            maEntity.lactose = Double.parseDouble(lactose);
            paramsRead.add(Params.LACTOSE);
        }

        fromIndex = strData.indexOf("Salts");
        if (fromIndex != -1) {
            String salts = strData.substring(fromIndex + 18, fromIndex + 23);
            salts = salts.replace("%", "");
            salts = salts.replace(" ", "");
            maEntity.salt = Double.parseDouble(salts);
            paramsRead.add(Params.SALT);
        }

        fromIndex = strData.indexOf("Freezing");
        if (fromIndex != -1) {
            String freezing = strData.substring(fromIndex + 16, fromIndex + 23);
            freezing = freezing.replace("%", "");
            freezing = freezing.replace(" ", "");
            maEntity.freezingPoint = Double.parseDouble(freezing);
            paramsRead.add(Params.FREEZING_POINT);
        }

        maEntity.fat = Double.valueOf(fat);
        paramsRead.add(Params.FAT);
        maEntity.snf = Double.valueOf(snf);
        paramsRead.add(Params.SNF);
        maEntity.addedWater = Double.valueOf(awm);
        paramsRead.add(Params.ADDED_WATER);
        //To calculate the clr

        return maEntity;
    }
}
