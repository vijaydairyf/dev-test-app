package com.devapp.devmain.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devApp.R;

import java.util.ArrayList;

/**
 * Created by Upendra on 12/6/2015.
 */
public class TotalPeriodicAdapter extends ArrayAdapter<ReportEntity> {


    LayoutInflater inflater;
    AmcuConfig amcuConfig;
    private Context mContext;
    private ArrayList<ReportEntity> allReportEntity;

    public TotalPeriodicAdapter(Context context, int resource, int textViewResourceId, ArrayList<ReportEntity> reportEntries) {
        super(context, resource, textViewResourceId, reportEntries);
        this.mContext = context;
        this.allReportEntity = reportEntries;
        amcuConfig = AmcuConfig.getInstance();

    }

    @Override
    public int getCount() {
        return allReportEntity.size();
    }

    @Override
    public ReportEntity getItem(int position) {
        return allReportEntity.get(position);
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
            holder.tvName = (TextView) convertView.findViewById(R.id.tvFarmerName);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.tvCode.setId(position);
        holder.tvTQty.setId(position);
        holder.tvARate.setId(position);
        holder.tvTAmount.setId(position);
        holder.tvSignature.setId(position);
        holder.tvMilType.setId(position);


        if (amcuConfig.getDisplayNameInReport()) {

            holder.tvName.setVisibility(View.VISIBLE);
            holder.tvName.setText(allReportEntity.get(position).farmerName);
            holder.tvARate.setVisibility(View.GONE);
            holder.tvSignature.setVisibility(View.GONE);

        } else {
            holder.tvName.setVisibility(View.GONE);
            holder.tvARate.setVisibility(View.VISIBLE);
            holder.tvSignature.setVisibility(View.VISIBLE);

        }

        holder.tvMilType.setVisibility(View.GONE);


        holder.tvCode.setText(allReportEntity.get(position).farmerId);
        holder.tvTQty.setText(String.valueOf(allReportEntity.get(position).quantity));
        holder.tvARate.setText(String.valueOf(allReportEntity.get(position).rate));
        holder.tvTAmount.setText(String.valueOf(allReportEntity.get(position).amount));
        //  holder.tvTAmount.setText(Util.getAmount(this.mContext, allReportEntity.get(position).amount, allReportEntity.get(position).bonus));

        holder.tvSignature.setText("---------");

        holder.tvFat.setVisibility(View.GONE);
        holder.tvSnf.setVisibility(View.GONE);


        return convertView;
    }

    class ViewHolder {
        TextView tvCode, tvTQty, tvARate, tvTAmount, tvSignature, tvMilType, tvFat, tvSnf, tvTax, tvName;

    }

}


