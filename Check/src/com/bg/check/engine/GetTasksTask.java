
package com.bg.check.engine;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.Context;

import com.bg.check.datatype.TaskData;
import com.bg.check.engine.utils.LogUtils;
import com.bg.check.webservice.SCWebService;

/**
 * Task to download task.
 *
 */
public class GetTasksTask extends BaseTask {
    private String mUserDM;

    private String mUserName;

    private String mUserZMLM;

    private Context mContext;

    public GetTasksTask(Context context, String userDM, String userName, String userZMLM) {
        mUserDM = userDM;
        mUserName = userName;
        mUserZMLM = userZMLM;
        mContext = context.getApplicationContext();
    }

    /**
     * Return Login status with DataTable instance
     */
    @Override
    public Object run() {

        SoapObject rpc = new SoapObject(SCWebService.SC_NAME_SPACE,
                SCWebService.SC_METHOD_GET_TASKS);

        rpc.addProperty("User_dm", mUserDM);
        rpc.addProperty("User_name", mUserName);
        rpc.addProperty("User_zmlm", mUserZMLM);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(SCWebService.getEndPoint(mContext));
        try {
            transport.call(SCWebService.SC_NAME_SPACE + SCWebService.SC_METHOD_GET_TASKS, envelope);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.logE("GetTasksTask: " + e);
            return null;
        }
        SoapObject object = (SoapObject)envelope.bodyIn;
        List<TaskData> tasks = new ArrayList<TaskData>();
        TaskData.parseTask(object, tasks);
        if (tasks.size() == 0) {
            LogUtils.logD("GetTasksTask: there is no task in server");
        } else {
            String[] messageIDs = new String[tasks.size()];
            for (int index = 0; index < tasks.size(); index++) {
                messageIDs[index] = String.valueOf(tasks.get(index).mTaskMessageID);
            }
            ReplyTasksTask task = new ReplyTasksTask(mContext, mUserDM, messageIDs);
            ReportTaskEngine.getInstance().appendTask(task);
        }

        return tasks;
    }

}
