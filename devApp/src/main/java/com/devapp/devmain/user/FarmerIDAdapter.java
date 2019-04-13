package com.devapp.devmain.user;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.devapp.devmain.entity.FarmerEntity;
import com.devApp.R;

import java.util.ArrayList;

/**
 * Created by y on 17/10/17.
 */

public class FarmerIDAdapter extends ArrayAdapter<FarmerEntity> {


    private final LayoutInflater mInflater;
    private final Context mContext;
    private final ArrayList<FarmerEntity> items;
    private final int mResource;


    public FarmerIDAdapter(Context context, int resource,
                           ArrayList<FarmerEntity> objects) {

        super(context, resource, 0, objects);

        mContext = context;
        mInflater = LayoutInflater.from(context);
        mResource = resource;
        items = objects;
    }

    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        final View view = mInflater.inflate(mResource, parent, false);

        TextView tvAgentname = (TextView) view.findViewById(R.id.title);

        FarmerEntity farmerEntity = items.get(position);

        tvAgentname.setText(farmerEntity.farmer_id);

        return view;
    }
}
