
package com.bg.check.engine;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bg.check.datatype.TaskContent;
import com.bg.check.datatype.TaskData;
import com.bg.check.engine.utils.LogUtils;
import com.bg.check.webservice.SCWebService;

public class GetDetailsTask extends BaseTask {

    private String mUserDM;

    private long mContentID;

    private int mTaskLX;

    private long mMessageID;

    public GetDetailsTask(String userDM, long contentID, int taskLX, long messageID) {
        mUserDM = userDM;
        mContentID = contentID;
        mTaskLX = taskLX;
        mMessageID = messageID;
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
            transport
                    .call(SCWebService.SC_NAME_SPACE + SCWebService.SC_METHOD_GETDETAILS, envelope);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.logE("GetDetailsTask: " + e);
            return null;
        }
        SoapObject object = (SoapObject)envelope.bodyIn;
        List<TaskContent> taskContents = new ArrayList<TaskContent>();
        TaskContent.parseTaskContent(object, mUserDM, mContentID, mMessageID, taskContents);
        if (taskContents.size() == 0) {
            LogUtils.logD("There is no taskcontent for ContentID" + mContentID + " MessageID "
                    + mMessageID);
        }
        return taskContents;
    }

}
