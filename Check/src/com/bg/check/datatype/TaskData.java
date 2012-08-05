
package com.bg.check.datatype;

import org.ksoap2.serialization.SoapObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bg.check.database.Databasehelper;
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
        mTaskMessageID = (Long)soap.getProperty(Databasehelper.TASK_MESSAGEID);
        mTaskID = (Long)soap.getProperty(Databasehelper.TASK_ID);
        mTask_ContentID = (Long)soap.getProperty(Databasehelper.TASK_CONTENTID);
        mTaskCZBZ = (Integer)soap.getProperty(Databasehelper.TASK_CZBZ);
        mTaskCC = soap.getPropertyAsString(Databasehelper.TASK_CC);
        mTaskGDM = soap.getPropertyAsString(Databasehelper.TASK_GDM);
        mTaskZYR = soap.getPropertyAsString(Databasehelper.TASK_ZYR);
        mTaskSCHM = soap.getPropertyAsString(Databasehelper.TASK_SCHM);
        mTaskLX = (Integer)soap.getProperty(Databasehelper.TASK_LX);
        mTaskZMLM = soap.getPropertyAsString(Databasehelper.TASK_ZMLM);
        mTaskJLSJ = soap.getPropertyAsString(Databasehelper.TASK_JLSJ);
        mTaskJCWZ = soap.getPropertyAsString(Databasehelper.TASK_JCWZ);
        mTaskQSXH = (Integer)soap.getProperty(Databasehelper.TASK_QSXH);
        mTaskZZXH = (Integer)soap.getProperty(Databasehelper.TASK_ZZXH);
        mTaskLYFX = soap.getPropertyAsString(Databasehelper.TASK_LYFX);
    }

    public void updateDB() {
        SQLiteDatabase db = Databasehelper.getInstance().getWritableDatabase();
        String where = Databasehelper.TASK_ID + "=" + mTaskID;
        ContentValues values = new ContentValues();
        values.put(Databasehelper.TASK_LYFX, mTaskLYFX);

        values.put(Databasehelper.TASK_JLSJ, mTaskJLSJ);

        values.put(Databasehelper.TASK_ZZXH, mTaskZZXH);

        values.put(Databasehelper.TASK_QSXH, mTaskQSXH);

        values.put(Databasehelper.TASK_JCWZ, mTaskJCWZ);

        values.put(Databasehelper.TASK_MESSAGEID, mTaskMessageID);

        values.put(Databasehelper.TASK_LX, mTaskLX);

        values.put(Databasehelper.TASK_ZMLM, mTaskZMLM);

        values.put(Databasehelper.TASK_SCHM, mTaskSCHM);

        values.put(Databasehelper.TASK_ZYR, mTaskZYR);

        values.put(Databasehelper.TASK_CZBZ, mTaskCZBZ);

        values.put(Databasehelper.TASK_GDM, mTaskGDM);

        values.put(Databasehelper.TASK_CC, mTaskCC);

        values.put(Databasehelper.TASK_CONTENTID, mTask_ContentID);
        Cursor c = db.query(Databasehelper.TABLE_SC_TASK, null, where, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            LogUtils.logD("Duplicated tasks " + this);
        } else {
            db.insert(Databasehelper.TABLE_SC_TASK, null, values);
        }
    }

    public String toString() {
        return mTaskMessageID + " " + mTaskID + " " + mTask_ContentID + " " + mTaskCZBZ + " "
                + mTaskCC + " " + mTaskGDM + " " + mTaskZYR + " " + mTaskSCHM + " " + mTaskLX + " "
                + mTaskZMLM + " " + mTaskJLSJ + " " + mTaskJCWZ + " " + mTaskQSXH + " " + mTaskZZXH
                + " " + mTaskLYFX;
    }
}
