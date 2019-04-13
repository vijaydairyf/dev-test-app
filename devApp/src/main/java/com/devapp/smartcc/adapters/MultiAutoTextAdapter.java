package com.devapp.smartcc.adapters;

/**
 * Created by u_pendra on 3/4/17.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.devApp.R;

import java.util.ArrayList;
import java.util.List;


public class MultiAutoTextAdapter extends ArrayAdapter<String> {
    private final Context mContext;
    private final List<String> allCenters;
    private final int mLayoutResourceId;
    private List<String> filterAllCenter;
    private List<String> publishCenter;
    private Activity mActivity;
    private LayoutInflater layoutInflator;

    public MultiAutoTextAdapter(Activity activity, Context context, int resource, List<String> allCenters) {
        super(context, resource, allCenters);
        this.mContext = context;
        this.mActivity = activity;
        this.mLayoutResourceId = R.layout.auto_text_item;
        this.allCenters = new ArrayList<>(allCenters);
        this.publishCenter = new ArrayList<>(allCenters);
        this.filterAllCenter = new ArrayList<>();
        layoutInflator = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return publishCenter.size();
    }

    public String getItem(int position) {
        return publishCenter.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                LayoutInflater inflater = mActivity.getLayoutInflater();
                convertView = inflater.inflate(mLayoutResourceId, parent, false);
            }
            String center = getItem(position);
            TextView name = (TextView) convertView.findViewById(R.id.tvData);
            name.setText(center);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return resultValue.toString();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                if (constraint != null) {
                    filterAllCenter.clear();
                    for (String center : allCenters) {
                        if (center.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filterAllCenter.add(center);
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = filterAllCenter;
                    filterResults.count = filterAllCenter.size();
                    return filterResults;
                } else if (constraint == null || constraint.toString().trim() == "") {
//                    FilterResults filterResults = new FilterResults();
//                    filterResults.values = allCenters;
//                    filterResults.count = allCenters.size();
                    return new FilterResults();
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, Filter.FilterResults results) {

                publishCenter.clear();
                if (results != null && results.count > 0) {
                    List<?> result = (List<?>) results.values;
                    for (Object object : result) {
                        if (object instanceof String) {
                            publishCenter.add(object.toString());
                        }
                    }
                } else if (constraint == null || constraint.equals("\n")) {


                }
                notifyDataSetInvalidated();
                // notifyDataSetChanged();

            }
        };
    }
}
