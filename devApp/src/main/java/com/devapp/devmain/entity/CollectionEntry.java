package com.devapp.devmain.entity;

import com.devapp.devmain.agentfarmersplit.QualityParams;
import com.devapp.devmain.agentfarmersplit.QuantityParams;
import com.devapp.devmain.agentfarmersplit.RateParams;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionEntry {


    public String producerId;
    public long collectionTime;
    public String userId;
    public String agentId;
    public String mode;
    public int sampleNumber;
    public String status;
    public String collectionType;
    //As per milma requirement added
    public int numberOfCans;
    public String collectionRoute;
    //This entity created to handle uncommited records from previous shift
    public String recordStatus;

    public String milkType;

    public QualityParams qualityParams;
    public QuantityParams quantityParams;
    public RateParams rateParams;


//    public double milkQuantity;
//    public double fat;
//    public double snf;
//    public double clr;
//    public double amount;
//    public double rate;
//    public double awm;
//    public double bonus;
//
//    public String quantity_mode;
//    public String quality_mode;
//    public long qualityTime;
//    public long quantityTime;
//    public double temperature;
//
//
//    //
//    public String milkQuality;
//    //Rate mode
//    public String rateMode;
//
//    public double kgQty;
//    public double ltrQty;
//    public String rateChartName;
//
//    //To support smartCC report
//
//    public long tippingStartTime;
//    public long tippingEndTime;
//
//    //rate calculation from server for device side
//    public boolean isRateCalculated;
//
//
//    public String maName;
//    public int maNumber;
}
