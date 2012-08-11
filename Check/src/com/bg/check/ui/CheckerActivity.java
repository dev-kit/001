
package com.bg.check.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bg.check.R;
import com.bg.check.Welcome;
import com.bg.check.database.Database;
import com.bg.check.database.DatabaseHandler;
import com.bg.check.database.DatabaseHandler.DatabaseObserver;
import com.bg.check.datatype.TaskContent;
import com.bg.check.datatype.User;
import com.bg.check.engine.LogoutTask;
import com.bg.check.engine.SpeechEngine;
import com.bg.check.engine.SpeechEngine.SpeechListener;
import com.bg.check.engine.utils.LogUtils;
import com.bg.check.engine.utils.TaskHelper;

public class CheckerActivity extends Activity implements DatabaseObserver, OnClickListener,
        SpeechListener {

    private static final int SPEECH_COUNT = 5;

    private static final int DIALOG_LOGOUT_PROGRESS = 1;

    private ListView mList;

    private CursorAdapter mAdapter;

    private LayoutInflater mInflater;

    private TextView mStart;

    private TextView mFeedback;

    private TextView mExit;

    private TextView mVoice;

    private int mCurrentIndex;

    private String[] mHeaders;

    private String mReportIndex;

    private int mContentId = -1;

    private int mMessageId = -1;

    private int mTaskLX = -1;

    private Resources mResources;

    private SpeechEngine mSpeechEngine;

    private String mVoiceString;

    private String mVoiceStopString;

    private View mSelectedItemView;

    private boolean mStopSeriesSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initString();
        initUi();
        initListAdapter();
    }

    private boolean mIsOnRestart;

    @Override
    protected void onRestart() {
        mIsOnRestart = true;
        super.onRestart();
    }

    @Override
    protected void onStart() {
        DatabaseHandler.addDatabaseObserver(this);
        mSpeechEngine.registerSpeechListener(this);
        if (!mIsOnRestart) {
            startSeriesSpeech();
        }
        super.onStart();
    }

    @Override
    protected void onPause() {
        stopSpeech();
        super.onPause();
    }

    @Override
    protected void onStop() {
        DatabaseHandler.removeDatabaseObserver(this);
        mSpeechEngine.unregisterSpeechListener();
        super.onStop();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.checker_activity);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.checker_activity_title);
        mInflater = LayoutInflater.from(this);
        mResources = getResources();
        mSpeechEngine = SpeechEngine.getInstance(getApplicationContext());
    }

    private void initString() {
        mHeaders = new String[4];
        mHeaders[0] = mResources.getString(R.string.train_order);
        mHeaders[1] = mResources.getString(R.string.track);
        mHeaders[2] = mResources.getString(R.string.position);
        mHeaders[3] = mResources.getString(R.string.notification);
        mReportIndex = mResources.getString(R.string.report_index);

        mVoiceString = mResources.getString(R.string.voice);
        mVoiceStopString = mResources.getString(R.string.voice_stop);
    }

    private void initUi() {
        mStart = (TextView)findViewById(R.id.start);
        mFeedback = (TextView)findViewById(R.id.feedback);
        mExit = (TextView)findViewById(R.id.exit);
        mVoice = (TextView)findViewById(R.id.voice);

        mStart.setOnClickListener(this);
        mFeedback.setOnClickListener(this);
        mExit.setOnClickListener(this);
        mVoice.setOnClickListener(this);
    }

    private void gotoLogin() {
        new LogoutLoader().execute();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case CheckerKeyEvent.KEYCODE_OK:
                // Do nothing;
                return true;
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
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case CheckerKeyEvent.KEYCODE_OK:
                startWork();
                return true;
            case CheckerKeyEvent.KEYCODE_REPLAY:
                startCurrentTaskSpeech();
                return true;
            case CheckerKeyEvent.KEYCODE_RETURN:
                gotoLogin();
                return true;
            case CheckerKeyEvent.KEYCODE_VOLUME_DOWN:
                if (mAdapter != null && mCurrentIndex < mAdapter.getCount() - 1) {
                    moveSelectionTo(++mCurrentIndex);
                    startCurrentTaskSpeech();
                }
                return true;
            case CheckerKeyEvent.KEYCODE_VOLUME_UP:
                if (mCurrentIndex > 0) {
                    moveSelectionTo(--mCurrentIndex);
                    startCurrentTaskSpeech();
                }
                return true;
            default:
        }

        return super.onKeyDown(keyCode, event);
    }

    private void gotoSelectReport() {
        if (mAdapter != null && mAdapter.getCount() > 0) {
            final Intent intent = new Intent(CheckerActivity.this, SelectReportActivity.class);
            if (mContentId == -1) {
                mContentId = Integer.valueOf(((String[])mAdapter.getItem(0))[0]);
            }

            if (mContentId == 0) {
                showVoiceToast(R.string.toast_no_content_id);
                return;
            }

            intent.putExtra("ContentID", mContentId);
            intent.putExtra("MessageID", mMessageId);
            intent.putExtra("TaskLX", mTaskLX);
            startActivity(intent);
        }
    }

    private void initListAdapter() {
        mList = (ListView)findViewById(R.id.list);
        mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> dapterView, View view, int position, long id) {
                highlightCurrentView(view);
                mCurrentIndex = position;
                mContentId = Integer.valueOf(((String[])mAdapter.getItem(position))[0]);
                mMessageId = Integer.valueOf(((String[])mAdapter.getItem(position))[5]);
                mTaskLX = Integer.valueOf(((String[])mAdapter.getItem(position))[6]);
                startCurrentTaskSpeech();
            }
        });

        mList.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                highlightCurrentView(view);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // Do nothing; 
            }
        });
        User user = ((Welcome)getApplication()).getCurrentUser();
        final Cursor cursor = DatabaseHandler.queryTask(user.mUserName);
        mAdapter = new ReportAdapter(this, cursor);
        mList.setAdapter(mAdapter);
    }

    private void highlightCurrentView(View view) {
        if (mSelectedItemView != null) {
            mSelectedItemView.setBackgroundResource(android.R.drawable.list_selector_background);
        }

        if (view != null) {
            view.setBackgroundResource(R.drawable.toolbar_bg);
        }

        mSelectedItemView = view;
    }

    private class ReportAdapter extends CursorAdapter {

        private final int mColumnIndexConetntId;

        private final int mColumnIndexOrder;

        private final int mColumnIndexTrack;

        private final int mColumnIndexPosition;

        private final int mColumnIndexNotification;

        private class ViewHolder {
            TextView mOrder;

            TextView mTrack;

            TextView mPosition;

            TextView mNotification;
        }

        public ReportAdapter(Context context, Cursor c) {
            super(context, c);
            mColumnIndexConetntId = c.getColumnIndexOrThrow(Database.TASK_CONTENTID);
            mColumnIndexOrder = c.getColumnIndexOrThrow(Database.TASK_CC);
            mColumnIndexTrack = c.getColumnIndexOrThrow(Database.TASK_GDM);
            mColumnIndexPosition = c.getColumnIndexOrThrow(Database.TASK_JCWZ);
            mColumnIndexNotification = c.getColumnIndexOrThrow(Database.TASK_JLSJ);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            view.setBackgroundResource(android.R.drawable.list_selector_background);
            ViewHolder holder = (ViewHolder)view.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.mOrder = (TextView)view.findViewById(R.id.train_order);
                holder.mTrack = (TextView)view.findViewById(R.id.track);
                holder.mPosition = (TextView)view.findViewById(R.id.position);
                holder.mNotification = (TextView)view.findViewById(R.id.notification);
            }

            holder.mOrder.setText(cursor.getString(mColumnIndexOrder));
            holder.mTrack.setText(cursor.getString(mColumnIndexTrack));
            holder.mPosition.setText(cursor.getString(mColumnIndexPosition));
            holder.mNotification.setText(cursor.getString(mColumnIndexNotification));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return mInflater.inflate(R.layout.checker_list_item, null, false);
        }

        @Override
        public Object getItem(int position) {
            Cursor cursor = getCursor();
            if (cursor != null && cursor.moveToPosition(position)) {
                String[] row = new String[7];
                row[0] = cursor.getString(mColumnIndexConetntId);
                row[1] = cursor.getString(mColumnIndexOrder);
                row[2] = cursor.getString(mColumnIndexTrack);
                row[3] = cursor.getString(mColumnIndexPosition);
                row[4] = cursor.getString(mColumnIndexNotification);
                row[5] = cursor.getString(cursor.getColumnIndex(Database.TASK_MESSAGEID));
                row[6] = cursor.getString(cursor.getColumnIndex(Database.TASK_LX));
                return row;
            }
            return null;
        }
    }

    private void stopSpeech() {
        mStopSeriesSpeech = true;
        mSpeechEngine.stopSpeak();
        mVoice.setText(mVoiceString);
    }

    private void showVoiceToast(int resId) {
        final String prompt = mResources.getString(resId);
        Toast.makeText(getApplicationContext(), prompt, Toast.LENGTH_LONG).show();
        startSingleSpeech(prompt);
    }

    private void startSingleSpeech(String words) {
        stopSpeech();
        mSpeechEngine.speak(words);
    }

    private void startCurrentTaskSpeech() {
        stopSpeech();
        final String words = getCurrentTaskString();
        mSpeechEngine.speak(words);
        setStartSpeechUi();
    }

    private void startSeriesSpeech() {
        if (mAdapter != null && mAdapter.getCount() > 0) {
            stopSpeech();
            mStopSeriesSpeech = false;
            mSpeechEngine.speakSeries();
            setStartSpeechUi();
        }
    }

    private void setStartSpeechUi() {
        mVoice.setText(mVoiceStopString);
        moveSelectionTo(mCurrentIndex);
    }

    private void startWork() {
        if (mAdapter == null || mAdapter.getCount() < 1) {
            return;
        }

        stopSpeech();
        Cursor c = mAdapter.getCursor();
        String title = mStart.getText().toString();
        if (title.equals(getString(R.string.complete)) && c != null && c.moveToPosition(mCurrentIndex)) {
            TaskHelper.reportTasksForSingleTask(
                    ((Welcome)getApplication()).getCurrentUser(), new TaskContent(),
                    c.getLong(c.getColumnIndex(Database.COLUMN_ID)));
            mStart.setText(R.string.start);
            mStart.setCompoundDrawablesWithIntrinsicBounds(null,
                    mResources.getDrawable(R.drawable.ic_go), null, null);
            return;
        }

        int messageID = 0;
        if (c != null) {
            messageID = c.getInt(c.getColumnIndex(Database.TASK_MESSAGEID));
        }
        User user = ((Welcome)getApplication()).getCurrentUser();
        TaskHelper.replyTasks(user.mUserDM, messageID);

        gotoSelectReport();

        if (mContentId <= 0) {
            mStart.setText(R.string.complete);
            mStart.setCompoundDrawablesWithIntrinsicBounds(null,
                    mResources.getDrawable(R.drawable.ic_complete), null, null);
        }
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id) {
            case R.id.start:
                startWork();
                break;
            case R.id.feedback: {
                if (mAdapter == null || mAdapter.getCount() < 1) {
                    return;
                }
                Cursor c = mAdapter.getCursor();
                int messageID = 0;
                if (c != null) {
                    messageID = c.getInt(c.getColumnIndex(Database.TASK_MESSAGEID));
                }
                User user = ((Welcome)getApplication()).getCurrentUser();
                TaskHelper.replyTasks(user.mUserDM, messageID);
                break;
            }
            case R.id.exit:
                gotoLogin();
                break;
            case R.id.voice:
                if (isSpeechWorking()) {
                    stopSpeech();
                } else {
                    startSeriesSpeech();
                }
                break;
            default:
                LogUtils.logW("unknown id = " + id);
        }
    }

    private boolean isSpeechWorking() {
        if (mVoiceString != null && mVoice != null) {
            return !mVoiceString.equals(mVoice.getText().toString());
        }

        return false;
    }

    private void moveSelectionTo(int position) {
        mList.requestFocusFromTouch();
        mList.setSelectionFromTop(position, 200);
    }

    private String getCurrentTaskString() {
        if (mCurrentIndex < mAdapter.getCount()) {
            String[] row = (String[])mAdapter.getItem(mCurrentIndex);
            StringBuilder builder = new StringBuilder();
            builder.append(String.format(mReportIndex, mCurrentIndex + 1));
            builder.append(SpeechEngine.COMMA);
            for (int i = 1; i < SPEECH_COUNT; i++) {
                builder.append(mHeaders[i - 1]);
                builder.append(SpeechEngine.COMMA);
                builder.append("\"" + row[i] + "\"");
                builder.append(SpeechEngine.COMMA);
            }

            return builder.toString();
        }

        return null;
    }

    @Override
    public String onPrepareSpeech() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                moveSelectionTo(mCurrentIndex);
            }
        });
        return getCurrentTaskString();
    }

    @Override
    public synchronized boolean moveToNext() {
        if (!mStopSeriesSpeech && isSpeechWorking() && mCurrentIndex + 1 < mAdapter.getCount()) {
            mCurrentIndex++;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized void onSpeechComplete() {
        mStopSeriesSpeech = false;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mVoice != null && mVoice.getText().toString().equals(mVoiceStopString)
                        && !mSpeechEngine.isSpeaking()) {
                    if (mSpeechEngine != null) {
                        mSpeechEngine.stopSpeak();
                    }
                    mVoice.setText(mVoiceString);
                }
            }
        });
    }

    @Override
    public void onUpdate() {
        User user = ((Welcome)getApplication()).getCurrentUser();
        final Cursor cursor = DatabaseHandler.queryTask(user.mUserName);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Cursor oldCursor = mAdapter.getCursor();
                final int count = oldCursor != null ? oldCursor.getCount() : 0;

                if (mAdapter != null) {
                    mAdapter.changeCursor(cursor);
                    final int newCount = mAdapter.getCount();
                    if (newCount > 0 && count != newCount) {
                        startSeriesSpeech();
                    }
                }
            }
        });
    }

    private class LogoutLoader extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {
            showDialog(DIALOG_LOGOUT_PROGRESS);
        }

        @Override
        protected Integer doInBackground(Void... para) {
            User user = ((Welcome)getApplication()).getCurrentUser();
            // For test: begain
            if (user.mUserRole != null && user.mUserRole.equals("≤‚ ‘’ﬂ")) {
                return 1;
            }
            // For test: end

            return (Integer)new LogoutTask(user.mUserDM).run();
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                showVoiceToast(R.string.toast_logout_success);
                final Intent intent = new Intent(CheckerActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                showVoiceToast(R.string.toast_logout_fail);
            }

            if (!CheckerActivity.this.isFinishing()) {
                dismissDialog(DIALOG_LOGOUT_PROGRESS);
            }
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_LOGOUT_PROGRESS: {
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage(mResources.getString(R.string.message_logout));
                dialog.setCancelable(false);
                return dialog;
            }
        }

        return super.onCreateDialog(id);
    }
}
