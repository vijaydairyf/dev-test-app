package com.devapp.devmain.agentfarmersplit;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.postentities.CollectionConstants;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.Serializable;

/**
 * Created by u_pendra on 23/10/17.
 */


public class AgentSplitEntity implements Serializable, Entity {


    @JsonIgnore
    public int parentSeqNum;
    @JsonIgnore
    public long columnId;
    public long collectionTime;
    public int seqNum;
    public String producerId;
    public String milkType;
    public String status;
    public String mode;
    public int numberOfCans;
    public String userId;
    @JsonIgnore
    public String collectionType;
    public QualityParams qualityParams;
    public QuantityParams quantityParams;
    public RateParams rateParams;
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
    public int sampleNumber;
    public int smsSent;
    public String postShift;
    public String postDate;
    public long sequenceNumber;
    @JsonIgnore
    public long updatedTime;

    public AgentSplitEntity() {
        this.sent = CollectionConstants.UNSENT;
        this.smsSent = CollectionConstants.UNSENT;
    }

    public long getColumnId() {
        return columnId;
    }

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }

    public int getParentSeqNum() {
        return parentSeqNum;
    }

    public void setParentSeqNum(int parentSeqNum) {
        this.parentSeqNum = parentSeqNum;
    }

    public long getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(long collectionTime) {
        this.collectionTime = collectionTime;
    }

    public int getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(int seqNum) {
        this.seqNum = seqNum;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public String getMilkType() {
        return milkType;
    }

    public void setMilkType(String milkType) {
        this.milkType = milkType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getNumberOfCans() {
        return numberOfCans;
    }

    public void setNumberOfCans(int numberOfCans) {
        this.numberOfCans = numberOfCans;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public QualityParams getQualityParams() {
        return qualityParams;
    }

    public void setQualityParams(QualityParams qualityParams) {
        this.qualityParams = qualityParams;
    }

    public QuantityParams getQuantityParams() {
        return quantityParams;
    }

    public void setQuantityParams(QuantityParams quantityParams) {
        this.quantityParams = quantityParams;
    }

    public RateParams getRateParams() {
        return rateParams;
    }

    public void setRateParams(RateParams rateParams) {
        this.rateParams = rateParams;
    }

    public int getCommited() {
        return commited;
    }

    public void setCommited(int commited) {
        this.commited = commited;
    }

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
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

    public int getSampleNumber() {
        return sampleNumber;
    }

    public void setSampleNumber(int sampleNumber) {
        this.sampleNumber = sampleNumber;
    }

    public int getSmsSent() {
        return smsSent;
    }

    public void setSmsSent(int smsSent) {
        this.smsSent = smsSent;
    }

    public String getPostShift() {
        return postShift;
    }

    public void setPostShift(String postShift) {
        this.postShift = postShift;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public boolean equals(Object obj) {

        boolean returnValue = false;
        if (obj != null && obj instanceof AgentSplitEntity) {
            try {
                if (this.agentId.equalsIgnoreCase(((AgentSplitEntity) obj).agentId)
                        && this.shift.equalsIgnoreCase(((AgentSplitEntity) obj).shift)
                        && this.date.equalsIgnoreCase(((AgentSplitEntity) obj).date)) {
                    returnValue = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return returnValue;
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
        this.sent = CollectionConstants.UNSENT;
        this.smsSent = CollectionConstants.UNSENT;
    }
}
