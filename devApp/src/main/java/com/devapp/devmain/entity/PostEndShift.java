package com.devapp.devmain.entity;

import com.devapp.devmain.additionalRecords.AdditionalParamsEntity;
import com.devapp.devmain.agentfarmersplit.ShiftConcluded;
import com.devapp.devmain.agentfarmersplit.SplitCollectionEntity;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;

public class PostEndShift implements Serializable, Comparable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    public String collectionDate;
    public String shift;
    public long startTime;
    public long endTime;
    @JsonProperty("centerId")
    public String societyId;
    @JsonIgnore
    @JsonProperty("endShift")
    public boolean endShift;

    public String timeZone;

    public int numberOfShifts;
    public ArrayList<? extends CollectionEntry> farmerCollectionEntryList;
    public ArrayList<SplitCollectionEntity> farmerSplitCollectionEntryList;
    public String deviceId;
    public ShiftConcluded shiftConcluded;
    public ArrayList<AdditionalParamsEntity> extraParametersUpdatedFarmerCollectionEntryList;
//	public ArrayList<CanCollectionEntry> canCollectionEntryList;
//	public ArrayList<TankerCollectionEntity> tankerCollectionEntryList;
//	public ArrayList<ExtraParamEntity> updatedFarmerCollectionEntryList;


    @Override
    public boolean equals(Object obj) {

        if (obj != null && obj instanceof PostEndShift) {
            PostEndShift obj1 = (PostEndShift) obj;
            return (this.shift.equalsIgnoreCase(obj1.shift)
                    && this.collectionDate.equalsIgnoreCase(obj1.collectionDate));
        } else {
            return false;
        }

    }

    @Override
    public int compareTo(Object o) {

        //Date 2017-11-17
        //Shift MORNING,EVENING

        if (o instanceof PostEndShift) {
            try {
                PostEndShift postEndShift = (PostEndShift) o;

                int date = Integer.parseInt(this.collectionDate.replace("-", ""));
                int compareDate = Integer.parseInt(postEndShift.collectionDate.replace("-", ""));

                if (date > compareDate) {
                    return 1;
                } else if (date == compareDate) {

                    if (this.shift.equalsIgnoreCase("EVENING")
                            && postEndShift.shift.equalsIgnoreCase("MORNING")) {
                        return 1;
                    } else if (postEndShift.shift.equalsIgnoreCase("EVENING")
                            && this.shift.equalsIgnoreCase("MORNING")) {
                        return -1;
                    }
                } else {
                    return -1;
                }
            } catch (NullPointerException e1) {

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }


        return 0;
    }
}
