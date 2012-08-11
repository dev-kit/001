
package com.bg.check.engine;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.bg.check.datatype.Report;
import com.bg.check.engine.utils.LogUtils;
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
//        SoapObject soap = new SoapObject(SCWebService.SC_NAME_SPACE,
//                SCWebService.SC_METHOD_REPORT_TO_BY_SINGLE);
//        soap.addProperty("Report_id", mReport.mReport_id);
//        soap.addProperty("Message_id", mReport.mMessage_id);
//        soap.addProperty("Task_id", mReport.mTask_id);
//        soap.addProperty("Report_contentid", mReport.mReport_contentid);
//        soap.addProperty("Report_lx", mReport.mReport_lx);
//        soap.addProperty("Report_czbz", mReport.mReport_czbz);
//        soap.addProperty("Report_zmlm", mReport.mReport_zmlm);
//        soap.addProperty("XXString1", mReport.mXXString1);
//        soap.addProperty("XXString2", mReport.mXXString2);
//        soap.addProperty("XXString3", mReport.mXXString3);
//        soap.addProperty("XXDate1", mReport.mXXDate1);
//        soap.addProperty("XXDate2", mReport.mXXDate2);
//        soap.addProperty("XXDate3", mReport.mXXDate3);
//
//        rpc.addProperty("report", soap);
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
            LogUtils.logE("ReportToBySingleTask: " + e);
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
            LogUtils.logE("ReportToBySingleTask: " + envelope.bodyIn);
        }
        return result;
    }

}
