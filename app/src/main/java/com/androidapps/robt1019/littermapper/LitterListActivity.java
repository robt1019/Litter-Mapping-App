package com.androidapps.robt1019.littermapper;

import android.support.v4.app.Fragment;

/**
 * Created by rob on 24/06/15.
 */
public class LitterListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new LitterListFragment();
    }
}
