
package com.bg.check;

import android.app.Application;

import com.bg.check.database.Database;
import com.bg.check.datatype.User;
import com.bg.check.engine.SpeechEngine;

/**
 *  Main entry to this application, where we will initiate some component like
 *  Database, test information.
 *
 */
public class Welcome extends Application {

    private User mUser = new User();

    @Override
    public void onCreate() {
        super.onCreate();
        Database.init(getApplicationContext());
        sayHello();

        // For test
        // TaskEngine.getInstance().appendTask(new GetUserInfoTask("mad"));
        // TaskEngine.getInstance().appendTask(new LoginTask("mad", "1", ""));
        // CycleDownloadTaskManager.getInstance().run("mad", "马爱东", "SXT");
        // TaskEngine.getInstance().appendTask(new GetTasksTask("mad", "马爱东",
        // "SXT"));
        // TaskEngine.getInstance().appendTask(new GetDetailsTask("mad", 499349,
        // 1, 0));
        // TaskEngine.getInstance().appendTask(new ReplyTasksTask("mad", new
        // String[]{"262"}));
//        Report r = new Report();
//        r.mMessage_id = "268";
//        r.mTask_id = "247";
//        r.mReport_contentid = "499349";
//        r.mReport_zmlm = "SXT";
//        r.mReport_czbz = "2";
//        r.mReport_lx = "1";
//        r.mXXString1 = "5050043";
//        r.mXXString2 = "马爱东";
//        r.mXXDate1 = "01-12-2011 16:08:05";
//        r.mXXDate2 = "01-12-2011 16:10:05";
//        r.mReport_id = "12323";
//        r.mXXDate3 = "32131";
//        r.mXXString3 = "3213";
//        ReportTaskEngine.getInstance().appendTask(new ReportToBySingleTask(this, "mad", r));
        // TaskEngine.getInstance().appendTask(new LogoutTask("mad"));
        // TaskEngine.getInstance().appendTask(new GetServerTimeTask());
    }

    private final void sayHello() {
        SpeechEngine speaker = SpeechEngine.getInstance(getApplicationContext());
        final String welcome = getResources().getString(R.string.welcome_speech);
        speaker.speak(welcome);
    }

    public User getCurrentUser() {
        return mUser;
    }

    public void setCurrentUser(User user) {
        mUser = user;
    }
}
