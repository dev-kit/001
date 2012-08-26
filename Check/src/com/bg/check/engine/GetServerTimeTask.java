
package com.bg.check.engine;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;

import com.bg.check.engine.utils.LogUtils;
import com.bg.check.webservice.SCWebService;

/**
 * Task to get server time.
 *
 */
public class GetServerTimeTask extends BaseTask {

    private Context mContext;

    public GetServerTimeTask(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * Return Login status with String. Format: 2012-05-23 14:16:48
     */
    @Override
    public Object run() {
        SoapObject rpc = new SoapObject(SCWebService.SC_NAME_SPACE,
                SCWebService.SC_METHOD_GET_SERVER_TIME);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(SCWebService.getEndPoint(mContext));
        try {
            transport.call(SCWebService.SC_NAME_SPACE + SCWebService.SC_METHOD_GET_SERVER_TIME,
                    envelope);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.logE("GetServerTimeTask: " + e);
            return null;
        }
        SoapObject object = (SoapObject)envelope.bodyIn;
        return object.getProperty(0).toString();
    }
}
