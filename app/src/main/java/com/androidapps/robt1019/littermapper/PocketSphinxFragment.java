package com.androidapps.robt1019.littermapper;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import java.util.Locale;

/**
 * Created by rob on 16/06/15.
 */
public class PocketSphinxFragment extends Fragment implements
        RecognitionListener {

    private static final String TAG = PocketSphinxFragment.class.getName();

    // Named searches allow for quickly reconfiguring decoder
    private static final String INTRO = "intro";
    public static final String LITTER_SEARCH = "litter item";
    private static final String TYPE_SEARCH = "litter type";
    private static final String BRAND_SEARCH = "litter brand";
    public static final String BIN_SEARCH  = "log rubbish bin";
    public static final String MENU_SEARCH = "menu";

//    // Keyword looking for to activate menu
//    private static final String KEYPHRASE = "log item";

    private LitterManager mLitterManager;
    private Litter mLitter;
    private boolean listening;
    private String nextSearch;
    private Button mStartListeningButton;
    private boolean litterLogged = false;
    private boolean brandLogged = false;
    private boolean typeLogged = false;
    private boolean binLogged = false;

    private String captionString;
    private TextView captionArea;

    private SpeechRecognizer recognizer;
    private TextToSpeech mSpeaker;
    private HashMap<String, Integer> captions;

    public String getCurrentSearch() {
        return recognizer.getSearchName();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if (savedInstanceState != null) {
            nextSearch = savedInstanceState.getString(getCurrentSearch());
            Log.d(TAG, "savedInstanceStateNextSearch: " + nextSearch);
        }
        else {
            // Default next search and current search are INTRO
            nextSearch = MENU_SEARCH;
        }

        // initialize LitterManager if not already initialized
        mLitterManager = LitterManager.get(getActivity());

        // Prepare data for UIX
        captions = new HashMap<String, Integer>();

        captions.put(INTRO, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(BIN_SEARCH, R.string.bin_caption);
        captions.put(LITTER_SEARCH, R.string.litter_caption);
        captions.put(BRAND_SEARCH, R.string.brand_caption);
        captions.put(TYPE_SEARCH, R.string.type_caption);

        captionString = "Getting ready";

        // Set up recognizers in asynchronous method as it takes lots of time
        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... voids) {
                try {
                    Assets assets = new Assets(getActivity());
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
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
                catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {

                    captionString = "failed to init recognizer " + result;
                    Toast.makeText(getActivity(),captionString,Toast.LENGTH_LONG).show();

//                    ((TextView) view.findViewById(R.id.caption_text))
//                            .setText("Failed to init recognizer " + result);
                }
                else {
                    Log.d(TAG, "onPostExecute");
                    switchSearch(MENU_SEARCH);
                }
            }
        }.execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
//            String currentSearch = savedInstanceState.getString(nextSearch);
//            Log.d(TAG, "nextSearch: " + nextSearch);
            captionString = getResources().getString(captions.get(nextSearch));
        }

        final View view =  inflater.inflate(R.layout.fragment_litter_mapper, container, false);

        captionArea = (TextView)view.findViewById(R.id.caption_text);
        captionArea.setText(captionString);

        mStartListeningButton = (Button)view.findViewById(R.id.start_listening_button);
        mStartListeningButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getCurrentSearch().equals(BRAND_SEARCH)
                        || getCurrentSearch().equals(TYPE_SEARCH)
                        || getCurrentSearch().equals(BIN_SEARCH)) {
                    toggleListening(nextSearch);
                }
                else {
                    toggleListening(getCurrentSearch());
                }
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

//        if (text.equals(KEYPHRASE)) {
//            switchSearch(MENU_SEARCH);
//        }
        if (text.equals(BIN_SEARCH)) {
            switchSearch(BIN_SEARCH);
        }
//        else if (text.equals(LITTER_SEARCH)) {
//            switchSearch(LITTER_SEARCH);
//        }
        else if (text.equals(BRAND_SEARCH)) {
//            switchSearchString(BRAND_SEARCH);
            switchSearch(BRAND_SEARCH);
        }
        else if (text.equals(TYPE_SEARCH)) {
//            switchSearchString(TYPE_SEARCH);
            switchSearch(TYPE_SEARCH);
        }
        else {
            ((TextView) getActivity().findViewById(R.id.result_text)).setText(text);
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {

        ((TextView) getActivity().findViewById(R.id.result_text)).setText("");
        String currentSearch = getCurrentSearch();
        Log.d(TAG, "current search: " + currentSearch);

        // Logic for deciding whether to start a new litter object or not
        if (mLitter == null &&
                (currentSearch.equals(TYPE_SEARCH) || currentSearch.equals(BRAND_SEARCH))) {
            Log.d(TAG, "starting litter item");
            litterLogged = false;
            brandLogged = false;
            typeLogged = false;
            startLitterItem();
            return;
        }

        if (hypothesis != null) {
            String text = hypothesis.getHypstr();

            if (mLitter != null) {
                // Ensures litter object has not already been fully populated with data
                if (!litterLogged) {
                    // Set litter brand/type if not already set
                    if (getCurrentSearch().equals(BRAND_SEARCH) && !text.equals(BRAND_SEARCH)) {
                        Toast.makeText(getActivity(), "litter brand set",Toast.LENGTH_SHORT).show();
                        mLitter.setBrand(text);
                        brandLogged = true;
                    }
                    if (getCurrentSearch().equals(TYPE_SEARCH) && !text.equals(TYPE_SEARCH)) {
                        Toast.makeText(getActivity(), "litter type set",Toast.LENGTH_SHORT).show();
                        mLitter.setType(text);
                        typeLogged = true;
                    }

                    // Return to menu search while Litter object not fully populated
                    switchSearch(MENU_SEARCH);
                }
                // Insert litter object into database and reset litter object to null
                // if litter object is fully populated
                if (brandLogged && typeLogged) {
                    mLitterManager.insertLitter(mLitter);
                    switchSearch(MENU_SEARCH);
                    String toastText = "litter object " + mLitter.getBrand() + ": "
                            + mLitter.getType() + " successfully logged";
                    Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();
                    mSpeaker.speak(toastText, TextToSpeech.QUEUE_FLUSH, null);
                    litterLogged = true;
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

        // Stop listening to make sure audio is logged for brands/type/bin
        // If this is not done then onresult gets called with no hypothesis string
        // due to switchSearch being called
        if (getCurrentSearch().equals(BRAND_SEARCH)
                || getCurrentSearch().equals(TYPE_SEARCH)
                || getCurrentSearch().equals(BIN_SEARCH)) {
            stopListening();
        }

        if (!getCurrentSearch().equals(MENU_SEARCH)) {
            nextSearch = MENU_SEARCH;
        }
    }

    public void startLitterItem() {
        mLitter = new Litter();
    }

    // Switch to different search string
    public void switchSearch(String searchName) {

        nextSearch = searchName;

        recognizer.stop();

        if (!searchName.equals(INTRO)) {
//            // start and immediately stop recognizer in order to update searchName
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
//        File menuKeyWord = new File(assetsDir, "menu.keyword");
//        recognizer.addKeywordSearch(MENU_SEARCH, menuKeyWord);

        // Grammar based search switching to logging different types of data
        File menuGrammar = new File(assetsDir, "menu.gram");
        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);

        // Grammar based search for litter menu
        File litterGrammar = new File(assetsDir, "litter.gram");
        recognizer.addGrammarSearch(LITTER_SEARCH, litterGrammar);

        // Type search (Glass, Plastic etc.)
        File typeGrammar = new File(assetsDir, "type.gram");
        recognizer.addGrammarSearch(TYPE_SEARCH, typeGrammar);

        // Litter search
        File brandGrammar = new File(assetsDir, "brand.gram");
        recognizer.addGrammarSearch(BRAND_SEARCH, brandGrammar);

        // Bin search
        File binGrammar = new File(assetsDir, "bin.gram");
        recognizer.addGrammarSearch(BIN_SEARCH, binGrammar);
    }

    @Override
    public void onError(Exception e) {
        ((TextView) getView().findViewById(R.id.caption_text)).setText(e.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(MENU_SEARCH);
    }

    public void updateListeningStatus(boolean listening) {
        this.listening = listening;
    }


    public void startListening(String currentSearch) {
        if(!listening) {
            recognizer.stop();
            recognizer.startListening(currentSearch);
            updateListeningStatus(true);
        }
    }

    public void stopListening() {
        recognizer.stop();
        updateListeningStatus(false);
    }

    public void toggleListening(String currentSearch) {
        if (!listening) {
            startListening(getCurrentSearch());
        }
        else {
            stopListening();
        }
    }
}
