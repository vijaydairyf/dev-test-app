package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RateChartEntity implements Entity, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    public int columnId;
    @JsonIgnore
    public String societyId;
    @JsonIgnore
    public String farmerId;
    @JsonProperty
    public double fat;
    @JsonProperty
    public double snf;
    @JsonProperty
    public double rate;
    @JsonIgnore
    public String startDate;
    @JsonIgnore
    public String endDate;
    @JsonIgnore
    public String milkType;
    @JsonIgnore
    public String managerID;
    @JsonIgnore
    public String clr;
    @JsonIgnore
    public long rateReferenceId;
    @JsonIgnore
    public String shift;
    @JsonIgnore
    public boolean isActive;

    @JsonIgnore
    public String rateChartName;

    @Override
    public Object getPrimaryKeyId() {
        return columnId;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        this.columnId = (int) id;
    }
}
