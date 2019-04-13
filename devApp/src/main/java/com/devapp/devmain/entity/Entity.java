package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Created by u_pendra on 4/2/18.
 */

public interface Entity {

    @JsonIgnore
    Object getPrimaryKeyId();

    void setPrimaryKeyId(Object id);
}
