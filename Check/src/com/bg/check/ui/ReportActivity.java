
package com.bg.check.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.bg.check.R;
import com.bg.check.database.Databasehelper;

public class ReportActivity extends Activity implements OnClickListener {

    public static final String ORDER = "order";

    private int mOrder = -1;

    private int mTranId = -1;

    private int mStartTranID;

    private Cursor mCursor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.report_activity);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.select_report_activity_title);

        initFromIntent();

        findViewById(R.id.up).setOnClickListener(this);
        findViewById(R.id.down).setOnClickListener(this);
        findViewById(R.id.ret).setOnClickListener(this);
        findViewById(R.id.tts).setOnClickListener(this);
        // kick off a query
        new AsyncQueryReportTask().execute(null);

    }

    private void initFromIntent() {
        Intent intent = getIntent();
        mOrder = intent.getIntExtra(ORDER, -1);
        mTranId = intent.getIntExtra(Databasehelper.TASK_CONTENT_SWH, -1);
        mStartTranID = mTranId;
    }

    private class AsyncQueryReportTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            SQLiteDatabase db = Databasehelper.getInstance().getReadableDatabase();
            Cursor c = db.query(Databasehelper.SC_TASK_CONTENT, null, null, null, null, null, null);
            return c;
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
                    Log.e("::::", "order is not correct!");
            }
            if (cursor.getCount() > mTranId) {
                cursor.moveToPosition(mTranId);
                render(cursor);
                return;
            }
            Log.e(":::::::::", "ReportActivity.onPostExecute()");
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

        sw.setText(cursor.getString(cursor.getColumnIndex(Databasehelper.TASK_CONTENT_SWH)));
        ch.setText(cursor.getString(cursor.getColumnIndex(Databasehelper.TASK_CONTENT_CH)));
        cz.setText(cursor.getString(cursor.getColumnIndex(Databasehelper.TASK_CONTENT_CZ)));
        fz.setText(cursor.getString(cursor.getColumnIndex(Databasehelper.TASK_CONTENT_FZM)));
        dz.setText(cursor.getString(cursor.getColumnIndex(Databasehelper.TASK_CONTENT_DZM)));
        pm.setText(cursor.getString(cursor.getColumnIndex(Databasehelper.TASK_CONTENT_PM)));
        jsl.setText(cursor.getString(cursor.getColumnIndex(Databasehelper.TASK_CONTENT_JSL)));
        // importance.setText(cursor.getString(cursor.getColumnIndex(Databasehelper.TASK_CONTENT)));
        operate.setText(cursor.getString(cursor.getColumnIndex(Databasehelper.TASK_CONTENT_SWH)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.up:
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
                        Log.e("::::", "order is not correct!");

                }
                break;
            case R.id.down:
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
                        Log.e("::::", "order is not correct!");

                }
                break;
            case R.id.tts:
                break;
            case R.id.ret:
                finish();
                break;
        }

    }
}
