package com.pens.afdolash.bytan.other;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pens.afdolash.bytan.main.profile.model.History;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by afdol on 5/29/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "bytan_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create history table
        db.execSQL(History.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + History.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public long insertHistory(String heartRate, String objTemp, String ambTemp, String code, String lat, String lng) {
        // Get writable database as we want to write data
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        // `id` and `timestamp` will be inserted automatically.
        // No need to add them
        values.put(History.COLUMN_HEART, heartRate);
        values.put(History.COLUMN_OBJTEMP, objTemp);
        values.put(History.COLUMN_AMBTEMP, ambTemp);
        values.put(History.COLUMN_CODE, code);
        values.put(History.COLUMN_LATITUDE, lat);
        values.put(History.COLUMN_LONGITUDE, lng);

        // Insert row
        long id = db.insert(History.TABLE_NAME, null, values);

        // Close db connection
        db.close();

        // Return newly inserted row id
        return id;
    }

    public History getHistory(long id) {
        // Get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(History.TABLE_NAME,
                new String[]{
                        History.COLUMN_ID,
                        History.COLUMN_HEART,
                        History.COLUMN_OBJTEMP,
                        History.COLUMN_AMBTEMP,
                        History.COLUMN_CODE,
                        History.COLUMN_LATITUDE,
                        History.COLUMN_LONGITUDE
                },
                History.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        // Prepare history object
        History history = new History(
                cursor.getInt(cursor.getColumnIndex(History.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_HEART)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_OBJTEMP)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_AMBTEMP)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_CODE)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_LATITUDE)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_LONGITUDE)),
                cursor.getString(cursor.getColumnIndex(History.COLUMN_TIMESTAMP)));

        // close the db connection
        cursor.close();

        return history;
    }

    public List<History> getAllHistory() {
        List<History> histories = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + History.TABLE_NAME + " ORDER BY " +
                History.COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                History history = new History();
                history.setId(cursor.getInt(cursor.getColumnIndex(History.COLUMN_ID)));
                history.setHeartRate(cursor.getString(cursor.getColumnIndex(History.COLUMN_HEART)));
                history.setObjTemp(cursor.getString(cursor.getColumnIndex(History.COLUMN_OBJTEMP)));
                history.setAmbTemp(cursor.getString(cursor.getColumnIndex(History.COLUMN_AMBTEMP)));
                history.setCode(cursor.getString(cursor.getColumnIndex(History.COLUMN_CODE)));
                history.setLatitude(cursor.getString(cursor.getColumnIndex(History.COLUMN_LATITUDE)));
                history.setLongitude(cursor.getString(cursor.getColumnIndex(History.COLUMN_LONGITUDE)));
                history.setTimestamp(cursor.getString(cursor.getColumnIndex(History.COLUMN_TIMESTAMP)));

                histories.add(history);
            } while (cursor.moveToNext());
        }

        // close db connection
        db.close();

        // return histories list
        return histories;
    }

    public int getHistoryCount() {
        String countQuery = "SELECT  * FROM " + History.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();


        // return count
        return count;
    }

    public void deleteHistory(History history) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(History.TABLE_NAME, History.COLUMN_ID + " = ?",
                new String[]{String.valueOf(history.getId())});
        db.close();
    }
}
