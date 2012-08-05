
package com.bg.check.datatype;

import org.ksoap2.serialization.SoapObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bg.check.database.Database;
import com.bg.check.database.DatabaseHandler;
import com.bg.check.engine.utils.LogUtils;

public class TaskData {
    // <TASK_MESSAGEID/>
    public long mTaskMessageID;

    // <TASK_ID />
    public long mTaskID;

    // <TASK_CONTENTID />
    public long mTask_ContentID;

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

    public TaskData(SoapObject soap) {
        mTaskMessageID = (Long)soap.getProperty(Database.TASK_MESSAGEID);
        mTaskID = (Long)soap.getProperty(Database.TASK_ID);
        mTask_ContentID = (Long)soap.getProperty(Database.TASK_CONTENTID);
        mTaskCZBZ = (Integer)soap.getProperty(Database.TASK_CZBZ);
        mTaskCC = soap.getPropertyAsString(Database.TASK_CC);
        mTaskGDM = soap.getPropertyAsString(Database.TASK_GDM);
        mTaskZYR = soap.getPropertyAsString(Database.TASK_ZYR);
        mTaskSCHM = soap.getPropertyAsString(Database.TASK_SCHM);
        mTaskLX = (Integer)soap.getProperty(Database.TASK_LX);
        mTaskZMLM = soap.getPropertyAsString(Database.TASK_ZMLM);
        mTaskJLSJ = soap.getPropertyAsString(Database.TASK_JLSJ);
        mTaskJCWZ = soap.getPropertyAsString(Database.TASK_JCWZ);
        mTaskQSXH = (Integer)soap.getProperty(Database.TASK_QSXH);
        mTaskZZXH = (Integer)soap.getProperty(Database.TASK_ZZXH);
        mTaskLYFX = soap.getPropertyAsString(Database.TASK_LYFX);
    }

    public void updateDB() {
        String where = Database.TASK_ID + "=" + mTaskID;
        final ContentValues values = new ContentValues();
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

        values.put(Database.TASK_CONTENTID, mTask_ContentID);
        Cursor c = DatabaseHandler.query(Database.TABLE_SC_TASK, null, where, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            LogUtils.logD("Duplicated tasks " + this);
        } else {
            DatabaseHandler.insert(Database.TABLE_SC_TASK, values);
        }
    }

    public String toString() {
        return mTaskMessageID + " " + mTaskID + " " + mTask_ContentID + " " + mTaskCZBZ + " "
                + mTaskCC + " " + mTaskGDM + " " + mTaskZYR + " " + mTaskSCHM + " " + mTaskLX + " "
                + mTaskZMLM + " " + mTaskJLSJ + " " + mTaskJCWZ + " " + mTaskQSXH + " " + mTaskZZXH
                + " " + mTaskLYFX;
    }
}
