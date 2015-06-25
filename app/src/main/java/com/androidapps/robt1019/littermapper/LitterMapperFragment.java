package com.androidapps.robt1019.littermapper;

import static android.widget.Toast.makeText;
import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by rob on 16/06/15.
 */
public class LitterMapperFragment extends Fragment implements
        RecognitionListener {

    // Named searches allow for quickly reconfiguring decoder
    private static final String INTRO = "intro";
    public static final String LITTER_SEARCH = "litter item";
    private static final String TYPE_SEARCH = "litter type";
    private static final String BRAND_SEARCH = "litter brand";
    public static final String BIN_SEARCH  = "bin item";
    public static final String MENU_SEARCH = "menu";

//    // Search term timeout period
//    private static int SEARCH_TIMEOUT = 10000;

//    // Keyword looking for to activate menu
//    private static final String KEYPHRASE = "log item";

    private LitterManager mLitterManager;
    private Litter mLitter;
    private boolean mListening;

    private SpeechRecognizer recognizer;
    private TextToSpeech mSpeaker;
    private HashMap<String, Integer> captions;

    public String getCurrentSearch() {
        return recognizer.getSearchName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
//        // Default next search and current search are INTRO
//        mNextSearch = INTRO;
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        // initialize LitterManager if not already initialized
        mLitterManager = LitterManager.get(getActivity());
        // Set up text to speech object
        mSpeaker = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    mSpeaker.setLanguage(Locale.UK);
                }
            }
        }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view =  inflater.inflate(R.layout.fragment_litter_mapper, container, false);

        // Prepare data for UIX
        captions = new HashMap<String, Integer>();

        captions.put(INTRO, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(BIN_SEARCH, R.string.bin_caption);
        captions.put(LITTER_SEARCH, R.string.litter_caption);
        captions.put(BRAND_SEARCH, R.string.brand_caption);
        captions.put(TYPE_SEARCH, R.string.type_caption);

        ((TextView) view.findViewById(R.id.caption_text))
                .setText("Getting ready");

        // Set up recognizer in asynchronous method as it takes lots of time
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
                    switchSearch(INTRO);
                }
            }
        }.execute();

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

//        if (text.equals(KEYPHRASE)) {
//            switchSearch(MENU_SEARCH);
//        }
//        else if (text.equals(BIN_SEARCH)) {
//            switchSearch(BIN_SEARCH);
//        }
//        else if (text.equals(LITTER_SEARCH)) {
//            switchSearch(LITTER_SEARCH);
//        }
        if (text.equals(BRAND_SEARCH)) {
            switchSearch(BRAND_SEARCH);
        }
        else if (text.equals(TYPE_SEARCH)) {
            switchSearch(TYPE_SEARCH);
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
            // Check whether litter object has already been created
            if(mLitter != null) {

                // Ensures litter object has not already been fully populated with data
                if (mLitter.getBrand() == null || mLitter.getType() == null) {
                    // Set litter brand/type if not already set
                    if (getCurrentSearch().equals(BRAND_SEARCH) && !text.equals(BRAND_SEARCH)) {
                        Toast.makeText(getActivity(), "litter brand set",Toast.LENGTH_SHORT).show();
                        mLitter.setBrand(text);
                    }
                    if (getCurrentSearch().equals(TYPE_SEARCH) && !text.equals(TYPE_SEARCH)) {
                        Toast.makeText(getActivity(), "litter type set",Toast.LENGTH_SHORT).show();
                        mLitter.setType(text);
                    }
                    // Return to litter search screen while Litter object not fully populated
                    switchSearch(LITTER_SEARCH);
                }
                // Insert litter object into database and reset litter object to null
                // if litter object is fully populated
                if (mLitter.getBrand() != null && mLitter.getType() != null) {
                    mLitterManager.insertLitter(mLitter);
                    switchSearch(INTRO);
                    String toastText = "litter object " + mLitter.getBrand() + ": "
                            + mLitter.getType() + " successfully logged";
                    Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
                    mSpeaker.speak(toastText, TextToSpeech.QUEUE_FLUSH, null);
                    mLitter = null;
                    return;
                }
            }
            Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
            mSpeaker.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    public void onBeginningOfSpeech() {

    }

    @Override
    public void onEndOfSpeech() {
        if (!getCurrentSearch().equals(INTRO)) {
            switchSearch(INTRO);
        }
    }

    public void startLitterItem() {
        mLitter = new Litter();
    }

    // Switch to different search string
    public void switchSearch(String searchName) {

        recognizer.stop();

        if (!searchName.equals(INTRO)) {
            // start and immediately stop recognizer in order to update searchName
            recognizer.startListening(searchName);
            recognizer.stop();
            updateListeningStatus(false);
        }

         // Send search name to screen
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

//        //Keyword activation for initializing voice-recognition
//        recognizer.addKeyphraseSearch(INTRO, KEYPHRASE);

//        // Grammar based search switching to logging different types of data
//        File menuGrammar = new File(assetsDir, "menu.gram");
//        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);

        // Grammar based search for litter menu
        File litterGrammar = new File(assetsDir, "litter.gram");
        recognizer.addGrammarSearch(LITTER_SEARCH, litterGrammar);

        // Type search (Glass, Plastic etc.)
        File typeGrammar = new File(assetsDir, "type.gram");
        recognizer.addGrammarSearch(TYPE_SEARCH, typeGrammar);

        // Litter search
        File brandGrammar = new File(assetsDir, "brand.gram");
        recognizer.addGrammarSearch(BRAND_SEARCH, brandGrammar);

//        // Bin search
//        File binGrammar = new File(assetsDir, "bin.gram");
//        recognizer.addGrammarSearch(BIN_SEARCH, binGrammar);
    }

    @Override
    public void onError(Exception e) {
        ((TextView) getView().findViewById(R.id.caption_text)).setText(e.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(INTRO);
    }

    public void updateListeningStatus(boolean listening) {
        mListening = listening;
    }


    public void startListening(String currentSearch) {
        if(!mListening) {
            recognizer.stop();
            recognizer.startListening(currentSearch);
            updateListeningStatus(true);
        }
    }

    public void stopListening() {
        recognizer.stop();
        updateListeningStatus(false);
       }
}
