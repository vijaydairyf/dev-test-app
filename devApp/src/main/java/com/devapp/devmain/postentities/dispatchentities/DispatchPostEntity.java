package com.devapp.devmain.postentities.dispatchentities;

import com.devapp.devmain.ConsolidationPost.CustomSerializable;
import com.devapp.devmain.ConsolidationPost.SynchronizableElement;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.postentities.QualityParamsPost;
import com.devapp.devmain.postentities.QuantityParamspost;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.DispatchCollectionTable;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by xx on 9/9/18.
 */

public class DispatchPostEntity implements Serializable, Entity, SynchronizableElement {

    @JsonIgnore
    public long columnId;
    @JsonIgnore
    public int sentStatus;
    @JsonIgnore
    public long timeInMillis;
    @JsonIgnore
    public String date;
    @JsonIgnore
    public String time;
    @JsonIgnore
    public String shift;
    public String supervisorId;
    public int numberOfCans;
    public String milkType;
    @JsonSerialize(using = CustomSerializable.class)
    public Date collectionTime;
    public QuantityParamspost quantityParams;
    public QualityParamsPost qualityParams;
    public long sequenceNumber;
    public long lastModified;

    @Override
    public Object getPrimaryKeyId() {
        return columnId;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        this.columnId = (long) id;
    }

    @Override
    public long getColumnId() {
        return columnId;
    }

    @Override
    public void setSentStatus(int status) {
        String updateQuery = "update " + DispatchCollectionTable.TABLE_DISPATCH_REPORT
                + " set " + DispatchCollectionTable.KEY_SEND_STATUS + " = " + status
                + ", " + FarmerCollectionTable.LAST_MODIFIED + " = " + this.lastModified
                + " where " + DispatchCollectionTable.KEY_COLUMN_ID + " = " + this.getColumnId();
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
