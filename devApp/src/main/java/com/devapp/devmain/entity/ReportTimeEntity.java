package com.devapp.devmain.entity;

import java.io.Serializable;

/**
 * Created by u_pendra on 19/12/16.
 */

public class ReportTimeEntity implements Serializable {

    public String date;
    public String time;
    public String shift;
    public long lDate;
    public long collectionTime;
    public long qualityTime;
    public long quantityTime;
    public long tippingStartTime;
    public long tippingEndTime;

}
