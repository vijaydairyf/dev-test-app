package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by x on 4/2/18.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class IncentiveRateChartPostEntity implements Entity {

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
    public String name;
    @JsonProperty
    public String rateChartType;
    @JsonProperty
    public List<BonusChartEntity> valueList;

    @Override
    public Object getPrimaryKeyId() {
        return name;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        name = (String) id;
    }
}
