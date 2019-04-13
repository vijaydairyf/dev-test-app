package com.devapp.devmain.cloud;

import android.content.Context;

import java.io.File;

//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Element;
//import com.itextpdf.text.Phrase;
//import com.itextpdf.text.pdf.BaseFont;
//import com.itextpdf.text.pdf.PdfContentByte;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;

/**
 * Created by Upendra on 12/19/2015.
 */
public class PDFDocument {

    private File pdfFile;
    private String filename = "Sample.pdf";
    private String filepath = "MyInvoices";

    private Context mContext;

    public PDFDocument(Context ctx) {
        this.mContext = ctx;
    }


    public void generatePDF(String personName) {

        //create a new document
//        Document document = new Document();
//
//        try {
//
//            PdfWriter docWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
//            document.read();
//
//
//            PdfContentByte cb = docWriter.getDirectContent();
//            //initialize fonts for text printing
//            initializeFonts();
//
//            //the company logo is stored in the assets which is read only
//            //get the logo and print on the document
////            InputStream inputStream = getAssets().read("olympic_logo.png");
////            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
////            ByteArrayOutputStream stream = new ByteArrayOutputStream();
////            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
////            Image companyLogo = Image.getInstance(stream.toByteArray());
////            companyLogo.setAbsolutePosition(25,700);
////            companyLogo.scalePercent(25);
//         //   document.add(companyLogo);
//
//            //creating a sample invoice with some customer data
//            createHeadings(cb,400,780,"Company Name");
//            createHeadings(cb,400,765,"Address Line 1");
//            createHeadings(cb,400,750,"Address Line 2");
//            createHeadings(cb,400,735,"City, State - ZipCode");
//            createHeadings(cb,400,720,"Country");
//
//            //list all the products sold to the customer
//            float[] columnWidths = {1.5f, 2f, 5f, 2f,2f};
//            //create PDF table with the given widths
//            PdfPTable table = new PdfPTable(columnWidths);
//            // set table width a percentage of the page width
//            table.setTotalWidth(500f);
//
//            PdfPCell cell = new PdfPCell(new Phrase("Qty"));
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(cell);
//            cell = new PdfPCell(new Phrase("Item Number"));
//            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(cell);
//            cell = new PdfPCell(new Phrase("Item Description"));
//            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//            table.addCell(cell);
//            cell = new PdfPCell(new Phrase("Price"));
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(cell);
//            cell = new PdfPCell(new Phrase("Ext Price"));
//            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
//            table.addCell(cell);
//            table.setHeaderRows(1);
//
//            DecimalFormat df = new DecimalFormat("0.00");
//            for(int i=0; i < 15; i++ ){
//                double price = Double.valueOf(df.format(Math.random() * 10));
//                double extPrice = price * (i+1) ;
//                table.addCell(String.valueOf(i+1));
//                table.addCell("ITEM" + String.valueOf(i+1));
//                table.addCell("Product Description - SIZE " + String.valueOf(i+1));
//                table.addCell(df.format(price));
//                table.addCell(df.format(extPrice));
//            }
//
//            //absolute location to print the PDF table from
//            table.writeSelectedRows(0, -1, document.leftMargin(), 650, docWriter.getDirectContent());
//
//            //print the signature image along with the persons name
////            inputStream = getAssets().read("signature.png");
////            bmp = BitmapFactory.decodeStream(inputStream);
////            stream = new ByteArrayOutputStream();
////            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
////            Image signature = Image.getInstance(stream.toByteArray());
////            signature.setAbsolutePosition(400f, 150f);
////            signature.scalePercent(25f);
////            document.add(signature);
//
//            createHeadings(cb,450,135,personName);
//
//            document.close();
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//
//        //PDF file is now ready to be sent to the bluetooth printer using PrintShare
//        Intent i = new Intent(Intent.ACTION_VIEW);
//        i.setPackage("com.dynamixsoftware.printershare");
//        i.setDataAndType(Uri.fromFile(pdfFile),"application/pdf");
//        mContext.startActivity(i);

    }

//
//    private BaseFont bfBold;
//    private void initializeFonts(){
//
//
//        try {
//            bfBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
//
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void createHeadings(PdfContentByte cb, float x, float y, String text){
//
//        cb.beginText();
//        cb.setFontAndSize(bfBold, 8);
//        cb.setTextMatrix(x,y);
//        cb.showText(text.trim());
//        cb.endText();
//
//    }


}
