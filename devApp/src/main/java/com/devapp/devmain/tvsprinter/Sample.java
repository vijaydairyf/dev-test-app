package com.devapp.devmain.tvsprinter;

import com.andprn.jpos.command.ESCPOS;
import com.andprn.jpos.printer.ESCPOSPrinter;
import com.andprn.port.android.DeviceConnection;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.util.AppConstants;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class Sample {
    final char ESC = ESCPOS.ESC;
    final char LF = ESCPOS.LF;
    ESCPOSPrinter posPtr;

    public Sample(DeviceConnection connection) {
        if (AmcuConfig.getInstance().getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)) {
            posPtr = new com.andprn.jpos.printer.ESCPOSPrinter(Charset.forName("x-ISCII91").name(), connection);

        } else if (AmcuConfig.getInstance().getPrinter().equalsIgnoreCase("TVS")) {
            posPtr = new com.andprn.jpos.printer.ESCPOSPrinter(connection);

        } else {
            posPtr = new com.andprn.jpos.printer.ESCPOSPrinter(connection);
        }

        try {
            printHeaderCommand();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void printHeaderCommand() throws UnsupportedEncodingException {

        if (AmcuConfig.getInstance().getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            String command1 = ESC + "~";
            String command2 = ESC + "~k4";

            posPtr.sendByte(command1.getBytes(Charset.forName("x-ISCII91").name()));
            posPtr.sendByte(command2.getBytes(Charset.forName("x-ISCII91").name()));
        } else if (AmcuConfig.getInstance().getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)) {
            String command1 = ESC + "~";
            String command2 = ESC + "~k2";

            posPtr.sendByte(command1.getBytes(Charset.forName("x-ISCII91").name()));
            posPtr.sendByte(command2.getBytes(Charset.forName("x-ISCII91").name()));
        }
    }


    public void samplePrint(String message) throws UnsupportedEncodingException {


        if (AmcuConfig.getInstance().getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {


//            posPtr.printNormal(ESC + "~");
//            posPtr.printNormal(ESC + "~k4");


            byte[] byteArray = message.getBytes(Charset.forName("x-ISCII91").name());
            byte[] tempByte = new byte[byteArray.length];

            int j = 0;
            //-17 for junk char for tamil letter 'Ka' 66 for 'B' and 68 for 'D'
            boolean IS_QUESTION_MARK = false;
            boolean IS_B = false;
            for (int i = 0; i < byteArray.length; i++) {
                if (byteArray[i] == -17 &&
                        ((i + 1) < byteArray.length && (byteArray[i + 1] == 68
                                || byteArray[i + 1] == 66))) {

                } else if ((byteArray[i] == 68 || byteArray[i] == 66) &&
                        ((i - 1) >= 0 && byteArray[i - 1] == -17)) {

                } else {
                    tempByte[j] = byteArray[i];
                    j++;
                }

            }
            posPtr.sendByte(tempByte);
        } else if (AmcuConfig.getInstance().getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)) {
//            posPtr.printNormal(ESC + "~");
//            posPtr.printNormal(ESC + "~k2");


            byte[] byteArray = message.getBytes(Charset.forName("x-ISCII91").name());
            byte[] tempByte = new byte[byteArray.length];

            int j = 0;
            boolean IS_QUESTION_MARK = false;
            boolean IS_B = false;
            for (int i = 0; i < byteArray.length; i++) {
                if (byteArray[i] == -17 &&
                        ((i + 1) < byteArray.length && byteArray[i + 1] == 66)) {

                } else if (byteArray[i] == 66 &&
                        ((i - 1) >= 0 && byteArray[i - 1] == -17)) {

                } else {
                    tempByte[j] = byteArray[i];
                    j++;
                }

            }
            posPtr.sendByte(tempByte);
        } else {
            posPtr.printString(message);
        }


    }

    public void Sample1(String printText, int lineFeed)
            throws UnsupportedEncodingException {
        posPtr.printNormal(printText);

        // posPtr.printText(printText, ESCPOSConst.LK_ALIGNMENT_CENTER,
        // ESCPOSConst.LK_FNT_DEFAULT, 15);

        if (lineFeed > 0)
            posPtr.lineFeed(1);


    }

    public void SampleprintHeader(String printText, int lineFeed)
            throws UnsupportedEncodingException {
        posPtr.printNormal(printText);

        if (lineFeed > 0) {
            posPtr.lineFeed(lineFeed);
        }


    }

    public void printReportEntry(String printText)
            throws UnsupportedEncodingException {
        posPtr.printNormal(printText);

    }

    public void printReportHeaderCenter(String data)
            throws UnsupportedEncodingException {

        posPtr.printText(data, 1, 1, 10);

    }

    public void printReportHeaderLeft(String data)
            throws UnsupportedEncodingException {

        posPtr.printText(data, 0, 1, 10);

    }

    /*public void printReportHeaderRight(String data)
            throws UnsupportedEncodingException {

        posPtr.printText(data, ESCPOSConst.LK_ALIGNMENT_RIGHT,
                ESCPOSConst.LK_FNT_DEFAULT, 10);
        posPtr.lineFeed(1);

    }*/

    public void Sample2() throws UnsupportedEncodingException {

        posPtr.printNormal("TVS Electronics Limited - Dot Matrix Android Sample Text Print- "
                + LF + LF);
        posPtr.printNormal("TEL 044-4567890" + LF);
        posPtr.printNormal("Thank you for coming to our shop!" + LF + LF);

        posPtr.printNormal("BOILED RICE 3KG BOX       75.00" + LF);
        posPtr.printNormal("RAW RICE 4KG BOX          80.00" + LF);
        posPtr.printNormal("Milk Drink                 9.00" + LF);
        posPtr.printNormal("Lemons                    40.00" + LF);
        posPtr.printNormal("Horlicks                 150.00" + LF);
        posPtr.printNormal("CARDOMOM 100G             17.00" + LF);
        posPtr.printNormal("SURF EXCEL 1 KG           63.00" + LF);
        posPtr.printNormal("CASHEWNUT  100G           30.00" + LF);
        // posPtr.printNormal("MOONG DALL KG			  33.00" + LF);
        posPtr.printNormal("SPINZ TALC                39.00" + LF);
        posPtr.printNormal("PONDS CREAM               30.00" + LF);
        posPtr.printNormal("HONEY 250 G               25.00" + LF);
        posPtr.printNormal("SALT 1 KG                  6.00" + LF);
        posPtr.printNormal("SUGAR 1 KG                18.00" + LF);
        posPtr.printNormal("CINTHOL BATH SOAP         16.00" + LF);
        posPtr.printNormal("TURMERIC 100g              8.00" + LF);
        posPtr.printNormal("LACTOGEN  1 KG           226.00" + LF);
        posPtr.printNormal("CHANNA DAL KG.            29.00" + LF);
        posPtr.printNormal("Excluded tax             150.00" + LF);

        posPtr.printNormal("Tax(5%)                    60.00" + LF);
        posPtr.printNormal("Total   550.00" + LF + LF);
        posPtr.printNormal("Payment                  610.00" + LF);

        posPtr.printNormal("Thank you" + LF);

        posPtr.lineFeed(4);

    }

   /* public void Sample3() throws IOException {
        posPtr.printBitmap("//sdcard//temp//test//logo_s.jpg",
                ESCPOSConst.LK_ALIGNMENT_CENTER);
        posPtr.printBitmap("//sdcard//temp//test//danmark_windmill.jpg",
                ESCPOSConst.LK_ALIGNMENT_LEFT);
        posPtr.printBitmap("//sdcard//temp//test//sample_2.jpg",
                ESCPOSConst.LK_ALIGNMENT_RIGHT);
        posPtr.printBitmap("//sdcard//temp//test//sample_3.jpg",
                ESCPOSConst.LK_ALIGNMENT_CENTER);
        posPtr.printBitmap("//sdcard//temp//test//sample_4.jpg",
                ESCPOSConst.LK_ALIGNMENT_LEFT);

        posPtr.lineFeed(4);
        // POSPrinter Only.

    }*/

    /*public int Sample4() throws IOException {
        int check = posPtr.printerCheck();
        if (check == ESCPOSConst.LK_SUCCESS) {
            Log.i("Sample", "sts= " + posPtr.status());
            return posPtr.status();
        } else {
            Log.i("Sample", "Retrieve Status Failed");
            return -1;
        }
    }*/

    public int Sample5() throws IOException, InterruptedException {
        return posPtr.printerSts();
    }
}
