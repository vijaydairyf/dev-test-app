package com.devapp.devmain.encryption;

import com.devapp.devmain.entity.essae.EssaeCleaningEntity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by xx on 22/3/18.
 */

public class CleaningConsolidatedData implements Serializable {


    public String csvVersion = "1.0";
    public ArrayList<EssaeCleaningEntity> records;

}
