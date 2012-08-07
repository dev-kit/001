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
import com.bg.check.database.Database;
import com.bg.check.database.DatabaseHandler;
import com.bg.check.database.DatabaseHandler.DatabaseObserver;
import com.bg.check.engine.SpeechEngine;
import com.bg.check.engine.SpeechEngine.SpeechListener;
import com.bg.check.engine.utils.LogUtils;

public class CheckerActivity extends Activity implements DatabaseObserver, OnClickListener, SpeechListener {

    private ListView mList;
    private CursorAdapter mAdapter;
    private LayoutInflater mInflater;
    private TextView mStart;
    private TextView mExit;
    private TextView mVoice;
    private int mCurrentIndex;
    private String[] mHeaders;
    private String mReportIndex;
    private int mContentId = -1;
    private Resources mResources;
    private SpeechEngine mSpeechEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initString();
        initUi();
        initListAdapter();
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
        mExit = (TextView) findViewById(R.id.exit);
        mVoice = (TextView) findViewById(R.id.voice);

        mStart.setOnClickListener(this);
        mExit.setOnClickListener(this);
        mVoice.setOnClickListener(this);
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
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case CheckerKeyEvent.KEYCODE_OK:
            
            return true;
        case CheckerKeyEvent.KEYCODE_SCAN:
            
            return true;
        case CheckerKeyEvent.KEYCODE_VOLUME_DOWN:
            if (mAdapter != null && mCurrentIndex < mAdapter.getCount() - 1) {
                moveFocus(++mCurrentIndex);
                return true;
            }
            break;
        case CheckerKeyEvent.KEYCODE_VOLUME_UP:
            if (mCurrentIndex > 0) {
                moveFocus(--mCurrentIndex);
                return true;
            }
            break;
            default:
        }

        return super.onKeyDown(keyCode, event);
    }

    private void gotoSelectReport() {
        if (mAdapter != null) {
            final Intent intent = new Intent(CheckerActivity.this, SelectReportActivity.class);
            if (mContentId == -1) {
                mContentId = Integer.valueOf(((String[]) mAdapter.getItem(0))[0]);
            }

            if (mContentId == 0) {
                final String prompt = mResources.getString(R.string.toast_no_content_id);
                Toast.makeText(getApplicationContext(), prompt, Toast.LENGTH_LONG).show();
                mSpeechEngine.speak(prompt);
                return;
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

    private synchronized void startSpeech() {
        mVoice.setText(R.string.voice_stop);
        mList.requestFocus();
        mList.requestFocusFromTouch();
        mSpeechEngine.speakSeries(this);
    }

    private synchronized void stopSpeech() {
        mVoice.setText(R.string.voice);
        mSpeechEngine.stopSpeak();
    }

    private boolean isSpeechStart() {
        return mVoice.getText().toString().equals(mResources.getString(R.string.voice));
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id) {
        case R.id.start:
            gotoSelectReport();
            break;
        case R.id.exit:
            gotoLogin();
            break;
        case R.id.voice:
            if (isSpeechStart()) {
                startSpeech();
            } else {
                stopSpeech();
            }
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

    @Override
    public String onPrepareSpeech() {
        if (mCurrentIndex < mAdapter.getCount()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    moveFocus(mCurrentIndex - 1);
                }
            });

            String[] row = (String[]) mAdapter.getItem(mCurrentIndex++);
            StringBuilder builder = new StringBuilder();
            builder.append(String.format(mReportIndex, mCurrentIndex));
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
    public boolean hasNextSpeech() {
        if (mCurrentIndex < mAdapter.getCount() && !isSpeechStart()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public synchronized void onSeriesSpeechComplete() {
        mCurrentIndex = 0;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVoice.setText(R.string.voice);
            }
        });
    }

    @Override
    public void onUpdate() {
        final Cursor cursor = DatabaseHandler.queryTask();
        mAdapter.changeCursor(cursor);
    }
}
