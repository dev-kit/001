package com.bg.check.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bg.check.datatype.User;

public final class DatabaseUtils {

    private final static Databasehelper mDatabase = Databasehelper.getInstance();

    private final static String[] USER_COLUMNS = {
        Databasehelper.USER_NAME,
        Databasehelper.USER_DM,
        Databasehelper.USER_ROLE
    };

    private final static String[] TASK_CONTENT = {
        Databasehelper.TASK_CONTENT_CONTENT_ID + " AS _id ",
        Databasehelper.TASK_CONTENT_CH,
        Databasehelper.TASK_CONTENT_CZ,
        Databasehelper.TASK_CONTENT_DZM,
        Databasehelper.TASK_CONTENT_FZM
    };

    public final static User getUser(String usercode) {
        final SQLiteDatabase db = mDatabase.getReadableDatabase();
        final String selection = Databasehelper.USER_DM + "=?";
        Cursor cursor = db.query(Databasehelper.TABLE_SC_USER, USER_COLUMNS,
                selection, new String[] { usercode }, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            final User user = new User();
            user.mUserName = cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.USER_NAME));
            user.mUserDM = cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.USER_DM));
            user.mUserRole = cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.USER_ROLE));
            return user;
        }

        return null;
    }

    public final static Cursor queryTask() {
        final SQLiteDatabase db = mDatabase.getReadableDatabase();
        return db.query(Databasehelper.TABLE_SC_TASK_CONTENT, TASK_CONTENT, null, null, null, null, null);
    }
}
