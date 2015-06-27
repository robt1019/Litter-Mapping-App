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
import android.widget.Button;
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
    private Button mStartListeningButton;
    boolean killCommanded = false;

    // Valid search terms
    private static final String[] VALID_COMMANDS = {
            "bin",
            "rubbish",
            "reset"
    };

    private static final String [] VALID_BRANDS = {

    };

    private static final int VALID_COMMANDS_SIZE = VALID_COMMANDS.length;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view =  inflater.inflate(R.layout.fragment_litter_mapper, container, false);

        resultText = (TextView)view.findViewById(R.id.caption_text);
        resultText.setText("Start talking");

        mStartListeningButton = (Button)view.findViewById(R.id.start_listening_button);
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

        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                "com.androidapps.robt1019.littermapper");

        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 20);

        mSpeechIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

//        mSpeechRecognizer.startListening(mSpeechIntent);

        super.onStart();
    }

    private String getResponse(int command) {

        String returnString = "I'm sorry, I don't recognize that option.";

        switch(command) {
            case 0:
                returnString = "bin";
                break;
            case 1:
                returnString = "rubbish";
                break;
            case 2:
                returnString = "Reset";
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
    private void processCommand(ArrayList<String> matchStrings) {
        String response = "I'm sorry I don't recognize that option.";
        int maxStrings = matchStrings.size();
        boolean resultFound = false;
        for (int i=0; i<VALID_COMMANDS_SIZE && !resultFound; i++) {
            for (int j=0; j<maxStrings && !resultFound; j++) {
                if (StringUtils.getLevenshteinDistance(matchStrings.get(j),
                        VALID_COMMANDS[i]) < (VALID_COMMANDS[i].length() / 3)) {
                    response = getResponse(i);
                }
            }
        }

        final String finalResponse = response;
        mHandler.post(new Runnable() {
            public void run() {
                resultText.setText(finalResponse);
            }
        });
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
            // If critical error then exit
            if (i == SpeechRecognizer.ERROR_CLIENT
                    || i == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                Log.d(TAG, "client error");
            }
            // Otherwise try again
            else {
                Log.d(TAG, "other error");
//                mSpeechRecognizer.startListening(mSpeechIntent);
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
            ArrayList<String> matches = null;
            if (bundle != null) {
                matches = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    Log.d(TAG, "results are " + matches.toString());
                    final ArrayList<String> matchesStrings = matches;
                    processCommand(matchesStrings);
                    if (!killCommanded) {
//                        mSpeechRecognizer.startListening(mSpeechIntent);
                    }
                    else {
                        Toast.makeText(getActivity(),"you can't quit that easy", Toast.LENGTH_SHORT).show();
                    }
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
