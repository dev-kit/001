
package com.bg.check.engine;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bg.check.datatype.Report;
import com.bg.check.webservice.SCWebService;

// If ReportToBySingleTask failed, it will report automatically
public class ReportToBySingleTask extends BaseTask {
    private String mUserDM;

    private Report mReport;

    public ReportToBySingleTask(String dm, Report report) {
        mUserDM = dm;
        mReport = report;
    }

    /**
     * Return ReplyTasks status with Integer Value 1 : success 0 : fail
     */
    @Override
    public Object run() {
        SoapObject rpc = new SoapObject(SCWebService.SC_NAME_SPACE,
                SCWebService.SC_METHOD_REPORT_TO_BY_SINGLE);

        rpc.addProperty("User_dm", mUserDM);
        rpc.addProperty("report", mReport);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        envelope.dotNet = true;
        envelope.setOutputSoapObject(rpc);

        HttpTransportSE transport = new HttpTransportSE(SCWebService.SC_END_POINT);
        try {
            transport.call(SCWebService.SC_NAME_SPACE + SCWebService.SC_METHOD_REPORT_TO_BY_SINGLE,
                    envelope);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        SoapObject object = (SoapObject)envelope.bodyIn;
        int result = Integer.parseInt(object.getProperty(0).toString());
        if (result != 1) {
            // Report it automatically if failed
            TaskEngine.getInstance().appendTask(this);
        }
        return result;
    }

}
