
package com.bg.check.ui;

import android.app.ListActivity;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.bg.check.R;
import com.bg.check.database.Databasehelper;

public class SelectReportActivity extends ListActivity {

    private AsyncQueryHandler mQueryHandler;

    private CursorAdapter mCursorAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.select_report_activity);

        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.select_report_activity_title);

        // setFullscreen();
        initListAdapter();
        new AsyncQueryReportTask().execute(null);

        // kick off a query for the threads which match the search string
        // mQueryHandler.startQuery(0, null, null, null, null, null, null);
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
//                TextView time = (TextView)view.findViewById(R.id.time);
                sw.setText(cursor.getString(cursor.getColumnIndex(Databasehelper.TASK_CONTENT_SWH)));
                ch.setText(cursor.getString(cursor.getColumnIndex(Databasehelper.TASK_CONTENT_CH)));
                js.setText(cursor.getString(cursor.getColumnIndex(Databasehelper.TASK_CONTENT_JSL)));

            }
        };
        setListAdapter(mCursorAdapter);
    }

    private class AsyncQueryReportTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            SQLiteDatabase db = Databasehelper.getInstance().getReadableDatabase();
            Cursor c = db.query(Databasehelper.SC_TASK_CONTENT, null, null, null, null, null, null);
            return c;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            super.onPostExecute(cursor);
            mCursorAdapter.changeCursor(cursor);
        }
    }

    public void setFullscreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    // private void initQueryHandler() {
    // mQueryHandler = new AsyncQueryHandler(getContentResolver()) {
    // protected void onQueryComplete(int token, Object cookie, Cursor c) {
    // if (c == null) {
    // return;
    // }
    //
    // // Note that we're telling the CursorAdapter not to do
    // // auto-requeries. If we
    // // want to dynamically respond to changes in the search results,
    // // we'll have have to add a setOnDataSetChangedListener().
    // setListAdapter(new CursorAdapter(SelectReportActivity.this, c, false) {
    // @Override
    // public void bindView(View view, Context context, Cursor cursor) {
    //
    // view.setOnClickListener(new View.OnClickListener() {
    // public void onClick(View v) {
    // final Intent onClickIntent = new Intent(SelectReportActivity.this,
    // ReportActivity.class);
    // startActivity(onClickIntent);
    // }
    // });
    // }
    //
    // @Override
    // public View newView(Context context, Cursor cursor, ViewGroup parent) {
    // LayoutInflater inflater = LayoutInflater.from(context);
    // View v = inflater.inflate(R.layout.select_report_item, parent, false);
    // return v;
    // }
    //
    // });
    //
    // }
    // };
    // }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }
}
