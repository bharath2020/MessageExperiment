package com.feathertouch.smsforwarder;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.feathertouch.smsforwarder.database.SMSDatabaseContract;
import com.feathertouch.smsforwarder.database.SMSDbHelper;
import com.feathertouch.smsforwarder.model.SMSMessage;

/**
 * Created by bbooshan on 8/20/16.
 */
public class SMSReaderWriter {

    private SMSDbHelper db;
    private SQLiteDatabase writer;
    public SMSReaderWriter(SMSDbHelper db){
        this.db = db;
    }

    @Override
    protected void finalize() throws Throwable {
        this.db.close();
        super.finalize();
    }

    public void addMessages(SMSMessage[] messages){
       SQLiteDatabase writer = db.getWritableDatabase();

        for( int index = 0; index < messages.length; index++ ){
            SMSMessage message = messages[index];

            ContentValues values = new ContentValues();
            values.put(SMSDatabaseContract.SMSEntry.COLUMN_NAME_TEXT, message.text);
            values.put(SMSDatabaseContract.SMSEntry.COLUMN_NAME_NUMBER, message.number);
            values.put(SMSDatabaseContract.SMSEntry.COLUMN_NAME_TIMESTAMP, message.timestampInMillis);

            long newRowId;
            newRowId = writer.insert(SMSDatabaseContract.SMSEntry.TABLE_NAME,null,values);
            message.uniqueId = new Long(newRowId);
        }
        if ( writer != null ){
            writer.close();
        }
    }

    public void deleteMessages(SMSMessage[] messages){
        SQLiteDatabase writer = db.getWritableDatabase();

        String idsToDelete = "(";
        for( int index=0; index< messages.length; index++ ){
            SMSMessage message = messages[index];
            if( message.uniqueId != -1L){
                idsToDelete = idsToDelete + message.uniqueId;
                if ( index != messages.length-1){
                    idsToDelete = idsToDelete + ",";
                }
            }
        }
        idsToDelete = idsToDelete + ")";

        if ( !idsToDelete.equals("()") ){
            writer.delete(SMSDatabaseContract.SMSEntry.TABLE_NAME, SMSDatabaseContract.SMSEntry._ID + " IN " + idsToDelete, null);
        }
        if ( writer != null ){
            writer.close();
        }
    }

    public SMSMessage[] allMessages(){
        SMSMessage[] messages = null;
        SQLiteDatabase writer = db.getWritableDatabase();

        String[] projection = {
                SMSDatabaseContract.SMSEntry._ID,
                SMSDatabaseContract.SMSEntry.COLUMN_NAME_NUMBER,
                SMSDatabaseContract.SMSEntry.COLUMN_NAME_TEXT,
                SMSDatabaseContract.SMSEntry.COLUMN_NAME_TIMESTAMP
        };

        Cursor c = writer.query(
                SMSDatabaseContract.SMSEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (c.getCount()> 0 ){
             messages = new SMSMessage[c.getCount()];
            int count  =0;
            while( c.moveToNext() ){

                String text = c.getString(2);
                String number = c.getString(1);
               long timeStampInMillis = c.getLong(3);
                long ID = c.getLong(0);

                SMSMessage message = new SMSMessage(number,text,ID);
                Log.wtf("SMSReaderWriter", "DB GET: " + message.toString());
                message.timestampInMillis = timeStampInMillis;
                messages[count++] = message;

            }


        }

        if ( c != null ){
            c.close();
        }
        if ( writer != null ){
            writer.close();
        }
        return messages;
    }


}
