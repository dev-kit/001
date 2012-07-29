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

    public final static User getUser(String usercode) {
        final SQLiteDatabase db = mDatabase.getReadableDatabase();
        final String selection = Databasehelper.USER_DM + "=?";
        Cursor cursor = db.query(Databasehelper.TABLE_SC_USER, USER_COLUMNS,
                selection, new String[] { usercode }, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            final User user = new User();
            user.mName = cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.USER_NAME));
            user.mCode = cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.USER_DM));
            user.mRole = cursor.getString(cursor.getColumnIndexOrThrow(Databasehelper.USER_ROLE));
            return user;
        }

        return null;
    }
}
