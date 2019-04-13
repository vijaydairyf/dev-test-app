package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x on 1/2/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class RateChartPostEntity implements Entity {
    @JsonProperty
    public long startDate;
    @JsonProperty
    public long endDate;
    @JsonProperty
    public boolean isActive;
    @JsonProperty
    public String milkType;
    @JsonProperty
    public String societyId;
    @JsonProperty
    public String rateChartName;
    @JsonProperty
    public String rateChartType;
    @JsonProperty
    public List<RateChartEntity> valuesList;
    @JsonProperty
    public String shift;

    @Override
    public Object getPrimaryKeyId() {
        return rateChartName;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        rateChartName = (String) id;
    }


    public void setRateChartEntity(RatechartDetailsEnt rde, ArrayList<RateChartEntity> rateList) {
        this.rateChartName = rde.rateChartName;
        this.startDate = rde.rateLvalidityFrom;
        this.endDate = rde.rateLvalidityTo;
        this.valuesList = rateList;
        this.isActive = Boolean.parseBoolean(rde.isActive);
        this.societyId = rde.rateSocId;
        this.rateChartType = rde.ratechartType;
        this.milkType = rde.rateMilkType;
        this.shift = rde.ratechartShift;


    }

}
