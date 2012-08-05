package com.bg.check.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bg.check.datatype.User;

public final class DatabaseHandler {

    private final static Database mDatabase = Database.getInstance();

    private final static ArrayList<DatabaseObserver> mObservers = new ArrayList<DatabaseHandler.DatabaseObserver>();

    private final static String[] USER_COLUMNS = {
        Database.USER_NAME,
        Database.USER_DM,
        Database.USER_ROLE
    };

    private final static String[] TASK_CONTENT = {
        Database.TASK_ID + " AS _id ",
        Database.TASK_CC,
        Database.TASK_GDM,
        Database.TASK_JCWZ,
        Database.TASK_JLSJ
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

    final SQLiteDatabase db = mDatabase.getWritableDatabase();
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

    public static final Cursor queryTask() {
        final SQLiteDatabase db = mDatabase.getReadableDatabase();
        return db.query(Database.TABLE_SC_TASK, TASK_CONTENT, null, null, null, null, null);
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

    public static int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        final int count = db.update(table, values, whereClause, whereArgs);
        notifyUpdate();
        return count;
    }

    public static int delete(String table, String whereClause, String[] whereArgs) {
        final SQLiteDatabase db = mDatabase.getWritableDatabase();
        final int count = db.delete(table, whereClause, whereArgs);
        notifyUpdate();
        return count;
    }
}
