
package com.bg.check.engine;

import com.bg.check.datatype.DataTable;

public class GetTasksTask extends BaseTask {

    /**
     * Return Login status with DataTable instance
     */
    @Override
    public Object run() {
        return new DataTable();
    }

}
