
package com.bg.check;

import com.bg.check.database.Databasehelper;

import android.app.Application;

public class Welcome extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Databasehelper.init(getApplicationContext());
    }

}
