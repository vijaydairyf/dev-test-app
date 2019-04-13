package com.devapp.kmfcommon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devApp.R;

import java.util.ArrayList;


/**
 * Created by yyy on 11/2/16.
 */
public class ListAllDashboardAdapter extends RecyclerView.Adapter<ListAllDashboardAdapter.ViewHolder> {

    Context context;
    String page;
    private ArrayList<ListAllDataItem> groupList;

    public ListAllDashboardAdapter(Context context, ArrayList<ListAllDataItem> groupList) {
        this.groupList = groupList;
        this.context = context;
        this.page = page;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.summary_list_row, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ListAllDataItem listAllGroupItem = groupList.get(position);
        holder.truckNumber.setText(groupList.get(position).getTruckNumber());
        holder.quantity.setText(groupList.get(position).getQuantity());
        holder.doi.setText(groupList.get(position).getIssueDate());
        holder.shift.setText(groupList.get(position).getShift());
        holder.revenue.setText(groupList.get(position).getAmount());
        holder.purity.setText(groupList.get(position).getPurity());
        holder.milktype.setText(groupList.get(position).getMilkType());
        holder.avgFat.setText(groupList.get(position).getAvgFat());
        holder.avgSnf.setText(groupList.get(position).getAvgSnf());
        holder.avgRate.setText(groupList.get(position).getRate());
        holder.main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "display rate todo" + holder.revenue.getText().toString(), Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView truckNumber, quantity, doi, revenue, shift, avgFat, avgSnf, avgRate, purity, milktype;
        RelativeLayout main;

        public ViewHolder(View itemView) {
            super(itemView);
            main = (RelativeLayout) itemView.findViewById(R.id.row1);
            truckNumber = (TextView) itemView.findViewById(R.id.dashboard_row1);
            quantity = (TextView) itemView.findViewById(R.id.dashboard_row2);
            doi = (TextView) itemView.findViewById(R.id.dashboard_row3);
            shift = (TextView) itemView.findViewById(R.id.dashboard_row4);
            revenue = (TextView) itemView.findViewById(R.id.dashboard_row5);
            purity = (TextView) itemView.findViewById(R.id.dashboard_row6);
            milktype = (TextView) itemView.findViewById(R.id.dashboard_row7);
            avgFat = (TextView) itemView.findViewById(R.id.dashboard_row8);
            avgSnf = (TextView) itemView.findViewById(R.id.dashboard_row9);
            avgRate = (TextView) itemView.findViewById(R.id.dashboard_row10);
        }
    }
}
