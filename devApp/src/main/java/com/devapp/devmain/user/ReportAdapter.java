package com.devapp.devmain.user;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.devApp.R;


public class ReportAdapter extends CursorAdapter {

    public ReportAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.report_items, parent, false);
        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

        TextView tvFarmerName = (TextView) view.findViewById(R.id.tvFarmerName);
        TextView tvfarmerId = (TextView) view.findViewById(R.id.tvFarmerId);

        TextView tvSnf = (TextView) view.findViewById(R.id.tvSnf);
        TextView tvFat = (TextView) view.findViewById(R.id.tvFat);
        TextView tvRate = (TextView) view.findViewById(R.id.tvRate);

        tvFarmerName.setText(cursor.getString(cursor.getColumnIndex(cursor
                .getColumnName(2))));

        tvfarmerId.setText(cursor.getString(cursor.getColumnIndex(cursor
                .getColumnName(1))));

        tvSnf.setText(cursor.getString(cursor.getColumnIndex(cursor
                .getColumnName(3))));

        tvFat.setText(cursor.getString(cursor.getColumnIndex(cursor
                .getColumnName(4))));

        tvRate.setText(cursor.getString(cursor.getColumnIndex(cursor
                .getColumnName(7))));

    }
}
