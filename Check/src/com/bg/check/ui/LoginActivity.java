
package com.bg.check.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.bg.check.R;
import com.bg.check.Welcome;
import com.bg.check.database.DatabaseHandler;
import com.bg.check.datatype.User;
import com.bg.check.engine.CycleDownloadTaskManager;
import com.bg.check.engine.GetUserInfoTask;
import com.bg.check.engine.LoginTask;
import com.bg.check.engine.SpeechEngine;

public class LoginActivity extends Activity {

    private static final int DIALOG_QUERY_PROGRESS = 1;

    private static final int DIALOG_USER_NOT_FOUND = 2;

    private static final int DIALOG_LOGIN_PROGRESS = 3;

    private EditText mEditUsercode;

    private String mUsercode;

    private EditText mEditPassword;

    private EditText mEditRole;

    private EditText mEditName;

    private TextView mLogin;

    private TextView mExit;

    private TextView mSetting;

    private Resources mResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.login_activity);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.login_activity_title);
        mResources = getResources();
        initUi();
    }

    private void initUi() {
        mEditUsercode = (EditText)findViewById(R.id.edit_user_code);
        mEditPassword = (EditText)findViewById(R.id.edit_password);
        mEditRole = (EditText)findViewById(R.id.edit_role);
        mEditName = (EditText)findViewById(R.id.edit_name);
        mLogin = (TextView)findViewById(R.id.login);
        mExit = (TextView)findViewById(R.id.exit);
        mSetting = (TextView)findViewById(R.id.setting);

        mEditUsercode.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    final String usercode = mEditUsercode.getText().toString().trim();
                    // ############ For test
                    if ("test".equals(usercode) || (usercode != null && usercode.length() == 1)) {
                        mEditUsercode.postDelayed(new Runnable() {
                            public void run() {
                                new UserInformationLoader().testPass();
                            }
                        }, 1000);
                        showDialog(DIALOG_QUERY_PROGRESS);
                        return;
                    }
                    // #############
                    if (!TextUtils.isEmpty(usercode)) {
                        if (!usercode.equals(mUsercode)) {
                            mUsercode = usercode;
                            loadUserInformation(usercode);
                        }
                    } else {
                        clearUserInformation();
                    }
                }
            }
        });

        mLogin.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });

        mExit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        mSetting.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                gotoSetting();
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_QUERY_PROGRESS: {
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage(mResources.getString(R.string.message_query));
                dialog.setCancelable(false);
                return dialog;
            }
            case DIALOG_USER_NOT_FOUND: {
                AlertDialog.Builder builder = new Builder(this);
                builder.setMessage(R.string.error_message_no_user_found).setNegativeButton(
                        R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                return builder.create();
            }
            case DIALOG_LOGIN_PROGRESS: {
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage(mResources.getString(R.string.message_login));
                dialog.setCancelable(false);
                return dialog;
            }
        }

        return super.onCreateDialog(id);
    }

    private void loadUserInformation(String usercode) {
        new UserInformationLoader().execute(usercode);
    }

    private class UserInformationLoader extends AsyncTask<String, Void, User> {

        @Override
        protected void onPreExecute() {
            showDialog(DIALOG_QUERY_PROGRESS);
        }

        @Override
        protected User doInBackground(String... usercode) {
            // TODO: It' bad to new task like this
            return (User)new GetUserInfoTask(usercode[0]).run();
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                setUserInformation(user);
            } else {
                clearUserInformation();
                mEditUsercode.requestFocus();
                mEditUsercode.selectAll();
                showDialog(DIALOG_USER_NOT_FOUND);
            }

            dismissDialog(DIALOG_QUERY_PROGRESS);
        }

        public void testPass() {
            User user = ((Welcome)getApplication()).getCurrentUser();
            user.mUserDM = "test";
            user.mUserName = "李白";
            user.mUserRole = "测试者";

            onPostExecute(user);
        }
    }

    private class LoginLoader extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            showDialog(DIALOG_LOGIN_PROGRESS);
        }

        @Override
        protected Integer doInBackground(Void... para) {
            User user = ((Welcome)getApplication()).getCurrentUser();
            user.mPassword = mEditPassword.getText().toString();
            return (Integer)new LoginTask(user.mUserDM, user.mPassword, user.mUserMobile).run();
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1 || result == -6) {
                final Intent intent = new Intent(LoginActivity.this, CheckerActivity.class);
                startActivity(intent);
                finish();
            } else {
                clearUserInformation();
                mEditUsercode.requestFocus();
                mEditUsercode.selectAll();
                showDialog(DIALOG_USER_NOT_FOUND);
            }

            if (!LoginActivity.this.isFinishing()) {
                dismissDialog(DIALOG_LOGIN_PROGRESS);
            }
        }
    }

    private void clearUserInformation() {
        mEditName.setText(null);
        mEditRole.setText(null);
        mLogin.setEnabled(false);
    }

    private void setUserInformation(User user) {
        mEditName.setText(user.mUserName);
        mEditRole.setText(user.mUserRole);
        mLogin.setEnabled(true);
    }

    private void gotoSetting() {
        final Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    private void login() {

        if (mEditRole.getText().toString().trim().equals("测试者")) {
            CycleDownloadTaskManager.getInstance().run("mad", "马爱东", "SXT");
            final Intent intent = new Intent(LoginActivity.this, CheckerActivity.class);
            startActivity(intent);
            finish();
        } else {
            new LoginLoader().execute();
        }
        // TODO do login action
    }
}
