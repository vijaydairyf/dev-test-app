package com.devapp.syncapp;

import android.util.Log;

import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.ConsolidationPost.SynchronizableElement;
import com.devapp.devmain.postentities.AdditionFieldEditEntity;
import com.devapp.devmain.postentities.ConsolidatedPostData;
import com.devapp.devmain.postentities.EditedCenterPostEntity;
import com.devapp.devmain.postentities.EditedFarmerPostEntity;
import com.devapp.devmain.postentities.FarmerPostEntity;
import com.devapp.devmain.postentities.FarmerSplitCollectionEntity;
import com.devapp.devmain.postentities.FarmerSplitSubRecords;
import com.devapp.devmain.postentities.IncompleteCenterCollection;
import com.devapp.devmain.postentities.MCCPostEntity;
import com.devapp.devmain.postentities.RoutePostEntity;
import com.devapp.devmain.postentities.SalesPostEntity;
import com.devapp.devmain.postentities.SamplePostEntity;
import com.devapp.devmain.postentities.TankerPostEntity;
import com.devapp.devmain.postentities.dispatchentities.DispatchPostEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * Created by Pankaj on 5/2/2018.
 */

public class SyncAppUtils {
    private static final String TAG = "SyncAppUtils";
    private static TreeMap<Date, List<Long>> seqTimeSeqMap;                                                             //Key will be long timestamp,& value will be Sequence List

    private static boolean isCollectionDateOld(long currentCollectionTime, long syncAppLastPostTime) {
        GregorianCalendar collectionTime = new GregorianCalendar(TimeZone.getTimeZone("Asia/Kolkata"));
        collectionTime.setTimeInMillis(currentCollectionTime);
        GregorianCalendar syncAppLastPostDateTime = new GregorianCalendar(TimeZone.getTimeZone("Asia/Kolkata"));
        syncAppLastPostDateTime.setTimeInMillis(syncAppLastPostTime);
        return collectionTime.compareTo(syncAppLastPostDateTime) <= 0;
    }

    public static ArrayList<ConsolidatedPostData> filterBySequenceNumber(long lastSeqCollectionPostDate, long syncAppLastSequence, ArrayList<ConsolidatedPostData> combinedPostEndShift) {
        try {
            seqTimeSeqMap = DevAppApplication.getSyncTimeStampMap();
            Log.i(TAG, "filterBySequenceNumber_V3: " + seqTimeSeqMap);

            for (ConsolidatedPostData d : combinedPostEndShift) {
                Hashtable<String, ArrayList<? extends SynchronizableElement>> recordEntries = d.recordEntries;
                Set<String> keys = recordEntries.keySet();
                for (String key : keys) {
                    ArrayList<? extends SynchronizableElement> synchronizableElements = recordEntries.get(key);
                    Iterator<? extends SynchronizableElement> iterator = synchronizableElements.iterator();
                    while (iterator.hasNext()) {
                        SynchronizableElement s = iterator.next();

                        if (s instanceof FarmerPostEntity) {
                            FarmerPostEntity s1 = (FarmerPostEntity) s;
                            if (isCollectionDateOld(s1.collectionTime.getTime(), lastSeqCollectionPostDate)) {
                                iterator.remove();
                                remove(s1.sequenceNumber, s1.collectionTime.getTime(), s1.shift);
                            }
                        } else if (s instanceof MCCPostEntity) {
                            MCCPostEntity s1 = (MCCPostEntity) s;
                            if (isCollectionDateOld(s1.collectionTime.getTime(), lastSeqCollectionPostDate)) {
                                iterator.remove();
                                remove(s1.sequenceNumber, s1.collectionTime.getTime(), s1.shift);
                            }
                        } else if (s instanceof SalesPostEntity) {
                            SalesPostEntity s1 = (SalesPostEntity) s;
                            if (isCollectionDateOld(s1.salesTime.getTime(), lastSeqCollectionPostDate)) {
                                iterator.remove();
                                remove(s1.sequenceNumber, s1.salesTime.getTime(), s1.shift);
                            }
                        } else if (s instanceof DispatchPostEntity) {
                            DispatchPostEntity s1 = (DispatchPostEntity) s;
                            if (isCollectionDateOld(s1.collectionTime.getTime(), lastSeqCollectionPostDate)) {
                                iterator.remove();
                                remove(s1.sequenceNumber, s1.collectionTime.getTime(), s1.shift);
                            }
                        } else if (s instanceof SamplePostEntity) {
                            SamplePostEntity s1 = (SamplePostEntity) s;
                            if (isCollectionDateOld(s1.collectionTime.getTime(), lastSeqCollectionPostDate)) {
                                iterator.remove();
                                remove(s1.sequenceNumber, s1.collectionTime.getTime(), s1.shift);
                            }
                        } else if (s instanceof TankerPostEntity) {
                            TankerPostEntity s1 = (TankerPostEntity) s;
                            if (isCollectionDateOld(s1.collectionTime.getTime(), lastSeqCollectionPostDate)) {
                                iterator.remove();
                                remove(s1.sequenceNumber, s1.collectionTime.getTime(), s1.shift);
                            }
                        } else if (s instanceof RoutePostEntity) {
                            RoutePostEntity s1 = (RoutePostEntity) s;
                            if (isCollectionDateOld(s1.securityTime.getTime(), lastSeqCollectionPostDate)) {
                                iterator.remove();
                                remove(s1.seqNum, s1.securityTime.getTime(), s1.shift);
                            }
                        } else if (s instanceof EditedFarmerPostEntity) {
                            EditedFarmerPostEntity s1 = (EditedFarmerPostEntity) s;
                            if (isCollectionDateOld(s1.updatedTime.getTime(), lastSeqCollectionPostDate)) {
                                iterator.remove();
                                remove(s1.sequenceNumber, s1.updatedTime.getTime(), s1.shift);
                            }
                        } else if (s instanceof EditedCenterPostEntity) {
                            EditedCenterPostEntity s1 = (EditedCenterPostEntity) s;
                            if (isCollectionDateOld(s1.updatedTime.getTime(), lastSeqCollectionPostDate)) {
                                iterator.remove();
                                remove(s1.sequenceNumber, s1.updatedTime.getTime(), s1.shift);
                            }
                        } else if (s instanceof IncompleteCenterCollection) {
                            IncompleteCenterCollection s1 = (IncompleteCenterCollection) s;
                            if (isCollectionDateOld(s1.collectionTime.getTime(), lastSeqCollectionPostDate)) {
                                iterator.remove();
                                remove(s1.sequenceNumber, s1.collectionTime.getTime(), s1.shift);
                            }
                        } else if (s instanceof AdditionFieldEditEntity) {
                            AdditionFieldEditEntity s1 = (AdditionFieldEditEntity) s;
                            if (isCollectionDateOld(s1.updatedTime.getTime(), lastSeqCollectionPostDate)) {
                                iterator.remove();
                                remove(s1.seqNum, s1.updatedTime.getTime(), s1.shift);
                            }
                        } else if (s instanceof FarmerSplitCollectionEntity) {
                            FarmerSplitCollectionEntity s1 = (FarmerSplitCollectionEntity) s;
                            ArrayList<FarmerSplitSubRecords> farmerSplitEntries = s1.farmerSplitEntries;
                            if (farmerSplitEntries != null && farmerSplitEntries.size() > 0) {
                                Iterator<FarmerSplitSubRecords> iterator1 = farmerSplitEntries.iterator();
                                while (iterator1.hasNext()) {
                                    FarmerSplitSubRecords f = iterator1.next();
                                    if (isCollectionDateOld(f.collectionTime.getTime(), lastSeqCollectionPostDate)) {
                                        remove(f.sequenceNumber, f.collectionTime.getTime(), s1.shift);
                                        iterator.remove();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return combinedPostEndShift;
    }

    private static void remove(long seq, long collectionTime, String shift) {
        if (seqTimeSeqMap != null) {
            Log.i(TAG, "remove: " + seq);
            Date date = new Date(collectionTime);
            List<Long> seqenceList = seqTimeSeqMap.get(date);                         // List Of Seq.
            if (seqenceList != null && seqenceList.contains(seq)) {
                seqenceList.remove(new Long(seq));
                if (seqenceList.size() == 0) {
                    seqTimeSeqMap.remove(date);
                }
            }

        }
    }

    public static long[] getEdgeSequences() {
        long[] edges = {0, 0};
        if (seqTimeSeqMap != null && seqTimeSeqMap.size() > 0) {
            if (seqTimeSeqMap.size() == 1) {
                List<Long> value = seqTimeSeqMap.firstEntry().getValue();
                edges[0] = Collections.min(value);
                edges[1] = Collections.max(value);
            } else {

                List<Long> value1 = seqTimeSeqMap.firstEntry().getValue();
                if (value1 != null && value1.size() > 0)
                    edges[0] = Collections.max(value1);

                List<Long> value2 = seqTimeSeqMap.lastEntry().getValue();
                if (value2 != null && value2.size() > 0)
                    edges[1] = Collections.max(value2);
            }
        }
        return edges;
    }

    public static long getLastPostDate() {
        long value = 0;
        if (seqTimeSeqMap != null && seqTimeSeqMap.size() > 0) {
            Date value2 = seqTimeSeqMap.lastEntry().getKey();
            value = value2.getTime();
        }
        return value;
    }
}

