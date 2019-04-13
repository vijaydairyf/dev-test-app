package com.devapp.devmain.macollection;

import android.content.Context;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.TimeEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.QuantityEntity;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.main.CollectionHelper;

import java.text.DecimalFormat;

/**
 * Created by u_pendra on 3/5/17.
 */

public class RecordHelper {


    ValidationHelper validationHelper;
    AmcuConfig amcuConfig;
    SessionManager sessionManager;
    DecimalFormat decimalFormatAmount = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatWeight = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatRate = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
    DecimalFormat decimalFormatClr = new DecimalFormat("#0");
    SmartCCUtil smartCCUtil;
    private Context mContext;


    /**
     * To get the full report entity from dummy report
     *
     * @param dummyReport
     * @param commited
     * @param comingFrom
     * @param timeEntity
     * @return
     */

    public ReportEntity getReportEntity(ReportEntity dummyReport,
                                        int commited, String comingFrom,
                                        TimeEntity timeEntity) {
        int txNumber = sessionManager.getTXNumber() + 1;

        String date = smartCCUtil.getReportFormatDate();
        ReportEntity reportEntity = new ReportEntity();

        //Add basic information
        reportEntity.user = sessionManager.getUserId();
        reportEntity.farmerId = sessionManager.getFarmerID();
        reportEntity.farmerName = sessionManager.getFarmerName();
        reportEntity.socId = sessionManager.getCollectionID();
        reportEntity.txnNumber = Integer.parseInt(Util.getTxnNumber(txNumber));
        if (sessionManager.getIsChillingCenter()) {
            reportEntity.collectionType = Util.REPORT_TYPE_MCC;
        } else {
            reportEntity.collectionType = Util.REPORT_TYPE_FARMER;
        }
        reportEntity.agentId = smartCCUtil.getAgentId();
        reportEntity.centerRoute = Util.getRouteFromChillingCenter(mContext,
                sessionManager.getFarmerID());

        reportEntity.milkType = dummyReport.milkType;
        reportEntity.rateChartName = amcuConfig.getRateChartName();


        reportEntity.temp = dummyReport.temp;

        reportEntity.fat = dummyReport.fat;
        reportEntity.snf = dummyReport.snf;
        reportEntity.awm = dummyReport.awm;
        reportEntity.clr = dummyReport.clr;

        reportEntity.quantity = dummyReport.quantity;
        CollectionHelper collectionHelper = new CollectionHelper(mContext);
        QuantityEntity quantityEntity =
                collectionHelper.getQuantityItems(reportEntity.quantity);
        reportEntity.kgWeight = quantityEntity.kgQuantity;
        reportEntity.ltrsWeight = quantityEntity.ltrQuanity;

        reportEntity.rate = dummyReport.rate;
        reportEntity.amount = dummyReport.amount;
        reportEntity.bonus = dummyReport.bonus;
        reportEntity.numberOfCans = dummyReport.numberOfCans;

        //All Time parameters

        reportEntity.time = timeEntity.time;
        reportEntity.lDate = timeEntity.lDate;
        reportEntity.tippingStartTime = timeEntity.tippingStartTime;
        reportEntity.tippingEndTime = timeEntity.tippingEndTime;
        reportEntity.miliTime = timeEntity.milliTime;
        reportEntity.milkAnalyserTime = timeEntity.qualityTime;
        reportEntity.weighingTime = timeEntity.quantityTime;


        //Set Extra parameters

        reportEntity.manual = dummyReport.manual;
        reportEntity.qualityMode = dummyReport.qualityMode;
        reportEntity.quantityMode = dummyReport.quantityMode;

        reportEntity.recordCommited = commited;


        // Adding milkquality

        reportEntity.status = dummyReport.status;
        reportEntity.milkQuality = dummyReport.milkQuality;
        reportEntity.rateMode = dummyReport.rateMode;
        if (commited == 0) {
            reportEntity.recordStatus = Util.RECORD_STATUS_INCOMPLETE;
        } else {
            reportEntity.recordStatus = Util.RECORD_STATUS_COMPLETE;
        }
        reportEntity.milkStatusCode = smartCCUtil.getMilkStatusCode("GOOD");
        reportEntity.rateCalculation = dummyReport.rateCalculation;

        //sequence number resemble sample number to server
        reportEntity.sampleNumber = 0;
        reportEntity.serialMa = 1;
        reportEntity.maName = amcuConfig.getMA();
        smartCCUtil.setCollectionStartData(reportEntity);
        return reportEntity;
    }


}
