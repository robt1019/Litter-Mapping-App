package com.androidapps.robt1019.littermapper;

import android.support.v4.app.Fragment;


public class LitterMapperActivity extends SingleFragmentActivity {

    // A key for passing a litter ID as a long
    public static final String EXTRA_LITTER_ID =
            "com.androidapps.robt1019.littermapper";

    @Override
    protected Fragment createFragment() {
        return new LitterMapperFragment();
    }
}
