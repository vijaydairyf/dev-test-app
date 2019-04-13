package com.devapp.devmain.user;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.devapp.devmain.tableEntities.RateChartNameTable;
import com.devApp.R;

import java.text.DecimalFormat;

public class RateAdapter extends CursorAdapter {

    DecimalFormat decimalFormatAMT = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
    private int CheckForLayout;

    public RateAdapter(Context context, Cursor c, int checkForLayout) {
        super(context, c);

        CheckForLayout = checkForLayout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View rateView = null;

        if (CheckForLayout == 0) {
            rateView = inflater.inflate(R.layout.rate_chart_listitem, parent,
                    false);
        } else if (CheckForLayout == 1) {
            rateView = inflater.inflate(R.layout.rate_details_item, parent,
                    false);
            ViewHolder viewHolder = new ViewHolder(rateView);
            rateView.setTag(viewHolder);

        }

        return rateView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

        if (CheckForLayout == 0) {
            TextView tvSnf = (TextView) view.findViewById(R.id.tvSnf);
            TextView tvFat = (TextView) view.findViewById(R.id.tvFat);
            TextView tvRate = (TextView) view.findViewById(R.id.tvRate);
            tvSnf.setText(decimalFormatFS.format(cursor.getDouble(cursor
                    .getColumnIndex(cursor.getColumnName(3)))));

            tvFat.setText(decimalFormatFS.format(cursor.getDouble(cursor
                    .getColumnIndex(cursor.getColumnName(4)))));
            tvRate.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                    .getColumnIndex(cursor.getColumnName(1)))));
        } else if (CheckForLayout == 1) {

            ViewHolder viewHolder = (ViewHolder) view.getTag();

            viewHolder.tvName.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(1))));

            String rateType = cursor.getString(cursor.getColumnIndex(
                    RateChartNameTable.TYPE));
            String milkType = cursor.getString(cursor.getColumnIndex(RateChartNameTable.MILKTYPE));

            if (rateType == null || rateType.equalsIgnoreCase(Util.RATECHART_TYPE_COLLECTION)) {
                viewHolder.tvMilkType.setText(milkType);
            } else {
                viewHolder.tvMilkType.setText(milkType + "(" + rateType + ")");
            }
            String shift = cursor.getString(
                    cursor.getColumnIndex(RateChartNameTable.RATE_SHIFT));
            if (shift != null && !shift.trim().equalsIgnoreCase("")
                    && !shift.equalsIgnoreCase("null")) {
                viewHolder.tvShiftType.setText("Shift: " + shift);
            } else {
                viewHolder.tvShiftType.setText("");
            }

            viewHolder.tvValidFrom.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(cursor.getColumnIndex(RateChartNameTable.VALIDFROM)))));

            if (cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(cursor.getColumnIndex(RateChartNameTable.VALIDTO)))) != null) {
                viewHolder.tvValidTill.setText(cursor.getString(cursor.getColumnIndex(cursor
                        .getColumnName(cursor.getColumnIndex(RateChartNameTable.VALIDTO)))));
            } else {
                viewHolder.tvValidTill.setText("");
            }


        }

    }


    public static class ViewHolder {

        TextView tvName;
        TextView tvMilkType;
        TextView tvValidFrom;
        TextView tvValidTill;
        TextView tvShiftType;

        public ViewHolder(View view) {
            tvName = (TextView) view
                    .findViewById(R.id.tvRateChartName);
            tvMilkType = (TextView) view.findViewById(R.id.tvMilkType);
            tvValidFrom = (TextView) view
                    .findViewById(R.id.tvValidFrom);
            tvValidTill = (TextView) view
                    .findViewById(R.id.tvValidTill);
            tvShiftType = (TextView) view.findViewById(R.id.tvShiftType);

        }


    }

}
