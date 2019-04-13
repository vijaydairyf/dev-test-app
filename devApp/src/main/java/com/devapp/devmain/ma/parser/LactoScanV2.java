package com.devapp.devmain.ma.parser;

import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;


/**
 * Created by Upendra on 9/7/2016.
 */
public class LactoScanV2 extends Ma {
    public LactoScanV2() {
        decimalFormatFat = new DecimalFormat("#0.0");
        decimalFormatSNF = new DecimalFormat("#0.0");
    }


    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {

    }

    @Override
    public MilkAnalyserEntity parse(String strData, ByteArrayOutputStream baos) throws NonAnalysisDataException {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        String[] strArr = strData.split(" ");

        if (strData.contains("CLFL") || strData.contains("CLN") || strData.contains("RIN")) {
            throw new NonAnalysisDataException(strData);
        } else if (strArr.length > 1) {
            maEntity.fat = Double.parseDouble(strArr[0]);
            maEntity.snf = Double.parseDouble(strArr[1]);
            paramsRead.add(Params.FAT);
            paramsRead.add(Params.SNF);
            //Need to add clr
            // reset connection for every collection data.because somtime ma data gets blank after few sample
//            mListener.resetConnection(500);
        }
        return maEntity;
    }

    @Override
    public String validateData(String strData, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException {
        int hashIndex = -1;
        int newLineIndex = -1;
        if (strData.contains("#")) {
            hashIndex = strData.indexOf('#');
            if (hashIndex != -1) {
                newLineIndex = strData.indexOf("\r\n", hashIndex);
            }
        }
        if (hashIndex != -1 && newLineIndex != -1) {
            strData = strData.substring(hashIndex + 1, newLineIndex);
            String str2 = strData;

            str2 = str2.replace(" ", "");
            if (str2.equalsIgnoreCase("CLFL")
                    || str2.equalsIgnoreCase("CLN")
                    || str2.equalsIgnoreCase("RIN")) {
                if (str2.length() < 7) {
                    return strData;
                }
                return null;
            } else {
                return strData;
            }
        }

        return null;
    }

}
