
package com.bg.check.engine;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;

import com.bg.check.engine.utils.LogUtils;
import com.bg.check.webservice.SCWebService;

/**
 * Task to logout.
 *
 */
public class LogoutTask extends BaseTask {
    private String mUserDM;
    private Context mContext;

    public LogoutTask(Context context, String dm) {
        mUserDM = dm;
        mContext = context.getApplicationContext();
    }

    /**
     * Return Logout status with Integer Value 1 : success 0 : fail
     */
    @Override
    public Object run() {
        SoapObject rpc = new SoapObject(SCWebService.SC_NAME_SPACE, SCWebService.SC_METHOD_LOGOUT);

        rpc.addProperty("User_dm", mUserDM);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(SCWebService.getEndPoint(mContext));
        try {
            transport.call(SCWebService.SC_NAME_SPACE + SCWebService.SC_METHOD_LOGOUT, envelope);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.logE("LogoutTask: " + e);
            return null;
        }
        SoapObject object = (SoapObject)envelope.bodyIn;
        int result = Integer.parseInt(object.getProperty(0).toString());
        if (result == 1) {
            CycleDownloadTaskManager.getInstance(mContext).stop();
            GeneralTaskEngine.getInstance().clearAllTasks();
        }
        return result;
    }

}
