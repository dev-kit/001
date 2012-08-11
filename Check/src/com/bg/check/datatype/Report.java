
package com.bg.check.datatype;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class Report implements KvmSerializable {

    public String mReport_id;

    public String mMessage_id;

    public String mTask_id;

    public String mReport_contentid;

    public String mReport_lx;

    public String mReport_czbz;;

    public String mReport_zmlm;

    public String mXXString1;

    public String mXXString2;

    public String mXXString3;

    public String mXXDate1;

    public String mXXDate2;

    public String mXXDate3;

    @Override
    public Object getProperty(int arg0) {
        switch (arg0) {
            case 0:
                return mReport_id;

            case 1:
                return mMessage_id;

            case 2:
                return mTask_id;

            case 3:
                return mReport_contentid;

            case 4:
                return mReport_lx;

            case 5:
                return mReport_czbz;

            case 6:
                return mReport_zmlm;

            case 7:
                return mXXString1;

            case 8:
                return mXXString2;

            case 9:
                return mXXString3;

            case 10:
                return mXXDate1;

            case 11:
                return mXXDate2;

            case 12:
                return mXXDate3;
        }
        return null;
    }

    @Override
    public int getPropertyCount() {
        return 13;
    }

    @Override
    public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo info) {
        switch (arg0) {
            case 0:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Report_id";

                break;
            case 1:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Message_id";

                break;
            case 2:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Task_id";

                break;
            case 3:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Report_contentid";

                break;
            case 4:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Report_lx";

                break;
            case 5:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Report_czbz";

                break;
            case 6:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "Report_zmlm";

                break;
            case 7:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "XXString1";

                break;
            case 8:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "XXString2";

                break;
            case 9:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "XXString3";

                break;
            case 10:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "XXDate1";

                break;
            case 11:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "XXDate2";

                break;
            case 12:
                info.type = PropertyInfo.STRING_CLASS;
                info.name = "XXDate3";
        }
    }

    @Override
    public void setProperty(int arg0, Object value) {
        switch (arg0) {
            case 0:
                mReport_id = value.toString();
                break;

            case 1:
                mMessage_id = value.toString();
                break;

            case 2:
                mTask_id = value.toString();
                break;

            case 3:
                mReport_contentid = value.toString();
                break;

            case 4:
                mReport_lx = value.toString();
                break;

            case 5:
                mReport_czbz = value.toString();
                break;

            case 6:
                mReport_zmlm = value.toString();
                break;

            case 7:
                mXXString1 = value.toString();
                break;

            case 8:
                mXXString2 = value.toString();
                break;

            case 9:
                mXXString3 = value.toString();
                break;

            case 10:
                mXXDate1 = value.toString();
                break;

            case 11:
                mXXDate2 = value.toString();
                break;

            case 12:
                mXXDate3 = value.toString();
                break;
        }

    }

}
