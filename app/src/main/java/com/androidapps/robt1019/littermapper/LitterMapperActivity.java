package com.androidapps.robt1019.littermapper;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;


public class LitterMapperActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new LitterMapperFragment();
    }
}
