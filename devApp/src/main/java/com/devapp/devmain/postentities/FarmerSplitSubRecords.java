package com.devapp.devmain.postentities;

import com.devapp.devmain.ConsolidationPost.CustomSerializable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

/**
 * Created by u_pendra on 18/1/18.
 */

public class FarmerSplitSubRecords {


    public QuantityParamspost quantityParams;
    public QualityParamsPost qualityParams;
    public RateParamsPost rateParams;
    public String producerId;
    @JsonSerialize(using = CustomSerializable.class)
    public Date collectionTime;
    public long sequenceNumber;
    public String milkType;
    public String status;
    public String mode;
    public int numberOfCans;
    public String userId;
    public int sampleNumber;
    @JsonIgnore
    public long columnId;


}
