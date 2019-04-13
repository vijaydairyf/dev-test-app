package com.devapp.devmain.ma.parser;


import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.ma.MaManager;

import java.io.ByteArrayOutputStream;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;


/**
 * Created by Upendra on 9/7/2016.
 */
public class EkomilkEven extends Ma {

    MaManager listener;

    public EkomilkEven() {
        decimalFormatFat = new DecimalFormat("#0.0");
        decimalFormatSNF = new DecimalFormat("#0.0");
        decimalFormatClr = new DecimalFormat("#0.0");
        // decimalFormatFS.setRoundingMode(RoundingMode.FLOOR);
        decimalFormatFat.setRoundingMode(RoundingMode.HALF_UP);
        decimalFormatSNF.setRoundingMode(RoundingMode.HALF_UP);
        decimalFormatClr.setRoundingMode(RoundingMode.HALF_UP);

    }

    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {

    }

    @Override
    public MilkAnalyserEntity parse(String str, ByteArrayOutputStream baos) throws NonAnalysisDataException {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        if (baos != null && (baos.toByteArray().length == 25)) {
            byte[] byteNuline = baos.toByteArray();
            System.out.println("data value real stream" + Arrays.toString(byteNuline));
            double fat = binaryToDecimal(byteNuline[0], byteNuline[1]);
            maEntity.fat = fat;
            double snf = binaryToDecimal(byteNuline[2], byteNuline[3]);
            maEntity.snf = snf;
            maEntity.clr = binaryToDecimal(byteNuline[4], byteNuline[5]);
            //    afterGettingMaData(etFat.getText().toString(), etSnf.getText().toString());
            float addedWater = binaryToDecimal(byteNuline[6], byteNuline[7]);
            maEntity.addedWater = addedWater;
            paramsRead.add(Params.FAT);
            paramsRead.add(Params.SNF);
            paramsRead.add(Params.CLR);
            paramsRead.add(Params.ADDED_WATER);
            return maEntity;
        } else {
            return null;
        }

    }

    @Override
    public String validateData(String str, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException {

        byte[] byteNuline = baos.toByteArray();
//        L.d("EkoMILk RawData: "+ Util.bytesToHex(byteNuline));
//        L.d("Validate data Length" + byteNuline.length);
        if (byteNuline == null)
            return null;
        if (isPrintData(byteNuline)) {
            mListener.resetConnection(3000);
            return null;
        }
        if (byteNuline.length == 18 && ifWaterTest(byteNuline)) {
//            L.d("Validate data Length Cleaning" + byteNuline.length);
            return str;
        }
        /*if(byteNuline.length > 25)
            return str;*/
        if (byteNuline.length < 25)
            return null;

        return str;
    }


    /**
     * for water test of charter 18 lenght starting with a0 and ending with b0
     *
     * @param byteNuline
     * @return
     */
    private boolean ifWaterTest(byte[] byteNuline) {

        if (byteNuline[0] == (byte) 0xa0 && byteNuline[17] == (byte) 0xb0)
            return true;
        return false;
    }

    private boolean isPrintData(byte[] bytes) {
        if (bytes[0] == 0x1b) {
            return true;
        }
        return false;
    }

}
