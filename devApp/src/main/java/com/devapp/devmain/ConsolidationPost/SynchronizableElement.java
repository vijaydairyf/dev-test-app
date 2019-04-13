package com.devapp.devmain.ConsolidationPost;

import android.database.sqlite.SQLiteException;

/**
 * Created by u_pendra on 19/1/18.
 */

public interface SynchronizableElement {


    long getColumnId();

    void setSentStatus(int status) throws SQLiteException;


    long calculateMin(long min);

    long calculateMax(long max);


}
