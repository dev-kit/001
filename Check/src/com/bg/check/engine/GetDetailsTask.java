
package com.bg.check.engine;

public class GetDetailsTask extends BaseTask {

    /**
     * Return Login status with DataTable instance
     */
    @Override
    public Object run() {
        return new DataDetails();
    }

}
