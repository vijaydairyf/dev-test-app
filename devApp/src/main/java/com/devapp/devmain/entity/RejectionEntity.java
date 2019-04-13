package com.devapp.devmain.entity;

import java.io.Serializable;

public class RejectionEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String socId;
    public String fat;
    public String snf;
    public String clr;
    public String snfChecked;
    public String clrChecked;
    public String fatChecked;
    public String mrnStartTime;
    public String mrnEndTime;
    public String evnStartTime;
    public String evnEndTime;

    public String chkFatRate;
    public String chkSnfRate;
    public String chkClrRate;

    public String maxFat;
    public String maxSnf;

}
