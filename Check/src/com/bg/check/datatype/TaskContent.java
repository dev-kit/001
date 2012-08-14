
package com.bg.check.datatype;

import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.content.ContentValues;
import android.database.Cursor;

import com.bg.check.database.Database;
import com.bg.check.database.DatabaseHandler;
import com.bg.check.engine.utils.LogUtils;

public class TaskContent {
    public long mMessageID;

    public long mContentID;

    public String mUserDM;

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

    public static void parseTaskContent(SoapObject soap, String userDM, long contentID,
            long messageID, List<TaskContent> tasks) {
        SoapObject result = (SoapObject)soap.getProperty(0);
        loopSoap(result, userDM, contentID, messageID, tasks);
    }

    public TaskContent() {
    }

    private static void loopSoap(SoapObject result, String userDM, long contentID, long messageID,
            List<TaskContent> tasks) {
        for (int i = 0; i < result.getPropertyCount(); i++) {
            Object childs = (Object)result.getProperty(i);
            if (childs instanceof SoapObject) {
                loopSoap((SoapObject)childs, userDM, contentID, messageID, tasks);
            } else {
                // LogUtils.logD(result.toString());
                TaskContent task = new TaskContent();
                task.addTasks(result, userDM, contentID, messageID);
                tasks.add(task);

                return;
            }
        }
    }

    private void addTasks(SoapObject soap, String userDM, long contentID, long messageID) {
        mMessageID = messageID;
        mContentID = contentID;
        mUserDM = userDM;
        mTaskContentPK = soap.getPropertySafelyAsString(Database.TASK_CONTENT_PK);

        mTaskContentSWH = soap.getPropertySafelyAsString(Database.TASK_CONTENT_SWH);

        mTaskContentCH = soap.getPropertySafelyAsString(Database.TASK_CONTENT_CH);

        mTaskContentCZ = soap.getPropertySafelyAsString(Database.TASK_CONTENT_CZ);

        mTaskContentYZ = soap.getPropertySafelyAsString(Database.TASK_CONTENT_YZ);

        mTaskContentZMLM = soap.getPropertySafelyAsString(Database.TASK_CONTENT_ZMLM);

        mTaskContentZAIZ = soap.getPropertySafelyAsString(Database.TASK_CONTENT_ZAIZ);

        mTaskContentDZM = soap.getPropertySafelyAsString(Database.TASK_CONTENT_DZM);

        mTaskContentPM = soap.getPropertySafelyAsString(Database.TASK_CONTENT_PM);

        mTaskContentSHR = soap.getPropertySafelyAsString(Database.TASK_CONTENT_SHR);

        mTaskContentFZM = soap.getPropertySafelyAsString(Database.TASK_CONTENT_FZM);

        mTaskContentPB = soap.getPropertySafelyAsString(Database.TASK_CONTENT_PB);

        mTaskContentJSL = soap.getPropertySafelyAsString(Database.TASK_CONTENT_JSL);

        mTaskContentQBID = soap.getPropertySafelyAsString(Database.TASK_CONTENT_QBID);

        mTaskContentHJ_ZSJ = soap.getPropertySafelyAsString(Database.TASK_CONTENT_HJ_ZSJ);
        if (mTaskContentHJ_ZSJ != null && mTaskContentHJ_ZSJ.length() > 19) {
            mTaskContentHJ_ZSJ = mTaskContentHJ_ZSJ.replace('T', ' ').substring(0, 19);
        }

        mTaskContentHJ_YSJ = soap.getPropertySafelyAsString(Database.TASK_CONTENT_HJ_YSJ);
        if (mTaskContentHJ_YSJ != null && mTaskContentHJ_YSJ.length() > 19) {
            mTaskContentHJ_YSJ = mTaskContentHJ_YSJ.replace('T', ' ').substring(0, 19);
        }

        mTaskContentLSSJ = soap.getPropertySafelyAsString(Database.TASK_CONTENT_LSSJ);

        mTaskContentHJZYSX = soap.getPropertySafelyAsString(Database.TASK_CONTENT_HJZYSX);

        mTaskContentLJZYSX = soap.getPropertySafelyAsString(Database.TASK_CONTENT_LJZYSX);
        updateDB();
    }

    public void updateDB() {
        String where = Database.TASK_CONTENT_PK + "=" + mTaskContentPK;
        ContentValues values = new ContentValues();
        values.put(Database.TASK_CONTENT_CONTENT_ID, mContentID);
        values.put(Database.TASK_MESSAGEID, mMessageID);
        values.put(Database.TASK_CONTENT_USERDM, mUserDM);
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

        Cursor c = DatabaseHandler.query(Database.TABLE_SC_TASK_CONTENT, null, where, null, null,
                null, null);
        if (c != null && c.getCount() > 0) {
            // LogUtils.logD("Duplicated tasks " + this);
            LogUtils.logD("TaskContent : Duplicated tasks ");
        } else {
            DatabaseHandler.insert(Database.TABLE_SC_TASK_CONTENT, values);
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
