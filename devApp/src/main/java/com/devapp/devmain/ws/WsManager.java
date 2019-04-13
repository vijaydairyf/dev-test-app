package com.devapp.devmain.ws;

/**
 * Created by x on 13/2/18.
 */

public interface WsManager {
    void openConnection();

    void closeConnection();

    void setOnNewDataListener(OnNewDataListener listener);

    void resetConnnection(int delay);

    void setToLitreMode();

    void setToKgMode();

    void tare();

    interface OnNewDataListener {
        void onNewData(double data);
    }
}
