package com.androidapps.robt1019.littermapper;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;


public class GoogleVoiceActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null) {

        }
    }

    @Override
    protected Fragment createFragment() {
        return new GoogleVoiceFragment();
    }

}

