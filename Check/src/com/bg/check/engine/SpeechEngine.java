package com.bg.check.engine;

import java.util.Locale;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.TextUtils;

import com.bg.check.engine.utils.LogUtils;

public class SpeechEngine implements OnInitListener {


    public static final char COMMA = ',';

    private static final int MESSAGE_SPEAK = 1;
    private static final int MESSAGE_SPEAK_SERIES = 2;

    private TextToSpeech mSpeaker;
    private static SpeechEngine mSpeechEngine;
    private SpeechHandler mHandler;
    private HandlerThread mThread;
    private boolean mSpeakerAvailable;
    private boolean mIsSpeakStart;
    private SpeechListener mSpeechListener;

    private SpeechEngine(Context context) {
        mSpeaker = new TextToSpeech(context.getApplicationContext(), this);
        HandlerThread mThread = new HandlerThread("speech");
        mThread.start();
        mHandler = new SpeechHandler(mThread.getLooper());
    }

    public void registerSpeechListener(SpeechListener listener) {
        mSpeechListener = listener;
    }

    public void unregisterSpeechListener() {
        mSpeechListener = null;
    }

    public static final SpeechEngine getInstance(Context context) {
        if (mSpeechEngine == null) {
            mSpeechEngine = new SpeechEngine(context);
        }

        return mSpeechEngine;
    }

    public static final SpeechEngine peekInstance() {
        return mSpeechEngine;
    }

    public interface SpeechListener {
        public String onPrepareSpeech();
        public boolean hasNextSpeech();
        public void onSpeechComplete();
    }

    private final class SpeechHandler extends Handler {

        public SpeechHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (!mSpeakerAvailable) {
                mHandler.sendMessageDelayed(Message.obtain(msg), 1000);
                return;
            }

            switch (msg.what) {
            case MESSAGE_SPEAK:
                speakInternal(msg.obj.toString());
                waitingForSpeakingFinish();
                reportSpeechComplete();
                mIsSpeakStart = false;
                return;
            case MESSAGE_SPEAK_SERIES:
                final SpeechListener listener = mSpeechListener;
                if (listener != null) {
                     do {
                        final String words = listener.onPrepareSpeech();
                        speakInternal(words);
                        waitingForSpeakingFinish();
                    } while(listener.hasNextSpeech());
                     reportSpeechComplete();
                }
                mIsSpeakStart = false;
                return;
            default:
                super.handleMessage(msg);
            }
        }
    }

    private void reportSpeechComplete() {
        if (mSpeechListener != null && !hasMessage()) {
            mSpeechListener.onSpeechComplete();
        }
    }

    private boolean hasMessage() {
        if (mHandler != null) {
            return mHandler.hasMessages(MESSAGE_SPEAK) ||
                    mHandler.hasMessages(MESSAGE_SPEAK_SERIES);
        }

        return false;
    }

    public void waitingForSpeakingFinish() {
        try {
            while (mSpeaker != null && mSpeaker.isSpeaking()) {
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            final int result = mSpeaker.setLanguage(Locale.CHINA);
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                LogUtils.logE("TTS: Chinese not supported");
                // TODO do something?
            }

            mSpeakerAvailable = true;
        } else {
            LogUtils.logE("TextToSpeech engine init error");
        }
    }

    public void speakSeries() {
        mIsSpeakStart = true;
        mHandler.obtainMessage(MESSAGE_SPEAK_SERIES).sendToTarget();
    }

    public void speak(String words) {
        mIsSpeakStart = true;
        mHandler.obtainMessage(MESSAGE_SPEAK, words).sendToTarget();
    }

    public boolean isSpeaking() {
        if (mSpeaker != null && mSpeaker.isSpeaking() && hasMessage() && mIsSpeakStart) {
            return true;
        }

        return false;
    }

    private final void speakInternal(String words) {
        if (TextUtils.isEmpty(words)) {
            return;
        }

        if (mSpeaker != null) {
            LogUtils.logI("speaking: " + words);
            mSpeaker.speak(words, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            LogUtils.logW("TextToSpeech engine not available");
        }
    }

    public void stopSpeak() {
        if (mSpeaker != null) {
            mSpeaker.stop();
        }

        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE_SPEAK);
            mHandler.removeMessages(MESSAGE_SPEAK_SERIES);
        }
    }

    public void close() {
        mSpeakerAvailable = false;

        if (mSpeaker != null) {
            mSpeaker.shutdown();
            mSpeaker = null;
        }

        if (mThread != null) {
            mThread.quit();
            mThread = null;
        }
    }
}
