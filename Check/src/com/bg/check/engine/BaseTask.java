
package com.bg.check.engine;

import com.bg.check.engine.utils.LogUtils;

public abstract class BaseTask {
    private boolean mIsCanceled;

    private String mTaskName;

    private TaskCallback mCallback;

    public static interface TaskCallback {
        public void onCallBack(Object result);
    }

    public abstract Object run();

    public void setTaskName(String name) {
        mTaskName = name;
    }

    public void setCallback(TaskCallback callback) {
        LogUtils.logD("setCallback");
        mCallback = callback;
    }

    public void onCallback(Object result) {
        LogUtils.logD("onCallback");
        TaskCallback callback = mCallback;
        if (callback != null) {
            callback.onCallBack(result);
        }
    }

    public boolean isCanceled() {
        LogUtils.logD("isCanceled");
        return mIsCanceled;
    }

    public void markAsCanceled() {
        LogUtils.logD("cancelTask");
        mIsCanceled = true;
        mCallback = null;
    }

    public String getTaskName() {
        return mTaskName;
    }

    public String toString() {
        return "TaskName " + mTaskName + " mCallback" + mCallback + " mIsCanceled " + mIsCanceled;
    }
}
