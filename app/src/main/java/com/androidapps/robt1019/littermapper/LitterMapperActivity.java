package com.androidapps.robt1019.littermapper;

import android.content.ComponentName;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;


public class LitterMapperActivity extends SingleFragmentActivity {

    // A key for passing a litter ID as a long
    public static final String EXTRA_LITTER_ID =
            "com.androidapps.robt1019.littermapper";

    private AudioManager mAudioManager;
    private ComponentName mRemoteControlResponder;

    @Override
    protected Fragment createFragment() {
        return new LitterMapperFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mRemoteControlResponder = new ComponentName(getPackageName(),
            RemoteControlReceiver.class.getName());
    }



    // If relevant key press detected, handle event and exit function, otherwise pass to rest of Android components.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Call relevant methods in LitterMapperFragment instance
        LitterMapperFragment litterMapperFragment = (LitterMapperFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);

        switch(keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                    litterMapperFragment.startListening(litterMapperFragment.getCurrentSearch());
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                litterMapperFragment.switchSearch(litterMapperFragment.LITTER_SEARCH);
                litterMapperFragment.startLitterItem();
                return true;
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        // Call relevant methods in LitterMapperFragment instance
        LitterMapperFragment litterMapperFragment = (LitterMapperFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);

        switch(keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                litterMapperFragment.stopListening();
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }
}
