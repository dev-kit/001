
package com.bg.check.engine.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.bg.check.database.Database;
import com.bg.check.database.DatabaseHandler;
import com.bg.check.datatype.Report;
import com.bg.check.datatype.TaskContent;
import com.bg.check.datatype.TaskData;
import com.bg.check.datatype.User;
import com.bg.check.engine.BaseTask.TaskCallback;
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
    public static void replyTasks(Context context, String dm, final int messageIds) {
        ContentValues values = new ContentValues();
        values.put(Database.TASK_STATUS, Database.TASK_STATUS_TO_REPLY);
        String where = Database.TASK_MESSAGEID + "=" + messageIds;
        DatabaseHandler.update(Database.TABLE_SC_TASK, values, where, null);

        ReplyTasksTask task = new ReplyTasksTask(context, dm, new String[] {
            String.valueOf(messageIds)
        });
        task.setCallback(new TaskCallback() {

            @Override
            public void onCallBack(Object result) {
                if (result == null || (Integer)result != 1) {
                    return;
                }
                ContentValues values = new ContentValues();
                values.put(Database.TASK_STATUS, Database.TASK_STATUS_REPLY_SUCCESS);
                String where = Database.TASK_MESSAGEID + "=" + messageIds;
                DatabaseHandler.update(Database.TABLE_SC_TASK, values, where, null);

            }
        });
        ReportTaskEngine.getInstance().appendTask(task);
    }

    public static void reportTasks(Context context, User user, TaskContent taskcontent, final long id) {
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
        ReportToBySingleTask task = new ReportToBySingleTask(context, user.mUserDM, r);
        task.setCallback(new TaskCallback() {

            @Override
            public void onCallBack(Object result) {
                if (result == null || (Integer)result != 1) {
                    return;
                }
                ContentValues values = new ContentValues();
                values.put(Database.TASK_STATUS, Database.TASK_STATUS_REPORT_SUCCESS);
                String where = Database.COLUMN_ID + "=" + id;
                DatabaseHandler.update(Database.TABLE_SC_TASK_CONTENT, values, where, null);
            }
        });
        ReportTaskEngine.getInstance().appendTask(task);
    }

    public static void reportTasksForSingleTask(Context context, User user, TaskContent taskcontent, final long id) {
        ContentValues values = new ContentValues();
        values.put(Database.TASK_STATUS, Database.TASK_STATUS_TO_REPORT);
        String where = Database.COLUMN_ID + "=" + id;
        DatabaseHandler.update(Database.TABLE_SC_TASK, values, where, null);

        Report r = new Report();
        // r.mReport_contentid = String.valueOf(task.mTaskContentID);
        // r.mMessage_id = String.valueOf(task.mTaskMessageID);
        // r.mReport_czbz = String.valueOf(task.mTaskCZBZ);
        // r.mReport_lx = String.valueOf(task.mTaskLX);
        // r.mTask_id = String.valueOf(task.mTaskID);
        ReportToBySingleTask task = new ReportToBySingleTask(context, user.mUserDM, r);
//        task.setCallback(new TaskCallback() {
//
//            @Override
//            public void onCallBack(Object result) {
//                ContentValues values = new ContentValues();
//                values.put(Database.TASK_STATUS, Database.TASK_STATUS_REPORT_SUCCESS);
//                String where = Database.COLUMN_ID + "=" + id;
//                DatabaseHandler.update(Database.TABLE_SC_TASK, values, where, null);
//            }
//        });
        ReportTaskEngine.getInstance().appendTask(task);
    }
}
