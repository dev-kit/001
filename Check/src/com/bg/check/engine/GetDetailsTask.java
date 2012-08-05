
package com.bg.check.engine;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bg.check.datatype.TaskContent;
import com.bg.check.webservice.SCWebService;

public class GetDetailsTask extends BaseTask {

    private String mUserDM;

    private String mContentID;

    private int mTaskLX;

    public GetDetailsTask(String userDM, String contentID, int taskLX) {
        mUserDM = userDM;
        mContentID = contentID;
        mTaskLX = taskLX;
    }

    /**
     * Return Login status with DataTable instance
     */
    @Override
    public Object run() {
        SoapObject rpc = new SoapObject(SCWebService.SC_NAME_SPACE,
                SCWebService.SC_METHOD_GETDETAILS);

        rpc.addProperty("User_dm", mUserDM);
        rpc.addProperty("Task_contentid", mContentID);
        rpc.addProperty("Task_lx", mTaskLX);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(SCWebService.SC_END_POINT);
        try {
            transport.call(SCWebService.SC_NAME_SPACE + SCWebService.SC_METHOD_GETDETAILS,
                    envelope);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SoapObject object = (SoapObject)envelope.bodyIn;
        TaskContent taskContent = new TaskContent(object);
        return taskContent;
    }

}
