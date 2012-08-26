
package com.bg.check.engine;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.bg.check.engine.utils.LogUtils;

/**
 * Main engine to run task, which is designed to only run tasks which download task and
 * task details. Report tasks is running in <b>ReportTaskEngine</b>.
 * If there is no task to do, it will sleep for <b>SLEEP_TIME_MS</b>.
 * Please modify this class carefully.
 *
 */
public class GeneralTaskEngine {
    private static final int SLEEP_TIME_MS = 10000;

    private ConcurrentLinkedQueue<BaseTask> mTaskQueue = new ConcurrentLinkedQueue<BaseTask>();

    private static GeneralTaskEngine sTaskEngineInstance = new GeneralTaskEngine();

    private boolean canceled;

    private static int sID;

    private static boolean sRun;

    private GeneralTaskEngine() {
        run();
    }

    public static GeneralTaskEngine getInstance() {
        LogUtils.logD("GeneralTaskEngine.getInstance");
        return sTaskEngineInstance;
    }

    private void run() {
        if (sRun) {
            LogUtils.logE("TaskEngine has been running!");
            return;
        }
        sRun = true;
        LogUtils.logD("GeneralTaskEngine.run, ID" + sID);
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
                            LogUtils.logE("GeneralTaskEngine has been canceled, ID");
                            return;
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
        }, "GeneralTaskEngine" + sID++).start();

    }

    public void clearAllTasks() {
        mTaskQueue.clear();
    }

    // public void restart() {
    // LogUtils.logD("GeneralTaskEngine.restart");
    // canceled = true;
    // sID++;
    // sTaskQueue.clear();
    // canceled = false;
    // run();
    // }

    public void appendTask(BaseTask task) {
        LogUtils.logD("GeneralTaskEngine.appendTask" + task);
        if (task == null) {
            return;
        }
        mTaskQueue.add(task);
    }

    public void cancelTask(BaseTask task) {
        LogUtils.logD("GeneralTaskEngine.cancelTask" + task);
        if (task == null) {
            return;
        }
        task.markAsCanceled();
        mTaskQueue.remove(task);
    }
}
