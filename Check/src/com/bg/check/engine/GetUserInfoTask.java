
package com.bg.check.engine;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;

import com.bg.check.datatype.User;
import com.bg.check.engine.utils.LogUtils;
import com.bg.check.webservice.SCWebService;

/**
 * Task to get User Information.
 */
public class GetUserInfoTask extends BaseTask {
    private String mUserDM;

    private Context mContext;

    public GetUserInfoTask(Context context, String dm) {
        mUserDM = dm;
        mContext = context.getApplicationContext();
    }

    @Override
    public Object run() {

        SoapObject rpc = new SoapObject(SCWebService.SC_NAME_SPACE,
                SCWebService.SC_METHOD_GET_USER_INFO);

        rpc.addProperty("User_dm", mUserDM);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(SCWebService.getEndPoint(mContext));
        try {
            transport.call(SCWebService.SC_NAME_SPACE + SCWebService.SC_METHOD_GET_USER_INFO,
                    envelope);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.logE("GetUserInfoTask: " + e);
            return null;
        }

        SoapObject result;
        User user = null;
        try {
            if (envelope.getResponse() != null) {
                result = (SoapObject)envelope.getResponse();
                user = new User(result);
                user.mUserDM = mUserDM;
                user.updateDB();
            }
        } catch (SoapFault e) {
            e.printStackTrace();
            LogUtils.logE("GetUserInfoTask: " + e);
            return null;
        }
        return user;

    }

}
