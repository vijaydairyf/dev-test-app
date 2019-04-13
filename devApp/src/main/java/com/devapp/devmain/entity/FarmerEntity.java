package com.devapp.devmain.entity;

import com.devapp.devmain.helper.CattleType;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class FarmerEntity implements Serializable, Entity {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    public long id;
    @JsonProperty("name")
    public String farmer_name;
    @JsonProperty("code")
    public String farmer_id;
    @JsonProperty("barcode")
    public String farmer_barcode;
    @JsonProperty("numberOfCans")
    public String farmer_cans;
    @JsonProperty("cattleType")
    public String farmer_cattle;
    @JsonIgnore
    @JsonProperty("centerCode")
    public String soc_code;
    @JsonProperty("mobileNumber")
    public String farm_mob;
    @JsonProperty("emailId")
    public String farm_email;
    @JsonProperty("numberOfCow")
    public String numCow;
    @JsonProperty("numberOfBuffalo")
    public String numBuff;
    @JsonProperty("numberOfCattle")
    public String numCattle;
    @JsonProperty("assignRatechart")
    public String assignRatechart;
    @JsonProperty("regDate")
    public long farmer_regDate;
    public boolean isActive;
    public boolean isMultipleCans;
    @JsonIgnore
    @JsonProperty("sentStatus")
    public int sentStatus;
    @JsonProperty("agentId")
    public String agentId;

    @JsonProperty("producerType")
    public String farmerType;
    @JsonIgnore
    public boolean isReservedFarmer;

    public FarmerEntity() {

    }

    public FarmerEntity(FarmerPostEntity farmerPostEntity) {
        this.farmer_name = farmerPostEntity.producerProfile.name;
        this.farmer_id = farmerPostEntity.producerProfile.id;
        this.farmer_barcode = farmerPostEntity.producerProfile.barcode;
        this.farmer_cans = String.valueOf(farmerPostEntity.nocans);
        this.farmer_cattle = farmerPostEntity.producerProfile.milkType != null ?
                farmerPostEntity.producerProfile.milkType.toUpperCase() : CattleType.COW;
        this.farm_mob = farmerPostEntity.producerProfile.mobileNumber;
        this.farm_email = farmerPostEntity.producerProfile.email;
        this.numCow = String.valueOf(farmerPostEntity.numCows);
        this.numBuff = String.valueOf(farmerPostEntity.numBuffaloes);
        this.numCattle = String.valueOf((farmerPostEntity.numCows + farmerPostEntity.numBuffaloes));
        this.farmer_regDate = farmerPostEntity.producerProfile.regDate;
        this.isActive = farmerPostEntity.producerProfile.isActive;
        this.isMultipleCans = farmerPostEntity.producerProfile.isMultipleCans;
        this.agentId = farmerPostEntity.agentId;
        this.farmerType = farmerPostEntity.producerProfile.producerType;
    }


    @Override
    public Object getPrimaryKeyId() {
        return farmer_id;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        this.farmer_id = (String) id;
    }
}
