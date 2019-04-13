package com.devapp.devmain.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class PostUpdatedEndShift implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public String collectionDate;
    public String shift;
    public long startTime;
    public long endTime;
    public String societyId;
    public boolean endShift;
    public ArrayList<? extends UpdatedRecordEntity> collectionEntryList;

}