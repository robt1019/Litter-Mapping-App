package com.androidapps.robt1019.littermapper;

import android.app.PendingIntent;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rob on 20/06/15.
 */
public class LitterManager {

    private static final String TAG = "LitterManager";

    private static String PREFS_FILE = "litter_mapper";
    private static final String PREF_CURRENT_RUN_ID = "LitterManager.currentLitterId";

    private static LitterManager sLitterManager;
    private Context mAppContext;
    private LitterMapperDatabaseHelper mHelper;
    private SharedPreferences mPrefs;
    private long mCurrentLitterId;

    // Private constructor forces user to use RunManager.get(Context)
    private LitterManager (Context appContext) {
        mAppContext = appContext;
        mHelper = new LitterMapperDatabaseHelper(mAppContext);
        mPrefs = mAppContext.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        mCurrentLitterId = mPrefs.getLong(PREF_CURRENT_RUN_ID, -1);
    }

    public static LitterManager get(Context context) {
        if (sLitterManager == null) {
            // Use application context to avoid leaking activities
            sLitterManager = new LitterManager(context.getApplicationContext());
        }
        return sLitterManager;
    }

}
