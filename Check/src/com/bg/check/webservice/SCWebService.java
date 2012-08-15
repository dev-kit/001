
package com.bg.check.webservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import com.bg.check.R;

public class SCWebService {
    public static final String SC_NAME_SPACE = "http://tempuri.org/";

    private static final String SC_END_POINT_SUFFIX = "/LzbService/SCService.asmx";

    public static final String SC_METHOD_LOGIN = "Login";

    public static final String SC_METHOD_GET_USER_INFO = "GetUserInfo";

    public static final String SC_METHOD_LOGOUT = "Logout";

    public static final String SC_METHOD_GET_TASKS = "GetTasks";

    public static final String SC_METHOD_REPLY_TASKS = "ReplyTasks";

    public static final String SC_METHOD_GETDETAILS = "GetDetails";

    public static final String SC_METHOD_REPORT_TO_BY_SINGLE = "ReportToBySingle";

    public static final String SC_METHOD_GET_SERVER_TIME = "GetServerTime";

    public static final String getEndPoint(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final Resources res = context.getResources();
        final String ip = preferences.getString("edit_ip", res.getString(R.string.default_ip));
        final String port = preferences.getString("edit_port", res.getString(R.string.default_port));
        return "http://" + ip + ":" + port + SC_END_POINT_SUFFIX;
    }
}
