package com.devapp.devmain.postentities;

import android.database.sqlite.SQLiteException;

import com.devapp.devmain.ConsolidationPost.CustomSerializable;
import com.devapp.devmain.ConsolidationPost.SynchronizableElement;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.TankerCollectionTable;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by u_pendra on 5/1/18.
 */

public class TankerPostEntity implements SynchronizableElement {


    @JsonIgnore
    public long seqNum;
    @JsonIgnore
    public long columnId;
    @JsonIgnore
    public String date;
    @JsonIgnore
    public String shift;
    @JsonIgnore
    public int smsSentStatus;

    public String comments;

    @JsonIgnore
    public int sent;

    @JsonProperty("tanker")
    public TankerMetadata tankerMetadata;

    @JsonProperty("qualityParams")
    public QualityParamsPost qualityParamsPost;

    @JsonProperty("quantityParams")
    public QuantityParamspost quantityParamspost;

    public ArrayList<String> compartmentNumbers;

    @JsonSerialize(using = CustomSerializable.class)
    @JsonProperty("collectionTime")
    public Date collectionTime;


    public long sequenceNumber;

    public String status;
    @JsonIgnore
    public Date smallestDate;
    @JsonIgnore
    public Date largestDate;

    @Override
    public long getColumnId() {
        return columnId;
    }

    @Override
    public void setSentStatus(int status) throws SQLiteException {
        String updateQuery = "update " + TankerCollectionTable.TABLE_TANKER
                + " set " + TankerCollectionTable.KEY_SENT + " = " + status
                + " where " + TankerCollectionTable.KEY_COLUMN_ID + " = " + this.getColumnId();
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
