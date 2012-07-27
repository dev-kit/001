
package com.bg.check.ui.database;

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
//        CREATE TABLE sc_qbzw (FLAG_UP TEXT, TASK_MESSAGEID TEXT, ZXSJ TEXT, USERDM TEXT, PK TEXT, SWH NUMERIC, CH TEXT, CZ TEXT, YZ TEXT, ZMLM TEXT, ZAIZ TEXT, DZM TEXT, PM TEXT, SHR TEXT, FZM TEXT, PB TEXT, JSL TEXT, QBID TEXT, HJ_ZSJ TEXT, HJ_YSJ TEXT, LSSJ TEXT, HJZYSX TEXT, LJZYSX TEXT);
//        CREATE TABLE tasks (JSSJ_ZT TEXT, JSSJ TEXT, KSSJ_ZT TEXT, KSSJ TEXT, HZSJ_ZT TEXT, HZSJ TEXT, ZXJG TEXT, TASK_MESSAGEID TEXT, TASK_ID TEXT, TASK_CONTENTID TEXT, TASK_CZBZ TEXT, TASK_CC TEXT, TASK_GDM TEXT, TASK_ZYR TEXT, TASK_SCHM TEXT, TASK_LX TEXT, TASK_ZMLM TEXT, TASK_JLSJ TEXT, TASK_JCWZ TEXT, TASK_QSXH TEXT, TASK_ZZXH TEXT, USERDM TEXT);

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
