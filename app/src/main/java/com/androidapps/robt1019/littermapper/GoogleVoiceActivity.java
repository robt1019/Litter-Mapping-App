package com.androidapps.robt1019.littermapper;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;


public class GoogleVoiceActivity extends SingleFragmentActivity {

    // find Google Voice fragment to use its public methods
    private GoogleVoiceFragment mGoogleVoiceFragment = (GoogleVoiceFragment)getSupportFragmentManager()
            .findFragmentById(R.id.fragmentContainer);

    @Override
    protected Fragment createFragment() {
        return new GoogleVoiceFragment();
    }

}

