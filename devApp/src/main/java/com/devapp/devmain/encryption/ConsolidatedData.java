package com.devapp.devmain.encryption;

import com.devapp.devmain.postentities.ConsolidatedPostData;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by u_pendra on 28/9/17.
 */

public class ConsolidatedData implements Serializable {


    public String csvVersion = "3.0";
    public ArrayList<ConsolidatedPostData> records;

}
