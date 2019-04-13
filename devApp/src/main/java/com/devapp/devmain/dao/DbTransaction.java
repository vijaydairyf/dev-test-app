package com.devapp.devmain.dao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x on 30/7/18.
 */

public class DbTransaction {


    public static final int SECONDARY_FAILED = 1;
    List<Dao> daoList = new ArrayList<>();
    private int transactionStatus = 0;

    public void add(Dao dao) {
        daoList.add(dao);
        dao.setTransactionTracker(this);
    }

    public void add(List<Dao> daos) {
        daoList.addAll(daos);
        for (Dao dao : daos) {
            dao.setTransactionTracker(this);
        }
    }

    public void startTransaction() {
        if (daoList.size() > 0) {
            daoList.get(0).startTransaction();
        }
    }

    public void endTransaction() {
        if (daoList.size() > 0) {
            daoList.get(0).endTransaction();
        }
        resetTransactionTrackers();
    }

    public void setTransactionSuccessful() {
        if (daoList.size() > 0) {
            daoList.get(0).setTransactionSuccessful();
        }
    }

    private void resetTransactionTrackers() {
        transactionStatus = 0;
        for (Dao dao : daoList) {
            dao.setTransactionTracker(null);
        }
    }

    public int getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(int transactionStatus) {
        this.transactionStatus = transactionStatus;
    }
}
