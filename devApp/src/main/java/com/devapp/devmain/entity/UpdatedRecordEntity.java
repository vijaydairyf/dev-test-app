package com.devapp.devmain.entity;


public class UpdatedRecordEntity {


    public String farmerId;
    public long collectionTime;
    public String milkType;
    public double milkQuantity;
    public double fat;
    public double snf;
    public double clr;
    public double amount;
    public double rate;
    public double awm;
    public double bonus;
    public String status;
    public String quantity_mode;
    public String quality_mode;
    public long qualityTime;
    public long quantityTime;
    public double temperature;
    public String mode;
    public String collectionType;
    //
    public String milkQuality;
    //Rate mode
    public String rateMode;

    //As per milma requirement added
    public int numberOfCans;
    public String collectionRoute;
    //This entity created to handle uncommited records from previous shift
    public String recordStatus;

//Entity for edited report

    public long updatedTime;
    public String recordType;
    public long foreignSequenceNum;

    //To support smartCC report

    public long tippingStartTime;
    public long tippingEndTime;
    public String userId;
    public String agentId;
    public int milkStatusCode;

    //rate calculation from server for device side
    public boolean isRateCalculated;
    public int sampleNumber;

    public String maName;
    public int maNumber;

    public double kgQty;
    public double ltrQty;


}
