package com.devapp.devmain.user;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.devapp.devmain.entity.RateChartEntity;
import com.devApp.R;

import java.util.ArrayList;


public class IncentiveAdapter extends ArrayAdapter<RateChartEntity> {

    private ArrayList<RateChartEntity> rateChartEntities = null;
    private LayoutInflater mInflater;


    public IncentiveAdapter(Context context, int resource, ArrayList<RateChartEntity> rateChartEntities) {
        super(context, resource, rateChartEntities);
        this.rateChartEntities = rateChartEntities;


        mInflater = LayoutInflater.from(context);

    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.rate_chart_listitem, null);
            viewHolder.tvPoint = (TextView) convertView.findViewById(R.id.tvSnf);
            viewHolder.tvRate = (TextView) convertView.findViewById(R.id.tvRate);
            viewHolder.tvFat = (TextView) convertView.findViewById(R.id.tvFat);

            convertView.setTag(viewHolder);
        }
        viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.tvPoint.setText(String.valueOf(rateChartEntities.get(position).snf));
        viewHolder.tvRate.setText(String.valueOf(rateChartEntities.get(position).rate));
        viewHolder.tvFat.setText("");

        return convertView;
    }

    static class ViewHolder {
        TextView tvPoint, tvRate, tvFat;
    }


}


