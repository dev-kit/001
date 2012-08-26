
package com.bg.check.engine;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;

import com.bg.check.engine.utils.LogUtils;
import com.bg.check.webservice.SCWebService;

/**
 * Task to reply task.
 *
 */
public class ReplyTasksTask extends BaseTask {
    private String mUserDM;
    private boolean mDebug = false;

    private String[] mMessageIds;
    private Context mContext;

    public ReplyTasksTask(Context context, String dm, String[] messageIds) {
        mUserDM = dm;
        mMessageIds = messageIds;
        mContext = context.getApplicationContext();
    }

    /**
     * Return ReplyTasks status with Integer Value 1 : success 0 : fail
     */
    @Override
    public Object run() {
        if (mDebug) {
            return 0;
        }
        SoapObject rpc = new SoapObject(SCWebService.SC_NAME_SPACE, SCWebService.SC_METHOD_REPLY_TASKS);

        rpc.addProperty("User_dm", mUserDM);
        SoapObject soap = new SoapObject(SCWebService.SC_NAME_SPACE, SCWebService.SC_METHOD_REPLY_TASKS);
        for (String id : mMessageIds) {
            soap.addProperty("string", id);
        }
        rpc.addProperty("messageids", soap);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(SCWebService.getEndPoint(mContext));
        try {
            transport.call(SCWebService.SC_NAME_SPACE + SCWebService.SC_METHOD_REPLY_TASKS, envelope);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.logE("ReplyTasksTask: " + e);
            return null;
        }
        int result = 0;
        if (envelope.bodyIn instanceof SoapObject) {
            SoapObject object = (SoapObject)envelope.bodyIn;
            result = Integer.parseInt(object.getProperty(0).toString());
            if (result != 1) {
                // Report it automatically if failed
                ReportTaskEngine.getInstance().appendTask(this);
            }
        } else {
            LogUtils.logE("ReplyTasksTask: " + envelope.bodyIn);
        }
        return result;
    }

}
