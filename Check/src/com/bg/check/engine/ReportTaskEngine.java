
package com.bg.check.engine;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.bg.check.engine.utils.LogUtils;

// FIXME: Duplicated Class to GerneralTaskEngine
public class ReportTaskEngine {
    private static final int SLEEP_TIME_MS = 10000;

    private ConcurrentLinkedQueue<BaseTask> mTaskQueue = new ConcurrentLinkedQueue<BaseTask>();

    private static ReportTaskEngine sTaskEngineInstance = new ReportTaskEngine();

    private boolean canceled;

    private static int sID;

    private static boolean sRun;

    private ReportTaskEngine() {
        run();
    }

    public static ReportTaskEngine getInstance() {
        LogUtils.logD("ReportTaskEngine.getInstance");
        return sTaskEngineInstance;
    }

    private void run() {
        if (sRun) {
            LogUtils.logE("TaskEngine has been running!");
            return;
        }
        sRun = true;
        LogUtils.logD("ReportTaskEngine.run, ID" + sID);
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
        }, "ReportTaskEngine" + sID++).start();

    }

    public void clearAllTasks() {
        mTaskQueue.clear();
    }

    // public void restart() {
    // LogUtils.logD("ReportTaskEngine.restart");
    // canceled = true;
    // sID++;
    // sTaskQueue.clear();
    // canceled = false;
    // run();
    // }

    public void appendTask(BaseTask task) {
        LogUtils.logD("ReportTaskEngine.appendTask" + task);
        if (task == null) {
            return;
        }
        mTaskQueue.add(task);
    }

    public void cancelTask(BaseTask task) {
        LogUtils.logD("ReportTaskEngine.cancelTask" + task);
        if (task == null) {
            return;
        }
        task.markAsCanceled();
        mTaskQueue.remove(task);
    }
}
