package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

public class RatechartDetailsEnt implements Entity, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String rateChartName;
    public String rateValidityFrom;
    public String rateValidityTo;
    public String rateMilkType;
    public String rateOther1;
    public String rateOther2;
    public String rateSocId;
    public String isActive;
    public long rateLvalidityFrom;
    public long rateLvalidityTo;
    public String ratechartType;
    public String ratechartShift;

    @JsonIgnore
    public long modifiedTime;
    @JsonIgnore
    public long columnId;


    @Override
    public Object getPrimaryKeyId() {
        return rateChartName;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        this.rateChartName = (String) id;
    }
}
