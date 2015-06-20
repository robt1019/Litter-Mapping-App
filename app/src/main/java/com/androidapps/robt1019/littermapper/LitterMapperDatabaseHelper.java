package com.androidapps.robt1019.littermapper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by rob on 19/06/15.
 */
public class LitterMapperDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "litter_mapper.sqlite";
    private static final int VERSION = 1;

    private static final String TABLE_LITTER = "litter";
    private static final String COLUMN_LITTER_ID = "_id";
    private static final String COLUMN_LITTER_BRAND = "brand";
    private static final String COLUMN_LITTER_DATE = "date";

    public LitterMapperDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create "type" table
        db.execSQL("CREATE TABLE litter (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "brand VARCHAR," +
                "date INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // For implementing schema changes
    }

    public Long insertLitter (Litter litter) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_LITTER_BRAND, "mcdonald's");
        cv.put(COLUMN_LITTER_DATE, litter.getDate().getTime());
        return getWritableDatabase().insert(TABLE_LITTER, null, cv);
    }

    public LitterCursor queryLitter() {
        // Equivalent to "SELECT * FROM litter ORDER_BY date ASC
        Cursor wrapped = getReadableDatabase().query(TABLE_LITTER,
                null, null, null, null, null, COLUMN_LITTER_DATE + " ASC");
        return new LitterCursor(wrapped);
    }

    // Convenience class to wrap a cursor that returns rows from "litter" table.
    // getBrand() method will give you a Litter instance representing the current row
    public static class LitterCursor extends CursorWrapper {

        public LitterCursor(Cursor c) {
            super(c);
        }

        // Returns a Litter object configured for current row
        // or null if current row is invalid
        public Litter getLitter() {
            if (isBeforeFirst() || isAfterLast()) {
                return null;
            }

            Litter litter = new Litter();
            long litterId = getLong(getColumnIndex(COLUMN_LITTER_ID));
            litter.setId(litterId);
            String brand = getString(getColumnIndex(COLUMN_LITTER_BRAND));
            litter.setBrand(brand);
            return litter;
        }
    }

    public LitterCursor queryLitter(long id) {
        Cursor wrapped = getReadableDatabase().query(TABLE_LITTER,
                null, // All columns
                COLUMN_LITTER_ID + " = ?", // Look for a run ID
                new String[]{ String.valueOf(id) }, // with this value
                null,  // group by
                null,  // having
                null,  // order by
                "1");  // limit 1 row
        return new LitterCursor(wrapped);
    }
}
