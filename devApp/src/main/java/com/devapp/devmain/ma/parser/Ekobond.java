package com.devapp.devmain.ma.parser;


import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;


/**
 * Created by Upendra on 9/7/2016.
 */
public class Ekobond extends Ma {

    public Ekobond() {
        decimalFormatFat = new DecimalFormat("#0.0");
        decimalFormatSNF = new DecimalFormat("#0.0");
        decimalFormatClr = new DecimalFormat("#0.0");
    }

    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {

    }

    @Override
    public MilkAnalyserEntity parse(String str, ByteArrayOutputStream baos) throws NonAnalysisDataException {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        if (baos != null && baos.toByteArray().length == 25) {
            byte[] byteNuline = baos.toByteArray();
            if ((byteNuline != null && byteNuline.length > 25)) {

                return null;
            } else if (byteNuline != null && byteNuline.length == 25) {
                double fat = binaryToDecimal(byteNuline[0], byteNuline[1]);
                maEntity.fat = fat;
                double snf = binaryToDecimal(byteNuline[2], byteNuline[3]);
                maEntity.snf = snf;
                //    afterGettingMaData(etFat.getText().toString(), etSnf.getText().toString());
                double addedWater = binaryToDecimal(byteNuline[6], byteNuline[7]);
                maEntity.addedWater = addedWater;
                paramsRead.add(Params.FAT);
                paramsRead.add(Params.SNF);
                paramsRead.add(Params.ADDED_WATER);
                //need to add clr
                baos.reset();
                return maEntity;
            } else {
                baos.reset();
                return null;
            }

        } else if (baos != null && baos.toByteArray().length > 25) {
            return null;
        }
        return maEntity;
    }

    @Override
    public String validateData(String str, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException {
        if (baos != null && baos.toByteArray().length == 25) {
            return str;
        } else {
            return null;
        }
    }

}
