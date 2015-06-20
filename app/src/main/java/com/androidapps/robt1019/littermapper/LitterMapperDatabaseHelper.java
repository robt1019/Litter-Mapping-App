package com.androidapps.robt1019.littermapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rob on 19/06/15.
 */
public class LitterMapperDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "litter_mapper.sqlite";
    private static final int VERSION = 1;

    private static final String TABLE_TYPE = "type";
    private static final String COLUMN_TYPE_ID = "_id";
    private static final String COLUMN_TYPE_NAME = "name";

    public LitterMapperDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create "type" table
        db.execSQL("CREATE TABLE litter (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "type VARCHAR ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For implementing schema changes
    }

    public Long insertType (Type type) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TYPE_NAME, "glass");
        return getWritableDatabase().insert(TABLE_TYPE, null, cv);
    }

//    public litterMapperCursor queryLitterDatabase() {
//
//    }

    // Convenience class to wrap a cursor that returns rows from "litter" table.
    // getType() method will give you a Type instance representing the current row
//    public static class typeCursor extends CursorWrapper {
//
//    }

//    public static class
}
