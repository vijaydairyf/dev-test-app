package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by x on 24/1/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FarmerPostEntity implements Serializable {
    @JsonProperty("producerProfile")
    ProducerProfile producerProfile;
    @JsonProperty
    String agentId;
    @JsonProperty
    int numCows;
    @JsonProperty
    int numBuffaloes;
    @JsonProperty
    int nocans;

    public FarmerPostEntity() {
    }

    public FarmerPostEntity(FarmerEntity farmerEntity) {
        producerProfile = new ProducerProfile();
        producerProfile.name = farmerEntity.farmer_name;
        producerProfile.id = farmerEntity.farmer_id;
        producerProfile.barcode = farmerEntity.farmer_barcode;
        producerProfile.milkType = farmerEntity.farmer_cattle;
        producerProfile.mobileNumber = farmerEntity.farm_mob;
        producerProfile.email = farmerEntity.farm_email;
        producerProfile.isActive = farmerEntity.isActive;
        producerProfile.regDate = farmerEntity.farmer_regDate;
        producerProfile.isMultipleCans = farmerEntity.isMultipleCans;
        producerProfile.producerType = farmerEntity.farmerType;
        this.agentId = farmerEntity.agentId;
        if (farmerEntity.numCow != null) {
            this.numCows = Integer.parseInt(farmerEntity.numCow);
        }
        if (farmerEntity.numBuff != null) {
            this.numBuffaloes = Integer.parseInt(farmerEntity.numBuff);
        }
        if (farmerEntity.farmer_cans != null) {
            this.nocans = Integer.parseInt(farmerEntity.farmer_cans);
        }

    }
}
