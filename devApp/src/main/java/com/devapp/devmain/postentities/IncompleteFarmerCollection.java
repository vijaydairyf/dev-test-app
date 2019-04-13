package com.devapp.devmain.postentities;

import android.database.sqlite.SQLiteException;

import com.devapp.devmain.ConsolidationPost.CustomSerializable;
import com.devapp.devmain.ConsolidationPost.SynchronizableElement;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.Date;

/**
 * Created by u_pendra on 20/1/18.
 */

public class IncompleteFarmerCollection implements SynchronizableElement {


    //    @JsonIgnore
    public long columnId;
    @JsonIgnore
    public String date;
    @JsonIgnore
    public String shift;
    @JsonIgnore
    public int sentStatus;
    @JsonIgnore
    public int smsSentStatus;


    @JsonProperty("id")
    public FarmerCollectionId collectionId;

    public String collectionRoute;
    public String milkType;
    public String mode;
    public String status;
    public String recordStatus;
    @JsonSerialize(using = CustomSerializable.class)
    public Date collectionTime;
    public int numberOfCans;
    public int sampleNumber;
    public long sequenceNumber;
    @JsonProperty("qualityParams")
    public QualityParamsPost qualityParams;
    @JsonProperty("quantityParams")
    public QuantityParamspost quantityParams;
    @JsonProperty("rateParams")
    public RateParamsPost rateParams;
    @JsonIgnore
    public Date smallestDate;
    @JsonIgnore
    public Date largestDate;
    //    @JsonIgnore
    public long lastModified;

    @Override
    public long getColumnId() {
        return columnId;
    }

    @Override
    public void setSentStatus(int status) throws SQLiteException {

        String updateQuery = "update " + FarmerCollectionTable.TABLE_REPORTS
                + " set " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " +
                status + ", " + FarmerCollectionTable.LAST_MODIFIED + " = " + this.lastModified
                + " where " + FarmerCollectionTable.KEY_REPORT_COLUMNID + " = " + this.getColumnId();
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

