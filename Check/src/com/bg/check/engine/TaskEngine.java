
package com.bg.check.engine;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.bg.check.engine.utils.LogUtils;

public class TaskEngine {
    private static final int SLEEP_TIME_MS = 1000;

    private ConcurrentLinkedQueue<BaseTask> mTaskQueue = new ConcurrentLinkedQueue<BaseTask>();

    private static TaskEngine sTaskEngineInstance = new TaskEngine();

    private boolean canceled;

    private static int sID;

    private TaskEngine() {
        run();
    }

    public static TaskEngine getInstance() {
        LogUtils.logD("TaskEngine.getInstance");
        return sTaskEngineInstance;
    }

    private void run() {
        LogUtils.logD("TaskEngine.init");
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    if (canceled) {
                        return;
                    }
                    BaseTask task = mTaskQueue.poll();
                    if (task == null) {
                        try {
                            Thread.sleep(SLEEP_TIME_MS);
                            LogUtils.logE("To sleep " + SLEEP_TIME_MS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            LogUtils.logE(e.toString());
                        }
                        continue;
                    }

                    if (canceled) {
                        return;
                    }

                    Object result = null;
                    if (!task.isCanceled()) {
                        result = task.run();
                        LogUtils.logD("task.run()" + task.toString());
                    }

                    if (canceled) {
                        return;
                    }

                    if (!task.isCanceled()) {
                        task.onCallback(result);
                    }

                }
            }
        }, "TaskEngine" + sID).start();

    }

    public void clearAllTasks() {
        mTaskQueue.clear();
    }

    // public void restart() {
    // LogUtils.logD("TaskEngine.restart");
    // canceled = true;
    // sID++;
    // sTaskQueue.clear();
    // canceled = false;
    // run();
    // }

    public void appendTask(BaseTask task) {
        LogUtils.logD("TaskEngine.appendTask" + task);
        if (task == null) {
            return;
        }
        mTaskQueue.add(task);
    }

    public void cancelTask(BaseTask task) {
        LogUtils.logD("TaskEngine.cancelTask" + task);
        if (task == null) {
            return;
        }
        task.markAsCanceled();
        mTaskQueue.remove(task);
    }
}
