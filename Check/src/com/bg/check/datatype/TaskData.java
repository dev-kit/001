
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
        SoapObject result = (SoapObject)soap.getProperty(0);
        // result = (SoapObject)result.getProperty(0);
        // result = (SoapObject)result.getProperty(0);
        loopSoap(result);

    }

    private void loopSoap(SoapObject result) {
        SoapObject nextSoapObject;
        for (int i = 0; i < result.getPropertyCount(); i++) {
            Object childs = (Object)result.getProperty(i);
            if (childs instanceof SoapObject) {
                loopSoap((SoapObject)childs);
            } else {
                LogUtils.logD(childs.toString());
                addTasks(result);
                return;
            }
        }
    }

    private void addTasks(SoapObject soap) {
        mTaskMessageID = Long.parseLong(soap
                .getPropertySafelyAsString(Databasehelper.TASK_MESSAGEID));
        mTaskID = Long.parseLong(soap.getPropertySafely(Databasehelper.TASK_ID).toString());
        mTask_ContentID = Long.parseLong(soap.getPropertySafely(Databasehelper.TASK_CONTENTID)
                .toString());
        mTaskCZBZ = Integer.parseInt(soap.getPropertySafely(Databasehelper.TASK_CZBZ).toString());
        mTaskCC = soap.getPropertySafelyAsString(Databasehelper.TASK_CC);
        mTaskGDM = soap.getPropertySafelyAsString(Databasehelper.TASK_GDM);
        mTaskZYR = soap.getPropertySafelyAsString(Databasehelper.TASK_ZYR);
        mTaskSCHM = soap.getPropertySafelyAsString(Databasehelper.TASK_SCHM);
        mTaskLX = Integer.parseInt(soap.getPropertySafely(Databasehelper.TASK_LX).toString());
        mTaskZMLM = soap.getPropertySafelyAsString(Databasehelper.TASK_ZMLM);
        mTaskJLSJ = soap.getPropertySafelyAsString(Databasehelper.TASK_JLSJ);
        mTaskJCWZ = soap.getPropertySafelyAsString(Databasehelper.TASK_JCWZ);
        mTaskQSXH = Integer.parseInt(soap.getPropertySafely(Databasehelper.TASK_QSXH).toString());
        mTaskZZXH = Integer.parseInt(soap.getPropertySafely(Databasehelper.TASK_ZZXH).toString());
        mTaskLYFX = soap.getPropertySafelyAsString(Databasehelper.TASK_LYFX);
        updateDB();
    }

    public void updateDB() {
        SQLiteDatabase db = Databasehelper.getInstance().getWritableDatabase();
        String where = Databasehelper.TASK_ID + "=" + mTaskID;
        ContentValues values = new ContentValues();
        values.put(Databasehelper.TASK_ID, mTaskID);
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
