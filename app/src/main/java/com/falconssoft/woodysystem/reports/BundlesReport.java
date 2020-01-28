package com.falconssoft.woodysystem.reports;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintJob;
import android.print.PrintJobInfo;
import android.print.PrintManager;
import android.print.PrinterInfo;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.PrinterCommands;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.ReportsActivity;
import com.falconssoft.woodysystem.WoodPresenter;
import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.Settings;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class BundlesReport extends AppCompatActivity {

    private TableLayout bundlesTable;
    private DatabaseHandler databaseHandler;
    private WoodPresenter presenter;
    private List<BundleInfo> bundleInfoForPrint = new ArrayList<>();
    private List<BundleInfo> bundleInfos = new ArrayList<>();
    private Animation animation;
    private TextView textView;
    private Settings generalSettings;
    private Button printAll, hide;
    private String bundleNumber;
    private TableRow hidedTableRow = null;
    private JSONArray jsonArrayBundles = new JSONArray();
    private List<TableRow> bundlesNoRows;

    private CheckBox checkBoxPrinter;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundles_report);

        presenter = new WoodPresenter(this);
        printAll = findViewById(R.id.loading_order_report_printAll);
        textView = findViewById(R.id.loading_order_report_tv);
        hide = findViewById(R.id.loading_order_report_delete);
        checkBoxPrinter = findViewById(R.id.checkBoxPrinter);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_to_right);
        textView.startAnimation(animation);

        bundlesTable = findViewById(R.id.addToInventory_table);
        databaseHandler = new DatabaseHandler(this);
        presenter.getPrintBarcodeData(this);
//        fillTable();

        checkBoxPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBoxPrinter.isChecked()) {

                    for (int i = 0; i < bundlesTable.getChildCount(); i++) {
                        TableRow table = (TableRow) bundlesTable.getChildAt(i);
                        CheckBox bundleCheck = (CheckBox) table.getChildAt(10);
                        bundleCheck.setChecked(true);

                    }
                } else {
                    for (int i = 0; i < bundlesTable.getChildCount(); i++) {
                        TableRow table = (TableRow) bundlesTable.getChildAt(i);
                        CheckBox bundleCheck = (CheckBox) table.getChildAt(10);
                        bundleCheck.setChecked(false);

                    }
                }

            }
        });

        printAll.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
//               PrintAll();
                try {

                    bundleInfoForPrint.clear();
                    for (int i = 0; i < bundlesTable.getChildCount(); i++) {
                        TableRow table = (TableRow) bundlesTable.getChildAt(i);
                        CheckBox bundleCheck = (CheckBox) table.getChildAt(10);
                        if (bundleCheck.isChecked()) {
                            Log.e("bundelCheak", "" + i + "  " + bundleInfos.get(Integer.parseInt(bundleCheck.getTag().toString())).getBundleNo());
                            bundleInfoForPrint.add(bundleInfos.get(Integer.parseInt(bundleCheck.getTag().toString())));
                        }
                    }
                    boolean permission = isStoragePermissionGranted();

                    if (permission) {
                        File file = null;
                        try {
                            file = createPdf();
                            PrintAll(file);
                            bundleInfoForPrint.clear();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }
                    }
//
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        hide.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                new android.support.v7.app.AlertDialog.Builder(BundlesReport.this)
                        .setTitle("Confirm Hide")
                        .setMessage("Are you want hide checked data ?!")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() { // Html.fromHtml("<font color='#FF7F27'>Yes</font>")
                            public void onClick(DialogInterface dialog, int whichButton) {

                                jsonArrayBundles = new JSONArray();
                                bundlesNoRows = new ArrayList<>();
                                for (int i = 0; i < bundlesTable.getChildCount(); i++) {
                                    TableRow table = (TableRow) bundlesTable.getChildAt(i);
                                    CheckBox bundleCheck = (CheckBox) table.getChildAt(10);
                                    TextView bundleNo = (TextView) table.getChildAt(1);
                                    if (bundleCheck.isChecked()) {
                                        bundlesNoRows.add(table);
                                        Log.e("bundelCheak", "" + i + "  " + bundleInfos.get(Integer.parseInt(bundleCheck.getTag().toString())).getBundleNo());
//                                        databaseHandler.updateAllPrinting(bundleNo.getText().toString(), 1);

                                        BundleInfo bundleInfo = new BundleInfo();
                                        bundleInfo.setBundleNo(bundleNo.getText().toString());
                                        jsonArrayBundles.put(bundleInfo.getJSONObject());
                                    }
                                }

                                new JSONTask3().execute();

//                                for (int i = 0; i < bundleInfoForPrint.size(); i++) {
//                                    databaseHandler.updateAllPrinting(bundleInfoForPrint.get(i).getBundleNo(), 1);
//
//                                }
//                                bundleInfos = databaseHandler.getAllBundleInfo("0");
//                                bundlesTable.removeAllViews();
                                bundleInfoForPrint.clear();
                                presenter.getBundleReportList();
//                                fillTable();
                            }
                        })
                        .setNegativeButton("Cancel", null).show();

            }
        });

    }

//    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
//    private void doPrint(String fileName) {
//        PrintManager printManager = (PrintManager) BundlesReport.this
//                .getSystemService(Context.PRINT_SERVICE);
//
////        mFileName = fileName;
//        String jobName = BundlesReport.this.getString(R.string.app_name) + " Document";
//        printManager.print(jobName, mPrintDocumentAdapter, null);
//    }

    public void fillTable() {
        Log.e("compare", "" + presenter.getBundleReportList().size());
        for (int i = 0; i < presenter.getBundleReportList().size(); i++)
            bundleInfos.add(presenter.getBundleReportList().get(i));
        Log.e("compare2", "" + bundleInfos.size());

        generalSettings = new Settings();
        generalSettings = databaseHandler.getSettings();

        TableRow tableRow;
        for (int m = 0; m < bundleInfos.size(); m++) {
            if (bundleInfos.get(m).getIsPrinted() != 1) {
                tableRow = new TableRow(this);
                tableRow = fillTableRows(tableRow
                        , bundleInfos.get(m).getBundleNo()
                        , "" + (int) bundleInfos.get(m).getLength()
                        , "" + (int) bundleInfos.get(m).getWidth()
                        , "" + (int) bundleInfos.get(m).getThickness()
                        , bundleInfos.get(m).getGrade()
                        , "" + (int) bundleInfos.get(m).getNoOfPieces()
                        , bundleInfos.get(m).getLocation()
                        , bundleInfos.get(m).getArea()
                        , R.color.light_orange
                        , m
                        , bundleInfos.get(m).getSerialNo()
                        , bundleInfos.get(m).getBackingList()
                );
                bundlesTable.addView(tableRow);

//                TableRow finalTableRow = tableRow;
//                tableRow.getVirtualChildAt(8).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        PrintHelper photoPrinter = new PrintHelper(BundlesReport.this);
//                        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
//                        TextView bundleNo = (TextView) finalTableRow.getChildAt(0);
//                        TextView length = (TextView) finalTableRow.getChildAt(1);
//                        TextView width = (TextView) finalTableRow.getChildAt(2);
//                        TextView thic = (TextView) finalTableRow.getChildAt(3);
//                        TextView grade = (TextView) finalTableRow.getChildAt(4);
//                        TextView pcs = (TextView) finalTableRow.getChildAt(5);
//                        Bitmap bitmap = writeBarcode(bundleNo.getText().toString(), length.getText().toString(), width.getText().toString(),
//                                thic.getText().toString(), grade.getText().toString(), pcs.getText().toString());
//                        databaseHandler.updateCheckPrinting(bundleNo.getText().toString(), 1);
//
//                        photoPrinter.printBitmap("invoice.jpg", bitmap);
//                        Toast.makeText(BundlesReport.this, "tested+" + bundleNo.getText().toString(), Toast.LENGTH_SHORT).show();
//                        bundleNumber = bundleNo.getText().toString();
//                        new JSONTask2().execute();
//
//                        photoPrinter.printBitmap("invoice.jpg", bitmap);
//                        Toast.makeText(BundlesReport.this, "tested+" + bundleNo.getText().toString(), Toast.LENGTH_SHORT).show();
//
//                    }
//                });
//
//                TableRow clickTableRow = tableRow;
//                tableRow.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
////                                                TextView textView = ((TextView) tableRow.getChildAt(0));
////                                                tableRow.setBackgroundResource(R.color.light_orange_2);
//                        String bundleNo = ((TextView) clickTableRow.getChildAt(1)).getText().toString();
//                        Log.e("b", bundleNo);
//                        AlertDialog.Builder builder = new AlertDialog.Builder(BundlesReport.this);
//                        builder.setMessage("Are you want hide bundle number: " + bundleNo + " ?");
//                        builder.setTitle("Hide");
//                        builder.setIcon(R.drawable.ic_warning_black_24dp);
//                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
////                                databaseHandler.updateBundlesFlag(bundleNo);// 1 mean hide
//                                bundleNumber = bundleNo;
//                                hidedTableRow = clickTableRow;
//                                new JSONTask2().execute();
//
//                            }
//                        });
//                        builder.show();
//                        return false;
//                    }
//                });
            }
        }
    }

    TableRow fillTableRows(TableRow tableRow, String bundlNo, String length, String width, String thic, String grade, String noOfPieces, String location, String area, int backgroundColor, int indexInList, String serialNo, String backingList) {

        for (int i = 0; i < 11; i++) {
            TextView textView = new TextView(this);
            textView.setBackgroundResource(backgroundColor);
            TableRow.LayoutParams textViewParam;
//            TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
//            textViewParam.setMargins(1, 5, 1, 1);
            textView.setTextSize(15);
            textView.setTextColor(ContextCompat.getColor(this, R.color.gray_dark_one));
//            textView.setLayoutParams(textViewParam);
            switch (i) {
                case 0:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setPadding(1, 6, 1, 7);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(serialNo);
                    break;
                case 1:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3.4f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setPadding(1, 6, 1, 7);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(bundlNo);
                    break;
                case 2:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setPadding(1, 6, 1, 7);
                    textView.setTextSize(15);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(thic);
                    break;
                case 3:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setPadding(1, 6, 1, 7);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(width);
                    break;
                case 4:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setPadding(1, 6, 1, 7);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(length);
                    break;
                case 5:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setPadding(1, 6, 1, 7);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(noOfPieces);
                    break;
                case 6:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setPadding(1, 6, 1, 7);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(grade);
                    break;
                case 7:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setPadding(1, 6, 1, 7);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(location);
                    break;
                case 8:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setPadding(1, 6, 1, 7);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(area);
                    break;
                case 9:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setPadding(1, 6, 1, 7);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(backingList);
                    break;
                case 10:
                    CheckBox checkBox = new CheckBox(this);
                    textViewParam = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    textViewParam.setMargins(1, 5, 1, 1);
                    checkBox.setLayoutParams(textViewParam);
                    checkBox.setText("");
                    checkBox.setTag("" + indexInList);
                    checkBox.setTextColor(ContextCompat.getColor(this, R.color.white));
                    checkBox.setBackgroundResource(backgroundColor);
                    tableRow.addView(checkBox);
                    break;
            }
            tableRow.addView(textView);
        }
        return tableRow;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void PrintAll(File file) {
        PrintManager printManager = (PrintManager) BundlesReport.this.getSystemService(Context.PRINT_SERVICE);
        String jobName = " Document";


        PrintDocumentAdapter pda = new PrintDocumentAdapter() {
            @Override
            public void onWrite(PageRange[] pages, ParcelFileDescriptor destination, CancellationSignal cancellationSignal, WriteResultCallback callback) {
                InputStream input = null;
                OutputStream output = null;

                try {

                    input = new FileInputStream(file);
                    output = new FileOutputStream(destination.getFileDescriptor());

                    byte[] buf = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = input.read(buf)) > 0) {
                        output.write(buf, 0, bytesRead);
                    }

                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});

                } catch (FileNotFoundException ee) {
                    //Catch exception
                } catch (Exception e) {
                    //Catch exception
                } finally {
                    try {
                        input.close();
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {

                if (cancellationSignal.isCanceled()) {
                    callback.onLayoutCancelled();

                    return;
                }


                PrintDocumentInfo pdi = new PrintDocumentInfo.Builder("pdfdemokk.pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).build();

                callback.onLayoutFinished(pdi, true);
            }

        };

        PrintAttributes attrib = new PrintAttributes.Builder()
                .setMediaSize(PrintAttributes.MediaSize.ISO_A5.UNKNOWN_LANDSCAPE)
                .build();
        printManager.print(jobName, pda, null);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    File creatFile() {

        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "pdfdemo");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
            Log.i("Created", "Pdf Directory created");
        }

        //Create time stamp
//        Date date = new Date() ;
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        File myFile = new File(pdfFolder + "kk" + ".pdf");
        return myFile;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private File createPdf() throws IOException, DocumentException {
        File myFile = creatFile();
        try {

            //Image image = Image.getInstance(byteArray);


            Log.e("file ", "" + myFile.getPath());
            OutputStream output = new FileOutputStream(myFile);
            //Step 1
//        Rectangle rect = new Rectangle(0, 0, 595, 842);
            Document document = new Document();
            document.setPageSize(PageSize.A4);
//        Document document = new Document();//PageSize.A4.rotate()
//        document
            //Step 2
//        document.newPage();

            PdfWriter.getInstance(document, output);

            //Step 3
            document.open();

            //Step 4 Add content
            int ispage = 0;
            for (int i = 0; i < bundleInfoForPrint.size(); i++) {
                if (bundleInfoForPrint.get(i).getIsPrinted() != 1) {
                    Bitmap bitmap = writeBarcode(String.valueOf(bundleInfoForPrint.get(i).getBundleNo()), String.valueOf(bundleInfoForPrint.get(i).getLength()), String.valueOf(bundleInfoForPrint.get(i).getWidth()),
                            String.valueOf(bundleInfoForPrint.get(i).getThickness()), String.valueOf(bundleInfoForPrint.get(i).getGrade()), String.valueOf(bundleInfoForPrint.get(i).getNoOfPieces()));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    Image signature;
                    signature = Image.getInstance(stream.toByteArray());
                    signature.setAbsolutePosition(0f, 20f);
                    signature.scalePercent(160f);
                    signature.setRotationDegrees(90f);
//            signature.setRotation(0f);
//            signature.setPaddingTop(10);
                    document.add(signature);

//                    if ((i + 1)< bundleInfos.size()){
//                        Bitmap bitmap2 = writeBarcode(String.valueOf(bundleInfos.get(i+1).getBundleNo()), String.valueOf(bundleInfos.get(i+1).getLength()), String.valueOf(bundleInfos.get(i+1).getWidth()),
//                                String.valueOf(bundleInfos.get(i+1).getThickness()), String.valueOf(bundleInfos.get(i+1).getGrade()), String.valueOf(bundleInfos.get(i+1).getNoOfPieces()));
//                    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
//                    bitmap2.compress(Bitmap.CompressFormat.PNG, 100, stream2);
//                    Image signature2;
//                    signature2 = Image.getInstance(stream2.toByteArray());
//                    signature2.setAbsolutePosition(20f, 450f);
//                    signature2.scalePercent(150f);
////                    signature2.setRotationDegrees(90f);
//
//                    document.add(signature2);
//                }
                    document.newPage();
                    ispage = 1;
                }
            }
            if (ispage == 0) {
                Paragraph p = new Paragraph("no bundle to print ");
                document.add(p);
            }
            Log.e("getPageNumber()= ", "" + document.getPageNumber());

            //document.add(new Paragraph(text.getText().toString()));
            //document.add(new Paragraph(mBodyEditText.getText().toString()));

            //Step 5: Close the document
            document.close();

        } catch (Exception ex) {
            Log.e("Exception create Pdf : ", "" + ex.getMessage());
        }
        return myFile;

    }
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    private void doPDFPrint(File pdfFile, String filename) {
//        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);
//        String jobName = this.getString(R.string.app_name) + " Report";
//        PrintPDFAdapter pda = new PrintPDFAdapter(pdfFile, filename);
//        PrintAttributes attrib = new PrintAttributes.Builder().
//                setMediaSize(PrintAttributes.MediaSize.NA_LETTER.asLandscape()).
//                setMinMargins(PrintAttributes.Margins.NO_MARGINS).
//                build();
//        printManager.print(jobName, pda, attrib);
//    }

    public Bitmap writeBarcode(String data, String length, String width, String thic, String grades, String pcs) {
        final Dialog dialog = new Dialog(BundlesReport.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.barcode_design);
        TextView companyName, bundelNo, TLW, pcsNo, grade;

        companyName = (TextView) dialog.findViewById(R.id.companyName);
        bundelNo = (TextView) dialog.findViewById(R.id.bundelNo);
        TLW = (TextView) dialog.findViewById(R.id.TLW);
        pcsNo = (TextView) dialog.findViewById(R.id.pcsNo);
        grade = (TextView) dialog.findViewById(R.id.grade);
        ImageView iv = (ImageView) dialog.findViewById(R.id.barcode);

        companyName.setText(generalSettings.getCompanyName());
        bundelNo.setText(data);
        TLW.setText(thic + " X " + width + " X " + length);
        pcsNo.setText(pcs);
        grade.setText(grades);


        LinearLayout linearView = (LinearLayout) dialog.findViewById(R.id.design);

        // barcode data
        String barcode_data = data;
        Bitmap bitmap = null;//  AZTEC -->QR
        try {
            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.CODE_128, 50, 50);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        iv.setImageBitmap(bitmap);

        linearView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        linearView.layout(0, 0, linearView.getMeasuredWidth(), linearView.getMeasuredHeight());

        Log.e("size of img ", "width=" + linearView.getMeasuredWidth() + "      higth =" + linearView.getHeight());

        Bitmap bitmaps = Bitmap.createBitmap(linearView.getWidth(), linearView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmaps);
        Drawable bgDrawable = linearView.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        linearView.draw(canvas);

//        dialog.show();

        return bitmaps;

    }

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }


    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("", "Permission is granted");
                return true;
            } else {

                Log.v("", "Permission is revoked");
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
                return false;
            }
        } else { // permission is automatically granted on sdk<23 upon
            // installation
            Log.v("", "Permission is granted");
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v("", "Permission: " + permissions[0] + "was "
                    + grantResults[0]);
            // resume tasks needing this permission

            File file = null;
            try {
                file = createPdf();
                PrintAll(file);
                bundleInfoForPrint.clear();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }

        }

    }


    class JSONTask2 extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("PRINT_BUNDLE", "1"));
                nameValuePairs.add(new BasicNameValuePair("BUNDLE_NO", bundleNumber));

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(request);

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();

                JsonResponse = sb.toString();
                Log.e("tag", "" + JsonResponse);

                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("BundleReport", "json 2 " + s);
            if (s != null) {
                if (s.contains("PRINT BUNDLE SUCCESS")) {
                    bundlesTable.removeView(hidedTableRow);
                    Log.e("BundleReport", "****Success");
                } else {
                    Toast.makeText(BundlesReport.this, "Failed to export data!", Toast.LENGTH_SHORT).show();
//                    Log.e("inventoryReport", "****Failed to export data");
                }
            } else {
                Toast.makeText(BundlesReport.this, "No internet connection!", Toast.LENGTH_SHORT).show();
//                Log.e("inventoryReport", "****Failed to export data Please check internet connection");
            }
        }
    }

    private class JSONTask3 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
//            Log.e("size", "" + jsonArrayBundles.size());
            try {
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("PRINT_BUNDLES", "1"));
                nameValuePairs.add(new BasicNameValuePair("BUNDLE_NO", jsonArrayBundles.toString().trim()));

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(request);

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();

                JsonResponse = sb.toString();
                Log.e("tag", "" + JsonResponse);

                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("Bundles report", "json 3 " + s);
            if (s != null) {
                if (s.contains("PRINT BUNDLES SUCCESS")) {
                    for (int i = 0; i < bundlesNoRows.size(); i++) {
                        bundlesTable.removeView(bundlesNoRows.get(i));
                    }
                    Log.e("tag", "****Success");
                } else {
                    Toast.makeText(BundlesReport.this, "Failed to export data!", Toast.LENGTH_SHORT).show();
//                    Log.e("tag", "****Failed to export data");
                }
            } else {
                Toast.makeText(BundlesReport.this, "No internet connection!", Toast.LENGTH_SHORT).show();
//                Log.e("tag", "****Failed to export data Please check internet connection");
            }
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}







