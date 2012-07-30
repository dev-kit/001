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
import com.bg.check.database.DatabaseUtils;
import com.bg.check.datatype.User;

public class LoginActivity extends Activity {

    private static final int DIALOG_QUERY_PROGRESS = 1;
    private static final int DIALOG_USER_NOT_FOUND = 2;

    private EditText mEditUsercode;
    private String mUsercode;
    private EditText mEditPassword;
    private EditText mEditRole;
    private EditText mEditName;
    private TextView mButtonLogin;
    private TextView mButtonExit;
    private TextView mButtonSetting;
    private Resources mResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.login_activity);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.login_activity_title);
        mResources = getResources();
        initUi();
    }

    private void initUi() {
        mEditUsercode = (EditText) findViewById(R.id.edit_user_code);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mEditRole = (EditText) findViewById(R.id.edit_role);
        mEditName = (EditText) findViewById(R.id.edit_name);
        mButtonLogin = (TextView) findViewById(R.id.button_login);
        mButtonExit = (TextView) findViewById(R.id.button_exit);
        mButtonSetting = (TextView) findViewById(R.id.button_setting);

        mEditUsercode.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    final String usercode = mEditUsercode.getText().toString().trim();
                    //############ For test
                    if ("test".equals(usercode) || (usercode != null && usercode.length() == 1)) {
                        mEditUsercode.postDelayed(new Runnable() {
                            public void run() {
                                new UserInformationLoader().testPass();
                            }
                        }, 1000);
                        showDialog(DIALOG_QUERY_PROGRESS);
                        return;
                    }
                    //#############
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

        mButtonLogin.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                login();
            }
        });

        mButtonExit.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        mButtonSetting.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                gotoSetting();
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_QUERY_PROGRESS:
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(mResources.getString(R.string.message_query));
            return dialog;
        case DIALOG_USER_NOT_FOUND:
            AlertDialog.Builder builder = new Builder(this);
            builder.setMessage(R.string.error_message_no_user_found)
            .setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            return builder.create();
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
            return DatabaseUtils.getUser(usercode[0]);
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
            final User user = new User();
            user.mCode = "test";
            user.mName = "���";
            user.mRole = "������";
            onPostExecute(user);
        }
    }

    private void clearUserInformation() {
        mEditName.setText(null);
        mEditRole.setText(null);
        mButtonLogin.setEnabled(false);
    }

    private void setUserInformation(User user) {
        mEditName.setText(user.mName);
        mEditRole.setText(user.mRole);
        mButtonLogin.setEnabled(true);
    }

    private void gotoSetting() {
        final Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
        startActivity(intent);
    }

    private void login() {
        final Intent intent = new Intent(LoginActivity.this, CheckerActivity.class);
        startActivity(intent);
        finish();
    }
}
