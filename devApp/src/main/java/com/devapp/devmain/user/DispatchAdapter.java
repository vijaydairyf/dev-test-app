package com.devapp.devmain.user;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.tableEntities.DispatchCollectionTable;
import com.devApp.R;

import java.text.DecimalFormat;

public class DispatchAdapter extends CursorAdapter {

    Context context;

    DecimalFormat decimalFormatAMT = new DecimalFormat("#0.00");
    AmcuConfig amcuConfig;
    int serialNumber = 0;
    ChooseDecimalFormat chooseDecimalFormat;
    DecimalFormat decimalFormatFat;
    DecimalFormat decimalFormatSNF;
    TextView tvShift, tvCollQty, tvSoldQty, tvTime,
            tvMeasuredQty, tvMilkType, tvFat, tvSnf;


    public DispatchAdapter(Context context, Cursor c) {
        super(context, c);
        this.context = context;
        amcuConfig = AmcuConfig.getInstance();
        chooseDecimalFormat = new ChooseDecimalFormat();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.dispatch_report_item, parent, false);


        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        tvShift = (TextView) view.findViewById(R.id.tv_shift);
        tvTime = (TextView) view.findViewById(R.id.tv_time);
        tvMilkType = (TextView) view.findViewById(R.id.tv_milk_type);
        tvFat = (TextView) view.findViewById(R.id.tv_fat);
        tvSnf = (TextView) view.findViewById(R.id.tv_snf);
//        tvCans = (TextView) view.findViewById(R.id.tv_cans);
        tvCollQty = (TextView) view.findViewById(R.id.tv_coll_qty);
        tvSoldQty = (TextView) view.findViewById(R.id.tv_sold_qty);
        tvMeasuredQty = (TextView) view.findViewById(R.id.tv_measured_qty);


        tvShift.setText(cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_POST_SHIFT)));
        tvTime.setText(Util.getTimeFromMiliSecond(cursor.getLong(cursor.getColumnIndex(DispatchCollectionTable.KEY_TIME_MILLIS))));
        tvCollQty.setText(cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_COLLECTED_QUANTITY)));
        tvSoldQty.setText(cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_SOLD_QUANTITY)));
        tvMeasuredQty.setText(cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_AVAILABLE_QUANTITY)));
        tvMilkType.setText(cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_MILKTYPE)));
        tvFat.setText(cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_FAT)));
        tvSnf.setText(cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_SNF)));
//        tvCans.setText(cursor.getInt(cursor.getColumnIndex(DispatchCollectionTable.NO_OF_CANS)));

//        tvRate.setText(decimalFormatFat.format(cursor.getDouble(cursor.getColumnIndex(
//                DispatchCollectionTable        ))));


    }
}
