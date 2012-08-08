package com.bg.check.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import com.bg.check.engine.SpeechEngine;
import com.bg.check.engine.SpeechEngine.SpeechListener;
import com.bg.check.engine.utils.LogUtils;
import com.bg.check.engine.utils.TaskHelper;

public class CheckerActivity extends Activity implements DatabaseObserver, OnClickListener, SpeechListener {

    private ListView mList;
    private CursorAdapter mAdapter;
    private LayoutInflater mInflater;
    private TextView mStart;
    private TextView mFeedback;
    private TextView mExit;
    private TextView mVoice;
    private TextView mVoiceStop;
    private int mCurrentIndex;
    private String[] mHeaders;
    private String mReportIndex;
    private int mContentId = -1;
    private Resources mResources;
    private SpeechEngine mSpeechEngine;
    private boolean mSpeechStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initString();
        initUi();
        initListAdapter();
    }

    @Override
    protected void onResume() {
        DatabaseHandler.addDatabaseObserver(this);
        mSpeechEngine.registerSpeechListener(this);
        super.onResume();
    }

    private void init() {
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.checker_activity);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.checker_activity_title);
        mInflater = LayoutInflater.from(this);
        mResources = getResources();
        mSpeechEngine = SpeechEngine.getInstance(getApplicationContext());
    }

    private void initString() {
        Resources res = getResources();
        mHeaders = new String[4];
        mHeaders[0] = res.getString(R.string.train_order);
        mHeaders[1] = res.getString(R.string.track);
        mHeaders[2] = res.getString(R.string.position);
        mHeaders[3] = res.getString(R.string.notification);
        mReportIndex = res.getString(R.string.report_index);
    }
    private void initUi() {
        mStart = (TextView) findViewById(R.id.start);
        mFeedback = (TextView) findViewById(R.id.feedback);
        mExit = (TextView) findViewById(R.id.exit);
        mVoice = (TextView) findViewById(R.id.voice);
        mVoiceStop = (TextView) findViewById(R.id.voice_stop);

        mStart.setOnClickListener(this);
        mFeedback.setOnClickListener(this);
        mExit.setOnClickListener(this);
        mVoice.setOnClickListener(this);
        mVoiceStop.setOnClickListener(this);
    }

    private void gotoLogin() {
        final Intent intent = new Intent(CheckerActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        // Do nothing
    }

    @Override
    protected void onPause() {
        stopSpeech();
        DatabaseHandler.removeDatabaseObserver(this);
        mSpeechEngine.unregisterSpeechListener();
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case CheckerKeyEvent.KEYCODE_OK:
            stopSpeech();
            startCurrentTaskSpeech();
            return true;
        case CheckerKeyEvent.KEYCODE_REPLAY:
            stopSpeech();
            startCurrentTaskSpeech();
            return true;
        case CheckerKeyEvent.KEYCODE_VOLUME_DOWN:
            if (mAdapter != null && mCurrentIndex < mAdapter.getCount() - 1) {
                moveFocus(++mCurrentIndex);
                stopSpeech();
                startCurrentTaskSpeech();
            }
            return true;
        case CheckerKeyEvent.KEYCODE_VOLUME_UP:
            if (mCurrentIndex > 0) {
                moveFocus(--mCurrentIndex);
                stopSpeech();
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
            if (mContentId == -1 && mAdapter.getCount() > 0) {
                mContentId = Integer.valueOf(((String[]) mAdapter.getItem(0))[0]);
            }

            if (mContentId == 0) {
                final String prompt = mResources.getString(R.string.toast_no_content_id);
                Toast.makeText(getApplicationContext(), prompt, Toast.LENGTH_LONG).show();
                mSpeechEngine.speak(prompt);
//                return;
            }

            intent.putExtra("ContentID", mContentId);
            startActivity(intent);
        }
    }

    private void initListAdapter() {
        mList = (ListView) findViewById(R.id.list);
        mList.setSelectionAfterHeaderView();
        mList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> dapterView, View view, int position, long id) {
                view.requestFocusFromTouch();
                mCurrentIndex = position;
                mContentId = Integer.valueOf(((String[]) mAdapter.getItem(position))[0]);
            }
        });

        final Cursor cursor = DatabaseHandler.queryTask();
        mAdapter = new ReportAdapter(this, cursor);
        mList.setAdapter(mAdapter);
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
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.mOrder = (TextView) view.findViewById(R.id.train_order);
                holder.mTrack = (TextView) view.findViewById(R.id.track);
                holder.mPosition = (TextView) view.findViewById(R.id.position);
                holder.mNotification = (TextView) view.findViewById(R.id.notification);
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
                String[] row = new String[5];
                row[0] = cursor.getString(mColumnIndexConetntId);
                row[1] = cursor.getString(mColumnIndexOrder);
                row[2] = cursor.getString(mColumnIndexTrack);
                row[3] = cursor.getString(mColumnIndexPosition);
                row[4] = cursor.getString(mColumnIndexNotification);
                return row;
            }
            return null;
        }
    }

    private synchronized void stopSpeech() {
        mSpeechStop = true;
        mVoice.setVisibility(View.VISIBLE);
        mVoiceStop.setVisibility(View.GONE);
        mSpeechEngine.stopSpeak();
    }

    private synchronized void prepareStartSpeech() {
        mVoice.setVisibility(View.GONE);
        mVoiceStop.setVisibility(View.VISIBLE);
        mList.requestFocus();
        mList.requestFocusFromTouch();
        mSpeechStop = false;
    }

    private synchronized void startCurrentTaskSpeech() {
        prepareStartSpeech();
        final String words = getCurrentTaskString();
        mSpeechEngine.speak(words);
    }

    private synchronized void startSeriesSpeech() {
        prepareStartSpeech();
        mSpeechEngine.speakSeries();
    }

    @Override
    public void onClick(View v) {
        if (mAdapter == null || mAdapter.getCount() < 1) {
            return;
        }

        final int id = v.getId();

        switch (id) {
        case R.id.start:
            Cursor c = mAdapter.getCursor();
            int messageID = 0;
            if (c != null) {
                c.getInt(c.getColumnIndex(Database.TASK_MESSAGEID));
            }
            User user = ((Welcome)getApplication()).getCurrentUser();
            TaskHelper.replyTasks(user.mUserDM, messageID);
            gotoSelectReport();
            break;
        case R.id.feedback:
            
        case R.id.exit:
            gotoLogin();
            break;
        case R.id.voice:
            startSeriesSpeech();
            break;
        case R.id.voice_stop:
            stopSpeech();
            break;
        default:
            LogUtils.logW("unknown id = " + id);
        }
    }

    private void moveFocus(int position) {
        mList.setSelectionFromTop(position, 100);
        mList.requestFocus();
        mList.requestFocusFromTouch();
        final View item = mList.getSelectedView();
        if (item != null) {
            item.requestFocus();
            item.requestFocusFromTouch();
        }
    }

    private String getCurrentTaskString() {
        if (mCurrentIndex < mAdapter.getCount()) {
            String[] row = (String[]) mAdapter.getItem(mCurrentIndex);
            StringBuilder builder = new StringBuilder();
            builder.append(String.format(mReportIndex, mCurrentIndex + 1));
            builder.append(SpeechEngine.COMMA);
            for(int i = 1; i < row.length; i++) {
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
        return getCurrentTaskString();
    }

    @Override
    public boolean hasNextSpeech() {
        if (!mSpeechStop && ++mCurrentIndex < mAdapter.getCount()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    moveFocus(mCurrentIndex);
                }
            });
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized void onSpeechComplete() {
//        mCurrentIndex = 0;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                mVoice.setVisibility(View.VISIBLE);
//                mVoiceStop.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onUpdate() {
        final Cursor cursor = DatabaseHandler.queryTask();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.changeCursor(cursor);
            }
        });
    }
}
