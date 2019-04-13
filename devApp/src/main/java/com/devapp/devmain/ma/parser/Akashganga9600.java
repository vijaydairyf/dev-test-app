package com.devapp.devmain.ma.parser;


import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;

import java.io.ByteArrayOutputStream;


/**
 * Created by Upendra on 9/7/2016.
 */
public class Akashganga9600 extends Ma {
    /*AMA MINI-40


            01\01\2018    13:11

    temperature:476.6\0xf8C   Cow milk

    F= 0.1    S= 0.6   CLR = 0.9

    P= 0.1    D= 2.0   Salt= 0.0

    L= 0.2   AW= 7.0   Fp= 0.000


    AKASHGANGA*/

    public Akashganga9600() {
    }


    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {

    }

    @Override
    public MilkAnalyserEntity parse(String strAkashGanga, ByteArrayOutputStream baos) throws NonAnalysisDataException {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        int fromIndex, toIndex;
        fromIndex = strAkashGanga.indexOf("Temp");
        toIndex = strAkashGanga.indexOf("C ");
        double temp = getOnlyDecimalFromString(strAkashGanga.substring(fromIndex, toIndex));

        maEntity.temp = temp;

        fromIndex = strAkashGanga.indexOf("F=");
        toIndex = strAkashGanga.indexOf("S=");

        double fat = getOnlyDecimalFromString(strAkashGanga.substring(fromIndex, toIndex));

        maEntity.fat = fat;

        fromIndex = strAkashGanga.indexOf("S=");
        toIndex = strAkashGanga.indexOf("CLR =");
        double snf = getOnlyDecimalFromString(strAkashGanga.substring(fromIndex, toIndex));
        maEntity.snf = snf;

        fromIndex = strAkashGanga.indexOf("P= ");
        toIndex = strAkashGanga.indexOf("D=");
        double protein = getOnlyDecimalFromString(strAkashGanga.substring(fromIndex, toIndex));
        maEntity.protein = protein;

        fromIndex = strAkashGanga.indexOf("L= ");
        toIndex = strAkashGanga.indexOf("AW= ");
        double lactose = getOnlyDecimalFromString(strAkashGanga.substring(fromIndex, toIndex));
        maEntity.lactose = lactose;

        paramsRead.add(Params.TEMPERATURE);
        paramsRead.add(Params.FAT);
        paramsRead.add(Params.SNF);
        paramsRead.add(Params.PROTEIN);
        paramsRead.add(Params.LACTOSE);

        //To get the clr

        fromIndex = strAkashGanga.indexOf("AW=");
        toIndex = strAkashGanga.indexOf("Fp=");

        if (toIndex != -1) {
            double addedWater = getOnlyDecimalFromString(strAkashGanga.substring(fromIndex, toIndex));
            maEntity.addedWater = addedWater;
            paramsRead.add(Params.ADDED_WATER);
        }
        return maEntity;
    }

    @Override
    public String validateData(String strData, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException {
        int hashIndex = -1;
        int newLineIndex = -1;
        if (strData.contains("Temp")) {
            hashIndex = strData.lastIndexOf("Temp");
            if (hashIndex != -1) {
                newLineIndex = strData.indexOf("Fp=", hashIndex);
            }
        }
        if (hashIndex != -1 && newLineIndex != -1) {
            return strData;
        }
        return null;
    }

}
