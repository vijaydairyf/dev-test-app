package com.devapp.devmain.ma.parser;


import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.util.logger.Log;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;


/**
 * Created by Upendra on 9/7/2016.
 */
public class Indifoss extends Ma {
    /*
     * Data format 10:27:07 31-12-2015,1, 0.35, 1.37, 0.34,Water,,,,,Abnormal
     *            10:27:07 31-12-2015,1, 0.35, 1.37, 0.34,Water,,,,,Abnormal
     * */

    public Indifoss() {
        decimalFormatFat = new DecimalFormat("#0.0");
        decimalFormatSNF = new DecimalFormat("#0.0");
    }


    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {

    }

    @Override
    public MilkAnalyserEntity parse(String strData, ByteArrayOutputStream baos) throws NonAnalysisDataException {
        MilkAnalyserEntity maEntity = null;
        String data = null;
        String[] strIndifoss = null;
        try {
            data = getIndifossArr(strData);
            strIndifoss = data.split(",");
        } catch (DataFlushRequiredException e) {
            e.printStackTrace();
        }

        if (strIndifoss != null) {
            String fat = strIndifoss[2];
            String snf = strIndifoss[3];
            String protein = strIndifoss[4];


            if ((Double.parseDouble(fat) > 0 ||
                    Double.parseDouble(snf) > 0)) {
                maEntity = new MilkAnalyserEntity();
                maEntity.fat = Double.parseDouble(fat);
                maEntity.snf = Double.parseDouble(snf);
                maEntity.protein = Double.parseDouble(protein);
                paramsRead.add(Params.FAT);
                paramsRead.add(Params.SNF);
                paramsRead.add(Params.PROTEIN);
                //Need to add Clr
//                maEntity.protein = Double.valueOf(protein);
                mListener.setPreviousData(data);
            }
        }

        return maEntity;
    }

    @Override
    public String validateData(String str, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException {
        if (getIndifossArr(str) != null) {
            return str;
        } else {
            return null;
        }
    }


    public String getIndifossArr(String strIndifoss) throws NonAnalysisDataException, DataFlushRequiredException {
        String[] strArray;
        if (strIndifoss == null) {
            strArray = null;
        } else if (strIndifoss.contains("\r\n")) {
            strArray = strIndifoss.split("\r\n");
        } else {
            strArray = strIndifoss.split("\n");
        }
        if (strArray != null && strArray.length > 2) {
            String data = strArray[strArray.length - 2];
            Log.d("myDebug", "Indifoss Cur Data: " + data);
            String[] strIndifossData = data.split(",");
            String[] timeStamp = strIndifossData[0].split(" ");
            if (timeStamp[0].contains("00:00:00")) {
                //This check is for cleaning
                throw new DataFlushRequiredException("Flush Data");
            }

            if (mListener.getPreviousData() == null) {
                return data;
            }
            if (strIndifossData.length > 3 && !mListener.getPreviousData().
                    equalsIgnoreCase(data)) {
                Log.d("myDebug", "Indifoss Valid Data");
                Log.d("myDebug", "Indifoss Old Data: " + mListener.getPreviousData());
                Log.d("myDebug", "Indifoss New Data: " + data);
                return data;
            } else {
                throw new DataFlushRequiredException("Flush Data");
            }

        } else {
            return null;
        }

    }

}
