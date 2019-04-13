package com.devapp.devmain.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.devapp.devmain.entity.AverageReportDetail;
import com.devApp.R;

import java.util.ArrayList;

/**
 * Created by Upendra on 1/4/2016.
 */
public class TotalShiftAdapter extends ArrayAdapter<AverageReportDetail> {

    LayoutInflater inflater;
    private Context mContext;
    private ArrayList<AverageReportDetail> allAverageReportDetail;

    public TotalShiftAdapter(Context context, int resource, ArrayList<AverageReportDetail> objects) {
        super(context, resource, objects);

        this.allAverageReportDetail = objects;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return allAverageReportDetail.size();
    }

    @Override
    public AverageReportDetail getItem(int position) {
        return allAverageReportDetail.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        int pos = position;

        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.daily_report_item, null);

            holder = new ViewHolder();

            holder.tvCode = (TextView) convertView.findViewById(R.id.tvMemberId);
            holder.tvTQty = (TextView) convertView.findViewById(R.id.tvTax);
            holder.tvARate = (TextView) convertView.findViewById(R.id.tvQuality);
            holder.tvTAmount = (TextView) convertView.findViewById(R.id.tvRate);
            holder.tvSignature = (TextView) convertView.findViewById(R.id.tvAmount);

            holder.tvFat = (TextView) convertView.findViewById(R.id.tvFat);
            holder.tvSnf = (TextView) convertView.findViewById(R.id.tvSnf);
            holder.tvMilType = (TextView) convertView.findViewById(R.id.tvMilkType);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvCode.setId(position);
        holder.tvTQty.setId(position);
        holder.tvARate.setId(position);
        holder.tvTAmount.setId(position);
        holder.tvSignature.setId(position);


        holder.tvCode.setText(String.valueOf(allAverageReportDetail.get(position).totalMember));
        holder.tvTQty.setText(String.valueOf(allAverageReportDetail.get(position).avgRate));
        holder.tvARate.setText(String.valueOf(allAverageReportDetail.get(position).totalQuantity));
        holder.tvTAmount.setText(String.valueOf(allAverageReportDetail.get(position).totalAmount));


        holder.tvFat.setVisibility(View.GONE);
        holder.tvSnf.setVisibility(View.GONE);
        holder.tvMilType.setVisibility(View.GONE);


        return convertView;

    }

    class ViewHolder {
        TextView tvCode, tvTQty, tvARate, tvTAmount, tvSignature, tvMilType, tvFat, tvSnf, tvTax;

    }
}
