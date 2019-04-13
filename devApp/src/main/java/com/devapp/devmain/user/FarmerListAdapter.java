package com.devapp.devmain.user;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.FarmerEntity;
import com.devApp.R;

import java.util.List;

/**
 * Created by x on 6/3/18.
 */

public class FarmerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<? extends Entity> mEnities;
    private OnItemClickListener mItemClickListener;
    private OnItemLongLongClickListener mItemOnLongClickListener;

    public FarmerListAdapter(List<? extends Entity> entities) {
        mEnities = entities;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_farmerlist, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FarmerEntity entity = (FarmerEntity) mEnities.get(position);
        MyViewHolder vh = (MyViewHolder) holder;
        vh.farmerNameTV.setText(entity.farmer_name);
        vh.farmerIdTV.setText(entity.farmer_id);
        if (entity.farmerType != null) {
            if (entity.farmerType.equalsIgnoreCase(AppConstants.FARMER_TYPE_AGENT)) {
                vh.typeTV.setVisibility(View.VISIBLE);
                vh.typeTV.setText(AppConstants.FARMER_TYPE_AGENT);
            } else {
                vh.typeTV.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mEnities.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongLongClickListener listener) {
        mItemOnLongClickListener = listener;
    }

    public interface OnItemClickListener {
        public void onItemClicked(int position, Entity entity);
    }

    public interface OnItemLongLongClickListener {
        public boolean onItemLongClicked(int position, Entity entity, View view);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        public TextView farmerNameTV;
        public TextView farmerIdTV;
        public TextView typeTV;

        public MyViewHolder(View itemView) {
            super(itemView);
            farmerNameTV = (TextView) itemView.findViewById(R.id.tvFarmerName);
            farmerIdTV = (TextView) itemView.findViewById(R.id.tvFarmerId);
            typeTV = (TextView) itemView.findViewById(R.id.tvType);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClicked(getAdapterPosition(), mEnities.get(getAdapterPosition()));
            }
        }

        @Override
        public boolean onLongClick(View v) {
            if (mItemOnLongClickListener != null) {
                return mItemOnLongClickListener.onItemLongClicked(getAdapterPosition(), mEnities.get(getAdapterPosition()), v);
            }
            return false;
        }
    }
}
