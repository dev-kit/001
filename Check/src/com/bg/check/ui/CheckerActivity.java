package com.bg.check.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bg.check.R;
import com.bg.check.database.DatabaseUtils;

public class CheckerActivity extends Activity {

    private ListView mList;
    private CursorAdapter mAdapter;
    private LayoutInflater mInflater;
    private TextView mStart;
    private TextView mExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.checker_activity);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.checker_activity_title);
        mInflater = LayoutInflater.from(this);
        initUi();
        initListAdapter();
    }

    private void initUi() {
        mStart = (TextView) findViewById(R.id.start);
        mExit = (TextView) findViewById(R.id.exit);

        mStart.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = 1;
                gotoReport(id);
            }
        });

        mExit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoLogin();
            }
        });
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

    private void gotoReport(int order) {
        final Intent intent = new Intent(CheckerActivity.this, SelectReportActivity.class);
        intent.putExtra(ReportActivity.ORDER, order);
        startActivity(intent);
    }

    private void initListAdapter() {
        mList = (ListView) findViewById(R.id.list);
        Cursor cursor = DatabaseUtils.queryTask();
        mAdapter = new ReportAdapter(this, cursor);
        mList.setAdapter(mAdapter);
    }

    private class ReportAdapter extends CursorAdapter {

        private class ViewHolder {
            TextView mOrder;
            TextView mTrack;
            TextView mPosition;
            TextView mNotification;
        }

        public ReportAdapter(Context context, Cursor c) {
            super(context, c);
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

            holder.mOrder.setText("D23");
            holder.mTrack.setText("CD05");
            holder.mPosition.setText("×ó");
            holder.mNotification.setText("20:02");
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return mInflater.inflate(R.layout.checker_list_item, null, false);
        }
        
    }
}
