package com.devapp.smartcc.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devapp.devmain.user.Util;
import com.devapp.smartcc.AgentDetailsActivity;
import com.devapp.smartcc.entities.AgentEntity;
import com.devApp.R;

import java.util.ArrayList;


/**
 * Created by Upendra on 10/4/2016.
 */
public class AgentListAdapter extends RecyclerView.Adapter<AgentListAdapter.AgentViewHolder>
        implements View.OnClickListener {


    ArrayList<AgentEntity> allAgentListEntity;
    Activity mActivity;
    RecyclerView recyclerView;

    public AgentListAdapter(ArrayList<AgentEntity> allAgentList, Activity activity, RecyclerView recView) {
        this.allAgentListEntity = allAgentList;
        this.mActivity = activity;
        this.recyclerView = recView;
    }


    @Override
    public AgentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.agent_details_item, null);
        view.setOnClickListener(this);
        return new AgentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AgentViewHolder holder, int position) {

        holder.tvAgentDetails.setText(allAgentListEntity.get(position).agentID);
        holder.tvAgentId.setText(allAgentListEntity.get(position).firstName);

    }

    @Override
    public int getItemCount() {
        return allAgentListEntity.size();
    }

    @Override
    public void onClick(View v) {

        int pos = recyclerView.getChildPosition(v);

        AgentEntity agentEntity = allAgentListEntity.get(pos);
        Util.displayErrorToast(String.valueOf(pos), mActivity);
        Intent intent = new Intent(mActivity, AgentDetailsActivity.class);
        intent.putExtra("AGENT_ENTITY", agentEntity);
        mActivity.startActivity(intent);


    }

    class AgentViewHolder extends RecyclerView.ViewHolder {
        TextView tvAgentId, tvAgentDetails;

        public AgentViewHolder(View itemView) {
            super(itemView);
            tvAgentId = (TextView) itemView.findViewById(R.id.agentId);
            tvAgentDetails = (TextView) itemView.findViewById(R.id.agentDetails);

        }
    }

}
