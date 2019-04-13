package com.android.internal.telephony;

public interface ITelephony {


    boolean endCall();

    void answerRingingCall();

    void silenceRinger();

    boolean supplyPin(String str);

    boolean handlePinMmi(String str);

    boolean isSimPinEnabled();

}
