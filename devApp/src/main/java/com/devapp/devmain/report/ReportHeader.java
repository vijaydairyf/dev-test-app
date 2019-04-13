package com.devapp.devmain.report;

import com.devapp.devmain.entity.ReportEntity;

/**
 * Created by u_pendra on 14/5/18.
 */

public class ReportHeader {


    private static ReportHeader reportHeader;
    public int HEADER_MEMBER_BILL = 1;
    public int HEADER_PERIODIC_BILL = 2;
    public int HEADER_MEMBER_BILL_REGISTER = 3;
    public int HEADER_DAILY_SHIFT_REPORT = 4;

    private ReportHeader() {
    }

    public ReportHeader getInstance() {
        if (reportHeader == null) {
            reportHeader = new ReportHeader();
        }

        return reportHeader;
    }

    public ReportEntity getReportHeader(int type) {


        ReportEntity repEntity = new ReportEntity();

        if (type == HEADER_MEMBER_BILL_REGISTER) {
            repEntity.amount = 0;
            repEntity.postDate = "Date";
            repEntity.fat = 0;
            repEntity.snf = 0;
            repEntity.quantity = 0;
            repEntity.farmerId = "MemId";
            repEntity.farmerName = "name";
            repEntity.milkType = "M";
            repEntity.rate = 0;
            repEntity.txnNumber = 0;
            repEntity.time = "Time";

        } else if (type == HEADER_MEMBER_BILL) {
            repEntity.amount = 0;
            repEntity.postDate = "date";
            repEntity.farmerId = "farmerId";
            repEntity.farmerName = "farmerName";
            repEntity.fat = 0;
            repEntity.lDate = 600;
            repEntity.manual = "manual";
            repEntity.milkType = "MilkType";
            repEntity.quantity = 0;
            repEntity.rate = 0;
            repEntity.postShift = "shift";
            repEntity.snf = 0;
            repEntity.txnNumber = 0;
            repEntity.user = "user";
        } else if (type == HEADER_DAILY_SHIFT_REPORT) {
            repEntity.amount = 0;
            repEntity.postDate = "Date";
            repEntity.farmerId = "Id";
            repEntity.farmerName = "name";
            repEntity.fat = 0;
            repEntity.lDate = 600;
            repEntity.manual = "Manual";
            repEntity.milkType = "Type";
            repEntity.quantity = 0;
            repEntity.rate = 0;
            repEntity.postShift = "Shift";
            repEntity.snf = 0;
            repEntity.txnNumber = 0;
            repEntity.user = "User";
            repEntity.time = "Time";
        } else if (type == HEADER_PERIODIC_BILL) {
            repEntity.amount = 0;
            repEntity.postDate = "Date";
            repEntity.farmerId = "Id";
            repEntity.farmerName = "name";
            repEntity.fat = 0;
            repEntity.lDate = 600;
            repEntity.manual = "Manual";
            repEntity.milkType = "Type";
            repEntity.quantity = 0;
            repEntity.rate = 0;
            repEntity.postShift = "Shift";
            repEntity.snf = 0;
            repEntity.txnNumber = 0;
            repEntity.user = "User";
            repEntity.time = "Time";

        }

        return repEntity;
    }


}
