
package com.bg.check.ui;

import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.CursorAdapter;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.bg.check.R;
import com.bg.check.Welcome;
import com.bg.check.database.Database;
import com.bg.check.database.DatabaseHandler;
import com.bg.check.database.DatabaseHandler.DatabaseObserver;
import com.bg.check.datatype.TaskContent;
import com.bg.check.datatype.User;
import com.bg.check.engine.GetDetailsTask;
import com.bg.check.engine.SpeechEngine;
import com.bg.check.engine.utils.LogUtils;
import com.bg.check.engine.utils.TaskHelper;

public class SelectReportActivity extends ListActivity implements DatabaseObserver,
        OnCheckedChangeListener, OnClickListener {
    private static final int DIALOG_LOAD_TASK_PROGRESS = 1;

    private static final int DIALOG_NO_TASK_FOUND = 2;

    private AsyncQueryHandler mQueryHandler;

    private CursorAdapter mCursorAdapter;

    private SpeechEngine mSpeechEngine;

    private RadioButton mRadioOrderPositive;

    private RadioButton mRadioOrderNegative;

    private int mContentID;

    private int mMessageID;

    private int mTaskID;

    private int mTaskLX;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.select_report_activity);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.select_report_activity_title);

        // setFullscreen();
        initListAdapter();

        mRadioOrderPositive = ((RadioButton)findViewById(R.id.order));
        mRadioOrderPositive.setOnCheckedChangeListener(this);
        mRadioOrderNegative = ((RadioButton)findViewById(R.id.reverse_order));
        mRadioOrderNegative.setOnCheckedChangeListener(this);
        findViewById(R.id.start).setOnClickListener(this);

        mSpeechEngine = SpeechEngine.getInstance(getApplicationContext());
        // kick off a query
        mContentID = getIntent().getIntExtra("ContentID", -1);
        mMessageID = getIntent().getIntExtra("MessageID", -1);
        mTaskLX = getIntent().getIntExtra("TaskLX", -1);
        mTaskID = getIntent().getIntExtra("TaskID", -1);
        showDialog(DIALOG_LOAD_TASK_PROGRESS);
    }

    private void initListAdapter() {
        mCursorAdapter = new CursorAdapter(this, null) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View v = inflater.inflate(R.layout.select_report_item, parent, false);
                return v;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView sw = (TextView)view.findViewById(R.id.sw);
                TextView ch = (TextView)view.findViewById(R.id.ch);
                TextView js = (TextView)view.findViewById(R.id.js);
                TextView time = (TextView)view.findViewById(R.id.time);
                sw.setText(cursor.getString(cursor.getColumnIndex(Database.TASK_CONTENT_SWH)));
                ch.setText(cursor.getString(cursor.getColumnIndex(Database.TASK_CONTENT_CH)));
                js.setText(cursor.getString(cursor.getColumnIndex(Database.TASK_CONTENT_JSL)));
                time.setText(cursor.getString(cursor.getColumnIndex(Database.TASK_CONTENT_HJ_ZSJ)));
            }
        };
        setListAdapter(mCursorAdapter);
    }

    public void setFullscreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean click) {
        switch (button.getId()) {
            case R.id.order:
                if (click) {
                    ((RadioButton)findViewById(R.id.reverse_order)).setChecked(false);
                    speakPositiveOrder();
                }
                break;
            case R.id.reverse_order:
                if (click) {
                    ((RadioButton)findViewById(R.id.order)).setChecked(false);
                    speakNegativeOrder();
                }
                break;
            default:
                LogUtils.logE("SelectReportActivity, onCheckedChanged");
        }

    }

    private void startWork() {
        Cursor c = mCursorAdapter.getCursor();
        if (c == null) {
            LogUtils.logE("SelectReportActivity, onClick");
            return;
        }
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("ContentID", mContentID);
        intent.putExtra("MessageID", mMessageID);
        intent.putExtra("TaskID", mTaskID);

        if (((RadioButton)findViewById(R.id.order)).isChecked()) {
            intent.putExtra(ReportActivity.ORDER, 1);
        } else if (((RadioButton)findViewById(R.id.reverse_order)).isChecked()) {
            intent.putExtra(ReportActivity.ORDER, 2);
        } else {
            intent.putExtra(ReportActivity.ORDER, 3);
            intent.putExtra(Database.TASK_CONTENT_SWH, c.getPosition());
        }
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start) {
            startWork();
        }
    }

    private class AsyncQueryReportTask extends AsyncTask<Integer, Void, Cursor> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Cursor doInBackground(Integer... contentID) {
            User user = ((Welcome)getApplication()).getCurrentUser();
            String where = null;
            if (contentID != null && contentID[0] >= 0) {
                where = Database.TASK_CONTENT_CONTENT_ID + "=" + contentID[0] + " and "
                        + Database.TASK_CONTENT_STATUS + "=" + Database.TASK_STATUS_DEFAULT
                        + " and " + Database.TASK_CONTENT_USERDM + "='" + user.mUserDM + "'";
            }

            Cursor c = DatabaseHandler.query(Database.TABLE_SC_TASK_CONTENT, null, where, null,
                    null, null, null);
            if (c.getCount() == 0) {
                GetDetailsTask downloadTask = new GetDetailsTask(SelectReportActivity.this,
                        user.mUserDM, mContentID, mTaskLX, mMessageID);
                @SuppressWarnings("unchecked")
                List<TaskContent> taskContents = (List<TaskContent>)downloadTask.run();
                if (taskContents != null && taskContents.size() > 0) {
                    c.close();
                    c = DatabaseHandler.query(Database.TABLE_SC_TASK_CONTENT, null, where, null,
                            null, null, null);
                }
            }
            if (c.getCount() == 0) {
                // Update task to complete status that don't has available task
                // content
                // ContentValues values = new ContentValues();
                // values.put(Database.TASK_STATUS,
                // Database.TASK_STATUS_TO_REPORT);
                // where = Database.TASK_MESSAGEID + "=" + mMessageID;
                // DatabaseHandler.update(Database.TABLE_SC_TASK, values, where,
                // null);
                TaskHelper.reportTask(SelectReportActivity.this, user, mTaskID, mContentID,
                        false);
            }
            return c;
        }

        @Override
        protected void onPostExecute(final Cursor cursor) {
            super.onPostExecute(cursor);
            findViewById(R.id.start).setClickable(cursor.getCount() > 0);
            mCursorAdapter.changeCursor(cursor);
            initSpinner(cursor);
            if (!isFinishing()) {
                dismissDialog(DIALOG_LOAD_TASK_PROGRESS);
                if (cursor.getCount() == 0) {
                    showDialog(DIALOG_NO_TASK_FOUND);
                }
            }
        }

        private void initSpinner(final Cursor cursor) {
            Spinner s = (Spinner)findViewById(R.id.spinner);
            // display = (TextView)findViewById(R.id.display);
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(SelectReportActivity.this,
                    R.layout.spinner, cursor, new String[] {
                        Database.TASK_CONTENT_SWH
                    }, new int[] {
                        android.R.id.text1
                    });
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            s.setAdapter(adapter);
            s.setOnItemSelectedListener(new OnItemSelectedListener() {
                public void onItemSelected(AdapterView<?> adapter, View v, int pos, long id) {
                    cursor.moveToPosition(pos);
                    if (pos != 0) {
                        ((RadioButton)findViewById(R.id.order)).setChecked(false);
                    }
                    ((RadioButton)findViewById(R.id.reverse_order)).setChecked(false);
                }

                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
    }

    @Override
    public void onUpdate() {
        new AsyncQueryReportTask().execute(mContentID);
    }

    @Override
    protected void onResume() {
        DatabaseHandler.addDatabaseObserver(this);
        new AsyncQueryReportTask().execute(mContentID);
        super.onResume();
    }

    @Override
    protected void onPause() {
        DatabaseHandler.removeDatabaseObserver(this);
        super.onPause();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case CheckerKeyEvent.KEYCODE_DPAD_DOWN:
            case CheckerKeyEvent.KEYCODE_VOLUME_DOWN:
                // Do nothing;
                return true;
            case CheckerKeyEvent.KEYCODE_DPAD_UP:
            case CheckerKeyEvent.KEYCODE_VOLUME_UP:
                // Do nothing;
                return true;
            case CheckerKeyEvent.KEYCODE_OK:
                // Do nothing;
                return true;
            default:
        }

        return super.onKeyUp(keyCode, event);
    }

    private void speakNegativeOrder() {
        mSpeechEngine.stopSpeak();
        mSpeechEngine.speak(getResources().getString(R.string.speech_negative));
    }

    private void speakPositiveOrder() {
        mSpeechEngine.stopSpeak();
        mSpeechEngine.speak(getResources().getString(R.string.speech_positive));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case CheckerKeyEvent.KEYCODE_DPAD_DOWN:
            case CheckerKeyEvent.KEYCODE_VOLUME_DOWN:
                speakNegativeOrder();
                mRadioOrderNegative.setChecked(true);
                mRadioOrderPositive.setChecked(false);
                return true;
            case CheckerKeyEvent.KEYCODE_DPAD_UP:
            case CheckerKeyEvent.KEYCODE_VOLUME_UP:
                speakPositiveOrder();
                mRadioOrderNegative.setChecked(false);
                mRadioOrderPositive.setChecked(true);
                return true;
            case CheckerKeyEvent.KEYCODE_OK:
                startWork();
                return true;
            default:
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_LOAD_TASK_PROGRESS:
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage(getString(R.string.message_load_task));
                dialog.setCancelable(false);
                return dialog;
            case DIALOG_NO_TASK_FOUND: {
                AlertDialog.Builder builder = new Builder(this);
                builder.setMessage(R.string.message_no_task_found).setNegativeButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                return builder.create();
            }
        }
        return null;
    }
}
