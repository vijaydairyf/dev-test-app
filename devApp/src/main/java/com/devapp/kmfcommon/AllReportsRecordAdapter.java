package com.devapp.kmfcommon;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

public class AllReportsRecordAdapter extends CursorAdapter {

    TextView tvFat, tvSnf, tvShift, tvQty, tvRate, tvAmount, textId, tvClr;
    ImageView img;
    Context context;
    Cursor cursorMain;

    public AllReportsRecordAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.context = context;

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Cursor getCursor() {
        return super.getCursor();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.editable_report_item, parent,
                false);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        ReportEntity reportEntity = SmartCCUtil.getReportFromCursor(cursor);

        textId = (TextView) view.findViewById(R.id.textId);
        tvFat = (TextView) view.findViewById(R.id.tvFat);
        tvSnf = (TextView) view.findViewById(R.id.tvSnf);
        tvShift = (TextView) view.findViewById(R.id.tvShift);
        tvQty = (TextView) view.findViewById(R.id.tvQty);
        tvRate = (TextView) view.findViewById(R.id.tvRate);
        tvAmount = (TextView) view.findViewById(R.id.tvAmount);
        tvClr = (TextView) view.findViewById(R.id.tvClr);
        img = (ImageView) view.findViewById(R.id.img);

        textId.setText(reportEntity.getFarmerId());
        tvFat.setText(String.valueOf(reportEntity.getDisplayFat()));
        tvSnf.setText(String.valueOf(reportEntity.getDisplaySnf()));
        tvClr.setText(String.valueOf(reportEntity.getDisplayClr()));
        //tvShift.setText(cursor.getString(cursor.getColumnIndex("date"))+"("+cursor.getString(cursor.getColumnIndex("shift"))+")");
        tvShift.setText("");
        tvQty.setText(String.valueOf(reportEntity.getDisplayQuantity()));
        tvRate.setText(String.valueOf(reportEntity.getDisplayRate()));
        img.setVisibility(View.GONE);
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        long seqId = cursor.getInt(cursor.getColumnIndex(FarmerCollectionTable.SEQUENCE_NUMBER));
        Cursor cursor1 = dbh.getEditedReportCursor(seqId);
        if (cursor1.moveToFirst()) {
            do {
                if (cursor1.getString(cursor1.getColumnIndex("oldOrNew")).equalsIgnoreCase("old")) {
                    img.setVisibility(View.GONE);
                } else {
                    img.setVisibility(View.VISIBLE);
                }
            } while (cursor1.moveToNext());
        }
        cursor1.close();
        final int position = cursor.getPosition();
        cursorMain = cursor;
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cursorMain.moveToPosition(position);
//                Intent intent = new Intent(context,UpdatePastRecord.class);
//                intent.putExtra("ID",cursorMain.getString(0));
//                context.startActivity(intent);
//
//
//            }
//        });

    }


}
