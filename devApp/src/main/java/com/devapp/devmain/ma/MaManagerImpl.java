package com.devapp.devmain.ma;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.ma.parser.DataFlushRequiredException;
import com.devapp.devmain.ma.parser.Indifoss;
import com.devapp.devmain.ma.parser.Ma;
import com.devapp.devmain.ma.parser.NonAnalysisDataException;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.peripherals.interfaces.DataObserver;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.ESSAE;
import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;

/**
 * Created by x on 20/10/16.
 */

public class MaManagerImpl implements MaManager {

    Context context;
    ExecutorService mExecutor = Executors.newFixedThreadPool(1);
    Ma ma;
    OnNewDataListener newDataListener;
    StringBuilder message = new StringBuilder();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    AmcuConfig amcuConfig;
    SessionManager session;
    private Device mDevice;
    private DataObserver mDataObserver = new DataObserver() {
        @Override
        public void onDataReceived(byte[] data) {
            parseData(data);
        }
    };

    private Ma.MaParserListener maParserListener = new Ma.MaParserListener() {
        @Override
        public String getPreviousData() {
            return amcuConfig.getMilkAnalyserPrvData(mDevice.getDeviceEntity().deviceName);
        }

        @Override
        public void setPreviousData(String message) {
            amcuConfig.setMilkAnalyserPrvData(message, mDevice.getDeviceEntity().deviceName);
        }

        @Override
        public int getClrPosition() {
            int clrStartPos = 4 * (amcuConfig.getClrPosition() - 1);
            if (amcuConfig.getAddedWaterPos() < amcuConfig.getClrPosition()) {
                clrStartPos = clrStartPos + 1;
            }
            return clrStartPos;
        }

        @Override
        public String getLastDataTimestamp() {
            if (ma instanceof Indifoss) {
                return amcuConfig.getIndifossTimeStamp();
            }
            return null;
        }

        @Override
        public void setLastDataTimestamp(String timestamp) {
            if (ma instanceof Indifoss) {
                amcuConfig.setIndifossTimeStamp(timestamp);
            }
        }

        @Override
        public void onOtherDataReceived(String data) {
            Log.v(PROBER, "Cleaning Json data: " + data);
            newDataListener.onOtherMessage(data);
            message = new StringBuilder();
            baos.reset();
        }

        @Override
        public void resetConnection(int delay) {
            MaManagerImpl.this.resetConnection(delay);
        }
    };


    public MaManagerImpl(Ma ma, Device device, Context context) {
        this.ma = ma;
        mDevice = device;
        this.context = context;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(context);
        ma.setMaParserListener(maParserListener);
        setDecimalFormats();

    }

    @Override
    public void startReading() {
        if (mDevice != null) {
            mDevice.registerObserver(mDataObserver);
            mDevice.read();
        }
    }

    @Override
    public void writeToMA(String msg) {
        Log.v(ESSAE, "Writing to MA Inside Ma Impl");
        if (mDevice != null)
            mDevice.write(msg);
    }

    @Override
    public void stopReading() {
        if (mDevice != null)
            mDevice.unregisterObserver();
    }

    @Override
    public void displayAlert(View.OnClickListener onClickListener, boolean mandatory) {
        ma.displayAlert(onClickListener, mandatory);
    }

    @Override
    public void setOnNewDataListener(OnNewDataListener newDataListener) {
        this.newDataListener = newDataListener;
    }

    @Override
    public void resetConnection(int delay) {
        stopReading();


        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        message = new StringBuilder();
                        baos.reset();
                        mExecutor = Executors.newFixedThreadPool(1);
                        startReading();
                    }
                },
                delay
        );
    }

    /**
     * @param data
     */
    private void parseData(byte[] data) {

        String s = new String(data);
        if (s.equalsIgnoreCase(SmartCCConstants.ACK)) {
            Log.d(PROBER, "Ignoring Wisens module ACK");
            return;
        }
        message.append(s);
        String strData = message.toString();
//        Log.d(PROBER, "MA Data : " + strData);
        if (data != null) {
            baos.write(data, 0, data.length);
        }
        MilkAnalyserEntity maEntity;
        try {
            String validatedString = ma.validateData(strData, baos);
            if (validatedString != null) {
                maEntity = ma.parse(validatedString, baos);
                if (maEntity != null) {
                    maEntity = this.modifyMaEntity(maEntity);
                }
                newDataListener.onNewData(maEntity);
                message = new StringBuilder();
                baos.reset();
            }
        } catch (NonAnalysisDataException e) {
            newDataListener.onOtherMessage(e.getMessage());
            e.printStackTrace();
            message = new StringBuilder();
            baos.reset();
        } catch (DataFlushRequiredException e) {
            e.printStackTrace();
            message = new StringBuilder();
            baos.reset();
        }
    }

    public MilkAnalyserEntity modifyMaEntity(MilkAnalyserEntity maEntity) {

        //Commented for NDDBDemo
       /* if (ma.paramsRead.contains(Ma.Params.CLR)) {
            if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC")
                    && session.getIsChillingCenter())
                    || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")
                    && !session.getIsChillingCenter())) {

                maEntity.snf = Util.getSNF(maEntity.fat, maEntity.clr);
            } else {
                maEntity.clr = Util.getCLR(maEntity.fat, maEntity.snf);
            }
        }*/
        if (!ma.paramsRead.contains(Ma.Params.CLR)) {
            maEntity.clr = Util.getCLR(maEntity.fat, maEntity.snf);
        }
        /*if(maEntity.protein == null || maEntity.equals("")){
            maEntity.protein = "0.0";
        }*/
        return maEntity;
    }

    public void setDecimalFormats() {
        ChooseDecimalFormat cdF = new ChooseDecimalFormat();
        DecimalFormat dfFat = cdF.getDecimalFormatTypeForDisplay(AppConstants.FAT);
        DecimalFormat dfSnf = cdF.getDecimalFormatTypeForDisplay(AppConstants.SNF);
        DecimalFormat dfClr = cdF.getDecimalFormatTypeForDisplay(AppConstants.CLR);
        DecimalFormat dfAwm = cdF.getDecimalFormatTypeForDisplay(AppConstants.AWM);
        DecimalFormat dfProtein = cdF.getDecimalFormatTypeForDisplay(AppConstants.PROTEIN);

        if (dfFat != null)
            ma.setDecimalFormatFat(dfFat);
        if (dfSnf != null)
            ma.setDecimalFormatSNF(dfSnf);
        if (dfClr != null)
            ma.setDecimalFormatClr(dfClr);
        if (dfAwm != null)
            ma.setDecimalFormatAw(dfAwm);
        if (dfProtein != null)
            ma.setDecimalFormatProtein(dfProtein);
    }
}
