package com.bg.check.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bg.check.datatype.User;

/**
 * Wrap class to access DB, which has a function to notify UI.
 * UI register database Observer by <b>addDatabaseObserver</b> and remove observer by
 * <b>removeDatabaseObserver</b>
 *
 */
public final class DatabaseHandler {

    private final static Database mDatabase = Database.getInstance();

    private final static ArrayList<DatabaseObserver> mObservers = new ArrayList<DatabaseHandler.DatabaseObserver>();

    private final static String[] USER_COLUMNS = {
        Database.USER_NAME,
        Database.USER_DM,
        Database.USER_ROLE
    };

    private final static String[] TASK_CONTENT = {
        Database.TASK_ID,
        Database.TASK_CONTENTID,
        Database.TASK_CC,
        Database.TASK_GDM,
        Database.TASK_JCWZ,
        Database.TASK_JLSJ,
        Database.TASK_MESSAGEID,
        Database.COLUMN_ID,
        Database.TASK_LX,
        Database.TASK_STATUS,
        Database.TASK_WAIT_SUCCESS
    };

    public interface DatabaseObserver {
        public void onUpdate();
    }

    public static void addDatabaseObserver(DatabaseObserver observer) {
        if (observer != null) {
            mObservers.add(observer);
        }
    }

    public static void removeDatabaseObserver(DatabaseObserver observer) {
        mObservers.remove(observer);
    }

    private static void notifyUpdate() {
        for (DatabaseObserver observer : mObservers) {
            observer.onUpdate();
        }
    }

    public final static User getUser(String usercode) {
        final SQLiteDatabase db = mDatabase.getReadableDatabase();
        final String selection = Database.USER_DM + "=?";
        Cursor cursor = db.query(Database.TABLE_SC_USER, USER_COLUMNS,
                selection, new String[] { usercode }, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            final User user = new User();
            user.mUserName = cursor.getString(cursor.getColumnIndexOrThrow(Database.USER_NAME));
            user.mUserDM = cursor.getString(cursor.getColumnIndexOrThrow(Database.USER_DM));
            user.mUserRole = cursor.getString(cursor.getColumnIndexOrThrow(Database.USER_ROLE));
            return user;
        }

        return null;
    }

    public static final Cursor queryTask(String userName) {
        final SQLiteDatabase db = mDatabase.getReadableDatabase();
        String where = Database.TASK_STATUS + "<" + Database.TASK_STATUS_TO_REPORT
                + " and " +  Database.TASK_ZYR + "='" + userName + "'";
        return db.query(Database.TABLE_SC_TASK, TASK_CONTENT, where, null, null, null, null);
    }

    public static Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        final SQLiteDatabase db = mDatabase.getReadableDatabase();
        return db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public static long insert(String table, ContentValues values) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        final long id = db.insert(table, null, values);
        notifyUpdate();
        return id;
    }

    public static long insertWithoutNotify(String table, ContentValues values) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        final long id = db.insert(table, null, values);
        return id;
    }

    public static void notifyDBObeserver() {
        notifyUpdate();
    }

    public static int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        final int count = db.update(table, values, whereClause, whereArgs);
        notifyUpdate();
        return count;
    }

    public static int updateWithoutNotify(String table, ContentValues values, String whereClause, String[] whereArgs) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        final int count = db.update(table, values, whereClause, whereArgs);
        return count;
    }

    public static int delete(String table, String whereClause, String[] whereArgs) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        final int count = db.delete(table, whereClause, whereArgs);
        notifyUpdate();
        return count;
    }
}
