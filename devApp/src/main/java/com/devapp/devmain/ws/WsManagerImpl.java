package com.devapp.devmain.ws;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.peripherals.interfaces.DataObserver;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;

/**
 * Created by x on 13/2/18.
 */

public class WsManagerImpl implements WsManager {

    AmcuConfig amcuConfig;
    private WsParseHelper parser;
    private Context mContext;
    private Device mDevice;
    private WsParams mWsParams;
    private StringBuilder sbMessage;
    private boolean ignoreInitialData = true;
    private OnNewDataListener mListener;
    private ChooseDecimalFormat chooseDecimalFormat;

    private DataObserver mObserver = new DataObserver() {
        @Override
        public void onDataReceived(byte[] data) {
            parse(data);
        }
    };

    public WsManagerImpl(Context context, Device device, WsParams wsParams) {
        mContext = context;
        mDevice = device;
        mWsParams = wsParams;
        parser = new WsParseHelper();
        sbMessage = new StringBuilder();
        amcuConfig = AmcuConfig.getInstance();
        chooseDecimalFormat = new ChooseDecimalFormat();

    }

    @Override
    public void openConnection() {
        Log.d(PROBER, "WS openConnection");
        ignoreInitialData = true;
        if (mDevice != null) {
            mDevice.registerObserver(mObserver);
            mDevice.read();
        }
    }

    @Override
    public void closeConnection() {
        if (mDevice != null) {
            mDevice.unregisterObserver();
        }
    }

    @Override
    public void setOnNewDataListener(OnNewDataListener listener) {
        mListener = listener;
    }

    @Override
    public void resetConnnection(int delay) {
        closeConnection();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openConnection();
            }
        }, delay);
    }

    @Override
    public void setToLitreMode() {
        if (mDevice != null) {
            mDevice.write(mWsParams.getLitreCommand());
        }
    }

    @Override
    public void setToKgMode() {
        if (mDevice != null) {
            mDevice.write(mWsParams.getKgCommand());
        }
    }

    @Override
    public void tare() {
        if (mDevice != null) {
            mDevice.write(mWsParams.getTareCommand());
        }
    }

    private synchronized void parse(byte[] data) {
        String newData = new String(data);
        Log.d(PROBER, "WS Data : " + newData);
        sbMessage.append(newData);
        String dataToParse = sbMessage.toString();
        if (ignoreInitialData) {
            if (dataToParse.length() > mWsParams.getIgnoreThreshold()) {
                ignoreInitialData = false;
                sbMessage = new StringBuilder();
            }
        } else {
            if (dataToParse.length() > 12) {
                String[] records = parser.splitUsingSeperator(dataToParse, mWsParams.getSeparator());
                if (records.length > 4) {
                    String record = records[records.length - 2];
                    double quantity;
                    if (parser.isValidFormat(record, mWsParams.getPrefix(), mWsParams.getSuffix())) {
                        quantity = parser.getWeight(record, mWsParams.getPrefix(), mWsParams.getSuffix());
                        quantity = applyDivisionFactor(quantity);
                        sbMessage = new StringBuilder();
                        mListener.onNewData(quantity);
                    } else {
                        Util.displayErrorToast("Invalid weighingScale format " + record + "," +
                                " Please check the prefix and suffix", mContext);
                    }
                }
            }
        }

    }

    private double applyDivisionFactor(double quantity) {
        if (quantity != 0) {
            quantity = quantity / amcuConfig.getWeighingDivisionFactor();
            quantity = Double.valueOf(chooseDecimalFormat.getWeightReadDecimalFormat().format(quantity));
        }
        return quantity;
    }
}
