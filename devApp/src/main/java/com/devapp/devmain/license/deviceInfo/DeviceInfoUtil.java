package com.devapp.devmain.license.deviceInfo;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by yyy on 29/6/16.
 */
public enum DeviceInfoUtil {
    Instance;

    public void setIPTableForLicense() {
        ArrayList<String> cmds = new ArrayList<String>();

        cmds.add("iptables -F");
        cmds.add("iptables -I INPUT 1 -i lo -j ACCEPT");
        cmds.add("iptables -A OUTPUT -p udp --dport 53 -j ACCEPT");
        String smptMail = "smtp.googlemail.com";
        cmds.add("iptables -A OUTPUT -p tcp --dport 465 -j ACCEPT");
        cmds.add("iptables -A OUTPUT -p tcp -d amcugw2.smartmoo.com --dport 80 -j ACCEPT");
        cmds.add("iptables -A OUTPUT -p tcp -d amcugw2.smartmoo.com --dport 443 -j ACCEPT");
        cmds.add("iptables -A INPUT -p tcp --sport 465 -m state --state ESTABLISHED -j ACCEPT");
        cmds.add("iptables -A INPUT -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT");
        cmds.add("iptables -A OUTPUT -p udp -d 2.android.pool.ntp.org --dport 123 -j ACCEPT");
        cmds.add("iptables -A INPUT -p udp  -s 2.android.pool.ntp.org --sport 123 -j ACCEPT");

        //Drop everything
        cmds.add("iptables -P INPUT DROP");
        cmds.add("iptables -P OUTPUT DROP");
        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            for (String tmpCmd : cmds) {

                os.writeBytes(tmpCmd + "\n");
                os.flush();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            //  displayErrorToast("Ip table setting Done", ctx);
            int i = process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
            //  displayErrorToast("Ip table setting Exp2", ctx);

        } catch (InterruptedException e) {
            //  displayErrorToast("Ip table setting Exp1", ctx);
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

    }


}