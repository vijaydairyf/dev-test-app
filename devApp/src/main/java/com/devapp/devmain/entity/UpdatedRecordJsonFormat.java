package com.devapp.devmain.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Upendra on 4/28/2016.
 */
public class UpdatedRecordJsonFormat implements Serializable {

    private static final long serialVersionUID = 1L;
    public String currentDate;
    public String currentShift;
    public int numberOfShifts;
    public String centerId;
    public ArrayList<? extends PostUpdatedEndShift> collectionEntrySetUpdateList;
}
