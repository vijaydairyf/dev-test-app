package com.devapp.devmain.ma.parser;


import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;


/**
 * Created by Nitin on 12/1/2017.
 */
public class EkomilkV2 extends Ma {


    public EkomilkV2() {
        decimalFormatFat = new DecimalFormat("#0.0");
        decimalFormatSNF = new DecimalFormat("#0.0");
    }


    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {

    }

    @Override
    public MilkAnalyserEntity parse(String strEkomilk, ByteArrayOutputStream baos) throws NonAnalysisDataException {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();

        double snf, fat;
        int startIndex = strEkomilk.indexOf("FAT:");
        int endIndex = strEkomilk.indexOf("SNF:");
        fat = getOnlyDecimalFromString(strEkomilk.substring(startIndex, endIndex));
        startIndex = strEkomilk.indexOf("SNF:");
        endIndex = strEkomilk.indexOf("Rate");
        snf = getOnlyDecimalFromString(strEkomilk.substring(startIndex, endIndex));


        maEntity.fat = fat;
        maEntity.snf = snf;

        paramsRead.add(Params.FAT);
        paramsRead.add(Params.SNF);
        //Need to calculate clr

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
