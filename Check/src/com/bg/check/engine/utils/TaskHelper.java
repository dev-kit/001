
package com.bg.check.engine.utils;

import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;

import com.bg.check.database.Database;
import com.bg.check.database.DatabaseHandler;
import com.bg.check.datatype.Report;
import com.bg.check.datatype.User;
import com.bg.check.engine.BaseTask.TaskCallback;
import com.bg.check.engine.ReplyTasksTask;
import com.bg.check.engine.ReportTaskEngine;
import com.bg.check.engine.ReportToBySingleTask;

public class TaskHelper {

    public static void replyTasks(Context context, User user, final int taskID) {
        ContentValues values = new ContentValues();
        values.put(Database.TASK_STATUS, Database.TASK_STATUS_TO_REPLY);
        String where = Database.TASK_ID + "=" + taskID;
        DatabaseHandler.updateWithoutNotify(Database.TABLE_SC_TASK, values, where, null);

        Report r = formReport(user, taskID, -1, REPORT_TYPE_REPLY);
        ReportToBySingleTask task = new ReportToBySingleTask(context, user.mUserDM, r);
        task.setCallback(new TaskCallback() {

            @Override
            public void onCallBack(Object result) {
                if (result == null || (Integer)result != 1) {
                    return;
                }
                String where = Database.TASK_ID + "=" + taskID;
                Cursor c = DatabaseHandler.query(Database.TABLE_SC_TASK, new String[] {
                    Database.TASK_STATUS
                }, where, null, null, null, null);
                int status = 0;
                if (c != null) {
                    try {
                        if (c.moveToNext()) {
                            status = c.getInt(0);
                            if (status > Database.TASK_STATUS_TO_REPLY) {
                                return;
                            }
                        }
                    } catch (SQLiteException e) {
                        c.close();
                    }
                }
                ContentValues values = new ContentValues();
                values.put(Database.TASK_STATUS, Database.TASK_STATUS_REPLY_SUCCESS);
                DatabaseHandler.update(Database.TABLE_SC_TASK, values, where, null);

            }
        });
        ReportTaskEngine.getInstance().appendTask(task);
    }

    // public static void replyTasks(Context context, String dm, final int
    // messageIds) {
    // ContentValues values = new ContentValues();
    // values.put(Database.TASK_STATUS, Database.TASK_STATUS_TO_REPLY);
    // String where = Database.TASK_MESSAGEID + "=" + messageIds;
    // DatabaseHandler.updateWithoutNotify(Database.TABLE_SC_TASK, values,
    // where, null);
    //
    // ReplyTasksTask task = new ReplyTasksTask(context, dm, new String[] {
    // String.valueOf(messageIds)
    // });
    // task.setCallback(new TaskCallback() {
    //
    // @Override
    // public void onCallBack(Object result) {
    // if (result == null || (Integer)result != 1) {
    // return;
    // }
    // String where = Database.TASK_MESSAGEID + "=" + messageIds;
    // Cursor c = DatabaseHandler.query(Database.TABLE_SC_TASK, new String[] {
    // Database.TASK_STATUS
    // }, where, null, null, null, null);
    // int status = 0;
    // if (c != null) {
    // try {
    // if (c.moveToNext()) {
    // status = c.getInt(0);
    // if (status > Database.TASK_STATUS_TO_REPLY) {
    // return;
    // }
    // }
    // } catch (SQLiteException e) {
    // c.close();
    // }
    // }
    // ContentValues values = new ContentValues();
    // values.put(Database.TASK_STATUS, Database.TASK_STATUS_REPLY_SUCCESS);
    // DatabaseHandler.update(Database.TABLE_SC_TASK, values, where, null);
    //
    // }
    // });
    // ReportTaskEngine.getInstance().appendTask(task);
    // }

    public static void reportTaskContent(Context context, User user, int taskID,
            final long taskColumnID) {
        ContentValues values = new ContentValues();
        values.put(Database.TASK_STATUS, Database.TASK_STATUS_TO_REPORT);
        String where = Database.COLUMN_ID + "=" + taskColumnID;
        DatabaseHandler.update(Database.TABLE_SC_TASK_CONTENT, values, where, null);

        Report r = formReport(user, taskID, taskColumnID, REPORT_TYPE_TASK_CONTENT);
        ReportToBySingleTask task = new ReportToBySingleTask(context, user.mUserDM, r);
        task.setCallback(new TaskCallback() {

            @Override
            public void onCallBack(Object result) {
                if (result == null || (Integer)result != 1) {
                    return;
                }
                ContentValues values = new ContentValues();
                values.put(Database.TASK_STATUS, Database.TASK_STATUS_REPORT_SUCCESS);
                String where = Database.COLUMN_ID + "=" + taskColumnID;
                DatabaseHandler.update(Database.TABLE_SC_TASK_CONTENT, values, where, null);
            }
        });
        ReportTaskEngine.getInstance().appendTask(task);
    }

    private static final int REPORT_TYPE_TASK = 0;

    private static final int REPORT_TYPE_TASK_CONTENT = 1;

    private static final int REPORT_TYPE_REPLY = 2;

    private static Report formReport(User user, int taskID, long taskColumnID, int reportType) {

        Report r = new Report();
        r.mXXString2 = user.mUserName;
        // r.mXXString3 = "3213";
        // r.mXXDate3 = "32131";
        // r.mReport_id = "5656787990";
        r.mReport_czbz = String.valueOf(reportType);

        String where = Database.TASK_ID + "=" + taskID;
        Cursor taskCursor = DatabaseHandler.query(Database.TABLE_SC_TASK, null, where, null, null,
                null, null);
        if (taskCursor != null) {
            try {
                if (taskCursor.moveToNext()) {
                    r.mReport_zmlm = taskCursor.getString(taskCursor
                            .getColumnIndex(Database.TASK_ZMLM));
                    r.mReport_contentid = taskCursor.getString(taskCursor
                            .getColumnIndex(Database.TASK_CONTENTID));
                    r.mMessage_id = taskCursor.getString(taskCursor
                            .getColumnIndex(Database.TASK_MESSAGEID));
                    r.mTask_id = taskCursor.getString(taskCursor.getColumnIndex(Database.TASK_ID));
                    r.mReport_zmlm = taskCursor.getString(taskCursor
                            .getColumnIndex(Database.TASK_ZMLM));
                    r.mReport_lx = taskCursor
                            .getString(taskCursor.getColumnIndex(Database.TASK_LX));
                    r.mXXDate1 = taskCursor.getString(taskCursor
                            .getColumnIndex(Database.TASK_BEGIN_TIME));
                    r.mXXDate2 = taskCursor.getString(taskCursor
                            .getColumnIndex(Database.TASK_FINISH_TIME));
                    // r.mReport_czbz = taskCursor.getString(taskCursor
                    // .getColumnIndex(Database.TASK_CZBZ));
                }
            } catch (SQLiteException e) {
                LogUtils.logE(e.toString());
                taskCursor.close();
            } finally {
                taskCursor.close();
            }
        }
        switch (reportType) {
            case REPORT_TYPE_TASK:
                break;
            case REPORT_TYPE_TASK_CONTENT:
                where = Database.COLUMN_ID + "=" + taskColumnID;
                Cursor taskContentCursor = DatabaseHandler.query(Database.TABLE_SC_TASK_CONTENT,
                        new String[] {
                            Database.TASK_CONTENT_CH
                        }, where, null, null, null, null);
                if (taskContentCursor != null) {
                    try {
                        if (taskContentCursor.moveToNext()) {
                            r.mXXString1 = taskContentCursor.getString(0);
                        }

                    } catch (SQLiteException e) {
                        LogUtils.logE(e.toString());
                        taskContentCursor.close();
                    } finally {
                        taskContentCursor.close();
                    }
                }
                break;
            case REPORT_TYPE_REPLY:
                r.mXXString1 = "0";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                r.mXXDate1 = simpleDateFormat.format(System.currentTimeMillis());
                r.mXXDate2 = null;
                break;
        }

        return r;
    }

    public static void reportTask(Context context, User user, final int taskID,
            final long contentID, boolean start) {
        String where = Database.TASK_ID + "=" + taskID;
        Cursor c = DatabaseHandler.query(Database.TABLE_SC_TASK, null, where, null, null, null,
                null);
        if (c != null) {
            try {
                if (c.moveToNext()) {
                    if (c.getString(c.getColumnIndex(Database.TASK_FINISH_TIME)) != null) {
                        return;
                    }
                }
            } finally {
                c.close();
            }
        }

        ContentValues values = new ContentValues();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (start) {
            values.put(Database.TASK_BEGIN_TIME,
                    simpleDateFormat.format(System.currentTimeMillis()));
            DatabaseHandler.updateWithoutNotify(Database.TABLE_SC_TASK, values, where, null);
        } else {
            values.put(Database.TASK_STATUS, Database.TASK_STATUS_TO_REPORT);
            values.put(Database.TASK_FINISH_TIME,
                    simpleDateFormat.format(System.currentTimeMillis()));
            DatabaseHandler.update(Database.TABLE_SC_TASK, values, where, null);
        }

        Report r = formReport(user, taskID, -1, REPORT_TYPE_TASK);
        // r.mReport_contentid = String.valueOf(task.mTaskContentID);
        // r.mMessage_id = String.valueOf(task.mTaskMessageID);
        // r.mReport_czbz = String.valueOf(task.mTaskCZBZ);
        // r.mReport_lx = String.valueOf(task.mTaskLX);
        // r.mTask_id = String.valueOf(task.mTaskID);
        ReportToBySingleTask task = new ReportToBySingleTask(context, user.mUserDM, r);
        if (!start) {
            task.setCallback(new TaskCallback() {

                @Override
                public void onCallBack(Object result) {
                    if (result == null || (Integer)result != 1) {
                        return;
                    }

                    ContentValues values = new ContentValues();
                    values.put(Database.TASK_STATUS, Database.TASK_STATUS_REPORT_SUCCESS);
                    String where = Database.TASK_ID + "=" + taskID;
                    DatabaseHandler
                            .updateWithoutNotify(Database.TABLE_SC_TASK, values, where, null);
                }
            });
        }
        ReportTaskEngine.getInstance().appendTask(task);
    }
}
