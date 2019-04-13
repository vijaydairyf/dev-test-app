package com.devapp.devmain.entity;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.smartcc.entities.AgentEntity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by x on 31/1/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AgentPostEntity implements Serializable {
    @JsonProperty
    public ProducerProfile producerProfile;
    @JsonProperty
    public String uniqueID1;
    @JsonProperty
    public String uniqueID2;
    @JsonProperty
    public String uniqueID3;
    @JsonProperty
    public String shiftsSupplyingTo;
    @JsonProperty
    public String pickupPoint;
    @JsonProperty
    public Double distanceToDelivery;
    @JsonProperty
    public List<String> centerIdList;

    public AgentPostEntity() {

    }

    public AgentPostEntity(AgentEntity entity) {
        producerProfile = new ProducerProfile();
        producerProfile.id = entity.agentID;
        producerProfile.barcode = entity.barcode;
        producerProfile.name = entity.firstName;
        producerProfile.mobileNumber = entity.mobileNum;
        producerProfile.milkType = entity.milkType;
        producerProfile.regDate = entity.registeredDate;
        producerProfile.producerType = AppConstants.ConfigurationTypes.AGENT;
        uniqueID1 = entity.uniqueID1;
        uniqueID2 = entity.uniqueID2;
        uniqueID3 = entity.uniqueID3;
        shiftsSupplyingTo = entity.shiftsSupplyingTo;
        pickupPoint = entity.pickupPoint;
        distanceToDelivery = entity.distanceToDelivery;
        centerIdList = entity.centerList;
    }
}
