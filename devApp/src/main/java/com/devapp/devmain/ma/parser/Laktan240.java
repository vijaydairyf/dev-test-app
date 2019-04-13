package com.devapp.devmain.ma.parser;

import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;

import java.io.ByteArrayOutputStream;

/**
 * Created by x on 5/1/18.
 */

public class Laktan240 extends Ma {

    public Laktan240() {

    }

    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {

    }

    @Override
    public MilkAnalyserEntity parse(String str, ByteArrayOutputStream baos) throws NonAnalysisDataException {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        byte[] byteLaktan = baos.toByteArray();
        String[] dataString = new String[byteLaktan.length];

        //  writeMADebugFile(byteLaktan,mContext);

        for (int i = 0; i < byteLaktan.length; i++) {
            dataString[i] = String.format("%02X ", byteLaktan[i]).trim();
        }

        if ((byteLaktan != null && byteLaktan.length > 66)) {
            return null;
        } else if (byteLaktan != null && byteLaktan.length == 66) {

            String fat = dataString[6] + dataString[5] + dataString[4] + dataString[3];
            String snf = dataString[10] + dataString[9] + dataString[8] + dataString[7];
            String density = dataString[36] + dataString[35] + dataString[34] + dataString[33];

            maEntity.fat = convertHexToDecimal(fat);
            maEntity.snf = convertHexToDecimal(snf);
            int addedWater = convertHexToInt(dataString[31]);
            maEntity.addedWater = Double.valueOf(addedWater);
            paramsRead.add(Params.FAT);
            paramsRead.add(Params.SNF);
            paramsRead.add(Params.ADDED_WATER);

            return maEntity;
        } else {
            return null;
        }
    }

    @Override
    public String validateData(String str, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException {
        if (baos != null && baos.toByteArray().length == 66) {
            return str;
        } else if (baos != null && baos.toByteArray().length > 66) {
            return str;
        } else {
            return null;
        }
    }


}
