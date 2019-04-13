package com.devapp.devmain.peripherals.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devApp.R;

public class WirelessConfigActivity extends AppCompatActivity {
    Context context = this;
    EditText maEt, wsEt, rduEt, printerEt;
    String ma, ws, rdu, printer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireless_config);
        maEt = (EditText) findViewById(R.id.ma_et);
        wsEt = (EditText) findViewById(R.id.ws_et);
        rduEt = (EditText) findViewById(R.id.rdu_et);
        printerEt = (EditText) findViewById(R.id.printer_et);
        setFieldValues();
    }

    private void setFieldValues() {
        maEt.setText(SmartCCConstants.maIp);
        wsEt.setText(SmartCCConstants.wsIp);
        rduEt.setText(SmartCCConstants.rduIp);
        printerEt.setText(SmartCCConstants.printerIp);
    }

    public void cancelActivity(View v) {
        finish();
    }

    public void saveDeviceIps(View v) {
        /*if (!emptyIps()) {
            SmartCCConstants.setMaIp(ma, context);
            SmartCCConstants.setWsIp(ws, context);
            SmartCCConstants.setRduIp(rdu, context);
            SmartCCConstants.setPrinterIp(printer, context);
            if (!duplicateIps()) {
                Toast.makeText(context, "Ips saved", Toast.LENGTH_SHORT).show();
                Util.restartApp(context);
            } else
                Toast.makeText(context, "Devices cannot have duplicate Ips", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(context, "Ips cannot be empty or contain spaces", Toast.LENGTH_SHORT).show();*/
    }

    private boolean duplicateIps() {
        if (!ma.equals(ws) && !ma.equals(rdu) && !ma.equals(printer) && !ws.equals(rdu)
                && !ws.equals(printer) && !rdu.equals(printer))
            return false;
        return true;
    }

    private boolean emptyIps() {
        ma = maEt.getText().toString();
        ws = wsEt.getText().toString();
        rdu = rduEt.getText().toString();
        printer = printerEt.getText().toString();
        if ((ma.equals("") || ws.equals("") || rdu.equals("") || printer.equals("")) ||
                (ma.startsWith(" ") || ws.startsWith(" ") || rdu.startsWith(" ") || printer.startsWith(" ")) ||
                (ma.startsWith("\n") || ws.startsWith("\n") || rdu.startsWith("\n") || printer.startsWith("\n")))
            return true;

        return false;
    }
}
