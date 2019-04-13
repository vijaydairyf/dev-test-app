package com.devapp.devmain.services;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

//Result receiver to display success and failure of write data in pendrive

public class WriteRecordReceiver extends ResultReceiver {

    private Receiver receiver;

    public WriteRecordReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    // Delegate method which passes the result to the receiver if the receiver has been assigned
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);

        }
    }

    // Defines our event interface for communication
    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

}
