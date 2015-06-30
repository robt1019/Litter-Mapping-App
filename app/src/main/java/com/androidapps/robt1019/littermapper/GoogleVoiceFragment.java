package com.androidapps.robt1019.littermapper;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * Created by rob on 27/06/15.
 */
public class GoogleVoiceFragment extends Fragment {

    private static final String TAG = GoogleVoiceFragment.class.getName();

    private SpeechRecognizer mSpeechRecognizer;
    private Handler mHandler = new Handler();
    private TextView resultText;
    private Intent mSpeechIntent;
    private ImageButton mStartListeningButton;
    private String[] mCurrentSearch;

    // Valid search terms
    private static final String[] MENU_COMMANDS = {
            "litter bin",
            "item material",
            "item brand"
    };

    private static final String[] VALID_BRANDS = {
            "McDonald's",
            "Burger King",
            "Lucozade",
            "Powerade"
    };

    private static final String[] BIN_TYPES = {
            "recycling",
            "landfill"
    };

    private static final String[] RUBBISH_MATERIALS = {
            "metal",
            "plastic",
            "glass",
            "other"
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view =  inflater.inflate(R.layout.fragment_litter_mapper, container, false);

        resultText = (TextView)view.findViewById(R.id.caption_text);
        resultText.setText("Start talking");

        mStartListeningButton = (ImageButton)view.findViewById(R.id.start_listening_button);
        mStartListeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startListening();
            }
        });

        return view;
    }

    @Override
    public void onStart() {

        // Instantiate speech recognizer class using factory method
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getActivity());

        SpeechListener mRecognitionListener = new SpeechListener();
        mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
        mSpeechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.androidapps.robt1019.littermapper");

        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20);

        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        mCurrentSearch = MENU_COMMANDS;

        super.onStart();
    }

    private String getMenuResponse(int command) {

        String returnString = "litter bin, item material or item brand";

        switch(command) {
            case 0:
                returnString = "What kind of bin was it? (\"Recycling\" or \"Landfill\")";
                mCurrentSearch = BIN_TYPES;
                break;
            case 1:
                returnString = "What material was it? (\"Glass\", \"Plastic\" or \"Metal\")?";
                mCurrentSearch = RUBBISH_MATERIALS;
                break;
            case 2:
                returnString = "What brand was it?";
                mCurrentSearch = VALID_BRANDS;
            default:
                break;
        }
        return returnString;
    }

    @Override
    public void onPause() {
        //kill voice recognizer
        if (mSpeechRecognizer != null) {
            mSpeechRecognizer.destroy();
            mSpeechRecognizer = null;
        }
        super.onPause();
    }

    // Magic algorithm for matching words. You don't understand this code yet
    private void processCommand(ArrayList<String> matchStrings, String[] currentSearch) {
        String response = "I'm sorry I don't recognize that option.";
        int maxStrings = matchStrings.size();
        boolean resultFound = false;
        for (int i=0; i<currentSearch.length && !resultFound; i++) {
            for (int j=0; j<maxStrings && !resultFound; j++) {
                if (StringUtils.getLevenshteinDistance(matchStrings.get(j),
                        currentSearch[i]) < (currentSearch[i].length() / 3)) {
                    if(mCurrentSearch.equals(MENU_COMMANDS)) {
                        response = getMenuResponse(i);
                    }
                }
            }
        }
        final String finalResponse = response;
        mHandler.post(new Runnable() {
            public void run() {
                resultText.setText(finalResponse);
            }
        });
        Toast.makeText(getActivity(),matchStrings.get(0),Toast.LENGTH_SHORT).show();
    }

    public void startListening() {
        mSpeechRecognizer.startListening(mSpeechIntent);
    }

    public void stopListening() {
        mSpeechRecognizer.stopListening();
    }


    class SpeechListener implements RecognitionListener {

        @Override
        public void onBufferReceived(byte[] bytes) {
            Log.d(TAG, "buffer received");
        }

        @Override
        public void onError(int i) {
            if (i == SpeechRecognizer.ERROR_CLIENT
                    || i == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                Log.d(TAG, "client error");
            }
            else {
                Log.d(TAG, "other error: " + i);
            }
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            Log.d(TAG, "onEvent");
        }

        @Override
        public void onPartialResults(Bundle bundle) {
            Log.d(TAG, "partialResults");
        }

        @Override
        public void onReadyForSpeech(Bundle bundle) {
            Log.d(TAG, "onReadyForSpeech");
        }

        @Override
        public void onResults(Bundle bundle) {
            Log.d(TAG, "onResults");
            Log.d(TAG, "currentSearch = " + mCurrentSearch[0]);
            ArrayList<String> matches = null;
            if (bundle != null) {
                matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    Log.d(TAG, "results are " + matches.toString());
                    final ArrayList<String> matchesStrings = matches;
                    processCommand(matchesStrings, mCurrentSearch);
                }
            }
        }

        @Override
        public void onRmsChanged(float v) {
            Log.d(TAG, "onRmsChanged");
        }

            @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG,"onBeginningOfSpeech");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG,"onEndOfSpeech");
        }
    }
}
