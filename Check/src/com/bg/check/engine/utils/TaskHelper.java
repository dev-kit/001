
package com.bg.check.engine.utils;

import android.content.ContentValues;
import android.database.Cursor;

import com.bg.check.database.Database;
import com.bg.check.database.DatabaseHandler;
import com.bg.check.datatype.Report;
import com.bg.check.datatype.TaskContent;
import com.bg.check.datatype.TaskData;
import com.bg.check.datatype.User;
import com.bg.check.engine.ReplyTasksTask;
import com.bg.check.engine.ReportTaskEngine;
import com.bg.check.engine.ReportToBySingleTask;

public class TaskHelper {

    // public static boolean login() {
    // LoginTask task = new LoginTask("mad", "1", "");
    // task.setCallback(new TaskCallback() {
    //
    // @Override
    // public void onCallBack(Object result) {
    //
    // }
    // });
    // GeneralTaskEngine.getInstance().appendTask(task);
    // }
    public static void replyTasks(String dm, int messageIds) {
        ContentValues values = new ContentValues();
        values.put(Database.TASK_STATUS, Database.TASK_STATUS_TO_REPLY);
        String where = Database.TASK_MESSAGEID + "=" + messageIds;
        DatabaseHandler.update(Database.TABLE_SC_TASK, values, where, null);

        Cursor c = DatabaseHandler.query(Database.TABLE_SC_TASK, null, where, null, null, null,
                null);
        if (c != null && c.getCount() <= 0) {
            ReportTaskEngine.getInstance().appendTask(new ReplyTasksTask(dm, new String[] {
                String.valueOf(messageIds)
            }));
        }
    }

    public static void reportTasks(User user, TaskContent taskcontent, long id) {
        ContentValues values = new ContentValues();
        values.put(Database.TASK_STATUS, Database.TASK_STATUS_TO_REPORT);
        String where = Database.COLUMN_ID + "=" + id;
        DatabaseHandler.update(Database.TABLE_SC_TASK_CONTENT, values, where, null);

        Report r = new Report();
        // r.mReport_contentid = String.valueOf(task.mTaskContentID);
        // r.mMessage_id = String.valueOf(task.mTaskMessageID);
        // r.mReport_czbz = String.valueOf(task.mTaskCZBZ);
        // r.mReport_lx = String.valueOf(task.mTaskLX);
        // r.mTask_id = String.valueOf(task.mTaskID);
        ReportTaskEngine.getInstance().appendTask(new ReportToBySingleTask("mad", r));
    }

    public static void reportTasks11(User user, TaskContent taskcontent, long id) {
        ContentValues values = new ContentValues();
        values.put(Database.TASK_STATUS, Database.TASK_STATUS_TO_REPORT);
        String where = Database.COLUMN_ID + "=" + id;
        DatabaseHandler.update(Database.TABLE_SC_TASK_CONTENT, values, where, null);

        Report r = new Report();
        // r.mReport_contentid = String.valueOf(task.mTaskContentID);
        // r.mMessage_id = String.valueOf(task.mTaskMessageID);
        // r.mReport_czbz = String.valueOf(task.mTaskCZBZ);
        // r.mReport_lx = String.valueOf(task.mTaskLX);
        // r.mTask_id = String.valueOf(task.mTaskID);
        ReportTaskEngine.getInstance().appendTask(new ReportToBySingleTask("mad", r));
    }
}
