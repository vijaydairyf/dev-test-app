package com.devapp.devmain.user;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devApp.R;

import java.text.DecimalFormat;

public class RateAdapterNew extends CursorAdapter {

    TextView tvFarmerId, tvTXN, tvMilkType, tvFAT, tvSNF, tvQuality, tvRATE,
            tvAmount;
    int chkReport;
    Context context;
    int count = 0;

    DecimalFormat decimalFormatAMT = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
    AmcuConfig amcuConfig;
    int serialNumber = 0;
    ChooseDecimalFormat chooseDecimalFormat;
    DecimalFormat decimalFormatClr;
    DecimalFormat decimalFormatFat;
    DecimalFormat decimalFormatSNF;
    DecimalFormat decimalFormatProtein;
    TextView tvIncentive;
    TextView tvProtein;
    TextView tvTotalAmount;
    TextView tvKgFat;
    TextView tvKgSnf;
    private boolean isProteinEnable;

    private String date;

    public RateAdapterNew(Context context, Cursor c, int i) {
        super(context, c);
        this.context = context;
        this.chkReport = i;
        amcuConfig = AmcuConfig.getInstance();
        chooseDecimalFormat = new ChooseDecimalFormat();

        decimalFormatFat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.FAT);
        decimalFormatSNF = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.SNF);
        decimalFormatProtein = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.PROTEIN);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = null;
        if (chkReport == 0 || chkReport == 4) {
            retView = inflater.inflate(R.layout.daily_report_item, parent,
                    false);
        } else if (chkReport == 1) {
            retView = inflater.inflate(R.layout.daily_report_item, parent,
                    false);
        } else if (chkReport == 2) {
            retView = inflater.inflate(R.layout.report_member_bill_items,
                    parent, false);
        } else if (chkReport == 3) {
            retView = inflater.inflate(R.layout.member_list_items, parent,
                    false);
        } else if (chkReport == 5) {
            retView = inflater.inflate(R.layout.daily_report_item, parent,
                    false);
        } else if (chkReport == 6) {
            retView = inflater.inflate(R.layout.daily_report_item, parent,
                    false);
        } else if (chkReport == 7) {
            //Center shift report
            retView = inflater.inflate(R.layout.daily_report_item, parent,
                    false);
        }

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

        if (chkReport == 0 || chkReport == 1 || chkReport == 4 || chkReport == 5 || chkReport == 7) {
            tvFarmerId = (TextView) view.findViewById(R.id.tvMemberId);
            tvTXN = (TextView) view.findViewById(R.id.tvTax);
            tvMilkType = (TextView) view.findViewById(R.id.tvMilkType);
            tvFAT = (TextView) view.findViewById(R.id.tvFat);
            tvSNF = (TextView) view.findViewById(R.id.tvSnf);
            tvQuality = (TextView) view.findViewById(R.id.tvQuality);
            tvRATE = (TextView) view.findViewById(R.id.tvRate);
            tvAmount = (TextView) view.findViewById(R.id.tvAmount);

            tvProtein = (TextView) view.findViewById(R.id.tvProtein);
            tvIncentive = (TextView) view.findViewById(R.id.tvIncentive);
            tvTotalAmount = (TextView) view.findViewById(R.id.tvToalAmount);
            tvKgFat = (TextView) view.findViewById(R.id.tvFatKg);
            tvKgSnf = (TextView) view.findViewById(R.id.tvSnfKg);

            if (amcuConfig.getKeyAllowProteinValue()) {
                tvTotalAmount.setVisibility(View.VISIBLE);
                tvIncentive.setVisibility(View.VISIBLE);
                tvProtein.setVisibility(View.VISIBLE);
            } else {
                tvTotalAmount.setVisibility(View.GONE);
                tvIncentive.setVisibility(View.GONE);
                tvProtein.setVisibility(View.GONE);

            }


            if (amcuConfig.getKeyAllowDisplayRate()) {
                tvKgFat.setVisibility(View.GONE);
                tvKgSnf.setVisibility(View.GONE);
                tvAmount.setVisibility(View.VISIBLE);
                tvRATE.setVisibility(View.VISIBLE);

            } else {
                tvKgFat.setVisibility(View.VISIBLE);
                tvKgSnf.setVisibility(View.VISIBLE);
                tvAmount.setVisibility(View.GONE);
                tvRATE.setVisibility(View.GONE);
            }


        } else if (chkReport == 2) {
            tvFarmerId = (TextView) view.findViewById(R.id.tvMemberId);
            tvQuality = (TextView) view.findViewById(R.id.tvQuality);
            tvRATE = (TextView) view.findViewById(R.id.tvRate);
            tvAmount = (TextView) view.findViewById(R.id.tvAmount);
            tvTXN = (TextView) view.findViewById(R.id.tvSign);
            tvTXN.setText("        ");
        } else if (chkReport == 3) {
            tvFarmerId = (TextView) view.findViewById(R.id.tvMemberId);
            tvQuality = (TextView) view.findViewById(R.id.tvMemberName);

        }

        if (chkReport == 0) {
            tvFarmerId.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(1))));

            tvTXN.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(17))));

            tvMilkType.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(12))));

            tvFAT.setText(decimalFormatFat.format(cursor.getDouble(cursor
                    .getColumnIndex(cursor.getColumnName(4)))));


            //To display clr

            if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC"))
                    || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC"))) {
                tvSNF.setText(decimalFormatSNF.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(20)))));
            } else {
                tvSNF.setText(decimalFormatSNF.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(3)))));
            }

            tvQuality.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(9))));


            if (amcuConfig.getKeyAllowDisplayRate()) {
                tvRATE.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(13)))));

			/*tvAmount.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(8))));*/
//			TODO Fetch the appropriate amount
                tvAmount.setText(String.valueOf(Util.getAmount(this.context, cursor.getDouble(
                        cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_AMOUNT)),
                        cursor.getDouble(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_BONUS)))));

            } else {
                tvKgFat.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(56)))));
                tvKgSnf.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(57)))));
            }

            if (amcuConfig.getKeyAllowProteinValue()) {
                tvProtein.setText(decimalFormatFat.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(48)))));
                tvIncentive.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(54)))));
                tvTotalAmount.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(55)))));
            }
        } else if (chkReport == 4) {

            if (cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(10))) != null) {
                tvFarmerId.setText(cursor.getString(cursor.getColumnIndex(cursor
                        .getColumnName(10))).replace("-", ""));
            } else {
                tvFarmerId.setText("NA");
            }

            tvTXN.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(17))));

            tvMilkType.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(12))));

            tvFAT.setText(decimalFormatFat.format(cursor.getDouble(cursor
                    .getColumnIndex(cursor.getColumnName(4)))));


            //To display clr

            if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC"))
                    || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC"))) {
                tvSNF.setText(decimalFormatSNF.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(20)))));
            } else {
                tvSNF.setText(decimalFormatSNF.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(3)))));
            }


            tvQuality.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(9))));


            if (amcuConfig.getKeyAllowDisplayRate()) {
                tvRATE.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(13)))));

			/*tvAmount.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(8))));*/
//			TODO Fetch the appropriate amount
                tvAmount.setText(String.valueOf(Util.getAmount(this.context, cursor.getDouble(cursor.
                        getColumnIndex(FarmerCollectionTable.KEY_REPORT_AMOUNT)), cursor.getDouble(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_BONUS)))));

            } else {
                tvKgFat.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(56)))));
                tvKgSnf.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(57)))));
            }

            if (amcuConfig.getKeyAllowProteinValue()) {
                tvProtein.setText(decimalFormatFat.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(48)))));
                tvIncentive.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(54)))));
                tvTotalAmount.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                        .getColumnIndex(cursor.getColumnName(55)))));
            }

        } else if (chkReport == 5) {

            //This is for sales entry
            serialNumber = serialNumber + 1;

            tvFarmerId.setText(String.valueOf(Util.paddingFarmerId(String.valueOf(serialNumber), 4)));

            tvTXN.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(15))));

            tvMilkType.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(12))));

            tvFAT.setText(decimalFormatFat.format(cursor.getDouble(cursor
                    .getColumnIndex(cursor.getColumnName(4)))));

            tvSNF.setText(decimalFormatSNF.format(cursor.getDouble(cursor
                    .getColumnIndex(cursor.getColumnName(3)))));

            tvQuality.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(9))));

            tvRATE.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                    .getColumnIndex(cursor.getColumnName(13)))));

			/*tvAmount.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(8))));*/
            tvAmount.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(8))));
        } else if (chkReport == 1) {
            tvFarmerId.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(11))));

            String str = cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(10)));

            if (str != null && str.length() > 4) {
                String sub = str.substring(0, 6);
                tvTXN.setText(sub);
            } else {
                tvTXN.setText(cursor.getString(cursor.getColumnIndex(cursor
                        .getColumnName(10))));
            }

            tvMilkType.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(12))));

            tvFAT.setText(decimalFormatFat.format(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(4)))));

            tvSNF.setText(decimalFormatSNF.format(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(3)))));

            tvQuality.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(9))));

            tvRATE.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(13))));

			/*tvAmount.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(8))));*/
//			TODO Fetch the appropriate amount
            tvAmount.setText(String.valueOf(Util.getAmount(this.context,
                    cursor.getDouble(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_AMOUNT)),
                    cursor.getDouble(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_BONUS)))));
        } else if (chkReport == 2) {

            tvFarmerId.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(1))));
            tvQuality.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(9))));

            tvRATE.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(13))));

			/*tvAmount.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(8))));*/
            tvAmount.setText(String.valueOf(Util.getAmount(this.context,
                    cursor.getDouble(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_AMOUNT)),
                    cursor.getDouble(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_BONUS)))));

        } else if (chkReport == 3) {
            tvFarmerId.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(7))));
            tvQuality.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(2))));

        } else if (chkReport == 7) {
            //For center report
            tvFarmerId.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(12))));

            tvTXN.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(13))));

            tvMilkType.setText(cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(10))));

            tvFAT.setText(decimalFormatFat.format(cursor.getDouble(cursor
                    .getColumnIndex(cursor.getColumnName(4)))));

            tvSNF.setText(decimalFormatSNF.format(cursor.getDouble(cursor
                    .getColumnIndex(cursor.getColumnName(3)))));

            tvQuality.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                    .getColumnIndex(cursor.getColumnName(8)))));

            tvRATE.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                    .getColumnIndex(cursor.getColumnName(11)))));

            // amount is not checked for bonus ??
            tvAmount.setText(decimalFormatAMT.format(cursor.getDouble(cursor
                    .getColumnIndex(cursor.getColumnName(7)))));
        }

    }
}
