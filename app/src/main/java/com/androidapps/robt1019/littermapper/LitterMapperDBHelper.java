package com.androidapps.robt1019.littermapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Date;

/**
 * Created by rob on 19/06/15.
 */


public class LitterMapperDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "litter_mapper.sqlite";
    private static final int DB_VERSION = 1;

    // 'litter' table fields etc.
    private static final String TABLE_LITTER = "litter";
    private static final String COLUMN_LITTER_ID = "_id";
    private static final String COLUMN_LITTER_BRAND = "brand";
    private static final String COLUMN_LITTER_TYPE = "type";
    private static final String COLUMN_LITTER_DATE = "date";

    // Currently set up to use default cursor
    public LitterMapperDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        getWritableDatabase();
        close();
    }

    // Set up initial tables and data
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_LITTER + "  (" +
                COLUMN_LITTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_LITTER_BRAND + " VARCHAR," +
                COLUMN_LITTER_TYPE + " VARCHAR," +
                COLUMN_LITTER_DATE + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // For implementing schema changes. Maybe for backing up to cloud storage?
    }

    public Long insertLitter (Litter litter) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LITTER_BRAND, litter.getBrand());
        cv.put(COLUMN_LITTER_TYPE, litter.getType());
        cv.put(COLUMN_LITTER_DATE, litter.getDate().getTime());
        return getWritableDatabase().insert(TABLE_LITTER, null, cv);
    }

    public LitterCursor queryLitterItems() {
        // Equivalent to "SELECT * FROM litter ORDER BY start_date ASC
        Cursor wrapped = getReadableDatabase().query(TABLE_LITTER,
                null, null, null, null, null, COLUMN_LITTER_DATE + " ASC");
        return new LitterCursor(wrapped);
    }

    // Convenience class to wrap a cursor that returns rows from the 'litter' table.
    // the getLitter() method will give you a litter instance representing the current row
    public static class LitterCursor extends CursorWrapper {

        public LitterCursor(Cursor c) {
            super(c);
        }

        // Returns a Litter object configured for the current row or null
        // if row is invalid
        public Litter getLitter() {
            if (isBeforeFirst() || isAfterLast()) {
                return null;
            }
            Litter litter = new Litter();
            long litterId = getLong(getColumnIndex(COLUMN_LITTER_ID));
            litter.setId(litterId);
            long date = getLong(getColumnIndex(COLUMN_LITTER_DATE));
            litter.setDate(new Date(date));
            String brand = getString(getColumnIndex(COLUMN_LITTER_BRAND));
            litter.setBrand(brand);
            String type = getString(getColumnIndex(COLUMN_LITTER_TYPE));
            litter.setType(type);
            return litter;
        }
    }
}
