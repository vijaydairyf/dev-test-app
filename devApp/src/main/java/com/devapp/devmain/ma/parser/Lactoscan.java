package com.devapp.devmain.ma.parser;

import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;


/**
 * Created by Upendra on 9/7/2016.
 */
public class Lactoscan extends Ma {

    public Lactoscan() {
        decimalFormatClr = new DecimalFormat("#0.0");
        decimalFormatFat = new DecimalFormat("#0.0");
        decimalFormatSNF = new DecimalFormat("#0.0");
        decimalFormatAw = new DecimalFormat("#0.00");
    }


    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {

    }

    @Override
    public MilkAnalyserEntity parse(String strLactoscan, ByteArrayOutputStream baos) throws NonAnalysisDataException {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();

        String[] strArr = strLactoscan.split(" ");

        if (strArr.length > 5) {
            String str3 = strArr[strArr.length - 2];
            str3 = str3.replace(" ", "");

            maEntity.fat = Double.parseDouble(strArr[0]);
            maEntity.snf = Double.parseDouble(strArr[1]);
            paramsRead.add(Params.FAT);
            paramsRead.add(Params.SNF);

            if (strArr[2] != null) {
                maEntity.clr = Double.parseDouble(strArr[2]);
                paramsRead.add(Params.CLR);
            }
            maEntity.lactose = Double.parseDouble(strArr[3]);
            maEntity.salt = Double.parseDouble(strArr[4]);
            maEntity.protein = Double.parseDouble(strArr[5]);
            maEntity.temp = Double.parseDouble(strArr[6]);
            paramsRead.add(Params.LACTOSE);
            paramsRead.add(Params.SALT);
            paramsRead.add(Params.PROTEIN);
            paramsRead.add(Params.TEMPERATURE);

            if (strArr.length > 8) {
                maEntity.addedWater = Double.parseDouble(strArr[7]);
                paramsRead.add(Params.ADDED_WATER);
            }
            maEntity.freezingPoint = Double.parseDouble(strArr[8]);
            paramsRead.add(Params.FREEZING_POINT);
            maEntity.calibration = strArr[9];
            maEntity.serialNum = strArr[10];
            maEntity.pH = Double.parseDouble(strArr[11]);
            paramsRead.add(Params.PH);
            maEntity.conductivity = Double.parseDouble(strArr[12]);
            paramsRead.add(Params.CONDUCTIVITY);
        }
        return maEntity;
    }

    @Override
    public String validateData(String strData, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException {
        int hashIndex = -1;
        int newLineIndex = -1;
        if (strData.contains("#")) {
            hashIndex = strData.lastIndexOf('#');
            if (hashIndex != -1) {
                newLineIndex = strData.indexOf('\r', hashIndex);
            }
        }

        if (hashIndex != -1 && newLineIndex != -1) {
            strData = strData.substring(hashIndex + 1, newLineIndex);
            String str2 = strData;
            str2.replace(" ", "");
            if (str2.equalsIgnoreCase("CLFL")
                    || str2.equalsIgnoreCase("CLN")
                    || str2.equalsIgnoreCase("RIN")) {
                throw new NonAnalysisDataException(str2);
            } else {
                return strData;
            }
        } else
            return null;
    }
}
