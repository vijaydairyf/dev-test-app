package com.devapp.syncapp.json.receive;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by Pankaj on 5/1/2018.
 */

public class LastRecordDetails {

    @JsonProperty("lastSeqNo")
    private long lastSeqNo;
    @JsonProperty("lastPostShift")
    private String lastPostShift;
    @JsonProperty("lastPostDate")
    private long lastPostDate;

    public long getLastSeqNo() {
        return lastSeqNo;
    }

    public void setLastSeqNo(long lastSeqNo) {
        this.lastSeqNo = lastSeqNo;
    }

    public String getLastPostShift() {
        return lastPostShift == null ? "" : this.lastPostShift;
    }

    public void setLastPostShift(String lastPostShift) {
        this.lastPostShift = lastPostShift;
    }

    public long getLastPostDate() {
        return lastPostDate;
    }

    public void setLastPostDate(long lastPostDate) {
        this.lastPostDate = lastPostDate;
    }
}
