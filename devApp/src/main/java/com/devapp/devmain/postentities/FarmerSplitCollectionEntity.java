package com.devapp.devmain.postentities;

import android.database.sqlite.SQLiteException;

import com.devapp.devmain.ConsolidationPost.SynchronizableElement;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.SplitRecordTable;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by u_pendra on 6/1/18.
 */

public class FarmerSplitCollectionEntity implements Serializable, SynchronizableElement {


    @JsonIgnore
    public long columnId;
    @JsonIgnore
    public String collectionType;


    @JsonIgnore
    public int commited;
    @JsonIgnore
    public int sent;
    @JsonIgnore
    public String centerId;
    @JsonIgnore
    public String agentId;
    @JsonIgnore
    public String date;
    @JsonIgnore
    public String shift;
    @JsonIgnore
    public int smsSentStatus;

    @JsonProperty("farmerSplitEntries")
    public ArrayList<FarmerSplitSubRecords> farmerSplitEntries;
    @JsonProperty("aggregateFarmerId")
    public String aggregateFarmerId;
    public int parentSeqNum;
    @JsonIgnore
    public Date smallestDate;
    @JsonIgnore
    public Date largestDate;

    @Override
    public boolean equals(Object obj) {

        boolean returnValue = false;
        if (obj != null && obj instanceof FarmerSplitCollectionEntity) {
            try {
                if (this.agentId.equalsIgnoreCase(((FarmerSplitCollectionEntity) obj).agentId)
                        && this.shift.equalsIgnoreCase(((FarmerSplitCollectionEntity) obj).shift)
                        && this.date.equalsIgnoreCase(((FarmerSplitCollectionEntity) obj).date)
                        ) {
                    returnValue = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return returnValue;
    }

    @Override
    public long getColumnId() {
        return columnId;
    }

    @Override
    public void setSentStatus(int status) throws SQLiteException {

        String updateQuery = "update " + SplitRecordTable.TABLE
                + " set " + SplitRecordTable.SENT + " = " +
                status
                + " where " + SplitRecordTable.COLUMNID + " = " + this.getColumnId();
        DatabaseHandler.getPrimaryTask().doAction(updateQuery);
        return;
    }

    @Override
    public long calculateMin(long min) {
        if (SmartCCUtil.MIN_DATE == null || SmartCCUtil.MIN_DATE.getTime() > min) {
            SmartCCUtil.MIN_DATE = SmartCCUtil.getCollectionDateFromLongTime(min);
        }
        return 0;
    }

    @Override
    public long calculateMax(long max) {
        if (SmartCCUtil.MAX_DATE == null || SmartCCUtil.MAX_DATE.getTime() < max) {
            SmartCCUtil.MAX_DATE = SmartCCUtil.getCollectionDateFromLongTime(max);
        }
        return 0;
    }


}
