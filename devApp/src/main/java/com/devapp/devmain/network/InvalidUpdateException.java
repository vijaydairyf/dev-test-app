package com.devapp.devmain.network;

/**
 * Created by x on 3/2/18.
 */

public class InvalidUpdateException extends Exception {


    private int updateStatus;

    public InvalidUpdateException(String message, int updateStatus) {
        super(message);
        this.updateStatus = updateStatus;
    }

    public int getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(int updateStatus) {
        this.updateStatus = updateStatus;
    }

}
