package com.bg.check.engine.utils;
import android.util.Log;

public class LogUtils {

    /** Application tag prefix for LogCat. **/
    private static final String APP_NAME_PREFIX = "Checker";

    /** Stores the enabled state of the LogUtils function. **/
    private static Boolean mEnabled = true;

    /**
     * Write info log string.
     * 
     * @param data String containing data to be logged.
     */
    public static void logI(final String data) {
        if (mEnabled) {
            Thread currentThread = Thread.currentThread();
            Log.i(APP_NAME_PREFIX, "[" + currentThread.getName() + "] " + data);
        }
    }

    /**
     * Write debug log string.
     * 
     * @param data String containing data to be logged.
     */
    public static void logD(final String data) {
        if (mEnabled) {
            Thread currentThread = Thread.currentThread();
            Log.d(APP_NAME_PREFIX, "[" + currentThread.getName() + "] " + data);
        }
    }

    /**
     * Write warning log string.
     * 
     * @param data String containing data to be logged.
     */
    public static void logW(final String data) {
        if (mEnabled) {
            Thread currentThread = Thread.currentThread();
            Log.w(APP_NAME_PREFIX, "[" + currentThread.getName() + "] " + data);
        }
    }

    /**
     * Write error log string.
     * 
     * @param data String containing data to be logged.
     */
    public static void logE(final String data) {

        if (mEnabled) {
            Thread currentThread = Thread.currentThread();
            // temporary fix to avoid crash SMS.
            Log.w(APP_NAME_PREFIX, "[" + currentThread.getName() + "] " + data);
        }
    }

    /**
     * Write verbose log string.
     * 
     * @param data String containing data to be logged.
     */
    public static void logV(final String data) {
        if (mEnabled) {
            Thread currentThread = Thread.currentThread();
            Log.v(APP_NAME_PREFIX, "[" + currentThread.getName() + "] " + data);
        }
    }

    /**
     * Write info log string with specific component name.
     * 
     * @param name String name string to prepend to log data.
     * @param data String containing data to be logged.
     */
    public static void logWithName(final String name, final String data) {
        if (mEnabled) {
            Log.v(APP_NAME_PREFIX + name, data);
        }
    }

    /**
     * Write error log string with Exception thrown.
     * 
     * @param data String containing data to be logged.
     * @param exception Exception associated with error.
     */
    public static void logE(final String data, final Throwable exception) {

        // exception.getClass().toString());
        if (mEnabled) {
            Thread currentThread = Thread.currentThread();
            // temporary fix to avoid crash SMS.
            Log.w(APP_NAME_PREFIX, "[" + currentThread.getName() + "] " + data, exception);
        }
    }

    /***
     * Returns if the logging feature is currently enabled.
     * 
     * @return TRUE if logging is enabled, FALSE otherwise.
     */
    public static Boolean isEnabled() {
        return mEnabled;
    }
}
