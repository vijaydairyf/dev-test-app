package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

/**
 * Created by x on 2/2/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigEntity implements Serializable, Entity {
    @JsonProperty
    public String name;
    @JsonProperty
    public String value;
    @JsonProperty
    public long lastModified;

    @Override
    public Object getPrimaryKeyId() {
        return name;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        name = (String) id;
    }
}
