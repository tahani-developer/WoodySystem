package com.falconssoft.woodysystem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.PlannedPL;
import com.falconssoft.woodysystem.stage_two.PlannedUnplanned;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.itextpdf.text.Element.ALIGN_CENTER;

public class ExportToExcel {

    private Context context;


    Document doc;
    File file;
    PdfWriter docWriter = null;
    //    PDFView pdfView;
    File pdfFileName;
    BaseFont base;

    {
        try {
            base = BaseFont.createFont("/assets/arialuni.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Font arabicFont = new Font(base, 11f);


    public ExportToExcel(Context context){
        this.context = context;
    }

    public void exportPlannedUnplanned(List<PlannedPL> list) {
        PdfPTable pdfPTable = new PdfPTable(7);
        PdfPTable pdfPTableHeader = new PdfPTable(7);

        createPDF("Planned Unplanned Inventory Report" + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

        int directionOfHeader= Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        directionOfHeader =Element.ALIGN_RIGHT;

        insertCell(pdfPTable, context.getString(R.string.thickness), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.width), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.length), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_of_pieces), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_bundle), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cubic), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {
            insertCell(pdfPTable, String.valueOf(list.get(i).getThickness()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getWidth()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getLength()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getGrade()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfCopies()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(String.format("%.3f", (list.get(i).getCubic() ))), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

//        insertCell(pdfPTableHeader, "Falcon Soft ", Element.ALIGN_CENTER, 4, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.market_report_), Element.ALIGN_CENTER, 4, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, "", Element.ALIGN_LEFT, 4, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.from_date) + fromDateT, directionOfHeader, 1, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, "", Element.ALIGN_LEFT, 2, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.to_date) + ToDateT, directionOfHeader, 1, arabicFont, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context,context.getString(R.string.export_to_excel), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();
        showPdf(pdfFileName);

    }

    public void exportUnloadPackingList(List<PlannedPL> list) {
        PdfPTable pdfPTable = new PdfPTable(10);
        PdfPTable pdfPTableHeader = new PdfPTable(10);

        createPDF("Planned Packing List Report" + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);

        insertCell(pdfPTable, "#", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cust), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "PL#", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.destination), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.order_no), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_of_pieces), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_bundle), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cubic), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {
            insertCell(pdfPTable, ""+ (i+1), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCustName()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getPackingList()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDestination()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getOrderNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getSupplier()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getGrade()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfCopies()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.format("%.3f", (list.get(i).getCubic() )), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context,context.getString(R.string.export_to_excel), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();
        showPdf(pdfFileName);

    }

    public void exportLoadPackingList(List<PlannedPL> list) {
        PdfPTable pdfPTable = new PdfPTable(10);
        PdfPTable pdfPTableHeader = new PdfPTable(10);

        createPDF("Loaded Packing List Report" + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);

        insertCell(pdfPTable, "#", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cust), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "PL#", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.destination), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.order_no), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_of_pieces), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_bundle), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cubic), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {
            insertCell(pdfPTable, ""+ (i+1), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCustName()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getPackingList()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDestination()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getOrderNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getSupplier()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getGrade()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfCopies()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.format("%.3f", (list.get(i).getCubic() )), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context,context.getString(R.string.export_to_excel), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();
        showPdf(pdfFileName);

    }



    void createPDF(String fileName) {
        doc = new Document();
        docWriter = null;
        Log.e("path45", "create" + "-->" + Environment.getExternalStorageDirectory().getPath());
        try {


            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/ReportRos/";
            file = new File(directory_path);
            if (!file.exists()) {
                file.mkdirs();
            }
            String targetPdf = directory_path + fileName;
            File path = new File(targetPdf);

            docWriter = PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.setPageSize(PageSize.A4);//size of page
            //open document
            doc.open();
            Paragraph paragraph = new Paragraph();
            paragraph.add("");
            doc.add(paragraph);

            Log.e("path44", "" + targetPdf);
            pdfFileName=path;

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void insertCell(PdfPTable table, String text, int align, int colspan, Font font, BaseColor border) {

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        cell.setBorderColor(border);
        //in case there is no text and you wan to create an empty row
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }

        cell.setRunDirection(PdfWriter.RUN_DIRECTION_RTL); //for make arabic string from right to left ...

//        cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        //add the call to the table
        table.addCell(cell);

    }

    void endDocPdf() {

        if (doc != null) {
            //close the document
            doc.close();
        }
        if (docWriter != null) {
            //close the writer
            docWriter.close();
        }
    }

    void showPdf(File path){

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(path), "application/pdf");
        context.startActivity(intent);
    }

}
