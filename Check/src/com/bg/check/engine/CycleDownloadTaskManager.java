
package com.bg.check.engine;

import java.util.List;

import com.bg.check.datatype.TaskData;
import com.bg.check.engine.BaseTask.TaskCallback;
import com.bg.check.engine.utils.LogUtils;

public class CycleDownloadTaskManager {
    private static int sID;

    private static boolean sRun;

    private static Thread sWorkThread;

    private final static int SLEEP_TIME_MS = 10000; // 10s

    private boolean mIsTaskCompleted = true;

    private static CycleDownloadTaskManager sCycleDownloadTaskManagerInstance = new CycleDownloadTaskManager();

    private CycleDownloadTaskManager() {
    }

    public static CycleDownloadTaskManager getInstance() {
        LogUtils.logD("CycleDownloadTaskManager.getInstance");
        return sCycleDownloadTaskManagerInstance;
    }

    public void run(final String userDM, final String userName, final String userZMLM) {
        if (sRun) {
            LogUtils.logW("CycleDownloadTaskManager has been running, cancel it");
            stop();
        }
        sRun = true;
        LogUtils.logD("CycleDownloadTaskManager.run, ID" + sID);
        sWorkThread = new Thread(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    // TODO: worried about that, if tasks is pending, then more
                    // and more task will be added into.
                    if (mIsTaskCompleted) {
                        mIsTaskCompleted = false;
                        addTask(userDM, userName, userZMLM);
                    }
                    try {
                        Thread.sleep(SLEEP_TIME_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        LogUtils.logW("CycleDownloadTaskManager has been canceled, ID" + sID);
                    }
                }
            }
        }, "CycleDownloadTaskManager" + sID++);
        sWorkThread.start();
    }

    private void addTask(final String userDM, String userName, String userZMLM) {
        final GetTasksTask getTasksTask = new GetTasksTask(userDM, userName, userZMLM);
        TaskCallback getTasksCallback = new TaskCallback() {
            @Override
            public void onCallBack(Object result) {
                if (result == null) {
                    GeneralTaskEngine.getInstance().appendTask(getTasksTask);
                } else {
                    mIsTaskCompleted = true;
                    @SuppressWarnings("unchecked")
                    List<TaskData> tasks = (List<TaskData>)result;
                    for (TaskData task : tasks) {
                        final GetDetailsTask getDetailsTask = new GetDetailsTask(userDM,
                                task.mTaskContentID, task.mTaskLX, task.mTaskMessageID);
                        GeneralTaskEngine.getInstance().appendTask(getDetailsTask);
                        // TODO: How about failed? setCallback
                    }
                }
            }
        };
        getTasksTask.setCallback(getTasksCallback);
        GeneralTaskEngine.getInstance().appendTask(getTasksTask);
    }

    public void stop() {
        if (sWorkThread != null) {
            sWorkThread.interrupt();
        }
    }
}
