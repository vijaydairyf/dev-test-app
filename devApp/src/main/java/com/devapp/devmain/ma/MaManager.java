package com.devapp.devmain.ma;

import android.view.View;

import com.devapp.devmain.entity.MilkAnalyserEntity;

/**
 * Interface for uSB connection and receive data
 */

public interface MaManager {
    void startReading();

    void writeToMA(String msg);

    void stopReading();

    void displayAlert(View.OnClickListener onClickListener, boolean mandatory);

    void setOnNewDataListener(OnNewDataListener listener);

    void resetConnection(int delay);

    interface OnNewDataListener {
        void onNewData(MilkAnalyserEntity maEntity);

        void onOtherMessage(String message);
    }
}
