package com.androidapps.robt1019.littermapper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by rob on 27/06/15.
 */
public class RemoteControlReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            // Code here to read contents of EXTRA_KEY_EVENT to know
            // Which key was pressed
        }
    }
}
