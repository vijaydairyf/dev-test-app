package com.devapp.devmain.dbbackup;

import android.content.ContentValues;

import java.io.Serializable;

/**
 * Created by Upendra on 8/12/2016.
 */
public class TableEntity implements Serializable {

    public String tableName;
    public ContentValues contentValues;
    public String strFilter;
    public String[] strArgs;

}
