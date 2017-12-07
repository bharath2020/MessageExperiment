package com.feathertouch.smsforwarder.database;

import android.provider.BaseColumns;

/**
 * Created by bbooshan on 8/20/16.
 */
public final class SMSDatabaseContract {

    public SMSDatabaseContract() {}



    public static abstract class SMSEntry implements BaseColumns {
        public static final String TABLE_NAME = "SMS";
        public static final String COLUMN_NAME_TEXT = "text";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }

    private static final String TEXT_TYPE = " TEXT";
    public static final String SQL_CREATE_SMS_TABLE =
            "CREATE TABLE " + SMSEntry.TABLE_NAME + " (" +
                    SMSEntry._ID + " INTEGER PRIMARY KEY," +
                    SMSEntry.COLUMN_NAME_NUMBER +  TEXT_TYPE + ", " +
                    SMSEntry.COLUMN_NAME_TIMESTAMP + TEXT_TYPE + ", " +
                    SMSEntry.COLUMN_NAME_TEXT + TEXT_TYPE +
                    " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SMSEntry.TABLE_NAME;
}
