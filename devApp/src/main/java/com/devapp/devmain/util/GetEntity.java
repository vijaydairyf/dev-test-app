package com.devapp.devmain.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.devapp.devmain.entity.ConfigurationConstants;
import com.devapp.devmain.entity.SampleDataEntity;
import com.devapp.devmain.entity.SocietyEntity;
import com.devapp.devmain.entity.UserEntity;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.UserDetails;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Upendra on 6/13/2016.
 */
public class GetEntity {


    SessionManager session;
    SharedPreferences preferences;
    private Context mContext;

    public GetEntity(Context mContext, SharedPreferences pref) {
        this.mContext = mContext;
        this.preferences = pref;
        session = new SessionManager(mContext);
    }


    public SocietyEntity getSocietyEntity() {
        SocietyEntity socEntity = new SocietyEntity();

        socEntity.address = "";
        socEntity.bmcId = "";
        socEntity.socCode = "SOC123";
        socEntity.contactNum1 = "";
        socEntity.contactNum2 = "";
        socEntity.conPerson1 = "";
        socEntity.conPerson2 = "";
        socEntity.socEmail1 = "";
        socEntity.location = "";
        socEntity.name = "Devapp Testing";
        socEntity.route = "";

        return socEntity;
    }

    public ArrayList<UserEntity> getUserEntity() {

        ArrayList<UserEntity> allUserEntity = new ArrayList<>();

        String[] strName = {"Operator", "Manager"};
        String[] strPassword = {"operator.smartamcu", "manager"};
        String[] strUserId = {UserDetails.OPERATOR, UserDetails.MANAGER};
        String[] strRole = {"Operator", "Manager"};
        String[] strMobile = {"", ""};

        for (int i = 0; i < strName.length; i++) {
            UserEntity userEntity = new UserEntity();

            // userEntity.emailId = strEmailId[i];
            userEntity.mobile = strMobile[i];
            userEntity.name = strName[i];
            userEntity.password = strPassword[i];
            userEntity.role = strRole[i];
            userEntity.userId = strUserId[i].toUpperCase(Locale.ENGLISH);
            userEntity.centerId = String.valueOf(session.getSocietyColumnId());
            allUserEntity.add(userEntity);

        }

        return allUserEntity;
    }

    public ArrayList<SampleDataEntity> getDefaultSample() {

        int digitLength =
                preferences.getInt(ConfigurationConstants.FARM_ID_DIGIT, 4);

        ArrayList<SampleDataEntity> allSampleEntity = new ArrayList<>();
        String[] strName = {"Cleaning", "Rinsing", "Sample Test", "Water Test", "Essae Cleaning"};
        String[] strID = new String[5];

        if (digitLength > 5) {
            strID[0] = "009997";
            strID[1] = "009998";
            strID[2] = "000999";
            strID[3] = "000991";
            strID[4] = "009996";
        } else if (digitLength > 4) {
            strID[0] = "09997";
            strID[1] = "09998";
            strID[2] = "00999";
            strID[3] = "00991";
            strID[4] = "09996";
        } else {
            strID[0] = "9997";
            strID[1] = "9998";
            strID[2] = "0999";
            strID[3] = "0991";
            strID[4] = "9996";
        }
        for (int i = 0; i < strName.length; i++) {
            SampleDataEntity sample = new SampleDataEntity();
            sample.sampleSocId = String.valueOf(new SessionManager(mContext).getSocietyColumnId());
            sample.sampleId = strID[i];
            sample.sampleMode = strName[i];
            sample.sampleBarcode = "";
            sample.sampleIsFat = "false";
            sample.sampleIsWeigh = "false";
            allSampleEntity.add(sample);
        }
        return allSampleEntity;
    }


}
