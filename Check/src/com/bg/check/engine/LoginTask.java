
package com.bg.check.engine;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;

import com.bg.check.engine.utils.LogUtils;
import com.bg.check.webservice.SCWebService;

public class LoginTask extends BaseTask {
    private String mUserDM;

    private String mPassword;

    private String mMobile;

    private Context mContext;

    public LoginTask(Context context, String dm, String password, String mobile) {
        mUserDM = dm;
        mPassword = password;
        mMobile = mobile;
        mContext = context.getApplicationContext();
    }

    /**
     * Return Login status with Integer Value 1 : success 0 : fail
     */
    @Override
    public Object run() {
        SoapObject rpc = new SoapObject(SCWebService.SC_NAME_SPACE, SCWebService.SC_METHOD_LOGIN);

        rpc.addProperty("User_dm", mUserDM);
        rpc.addProperty("User_password", mPassword);
        rpc.addProperty("mobile", mMobile);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(SCWebService.getEndPoint(mContext));
        try {
            transport.call(SCWebService.SC_NAME_SPACE + SCWebService.SC_METHOD_LOGIN, envelope);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.logE("LoginTask: " + e.toString());
            return null;
        }
        SoapObject object = (SoapObject)envelope.bodyIn;
        int result = Integer.parseInt(object.getProperty(0).toString());
        if (result == -6) {
            LogUtils.logE("User " + mUserDM +" re-login:" + result);
        }
        if (result == 1 || result == -6) {
            CycleDownloadTaskManager.getInstance(mContext).run(mUserDM, "", "");
        }
        return result;
    }

}
