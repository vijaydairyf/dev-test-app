package com.devapp.devmain.ma.parser;


import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;


/**
 * Created by Upendra on 9/7/2016.
 */
public class Ksheeraa extends Ma {

    public Ksheeraa() {
        decimalFormatAw = new DecimalFormat("#0.00");
        decimalFormatFat = new DecimalFormat("#0.0");
        decimalFormatSNF = new DecimalFormat("#0.0");
    }

    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {

    }

    @Override
    public MilkAnalyserEntity parse(String strData, ByteArrayOutputStream baos) throws NonAnalysisDataException {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        strData = strData.replace("|", "=");
        String[] strArr = strData.split("=");
        if (strArr.length > 5) {
            String fat = strArr[3].replace("F", "");
            String snf = strArr[4].replace("S", "");
            String clr = strArr[5].replace("C", "");
            String protein = strArr[6].replace("P", "");
            maEntity.fat = Double.parseDouble(fat);
            maEntity.snf = Double.parseDouble(snf);
            maEntity.protein = Double.parseDouble(protein);
            maEntity.clr = Double.parseDouble(clr);
            maEntity.lactose = Double.parseDouble(strArr[7].replace("L", ""));
            paramsRead.add(Params.FAT);
            paramsRead.add(Params.SNF);
            paramsRead.add(Params.PROTEIN);
            paramsRead.add(Params.CLR);
            paramsRead.add(Params.LACTOSE);
            //To clr calculation

            if (strArr.length > 9) {
                String addedWater = strArr[9].replace("W", "");
                maEntity.addedWater = Double.parseDouble(addedWater);
                paramsRead.add(Params.ADDED_WATER);
                String temp = strArr[10].replace("T", "");
                maEntity.temp = Double.parseDouble(temp);
                paramsRead.add(Params.TEMPERATURE);
            }


        }
        return maEntity;
    }

    @Override
    public String validateData(String strData, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException {
        int hashIndex = -1;
        int newLineIndex = -1;
        if (strData.contains("KS")) {
            hashIndex = strData.lastIndexOf("KS");
            if (hashIndex != -1) {
                newLineIndex = strData.indexOf('\r', hashIndex);
            }
        }
        if (hashIndex != -1 && newLineIndex != -1) {
            strData = strData.substring(hashIndex + 1, newLineIndex);
            return strData;
        }
        return null;
    }

}
