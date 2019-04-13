package com.devapp.devmain.ma.parser;


import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;


/**
 * Created by Upendra on 9/7/2016.
 */
public class EkomilkUltraPro extends Ma {


    DecimalFormat decimalFormatClr;
    String previousRecord;

    public EkomilkUltraPro() {
        decimalFormatClr = new DecimalFormat("#0.00");
        decimalFormatFat = new DecimalFormat("#0.0");
        decimalFormatSNF = new DecimalFormat("#0.0");
    }


    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {

    }

    @Override
    public MilkAnalyserEntity parse(String strECOMILKULTRA, ByteArrayOutputStream baos) throws NonAnalysisDataException {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        strECOMILKULTRA = strECOMILKULTRA.replace(" ", "");

        String[] str = strECOMILKULTRA.split("(?<=\\G.{4})");

        String fat = strECOMILKULTRA.substring(0, 4);
        String snf = strECOMILKULTRA.substring(4, 8);
        int clrPosition = mListener.getClrPosition();
        String clr = strECOMILKULTRA.substring(clrPosition, clrPosition + 4);

        maEntity.fat = addDecimalPoint(fat, decimalFormatFat);
        maEntity.snf = addDecimalPoint(snf, decimalFormatSNF);
        maEntity.clr = addDecimalPoint(clr, decimalFormatClr);
        maEntity.addedWater = addDecimalPoint(strECOMILKULTRA.substring(12, 16), decimalFormatAw);

        paramsRead.add(Params.FAT);
        paramsRead.add(Params.SNF);
        paramsRead.add(Params.CLR);
        paramsRead.add(Params.ADDED_WATER);

        previousRecord = strECOMILKULTRA;
        mListener.setPreviousData(strECOMILKULTRA);
        return maEntity;
    }

    @Override
    public String validateData(String strData, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException {
        previousRecord = mListener.getPreviousData();
        int parIndex1 = -1;
        int parIndex2 = -1;

        if (strData.contains("(") && strData.contains(")")) {

            parIndex1 = strData.indexOf("(");
            parIndex2 = strData.indexOf(')', parIndex1);

        } else {
            return null;
        }

        if (parIndex1 != -1 && parIndex2 != -1) {
            strData = strData.substring(parIndex1 + 1, parIndex2);

            String regex = "[0-9]+";

            if (!strData.matches(regex)) {
                throw new DataFlushRequiredException("Flush Data Buffer");
            }

            if (previousRecord == null
                    || !previousRecord.equalsIgnoreCase(strData)) {

                return strData;
            } else {
                throw new DataFlushRequiredException("Flush Data Buffer");
            }
        }
        return null;
    }


   /* public int getClrPositionForEkoMilkUltra()

    {
        int clrStartPos=4*(MAUtil.getClrPosition()-1);
        if(MAUtil.getWaterPosition()<MAUtil.getClrPosition())
        {
            clrStartPos=clrStartPos+1;
        }

        return clrStartPos;
    }*/

    public double addDecimalPoint(String str, DecimalFormat decimalFormat) {

        String value;
        String[] str1 = str.split("(?<=\\G.{2})");
        value = str1[0] + "." + str1[1];
//        value = decimalFormat.format(Double.parseDouble(value));
        return Double.parseDouble(value);

    }
}
