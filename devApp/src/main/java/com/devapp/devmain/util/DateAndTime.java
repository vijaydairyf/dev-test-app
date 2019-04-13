package com.devapp.devmain.util;


import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by u_pendra on 27/12/16.
 */

public class DateAndTime {

    public Calendar getCalendarInstance() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        return calendar;

    }

}
