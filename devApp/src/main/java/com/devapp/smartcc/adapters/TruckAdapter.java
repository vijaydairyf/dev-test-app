package com.devapp.smartcc.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devapp.devmain.user.Util;
import com.devapp.smartcc.UpdateTruckDetails;
import com.devapp.smartcc.entities.TrucKInfo;
import com.devApp.R;

import java.util.ArrayList;


/**
 * Created by Upendra on 10/4/2016.
 */
public class TruckAdapter extends RecyclerView.Adapter<TruckAdapter.TruckViewHolder> implements View.OnClickListener {


    ArrayList<TrucKInfo> allTruckDetailsEntity;
    Activity mActivity;
    RecyclerView recycleView;

    public TruckAdapter(ArrayList<TrucKInfo> allTruckEntity, Activity activity, RecyclerView rView) {
        this.allTruckDetailsEntity = allTruckEntity;
        this.mActivity = activity;
        this.recycleView = rView;
    }


    @Override
    public TruckViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.truck_details_items, null);
        view.setOnClickListener(this);
        return new TruckViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TruckViewHolder holder, int position) {

        holder.tvTruckDetails.setText(allTruckDetailsEntity.get(position).truckName);
        holder.tvTruckId.setText(allTruckDetailsEntity.get(position).truckId);

    }

    @Override
    public int getItemCount() {
        return allTruckDetailsEntity.size();
    }

    @Override
    public void onClick(View v) {

        int pos = recycleView.getChildPosition(v);

        TrucKInfo truckInfo = allTruckDetailsEntity.get(pos);
        Util.displayErrorToast(String.valueOf(pos), mActivity);
        Intent intent = new Intent(mActivity, UpdateTruckDetails.class);
        intent.putExtra("TRUCK_DETAILS", truckInfo);
        mActivity.startActivity(intent);


    }

    class TruckViewHolder extends RecyclerView.ViewHolder {
        TextView tvTruckId, tvTruckDetails;

        public TruckViewHolder(View itemView) {
            super(itemView);

            tvTruckId = (TextView) itemView.findViewById(R.id.truckId);
            tvTruckDetails = (TextView) itemView.findViewById(R.id.truckDetails);

        }
    }

}
