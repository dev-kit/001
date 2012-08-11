
package com.bg.check.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.bg.check.R;
import com.bg.check.Welcome;
import com.bg.check.database.Database;
import com.bg.check.database.DatabaseHandler;
import com.bg.check.database.DatabaseHandler.DatabaseObserver;
import com.bg.check.datatype.TaskContent;
import com.bg.check.engine.SpeechEngine;
import com.bg.check.engine.SpeechEngine.SpeechListener;
import com.bg.check.engine.utils.LogUtils;
import com.bg.check.engine.utils.TaskHelper;

public class ReportActivity extends Activity implements DatabaseObserver, OnClickListener,
        SpeechListener {

    public static final String ORDER = "order";

    private int mOrder = -1;

    private int mTranId = -1;

    private int mStartTranID;

    private Cursor mCursor;

    private TextView mTts;

    private String mLabelSw;

    private String mLabelCh;

    private String mLabelCz;

    private String mLabelFz;

    private String mLabelDz;

    private String mLabelPm;

    private String mLabelJsl;

    private String mLabelImportance;

    private String mLabelOperate;

    private String mEmpty;

    private String mStopTts;

    private String mSw;

    private String mCh;

    private String mCz;

    private String mFz;

    private String mDz;

    private String mPm;

    private String mJsl;

    private String mImportance;

    private String mOperate;

    private TaskContent mTaskContent = new TaskContent();

    private SpeechEngine mSpeechEngine;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.report_activity);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.report_activity_title);

        initFromIntent();

        initString();

        findViewById(R.id.up).setOnClickListener(this);
        findViewById(R.id.down).setOnClickListener(this);
        findViewById(R.id.ret).setOnClickListener(this);
        mTts = (TextView)findViewById(R.id.tts);
        mTts.setOnClickListener(this);
        mSpeechEngine = SpeechEngine.getInstance(getApplicationContext());
        // kick off a query
        int contentID = getIntent().getIntExtra("ContentID", -1);
        new AsyncQueryReportTask().execute(contentID);
    }

    private void initString() {
        final Resources res = getResources();
        mLabelSw = res.getString(R.string.sw);
        mLabelCh = res.getString(R.string.ch);
        mLabelCz = res.getString(R.string.cz);
        mLabelFz = res.getString(R.string.fz);
        mLabelDz = res.getString(R.string.dz);
        mLabelPm = res.getString(R.string.pm);
        mLabelJsl = res.getString(R.string.jsl);
        mLabelImportance = res.getString(R.string.zdnr);
        mLabelOperate = res.getString(R.string.sfcz);
        mEmpty = res.getString(R.string.empty);
        mStopTts = res.getString(R.string.stop_tts);
    }

    private void initFromIntent() {
        Intent intent = getIntent();
        mOrder = intent.getIntExtra(ORDER, -1);
        mTranId = intent.getIntExtra(Database.TASK_CONTENT_SWH, -1);
        mStartTranID = mTranId;
    }

    private class AsyncQueryReportTask extends AsyncTask<Integer, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Integer... contentID) {
            String where = Database.TASK_CONTENT_CONTENT_ID + "=" + contentID[0] + " and "
                    + Database.TASK_CONTENT_STATUS + "=" + Database.TASK_STATUS_DEFAULT;
            return DatabaseHandler.query(Database.TABLE_SC_TASK_CONTENT, null, where, null, null,
                    null, null);
        }

        @Override
        protected void onPostExecute(final Cursor cursor) {
            super.onPostExecute(cursor);
            mCursor = cursor;
            switch (mOrder) {
                case 1:
                    mTranId = 0;
                    break;
                case 2:
                    mTranId = cursor.getCount() - 1;
                    break;
                case 3:
                    break;
                default:
                    LogUtils.logE("ReportActivity, order is not correct!");
            }
            if (cursor.getCount() > mTranId) {
                cursor.moveToPosition(mTranId);
                render(cursor);
                return;
            }
            LogUtils.logE("ReportActivity.onPostExecute()");
        }
    }

    private void render(final Cursor cursor) {
        TextView sw = (TextView)ReportActivity.this.findViewById(R.id.sw);
        TextView ch = (TextView)ReportActivity.this.findViewById(R.id.ch);
        TextView cz = (TextView)ReportActivity.this.findViewById(R.id.cz);
        TextView fz = (TextView)ReportActivity.this.findViewById(R.id.fz);
        TextView dz = (TextView)ReportActivity.this.findViewById(R.id.dz);
        TextView pm = (TextView)ReportActivity.this.findViewById(R.id.pm);
        TextView jsl = (TextView)ReportActivity.this.findViewById(R.id.jsl);
        TextView operate = (TextView)ReportActivity.this.findViewById(R.id.operate);

        mSw = cursor.getString(cursor.getColumnIndex(Database.TASK_CONTENT_SWH));
        mCh = cursor.getString(cursor.getColumnIndex(Database.TASK_CONTENT_CH));
        mCz = cursor.getString(cursor.getColumnIndex(Database.TASK_CONTENT_CZ));
        mFz = cursor.getString(cursor.getColumnIndex(Database.TASK_CONTENT_FZM));
        mDz = cursor.getString(cursor.getColumnIndex(Database.TASK_CONTENT_DZM));
        mPm = cursor.getString(cursor.getColumnIndex(Database.TASK_CONTENT_PM));
        mJsl = cursor.getString(cursor.getColumnIndex(Database.TASK_CONTENT_JSL));
        // mImportance =
        // cursor.getString(cursor.getColumnIndex(Databasehelper.TASK_CONTENT));
        mOperate = cursor.getString(cursor.getColumnIndex(Database.TASK_CONTENT_SWH));
        sw.setText(mSw);
        ch.setText(mCh);
        cz.setText(mCz);
        fz.setText(mFz);
        dz.setText(mDz);
        pm.setText(mPm);
        jsl.setText(mJsl);
        // importance.setText(mImportance);
        operate.setText(mOperate);
        startSpeech();
    }

    private void handleUp() {
        TaskHelper.reportTasks(((Welcome)getApplication()).getCurrentUser(), mTaskContent,
                mCursor.getLong(mCursor.getColumnIndex(Database.COLUMN_ID)));
        switch (mOrder) {
            case 1:
                if (mTranId > 0) {
                    mTranId--;
                    mCursor.moveToPosition(mTranId);
                    render(mCursor);
                }
                break;
            case 2:
                if (mTranId < mCursor.getCount() - 1) {
                    mTranId++;
                    mCursor.moveToNext();
                    render(mCursor);
                }
                break;
            case 3:
                if (mTranId > mStartTranID) {
                    mTranId--;
                    mCursor.moveToPosition(mTranId);
                    render(mCursor);
                }
                break;
            default:
                LogUtils.logE("ReportActivity, order is not correct!");

        }
    }

    private void handleDown() {
        TaskHelper.reportTasks(((Welcome)getApplication()).getCurrentUser(), mTaskContent,
                mCursor.getLong(mCursor.getColumnIndex(Database.COLUMN_ID)));
        stopSpeech();
        switch (mOrder) {
            case 1:
            case 3:
                if (mTranId < mCursor.getCount() - 1) {
                    mTranId++;
                    mCursor.moveToNext();
                    render(mCursor);
                }
                break;
            case 2:
                if (mTranId > 0) {
                    mTranId--;
                    mCursor.moveToPosition(mTranId);
                    render(mCursor);
                }
                break;
            default:
                LogUtils.logE("ReportActivity, order is not correct!");

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.up:
                handleUp();
            case R.id.down:
                handleDown();
            case R.id.tts:
                if (mStopTts.equals(mTts.getText().toString())) {
                    stopSpeech();
                } else {
                    startSpeech();
                }
                break;
            case R.id.ret:
                goBack();
                break;
        }
    }

    private void goBack() {
        finish();
    }

    @Override
    protected void onPause() {
        stopSpeech();
        mSpeechEngine.unregisterSpeechListener();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mSpeechEngine.registerSpeechListener(this);
        super.onResume();
    }

    private void startSpeech() {
        mTts.setText(mStopTts);
        mSpeechEngine.speakSeries();
    }

    private void stopSpeech() {
        mSpeechEngine.stopSpeak();
        mTts.setText(R.string.tts);
    }

    @Override
    public String onPrepareSpeech() {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean hasOrder = preferences.getBoolean("checkbox_oreder", false);
        final boolean hasTrainId = preferences.getBoolean("checkbox_train_id", false);
        final boolean hasTrainType = preferences.getBoolean("checkbox_train_type", false);
        final boolean hasStationStart = preferences.getBoolean("checkbox_station_start", false);
        final boolean hasStationStop = preferences.getBoolean("checkbox_station_stop", false);
        final boolean hasProduct = preferences.getBoolean("checkbox_product", false);
        final boolean hasRecord = preferences.getBoolean("checkbox_record", false);
        final boolean hasImportance = preferences.getBoolean("checkbox_importance", false);

        final StringBuilder builder = new StringBuilder();

        if (hasOrder) {
            builder.append(mLabelSw);
            builder.append(SpeechEngine.COMMA);
            builder.append(TextUtils.isEmpty(mSw) ? mEmpty : mSw);
            builder.append(SpeechEngine.COMMA);
        }

        if (hasTrainId) {
            builder.append(mLabelCh);
            builder.append(SpeechEngine.COMMA);
            builder.append(TextUtils.isEmpty(mCh) ? mEmpty : mCh);
            builder.append(SpeechEngine.COMMA);
        }

        if (hasTrainType) {
            builder.append(mLabelCz);
            builder.append(SpeechEngine.COMMA);
            builder.append(TextUtils.isEmpty(mCz) ? mEmpty : mCz);
            builder.append(SpeechEngine.COMMA);
        }

        if (hasStationStart) {
            builder.append(mLabelFz);
            builder.append(SpeechEngine.COMMA);
            builder.append(TextUtils.isEmpty(mFz) ? mEmpty : mFz);
            builder.append(SpeechEngine.COMMA);
        }

        if (hasStationStop) {
            builder.append(mLabelDz);
            builder.append(SpeechEngine.COMMA);
            builder.append(TextUtils.isEmpty(mDz) ? mEmpty : mDz);
            builder.append(SpeechEngine.COMMA);
        }

        if (hasProduct) {
            builder.append(mLabelPm);
            builder.append(SpeechEngine.COMMA);
            builder.append(TextUtils.isEmpty(mPm) ? mEmpty : mPm);
            builder.append(SpeechEngine.COMMA);
        }

        if (hasRecord) {
            builder.append(mLabelJsl);
            builder.append(SpeechEngine.COMMA);
            builder.append(TextUtils.isEmpty(mJsl) ? mEmpty : mJsl);
            builder.append(SpeechEngine.COMMA);
        }

        if (hasImportance) {
            builder.append(mLabelImportance);
            builder.append(SpeechEngine.COMMA);
            builder.append(TextUtils.isEmpty(mImportance) ? mEmpty : mImportance);
            builder.append(SpeechEngine.COMMA);
        }

        builder.append(mLabelOperate);
        builder.append(SpeechEngine.COMMA);
        builder.append(TextUtils.isEmpty(mOperate) ? mEmpty : mOperate);

        return builder.toString();
    }

    @Override
    public boolean moveToNext() {
        return false;
    }

    @Override
    public void onSpeechComplete() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTts.setText(R.string.tts);
            }
        });
    }

    @Override
    public void onUpdate() {
        new AsyncQueryReportTask().execute();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case CheckerKeyEvent.KEYCODE_REPLAY:
            stopSpeech();
            startSpeech();
            return true;
        case CheckerKeyEvent.KEYCODE_VOLUME_DOWN:
            handleDown();
            return true;
        case CheckerKeyEvent.KEYCODE_VOLUME_UP:
            handleUp();
            return true;
        case CheckerKeyEvent.KEYCODE_RETURN:
            goBack();
            return true;
        default:
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case CheckerKeyEvent.KEYCODE_REPLAY:
            // Do nothing;
            return true;
        case CheckerKeyEvent.KEYCODE_VOLUME_DOWN:
            // Do nothing;
            return true;
        case CheckerKeyEvent.KEYCODE_VOLUME_UP:
            // Do nothing;
            return true;
        case CheckerKeyEvent.KEYCODE_RETURN:
            // Do nothing;
            return true;
        default:
        }

        return super.onKeyUp(keyCode, event);
    }
}
