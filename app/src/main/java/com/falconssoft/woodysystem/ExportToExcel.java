package com.falconssoft.woodysystem;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.NewRowInfo;
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
    private int directionOfHeader = Element.ALIGN_RIGHT;

    {
        try {
            base = BaseFont.createFont("/assets/arialuni.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Font arabicFont = new Font(base, 10f);
    Font arabicFontHeader = new Font(base, 11f);


    public ExportToExcel(Context context) {
        this.context = context;
    }

    public void exportPlannedUnplanned(List<PlannedPL> list, String headerDate, String date) {
        PdfPTable pdfPTable = new PdfPTable(7);
        PdfPTable pdfPTableHeader = new PdfPTable(7);

        createPDF("Planned Unplanned Inventory Report"+ headerDate + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;

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
            insertCell(pdfPTable, String.valueOf(String.format("%.3f", (list.get(i).getCubic()))), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTableHeader, "Planned Unplanned Inventory Report", Element.ALIGN_CENTER, 7, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.date) + ": " + date, Element.ALIGN_RIGHT, 7, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + supplier, Element.ALIGN_LEFT, 6, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.grade) + ": " + grade, Element.ALIGN_RIGHT, 6, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.cust) + ": " + customer, Element.ALIGN_LEFT, 6, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);

        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_excel), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();

        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                showPdf(pdfFileName);
                Log.v("", "Permission is granted");
            } else {

                Log.v("", "Permission is revoked");
                ActivityCompat.requestPermissions(
                        (Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        } else { // permission is automatically granted on sdk<23 upon
            // installation
            showPdf(pdfFileName);
            Log.v("", "Permission is granted");
        }
//        showPdf(pdfFileName);

    }

    public void exportUnloadPackingList(List<PlannedPL> list, String customer, String supplier, String headerDate, String grade, String date) {
        PdfPTable pdfPTable = new PdfPTable(12);
        PdfPTable pdfPTableHeader = new PdfPTable(12);

        createPDF("Planned Packing List Report" + headerDate + "_.pdf");
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
        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_of_pieces), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_bundle), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cubic), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {
            insertCell(pdfPTable, "" + (i + 1), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCustName()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getPackingList()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDestination()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getOrderNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getSupplier()), Element.ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getGrade()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfCopies()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.format("%.3f", (list.get(i).getCubic())), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTableHeader, "Planned Packing List Report", Element.ALIGN_CENTER, 12, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.date) + ": " + date, Element.ALIGN_RIGHT, 6, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + supplier, Element.ALIGN_LEFT, 6, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.grade) + ": " + grade, Element.ALIGN_RIGHT, 6, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.cust) + ": " + customer, Element.ALIGN_LEFT, 6, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_excel), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();
        showPdf(pdfFileName);

    }

    public void exportLoadPackingList(List<PlannedPL> list, String customer, String supplier, String headerDate, String grade, String date) {
        PdfPTable pdfPTable = new PdfPTable(10);
        PdfPTable pdfPTableHeader = new PdfPTable(10);

        createPDF("Loaded Packing List Report" + headerDate + "_.pdf");
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
            insertCell(pdfPTable, "" + (i + 1), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCustName()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getPackingList()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDestination()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getOrderNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getSupplier()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getGrade()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfCopies()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.format("%.3f", (list.get(i).getCubic())), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTableHeader, "Loaded Packing List Report", Element.ALIGN_CENTER, 12, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.date) + ": " + date, Element.ALIGN_RIGHT, 6, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + supplier, Element.ALIGN_LEFT, 6, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.grade) + ": " + grade, Element.ALIGN_RIGHT, 6, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.cust) + ": " + customer, Element.ALIGN_LEFT, 6, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);

        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_excel), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();
        showPdf(pdfFileName);

    }

    public void exportInventoryReport(List<BundleInfo> list, String location, String area, String grade, String fromDate, String toDate, String headerDate) {
        PdfPTable pdfPTable = new PdfPTable(15);
        PdfPTable pdfPTableHeader = new PdfPTable(15);

        createPDF("Inventory Report" + headerDate + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;

        insertCell(pdfPTable, context.getString(R.string.serial), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.bundle_no), ALIGN_CENTER, 4, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.thickness), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.width), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.length), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_of_pieces), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.location), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.area), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.p_list), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cubic), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {

            insertCell(pdfPTable, String.valueOf(list.get(i).getSerialNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getBundleNo()), Element.ALIGN_CENTER, 4, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int)list.get(i).getThickness()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int)list.get(i).getWidth()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getLength()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int)list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getGrade()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getLocation()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getArea()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getBackingList()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTableHeader, "Inventory Report ", Element.ALIGN_CENTER, 15, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.location) + ": " + location, Element.ALIGN_RIGHT, 8, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.from) + ": " + fromDate, Element.ALIGN_LEFT, 7, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.area) + ": " + area, Element.ALIGN_RIGHT, 8, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.to) + ": " + toDate, Element.ALIGN_LEFT, 7, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_excel), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();

        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                showPdf(pdfFileName);
                Log.v("", "Permission is granted");
            } else {

                Log.v("", "Permission is revoked");
                ActivityCompat.requestPermissions(
                        (Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        } else { // permission is automatically granted on sdk<23 upon
            // installation
            showPdf(pdfFileName);
            Log.v("", "Permission is granted");
        }
//        showPdf(pdfFileName);

    }

    public void exportReportOne(List<NewRowInfo> bundles ,List<NewRowInfo> list, String truck, String location, String fromDate, String toDate, String headerDate) {
        PdfPTable pdfPTable = new PdfPTable(10);
        PdfPTable pdfPTableHeader = new PdfPTable(10);

        createPDF("Report One" + headerDate + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;

        insertCell(pdfPTable, context.getString(R.string.truck), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 4, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.ttn), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.date_of_acceptance), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_bundle), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.rejected_pieces), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {

            insertCell(pdfPTable, String.valueOf(list.get(i).getTruckNo()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(findSupplier(bundles, list.get(i))), Element.ALIGN_CENTER, 4, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTtnNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDate()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNetBundles()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTotalRejectedNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

//            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
//            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTableHeader, "Report One", Element.ALIGN_CENTER, 10, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.location) + ": " + location, Element.ALIGN_RIGHT, 5, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.from) + ": " + fromDate, Element.ALIGN_LEFT, 5, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.truck_no) + ": " + truck, Element.ALIGN_RIGHT, 5, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.to) + ": " + toDate, Element.ALIGN_LEFT, 5, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_excel), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();

        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                showPdf(pdfFileName);
                Log.v("", "Permission is granted");
            } else {

                Log.v("", "Permission is revoked");
                ActivityCompat.requestPermissions(
                        (Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        } else { // permission is automatically granted on sdk<23 upon
            // installation
            showPdf(pdfFileName);
            Log.v("", "Permission is granted");
        }
//        showPdf(pdfFileName);

    }

    public void exportReportTwo(List<NewRowInfo> list, String fromDate, String toDate, String headerDate) {
        PdfPTable pdfPTable = new PdfPTable(9);
        PdfPTable pdfPTableHeader = new PdfPTable(9);

        createPDF("Report Two" + headerDate + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;

        insertCell(pdfPTable, context.getString(R.string.truck), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 4, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.ttn), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.date_of_acceptance), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_bundle), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.rejected_pieces), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {

            insertCell(pdfPTable, String.valueOf(list.get(i).getTruckNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getSupplierName()), Element.ALIGN_CENTER, 4, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTtnNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDate()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfBundles()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfRejected()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

//            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
//            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTableHeader, "Report Two", Element.ALIGN_CENTER, 9, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.location) + ": " + location, Element.ALIGN_RIGHT, 5, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.from) + ": " + fromDate, Element.ALIGN_RIGHT, 4, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.to) + ": " + toDate, Element.ALIGN_LEFT, 5, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.truck_no) + ": " + truck, Element.ALIGN_RIGHT, 5, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);


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

    public void exportInventoryReport(List<BundleInfo> list) {
        PdfPTable pdfPTable = new PdfPTable(11);
        PdfPTable pdfPTableHeader = new PdfPTable(11);

        createPDF("Inventory Report" + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);

        insertCell(pdfPTable, "Serial", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.bundle_no), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "TH", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "W", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "L", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "#Pieces", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.loc), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.area), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.p_list), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {
            insertCell(pdfPTable, String.valueOf(list.get(i).getSerialNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getBundleNo()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getThickness()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getWidth()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getLength()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getGrade()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getLocation()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getArea()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getBackingList()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_excel), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();

        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                showPdf(pdfFileName);
                Log.v("", "Permission is granted");
            } else {

                Log.v("", "Permission is revoked");
                ActivityCompat.requestPermissions(
                        (Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
            }
        } else { // permission is automatically granted on sdk<23 upon
            // installation
            showPdf(pdfFileName);
            Log.v("", "Permission is granted");
        }
//        showPdf(pdfFileName);

    }

    String findSupplier(List<NewRowInfo> bundles, NewRowInfo newRowInfo) {
        for (int i = 0; i < bundles.size(); i++) {
            if (newRowInfo.getSerial().equals(bundles.get(i).getSerial()))
                return bundles.get(i).getSupplierName();
        }

        return "----";
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
            pdfFileName = path;

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

    void showPdf(File path) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", path);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "application/pdf");//intent.setDataAndType(Uri.fromFile(path), "application/pdf");
        context.startActivity(intent);
    }

}
