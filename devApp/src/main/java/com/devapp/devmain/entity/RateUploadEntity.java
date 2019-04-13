package com.devapp.devmain.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class RateUploadEntity implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public String startDate;
    public String endDate;
    public boolean isActive;
    public String milkType;
    public String societyId;
    public String rateChartName;

    public ArrayList<RateUpload> valuesList;

}
