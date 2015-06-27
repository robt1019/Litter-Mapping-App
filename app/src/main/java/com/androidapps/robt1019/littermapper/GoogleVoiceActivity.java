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

    // If relevant key press detected, handle event and exit function, otherwise pass to rest of Android components.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Call relevant methods in PocketSphinxFragment instance
        GoogleVoiceFragment mGoogleVoiceFragment = (GoogleVoiceFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);

        switch(keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                mGoogleVoiceFragment.startListening();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                mGoogleVoiceFragment.stopListening();
                return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch(keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
//                mGoogleVoiceFragment.stopListening();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}

