package com.androidapps.robt1019.littermapper;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by rob on 16/06/15.
 */
public class LitterMapperFragment extends Fragment implements
        RecognitionListener {

    // Named searches allow for quickly reconfiguring decoder
    private static final String RUBBISH_SEARCH = "rubbish";
    private static final String BIN_SEARCH  = "bin";
    private static final String MENU_SEARCH = "menu";

    private SpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;

    private Button mStartButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view =  inflater.inflate(R.layout.fragment_litter_mapper, container, false);

        // Prepare data for UI
        captions = new HashMap<String, Integer>();
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(BIN_SEARCH, R.string.bin_caption);
        captions.put(RUBBISH_SEARCH, R.string.rubbish_caption);

        ((TextView) view.findViewById(R.id.caption_text))
                .setText("Preparing the recognizer");

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... voids) {
                try {
                    Assets assets = new Assets(getActivity());
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                }
                catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    ((TextView) view.findViewById(R.id.caption_text))
                            .setText("Failed to init recognizer " + result);
                }
                else {
                    switchSearch(MENU_SEARCH);
                }
            }
        }.execute();

        mStartButton = (Button) view.findViewById(R.id.startLoggingButton);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recognizer.startListening(MENU_SEARCH);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recognizer.cancel();
        recognizer.shutdown();
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null) {
            return;
        }

        String text = hypothesis.getHypstr();
        if (text.equals(BIN_SEARCH)) {
            switchSearch(BIN_SEARCH);
        }
        if (text.equals(RUBBISH_SEARCH)) {
            switchSearch(RUBBISH_SEARCH);
        }
        else {
            ((TextView) getActivity().findViewById(R.id.result_text)).setText(text);
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        ((TextView) getActivity().findViewById(R.id.result_text)).setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
    }

    // Switch to different search string
    private void switchSearch(String searchName) {
        recognizer.stop();

        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(MENU_SEARCH))
            recognizer.startListening(searchName);
        else
            recognizer.startListening(searchName, 10000);

        String caption = getResources().getString(captions.get(searchName));
        ((TextView) getView().findViewById(R.id.caption_text)).setText(caption);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "cmudict-en-us.dict"))

                        // To disable logging of raw audio comment out this call
                .setRawLogDir(assetsDir)

                        // Threshold to tune for keyphrase to balance between false alarms and misses
                .setKeywordThreshold(1e-45f)

                        // Use context-independent phonetic search
                .setBoolean("-allphone_ci", true)

                .getRecognizer();
        recognizer.addListener(this);

        // Grammar based search switching to logging different types of data
        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);

        // Rubbish search
        File rubbishGrammar = new File(assetsDir, "rubbish.gram");
        recognizer.addGrammarSearch(RUBBISH_SEARCH, rubbishGrammar);

        // Bin search
        File binGrammar = new File(assetsDir, "bin.gram");
        recognizer.addGrammarSearch(BIN_SEARCH, binGrammar);
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onTimeout() {
        switchSearch(MENU_SEARCH);
    }

}
