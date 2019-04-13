package com.devapp.devmain.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by u_pendra on 17/5/17.
 */

public class WhiteListUrl implements Serializable {

    public String uri;
    public ArrayList<Integer> ports;
}
