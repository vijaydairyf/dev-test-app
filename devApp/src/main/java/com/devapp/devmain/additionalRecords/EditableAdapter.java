package com.devapp.devmain.additionalRecords;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by u_pendra on 28/7/17.
 */

public class EditableAdapter extends ArrayAdapter<ReportEntity> implements Filterable {

    private ArrayList<ReportEntity> allReportList = null;
    private List<ReportEntity> filterReportList = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();


    public EditableAdapter(Context context, int resource, ArrayList<ReportEntity> reportList) {
        super(context, resource, reportList);
        this.allReportList = reportList;
        this.filterReportList = new ArrayList<>();
        this.filterReportList.addAll(reportList);
        this.allReportList = new ArrayList<>();
        this.allReportList.addAll(filterReportList);
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return filterReportList.size();
    }

    @Nullable
    @Override
    public ReportEntity getItem(int position) {
        return filterReportList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ItemFilter();
        }
        return mFilter;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.editable_list_item, null);
            viewHolder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
            viewHolder.tvId = (TextView) convertView.findViewById(R.id.tvId);
            viewHolder.tvShift = (TextView) convertView.findViewById(R.id.tvShift);
            convertView.setTag(viewHolder);
        }

        viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.tvShift.setText(SmartCCUtil.getAlternateShift(filterReportList.get(position).postShift));
        viewHolder.tvDate.setText(filterReportList.get(position).postDate);
        viewHolder.tvId.setText(filterReportList.get(position).farmerId);

        return convertView;
    }

    static class ViewHolder {
        TextView tvId, tvDate, tvShift;
    }


    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<ReportEntity> list = allReportList;

            int count = list.size();
            final ArrayList<ReportEntity> nlist = new ArrayList<ReportEntity>(count);

            String filterableString;

            if (filterString != null && !filterString.trim().isEmpty())

            {
                for (int i = 0; i < count; i++) {
                    filterableString = list.get(i).farmerId;
                    if (filterableString.toLowerCase().contains(filterString)) {
                        nlist.add(list.get(i));
                    }
                }

                results.values = nlist;
                results.count = nlist.size();
            } else {
                synchronized (this) {
                    results.values = allReportList;
                    results.count = allReportList.size();
                }
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filterReportList = (ArrayList<ReportEntity>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = filterReportList.size(); i < l; i++)
                add(filterReportList.get(i));
            notifyDataSetInvalidated();
        }
    }

}


