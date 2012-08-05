
package com.bg.check.datatype;

import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.bg.check.database.Database;
import com.bg.check.database.DatabaseHandler;
import com.bg.check.engine.utils.LogUtils;

public class TaskData {
    // <TASK_MESSAGEID/>
    public long mTaskMessageID;

    // <TASK_ID />
    public long mTaskID;

    // <TASK_CONTENTID />
    public long mTaskContentID;

    // <TASK_CZBZ />
    public int mTaskCZBZ;

    // <TASK_CC />
    public String mTaskCC;

    // <TASK_GDM />
    public String mTaskGDM;

    // <TASK_ZYR />
    public String mTaskZYR;

    // <TASK_SCHM />
    public String mTaskSCHM;

    // <TASK_LX />
    public int mTaskLX;

    // <TASK_ZMLM />
    public String mTaskZMLM;

    // <TASK_JLSJ />
    public String mTaskJLSJ;

    // <TASK_JCWZ />
    public String mTaskJCWZ;

    // <TASK_QSXH />
    public int mTaskQSXH;

    // <TASK_ZZXH />
    public int mTaskZZXH;

    // <TASK_LYFX />
    public String mTaskLYFX;

    public static void parseTask(SoapObject soap, List<TaskData> tasks) {
        SoapObject result = (SoapObject)soap.getProperty(0);
        loopSoapObject(result, tasks);
    }

    private static void loopSoapObject(SoapObject result, List<TaskData> tasks) {
        for (int i = 0; i < result.getPropertyCount(); i++) {
            Object childs = (Object)result.getProperty(i);
            if (childs instanceof SoapObject) {
                loopSoapObject((SoapObject)childs, tasks);
            } else {
                // LogUtils.logD(result.toString());
                TaskData task = new TaskData();
                task.addTasks(result);
                tasks.add(task);
                return;
            }
        }
    }

    private void addTasks(SoapObject soap) {
        mTaskMessageID = Long.parseLong(soap.getPropertySafelyAsString(Database.TASK_MESSAGEID));
        mTaskID = Long.parseLong(soap.getPropertySafely(Database.TASK_ID).toString());
        mTaskContentID = Long.parseLong(soap.getPropertySafely(Database.TASK_CONTENTID).toString());
        mTaskCZBZ = Integer.parseInt(soap.getPropertySafely(Database.TASK_CZBZ).toString());
        mTaskCC = soap.getPropertySafelyAsString(Database.TASK_CC);
        mTaskGDM = soap.getPropertySafelyAsString(Database.TASK_GDM);
        mTaskZYR = soap.getPropertySafelyAsString(Database.TASK_ZYR);
        mTaskSCHM = soap.getPropertySafelyAsString(Database.TASK_SCHM);
        mTaskLX = Integer.parseInt(soap.getPropertySafely(Database.TASK_LX).toString());
        mTaskZMLM = soap.getPropertySafelyAsString(Database.TASK_ZMLM);
        mTaskJLSJ = soap.getPropertySafelyAsString(Database.TASK_JLSJ);
        mTaskJCWZ = soap.getPropertySafelyAsString(Database.TASK_JCWZ);
        mTaskQSXH = Integer.parseInt(soap.getPropertySafely(Database.TASK_QSXH).toString());
        mTaskZZXH = Integer.parseInt(soap.getPropertySafely(Database.TASK_ZZXH).toString());
        mTaskLYFX = soap.getPropertySafelyAsString(Database.TASK_LYFX);
        updateDB();
    }

    public void updateDB() {
        String where = Database.TASK_ID + "=" + mTaskID;
        ContentValues values = new ContentValues();
        values.put(Database.TASK_ID, mTaskID);
        values.put(Database.TASK_LYFX, mTaskLYFX);

        values.put(Database.TASK_JLSJ, mTaskJLSJ);

        values.put(Database.TASK_ZZXH, mTaskZZXH);

        values.put(Database.TASK_QSXH, mTaskQSXH);

        values.put(Database.TASK_JCWZ, mTaskJCWZ);

        values.put(Database.TASK_MESSAGEID, mTaskMessageID);

        values.put(Database.TASK_LX, mTaskLX);

        values.put(Database.TASK_ZMLM, mTaskZMLM);

        values.put(Database.TASK_SCHM, mTaskSCHM);

        values.put(Database.TASK_ZYR, mTaskZYR);

        values.put(Database.TASK_CZBZ, mTaskCZBZ);

        values.put(Database.TASK_GDM, mTaskGDM);

        values.put(Database.TASK_CC, mTaskCC);

        values.put(Database.TASK_CONTENTID, mTaskContentID);
        Cursor c = DatabaseHandler.query(Database.TABLE_SC_TASK, null, where, null, null, null,
                null);
        if (c != null && c.getCount() > 0) {
            LogUtils.logD("Duplicated tasks " + this);
        } else {
            DatabaseHandler.insert(Database.TABLE_SC_TASK, values);
        }
    }

    public String toString() {
        return mTaskMessageID + " " + mTaskID + " " + mTaskContentID + " " + mTaskCZBZ + " "
                + mTaskCC + " " + mTaskGDM + " " + mTaskZYR + " " + mTaskSCHM + " " + mTaskLX + " "
                + mTaskZMLM + " " + mTaskJLSJ + " " + mTaskJCWZ + " " + mTaskQSXH + " " + mTaskZZXH
                + " " + mTaskLYFX;
    }
}
