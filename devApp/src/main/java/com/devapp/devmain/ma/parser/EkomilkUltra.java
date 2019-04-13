package com.devapp.devmain.ma.parser;


import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;


/**
 * Created by Upendra on 9/7/2016.
 */
public class EkomilkUltra extends Ma {


    public EkomilkUltra() {
        decimalFormatFat = new DecimalFormat("#0.0");
        decimalFormatSNF = new DecimalFormat("#0.0");
        decimalFormatClr = new DecimalFormat("#0.0");
    }


    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {

    }

    @Override
    public MilkAnalyserEntity parse(String strEkomilk, ByteArrayOutputStream baos) throws NonAnalysisDataException {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();

        String snf, fat, code, milkType, awm;
        int i = strEkomilk.indexOf("Code:");

        i = strEkomilk.indexOf("FAT:");
        fat = strEkomilk.substring(i + 4, i + 19).replace(" ", "");
        i = strEkomilk.indexOf("SNF:");
        snf = strEkomilk.substring(i + 4, i + 19).replace(" ", "");
        i = strEkomilk.indexOf("AWM:");
        awm = strEkomilk.substring(i + 4, i + 17).replace(" ", "");
        snf = snf.replace("%", "");
        fat = fat.replace("%", "");
        awm = awm.replace("%", "");

        snf = decimalFormatSNF.format(Double.parseDouble(snf));
        fat = decimalFormatFat.format(Double.parseDouble(fat));
        maEntity.fat = Double.parseDouble(fat);
        maEntity.snf = Double.parseDouble(snf);

        paramsRead.add(Params.FAT);
        paramsRead.add(Params.SNF);

        //Need to calculate clr

        try {
            double addedWater = Double.parseDouble(awm);
            maEntity.addedWater = addedWater;
            paramsRead.add(Params.ADDED_WATER);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        return maEntity;
    }

    @Override
    public String validateData(String strData, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException {
        int amtIdx = -1;
        int dateIdx = strData.indexOf("Date:");
        if (dateIdx != -1)
            amtIdx = strData.indexOf("Amount:", dateIdx);

        if (amtIdx != -1) {
            return strData;
        }
        return null;
    }
}
