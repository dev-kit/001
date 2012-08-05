
package com.bg.check.datatype;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.bg.check.database.Databasehelper;

public class User implements KvmSerializable {
    public String mUserName;

    public String mUserDM;

    public String mUserRole;

    private String mPassword;

    private String mUserGWM;

    private String mUserOnline;

    private String mUserSession;

    private String mUserMobile;

    public String mUserZMLM;

    public User() {
    }

    public User(SoapObject soap) {
        mUserDM = soap.getPropertySafelyAsString("User_dm");
        mUserName = soap.getPropertySafelyAsString("User_name");

        mPassword = soap.getPropertySafelyAsString("User_password");

        mUserGWM = soap.getPropertySafelyAsString("User_gwm");

        mUserRole = soap.getPropertySafelyAsString("User_role");

        mUserOnline = soap.getPropertySafelyAsString("User_online");

        mUserSession = soap.getPropertySafelyAsString("User_session");

        mUserMobile = soap.getPropertySafelyAsString("User_mobile");

        mUserZMLM = soap.getPropertySafelyAsString("User_zmlm");
    }

    @Override
    public Object getProperty(int index) {
        Object obj = null;
        switch (index) {
            case 0:
                obj = mUserDM;
                break;
            case 1:
                obj = mUserName;
                break;
            case 2:
                obj = mPassword;
                break;
            case 3:
                obj = mUserGWM;
                break;
            case 4:
                obj = mUserRole;
                break;
            case 5:
                obj = mUserOnline;
                break;
            case 6:
                obj = mUserSession;
                break;
            case 7:
                obj = mUserMobile;
                break;
            case 8:
                obj = mUserZMLM;
        }
        return obj;
    }

    @Override
    public int getPropertyCount() {
        // TODO Auto-generated method stub
        return 9;
    }

    @Override
    public void getPropertyInfo(int index, Hashtable arg1, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "User_dm";
                info.type = PropertyInfo.STRING_CLASS;
                break;
            case 1:
                info.name = "User_name";
                info.type = PropertyInfo.STRING_CLASS;
                break;
            case 2:
                info.name = "User_password";
                info.type = PropertyInfo.STRING_CLASS;
                break;
            case 3:
                info.name = "User_gwm";
                info.type = PropertyInfo.STRING_CLASS;
                break;
            case 4:
                info.name = "User_role";
                info.type = PropertyInfo.STRING_CLASS;
                break;
            case 5:
                info.name = "User_online";
                info.type = PropertyInfo.STRING_CLASS;
                break;
            case 6:
                info.name = "User_session";
                info.type = PropertyInfo.STRING_CLASS;
                break;
            case 7:
                info.name = "User_mobile";
                info.type = PropertyInfo.STRING_CLASS;
                break;
            case 8:
                info.name = "User_zmlm";
                info.type = PropertyInfo.STRING_CLASS;
                break;
        }
    }

    @Override
    public void setProperty(int index, Object obj) {
        switch (index) {
            case 0:
                mUserDM = obj.toString();
                break;
            case 1:
                mUserName = obj.toString();
                break;
            case 2:
                mPassword = obj.toString();
                break;
            case 3:
                mUserGWM = obj.toString();
                break;
            case 4:
                mUserRole = obj.toString();
                break;
            case 5:
                mUserOnline = obj.toString();
                break;
            case 6:
                mUserSession = obj.toString();
                break;
            case 7:
                mUserMobile = obj.toString();
                break;
            case 8:
                mUserZMLM = obj.toString();
        }
    }

    public void updateDB() {
        SQLiteDatabase db = Databasehelper.getInstance().getWritableDatabase();
        String where = Databasehelper.USER_DM + "='" + mUserDM + "'";
        ContentValues values = new ContentValues();
        values.put(Databasehelper.USER_DM, mUserDM);
        values.put(Databasehelper.USER_MOBILE, mUserMobile);
        values.put(Databasehelper.USER_NAME, mUserName);
        values.put(Databasehelper.USER_ONLINE, mUserOnline);
        values.put(Databasehelper.USER_ROLE, mUserRole);
        values.put(Databasehelper.USER_ZMLM, mUserZMLM);
        Cursor c = db.query(Databasehelper.TABLE_SC_USER, null, where, null, null, null, null);
        if (c != null && c.getCount() > 0) {
            db.update(Databasehelper.TABLE_SC_USER, values, null, null);
        } else {
            db.insert(Databasehelper.TABLE_SC_USER, null, values);
        }

    }

}
