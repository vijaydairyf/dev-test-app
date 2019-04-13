package com.devapp.devmain.postentities;

import com.devapp.devmain.ConsolidationPost.CustomSerializable;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

/**
 * Created by u_pendra on 17/1/18.
 */

public class ConcludedShift {

    @JsonSerialize(using = CustomSerializable.class)
    public Date date;
    public String shift;
    public int numberOfShifts;
}
