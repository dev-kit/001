
package com.bg.check.ui;

import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import com.bg.check.datatype.User;
import com.bg.check.engine.CycleDownloadTaskManager;
import com.bg.check.engine.GeneralTaskEngine;
import com.bg.check.engine.LogoutTask;
import com.bg.check.engine.SpeechEngine;
import com.bg.check.engine.SpeechEngine.SpeechListener;
import com.bg.check.engine.utils.LogUtils;
import com.bg.check.engine.utils.TaskHelper;

/**
 * 外勤系统：货检作业页面。
 * 点击任意项会有语音播报。
 */
public class CheckerActivity extends Activity implements DatabaseObserver, OnClickListener,
        SpeechListener {

    private static final int SPEECH_COUNT = 5;

    private static final int DIALOG_LOGOUT_PROGRESS = 1;

    private ListView mList;

    private CursorAdapter mAdapter;

    private LayoutInflater mInflater;

    private TextView mStart;

    private TextView mComplete;

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

    private boolean mIsFirstStart = true;

    private int mTaskID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initString();
        initUi();
        initListAdapter();
    }

    @Override
    protected void onRestart() {
        mIsFirstStart = false;
        super.onRestart();
    }

    @Override
    protected void onStart() {
        User user = ((Welcome)getApplication()).getCurrentUser();
        Cursor cursor = DatabaseHandler.queryTask(user.mUserName);
        if (mAdapter != null) {
            mAdapter.changeCursor(cursor);
        }
        DatabaseHandler.addDatabaseObserver(this);
        mSpeechEngine.registerSpeechListener(this);
        if (mIsFirstStart) {
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
        mComplete = (TextView)findViewById(R.id.complete);
        mFeedback = (TextView)findViewById(R.id.feedback);
        mExit = (TextView)findViewById(R.id.exit);
        mVoice = (TextView)findViewById(R.id.voice);

        mComplete.setOnClickListener(this);
        mStart.setOnClickListener(this);
        mFeedback.setOnClickListener(this);
        mExit.setOnClickListener(this);
        mVoice.setOnClickListener(this);

        User user = ((Welcome)getApplication()).getCurrentUser();
        ((TextView)findViewById(R.id.username)).setText(user.mUserName);
    }

    private void gotoLogout() {
        if (!Utils.isNetworkAvailable(this)) {
            leave();
            CycleDownloadTaskManager.getInstance(this).stop();
            GeneralTaskEngine.getInstance().clearAllTasks();
            return;
        }
        new LogoutLoader().execute();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case CheckerKeyEvent.KEYCODE_OK:
            case CheckerKeyEvent.KEYCODE_REPLAY:
            case CheckerKeyEvent.KEYCODE_DPAD_DOWN:
            case CheckerKeyEvent.KEYCODE_VOLUME_DOWN:
            case CheckerKeyEvent.KEYCODE_DPAD_UP:
            case CheckerKeyEvent.KEYCODE_VOLUME_UP:
            case CheckerKeyEvent.KEYCODE_RETURN:
            case KeyEvent.KEYCODE_BACK:
                // Do nothing;
                return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case CheckerKeyEvent.KEYCODE_OK:
                if (isDoubleClick()) {
                    return true;
                }

                if (mComplete.getVisibility() == View.VISIBLE) {
                    replyComplete();
                } else if (mStart.getVisibility() == View.VISIBLE) {
                    startWork();
                }
                return true;
            case CheckerKeyEvent.KEYCODE_REPLAY:
                startCurrentTaskSpeech();
                return true;
            case CheckerKeyEvent.KEYCODE_RETURN:
                gotoLogout();
                return true;
            case CheckerKeyEvent.KEYCODE_DPAD_DOWN:
            case CheckerKeyEvent.KEYCODE_VOLUME_DOWN:
                if (mAdapter != null && mCurrentIndex < mAdapter.getCount() - 1) {
                    moveSelectionTo(++mCurrentIndex);
                    startCurrentTaskSpeech();
                }
                return true;
            case CheckerKeyEvent.KEYCODE_DPAD_UP:
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
        final Intent intent = new Intent(CheckerActivity.this, SelectReportActivity.class);
        intent.putExtra("ContentID", mContentId);
        intent.putExtra("MessageID", mMessageId);
        intent.putExtra("TaskLX", mTaskLX);
        intent.putExtra("TaskID", mTaskID);
        startActivity(intent);
    }

    private void checkFeedback(int position) {
        int taskStatus = Integer.valueOf(((String[])mAdapter.getItem(position))[7]);
        final boolean disable = taskStatus > Database.TASK_STATUS_DEFAULT;
        if (disable) {
            mFeedback.setEnabled(false);
            mFeedback.getCompoundDrawables()[1].setAlpha(50);
        } else {
            mFeedback.setEnabled(true);
            mFeedback.getCompoundDrawables()[1].setAlpha(255);
        }
    }

    private void checkTask(int position) {
        User user = ((Welcome)getApplication()).getCurrentUser();
        final Cursor cursor = DatabaseHandler.queryTask(user.mUserName);
        if (cursor != null && cursor.moveToPosition(position)) {
            final int status = cursor.getInt(cursor.getColumnIndexOrThrow(Database.TASK_WAIT_SUCCESS));
            final boolean unsucceed = status > 0;
            if (unsucceed) {
                mComplete.setVisibility(View.VISIBLE);
                mStart.setVisibility(View.GONE);
            } else {
                mComplete.setVisibility(View.GONE);
                mStart.setVisibility(View.VISIBLE);
            }
        }
    }

    private void recordValues(int position) {
        mContentId = Integer.valueOf(((String[])mAdapter.getItem(position))[0]);
        mMessageId = Integer.valueOf(((String[])mAdapter.getItem(position))[5]);
        mTaskLX = Integer.valueOf(((String[])mAdapter.getItem(position))[6]);
        mTaskID = Integer.valueOf(((String[])mAdapter.getItem(position))[8]);
    }

    private void initListAdapter() {
        mList = (ListView)findViewById(R.id.list);
        mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> dapterView, View view, int position, long id) {
                highlightCurrentView(view);
                mCurrentIndex = position;
                recordValues(position);
                startCurrentTaskSpeech();
            }
        });

        mList.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                highlightCurrentView(view);
                checkFeedback(position);
                checkTask(position);
                recordValues(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        User user = ((Welcome)getApplication()).getCurrentUser();
        final Cursor cursor = DatabaseHandler.queryTask(user.mUserName);
        mAdapter = new ReportAdapter(this, cursor);
        mList.setAdapter(mAdapter);
    }

    private void highlightCurrentView(View view) {
        if (mSelectedItemView != null) {
            mSelectedItemView.setBackgroundResource(R.drawable.white);
        }

        if (view != null) {
            view.setBackgroundResource(R.drawable.list_item_selected_background);
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
            final int position = cursor.getPosition();
            if (position == mCurrentIndex) {
                highlightCurrentView(view);
            } else {
                view.setBackgroundResource(R.drawable.white);
            }
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
                String[] row = new String[9];
                row[0] = cursor.getString(mColumnIndexConetntId);
                row[1] = cursor.getString(mColumnIndexOrder);
                row[1] = Utils.replaceVoiceChar(row[1]);
                row[2] = cursor.getString(mColumnIndexTrack);
                row[2] = Utils.replaceVoiceChar(row[2]);
                row[3] = cursor.getString(mColumnIndexPosition);
                row[4] = cursor.getString(mColumnIndexNotification);
                row[5] = cursor.getString(cursor.getColumnIndex(Database.TASK_MESSAGEID));
                row[6] = cursor.getString(cursor.getColumnIndex(Database.TASK_LX));
                row[7] = cursor.getString(cursor.getColumnIndex(Database.TASK_STATUS));
                row[8] = cursor.getString(cursor.getColumnIndex(Database.TASK_ID));
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

    private Toast mToast;

    /**
     * Show the toast with voice prompt.
     */
    private void showVoiceToast(int resId) {
        final String prompt = mResources.getString(resId);
        startSingleSpeech(prompt);

        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), prompt, Toast.LENGTH_LONG);
        }

        mToast.cancel();
        mToast.show();
    }

    /**
     * Start the single voice.
     */
    private void startSingleSpeech(String words) {
        stopSpeech();
        mSpeechEngine.speak(words);
    }

    private void startSingleSpeech(String words, Runnable runnable) {
        stopSpeech();
        mSpeechEngine.speak(words, runnable);
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

        final Cursor c = mAdapter.getCursor();
        if (c == null || !c.moveToPosition(mCurrentIndex)) {
            return;
        }

        stopSpeech();
        final ContentValues values = new ContentValues(1);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        values.put(Database.TASK_BEGIN_TIME, simpleDateFormat.format(System.currentTimeMillis()));
        final String where = Database.COLUMN_ID + "="
                + c.getLong(c.getColumnIndex(Database.COLUMN_ID));
        DatabaseHandler.updateWithoutNotify(Database.TABLE_SC_TASK, values, where, null);

        final int taskID = c.getInt(c.getColumnIndex(Database.TASK_ID));
        final long id = c.getLong(c.getColumnIndex(Database.COLUMN_ID));
        final User user = ((Welcome)getApplication()).getCurrentUser();
        final long contentID = c.getLong(c.getColumnIndex(Database.TASK_CONTENTID));
        // TaskHelper.replyTasks(this, user.mUserDM, messageID);
        TaskHelper.replyTasks(this, user, taskID);
        TaskHelper.reportTask(this, user, taskID, contentID, true);

        if (mContentId == -1) {
            mContentId = Integer.valueOf(((String[])mAdapter.getItem(0))[0]);
        }

        if (mContentId <= 0) {
            mComplete.setVisibility(View.VISIBLE);
            mStart.setVisibility(View.GONE);
            showVoiceToast(R.string.toast_no_content_id);
            // Mark it for "wait success"
            final ContentValues v = new ContentValues(1);
            v.put(Database.TASK_WAIT_SUCCESS, 1);
            DatabaseHandler.updateWithoutNotify(Database.TABLE_SC_TASK, v, Database.COLUMN_ID
                    + " = " + id, null);
            moveSelectionTo(mCurrentIndex);
            return;
        }

        gotoSelectReport();
    }

    private static long sLastClickTime;

    public static final boolean isDoubleClick() {
        long current = System.currentTimeMillis();
        if (current - sLastClickTime > 500) {
            sLastClickTime = current;
            return false;
        }

        sLastClickTime = current;
        return true;
    }

    public void replyComplete() {
        if (mAdapter == null || mAdapter.getCount() < 1) {
            return;
        }

        final Cursor c = mAdapter.getCursor();
        if (c == null || !c.moveToPosition(mCurrentIndex)) {
            return;
        }

        mComplete.setVisibility(View.GONE);
        stopSpeech();
        TaskHelper.reportTask(this, ((Welcome)getApplication()).getCurrentUser(), mTaskID,
                c.getLong(c.getColumnIndex(Database.TASK_CONTENTID)), false);
        showVoiceToast(R.string.voice_complete_task);
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id) {
            case R.id.complete: {
                if (isDoubleClick()) {
                    return;
                }

                replyComplete();
                break;
            }
            case R.id.start:
                if (isDoubleClick()) {
                    return;
                }

                startWork();
                break;
            case R.id.feedback: {
                if (mAdapter == null || mAdapter.getCount() < 1) {
                    return;
                }

                Cursor c = mAdapter.getCursor();
                if (c != null && c.moveToPosition(mCurrentIndex)) {
                    stopSpeech();
                    final int taskID = c.getInt(c.getColumnIndex(Database.TASK_ID));
                    final User user = ((Welcome)getApplication()).getCurrentUser();
                    // TaskHelper.replyTasks(this, user.mUserDM, messageID);
                    TaskHelper.replyTasks(this, user, taskID);
                }
                break;
            }
            case R.id.exit:
                gotoLogout();
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
                final int oldCount = oldCursor != null ? oldCursor.getCount() : 0;

                if (mAdapter != null) {
                    mAdapter.changeCursor(cursor);
                    final int newCount = mAdapter.getCount();
                    if (newCount > 0 && oldCount != newCount) {
                        mStart.setVisibility(View.VISIBLE);
                        mComplete.setVisibility(View.GONE);
                        final int count = newCount - oldCount;
                        if (count > 0) {
                            startSingleSpeech("您有" + count + "条新任务", new Runnable() {
                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            startSeriesSpeech();
                                            moveSelectionTo(0);
                                        }
                                    });
                                }
                            });
                        }
                    }
                }

                moveSelectionTo(mCurrentIndex);
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
            if (user.mUserRole != null && user.mUserRole.equals("测试者")) {
                return 1;
            }
            // For test: end

            return (Integer)new LogoutTask(CheckerActivity.this, user.mUserDM).run();
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result != null && result.intValue() == 1) {
                leave();
            } else {
                showVoiceToast(R.string.toast_logout_fail);
            }

            if (!CheckerActivity.this.isFinishing()) {
                dismissDialog(DIALOG_LOGOUT_PROGRESS);
            }
        }

    }

    private void leave() {
        final Intent intent = new Intent(CheckerActivity.this, LoginActivity.class);
        intent.setAction("logout");
        startActivity(intent);
        final User u = ((Welcome)getApplication()).getCurrentUser();
        u.mUserDM = null;
        u.mUserMobile = null;
        u.mUserName = null;
        u.mUserRole = null;
        u.mUserZMLM = null;
        finish();
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
