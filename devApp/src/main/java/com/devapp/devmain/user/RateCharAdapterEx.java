package com.devapp.devmain.user;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.RateChartNameDao;
import com.devapp.devmain.entity.EntityRateChart;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.EnterRCEntity;
import com.devApp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RateCharAdapterEx extends ArrayAdapter<EntityRateChart> {

    public static Boolean FilterProduct = true;
    public int loc;
    List<EntityRateChart> allBillEntry;
    Date VarifyDate;
    TextView tvNoproduct;
    int checkGroup;
    Spinner spin;
    EntityRateChart EntityRateChart;
    double dblBaseRate, dblSnfRateIncrease, dblFatRateIncrease;
    DecimalFormat decimalFormatAMT = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
    ArrayList<RateChartEntity> allRatechartEnt = new ArrayList<RateChartEntity>();
    double dblRateChangePerSNF = 0, dblRateChangePerFat = 0, dblTotalRate,
            dblSnfStart = 0, dblFatStart = 0, dblSnfEnd = 0, dblFatEnd = 0,
            dblRate = 0, dblFatDifference, dblSnfDifference;
    Handler myHandler = new Handler();
    Runnable updateRunnable;
    RelativeLayout progressLayout;
    boolean dataValidatioin;
    AmcuConfig amcuConfig;
    String rateChartName, milkType;
    private LayoutInflater inflater;
    private ArrayList<EntityRateChart> originalBillList;
    private List<EntityRateChart> data;
    private Context ctx;
    private int Count;
    private ComponentName startService;

    public RateCharAdapterEx(Activity context, int textViewResourceId,
                             List<EntityRateChart> mEntries, int count,
                             RelativeLayout progressBar) {
        super(context, textViewResourceId, mEntries);
        this.originalBillList = new ArrayList<EntityRateChart>();
        allBillEntry = mEntries;
        this.data = mEntries;
        checkGroup = textViewResourceId;
        this.originalBillList.addAll(data);
        this.progressLayout = progressBar;
        this.ctx = context;
        this.Count = count;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        amcuConfig = AmcuConfig.getInstance();

        rateChartName = amcuConfig.getRateChartName();
        milkType = amcuConfig.getMilkType();

    }

    @Override
    public int getCount() {
        return allBillEntry.size();
    }

    @Override
    public EntityRateChart getItem(int position) {
        return allBillEntry.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        final int pos = position;

        if (convertView == null) {

            holder = new ViewHolder();

            convertView = inflater.inflate(R.layout.rate_chart_item, null);

            holder.etBaseRate = (EditText) convertView
                    .findViewById(R.id.etBaseRateSet1);

            holder.etFatStart = (EditText) convertView
                    .findViewById(R.id.etFatFrom1);

            holder.etFatEnd = (EditText) convertView
                    .findViewById(R.id.etFatTo1);
            holder.etSnfStart = (EditText) convertView
                    .findViewById(R.id.etSnfFrom1);
            holder.etSnfEnd = (EditText) convertView
                    .findViewById(R.id.etSnfTo1);

            holder.etFatRateIn = (EditText) convertView
                    .findViewById(R.id.etRateChange1);
            holder.etSnfRateIn = (EditText) convertView
                    .findViewById(R.id.etRateChangeForSnf1);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.btnDone = (Button) convertView.findViewById(R.id.btnDone);
            holder.mProgressBar = (ProgressBar) convertView
                    .findViewById(R.id.progressBar);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();

        EntityRateChart etRateChart = new EntityRateChart();

        etRateChart = RateChartNew.allEntRateChart.get(position);
        holder.etBaseRate.setId(position);
        holder.etFatStart.setId(position);
        holder.etFatEnd.setId(position);
        holder.etFatRateIn.setId(position);
        holder.etSnfStart.setId(position);
        holder.etSnfEnd.setId(position);
        holder.etSnfRateIn.setId(position);

        holder.btnDone.setId(position);
        holder.mProgressBar.setId(position);

        holder.mProgressBar.setVisibility(View.GONE);

        holder.etBaseRate.setText(etRateChart.etBaseRate);
        holder.etFatEnd.setText(etRateChart.etFatEnd);
        holder.etFatRateIn.setText(etRateChart.etFatRateIn);
        holder.etFatStart.setText(etRateChart.etFatStart);
        holder.etSnfStart.setText(etRateChart.etSnfStart);
        holder.etSnfEnd.setText(etRateChart.etSnfEnd);
        holder.etSnfRateIn.setText(etRateChart.etSnfRateIn);
        holder.btnDone.setText(etRateChart.button);

        if (holder.btnDone.getText().toString().equalsIgnoreCase("Edit")) {
            holder.btnDone.setVisibility(View.GONE);
        } else {
            holder.btnDone.setVisibility(View.VISIBLE);
        }

        int pos1 = position + 1;
        holder.tvTitle.setText("Rate rule : " + pos1);

        EditValidation(holder.etBaseRate, holder.etFatStart, holder.etFatEnd,
                holder.etSnfStart, holder.etSnfEnd, holder.etFatRateIn,
                holder.etSnfRateIn);

        loc = position;

        convertView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        holder.etBaseRate.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        holder.btnDone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                addData(holder.etBaseRate, holder.etFatStart, holder.etFatEnd,
                        holder.etFatRateIn, holder.etSnfStart, holder.etSnfEnd,
                        holder.etSnfRateIn, holder.btnDone, pos,
                        holder.mProgressBar);

            }
        });

        return convertView;
    }

    public void EditValidation(EditText edit1, EditText edit2, EditText edit3,
                               EditText edit4, EditText edit5, EditText edit6, EditText edit7) {

        Util.alphabetValidation(edit1, Util.ONLY_DECIMAL, ctx, 6);
        Util.alphabetValidation(edit2, Util.ONLY_DECIMAL, ctx, 6);
        Util.alphabetValidation(edit3, Util.ONLY_DECIMAL, ctx, 6);
        Util.alphabetValidation(edit4, Util.ONLY_DECIMAL, ctx, 6);
        Util.alphabetValidation(edit5, Util.ONLY_DECIMAL, ctx, 6);
        Util.alphabetValidation(edit6, Util.ONLY_DECIMAL, ctx, 6);
        Util.alphabetValidation(edit7, Util.ONLY_DECIMAL, ctx, 6);

    }

    public void addData(EditText edit1, final EditText edit2,
                        final EditText edit3, EditText edit4, final EditText edit5,
                        final EditText edit6, EditText edit7, final Button btn,
                        final int pos, final ProgressBar progressBa) {

        final EntityRateChart etRateChart = new EntityRateChart();

        if (!edit1.getText().toString().equalsIgnoreCase("")) {
            dblBaseRate = Double.parseDouble(edit1.getText().toString());
        } else {
            edit1.requestFocus();
            showToast();
            hideProgressBar(progressBa);
            return;

        }

        if (!edit2.getText().toString().equalsIgnoreCase("")) {
            dblFatStart = Double.parseDouble(edit2.getText().toString());
        } else {
            edit2.requestFocus();
            showToast();
            hideProgressBar(progressBa);
            return;
        }

        if (!edit3.getText().toString().equalsIgnoreCase("")) {
            dblFatEnd = Double.parseDouble(edit3.getText().toString());
        } else {
            edit3.requestFocus();
            showToast();
            hideProgressBar(progressBa);
            return;
        }

        if (!edit4.getText().toString().equalsIgnoreCase("")) {
            dblFatRateIncrease = Double.parseDouble(edit4.getText().toString());
        } else {
            edit4.requestFocus();
            showToast();
            hideProgressBar(progressBa);
            return;
        }

        if (!edit5.getText().toString().equalsIgnoreCase("")) {
            dblSnfStart = Double.parseDouble(edit5.getText().toString());
        } else {
            edit5.requestFocus();
            showToast();
            hideProgressBar(progressBa);
            return;
        }

        if (!edit6.getText().toString().equalsIgnoreCase("")) {
            dblSnfEnd = Double.parseDouble(edit6.getText().toString());
        } else {
            edit6.requestFocus();
            showToast();
            hideProgressBar(progressBa);
            return;
        }

        if (!edit7.getText().toString().equalsIgnoreCase("")) {
            dblSnfRateIncrease = Double.parseDouble(edit7.getText().toString());
        } else {
            edit7.requestFocus();
            showToast();
            hideProgressBar(progressBa);
            return;
        }

        etRateChart.count = String.valueOf(pos);
        etRateChart.etBaseRate = edit1.getText().toString();
        etRateChart.etFatStart = edit2.getText().toString();
        etRateChart.etFatEnd = edit3.getText().toString();
        etRateChart.etFatRateIn = edit4.getText().toString();
        etRateChart.etSnfStart = edit5.getText().toString();
        etRateChart.etSnfEnd = edit6.getText().toString();
        etRateChart.etSnfRateIn = edit7.getText().toString();

        Toast.makeText(ctx, "Please wait..", Toast.LENGTH_SHORT).show();
        showProgressBar(progressBa);

        if (DataValidation(etRateChart, edit2, edit3, edit6, edit5, ctx)) {

            if (!etRateChart.etFatStart.replace(" ", "").equalsIgnoreCase("")) {
                dblFatStart = Double.parseDouble(etRateChart.etFatStart);
                if (dblFatStart > 14) {
                    Toast.makeText(ctx,
                            "Max FAT value should be less than 14!",
                            Toast.LENGTH_SHORT).show();
                    hideProgressBar(progressBa);
                    return;
                }

            } else {

                Toast.makeText(ctx, "Please fill the details",
                        Toast.LENGTH_SHORT).show();
                hideProgressBar(progressBa);
                return;
            }

            if (!etRateChart.etSnfStart.replace(" ", "").equalsIgnoreCase("")) {
                dblSnfStart = Double.parseDouble(etRateChart.etSnfStart);
                if (dblSnfStart > 14) {
                    Toast.makeText(ctx,
                            "Max SNF value should be less than 14!",
                            Toast.LENGTH_SHORT).show();
                    hideProgressBar(progressBa);

                    return;
                }

            } else {
                Toast.makeText(ctx, "Please fill the details",
                        Toast.LENGTH_SHORT).show();

                return;
            }

            if (!etRateChart.etBaseRate.replace(" ", "").equalsIgnoreCase("")) {
                dblRate = Double.parseDouble(etRateChart.etBaseRate);
            } else {
                Toast.makeText(ctx, "Please fill the details",
                        Toast.LENGTH_SHORT).show();
                hideProgressBar(progressBa);
                return;
            }

            if (etRateChart.etFatEnd.replace(" ", "").equalsIgnoreCase("")) {

                dblFatEnd = dblFatStart;

            } else {
                dblFatEnd = Double.parseDouble(etRateChart.etFatEnd);

                if (dblFatEnd > 14) {
                    Toast.makeText(ctx,
                            "Max Fat value should be less than 14!",
                            Toast.LENGTH_SHORT).show();
                    hideProgressBar(progressBa);

                    return;
                }
            }
            if (etRateChart.etSnfEnd.replace(" ", "").equalsIgnoreCase("")) {
                dblSnfEnd = dblSnfStart;

            } else {
                dblSnfEnd = Double.parseDouble(etRateChart.etSnfEnd);

                if (dblSnfEnd > 14) {
                    Toast.makeText(ctx,
                            "Max SNF value should be less than 14!",
                            Toast.LENGTH_SHORT).show();
                    hideProgressBar(progressBa);

                    return;
                }
            }
            if (etRateChart.etSnfRateIn.replace(" ", "").equalsIgnoreCase("")) {
                dblRateChangePerSNF = 0;

            } else {
                dblRateChangePerSNF = Double
                        .parseDouble(etRateChart.etSnfRateIn);
            }
            if (etRateChart.etFatRateIn.replace(" ", "").equalsIgnoreCase("")) {
                dblRateChangePerFat = 0;
            } else {
                dblRateChangePerFat = Double
                        .parseDouble(etRateChart.etFatRateIn);
            }

            dblTotalRate = dblRate;

            dblFatDifference = dblFatEnd - dblFatStart;
            dblSnfDifference = dblSnfEnd - dblSnfStart;

            new Thread(new Runnable() {

                @Override
                public void run() {

                    dataValidatioin = setRateChartList(pos);

                    myHandler.post(updateRunnable);

                }
            }).start();

            updateRunnable = new Runnable() {

                @Override
                public void run() {

                    if (dataValidatioin) {

                        if (etRateChart.button.equalsIgnoreCase("Done")
                                || btn.getText().toString()
                                .equalsIgnoreCase("Done")) {
                            etRateChart.button = "Edit";
                            btn.setText("Edit");
                            btn.setVisibility(View.GONE);
                        } else {
                            etRateChart.button = "Done";
                            btn.setText("Done");
                        }

                        dataValidatioin = false;
                        RateChartNew.allEntRateChart.set(pos, etRateChart);

                        Toast.makeText(ctx, "Validation done.",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(ctx, "Please check the values.",
                                Toast.LENGTH_SHORT).show();
                    }

                    hideProgressBar(progressBa);
                }
            };

        } else {
            hideProgressBar(progressBa);
        }

    }

    public void showToast() {
        Toast.makeText(ctx, "Please fill all the details!", Toast.LENGTH_SHORT)
                .show();
    }

    public boolean setRateChartList(int cont) {

        boolean rateChartCHK = true;

        RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);
        DecimalFormat decimalFormat;
        ArrayList<RateChartEntity> tempRateCharEnt = new ArrayList<RateChartEntity>();
        decimalFormat = new DecimalFormat("##.00");

        int snfCount = 0, fatCount = 0;
        double snfIncrease = 0, snfRate = 0;

        long refId = rateChartNameDao.findRateRefIdFromName(rateChartName);

        if (dblFatDifference >= 0 && dblSnfDifference >= 0) {

            for (double i = dblFatStart; i <= dblFatEnd; ) {

                snfCount = 0;

                snfIncrease = fatCount * dblRateChangePerFat;

                snfRate = dblRate + snfIncrease;

                for (double k = dblSnfStart; k <= dblSnfEnd; ) {

                    dblTotalRate = snfRate + (snfCount * dblRateChangePerSNF);

                    dblTotalRate = Double.valueOf(decimalFormat
                            .format(dblTotalRate));

                    RateChartEntity RCEntity = new RateChartEntity();
                    RCEntity.snf = Double.valueOf(decimalFormat.format(k));
                    RCEntity.fat = Double.valueOf(decimalFormat.format(i));

                    RCEntity.societyId = String.valueOf(new SessionManager(ctx)
                            .getSocietyColumnId());
                    RCEntity.milkType = milkType;
                    RCEntity.managerID = new SessionManager(ctx)
                            .getManagerEmailID();
                    RCEntity.rate = dblTotalRate;
                    RCEntity.farmerId = "All farmers";
                    RCEntity.rateReferenceId = refId;

                    RateChartNew.allDblSnf.add(Double.valueOf(decimalFormat
                            .format(k)));
                    RateChartNew.allDblSnf.add(Double.valueOf(decimalFormat
                            .format(dblSnfEnd)));

                    tempRateCharEnt.add(RCEntity);

                    k = k + 0.1;

                    k = Double.valueOf(decimalFormat.format(k));
                    snfCount = snfCount + 1;

                    allRatechartEnt.add(RCEntity);

                }
                RateChartNew.allDblFat.add(Double.valueOf(decimalFormat
                        .format(i)));

                RateChartNew.allDblFat.add(Double.valueOf(decimalFormat
                        .format(dblFatEnd)));

                i = i + 0.1;

                i = Double.valueOf(decimalFormat.format(i));
                fatCount = fatCount + 1;
            }
        } else {

            rateChartCHK = false;
            return rateChartCHK;
        }

        if (rateChartCHK) {
            RateChartNew.MixedList.put(cont, tempRateCharEnt);

            Util.WriteRateChartEnt(ctx, allRatechartEnt, 0);
            Intent i = new Intent(ctx, EnterRCEntity.class);
            ctx.startService(i);
            allRatechartEnt = new ArrayList<RateChartEntity>();
        }

        return rateChartCHK;

    }

    public boolean DataValidation(EntityRateChart entRateChart, EditText edit1,
                                  EditText edit2, EditText edit3, EditText edit4, Context context) {

        boolean datavalid = true;

        ArrayList<EntityRateChart> allEntRate = RateChartNew.allEntRateChart;
        double dblFatStart = Double.parseDouble(entRateChart.etFatStart);
        double dblFatEnd = Double.parseDouble(entRateChart.etFatEnd);
        double dblSnfStart = Double.parseDouble(entRateChart.etSnfStart);
        double dblSnfEnd = Double.parseDouble(entRateChart.etSnfEnd);

        if (allEntRate != null && allEntRate.size() > 1) {
            for (int i = 0; i < allEntRate.size() - 1; i++) {

                if (dblFatStart >= Double
                        .parseDouble(allEntRate.get(i).etFatStart)
                        && dblFatStart <= Double
                        .parseDouble(allEntRate.get(i).etFatEnd)) {

                    if ((dblSnfStart >= Double
                            .parseDouble(allEntRate.get(i).etSnfStart) && dblSnfStart <= Double
                            .parseDouble(allEntRate.get(i).etSnfEnd))
                            || (dblSnfEnd >= Double.parseDouble(allEntRate
                            .get(i).etSnfStart) && dblSnfEnd <= Double
                            .parseDouble(allEntRate.get(i).etSnfEnd))) {
                        Toast.makeText(ctx, "Please check the overlap values!",
                                Toast.LENGTH_SHORT).show();

                        edit1.setText("");
                        edit2.setText("");
                        edit3.setText("");
                        edit4.setText("");

                        edit1.requestFocus();

                        return false;
                    }
                } else if (dblFatEnd >= Double
                        .parseDouble(allEntRate.get(i).etFatStart)
                        && dblFatEnd <= Double
                        .parseDouble(allEntRate.get(i).etFatEnd)) {

                    if ((dblSnfStart >= Double
                            .parseDouble(allEntRate.get(i).etSnfStart) && dblSnfStart <= Double
                            .parseDouble(allEntRate.get(i).etSnfEnd))
                            || (dblSnfEnd >= Double.parseDouble(allEntRate
                            .get(i).etSnfStart) && dblSnfEnd <= Double
                            .parseDouble(allEntRate.get(i).etSnfEnd))) {
                        Toast.makeText(ctx, "Please check the overlap values!",
                                Toast.LENGTH_SHORT).show();

                        edit1.setText("");
                        edit2.setText("");
                        edit3.setText("");
                        edit4.setText("");

                        edit1.requestFocus();

                        return false;
                    }
                } else
                    return true;
            }

        } else {
            datavalid = true;
        }

        return datavalid;

    }

    private void showProgressBar(ProgressBar mProgressBar) {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(ProgressBar mProgressBar) {
        mProgressBar.setVisibility(View.GONE);
    }

    class ViewHolder {

        EditText etBaseRate, etFatStart, etFatEnd, etFatRateIn, etSnfStart,
                etSnfEnd, etSnfRateIn;

        Button btnDone;

        TextView tvTitle;

        ProgressBar mProgressBar;

    }

    private class GenericTextWatcher implements TextWatcher {

        private View view;
        private int CountPos;

        private GenericTextWatcher(View view, int pos) {
            this.view = view;
            this.CountPos = pos;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                      int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1,
                                  int i2) {
        }

        public void afterTextChanged(Editable editable) {
            final int position = view.getId();
            final EditText editText = (EditText) view;

            if (!editText.getText().toString().equalsIgnoreCase("")) {

            } else {
                editText.requestFocus();
            }

        }
    }
}
