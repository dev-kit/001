
package com.bg.check.engine;

import com.bg.check.datatype.DataTable;

public class GetTasksTask extends BaseTask {
    private String mUserDM;

    private String mUserName;

    private String mUserZMLM;

    public GetTasksTask(String userDM, String userName, String userZMLM) {
        mUserDM = userDM;
        mUserName = userName;
        mUserZMLM = userZMLM;
    }

    /**
     * Return Login status with DataTable instance
     */
    @Override
    public Object run() {
        return new DataTable();
    }

}
