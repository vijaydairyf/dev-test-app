package com.devapp.devmain.milkline.entities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devapp.devmain.milkline.EnterTankerDetails;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.ArrayList;

/**
 * Created by u_pendra on 19/12/16.
 */

public class TankerAdapter extends RecyclerView.Adapter<TankerAdapter.TankerViewHolder>
        implements View.OnClickListener {


    ArrayList<TankerCollectionEntity> allTankerCollectionEntity;
    Activity mActivity;
    RecyclerView recycleView;

    SmartCCUtil smartCCUtil;

    public TankerAdapter(ArrayList<TankerCollectionEntity> allTankerCollectionEntity, Activity activity, RecyclerView rView) {
        this.allTankerCollectionEntity = allTankerCollectionEntity;
        this.mActivity = activity;
        this.recycleView = rView;
        smartCCUtil = new SmartCCUtil(mActivity);
    }


    @Override
    public TankerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.tanker_items, null);
        view.setOnClickListener(this);

        view.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        || ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    int pos = recycleView.getChildPosition(v);
                    Intent intent = new Intent(mActivity, EnterTankerDetails.class);
                    intent.putExtra("TANKER_ENTITY", allTankerCollectionEntity.get(pos));
                    mActivity.startActivity(intent);
                    return true;
                }
                return false;
            }
        });


        return new TankerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TankerViewHolder holder, final int position) {

        holder.tvDate.setText(smartCCUtil.getDateAndTimeFromMiliSecond(allTankerCollectionEntity.get(position).collectionTime));
        holder.tvTankerNumber.setText(allTankerCollectionEntity.get(position).tankerNumber);
        holder.tvRoute.setText(allTankerCollectionEntity.get(position).routeNumber);

        if (allTankerCollectionEntity.get(position).sent == 1) {
            holder.ivIcon.setBackgroundResource(R.drawable.sent);
        } else {
            holder.ivIcon.setBackgroundResource(R.drawable.unsent);
        }

        holder.cardRecyle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {

                if (isFocus) {
                    holder.rlView.setBackgroundResource(R.color.focused);
                } else {
                    holder.rlView.setBackgroundResource(R.color.white);
                }

            }
        });

        holder.cardRecyle.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        || ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    Intent intent = new Intent(mActivity, EnterTankerDetails.class);
                    intent.putExtra("TANKER_ENTITY", allTankerCollectionEntity.get(position));
                    mActivity.startActivity(intent);
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return allTankerCollectionEntity.size();
    }

    @Override
    public void onClick(View v) {
        int pos = recycleView.getChildPosition(v);

        TankerCollectionEntity tankerCollectionEntity = allTankerCollectionEntity.get(pos);
        Intent intent = new Intent(mActivity, EnterTankerDetails.class);
        intent.putExtra("TANKER_ENTITY", tankerCollectionEntity);
        mActivity.startActivity(intent);


    }

    class TankerViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvTankerNumber, tvRoute;
        ImageView ivIcon;
        RelativeLayout rlView;
        CardView cardRecyle;

        public TankerViewHolder(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvTankerNumber = (TextView) itemView.findViewById(R.id.tvTankerNumber);
            tvRoute = (TextView) itemView.findViewById(R.id.tvRouteNumber);
            ivIcon = (ImageView) itemView.findViewById(R.id.sendIcon);
            rlView = (RelativeLayout) itemView.findViewById(R.id.rlRecycle);
            cardRecyle = (CardView) itemView.findViewById(R.id.cardRecycle);

        }
    }

}