package com.androidapps.robt1019.littermapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rob on 19/06/15.
 */


public class LitterMapperDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "litter_mapper.db";
    private static final int DB_VERSION = 1;

    // 'litter' table fields etc.
    private static final String TABLE_LITTER = "litter";
    private static final String COLUMN_LITTER_ID = "_id";
    private static final String COLUMN_LITTER_BRAND = "brand";
    private static final String COLUMN_LITTER_DATE = "date";

    // Currently set up to use default cursor
    public LitterMapperDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Set up initial tables and data
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Create "litter" table
            db.execSQL("CREATE TABLE" + TABLE_LITTER + "  (" +
                    COLUMN_LITTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_LITTER_BRAND + " VARCHAR," +
                    COLUMN_LITTER_DATE + " INTEGER);");
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // For implementing schema changes. Maybe for backing up to cloud storage?
    }

    public Long insertLitter (Litter litter) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LITTER_BRAND, litter.getBrand());
        cv.put(COLUMN_LITTER_DATE, litter.getDate().getTime());
        return getWritableDatabase().insert(TABLE_LITTER, null, cv);
    }
}
