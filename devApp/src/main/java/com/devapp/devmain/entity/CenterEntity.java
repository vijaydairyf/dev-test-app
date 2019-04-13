package com.devapp.devmain.entity;

import com.devapp.devmain.user.Util;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Upendra on 1/5/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CenterEntity implements Serializable, Entity {

    public String chillingCenterId;
    public String centerId;
    public String centerName;
    public String centerBarcode;
    public String contactPerson;
    public String contactNumber;
    public String contactEmailId;
    public String centerRoute;
    public long registerDate;
    public String singleOrMultiple;
    public String cattleType;
    public int sentStatus;
    public String activeStatus;

    public CenterEntity() {

    }

    public CenterEntity(CenterPostEntity entity) {
        centerId = entity.producerProfile.id.toUpperCase();
        centerName = entity.producerProfile.name;
        centerBarcode = entity.producerProfile.barcode;
        contactPerson = entity.producerProfile.contactPersonName;
        contactEmailId = entity.producerProfile.email;
        registerDate = entity.producerProfile.regDate;
        cattleType = entity.producerProfile.milkType.toUpperCase();
        activeStatus = entity.producerProfile.isActive ? "1" : "0";
        singleOrMultiple = entity.producerProfile.isMultipleCans ? Util.MULTIPLE : Util.SINGLE;
        centerRoute = entity.centerRoute;
    }

    @Override
    public Object getPrimaryKeyId() {
        return centerId;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        this.centerId = (String) id;
    }
}
