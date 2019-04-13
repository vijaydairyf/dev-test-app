package com.devapp.devmain.user;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.devapp.devmain.server.DatabaseHandler;
import com.devApp.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyRateChartAdapter extends CursorAdapter {
    Context mcontext;

    public MyRateChartAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        this.mcontext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.myrate_list_row, parent, false);
        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView rcName = (TextView) view.findViewById(R.id.rcName);
        TextView cattletype = (TextView) view.findViewById(R.id.cattletype);
        TextView sDate = (TextView) view.findViewById(R.id.sDate);
        TextView eDate = (TextView) view.findViewById(R.id.eDate);
        CardView cv = (CardView) view.findViewById(R.id.row1);
        rcName.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_NAME)));
        cattletype.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_MILK_TYPE)));
        long sDte = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_DATE));
        Date tmpdate = new Date(sDte);
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String tempStringDate = df.format(tmpdate);
        sDate.setText(tempStringDate +
                "(" + cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_SHIFT)) + ")");
        long eDte = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_DATE));
        Date tmpedate = new Date(eDte);
        String tempeStringDate = df.format(tmpedate);
        eDate.setText(tempeStringDate +
                "(" + cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_SHIFT)) + ")");
        cattletype.setText(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_MILK_TYPE)));
        final String rcname = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_NAME));
        /*view.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
				Intent intent = new Intent(mcontext, ShowMyRateChart.class);
				intent.putExtra("RN", rcname);
				mcontext.startActivity(intent);

				Toast.makeText(mcontext, "RateChartName "+rcname, Toast.LENGTH_SHORT).show();


			}
		});*/


    }

}
