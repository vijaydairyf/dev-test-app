package com.devapp.smartcc.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.report.ReportHintConstant;
import com.devApp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by u_pendra on 28/3/17.
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ReportHolder>
        implements View.OnClickListener {


    public static int SHORT_VIEW = 1;
    public static int DETAIL_VIEW = 2;
    public static int MEMBER_REPORT_VIEW = 3;
    ChooseDecimalFormat chooseDecimalFormat;
    DecimalFormat decimalFormatClr;
    DecimalFormat decimalFormatFat;
    DecimalFormat decimalFormatSNF;
    DecimalFormat decimalFormatProtein;
    DecimalFormat decimalFormat;
    SessionManager sessionManager;
    private Activity mActivity;
    private ArrayList<ReportEntity> mAllRepEntity;
    private int mReportType;
    private SmartCCUtil smartCCUtil;
    private Context mContext;
    private ValidationHelper validationHelper;

    public ReportAdapter(ArrayList<ReportEntity> allReportEntity, int reportType, Context context) {

        this.mAllRepEntity = allReportEntity;
        this.mReportType = reportType;
        mContext = context;
        chooseDecimalFormat = new ChooseDecimalFormat();
        validationHelper = new ValidationHelper();
        decimalFormat = new DecimalFormat("#0.00");
        sessionManager = new SessionManager(mContext);

        decimalFormatFat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(com.devapp.devmain.agentfarmersplit.AppConstants.FAT);
        decimalFormatSNF = chooseDecimalFormat.getDecimalFormatTypeForDisplay(com.devapp.devmain.agentfarmersplit.AppConstants.SNF);
        decimalFormatProtein = chooseDecimalFormat.getDecimalFormatTypeForDisplay(com.devapp.devmain.agentfarmersplit.AppConstants.PROTEIN);

    }


    @Override
    public ReportHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        this.smartCCUtil = new SmartCCUtil(parent.getContext());
        View view = layoutInflater.inflate(R.layout.short_report_item, parent, false);
        if (mReportType == SHORT_VIEW) {
            //Display MCC report
            view = layoutInflater.inflate(R.layout.short_report_item, parent, false);
        } else if (mReportType == DETAIL_VIEW) {
            //Display MCC report detailed view
            view = layoutInflater.inflate(R.layout.detail_report_item, parent, false);
        } else if (mReportType == MEMBER_REPORT_VIEW) {
            view = layoutInflater.inflate(R.layout.all_member_report_item, parent, false);
        }
        view.setOnClickListener(this);
        return new ReportHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReportHolder holder, int position) {


        if (mReportType == SHORT_VIEW) {

            holder.tvMCCId.setText(mAllRepEntity.get(position).farmerId);
            holder.tvRouteId.setText(mAllRepEntity.get(position).centerRoute);
            holder.tvDate.setText(Util.getDateDDMMYY(mAllRepEntity.get(position).postDate, 0));
            holder.tvFat.setText(String.valueOf(mAllRepEntity.get(position).getDisplayFat()));
            holder.tvSnf.setText(String.valueOf(mAllRepEntity.get(position).getDisplaySnf()));
            holder.tvQty.setText(String.valueOf(mAllRepEntity.get(position).getPrintAndReportQuantity()));
            holder.tvHClr.setText(String.valueOf(mAllRepEntity.get(position).getDisplayClr()));

            if (AppConstants.IS_SELECTED_AGGERATE_FARMER) {
                holder.tvRate.setText(String.valueOf(mAllRepEntity.get(position).getDisplayClr()));

            } else {
                holder.tvRate.setText(String.valueOf(mAllRepEntity.get(position).getDisplayRate()));

            }
            holder.tvAmount.setText(String.valueOf(Util.getAmount(mContext, mAllRepEntity.get(position).getTotalAmount(),
                    mAllRepEntity.get(position).bonus)));

            // setAnimation(holder.linearView,0);
        } else if (mReportType == DETAIL_VIEW) {

            smartCCUtil.toSetDrawableOnText(holder.tvMCCId, ReportHintConstant.MCC);
            smartCCUtil.toSetDrawableOnText(holder.tvRouteId, ReportHintConstant.ROUTE);
            smartCCUtil.toSetDrawableOnText(holder.tvMCCName, ReportHintConstant.MCC_NAME);
            smartCCUtil.toSetDrawableOnText(holder.tvSampleNum, ReportHintConstant.SAMPLE_NUM);
            smartCCUtil.toSetDrawableOnText(holder.tvNoc, ReportHintConstant.N_O_C);

            holder.tvMCCId.setText(mAllRepEntity.get(position).farmerId);
            holder.tvRouteId.setText(mAllRepEntity.get(position).centerRoute);
            holder.tvMCCName.setText(mAllRepEntity.get(position).farmerName);
            holder.tvSampleNum.setText(String.valueOf(mAllRepEntity.get(position).sampleNumber));
            holder.tvNoc.setText(String.valueOf(mAllRepEntity.get(position).numberOfCans));

            smartCCUtil.toSetDrawableOnText(holder.tvDate, ReportHintConstant.DATE);
            smartCCUtil.toSetDrawableOnText(holder.tvShift, ReportHintConstant.SHIFT);
            smartCCUtil.toSetDrawableOnText(holder.tvCollTime, ReportHintConstant.COLL_TIME);
            smartCCUtil.toSetDrawableOnText(holder.tvQualityTime, ReportHintConstant.QUALITY_TIME);
            smartCCUtil.toSetDrawableOnText(holder.tvQuantityTime, ReportHintConstant.QUANTITY_TIME);

            holder.tvDate.setText(Util.getDateDDMMYY(mAllRepEntity.get(position).postDate, 0));

            holder.tvShift.setText(SmartCCUtil.getAlternateShift(mAllRepEntity.get(position).postShift));
            holder.tvCollTime.setText(mAllRepEntity.get(position).time);
            if (mAllRepEntity.get(position).milkAnalyserTime == 0) {
                holder.tvQualityTime.setText("NA");
            } else {
                holder.tvQualityTime.setText(smartCCUtil.getTimeFromMiliSecond(mAllRepEntity.get(position).milkAnalyserTime));
            }
            holder.tvQuantityTime.setText(
                    smartCCUtil.getTimeFromMiliSecond(mAllRepEntity.get(position).weighingTime));
            smartCCUtil.toSetDrawableOnText(holder.tvFat, ReportHintConstant.FAT);
            smartCCUtil.toSetDrawableOnText(holder.tvSnf, ReportHintConstant.SNF);
            smartCCUtil.toSetDrawableOnText(holder.tvKQty, ReportHintConstant.QTY_KG);
            smartCCUtil.toSetDrawableOnText(holder.tvLtQty, ReportHintConstant.QTY_LTRS);
            smartCCUtil.toSetDrawableOnText(holder.tvRate, ReportHintConstant.RATE);
            smartCCUtil.toSetDrawableOnText(holder.tvAmount, ReportHintConstant.AMOUNT);

            holder.tvFat.setText(String.valueOf(mAllRepEntity.get(position).getDisplayFat()));
            holder.tvSnf.setText(String.valueOf(mAllRepEntity.get(position).getDisplaySnf()));
            holder.tvKQty.setText(String.valueOf(mAllRepEntity.get(position).getPrintAndReportKgQuantity()));
            holder.tvLtQty.setText(String.valueOf(mAllRepEntity.get(position).getPrintAndReportLtQuantity()));
            if (AppConstants.IS_SELECTED_AGGERATE_FARMER) {
                holder.tvRate.setText(String.valueOf(mAllRepEntity.get(position).getDisplayClr()));
            } else {
                holder.tvRate.setText(String.valueOf(mAllRepEntity.get(position).getDisplayRate()));

            }
            smartCCUtil.toSetDrawableOnText(holder.tvSnfKg, ReportHintConstant.FAT_KG);
            smartCCUtil.toSetDrawableOnText(holder.tvFatKg, ReportHintConstant.SNF_KG);
            if (!AmcuConfig.getInstance().getKeyAllowDisplayRate()) {
                holder.tvSnfKg.setText(decimalFormat.format(mAllRepEntity.get(position).snfKg));
                holder.tvFatKg.setText(decimalFormat.format(mAllRepEntity.get(position).fatKg));
            }
            holder.tvAmount.setText(String.valueOf(Util.getAmount(mContext, mAllRepEntity.get(position).getTotalAmount(),
                    mAllRepEntity.get(position).bonus)));

            //  setAnimation(holder.linearView,0);
        } else if (mReportType == MEMBER_REPORT_VIEW) {
            holder.tvMemberId.setText(mAllRepEntity.get(position).farmerId);
            holder.tvCattleType.setText(String.valueOf(mAllRepEntity.get(position).milkType.charAt(0)));
            holder.tvDate.setText(Util.getDateDDMMYY(mAllRepEntity.get(position).postDate, 0));
            holder.tvShift.setText(SmartCCUtil.getAlternateShift(mAllRepEntity.get(position).postShift));
            holder.tvCollTime.setText(mAllRepEntity.get(position).time);

            holder.tvFat.setText(decimalFormatFat.format(mAllRepEntity.get(position).getDisplayFat()));
            holder.tvSnf.setText(decimalFormatSNF.format(mAllRepEntity.get(position).getDisplaySnf()));
            holder.tvQty.setText(String.valueOf(mAllRepEntity.get(position).getPrintAndReportQuantity()));
            holder.tvHClr.setText(String.valueOf(mAllRepEntity.get(position).getDisplayClr()));

            if (AmcuConfig.getInstance().getKeyAllowProteinValue()) {
                holder.tvIncentive.setText(String.valueOf(mAllRepEntity.get(position).incentiveAmount));
                holder.tvProtein.setText(decimalFormatProtein.format(mAllRepEntity.get(position).protein));
                holder.tvAmount.setText(String.valueOf(mAllRepEntity.get(position).getTotalAmount()));


            } else {
                holder.tvAmount.setText(String.valueOf(Util.getAmount(mContext, mAllRepEntity.get(position).getTotalAmount(),
                        mAllRepEntity.get(position).bonus)));

            }


            //Agent WS Collection incomplete records
            if (sessionManager.getMCCStatus()) {
                if (!sessionManager.getRecordStatusComplete()) {
                    holder.tvFat.setText("NA");
                    holder.tvSnf.setText("NA");
                    holder.tvIncentive.setText("NA");
                    holder.tvProtein.setText("NA");
                    holder.tvAmount.setText("NA");
                }
            }


            if (!AmcuConfig.getInstance().getKeyAllowDisplayRate()) {
                holder.tvSnfKg.setText(decimalFormat.format(mAllRepEntity.get(position).snfKg));
                holder.tvFatKg.setText(decimalFormat.format(mAllRepEntity.get(position).fatKg));
            }
            if (AppConstants.IS_SELECTED_AGGERATE_FARMER) {

                holder.tvRate.setText(String.valueOf(mAllRepEntity.get(position).clr));
            } else {
                holder.tvRate.setText(String.valueOf(mAllRepEntity.get(position).getDisplayRate()));

            }
            //  holder.tvAmount.setText(mAllRepEntity.get(position).amount);
        }

        holder.cardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocus) {

                if (isFocus) {
                    holder.linearView.setBackgroundResource(R.color.focused);
                } else {
                    holder.linearView.setBackgroundResource(R.color.white);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return mAllRepEntity.size();
    }

    @Override
    public void onClick(View view) {


    }

    public void setViewType(int viewType) {
        mReportType = viewType;
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (mReportType == SHORT_VIEW) {
            Animation animation = AnimationUtils.loadAnimation(mActivity, android.R.anim.slide_in_left);
            animation.setDuration(500);
            animation.setBackgroundColor(mActivity.getResources().getColor(R.color.green));
            viewToAnimate.startAnimation(animation);
        } else if (mReportType == DETAIL_VIEW) {
            Animation animation = AnimationUtils.loadAnimation(mActivity, android.R.anim.slide_out_right);
            animation.setDuration(500);
            animation.setBackgroundColor(mActivity.getResources().getColor(R.color.green));
            viewToAnimate.startAnimation(animation);
        }


    }

    public void updateList(ArrayList<ReportEntity> list) {
        mAllRepEntity = list;
    }

    class ReportHolder extends RecyclerView.ViewHolder {

        private TextView tvRouteId, tvMCCId, tvMCCName, tvSampleNum,
                tvDate, tvShift, tvCollTime, tvQuantityTime, tvQualityTime,
                tvFat, tvSnf, tvLtQty, tvKQty, tvRate, tvAmount, tvQty, tvNoc, tvIncentive, tvProtein, tvFatKg, tvSnfKg, tvHClr;
        private CardView cardView;

        private TextView tvMemberId, tvCattleType;

        private LinearLayout linearView;

        public ReportHolder(View itemView) {
            super(itemView);

            if (SHORT_VIEW == mReportType) {
                tvMCCId = (TextView) itemView.findViewById(R.id.tvMccId);
                tvRouteId = (TextView) itemView.findViewById(R.id.tvRouteId);
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);
                tvFat = (TextView) itemView.findViewById(R.id.tvFat);
                tvSnf = (TextView) itemView.findViewById(R.id.tvSnf);
                tvHClr = (TextView) itemView.findViewById(R.id.tvClr);
                tvQty = (TextView) itemView.findViewById(R.id.tvQty);
                tvRate = (TextView) itemView.findViewById(R.id.tvRate);
                tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
                linearView = (LinearLayout) itemView.findViewById(R.id.linearView);
                cardView = (CardView) itemView.findViewById(R.id.cardView);
                if (AmcuConfig.getInstance().getCollectionFATSNFCLR().equalsIgnoreCase("FC")) {
                    tvSnf.setVisibility(View.GONE);
                    tvHClr.setVisibility(View.VISIBLE);
                } else {
                }

            } else if (mReportType == DETAIL_VIEW) {

                tvRouteId = (TextView) itemView.findViewById(R.id.tvRouteId);
                tvMCCId = (TextView) itemView.findViewById(R.id.tvMCCId);
                tvMCCName = (TextView) itemView.findViewById(R.id.tvMCCName);
                tvSampleNum = (TextView) itemView.findViewById(R.id.tvSampleNum);
                tvDate = (TextView) itemView.findViewById(R.id.tvDate);
                tvShift = (TextView) itemView.findViewById(R.id.tvShift);
                tvCollTime = (TextView) itemView.findViewById(R.id.tvCollTime);
                tvQuantityTime = (TextView) itemView.findViewById(R.id.tvQuantityTime);
                tvQualityTime = (TextView) itemView.findViewById(R.id.tvQualityTime);
                tvFat = (TextView) itemView.findViewById(R.id.tvFat);
                tvSnf = (TextView) itemView.findViewById(R.id.tvSnf);
                tvLtQty = (TextView) itemView.findViewById(R.id.tvLtQty);
                tvKQty = (TextView) itemView.findViewById(R.id.tvKQty);
                tvRate = (TextView) itemView.findViewById(R.id.tvRate);
                tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
                linearView = (LinearLayout) itemView.findViewById(R.id.linearView);
                tvNoc = (TextView) itemView.findViewById(R.id.tvNoc);
                cardView = (CardView) itemView.findViewById(R.id.cardView);
                tvFatKg = (TextView) itemView.findViewById(R.id.tvFatkg);
                tvSnfKg = (TextView) itemView.findViewById(R.id.tvSnfkg);

                if (AmcuConfig.getInstance().getKeyAllowDisplayRate()) {
                    tvSnfKg.setVisibility(View.GONE);
                    tvFatKg.setVisibility(View.GONE);
                    tvAmount.setVisibility(View.VISIBLE);
                    tvRate.setVisibility(View.VISIBLE);
                } else {
                    tvSnfKg.setVisibility(View.VISIBLE);
                    tvFatKg.setVisibility(View.VISIBLE);
                    tvAmount.setVisibility(View.GONE);
                    tvRate.setVisibility(View.GONE);

                }

            } else if (mReportType == MEMBER_REPORT_VIEW) {
                tvMemberId = (TextView) itemView.findViewById(R.id.tvHMemberId);
                tvDate = (TextView) itemView.findViewById(R.id.tvHDate);
                tvCollTime = (TextView) itemView.findViewById(R.id.tvHTime);
                tvShift = (TextView) itemView.findViewById(R.id.tvHShift);
                tvCattleType = (TextView) itemView.findViewById(R.id.tvHType);
                tvQty = (TextView) itemView.findViewById(R.id.tvHQty);
                tvRate = (TextView) itemView.findViewById(R.id.tvHRate);
                tvAmount = (TextView) itemView.findViewById(R.id.tvHAmount);
                tvFat = (TextView) itemView.findViewById(R.id.tvHFat);
                tvSnf = (TextView) itemView.findViewById(R.id.tvHSnf);
                linearView = (LinearLayout) itemView.findViewById(R.id.linearView);
                cardView = (CardView) itemView.findViewById(R.id.cardView);
                tvIncentive = (TextView) itemView.findViewById(R.id.tvIncentive);
                tvProtein = (TextView) itemView.findViewById(R.id.tvProtein);
                tvFatKg = (TextView) itemView.findViewById(R.id.tvHFatKg);
                tvSnfKg = (TextView) itemView.findViewById(R.id.tvHSnfKg);
                tvHClr = (TextView) itemView.findViewById(R.id.tvHClr);

                if (AmcuConfig.getInstance().getKeyAllowProteinValue()) {
                    tvIncentive.setVisibility(View.VISIBLE);
                    tvProtein.setVisibility(View.VISIBLE);
                } else {
                    tvIncentive.setVisibility(View.GONE);
                    tvProtein.setVisibility(View.GONE);

                }

                if (AmcuConfig.getInstance().getKeyAllowDisplayRate()) {
                    tvSnfKg.setVisibility(View.GONE);
                    tvFatKg.setVisibility(View.GONE);
                    tvAmount.setVisibility(View.VISIBLE);
                    tvRate.setVisibility(View.VISIBLE);
                } else {
                    tvSnfKg.setVisibility(View.VISIBLE);
                    tvFatKg.setVisibility(View.VISIBLE);
                    tvAmount.setVisibility(View.GONE);
                    tvRate.setVisibility(View.GONE);

                }
                if (AmcuConfig.getInstance().getCollectionFATSNFCLR().equalsIgnoreCase("FC")) {
                    tvSnf.setVisibility(View.GONE);
                    tvHClr.setVisibility(View.VISIBLE);
                } else {
                }
            }


        }
    }


}