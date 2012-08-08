
package com.bg.check.ui;

import android.app.ListActivity;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.bg.check.database.Database;
import com.bg.check.database.DatabaseHandler;
import com.bg.check.database.DatabaseHandler.DatabaseObserver;

public class SelectReportActivity extends ListActivity implements DatabaseObserver,
        OnCheckedChangeListener, OnClickListener {

    private AsyncQueryHandler mQueryHandler;

    private CursorAdapter mCursorAdapter;

    private int mContentID;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.select_report_activity);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.select_report_activity_title);

        // setFullscreen();
        initListAdapter();

        ((RadioButton)findViewById(R.id.order)).setOnCheckedChangeListener(this);
        ((RadioButton)findViewById(R.id.reverse_order)).setOnCheckedChangeListener(this);
        findViewById(R.id.start).setOnClickListener(this);

        // kick off a query
        mContentID = getIntent().getIntExtra("ContentID", -1);
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
                }
                break;
            case R.id.reverse_order:
                if (click) {
                    ((RadioButton)findViewById(R.id.order)).setChecked(false);
                }
                break;
            default:
                Log.e(":::::::", "onCheckedChanged");
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start) {
            Cursor c = mCursorAdapter.getCursor();
            if (c.getCount() <= 0) {
                Log.e(":::::::", "onClick");
                return;
            }
            Intent intent = new Intent(this, ReportActivity.class);
            intent.putExtra("ContentID", mContentID);

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
    }

    private class AsyncQueryReportTask extends AsyncTask<Integer, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Integer... contentID) {
            String where = null;
            if (contentID != null && contentID[0] >= 0) {
                where = Database.TASK_CONTENT_CONTENT_ID + "=" + contentID[0] + " and "
                        + Database.TASK_CONTENT_STATUS + "=" + Database.TASK_STATUS_DEFAULT;
            }

            return DatabaseHandler.query(Database.TABLE_SC_TASK_CONTENT, null, where, null, null,
                    null, null);
        }

        @Override
        protected void onPostExecute(final Cursor cursor) {
            super.onPostExecute(cursor);
            findViewById(R.id.start).setClickable(cursor.getCount() > 0);
            mCursorAdapter.changeCursor(cursor);
            initSpinner(cursor);
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
}
