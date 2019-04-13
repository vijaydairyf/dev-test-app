package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by y on 4/12/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class BonusChartEntity implements Serializable, Entity {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    public String societyId;
    @JsonIgnore
    public String farmerId;
    @JsonProperty("rate")
    public double bonusRate;
    @JsonProperty
    public double point;
    @JsonIgnore
    public String startDate;
    @JsonIgnore
    public String endDate;
    @JsonIgnore
    public String milkType;
    @JsonIgnore
    public String name;
    @JsonIgnore
    public boolean isActive;
    @JsonIgnore
    public String rateChartType;


    @Override
    public Object getPrimaryKeyId() {
        return name;
    }

    @Override
    public void setPrimaryKeyId(Object id) {

        this.name = (String) id;

    }
}
