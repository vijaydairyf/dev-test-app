package com.devapp.devmain.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by u_pendra on 16/2/17.
 */

public class ConfigurationPushEntity implements Serializable {

    public String deviceId;
    public String societyId;
    public ArrayList<ConfigurationElement> configurationList;

}
