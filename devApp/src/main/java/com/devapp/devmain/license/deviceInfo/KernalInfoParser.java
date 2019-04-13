package com.devapp.devmain.license.deviceInfo;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yyy on 23/6/16.
 */
public class KernalInfoParser {
    String linuxVersion;

    public static String getLinuxVersion() {
        ProcessBuilder cmd;
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/sys/kernel/osrelease"};
            cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "3.10.20-x86_64_moor-265388-ga4369b3";
    }

    public static String getVersionTime() {
        ProcessBuilder cmd;
        String result = "";
        try {
            String[] args = {"/system/bin/cat", "/proc/sys/kernel/version"};
            cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return "#106 SMP PREEMPT Tue Jan 19 12:11:09 CST 2016";
    }

    public String versionCode() {
        ProcessBuilder cmd;
        String result = "";

        try {
            String[] args = {"/system/bin/cat", "/proc/version"};
            cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[1024];
            while (in.read(re) != -1) {
                result = result + new String(re);
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }
}
