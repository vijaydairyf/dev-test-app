package com.devapp.devmain.ma.parser;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;

import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.user.Util;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.ESSAE;

public class Essae extends Ma implements EssaeCleaningParser.JsonDataListener {
    String cleaningStr = "";
    EssaeCleaningParser essaeCleaningParser;
    private boolean timerStarted;
    CountDownTimer timer = new CountDownTimer(2000, 2000) {
        @Override
        public void onTick(long millisUntilFinished) {
            Log.v(ESSAE, "Timer on tick");

        }

        @Override
        public void onFinish() {
            Log.v(ESSAE, "Timer countdown finished");
            timerStarted = false;
            essaeCleaningParser.splitIntoRecords(cleaningStr);
//            mListener.onOtherDataReceived(cleaningStr);
            cleaningStr = "";

        }
    };
    private Context context;

    public Essae() {
        decimalFormatClr = new DecimalFormat("#0.0");
        decimalFormatFat = new DecimalFormat("#0.0");
        decimalFormatSNF = new DecimalFormat("#0.0");
        decimalFormatAw = new DecimalFormat("#0.00");
        essaeCleaningParser = new EssaeCleaningParser();
        essaeCleaningParser.setListener(Essae.this);
        context = DevAppApplication.getAmcuContext();
    }


    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {
        Util.displayMACleaningAlert(context, onClickListener, mandatory);
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
            maEntity.protein = Double.parseDouble(strArr[5]);
            maEntity.temp = Double.parseDouble(strArr[6]);
            paramsRead.add(Params.LACTOSE);
            paramsRead.add(Params.PROTEIN);
            paramsRead.add(Params.TEMPERATURE);
            if (strArr.length > 8) {
                maEntity.addedWater = Double
                        .parseDouble(strArr[7]);
                paramsRead.add(Params.ADDED_WATER);
            } else {
            }
        }
        return maEntity;
    }

    @Override
    public String validateData(String strData, ByteArrayOutputStream baos) throws NonAnalysisDataException, DataFlushRequiredException {

        int hashIndex = -1;
        int newLineIndex = -1;

        if (strData.contains("\r\n") && strData.contains(",")) {
            if (!timerStarted) {
                timer.start();
                timerStarted = true;
            } else {
                timer.cancel();
                timer.start();
            }
            cleaningStr = cleaningStr + strData;


        }

        if (strData.contains("#"))

        {
            hashIndex = strData.lastIndexOf('#');
            if (hashIndex != -1) {
                newLineIndex = strData.indexOf('\r', hashIndex);
            }
        }

        if (hashIndex != -1 && newLineIndex != -1)

        {
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

    @Override
    public void onJsonDataReceived(String jsonData) {
        mListener.onOtherDataReceived(jsonData);
    }
}
