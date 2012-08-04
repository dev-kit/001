package com.bg.check.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bg.check.R;
import com.bg.check.database.DatabaseUtils;
import com.bg.check.database.Databasehelper;
import com.bg.check.engine.SpeechEngine;
import com.bg.check.engine.SpeechEngine.SpeechListener;
import com.bg.check.engine.utils.LogUtils;

public class CheckerActivity extends Activity implements OnClickListener, SpeechListener {

    private ListView mList;
    private CursorAdapter mAdapter;
    private LayoutInflater mInflater;
    private TextView mStart;
    private TextView mExit;
    private TextView mVoice;
    private int mCurrentIndex;
    private String[] mHeaders;
    private String mReportIndex;
    private boolean mStopSpeak = true;

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

    private void gotoReport(int order) {
        final Intent intent = new Intent(CheckerActivity.this, SelectReportActivity.class);
        intent.putExtra(ReportActivity.ORDER, order);
        startActivity(intent);
    }

    private void initListAdapter() {
        mList = (ListView) findViewById(R.id.list);
        mList.setItemsCanFocus(false);
        Cursor cursor = DatabaseUtils.queryTask();
        mAdapter = new ReportAdapter(this, cursor);
        mList.setAdapter(mAdapter);
    }

    private class ReportAdapter extends CursorAdapter {

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
            mColumnIndexOrder = c.getColumnIndexOrThrow(Databasehelper.TASK_CC);
            mColumnIndexTrack = c.getColumnIndexOrThrow(Databasehelper.TASK_GDM);
            mColumnIndexPosition = c.getColumnIndexOrThrow(Databasehelper.TASK_JCWZ);
            mColumnIndexNotification = c.getColumnIndexOrThrow(Databasehelper.TASK_JLSJ);
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
                String[] row = new String[4];
                row[0] = cursor.getString(mColumnIndexOrder);
                row[1] = cursor.getString(mColumnIndexTrack);
                row[2] = cursor.getString(mColumnIndexPosition);
                row[3] = cursor.getString(mColumnIndexNotification);
                return row;
            }
            return null;
        }
    }

    private void startSpeech() {
        mStopSpeak = false;
        SpeechEngine.getInstance(getApplicationContext()).speakSeries(this);
    }

    private void stopSpeech() {
        mStopSpeak = true;
        SpeechEngine.getInstance(getApplicationContext()).stopSpeak();
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        switch (id) {
        case R.id.start:
            gotoReport(1);
            break;
        case R.id.exit:
            gotoLogin();
            break;
        case R.id.voice:
            if (mStopSpeak) {
                mVoice.setText(R.string.voice_stop);
                startSpeech();
            } else {
                stopSpeech();
            }
            break;
        default:
            LogUtils.logW("unknown id = " + id);
        }
    }

    @Override
    public String onPrepareSpeech() {
        if (mCurrentIndex < mAdapter.getCount()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mList.setSelection(mCurrentIndex - 1);
                }
            });

            String[] row = (String[]) mAdapter.getItem(mCurrentIndex++);
            StringBuilder builder = new StringBuilder();
            builder.append(String.format(mReportIndex, mCurrentIndex));
            builder.append(SpeechEngine.COMMA);
            for(int i = 0; i < row.length; i++) {
                builder.append(mHeaders[i]);
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
        if (mCurrentIndex < mAdapter.getCount() && !mStopSpeak) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onSeriesSpeechComplete() {
        mStopSpeak = true;
        mCurrentIndex = 0;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVoice.setText(R.string.voice);
            }
        });
    }
}
