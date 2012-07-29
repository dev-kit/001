
package com.bg.check.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Databasehelper extends SQLiteOpenHelper {
    public static final String COLUMN_ID = "_id";

    public static final String TASK_CONTENT_CONTENT_ID = "CONTENT_ID";

    public static final String TASK_CONTENT_LJZYSX = "LJZYSX";

    public static final String TASK_CONTENT_HJZYSX = "HJZYSX";

    public static final String TASK_CONTENT_LSSJ = "LSSJ";

    public static final String TASK_CONTENT_HJ_YSJ = "HJ_YSJ";

    public static final String TASK_CONTENT_HJ_ZSJ = "HJ_ZSJ";

    public static final String TASK_CONTENT_QBID = "QBID";

    public static final String TASK_CONTENT_JSL = "JSL";

    public static final String TASK_CONTENT_PB = "PB";

    public static final String TASK_CONTENT_FZM = "FZM";

    public static final String TASK_CONTENT_SHR = "SHR";

    public static final String TASK_CONTENT_PM = "PM";

    public static final String TASK_CONTENT_DZM = "DZM";

    public static final String TASK_CONTENT_ZAIZ = "ZAIZ";

    public static final String TASK_CONTENT_ZMLM = "ZMLM";

    public static final String TASK_CONTENT_YZ = "YZ";

    public static final String TASK_CONTENT_CZ = "CZ";

    public static final String TASK_CONTENT_CH = "CH";

    public static final String TASK_CONTENT_SWH = "SWH";

    public static final String TASK_CONTENT_PK = "PK";

    public static final String TASK_LYFX = "TASK_LYFX";

    public static final String TASK_JLSJ = "TASK_JLSJ";

    public static final String TASK_ZZXH = "TASK_ZZXH";

    public static final String TASK_QSXH = "TASK_QSXH";

    public static final String TASK_JCWZ = "TASK_JCWZ";

    public static final String TASK_MESSAGEID = "TASK_MESSAGEID";

    public static final String TASK_DQSJ = "TASK_DQSJ";

    public static final String TASK_LX = "TASK_LX";

    public static final String TASK_ZMLM = "TASK_ZMLM";

    public static final String TASK_SCHM = "TASK_SCHM";

    public static final String TASK_ZYR = "TASK_ZYR";

    public static final String TASK_CZBZ = "TASK_CZBZ";

    public static final String TASK_GDM = "TASK_GDM";

    public static final String TASK_CC = "TASK_CC";

    public static final String TASK_CONTENTID = "TASK_CONTENTID";

    public static final String XLTASK_ID = "XLTASK_ID";

    public static final String TASK_ID = "TASK_ID";

    public static final String USER_ZMLM = "USER_ZMLM";

    public static final String USER_TIME = "USER_TIME";

    public static final String USER_ROLE = "USER_ROLE";

    public static final String USER_ONLINE = "USER_ONLINE";

    public static final String USER_MOBILE = "USER_MOBILE";

    public static final String USER_NAME = "USER_NAME";

    public static final String USER_DM = "USER_DM";

    public static final String DATABASE_NAME = "lzbdata";

    public static final int DATABASE_VERSION = 1;

    public static final String TABLE_SC_USER = "sc_user";
    
    public static final String TABLE_SC_TASK = "sc_task";

    public static final String TABLE_SC_TASK_CONTENT = "sc_task_content";

    private static Databasehelper sInstance = null;

    public Databasehelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createUserTable(db);
        createTaskTable(db);
        createTaskContentTable(db);
    }

    private void createUserTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SC_USER + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
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
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                TASK_ID + " NUMBER(10) not null," +
                XLTASK_ID  + " NUMBER(10)," +
                TASK_CONTENTID + " NUMBER(20)," +
                TASK_CC   + "   VARCHAR2(30) not null," +
                TASK_GDM   + " VARCHAR2(20) not null," +
                TASK_CZBZ  + " NUMBER(1) not null," +
                TASK_ZYR    + " VARCHAR2(10) not null," +
                TASK_SCHM  + " VARCHAR2(10)," +
                TASK_ZMLM  + " VARCHAR2(4) not null," +
                TASK_LX   + " NUMBER(2) not null," +
                TASK_DQSJ  + " DATE," +
                TASK_MESSAGEID + " NUMBER(10)," +
                TASK_JCWZ   + " VARCHAR2(2)," +
                TASK_QSXH  + " NUMBER(3)," +
                TASK_ZZXH   + " NUMBER(3)," +
                TASK_JLSJ   + " DATE," +
                TASK_LYFX   + " VARCHAR2(10));");
    }

    private void createTaskContentTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SC_TASK_CONTENT + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                TASK_CONTENT_CONTENT_ID + "  TEXT, " +
                TASK_CONTENT_PK + "  TEXT, " +
                TASK_CONTENT_SWH + "  TEXT," +
                TASK_CONTENT_CH + "  TEXT, " +
                TASK_CONTENT_CZ + "  TEXT, " +
                TASK_CONTENT_YZ + "  TEXT, " +
                TASK_CONTENT_ZMLM + "  TEXT, " +
                TASK_CONTENT_ZAIZ + "  TEXT, " +
                TASK_CONTENT_DZM + "  TEXT," +
                TASK_CONTENT_PM + "  TEXT, " +
                TASK_CONTENT_SHR + "  TEXT," +
                TASK_CONTENT_FZM + "  TEXT," +
                TASK_CONTENT_PB + "  TEXT, " +
                TASK_CONTENT_JSL + "  TEXT," +
                TASK_CONTENT_QBID + "  TEXT,   " +
                TASK_CONTENT_HJ_ZSJ + "  TEXT, " +
                TASK_CONTENT_HJ_YSJ + "  TEXT, " +
                TASK_CONTENT_LSSJ + "  TEXT,   " +
                TASK_CONTENT_HJZYSX + "  TEXT, " +
                TASK_CONTENT_LJZYSX + "  TEXT" +
                ");");
    }

    /**
     * Return a singleton helper for the database. You should involve init() when application 
     * is launched firstly.
     */
    public static Databasehelper getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException();
        }
        return sInstance;
    }

    public static synchronized void init(Context context) {
        if (context == null) {
            throw new IllegalArgumentException();
        }
        if (sInstance == null) {
            sInstance = new Databasehelper(context);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
