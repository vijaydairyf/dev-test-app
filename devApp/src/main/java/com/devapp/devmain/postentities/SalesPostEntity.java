package com.devapp.devmain.postentities;

import android.database.sqlite.SQLiteException;

import com.devapp.devmain.ConsolidationPost.CustomSerializable;
import com.devapp.devmain.ConsolidationPost.SynchronizableElement;
import com.devapp.devmain.postentities.salesEntities.Id;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.SalesCollectionTable;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by u_pendra on 5/1/18.
 */

public class SalesPostEntity implements Serializable, SynchronizableElement {

    @JsonIgnore
    public long seqNum;
    //@JsonIgnore
    public long columnId;
    @JsonIgnore
    public String date;
    @JsonIgnore
    public String shift;
    @JsonIgnore
    public int smsSentStatus;

    public Id id;

    public String salesId;
    @JsonSerialize(using = CustomSerializable.class)
    public Date salesTime;
    public String mode;
    public QuantityParamspost quantityParams;
    public QualityParamsPost qualityParams;
    public RateParamsPost rateParams;
    public SalesTransaction salesTransaction;

    public String milkType;

    public String status;
    public long sequenceNumber;
    @JsonIgnore
    public Date smallestDate;
    @JsonIgnore
    public Date largestDate;

    public long getColumnId() {
        return columnId;
    }

    @Override
    public void setSentStatus(int status) throws SQLiteException {

        String updateQuery = "update " + SalesCollectionTable.TABLE_SALES_REPORT
                + " set " + SalesCollectionTable.KEY_SALES_SEND_STATUS + " = " + status
                + " where " + SalesCollectionTable.KEY_SALES_COLUMNID + " = " + this.getColumnId();
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
