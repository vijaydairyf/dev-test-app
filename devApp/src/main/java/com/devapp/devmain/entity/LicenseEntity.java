package com.devapp.devmain.entity;

import java.io.Serializable;

public class LicenseEntity implements Entity, Serializable {


    public long columnId;
    public long requestTime;
    public String responseStatusCode;
    public long startDate;
    public long endDate;
    public String isValid;
    public String message;


    @Override
    public Object getPrimaryKeyId() {
        return (Long) columnId;
    }

    @Override
    public void setPrimaryKeyId(Object id) {

        this.columnId = (Long) id;

    }
}
