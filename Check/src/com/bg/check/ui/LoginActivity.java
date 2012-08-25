
package com.bg.check.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bg.check.R;
import com.bg.check.Welcome;
import com.bg.check.datatype.User;
import com.bg.check.engine.CycleDownloadTaskManager;
import com.bg.check.engine.GetUserInfoTask;
import com.bg.check.engine.LoginTask;
import com.bg.check.engine.SpeechEngine;

public class LoginActivity extends Activity {

    private static final int DIALOG_QUERY_PROGRESS = 1;

    private static final int DIALOG_USER_NOT_FOUND = 2;

    private static final int DIALOG_LOGIN_PROGRESS = 3;

    private static final int DIALOG_LOGIN_FAIL = 4;

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
        showLogoutVoiceToast();
        initUi();
    }

    private void showVoiceToast(int resId) {
        showVoiceToast(resId, null);
    }

    private void showVoiceToast(int resId, Integer append) {
        final String prompt = String.format(mResources.getString(resId), append);
        SpeechEngine.getInstance(this).speak(prompt);
        Toast.makeText(getApplicationContext(), prompt, Toast.LENGTH_LONG).show();
    }

    private void showLogoutVoiceToast() {
        if ("logout".equals(getIntent().getAction())) {
            showVoiceToast(R.string.toast_logout_success);
        }
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

                    if (!Utils.isNetworkAvailable(LoginActivity.this)) {
                        showVoiceToast(R.string.error_message_connect_fail);
                    } else if (!TextUtils.isEmpty(usercode)) {
                        mUsercode = usercode;
                        loadUserInformation(usercode);
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
    protected Dialog onCreateDialog(int id, Bundle bundle) {
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
            case DIALOG_LOGIN_FAIL: {
                AlertDialog.Builder builder = new Builder(this);
                int errorCode = bundle.getInt("error_code");
                builder.setMessage(getString(R.string.error_message_login_fail, errorCode)).setNegativeButton(
                        R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                return builder.create();
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
            return (User)new GetUserInfoTask(LoginActivity.this, usercode[0]).run();
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                setUserInformation(user);
            } else {
                clearUserInformation();
                mEditUsercode.requestFocus();
                mEditUsercode.selectAll();
//                showDialog(DIALOG_USER_NOT_FOUND);
                showVoiceToast(R.string.error_message_no_user_found);
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
            return (Integer)new LoginTask(LoginActivity.this, user.mUserDM, user.mPassword, user.mUserMobile).run();
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1 || result == -6) {
                final Intent intent = new Intent(LoginActivity.this, CheckerActivity.class);
                startActivity(intent);
                finish();
            } else {
                // clearUserInformation();
                // mEditUsercode.requestFocus();
                // mEditUsercode.selectAll();
                Bundle bundle = new Bundle();
                bundle.putInt("error_code", result);
                showVoiceToast(R.string.error_message_login_fail, result == null ? null : result);
//                showDialog(DIALOG_LOGIN_FAIL, bundle);
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
        User u = ((Welcome)getApplication()).getCurrentUser();
        u.mUserDM = user.mUserDM;
        u.mUserMobile = user.mUserMobile;
        u.mUserName = user.mUserName;
        u.mUserRole = user.mUserRole;
        u.mUserZMLM = user.mUserZMLM;
    }

    private void gotoSetting() {
        final Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    private void login() {
        if (!Utils.isNetworkAvailable(this)) {
            showVoiceToast(R.string.error_message_connect_fail);
        }

        if (mEditRole.getText().toString().trim().equals("测试者")) {
            CycleDownloadTaskManager.getInstance(LoginActivity.this).run("mad", "马爱东", "SXT");
            final Intent intent = new Intent(LoginActivity.this, CheckerActivity.class);
            startActivity(intent);
            finish();
        } else {
            new LoginLoader().execute();
        }
    }
}
