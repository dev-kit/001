
package com.bg.check;

import java.util.Locale;
import java.util.Random;

import com.bg.check.database.Databasehelper;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;

public class Welcome extends Application implements OnInitListener {

    private static final String TAG = "Welcome";

    private TextToSpeech mTts;

    @Override
    public void onCreate() {
        super.onCreate();
        Databasehelper.init(getApplicationContext());

        mTts = new TextToSpeech(this, this);
    }

    @Override
    public void onTerminate() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onTerminate();
    }

    // Implements TextToSpeech.OnInitListener.
    public void onInit(int status) {
        // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
        if (status == TextToSpeech.SUCCESS) {
            // Set preferred language to US english.
            // Note that a language may not be available, and the result will
            // indicate this.
            int result = mTts.setLanguage(Locale.US);
            // Try this someday for some interesting results.
            // int result mTts.setLanguage(Locale.FRANCE);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "Language is not available.");
            } else {

                sayHello();
            }
        } else {
            // Initialization failed.
            Log.e(TAG, "Could not initialize TextToSpeech.");
        }
    }

    private static final Random RANDOM = new Random();

    private static final String[] HELLOS = {
            "Hello", "Salutations", "Greetings", "Howdy", "What's crack-a-lackin?",
            "That explains the stench!"
    };

    private void sayHello() {
        // Select a random hello.
        int helloLength = HELLOS.length;
        String hello = HELLOS[RANDOM.nextInt(helloLength)];
        mTts.speak(hello, TextToSpeech.QUEUE_FLUSH, // Drop all pending entries
                                                    // in the playback queue.
                null);
    }
}
