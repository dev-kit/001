package com.bg.check.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;

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
    private Button mButtonLogin;
    private Button mButtonExit;
    private Button mButtonSetting;
    private Resources mResources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mResources = getResources();
        initUi();
    }

    private void initUi() {
        mEditUsercode = (EditText) findViewById(R.id.edit_user_code);
        mEditPassword = (EditText) findViewById(R.id.edit_password);
        mEditRole = (EditText) findViewById(R.id.edit_role);
        mEditName = (EditText) findViewById(R.id.edit_name);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mButtonExit = (Button) findViewById(R.id.button_exit);
        mButtonSetting = (Button) findViewById(R.id.button_setting);

        mEditUsercode.setOnFocusChangeListener(new OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    final String usercode = mEditUsercode.getText().toString().trim();
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
                // TODO: Perform login
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
    }

    private void clearUserInformation() {
        mEditName.setText(null);
        mEditRole.setText(null);
        mButtonLogin.setEnabled(false);
        mButtonExit.setEnabled(false);
        mButtonSetting.setEnabled(false);
    }

    private void setUserInformation(User user) {
        mEditName.setText(user.mName);
        mEditRole.setText(user.mCode);
        mButtonLogin.setEnabled(true);
        mButtonExit.setEnabled(true);
        mButtonSetting.setEnabled(true);
    }

    private void gotoSetting() {
        final Intent intent = new Intent(LoginActivity.this, SettingActivity.class);
        startActivity(intent);
    }
}
