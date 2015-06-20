package com.androidapps.robt1019.littermapper;

import android.support.v4.app.Fragment;


public class LitterMapperActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new LitterMapperFragment();
    }
}
