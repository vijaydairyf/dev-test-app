package com.devapp.devmain.services;

import com.devapp.devmain.user.Util;

import java.io.File;

/**
 * Created by Upendra on 5/28/2015.
 */
public class DeleteExcelService {

    public DeleteExcelService() {

    }

    public void deleteExcel() {
        Long currentTime = System.currentTimeMillis();
        Long oneMonthTime = 30 * 24 * 60 * 60 * 1000L;
        Long gapTime = currentTime - oneMonthTime;

        File dir = new File(Util.getSDCardPath(), "smartAmcuReports");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File file = new File(dir, children[i]);
                Long mDate = file.lastModified();

                if (mDate < gapTime) {
                    file.delete();
                }

            }
        }

    }

}
