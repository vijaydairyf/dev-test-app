package com.devapp.devmain.entity;

import com.devapp.devmain.agentfarmersplit.AppConstants;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by x on 31/1/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CenterPostEntity implements Serializable {
    @JsonProperty
    public ProducerProfile producerProfile;
    @JsonProperty
    public String centerRoute;

    public CenterPostEntity() {

    }

    public CenterPostEntity(CenterEntity entity) {
        producerProfile = new ProducerProfile();
        producerProfile.id = entity.centerId;
        producerProfile.name = entity.centerName;
        producerProfile.barcode = entity.centerBarcode;
        producerProfile.contactPersonName = entity.contactPerson;
        producerProfile.email = entity.contactEmailId;
        producerProfile.regDate = entity.registerDate;
        producerProfile.milkType = entity.cattleType;
        producerProfile.producerType = AppConstants.ConfigurationTypes.COLLECTION_CENTER_LIST;
//        TODO: check if singleOrMultiple is same as producerProfile.isMultipleCans
        centerRoute = entity.centerRoute;
    }

}
