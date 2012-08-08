
package com.bg.check;

import android.app.Application;

import com.bg.check.database.Database;
import com.bg.check.datatype.Report;
import com.bg.check.datatype.User;
import com.bg.check.engine.CycleDownloadTaskManager;
import com.bg.check.engine.GetDetailsTask;
import com.bg.check.engine.GetServerTimeTask;
import com.bg.check.engine.GetTasksTask;
import com.bg.check.engine.GetUserInfoTask;
import com.bg.check.engine.LoginTask;
import com.bg.check.engine.LogoutTask;
import com.bg.check.engine.ReplyTasksTask;
import com.bg.check.engine.ReportToBySingleTask;
import com.bg.check.engine.SpeechEngine;
import com.bg.check.engine.GeneralTaskEngine;

public class Welcome extends Application {

    private User mUser = new User();

    @Override
    public void onCreate() {
        super.onCreate();
        Database.init(getApplicationContext());
        sayHello();

        mUser.mUserDM = "mad";
        mUser.mUserName = "马爱东";
        mUser.mUserZMLM = "SXT";
        // For test
        // TaskEngine.getInstance().appendTask(new GetUserInfoTask("mad"));
        // TaskEngine.getInstance().appendTask(new LoginTask("mad", "1", ""));
        CycleDownloadTaskManager.getInstance().run("mad", "马爱东", "SXT");
        // TaskEngine.getInstance().appendTask(new GetTasksTask("mad", "马爱东",
        // "SXT"));
        // TaskEngine.getInstance().appendTask(new GetDetailsTask("mad", 499349,
        // 1, 0));
        // TaskEngine.getInstance().appendTask(new ReplyTasksTask("mad", new
        // String[]{"262"}));
        Report r = new Report();
        r.mReport_contentid = "499349";
        r.mMessage_id = "268";
        r.mReport_czbz = "SXT";
        r.mReport_lx = "1";
        r.mTask_id = "247";
        // TaskEngine.getInstance().appendTask(new ReportToBySingleTask("mad",
        // r));
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
}
