package com.androidapps.robt1019.littermapper;

import android.support.v4.app.Fragment;
import android.view.KeyEvent;


public class PocketSphinxActivity extends SingleFragmentActivity {

    // A key for passing a litter ID as a long
    public static final String EXTRA_LITTER_ID =
            "com.androidapps.robt1019.littermapper";


    @Override
    protected Fragment createFragment() {
        return new PocketSphinxFragment();
    }

    // If relevant key press detected, handle event and exit function, otherwise pass to rest of Android components.
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Call relevant methods in PocketSphinxFragment instance
        PocketSphinxFragment pocketSphinxFragment = (PocketSphinxFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragmentContainer);

        switch(keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                    pocketSphinxFragment.startListening(pocketSphinxFragment.getCurrentSearch());
                return true;
//            case KeyEvent.KEYCODE_VOLUME_DOWN:
//                pocketSphinxFragment.switchSearch(pocketSphinxFragment.LITTER_SEARCH);
//                pocketSphinxFragment.startLitterItem();
//                return true;
        }
        return super.onKeyDown(keyCode,event);
    }

//    @Override
//    public boolean onKeyUp(int keyCode, KeyEvent event) {
//
//        // Call relevant methods in PocketSphinxFragment instance
//        PocketSphinxFragment pocketSphinxFragment = (PocketSphinxFragment)getSupportFragmentManager()
//                .findFragmentById(R.id.fragmentContainer);
//
//        switch(keyCode) {
//            case KeyEvent.KEYCODE_VOLUME_UP:
//                pocketSphinxFragment.stopListening();
//                return true;
//        }
//        return super.onKeyUp(keyCode, event);
//    }
}
