package com.devapp.devmain.user;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.DatabaseHandler;
import com.devApp.R;


public class FarmerAdapter extends CursorAdapter {

    Context cxt;
    private int forUserCheck;
    private LinearLayout layoutHeader;

    public FarmerAdapter(Context context, Cursor c, int forUserchk) {
        super(context, c);

        this.forUserCheck = forUserchk;
        this.cxt = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = null;

        if (forUserCheck == 2) {
            retView = inflater.inflate(R.layout.allsociety_item, parent, false);
        } else {
            retView = inflater.inflate(R.layout.allfarmerlst_item, parent,
                    false);
        }

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

        TextView tvfarmer = (TextView) view.findViewById(R.id.tvFarmerName);
        TextView tvfarmerId = (TextView) view.findViewById(R.id.tvFarmerId);
        if (forUserCheck == 0) {
            TextView tvType = (TextView) view.findViewById(R.id.tvType);
            tvfarmer.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(2))));
            tvfarmerId.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(7))));

            if (cursor.getString(cursor.getColumnIndex(
                    DatabaseHandler.KEY_FARMER_TYPE)) != null && cursor.getString(cursor.getColumnIndex(
                    DatabaseHandler.KEY_FARMER_TYPE)).equalsIgnoreCase(AppConstants.FARMER_TYPE_AGENT)) {
                tvType.setVisibility(View.VISIBLE);
                tvType.setText(AppConstants.FARMER_TYPE_AGENT);
            } else {
                tvType.setVisibility(View.GONE);
            }

        } else if (forUserCheck == 4) {

            //To display center list from chilling center
            tvfarmer.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(3))));
            tvfarmerId.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(2))));
            String activeStatus = cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(13)));

            if (activeStatus != null) {
                if (activeStatus.equalsIgnoreCase("1")) {

                    view.setBackgroundColor(cxt.getResources().getColor(R.color.transparent));
                } else {

                    view.setBackgroundColor(cxt.getResources().getColor(R.color.focused));

                }
            }
        } else if (forUserCheck == 1) {

            tvfarmer.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(3))));
            tvfarmerId.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(7))));
        } else if (forUserCheck == 2) {
            TextView tvSocId = (TextView) view.findViewById(R.id.tvSocietyId);
            TextView tvSocietyName = (TextView) view
                    .findViewById(R.id.tvSocietyName);
            TextView tvNumOfFarm = (TextView) view
                    .findViewById(R.id.tvNumOfFarm);
            tvSocId.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(3))));
            int i = count(cursor.getInt(cursor.getColumnIndex(cursor
                    .getColumnName(0))));
            tvSocietyName.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(9))));
            tvNumOfFarm.setText(String.valueOf(i));

        } else if (forUserCheck == 3) {
            tvfarmer.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(1))));

            tvfarmerId.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(14))));
        }

    }

    public int count(int socId) {
        int count = 0;
        FarmerDao farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        count = farmerDao.getCount();
        return count;
    }
}
