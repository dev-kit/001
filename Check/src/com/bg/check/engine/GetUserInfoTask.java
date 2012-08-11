
package com.bg.check.engine;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bg.check.datatype.User;
import com.bg.check.engine.utils.LogUtils;
import com.bg.check.webservice.SCWebService;

public class GetUserInfoTask extends BaseTask {
    private String mUserDM;
    public GetUserInfoTask(String dm) {
        mUserDM = dm;
    }

    @Override
    public Object run() {

        SoapObject rpc = new SoapObject(SCWebService.SC_NAME_SPACE,
                SCWebService.SC_METHOD_GET_USER_INFO);

        // UserInfo u = new UserInfo();
        // u.setProperty(1, "ycf");
        rpc.addProperty("User_dm", mUserDM);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        // envelope.addMapping(SCWebService.SC_NAME_SPACE, "User", User.class);

        envelope.bodyOut = rpc;
        // �����Ƿ���õ���dotNet������WebService
        envelope.dotNet = true;
        // �ȼ���envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(SCWebService.SC_END_POINT);
        try {
            // ����WebService
            transport.call(SCWebService.SC_NAME_SPACE + SCWebService.SC_METHOD_GET_USER_INFO,
                    envelope);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.logE("GetUserInfoTask: " + e);
            return null;
        }

        // ��ȡ���ص�����
        // SoapObject object = (SoapObject)envelope.bodyIn;
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
            // TODO Auto-generated catch block
            e.printStackTrace();
            LogUtils.logE("GetUserInfoTask: " + e);
            return null;
        }
        return user;

    }

}
