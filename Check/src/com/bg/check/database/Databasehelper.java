
package com.bg.check.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Databasehelper extends SQLiteOpenHelper {
    private static final String USER_ZMLM = "USER_ZMLM";

    private static final String USER_TIME = "USER_TIME";

    private static final String USER_ROLE = "USER_ROLE";

    private static final String USER_ONLINE = "USER_ONLINE";

    private static final String USER_MOBILE = "USER_MOBILE";

    private static final String USER_NAME = "USER_NAME";

    private static final String USER_DM = "USER_DM";

    private static final String DATABASE_NAME = "lzbdata";

    static final int DATABASE_VERSION = 1;

    private static final String TABLE_SC_USER = "sc_user";
    
    private static final String TABLE_SC_TASK = "sc_task";

    private static Databasehelper sInstance = null;

    public Databasehelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUserTable(db);
        createTaskTable(db);
    }

    private void createUserTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SC_USER + " (" +
                USER_DM +     " VARCHAR2(20) not null," +
                USER_NAME +  " VARCHAR2(20) not null," +
                USER_MOBILE +" VARCHAR2(20)," +
                USER_ONLINE +" NUMBER default 0 not null," +
                USER_ROLE +  " VARCHAR2(20) not null," +
                USER_TIME  + " DATE," +
                USER_ZMLM + " VARCHAR2(20) not null);");

    }
    
    private void createTaskTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SC_TASK + " (" +
                "TASK_ID" + " NUMBER(10) not null," +
                "XLTASK_ID"  + " NUMBER(10)," +
                "TASK_CONTENTID" + " NUMBER(20)," +
                "TASK_CC"   + "   VARCHAR2(30) not null," +
                "TASK_GDM"   + " VARCHAR2(20) not null," +
                "TASK_CZBZ"  + " NUMBER(1) not null," +
                "TASK_ZYR"    + " VARCHAR2(10) not null," +
                "TASK_SCHM"  + " VARCHAR2(10)," +
                "TASK_ZMLM"  + " VARCHAR2(4) not null," +
                "TASK_LX"   + " NUMBER(2) not null," +
                "TASK_DQSJ"  + " DATE," +
                "TASK_MESSAGEID" + " NUMBER(10)," +
                "TASK_JCWZ"   + " VARCHAR2(2)," +
                "TASK_QSXH"  + " NUMBER(3)," +
                "TASK_ZZXH"   + " NUMBER(3)," +
                "TASK_JLSJ"   + " DATE," +
                "TASK_LYFX"   + " VARCHAR2(10));");
    }

    /**
     * Return a singleton helper for the combined MMS and SMS
     * database.
     */
    public static synchronized Databasehelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Databasehelper(context);
        }
        return sInstance;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
