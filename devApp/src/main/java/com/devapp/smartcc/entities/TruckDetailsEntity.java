package com.devapp.smartcc.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Upendra on 10/3/2016.
 */
public class TruckDetailsEntity implements Parcelable {

    public static final Creator<TruckDetailsEntity> CREATOR = new Creator<TruckDetailsEntity>() {
        @Override
        public TruckDetailsEntity createFromParcel(Parcel in) {
            return new TruckDetailsEntity(in);
        }

        @Override
        public TruckDetailsEntity[] newArray(int size) {
            return new TruckDetailsEntity[size];
        }
    };
    public long seqNum;
    public String centerId;
    public long date;
    public int shift;
    public String truckId;
    public long securityTime;
    public double recoveryAmount;
    public String material;
    public int cansIn;
    public int cansOut;
    public String deviceId;
    public int sentStatus;
    public String remarks;

    public int smsSentStatus;
    public String postDate;
    public String postShift;


    public TruckDetailsEntity(long sequenceNumber, String centerId, long date, int shift
            , String truckId, long securityTime
            , double recoveryAmount, String material
            , int cansIn, int cansOut, String deviceId, int sentStatus, String remarks,
                              int smsSentStatus, String postDate, String postShift) {
        this.centerId = centerId;
        this.date = date;
        this.shift = shift;
        this.truckId = truckId;
        this.securityTime = securityTime;
        this.recoveryAmount = recoveryAmount;
        this.material = material;
        this.cansIn = cansIn;
        this.cansOut = cansOut;
        this.deviceId = deviceId;
        this.seqNum = sequenceNumber;
        this.sentStatus = sentStatus;
        this.remarks = remarks;
        this.smsSentStatus = smsSentStatus;
        this.postDate = postDate;
        this.postShift = postShift;


    }

    protected TruckDetailsEntity(Parcel in) {
        centerId = in.readString();
        date = in.readLong();
        shift = in.readInt();
        truckId = in.readString();
        securityTime = in.readLong();
        recoveryAmount = in.readDouble();
        material = in.readString();
        cansIn = in.readInt();
        cansOut = in.readInt();
        deviceId = in.readString();
        seqNum = in.readLong();
        sentStatus = in.readInt();
        remarks = in.readString();

        smsSentStatus = in.readInt();
        postDate = in.readString();
        postShift = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(centerId);
        dest.writeLong(date);
        dest.writeInt(shift);
        dest.writeString(truckId);
        dest.writeLong(securityTime);
        dest.writeDouble(recoveryAmount);
        dest.writeString(material);
        dest.writeInt(cansIn);
        dest.writeInt(cansOut);
        dest.writeLong(seqNum);
        dest.writeInt(sentStatus);
        dest.writeInt(smsSentStatus);
        dest.writeString(postDate);
        dest.writeString(postShift);

    }
}
