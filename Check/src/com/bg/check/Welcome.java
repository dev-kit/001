
package com.bg.check;

import android.app.Application;

import com.bg.check.database.Databasehelper;
import com.bg.check.engine.SpeechEngine;

public class Welcome extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Databasehelper.init(getApplicationContext());
        sayHello();
    }

    private final void sayHello() {
        SpeechEngine speaker = SpeechEngine.getInstance(getApplicationContext());
        final String welcome = getResources().getString(R.string.welcome_speech);
        speaker.speak(welcome);
    }
}
