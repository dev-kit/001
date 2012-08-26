package com.bg.check.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    public static final String TASK_CONTENT_USERDM = "USERDM";

    private static final String TASK_CONTENTZXSJ = "ZXSJ";

    private static final String TASK_CONTENT_FLAG_UP = "FLAG_UP";

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

    // 0: default
    // 1: to reply
    // 2: reply success
    // 3: to report
    // 4: report success
    public static final String TASK_CONTENT_STATUS = "status";

    /**
     * 标识task为“开始“还是”完成“
     * 0：开始
     * 1：完成
     */
    public static final String TASK_WAIT_SUCCESS = "wait_success";

    public static final String TASK_LYFX = "TASK_LYFX";

    public static final String TASK_JLSJ = "TASK_JLSJ";

    public static final String TASK_ZZXH = "TASK_ZZXH";

    public static final String TASK_QSXH = "TASK_QSXH";

    public static final String TASK_JCWZ = "TASK_JCWZ";

    public static final int TASK_STATUS_DEFAULT = 0;

    public static final int TASK_STATUS_TO_REPLY = TASK_STATUS_DEFAULT + 1;

    public static final int TASK_STATUS_REPLY_SUCCESS = TASK_STATUS_DEFAULT + 2;

    public static final int TASK_STATUS_TO_REPORT = TASK_STATUS_DEFAULT + 3;

    public static final int TASK_STATUS_REPORT_SUCCESS = TASK_STATUS_DEFAULT + 4;
    // 0: default
    // 1: to reply
    // 2: reply success
    // 3: to report
    // 4: report success
    public static final String TASK_STATUS = "status";

    public static final String TASK_BEGIN_TIME = "Task_begin";

    public static final String TASK_FINISH_TIME = "Task_finish";

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

    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_SC_USER = "sc_user";
    
    public static final String TABLE_SC_TASK = "sc_task ";

    public static final String TABLE_SC_TASK_CONTENT = "sc_task_content";

    private static Database sInstance = null;

    public Database(Context context) {
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
                USER_DM +     " TEXT not null," +
                USER_NAME +  " TEXT not null," +
                USER_MOBILE +" TEXT," +
                USER_ONLINE +" INTEGER default 0 not null," +
                USER_ROLE +  " TEXT not null," +
                USER_TIME  + " TEXT," +
                USER_ZMLM + " TEXT not null);");

    }
    
    private void createTaskTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SC_TASK + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                TASK_ID + " INTEGER," +
                XLTASK_ID  + " INTEGER," +
                TASK_CONTENTID + " INTEGER," +
                TASK_CC   + " TEXT," +
                TASK_GDM   + " TEXT," +
                TASK_CZBZ  + " INTEGER," +
                TASK_ZYR    + " TEXT," +
                TASK_SCHM  + " TEXT," +
                TASK_ZMLM  + " TEXT," +
                TASK_LX   + " INTEGER," +
                TASK_DQSJ  + " TEXT," +
                TASK_MESSAGEID + " INTEGER," +
                TASK_JCWZ   + " TEXT," +
                TASK_QSXH  + " INTEGER," +
                TASK_ZZXH   + " INTEGER," +
                TASK_JLSJ   + " TEXT," +
                TASK_LYFX   + " TEXT," +
                TASK_STATUS + " INTEGER default 0," +
                TASK_WAIT_SUCCESS + " INTEGER default 0," +
                TASK_BEGIN_TIME + " TEXT, " +
                TASK_FINISH_TIME + " TEXT " +");");
    }

    private void createTaskContentTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SC_TASK_CONTENT + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                TASK_CONTENT_FLAG_UP + " TEXT," +
                TASK_MESSAGEID + " INTEGER," +
                TASK_CONTENTZXSJ + " TEXT," +
                TASK_CONTENT_USERDM + " TEXT," +
                TASK_CONTENT_CONTENT_ID + "  INTEGER, " +
                TASK_CONTENT_PK + "  TEXT, " +
                TASK_CONTENT_SWH + "  INTEGER," +
                TASK_CONTENT_CH + "  TEXT, " +
                TASK_CONTENT_CZ + "  TEXT, " +
                TASK_CONTENT_YZ + "  TEXT, " +
                TASK_CONTENT_ZMLM + "  TEXT, " +
                TASK_CONTENT_ZAIZ + "  TEXT, " +
                TASK_CONTENT_DZM + "  TEXT," +
                TASK_CONTENT_PM + "  TEXT, " +
                TASK_CONTENT_SHR + "  TEXT," +
                TASK_CONTENT_FZM + "  TEXT NOT NULL," +
                TASK_CONTENT_PB + "  TEXT, " +
                TASK_CONTENT_JSL + "  TEXT," +
                TASK_CONTENT_QBID + "  TEXT,   " +
                TASK_CONTENT_HJ_ZSJ + "  TEXT, " +
                TASK_CONTENT_HJ_YSJ + "  TEXT, " +
                TASK_CONTENT_LSSJ + "  TEXT,   " +
                TASK_CONTENT_HJZYSX + "  TEXT, " +
                TASK_CONTENT_LJZYSX + "  TEXT," +
                TASK_CONTENT_STATUS + " INTEGER default 0 " +
                ");");
    }

    /**
     * Return a singleton helper for the database. You should involve init() when application 
     * is launched firstly.
     */
    public static Database getInstance() {
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
            sInstance = new Database(context);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE " + TABLE_SC_TASK + " ADD COLUMN " + TASK_BEGIN_TIME + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_SC_TASK + " ADD COLUMN " + TASK_FINISH_TIME + " TEXT");
                db.execSQL("ALTER TABLE " + TABLE_SC_TASK + " ADD COLUMN " + TASK_WAIT_SUCCESS + " INTEGER default 0");
                break;
        }
    }
}

