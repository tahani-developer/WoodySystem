package com.falconssoft.woodysystem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.falconssoft.woodysystem.models.PlannedPL;

import java.io.File;
import java.io.IOException;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ExportToExcel {

    private static ExportToExcel instance;

    public static ExportToExcel getInstance() {
        if (instance == null)
            instance = new ExportToExcel();

        return instance;
    }

    public void createExcelFile(Context context, String fileName, int report, List<?> list) {

//        this.context = context;
//        final String fileName = "planned_packing_list_report.xls";

        //Saving file in external storage
        File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard.getAbsolutePath() + "/blackseawood");

        if (!directory.isDirectory()) {//create directory if not exist
            directory.mkdirs();
        }

        File file = new File(directory, fileName);//file path

//        WorkbookSettings wbSettings = new WorkbookSettings();
//        wbSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook = null;//, wbSettings);
        try {
            workbook = Workbook.createWorkbook(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        switch (report) {
            case 2:
                workbook = stageTwoReportOne(workbook, (List<PlannedPL>) list);
                break;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/blackseawood/";
        file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path + fileName;
        File path = new File(targetPdf);

        Uri uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", path);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "application/vnd.ms-excel");//intent.setDataAndType(Uri.fromFile(path), "application/pdf");
        context.startActivity(intent);
    }

    // 0
    void stageOneReportOne() {

    }

    // 1
    void stageOneReportTwo() {

    }

    // 2 planned_packing_list_report
    WritableWorkbook stageTwoReportOne(WritableWorkbook workbook, List<PlannedPL> list) {

        try {
            WritableSheet sheet = workbook.createSheet("Sheet1", 0);//Excel sheet name. 0 represents first sheet

            try {
                sheet.addCell(new Label(0, 0, "Customer")); // column and row
                sheet.addCell(new Label(2, 0, "packing List"));
                sheet.addCell(new Label(3, 0, "Destination"));
                sheet.addCell(new Label(5, 0, "Order No."));
                sheet.addCell(new Label(6, 0, "Supplier"));
                sheet.addCell(new Label(8, 0, "Grade"));
                sheet.addCell(new Label(9, 0, "Pieces"));
                sheet.addCell(new Label(10, 0, "Bundles"));
                sheet.addCell(new Label(11, 0, "Cubic"));
                sheet.mergeCells(0,0, 1, 0);// col , row, to col , to row
                sheet.mergeCells(3,0, 4, 0);// col , row, to col , to row
                sheet.mergeCells(6,0, 7, 0);// col , row, to col , to row
                sheet.mergeCells(11,0, 12, 0);// col , row, to col , to row

                sheet.mergeCells(0,1, 1, 1);// col , row, to col , to row
                sheet.mergeCells(3,1, 4, 1);// col , row, to col , to row
                sheet.mergeCells(6,1, 7, 1);// col , row, to col , to row
                sheet.mergeCells(11,1, 12, 1);// col , row, to col , to row

                for (int i = 0; i < list.size(); i++) {
                    sheet.addCell(new Label(0, i + 2, list.get(i).getCustName()));
                    sheet.addCell(new Label(2, i + 2, list.get(i).getPackingList()));
                    sheet.addCell(new Label(3, i + 2, list.get(i).getDestination()));
                    sheet.addCell(new Label(5, i + 2, list.get(i).getOrderNo()));
                    sheet.addCell(new Label(6, i + 2, list.get(i).getSupplier()));
                    sheet.addCell(new Label(8, i + 2, list.get(i).getGrade()));
                    sheet.addCell(new Label(9, i + 2, "" + list.get(i).getNoOfPieces()));
                    sheet.addCell(new Label(10, i + 2, "" + list.get(i).getNoOfCopies()));
                    sheet.addCell(new Label(11, i + 2, "" +  String.format("%.3f", (list.get(i).getCubic()))));
                    sheet.mergeCells(0,i + 2, 1, i + 2);// col , row, to col , to row
                    sheet.mergeCells(3,i + 2, 4, i + 2);// col , row, to col , to row
                    sheet.mergeCells(6,i + 2, 7, i + 2);// col , row, to col , to row
                    sheet.mergeCells(11,i + 2, 12, i + 2);// col , row, to col , to row
                }

            } catch (RowsExceededException e) {
                e.printStackTrace();
            } catch (WriteException e) {
                e.printStackTrace();
            }
            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;

    }

    // 3
    WritableWorkbook stageTwoReportTwo(WritableWorkbook workbook, List<PlannedPL> list) {
        return null;
    }

    //4 loaded_packing_list_report
    void stageTwoReportThree() {

    }

    void stageThreeReportOne() {

    }

    void stageThreeReportTwo() {

    }
}