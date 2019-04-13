package com.devapp.devmain.additionalRecords;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.TransactionalEntity;
import com.devapp.devmain.postentities.CollectionConstants;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Created by x on 26/7/17.
 */

@TransactionalEntity
public class AdditionalParamsEntity implements Entity {


    @JsonIgnore
    public long columnId;
    @JsonIgnore
    public int sentStatus;
    public String key;
    public String extraParameters;
    public long updatedTime;
    public long collectionTime;
    public String farmerId;
    public String date;
    public String shift;
    public String milkType;
    public long seqNum;
    public long refSeqNum;
    public int smsSentStatus;
    public String postDate;
    public String postShift;

    public AdditionalParamsEntity() {
        this.sentStatus = CollectionConstants.UNSENT;
        this.smsSentStatus = CollectionConstants.UNSENT;
    }

    public long getColumnId() {
        return columnId;
    }

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }

    public int getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(int sentStatus) {
        this.sentStatus = sentStatus;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getExtraParameters() {
        return extraParameters;
    }

    public void setExtraParameters(String extraParameters) {
        this.extraParameters = extraParameters;
    }

    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }

    public long getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(long collectionTime) {
        this.collectionTime = collectionTime;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public String getMilkType() {
        return milkType;
    }

    public void setMilkType(String milkType) {
        this.milkType = milkType;
    }

    public long getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(long seqNum) {
        this.seqNum = seqNum;
    }

    public long getRefSeqNum() {
        return refSeqNum;
    }

    public void setRefSeqNum(long refSeqNum) {
        this.refSeqNum = refSeqNum;
    }

    public int getSmsSentStatus() {
        return smsSentStatus;
    }

    public void setSmsSentStatus(int smsSentStatus) {
        this.smsSentStatus = smsSentStatus;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostShift() {
        return postShift;
    }

    public void setPostShift(String postShift) {
        this.postShift = postShift;
    }

    @Override
    public Object getPrimaryKeyId() {
        return columnId;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        this.columnId = (long) id;
    }

    public void resetSentMarkers() {
        this.sentStatus = CollectionConstants.UNSENT;
        this.smsSentStatus = CollectionConstants.UNSENT;
    }
}


