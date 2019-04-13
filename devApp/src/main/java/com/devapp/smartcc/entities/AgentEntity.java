package com.devapp.smartcc.entities;

import android.os.Parcel;
import android.os.Parcelable;

import com.devapp.devmain.entity.AgentPostEntity;
import com.devapp.devmain.entity.Entity;

import java.util.List;

/**
 * Created by Upendra on 10/5/2016.
 */
public class AgentEntity implements Parcelable, Entity {

    public static final Creator<AgentEntity> CREATOR = new Creator<AgentEntity>() {
        @Override
        public AgentEntity createFromParcel(Parcel in) {
            return new AgentEntity(in);
        }

        @Override
        public AgentEntity[] newArray(int size) {
            return new AgentEntity[size];
        }
    };
    public String agentID;
    public String barcode;
    public String firstName;
    public String lastName;
    public String phoneNum;
    public String mobileNum;
    public String milkType;
    public long registeredDate;
    public String uniqueID1;
    public String uniqueID2;
    public String uniqueID3;
    public int numCans;
    public String shiftsSupplyingTo;
    public String routeID;
    public String pickupPoint;
    public double distanceToDelivery;
    public List<String> centerList;

    public AgentEntity() {

    }

    public AgentEntity(AgentPostEntity entity) {
        agentID = entity.producerProfile.id;
        barcode = entity.producerProfile.barcode;
        firstName = entity.producerProfile.name;
        mobileNum = entity.producerProfile.mobileNumber;
        milkType = entity.producerProfile.milkType;
        registeredDate = entity.producerProfile.regDate;
        uniqueID1 = entity.uniqueID1;
        uniqueID2 = entity.uniqueID2;
        uniqueID3 = entity.uniqueID3;
        shiftsSupplyingTo = entity.shiftsSupplyingTo;
        pickupPoint = entity.pickupPoint;
        distanceToDelivery = entity.distanceToDelivery;
        centerList = entity.centerIdList;
    }


    public AgentEntity(String agentId, String barcode, String firstName, String lastName, String phoneNum
            , String mobileNum, String milkType, long registeredDate, String uniqueId1, String uniqueId2, String uniqueId3
            , int numCans, String shiftSupplyingTo, String routeId, String pickupPoint, double distanceToDelivery, List<String> centerIdList) {

        this.agentID = agentId;
        this.barcode = barcode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNum = phoneNum;
        this.mobileNum = mobileNum;
        this.numCans = numCans;
        this.shiftsSupplyingTo = shiftSupplyingTo;
        this.milkType = milkType;
        this.registeredDate = registeredDate;
        this.uniqueID1 = uniqueId1;
        this.uniqueID2 = uniqueId2;
        this.uniqueID3 = uniqueId3;
        this.routeID = routeId;
        this.pickupPoint = pickupPoint;
        this.shiftsSupplyingTo = shiftSupplyingTo;
        this.distanceToDelivery = distanceToDelivery;
        this.centerList = centerIdList;


    }

    protected AgentEntity(Parcel in) {
        agentID = in.readString();
        barcode = in.readString();
        firstName = in.readString();
        lastName = in.readString();
        phoneNum = in.readString();
        mobileNum = in.readString();
        milkType = in.readString();
        registeredDate = in.readLong();
        uniqueID1 = in.readString();
        uniqueID2 = in.readString();
        uniqueID3 = in.readString();
        numCans = in.readInt();
        shiftsSupplyingTo = in.readString();
        routeID = in.readString();
        pickupPoint = in.readString();
        distanceToDelivery = in.readDouble();
        centerList = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(agentID);
        dest.writeString(barcode);
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(phoneNum);
        dest.writeString(mobileNum);
        dest.writeString(milkType);
        dest.writeLong(registeredDate);
        dest.writeString(uniqueID1);
        dest.writeString(uniqueID2);
        dest.writeString(uniqueID3);
        dest.writeInt(numCans);
        dest.writeString(shiftsSupplyingTo);
        dest.writeString(routeID);
        dest.writeString(pickupPoint);
        dest.writeDouble(distanceToDelivery);
        dest.writeStringList(centerList);
    }

    @Override
    public Object getPrimaryKeyId() {
        return agentID;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        this.agentID = (String) id;
    }
}
