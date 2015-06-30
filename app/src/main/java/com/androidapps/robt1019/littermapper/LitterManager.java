package com.androidapps.robt1019.littermapper;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by rob on 22/06/15.
 */
public class LitterManager {

    private static LitterManager sLitterManager;
    private Context mAppContext;
    private LitterMapperDBHelper mHelper;
    private TextToSpeech mTts;

    private LitterManager(Context appContext) {
        mAppContext = appContext;
        mHelper = new LitterMapperDBHelper(mAppContext);
        mTts = new TextToSpeech(mAppContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR) {
                    mTts.setLanguage(Locale.UK);
                }
            }
        });
    }

    public static LitterManager get (Context context) {
        if (sLitterManager == null) {
            // Use application context to avoid leaking activities
            sLitterManager = new LitterManager(context.getApplicationContext());
        }
        return sLitterManager;
    }

    public void speak(String speechString) {
        mTts.speak(speechString, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void insertLitter (Litter litter) {
//        litter.setId(mHelper.insertLitter(litter));
        mHelper.insertLitter(litter);
    }

    public LitterMapperDBHelper.LitterCursor queryLitterItems() {
        return mHelper.queryLitterItems();
    }

}
