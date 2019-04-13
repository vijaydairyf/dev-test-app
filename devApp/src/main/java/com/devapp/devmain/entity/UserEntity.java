package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;

public class UserEntity extends Utility implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    @JsonProperty("name")
    public String name;
    @JsonProperty("role")
    public String role;
    @JsonProperty("userId")
    public String userId;
    @JsonProperty("password")
    public String password;
    @JsonProperty("mobileNumber")
    public String mobile;
    @JsonProperty("emailId")
    public String emailId;
    @JsonIgnore
    @JsonProperty("centerId")
    public String centerId;
    @JsonIgnore
    @JsonProperty("regDate")
    public long regDate;
    @JsonIgnore
    @JsonProperty("weekDate")
    public long weekDate;
    @JsonIgnore
    @JsonProperty("monthDate")
    public long monthDate;
    @JsonIgnore
    @JsonProperty("sentStatus")
    public int sentStatus;


    boolean isValidNumber() {
        return super.isValidNumber(this.mobile);
    }

    String getOperationalMobileNumber() {
        return super.getOperationalMobileNumber(this.mobile);
    }
}
