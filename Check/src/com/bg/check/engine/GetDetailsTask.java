
package com.bg.check.engine;

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
        return new DataDetails();
    }

}
