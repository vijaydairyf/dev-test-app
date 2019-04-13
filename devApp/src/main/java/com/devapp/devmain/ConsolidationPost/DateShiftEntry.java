package com.devapp.devmain.ConsolidationPost;

/**
 * Created by u_pendra on 17/1/18.
 */

public class DateShiftEntry implements Comparable {
    String date;
    String shift;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }


    @Override
    public int compareTo(Object object) {


        if (object instanceof DateShiftEntry) {
            try {
                DateShiftEntry dateShiftEntry = (DateShiftEntry) object;

                int date = Integer.parseInt(this.date.replace("-", ""));
                int compareDate = Integer.parseInt(dateShiftEntry.date.replace("-", ""));

                if (date > compareDate) {
                    return 1;
                } else if (date == compareDate) {

                    if (this.shift.equalsIgnoreCase("EVENING")
                            && dateShiftEntry.shift.equalsIgnoreCase("MORNING")) {
                        return 1;
                    } else if (dateShiftEntry.shift.equalsIgnoreCase("EVENING")
                            && this.shift.equalsIgnoreCase("MORNING")) {
                        return -1;
                    } else {
                        return 0;
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
