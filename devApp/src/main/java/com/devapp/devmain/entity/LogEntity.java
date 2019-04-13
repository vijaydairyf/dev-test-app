package com.devapp.devmain.entity;

import android.database.sqlite.SQLiteException;

import com.devapp.devmain.ConsolidationPost.CustomSerializable;
import com.devapp.devmain.ConsolidationPost.SynchronizableElement;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.devmain.tableEntities.LogTable;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by u_pendra on 7/8/18.
 */

public class LogEntity implements Entity, Serializable, SynchronizableElement {

    @JsonIgnore
    public long columnId;
    public String type;
    @JsonSerialize(using = CustomSerializable.class)
    public Date receivedDate;
    public String metaData;

    @JsonIgnore
    public long receivedTime;


    public LogId id;
    @JsonIgnore
    public int sentStatus;

    //This can be ONLINE or OFFLINE
    public String mode;


    @Override
    public Object getPrimaryKeyId() {
        return this.columnId;
    }

    @Override
    public void setPrimaryKeyId(Object id) {

        this.columnId = (Long) id;
    }

    @Override
    public long getColumnId() {
        return this.columnId;
    }

    @Override
    public void setSentStatus(int status) throws SQLiteException {

        String updateQuery = "update " + LogTable.TABLE
                + " set " + LogTable.SENT_STATUS + " = " + status + ", "
                + " where " + FarmerCollectionTable.KEY_REPORT_COLUMNID + " = " + this.getColumnId();

        DatabaseHandler.getPrimaryTask().doAction(updateQuery);

        return;
    }


    @Override
    public long calculateMin(long min) {
        return 0;
    }

    @Override
    public long calculateMax(long max) {
        return 0;
    }
}
