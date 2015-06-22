package com.androidapps.robt1019.littermapper;

import android.content.Context;

/**
 * Created by rob on 22/06/15.
 */
public class LitterManager {

    private static LitterManager sLitterManager;
    private Context mAppContext;
    private LitterMapperDBHelper mHelper;

    private LitterManager (Context appContext) {
        mAppContext = appContext;
        mHelper = new LitterMapperDBHelper(mAppContext);
    }

    public static LitterManager get (Context context) {
        if (sLitterManager == null) {
            // Use application context to avoid leaking activities
            sLitterManager = new LitterManager(context.getApplicationContext());
        }
        return sLitterManager;
    }

    public void insertLitter (String text) {
        // Code for inserting into SQLiteDatabase object
    }
}
