package com.devapp.devmain.postentities;

import android.database.sqlite.SQLiteException;

import com.devapp.devmain.ConsolidationPost.CustomSerializable;
import com.devapp.devmain.ConsolidationPost.SynchronizableElement;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.ExtraParams;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

/**
 * Created by u_pendra on 6/1/18.
 */

public class AdditionFieldEditEntity implements SynchronizableElement {

    @JsonProperty("sequenceNumber")
    public long seqNum;
    @JsonIgnore
    public long columnId;
    @JsonIgnore
    public int sentStatus;
    @JsonIgnore
    public int smsSentStatus;
    @JsonIgnore
    public String key;
    @JsonProperty("extraParametersData")
    public String extraParameters;
    @JsonSerialize(using = CustomSerializable.class)
    public Date updatedTime;
    @JsonSerialize(using = CustomSerializable.class)
    public Date collectionTime;
    public String farmerId;
    @JsonIgnore
    public String date;
    @JsonIgnore
    public String shift;
    public String milkType;

    @JsonProperty("refSequenceNumber")
    public long refSeqNum;
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

        String updateQuery = "update " + ExtraParams.TABLE_EXTRA_PARAMS
                + " set " + ExtraParams.SENT_STATUS + " = " +
                CollectionConstants.SENT
                + " where " + ExtraParams.COLUMN_ID + " = " + this.getColumnId();
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
