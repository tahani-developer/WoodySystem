package com.falconssoft.woodysystem;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Orders;
import com.falconssoft.woodysystem.models.PaymentAccountSupplier;
import com.falconssoft.woodysystem.models.PlannedPL;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.itextpdf.text.Element.ALIGN_CENTER;

public class ExportToPDF {

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
        }catch (Exception e){

        }
    }

    Font arabicFont = new Font(base, 10f);
    Font arabicFontHeader = new Font(base, 11f);


    public ExportToPDF(Context context) {
        this.context = context;
    }

    public void exportPlannedUnplanned(List<PlannedPL> list, String headerDate, String date, String bundle, String cubic) {
        PdfPTable pdfPTable = new PdfPTable(7);
        PdfPTable pdfPTableHeader = new PdfPTable(7);

        createPDF("Planned Unplanned Inventory Report" + headerDate + "_.pdf");
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
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getThickness()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getWidth()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getLength()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getGrade()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfCopies()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(String.format("%.3f", (list.get(i).getCubic()))), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTable, "Total", Element.ALIGN_RIGHT, 5, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "" + bundle, Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "" + cubic, Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);


        insertCell(pdfPTableHeader, "Planned Unplanned Inventory Report", Element.ALIGN_CENTER, 7, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.date) + ": " + date, Element.ALIGN_RIGHT, 7, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + supplier, Element.ALIGN_LEFT, 6, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.grade) + ": " + grade, Element.ALIGN_RIGHT, 6, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.cust) + ": " + customer, Element.ALIGN_LEFT, 6, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);

        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

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

    public void exportUnloadPackingList(List<PlannedPL> list, String customer, String supplier, String headerDate, String grade, String date, String bundle, String cubic) {
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
        insertCell(pdfPTable, context.getString(R.string.destination), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.order_no), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_of_pieces), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_bundle), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cubic), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {
            insertCell(pdfPTable, "" + (i + 1), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCustName()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getPackingList()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDestination()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getOrderNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getSupplier()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getGrade()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfCopies()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.format("%.3f", (list.get(i).getCubic())), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTable, "Total", Element.ALIGN_RIGHT, 10, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "" + bundle, Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "" + cubic, Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTableHeader, "Planned Packing List Report", Element.ALIGN_CENTER, 12, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.date) + ": " + date, Element.ALIGN_RIGHT, 6, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + supplier, Element.ALIGN_LEFT, 6, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.grade) + ": " + grade, Element.ALIGN_RIGHT, 6, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.cust) + ": " + customer, Element.ALIGN_LEFT, 6, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();
        showPdf(pdfFileName);

    }

    public void exportLoadPackingList(List<PlannedPL> list, String customer, String supplier, String headerDate, String grade, String date, String bundle, String cubic) {
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
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfCopies()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.format("%.3f", (list.get(i).getCubic())), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTable, "Total", Element.ALIGN_RIGHT, 8, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "" + bundle, Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "" + cubic, Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTableHeader, "Loaded Packing List Report", Element.ALIGN_CENTER, 12, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.date) + ": " + date, Element.ALIGN_RIGHT, 6, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + supplier, Element.ALIGN_LEFT, 6, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.grade) + ": " + grade, Element.ALIGN_RIGHT, 6, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.cust) + ": " + customer, Element.ALIGN_LEFT, 6, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);

        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

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
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getThickness()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getWidth()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getLength()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
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
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

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

    public void exportReportOne(List<NewRowInfo> bundles, List<NewRowInfo> list, String truck, String location, String fromDate, String toDate, String headerDate,String TotalCubic,String TotalCubicRej,String totalBundel,String totalReject,String totalACbm) {
        PdfPTable pdfPTable = new PdfPTable(13);
        PdfPTable pdfPTableHeader = new PdfPTable(13);

        createPDF("Report One" + headerDate + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;

        insertCell(pdfPTable, context.getString(R.string.truck), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.ttn), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.date_of_acceptance), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_bundle), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.rejected_pieces), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.truck_cbm), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.rejcmb), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cbmAccept), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {

            insertCell(pdfPTable, String.valueOf(list.get(i).getTruckNo()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(findSupplier(bundles, list.get(i))), Element.ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTtnNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDate()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNetBundles()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTotalRejectedNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCubic()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCubicRej()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            double acc=list.get(i).getCubic()-list.get(i).getCubicRej();
            insertCell(pdfPTable, String.format("%.3f", acc), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
//            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }
        insertCell(pdfPTable,"Total", Element.ALIGN_LEFT, 8, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(totalBundel), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(totalReject), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(TotalCubic), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(TotalCubicRej), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(totalACbm), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTableHeader, "Accepted Truck Report", Element.ALIGN_CENTER, 13, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.location) + ": " + location, Element.ALIGN_RIGHT, 6, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.from) + ": " + fromDate, Element.ALIGN_LEFT, 7, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, " "/*context.getString(R.string.truck_no) + ": " + truck*/, Element.ALIGN_RIGHT, 6, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.to) + ": " + toDate, Element.ALIGN_LEFT, 7, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

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
    public void exportSupplierAcceptance (List<NewRowInfo> list, String truck, String grad, String fromDate, String toDate, String supplier,String TotalCubic,String TotalCubicRej,String totalBundel,String totalReject,String totalACbm) {
        PdfPTable pdfPTable = new PdfPTable(14);
        PdfPTable pdfPTableHeader = new PdfPTable(14);

        createPDF("AcceptanceSupplierReport"  + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;

        insertCell(pdfPTable, context.getString(R.string.truck), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.ttn_no), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.thickness), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.width), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.length), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_bundle), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.date_of_acceptance), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_pieces_accept), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.truck_cbm), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_of_p_reject), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.rejcmb), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cbmAccept), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {

            insertCell(pdfPTable, String.valueOf(list.get(i).getTruckNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTtnNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getThickness()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getWidth()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getLength()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfBundles()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDate()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTruckCMB()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTotalRejectedNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCbmRej()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCbmAccept()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getGrade()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//
//            double acc=list.get(i).getCubic()-list.get(i).getCubicRej();
//            insertCell(pdfPTable, String.format("%.3f", acc), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
//            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }
        insertCell(pdfPTable,"Total", Element.ALIGN_LEFT, 9, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(TotalCubic), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(totalReject), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(TotalCubicRej), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(totalACbm), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTableHeader, "Accepted Supplier Report", Element.ALIGN_CENTER, 14, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + supplier, Element.ALIGN_RIGHT, 7, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.from) + ": " + fromDate, Element.ALIGN_LEFT, 7, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, " "+context.getString(R.string.truck_no) + ": " + truck, Element.ALIGN_RIGHT, 7, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.to) + ": " + toDate, Element.ALIGN_LEFT, 7, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

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
    public void exportSupplierAcceptanceForEmail (List<NewRowInfo> list, String truck, String grad, String fromDate, String toDate, String supplier,String TotalCubic,String TotalCubicRej,String totalBundel,String totalReject,String totalACbm) {
        PdfPTable pdfPTable = new PdfPTable(14);
        PdfPTable pdfPTableHeader = new PdfPTable(14);

        createPDFForSendEmail("AcceptanceSupplierReport"  + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;

        insertCell(pdfPTable, context.getString(R.string.truck), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.ttn_no), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.thickness), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.width), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.length), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_bundle), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.date_of_acceptance), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_pieces_accept), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.truck_cbm), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_of_p_reject), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.rejcmb), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cbmAccept), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {

            insertCell(pdfPTable, String.valueOf(list.get(i).getTruckNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTtnNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getThickness()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getWidth()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getLength()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfBundles()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDate()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTruckCMB()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTotalRejectedNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCbmRej()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCbmAccept()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getGrade()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//
//            double acc=list.get(i).getCubic()-list.get(i).getCubicRej();
//            insertCell(pdfPTable, String.format("%.3f", acc), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
//            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }
        insertCell(pdfPTable,"Total", Element.ALIGN_LEFT, 9, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(TotalCubic), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(totalReject), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(TotalCubicRej), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(totalACbm), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);


        insertCell(pdfPTableHeader, "Accepted Supplier Report", Element.ALIGN_CENTER, 14, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + supplier, Element.ALIGN_RIGHT, 7, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.from) + ": " + fromDate, Element.ALIGN_LEFT, 7, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, " "+context.getString(R.string.truck_no) + ": " + truck, Element.ALIGN_RIGHT, 7, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.to) + ": " + toDate, Element.ALIGN_LEFT, 7, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();

        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
               // showPdf(pdfFileName);
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
            //showPdf(pdfFileName);
            Log.v("", "Permission is granted");
        }
//        showPdf(pdfFileName);

    }

    public void exportTruckAcceptance(List<NewRowInfo> bundles,NewRowInfo newRowInfoMaster, String headerDate,String totalTruck,String totalRej,String totRejCbm , String totalACbm) {
        PdfPTable pdfPTable = new PdfPTable(25);
        PdfPTable pdfPTableHeader = new PdfPTable(25);

        createPDF("TruckAcceptance"  + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;
        insertCell(pdfPTable, context.getString(R.string.truck_no), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.thickness), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTable, context.getString(R.string.width), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.length), ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_pieces_accept), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.bundle_no), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.truck_cbm), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_of_p_reject), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.rejcmb), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cbmAccept), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < bundles.size(); i++) {

            insertCell(pdfPTable, String.valueOf(newRowInfoMaster.getTruckNo()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getSupplierName()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getThickness()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getWidth()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getLength()), Element.ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getNoOfBundles()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getTruckCMB()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getNoOfRejected()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getCbmRej()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            double acc=Double.parseDouble(convertToEnglish(bundles.get(i).getTruckCMB()))-Double.parseDouble(convertToEnglish(bundles.get(i).getCbmRej()));
            insertCell(pdfPTable, String.format("%.3f", acc), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getGrade()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

//            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
//            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }


        insertCell(pdfPTable,"Total", Element.ALIGN_LEFT, 15, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,totalTruck, Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,totalRej, Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,totRejCbm, Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,totalACbm, Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,"", Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTableHeader, "Accepted Truck for "+bundles.get(0).getSupplierName(), Element.ALIGN_CENTER, 25, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + bundles.get(0).getSupplierName(), Element.ALIGN_RIGHT, 12, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.truck_no) + ": " + newRowInfoMaster.getTruckNo(), Element.ALIGN_LEFT, 13, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.ttn_no) + ": " + newRowInfoMaster.getTtnNo(), Element.ALIGN_RIGHT, 12, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.acceptance_loc) + ": " + newRowInfoMaster.getLocationOfAcceptance(), Element.ALIGN_LEFT, 13, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.date) + ": " + newRowInfoMaster.getDate(), Element.ALIGN_LEFT, 25, arabicFont, BaseColor.WHITE);

        insertCell(pdfPTableHeader, "", directionOfHeader, 2, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);


            Image signature = null;
            try {
                for(int im=0;im<15;im++) {
                    if(bundles.get(0)!=null) {
                        Bitmap bitmap=null;

                        switch (im){
                            case 0:
                                bitmap=bundles.get(0).getPic11();
                                break;
                            case 1:
                                bitmap=bundles.get(0).getPic22();

                                break;
                            case 2:
                                bitmap=bundles.get(0).getPic33();

                                break;
                            case 3:
                                bitmap=bundles.get(0).getPic44();
                                break;
                            case 4:
                                bitmap=bundles.get(0).getPic55();
                                break;
                            case 5:
                                bitmap=bundles.get(0).getPic66();
                                break;
                            case 6:
                                bitmap=bundles.get(0).getPic77();
                                break;
                            case 7:
                                bitmap=bundles.get(0).getPic88();
                                break;
                            case 8:
                                bitmap=bundles.get(0).getPic99();
                                break;
                            case 9:
                                bitmap=bundles.get(0).getPic1010();
                                break;
                            case 10:
                                bitmap=bundles.get(0).getPic1111();
                                break;
                            case 11:
                                bitmap=bundles.get(0).getPic1212();
                                break;
                            case 12:
                                bitmap=bundles.get(0).getPic1313();
                                break;
                            case 13:
                                bitmap=bundles.get(0).getPic1414();
                                break;
                            case 14:
                                bitmap=bundles.get(0).getPic1515();
                                break;



                        }

                        if(bitmap!=null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                            signature = Image.getInstance(stream.toByteArray());
                            signature.setAbsolutePosition(0f, 20f);
                            signature.scalePercent(50f);
                            signature.setRotationDegrees(0f);



                            doc.newPage();
                        }
                        doc.add(signature);
                    }
                }
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

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

    public void exportTruckAcceptanceEmail(List<NewRowInfo> bundles, String headerDate,String totalTruck,String totalRej,String totRejCbm , String totalACbm) {
        PdfPTable pdfPTable = new PdfPTable(25);
        PdfPTable pdfPTableHeader = new PdfPTable(25);

        createPDF("TruckAcceptance"  + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;
        insertCell(pdfPTable, context.getString(R.string.truck_no), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.thickness), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTable, context.getString(R.string.width), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.length), ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_pieces_accept), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.bundle_no), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.truck_cbm), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_of_p_reject), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.rejcmb), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cbmAccept), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < bundles.size(); i++) {

            insertCell(pdfPTable, String.valueOf(bundles.get(0).getTruckNo()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getSupplierName()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getThickness()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getWidth()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getLength()), Element.ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getNoOfBundles()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getTruckCMB()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getNoOfRejected()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getCbmRej()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            double accCubic= 0;
            accCubic= Double.parseDouble(convertToEnglish(bundles.get(i).getTruckCMB()))- Double.parseDouble(convertToEnglish(bundles.get(i).getCbmRej()));
            accCubic=Double.parseDouble(convertToEnglish(String.format("%.3f", accCubic)));
            insertCell(pdfPTable, String.valueOf(accCubic), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getGrade()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

//            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
//            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTable,"Total", Element.ALIGN_LEFT, 15, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,totalTruck, Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,totalRej, Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,totRejCbm, Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,totalACbm, Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,"", Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTableHeader, "Accepted Truck for "+bundles.get(0).getSupplierName(), Element.ALIGN_CENTER, 25, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + bundles.get(0).getSupplierName(), Element.ALIGN_RIGHT, 12, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.truck_no) + ": " + bundles.get(0).getTruckNo(), Element.ALIGN_LEFT, 13, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.ttn_no) + ": " + bundles.get(0).getTtnNo(), Element.ALIGN_RIGHT, 12, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.acceptance_loc) + ": " + bundles.get(0).getLocationOfAcceptance(), Element.ALIGN_LEFT, 13, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.date) + ": " + bundles.get(0).getDate(), Element.ALIGN_LEFT, 25, arabicFont, BaseColor.WHITE);

        insertCell(pdfPTableHeader, "", directionOfHeader, 2, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);

            Image signature = null;
            try {
                for(int im=0;im<15;im++) {
                    if(bundles.get(0)!=null) {
                        Bitmap bitmap=null;

                        switch (im){
                            case 0:
                                bitmap=bundles.get(0).getPic11();
                                break;
                            case 1:
                                bitmap=bundles.get(0).getPic22();

                                break;
                            case 2:
                                bitmap=bundles.get(0).getPic33();

                                break;
                            case 3:
                                bitmap=bundles.get(0).getPic44();
                                break;
                            case 4:
                                bitmap=bundles.get(0).getPic55();
                                break;
                            case 5:
                                bitmap=bundles.get(0).getPic66();
                                break;
                            case 6:
                                bitmap=bundles.get(0).getPic77();
                                break;
                            case 7:
                                bitmap=bundles.get(0).getPic88();
                                break;
                            case 8:
                                bitmap=bundles.get(0).getPic99();
                                break;
                            case 9:
                                bitmap=bundles.get(0).getPic1010();
                                break;
                            case 10:
                                bitmap=bundles.get(0).getPic1111();
                                break;
                            case 11:
                                bitmap=bundles.get(0).getPic1212();
                                break;
                            case 12:
                                bitmap=bundles.get(0).getPic1313();
                                break;
                            case 13:
                                bitmap=bundles.get(0).getPic1414();
                                break;
                            case 14:
                                bitmap=bundles.get(0).getPic1515();
                                break;



                        }

                        if(bitmap!=null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                            signature = Image.getInstance(stream.toByteArray());
                            signature.setAbsolutePosition(0f, 20f);
                            signature.scalePercent(50f);
                            signature.setRotationDegrees(0f);



                            doc.newPage();
                        }
                        doc.add(signature);
                    }
                }
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

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

    public void exportTruckAcceptanceSendEmail(List<NewRowInfo> bundles, String headerDate,String totalTruck,String totalRej,String totRejCbm , String totalACbm) {
        PdfPTable pdfPTable = new PdfPTable(25);
        PdfPTable pdfPTableHeader = new PdfPTable(25);

        createPDFForSendEmail("TruckAcceptance"  + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;
        insertCell(pdfPTable, context.getString(R.string.truck_no), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.thickness), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTable, context.getString(R.string.width), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.length), ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_pieces_accept), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.bundle_no), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.truck_cbm), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_of_p_reject), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.rejcmb), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cbmAccept), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < bundles.size(); i++) {

            insertCell(pdfPTable, String.valueOf(bundles.get(i).getTruckNo()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getSupplierName()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getThickness()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getWidth()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getLength()), Element.ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getNoOfBundles()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getTruckCMB()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getNoOfRejected()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getCbmRej()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            double accCubic= 0;
            accCubic= Double.parseDouble(convertToEnglish(bundles.get(i).getTruckCMB()))- Double.parseDouble(convertToEnglish(bundles.get(i).getCbmRej()));
            accCubic=Double.parseDouble(convertToEnglish(String.format("%.3f", accCubic)));
            insertCell(pdfPTable, String.valueOf(accCubic), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(bundles.get(i).getGrade()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

//            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
//            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTable,"Total", Element.ALIGN_LEFT, 15, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,totalTruck, Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,totalRej, Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,totRejCbm, Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,totalACbm, Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable,"", Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTableHeader, "Accepted Truck for "+bundles.get(0).getSupplierName(), Element.ALIGN_CENTER, 25, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + bundles.get(0).getSupplierName(), Element.ALIGN_RIGHT, 12, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.truck_no) + ": " + bundles.get(0).getTruckNo(), Element.ALIGN_LEFT, 13, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.acceptor) + ": " + bundles.get(0).getAcceptedPersonName(), Element.ALIGN_RIGHT, 12, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.acceptance_loc) + ": " + bundles.get(0).getLocationOfAcceptance(), Element.ALIGN_LEFT, 13, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.date) + ": " + bundles.get(0).getDate(), Element.ALIGN_LEFT, 25, arabicFont, BaseColor.WHITE);

        insertCell(pdfPTableHeader, "", directionOfHeader, 2, arabicFontHeader, BaseColor.WHITE);



//            signature.setRotation(0f);
//            signature.setPaddingTop(10);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);

            Image signature = null;
            try {
                for(int im=0;im<15;im++) {
                    if(bundles.get(0)!=null) {
                        Bitmap bitmap=null;

                        switch (im){
                            case 0:
                                bitmap=bundles.get(0).getPic11();
                                break;
                            case 1:
                                bitmap=bundles.get(0).getPic22();

                                break;
                            case 2:
                                bitmap=bundles.get(0).getPic33();

                                break;
                            case 3:
                                bitmap=bundles.get(0).getPic44();
                                break;
                            case 4:
                                bitmap=bundles.get(0).getPic55();
                                break;
                            case 5:
                                bitmap=bundles.get(0).getPic66();
                                break;
                            case 6:
                                bitmap=bundles.get(0).getPic77();
                                break;
                            case 7:
                                bitmap=bundles.get(0).getPic88();
                                break;
                            case 8:
                                bitmap=bundles.get(0).getPic99();
                                break;
                            case 9:
                                bitmap=bundles.get(0).getPic1010();
                                break;
                            case 10:
                                bitmap=bundles.get(0).getPic1111();
                                break;
                            case 11:
                                bitmap=bundles.get(0).getPic1212();
                                break;
                            case 12:
                                bitmap=bundles.get(0).getPic1313();
                                break;
                            case 13:
                                bitmap=bundles.get(0).getPic1414();
                                break;
                            case 14:
                                bitmap=bundles.get(0).getPic1515();
                                break;



                        }

                        if(bitmap!=null) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                            signature = Image.getInstance(stream.toByteArray());
                            signature.setAbsolutePosition(0f, 20f);
                            signature.scalePercent(50f);
                            signature.setRotationDegrees(0f);



                          doc.newPage();
                        }
                        doc.add(signature);
                    }
                }
            } catch (BadElementException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();

        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
               // showPdf(pdfFileName);
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
          //  showPdf(pdfFileName);
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

        insertCell(pdfPTable, context.getString(R.string.truck), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.ttn), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.date_of_acceptance), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_bundle), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.rejected_pieces), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {

            insertCell(pdfPTable, String.valueOf(list.get(i).getTruckNo()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getSupplierName()), Element.ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTtnNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDate()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getNoOfBundles()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getNoOfRejected()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

//            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
//            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTableHeader, "Report Two", Element.ALIGN_CENTER, 9, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", Element.ALIGN_CENTER, 9, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.from) + ": " + fromDate, Element.ALIGN_RIGHT, 4, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.to) + ": " + toDate, Element.ALIGN_LEFT, 5, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.truck_no) + ": " + truck, Element.ALIGN_RIGHT, 5, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();
        showPdf(pdfFileName);

    }

    public void exportLoadingOrderReport(List<Orders> list, String date, String fromDate, String toDate) {
        PdfPTable pdfPTable = new PdfPTable(14);
        PdfPTable pdfPTableHeader = new PdfPTable(14);

        createPDF("Loading Order Report" + date + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);

        insertCell(pdfPTable, context.getString(R.string.packing_list), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "Thick", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.width), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.length), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "Pieces", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.truck_no), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.container_no), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.date_of_load), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.destination_), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {
            insertCell(pdfPTable, String.valueOf(list.get(i).getOrderNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getThickness()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getWidth()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getLength()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getGrade()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getPlacingNo()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getContainerNo()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDateOfLoad()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDestination()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTableHeader, "Loading Order Report", Element.ALIGN_CENTER, 14, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", Element.ALIGN_CENTER, 14, arabicFontHeader, BaseColor.WHITE);

        insertCell(pdfPTableHeader, context.getString(R.string.from) + ": " + fromDate, Element.ALIGN_RIGHT, 7, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.to) + ": " + toDate, Element.ALIGN_LEFT, 7, arabicFont, BaseColor.WHITE);

        try {
            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

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

    public void exportLoadingOrderReportRow(List<BundleInfo> list, String date, Orders header) {
        PdfPTable pdfPTable = new PdfPTable(10);
        PdfPTable pdfPTableHeader = new PdfPTable(10);

        createPDF("Loading Order Report" + date + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);

        insertCell(pdfPTable, context.getString(R.string.bundle_no), ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "Thick", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.width), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.length), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "Pieces", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.grade), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.loc), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.area), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, context.getString(R.string.date_of_load), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, context.getString(R.string.destination_), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {
            insertCell(pdfPTable, String.valueOf(list.get(i).getBundleNo()), Element.ALIGN_CENTER, 3, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getThickness()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getWidth()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getLength()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getGrade()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getLocation()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getArea()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//            insertCell(pdfPTable, String.valueOf(list.get(i).getDateOfLoad()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
//            insertCell(pdfPTable, String.valueOf(list.get(i).getDestination()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);

        }

        insertCell(pdfPTableHeader, "Loading Order Report", Element.ALIGN_CENTER, 10, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", Element.ALIGN_CENTER, 10, arabicFontHeader, BaseColor.WHITE);

        insertCell(pdfPTableHeader, context.getString(R.string.packing_list) + ": " + header.getPackingList(), Element.ALIGN_RIGHT, 5, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.date_of_load) + ": " + header.getDateOfLoad(), Element.ALIGN_LEFT, 5, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.container_no) + ": " + header.getContainerNo(), Element.ALIGN_RIGHT, 5, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.truck_no) + ": " + header.getPlacingNo(), Element.ALIGN_LEFT, 5, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.destination_) + ": " + header.getDestination(), Element.ALIGN_RIGHT, 5, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.bundles) + ": " + list.size(), Element.ALIGN_LEFT, 5, arabicFontHeader, BaseColor.WHITE);

        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);

        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

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

    public void exportLoadPackingListChild(List<PlannedPL> list, String pl, String destination, String headerDate, String grade) {
        PdfPTable pdfPTable = new PdfPTable(8);
        PdfPTable pdfPTableHeader = new PdfPTable(8);

        createPDF("LoadedPackingListReportChild" + headerDate + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);

        insertCell(pdfPTable, "\n\n\n   Pine Sawn timber  \n\n Grad " + grade + "  Moisture  16%-18% \n\n\n", ALIGN_CENTER, 8, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTable, "N\nn/n", ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "the number of packets", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "thickness \n(mm)", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "width \n(mm)", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "length \n(mm)", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "the number of pieces in the package/", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "Total \n(m3)", ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);


        pdfPTable.setHeaderRows(1);
        int totalBundle = 0;
        double totalCubic = 0;

        for (int i = 0; i < list.size(); i++) {
            insertCell(pdfPTable, "" + (i + 1), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfCopies()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getThickness()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getWidth()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getLength()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf((int) list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.format("%.3f", (list.get(i).getCubic())), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            totalBundle += list.get(i).getNoOfCopies();
            totalCubic += list.get(i).getCubic();
        }

        insertCell(pdfPTable, "Total", Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.valueOf(totalBundle), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, "", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, String.format("%.3f", (totalCubic)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);


        insertCell(pdfPTableHeader, "Packing List : " + pl, Element.ALIGN_RIGHT, 8, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", Element.ALIGN_RIGHT, 8, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "Destination  : " + destination, Element.ALIGN_RIGHT, 8, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", Element.ALIGN_RIGHT, 8, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.cust) + ": " + customer, Element.ALIGN_LEFT, 6, arabicFont, BaseColor.WHITE);
        insertCell(pdfPTableHeader, "", Element.ALIGN_RIGHT, 8, arabicFontHeader, BaseColor.WHITE);

        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();
        showPdf(pdfFileName);

    }
    public void exportSupplierAccountPayment(List<PaymentAccountSupplier> list) {
        PdfPTable pdfPTable = new PdfPTable(11);
        PdfPTable pdfPTableHeader = new PdfPTable(11);

        createPDF("PaymentAccountSupplierReport"  + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;

        insertCell(pdfPTable, context.getString(R.string.paymentDate), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.date_of_acceptance), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.Value), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.payer), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.invoice_number), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.Balance), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.totalBank), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.totalCash), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.paymenttype), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);


        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {

            insertCell(pdfPTable, String.valueOf(list.get(i).getDATE_OF_PAYMENT()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getSUPLIER()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getACCEPTANCE_DATE()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getVALUE_OF_PAYMENT()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getPAYER()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getINVOICE_NO()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getSTART_BALANCE()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTOTAL_BANK()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTOTAL_CASH()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            if(list.get(i).getPAYMENT_TYPE().equals("1")){
                insertCell(pdfPTable, "Bank", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

            }else {

                insertCell(pdfPTable, "Cash", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            }


//
//            double acc=list.get(i).getCubic()-list.get(i).getCubicRej();
//            insertCell(pdfPTable, String.format("%.3f", acc), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
//            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }
//        insertCell(pdfPTable,"Total", Element.ALIGN_LEFT, 9, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(TotalCubic), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(totalReject), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(TotalCubicRej), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(totalACbm), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, "", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTableHeader, "Payment Account Supplier Report", Element.ALIGN_CENTER, 11, arabicFontHeader, BaseColor.WHITE);
        //insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + supplier, Element.ALIGN_RIGHT, 16, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.from) + ": " + fromDate, Element.ALIGN_LEFT, 8, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, " "+context.getString(R.string.truck_no) + ": " + truck, Element.ALIGN_RIGHT, 8, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.to) + ": " + toDate, Element.ALIGN_LEFT, 8, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

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
    public void exportSupplierAccountPaymentEmail(List<PaymentAccountSupplier> list) {
        PdfPTable pdfPTable = new PdfPTable(11);
        PdfPTable pdfPTableHeader = new PdfPTable(11);

        createPDFForSendEmail("PaymentAccountSupplierReport"  + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;

        insertCell(pdfPTable, context.getString(R.string.paymentDate), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.date_of_acceptance), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.Value), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.payer), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.invoice_number), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.Balance), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.totalBank), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.totalCash), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.paymenttype), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);


        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {

            insertCell(pdfPTable, String.valueOf(list.get(i).getDATE_OF_PAYMENT()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getSUPLIER()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getACCEPTANCE_DATE()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getVALUE_OF_PAYMENT()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getPAYER()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getINVOICE_NO()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getSTART_BALANCE()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTOTAL_BANK()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTOTAL_CASH()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            if(list.get(i).getPAYMENT_TYPE().equals("1")){
                insertCell(pdfPTable, "Bank", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

            }else {

                insertCell(pdfPTable, "Cash", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            }


//
//            double acc=list.get(i).getCubic()-list.get(i).getCubicRej();
//            insertCell(pdfPTable, String.format("%.3f", acc), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
//            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }
//        insertCell(pdfPTable,"Total", Element.ALIGN_LEFT, 9, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(TotalCubic), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(totalReject), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(TotalCubicRej), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(totalACbm), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, "", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTableHeader, "Payment Account Supplier Report", Element.ALIGN_CENTER, 11, arabicFontHeader, BaseColor.WHITE);
        //insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + supplier, Element.ALIGN_RIGHT, 16, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.from) + ": " + fromDate, Element.ALIGN_LEFT, 8, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, " "+context.getString(R.string.truck_no) + ": " + truck, Element.ALIGN_RIGHT, 8, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.to) + ": " + toDate, Element.ALIGN_LEFT, 8, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();

        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
              //  showPdf(pdfFileName);
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
        //    showPdf(pdfFileName);
            Log.v("", "Permission is granted");
        }
//        showPdf(pdfFileName);

    }
    public void exportSupplierAccountSupplier(List<NewRowInfo> list,String supplier) {
        PdfPTable pdfPTable = new PdfPTable(16);
        PdfPTable pdfPTableHeader = new PdfPTable(16);

        createPDF("AccountSupplierReport"  + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;

        insertCell(pdfPTable, context.getString(R.string.date_of_acceptance), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.truck_no), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.ttn_no), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.thickness), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.width), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.length), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_pieces), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.bundle_no), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.reject), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cbmAccept), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.price), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cash), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.Debt_), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cash_), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {

            insertCell(pdfPTable, String.valueOf(list.get(i).getDate()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getSupplierName()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTruckNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTtnNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getThickness()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getWidth()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getLength()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfBundles()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfRejected()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCbmAccept()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getPrice()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCash()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDebt$()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCash$()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

//
//            double acc=list.get(i).getCubic()-list.get(i).getCubicRej();
//            insertCell(pdfPTable, String.format("%.3f", acc), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
//            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }
//        insertCell(pdfPTable,"Total", Element.ALIGN_LEFT, 9, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(TotalCubic), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(totalReject), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(TotalCubicRej), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(totalACbm), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, "", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTableHeader, "Account Supplier Report", Element.ALIGN_CENTER, 16, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + supplier, Element.ALIGN_RIGHT, 16, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.from) + ": " + fromDate, Element.ALIGN_LEFT, 8, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, " "+context.getString(R.string.truck_no) + ": " + truck, Element.ALIGN_RIGHT, 8, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.to) + ": " + toDate, Element.ALIGN_LEFT, 8, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

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

    public void exportSupplierAccountSupplierEmail(List<NewRowInfo> list,String supplier) {
        PdfPTable pdfPTable = new PdfPTable(16);
        PdfPTable pdfPTableHeader = new PdfPTable(16);

        createPDFForSendEmail("AccountSupplierReport"  + "_.pdf");
        pdfPTable.setWidthPercentage(100f);
        pdfPTableHeader.setWidthPercentage(100f);
        pdfPTableHeader.setSpacingAfter(20);

//        int directionOfHeader = Element.ALIGN_RIGHT;

        pdfPTable.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
        pdfPTableHeader.setRunDirection(PdfWriter.RUN_DIRECTION_LTR);
//        directionOfHeader = Element.ALIGN_RIGHT;

        insertCell(pdfPTable, context.getString(R.string.date_of_acceptance), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.supplier_name), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.truck_no), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.ttn_no), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.thickness), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.width), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.length), ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.no_pieces), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.bundle_no), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.reject), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cbmAccept), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.price), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cash), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.Debt_), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
        insertCell(pdfPTable, context.getString(R.string.cash_), ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        pdfPTable.setHeaderRows(1);
        for (int i = 0; i < list.size(); i++) {

            insertCell(pdfPTable, String.valueOf(list.get(i).getDate()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getSupplierName()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTruckNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getTtnNo()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getThickness()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getWidth()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getLength()), Element.ALIGN_CENTER, 2, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfPieces()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfBundles()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getNoOfRejected()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCbmAccept()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getPrice()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCash()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getDebt$()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
            insertCell(pdfPTable, String.valueOf(list.get(i).getCash$()), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

//
//            double acc=list.get(i).getCubic()-list.get(i).getCubicRej();
//            insertCell(pdfPTable, String.format("%.3f", acc), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//            double cubic = (list.get(i).getLength() * list.get(i).getWidth() * list.get(i).getThickness() * list.get(i).getNoOfPieces());
//            insertCell(pdfPTable, String.valueOf(String.format("%.3f", cubic / 1000000000)), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        }
//        insertCell(pdfPTable,"Total", Element.ALIGN_LEFT, 9, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(TotalCubic), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(totalReject), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(TotalCubicRej), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, String.valueOf(totalACbm), Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);
//        insertCell(pdfPTable, "", Element.ALIGN_CENTER, 1, arabicFont, BaseColor.BLACK);

        insertCell(pdfPTableHeader, "Account Supplier Report", Element.ALIGN_CENTER, 16, arabicFontHeader, BaseColor.WHITE);
        insertCell(pdfPTableHeader, context.getString(R.string.supplier_name) + ": " + supplier, Element.ALIGN_RIGHT, 16, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.from) + ": " + fromDate, Element.ALIGN_LEFT, 8, arabicFontHeader, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, " "+context.getString(R.string.truck_no) + ": " + truck, Element.ALIGN_RIGHT, 8, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, context.getString(R.string.to) + ": " + toDate, Element.ALIGN_LEFT, 8, arabicFont, BaseColor.WHITE);
//        insertCell(pdfPTableHeader, "", directionOfHeader, 1, arabicFontHeader, BaseColor.WHITE);


        try {

            doc.add(pdfPTableHeader);
            doc.add(pdfPTable);
            Toast.makeText(context, context.getString(R.string.export_to_pdf), Toast.LENGTH_LONG).show();

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        endDocPdf();

        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && (context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
              //  showPdf(pdfFileName);
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
         //   showPdf(pdfFileName);
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

    void createPDFForSendEmail(String fileName) {
        doc = new Document();
        docWriter = null;
        Log.e("path45", "create" + "-->" + Environment.getExternalStorageDirectory().getPath());
        try {


            String directory_path = Environment.getExternalStorageDirectory().getPath() + "/SendEmailWood/";
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

    public String convertToEnglish(String value) {
        String newValue = (((((((((((value + "").replaceAll("١", "1")).replaceAll("٢", "2")).replaceAll("٣", "3")).replaceAll("٤", "4")).replaceAll("٥", "5")).replaceAll("٦", "6")).replaceAll("٧", "7")).replaceAll("٨", "8")).replaceAll("٩", "9")).replaceAll("٠", "0").replaceAll("٫", "."));
        return newValue;
    }

}
