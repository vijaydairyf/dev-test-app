package com.devapp.devmain.postentities;

import android.database.sqlite.SQLiteException;

import com.devapp.devmain.ConsolidationPost.CustomSerializable;
import com.devapp.devmain.ConsolidationPost.SynchronizableElement;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.RouteCollectionTable;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by u_pendra on 5/1/18.
 */

public class RoutePostEntity implements Serializable, SynchronizableElement {

    @JsonProperty("sequenceNumber")
    public long seqNum;
    @JsonIgnore
    public long columnId;
    @JsonIgnore
    public String date;
    @JsonIgnore
    public String shift;
    @JsonIgnore
    public int sentStatus;
    @JsonIgnore
    public int smsSentStatus;

    public CanInOut can;


    public String routeId;
    @JsonSerialize(using = CustomSerializable.class)
    public Date securityTime;
    public double recoveryAmount;
    public String material;
    public String remarks;
    @JsonIgnore
    public Date smallestDate;
    @JsonIgnore
    public Date largestDate;

    public long getColumnId() {
        return columnId;
    }

    @Override
    public void setSentStatus(int status) throws SQLiteException {

        String updateQuery = "update " + RouteCollectionTable.TABLE_TRUCK_DETAILS
                + " set " + RouteCollectionTable.KEY_TRUCK_SENT_STATUS + " = " +
                status
                + " where " + RouteCollectionTable.KEY_TRUCK_COLUMN_ID + " = " + this.getColumnId();
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
