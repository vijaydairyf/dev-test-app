package com.devapp.devmain.multipleequipments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devApp.R;

import java.util.ArrayList;

/**
 * Created by Upendra on 6/7/2016.
 */
public class SampleMilkAdapter extends ArrayAdapter<MilkAnalyserEntity> {

    private Context mContext;
    private ArrayList<MilkAnalyserEntity> allMilkAnalyserEntity;

    public SampleMilkAdapter(Context context, int resource, int textViewResourceId, ArrayList<MilkAnalyserEntity> allMAEntity) {
        super(context, resource, textViewResourceId, allMAEntity);
        this.mContext = context;
        this.allMilkAnalyserEntity = allMAEntity;
    }


    @Override
    public int getCount() {
        return allMilkAnalyserEntity.size();
    }

    @Override
    public MilkAnalyserEntity getItem(int position) {
        return allMilkAnalyserEntity.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sample_record_item, null);
            holder = new ViewHolder();

            holder.etCalibration = (EditText) convertView.findViewById(R.id.etCalibration);
            holder.etClr = (EditText) convertView.findViewById(R.id.etClr);
            holder.etDate = (EditText) convertView.findViewById(R.id.etDate);
            holder.etFat = (EditText) convertView.findViewById(R.id.etMAFAT);
            holder.etSnf = (EditText) convertView.findViewById(R.id.etMASNF);
            holder.etTime = (EditText) convertView.findViewById(R.id.etTime);
            holder.etAddedWater = (EditText) convertView.findViewById(R.id.etAddedWater);
            holder.etLactose = (EditText) convertView.findViewById(R.id.etLactose);
            holder.etSolution = (EditText) convertView.findViewById(R.id.etSolution);
            holder.etTemperature = (EditText) convertView.findViewById(R.id.etTemp);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.etCalibration.setText(allMilkAnalyserEntity.get(position).calibration);
        holder.etClr.setText(String.valueOf(allMilkAnalyserEntity.get(position).clr));
        holder.etDate.setText(String.valueOf(allMilkAnalyserEntity.get(position).date));
        holder.etTime.setText(allMilkAnalyserEntity.get(position).time);
        holder.etFat.setText(String.valueOf(allMilkAnalyserEntity.get(position).fat));
        holder.etSnf.setText(String.valueOf(allMilkAnalyserEntity.get(position).snf));
        // holder.etSolution.setText(allMilkAnalyserEntity.get(position).protein);
        return convertView;
    }

    class ViewHolder {

        EditText etFat, etSnf, etClr, etCalibration, etDate, etTime, etLactose, etSolution, etAddedWater, etProtein, etTemperature;
    }
}
