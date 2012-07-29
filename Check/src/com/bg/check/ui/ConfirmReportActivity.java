package com.bg.check.ui;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bg.check.R;
import com.bg.check.database.DatabaseUtils;

public class ConfirmReportActivity extends Activity {

    private ListView mList;
    private TextView mHeaderOrder;
    private CursorAdapter mAdapter;
    private LayoutInflater mInflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_report);
        mInflater = LayoutInflater.from(this);
        initListAdapter();
    }

    private void initListAdapter() {
        mList = (ListView) findViewById(R.id.list);
        Cursor cursor = DatabaseUtils.queryConfirmReport();
        mAdapter = new ReportAdapter(this, cursor);
        final View header = mInflater.inflate(R.layout.confirm_report_list_header, null, false);
//        mHeaderOrder = (TextView) header.findViewById(R.id.order)).setText(R.string.record_order);
        mHeaderOrder = (TextView) header.findViewById(R.id.order);
//        ((TextView) header.findViewById(R.id.train_id)).setText(R.string.train_id);
//        ((TextView) header.findViewById(R.id.record)).setText(R.string.record);
//        ((TextView) header.findViewById(R.id.time)).setText(R.string.record_time);
        mList.addHeaderView(header, null, false);
        mList.setAdapter(mAdapter);
    }

    private class ReportAdapter extends CursorAdapter {

        private class ViewHolder {
            TextView mOrder;
            TextView mTrainNumber;
            TextView mRecord;
            TextView mTime;
        }

        public ReportAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder == null) {
                holder = new ViewHolder();
                holder.mOrder = (TextView) view.findViewById(R.id.order);
                holder.mTrainNumber = (TextView) view.findViewById(R.id.train_id);
                holder.mRecord = (TextView) view.findViewById(R.id.record);
                holder.mTime = (TextView) view.findViewById(R.id.time);
            }

            holder.mOrder.setText("orderasdfasdfasdfasdfasdfasdfasdfasdfasdfasdf");
            holder.mTrainNumber.setText("train number, nasdfasdfasdfasdfasdfasdfasdfasdfasdfnumbernumber");
            holder.mRecord.setText("record");
            holder.mTime.setText("time");

//            mHeaderOrder.setWidth(holder.mOrder.getWidth());
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return mInflater.inflate(R.layout.confirm_report_list_item, null, false);
        }
        
    }
}
