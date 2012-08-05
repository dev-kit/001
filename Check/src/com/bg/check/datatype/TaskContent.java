
package com.bg.check.datatype;

import org.ksoap2.serialization.SoapObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bg.check.database.Database;
import com.bg.check.engine.utils.LogUtils;

public class TaskContent {
    public long mMessageID;

    public long mContentID;

    public String mTaskContentPK;

    public String mTaskContentSWH;

    public String mTaskContentCH;

    public String mTaskContentCZ;

    public String mTaskContentYZ;

    public String mTaskContentZMLM;

    public String mTaskContentZAIZ;

    public String mTaskContentDZM;

    public String mTaskContentPM;

    public String mTaskContentSHR;

    public String mTaskContentFZM;

    public String mTaskContentPB;

    public String mTaskContentJSL;

    public String mTaskContentQBID;

    public String mTaskContentHJ_ZSJ;

    public String mTaskContentHJ_YSJ;

    public String mTaskContentLSSJ;

    public String mTaskContentHJZYSX;

    public String mTaskContentLJZYSX;

    public TaskContent(SoapObject soap) {
        mTaskContentPK = soap.getPropertyAsString(Database.TASK_CONTENT_PK);

        mTaskContentSWH = soap.getPropertyAsString(Database.TASK_CONTENT_SWH);

        mTaskContentCH = soap.getPropertyAsString(Database.TASK_CONTENT_CH);

        mTaskContentCZ = soap.getPropertyAsString(Database.TASK_CONTENT_CZ);

        mTaskContentYZ = soap.getPropertyAsString(Database.TASK_CONTENT_YZ);

        mTaskContentZMLM = soap.getPropertyAsString(Database.TASK_CONTENT_ZMLM);

        mTaskContentZAIZ = soap.getPropertyAsString(Database.TASK_CONTENT_ZAIZ);

        mTaskContentDZM = soap.getPropertyAsString(Database.TASK_CONTENT_DZM);

        mTaskContentPM = soap.getPropertyAsString(Database.TASK_CONTENT_PM);

        mTaskContentSHR = soap.getPropertyAsString(Database.TASK_CONTENT_SHR);

        mTaskContentFZM = soap.getPropertyAsString(Database.TASK_CONTENT_FZM);

        mTaskContentPB = soap.getPropertyAsString(Database.TASK_CONTENT_PB);

        mTaskContentJSL = soap.getPropertyAsString(Database.TASK_CONTENT_JSL);

        mTaskContentQBID = soap.getPropertyAsString(Database.TASK_CONTENT_QBID);

        mTaskContentHJ_ZSJ = soap.getPropertyAsString(Database.TASK_CONTENT_HJ_ZSJ);

        mTaskContentHJ_YSJ = soap.getPropertyAsString(Database.TASK_CONTENT_HJ_YSJ);

        mTaskContentLSSJ = soap.getPropertyAsString(Database.TASK_CONTENT_LSSJ);

        mTaskContentHJZYSX = soap.getPropertyAsString(Database.TASK_CONTENT_HJZYSX);

        mTaskContentLJZYSX = soap.getPropertyAsString(Database.TASK_CONTENT_LJZYSX);
    }

    public void updateDB() {
        SQLiteDatabase db = Database.getInstance().getWritableDatabase();
        String where = Database.TASK_CONTENT_CONTENT_ID + "=" + mContentID;
        ContentValues values = new ContentValues();
        values.put(Database.TASK_CONTENTID, mContentID);
        values.put(Database.TASK_MESSAGEID, mMessageID);
        values.put(Database.TASK_CONTENT_PK, mTaskContentPK);

        values.put(Database.TASK_CONTENT_SWH, mTaskContentSWH);

        values.put(Database.TASK_CONTENT_CH, mTaskContentCH);

        values.put(Database.TASK_CONTENT_CZ, mTaskContentCZ);

        values.put(Database.TASK_CONTENT_YZ, mTaskContentYZ);

        values.put(Database.TASK_CONTENT_ZMLM, mTaskContentZMLM);

        values.put(Database.TASK_CONTENT_ZAIZ, mTaskContentZAIZ);

        values.put(Database.TASK_CONTENT_DZM, mTaskContentDZM);

        values.put(Database.TASK_CONTENT_PM, mTaskContentPM);

        values.put(Database.TASK_CONTENT_SHR, mTaskContentSHR);

        values.put(Database.TASK_CONTENT_FZM, mTaskContentFZM);

        values.put(Database.TASK_CONTENT_PB, mTaskContentPB);

        values.put(Database.TASK_CONTENT_JSL, mTaskContentJSL);

        values.put(Database.TASK_CONTENT_QBID, mTaskContentQBID);

        values.put(Database.TASK_CONTENT_HJ_ZSJ, mTaskContentHJ_ZSJ);

        values.put(Database.TASK_CONTENT_HJ_YSJ, mTaskContentHJ_YSJ);

        values.put(Database.TASK_CONTENT_LSSJ, mTaskContentLSSJ);

        values.put(Database.TASK_CONTENT_HJZYSX, mTaskContentHJZYSX);

        values.put(Database.TASK_CONTENT_LJZYSX, mTaskContentLJZYSX);

        Cursor c = db.query(Database.TABLE_SC_TASK_CONTENT, null, where, null, null, null,
                null);
        if (c != null && c.getCount() > 0) {
            LogUtils.logD("Duplicated tasks " + this);
        } else {
            db.insert(Database.TABLE_SC_TASK_CONTENT, null, values);
        }
    }

    public String toString() {
        return mTaskContentPK + " " + mTaskContentSWH + " " + mTaskContentCH + " " + mTaskContentCZ
                + " " + mTaskContentYZ + " " + mTaskContentZMLM + " " + mTaskContentZAIZ + " "
                + mTaskContentDZM + " " + mTaskContentPM + " " + mTaskContentSHR + " "
                + mTaskContentFZM + " " + mTaskContentPB + " " + mTaskContentJSL + " "
                + mTaskContentQBID + " " + mTaskContentHJ_ZSJ + " " + mTaskContentHJ_YSJ + " "
                + mTaskContentLSSJ + " " + mTaskContentHJZYSX + " " + mTaskContentLJZYSX;

    }
}
