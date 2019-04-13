package com.devapp.devmain.agentfarmersplit;

import java.io.Serializable;

/**
 * Created by u_pendra on 16/10/17.
 */

public class RateParams implements Serializable {


    public double rate;
    public double amount;
    public double bonus;
    public double incentive;
    public String rateUnit;
    public String rateMode;

    public String rateChartName;
    public boolean isRateCalculated;

    public double incentiveRate;
    public double incentiveAmount;
    public double baseAmount;
    public double baseRate;


}
