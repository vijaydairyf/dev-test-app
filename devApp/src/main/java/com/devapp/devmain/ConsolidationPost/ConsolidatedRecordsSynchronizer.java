package com.devapp.devmain.ConsolidationPost;

import android.content.Context;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.ConcludedShift;
import com.devapp.devmain.postentities.ConsolidatedDate;
import com.devapp.devmain.postentities.ConsolidatedId;
import com.devapp.devmain.postentities.ConsolidatedMetadata;
import com.devapp.devmain.postentities.ConsolidatedPostData;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.AdvanceUtil;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by u_pendra on 17/1/18.
 */

public class ConsolidatedRecordsSynchronizer {


    private static ConsolidatedRecordsSynchronizer consolidatedRecordsSynchronizer;
    private static Context mContext;
    private Map<String, RecordSet> mapRecordFactories;
    private AmcuConfig amcuConfig;
    private SmartCCUtil smartCCUtil;
    private String CENTER_ROUTE;


    private ConsolidatedRecordsSynchronizer() {

        initializeClass();
    }


    public static ConsolidatedRecordsSynchronizer getInstance(Context context) {
        if (consolidatedRecordsSynchronizer == null) {
            mContext = context;
            consolidatedRecordsSynchronizer = new ConsolidatedRecordsSynchronizer();
        }
        return consolidatedRecordsSynchronizer;
    }


    private void initializeClass() {

        mapRecordFactories = new HashMap<>();
        mapRecordFactories.put(CollectionConstants.
                        FARMER_MILK_COLLECTION,
                FarmerMilkCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.MCC_MILK_COLLECTION,
                MCCMilkCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.SALES_COLLECTION,
                SalesCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.DISPATCH_COLLECTION,
                DispatchCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.SAMPLE_RECORDS,
                SampleCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.TANKER_MILK_COLLECTION,
                TankerCollectionRecordSet.getInstance(mContext));

        mapRecordFactories.put(CollectionConstants.ROUTE_COLLECTION,
                RouteCollectionRecordSet.getInstance(mContext));

        mapRecordFactories.put(CollectionConstants.EDITED_FARMER_COLLECTION,
                EditFarmerCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.EDITED_MCC_COLLECTION,
                EditMCCCollectionRecordSet.getInstance(mContext));

        // Record is persisted in incomplete state with quality params, immediately it is updated
        //with quantity params and moved to complete state, hence sending these records is unnecessary
      /*  mapRecordFactories.put(CollectionConstants.INCOMPLETE_FARMER_RECORDS,
                IncompleteFarmerRecordSet.getInstance(mContext));*/
        mapRecordFactories.put(CollectionConstants.INCOMPLETE_MCC_RECORDS,
                IncompleteMCCRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.EXTRA_PARAM_DETAILS,
                AdditionalCollectionRecordSet.getInstance(mContext));
        mapRecordFactories.put(CollectionConstants.FARMER_SPLIT_COLLECTION,
                FarmerSplitCollectionRecordSet.getInstance(mContext));
        amcuConfig = AmcuConfig.getInstance();
        smartCCUtil = new SmartCCUtil(mContext);
        CENTER_ROUTE = SmartCCUtil.getCenterEntity(mContext).route;
    }


    public ArrayList<ConsolidatedPostData> getAllUnsentRecords() {

        TreeSet<DateShiftEntry> allDateShiftEntrySet = getUnsentDayShiftList();

        ArrayList<ConsolidatedPostData> shiftRecordsList = getAllConsolidatedRecords(allDateShiftEntrySet);
        return shiftRecordsList;
    }

    public ArrayList<ConsolidatedPostData> findAllRecords(TreeSet<DateShiftEntry> dateShiftEntries) {
        ArrayList<ConsolidatedPostData> shiftRecordsList =
                getAllConsolidatedRecords(dateShiftEntries);
        return shiftRecordsList;
    }


    public int getAllUnsentCount() {

        int unsent = 0;

        for (Map.Entry<String, RecordSet> mapEntry : mapRecordFactories.entrySet()) {

            unsent = unsent + mapEntry.getValue().getUnsentCount();

        }

        return unsent;
    }

    //TODO Null pointer need to handle
    public TreeSet<DateShiftEntry> getUnsentDayShiftList() {

        TreeSet<DateShiftEntry> allTreeSet = new TreeSet<>();
        ArrayList<DateShiftEntry> arrayList = new ArrayList<>();

        for (Map.Entry<String, RecordSet> mapEntry : mapRecordFactories.entrySet()) {
            arrayList.addAll(mapEntry.getValue().getUnsentDatesAndShiftsList());
        }


        for (DateShiftEntry dateShiftEntry : arrayList) {
            if (dateShiftEntry.date != null)
                allTreeSet.add(dateShiftEntry);

        }
        return allTreeSet;
    }


    public ArrayList<ConsolidatedPostData> getAllConsolidatedRecords(TreeSet<DateShiftEntry> dataEntrySet)

    {
        Iterator<DateShiftEntry> iterator = dataEntrySet.iterator();
        ArrayList<ConsolidatedPostData> allConsolidatedRecords = new ArrayList<>();

        while (iterator.hasNext()) {
            DateShiftEntry dataEntry = iterator.next();

            if (dataEntry.date == null) {
                return allConsolidatedRecords;
            }

            ConsolidatedPostData consolidatedPostData = getConsolidatedMetadata(dataEntry);
            consolidatedPostData.recordEntries = new Hashtable<>();
            for (Map.Entry<String, RecordSet> mapEntry : mapRecordFactories.entrySet()) {
                consolidatedPostData.recordEntries.put(mapEntry.getKey(),
                        mapEntry.getValue().getUnsentRecords(dataEntry));
                //Need to set start time and end time for collection
            }
            setStartEndDate(consolidatedPostData);
            allConsolidatedRecords.add(consolidatedPostData);

        }

        return allConsolidatedRecords;

    }


    private ConsolidatedPostData getConsolidatedMetadata(DateShiftEntry dateShiftEntry) {
        ConsolidatedPostData consolidatedPostData = new ConsolidatedPostData();


        ConsolidatedMetadata consolidatedMetadata = new ConsolidatedMetadata();

        ConcludedShift concludedShift = getConcludedShift();


        ConsolidatedId consolidatedId = new ConsolidatedId();

        initializeStartEndDate();

        consolidatedId.chillingCenter = new SessionManager(mContext).getCollectionID();
        consolidatedId.collectionCenter = new SessionManager(mContext).getCollectionID();
        // consolidatedId.organization = "Get organization";
        consolidatedId.route = CENTER_ROUTE;

        consolidatedMetadata.concludedShift = concludedShift;
        consolidatedMetadata.consolidatedId = consolidatedId;

        consolidatedMetadata.deviceId = amcuConfig.getDeviceID();

        ConsolidatedDate consolidatedDate = new ConsolidatedDate();

        consolidatedDate.collectionDate =
                SmartCCUtil.getCollectionDateFromLongTime(
                        smartCCUtil.getDateFromFormat(dateShiftEntry.date, "yyyy-MM-dd").getTime());

        //Need to change
        consolidatedDate.startTime =
                consolidatedDate.collectionDate;
        consolidatedDate.endTime =
                consolidatedDate.collectionDate;

        consolidatedMetadata.shift = dateShiftEntry.shift;

        consolidatedMetadata.consolidatedDate = consolidatedDate;

        consolidatedPostData.consolidatedMetadata = consolidatedMetadata;


        return consolidatedPostData;
    }


    public ConcludedShift getConcludedShift() {
        String date, shift;
        ArrayList<String> allList = new AdvanceUtil(mContext).getShiftConcludeDateAndShift();

        if (allList == null || allList.size() == 0) {
            if (AmcuConfig.getInstance().getCollectionEndShift()) {
                date = Util.getTodayDateAndTime(1);
                shift = Util.getCurrentShift();
            } else {
                shift = Util.getCurrentShift();
                if (shift.equalsIgnoreCase(AppConstants.Shift.EVENING)) {
                    date = Util.getTodayDateAndTime(1);
                    shift = AppConstants.Shift.MORNING;
                } else {
                    date = new AdvanceUtil(mContext).getReportDate(-1);
                    shift = AppConstants.Shift.EVENING;

                }
            }

        } else {
            String[] strArray = allList.get(allList.size() - 1).split(AppConstants.DB_SEPERATOR);
            date = strArray[0];
            shift = strArray[1];
        }
        date = new AdvanceUtil(mContext).changeDateFormat(date,
                "dd-MMM-yyyy", "yyyy-MM-dd");
        ConcludedShift shiftConcluded = new ConcludedShift();
       /* if (shift.equalsIgnoreCase("E")) {
            shift = "EVENING";
        } else {
            shift = "MORNING";
        }*/
        shiftConcluded.date = SmartCCUtil.getCollectionDateFromLongTime(
                smartCCUtil.getDateFromFormat(date, "yyyy-MM-dd").getTime());
        ;
        shiftConcluded.shift = shift;
        return shiftConcluded;
    }


    public void setUpdateStatus(ConsolidatedPostData consolidatedPostData, int status) {


        for (Map.Entry<String, ArrayList<? extends SynchronizableElement>> mapEntry :
                consolidatedPostData.recordEntries.entrySet()) {

            mapRecordFactories.get(mapEntry.getKey()).
                    setUpdateStatus(mapEntry.getValue(), status);
        }

    }


    private void initializeStartEndDate() {
        SmartCCUtil.MIN_DATE = null;
        SmartCCUtil.MAX_DATE = null;

    }

    private void setStartEndDate(ConsolidatedPostData consolidatedPostData) {
        consolidatedPostData.consolidatedMetadata.consolidatedDate.startTime = SmartCCUtil.MIN_DATE;
        consolidatedPostData.consolidatedMetadata.consolidatedDate.endTime = SmartCCUtil.MAX_DATE;

    }

    public ArrayList<ConsolidatedPostData> getAllUnsentRecordsForEncryption(
            long startTime, long endTime, String shift, String type) {

        TreeSet<DateShiftEntry> allDateShiftEntrySet = getUnsentDayShiftList();

        ArrayList<ConsolidatedPostData> shiftRecordsList = getAllConsolidatedRecords(allDateShiftEntrySet);
        return shiftRecordsList;
    }


}
