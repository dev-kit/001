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

/**
 * 语音引擎类。
 * 提供了连续语音播报和单一语音播报功能。
 * 
 * 要实现连续语音播报功能，需要实现SpeechListener接口。
 */
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

    /**
     * 得到语音引擎的实例对象。
     */
    public static final SpeechEngine peekInstance() {
        return mSpeechEngine;
    }

    /**
     * 语音服务listener，提供连续语音播报功能。
     */
    public interface SpeechListener {
        /**
         * 回调方法，当连续语音播放时，返回当前要播报的语句。
         */
        public String onPrepareSpeech();

        /**
         * 移动到下一条语音播报语句。
         * 
         * 返回true表示移动成功，并继续播报
         * 返回false表示不再继续播报
         */
        public boolean moveToNext();

        /**
         * 当连续语音播报成功结束时被调用。
         */
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
                    } while (listener.moveToNext());
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

    /**
     * 等待直到当前语音播报结束。
     */
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

    /**
     * 播报连续语音，播报内容有SpeechListener提供。
     */
    public void speakSeries() {
        mIsSpeakStart = true;
        mHandler.obtainMessage(MESSAGE_SPEAK_SERIES).sendToTarget();
    }

    /**
     * 播报单一语音内容。
     */
    public void speak(String words) {
        mIsSpeakStart = true;
        mHandler.obtainMessage(MESSAGE_SPEAK, words).sendToTarget();
    }

    /**
     * 判断是否在语音播报在进行中。
     */
    public boolean isSpeaking() {
        if (mSpeaker != null && mSpeaker.isSpeaking() && hasMessage() && mIsSpeakStart) {
            return true;
        }

        return false;
    }

    /**
     * 播报单一语音内容，并且在完成之后调用runnable。
     */
    public void speak(final String words, final Runnable runnable) {
        new Thread() {
            public void run() {
                speak(words);
                waitingForSpeakingFinish();
                if (runnable != null) {
                    runnable.run();
                }
            }
        }.start();
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

    /**
     * 停止语音播报。
     */
    public void stopSpeak() {
        if (mSpeaker != null) {
            mSpeaker.stop();
        }

        if (mHandler != null) {
            mHandler.removeMessages(MESSAGE_SPEAK);
            mHandler.removeMessages(MESSAGE_SPEAK_SERIES);
        }
    }

    /**
     * 关闭语音播报引擎。
     */
    public void close() {
        mSpeakerAvailable = false;
        mSpeechEngine = null;

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
