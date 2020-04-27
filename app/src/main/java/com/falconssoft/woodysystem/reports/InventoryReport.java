package com.falconssoft.woodysystem.reports;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.R;
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
import org.json.JSONObject;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class InventoryReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private ProgressDialog progressDialog;
    private Snackbar snackbar;
    private ConstraintLayout containerLayout;
    private DatabaseHandler databaseHandler;
    public static List<BundleInfo> bundleInfoServer = new ArrayList<>();
    public static List<BundleInfo> bundleInfoServer2 = new ArrayList<>();
    private List<String> locationList = new ArrayList<>();
    private List<String> areaList = new ArrayList<>();
    private List<String> orderedList = new ArrayList<>();
    private List<String> plList = new ArrayList<>();
    private List<String> gradeList = new ArrayList<>();
    private List<BundleInfo> bundlesForDelete = new ArrayList<>();
    private List<BundleInfo> dateFiltered, filtered;
    private JSONArray jsonArrayBundles = new JSONArray();
    private WoodPresenter woodPresenter;
    private Animation animation;
    private TextView textView, noOfBundles, noOfPieces, cubicField, deleteAll, dateFrom, dateTo, thicknessOrder, widthOrder, lengthOrder, searchPListTool, searchSerialTool;
    private Spinner location, area, ordered, pList, grade, thicknessSpinner;
    private ArrayAdapter<String> locationAdapter;
    private ArrayAdapter<String> areaAdapter;
    private ArrayAdapter<String> orderedAdapter;
    private ArrayAdapter<String> gradeAdapter;
    private ArrayAdapter<String> plAdapter;
    private ArrayAdapter<String> thicknessAdapter;
    private String loc = "All", areaField = "All", orderedField = "All", plField = "All", gradeFeld = "All", thicknessField = "All", serialString = "All";

    private Settings generalSettings;
    private Calendar calendar;
    private Date date;
    private String serialNumber;
    private int index;
    //    private SearchView searchViewTh, searchViewW, searchViewL;
    private CheckBox checkBoxPrint;
    private List<BundleInfo> bundleInfoForPrint = new ArrayList<>();
    private List<BundleInfo> bundleInfoForPList = new ArrayList<>();
    private Button printAll, pListAll;
    private TableRow tableRowToDelete = null;
    private EditText fromLength, toLength, fromWidth, toWidth, searchPListTextView, searchSerial;//, fromThickness, toThickness
    private boolean isThicknessAsc = true, isWidthAsc = true, isLengthAsc = true;
    private String fromLengthNo = "", toLengthNo = "", fromWidthNo = "", toWidthNo = "", searchPackingList = "";//, fromThicknessNo = "", toThicknessNo = ""

    private ListView listView;
    private InventoryReportAdapter adapter;
    private int sortFlag = 0;
    private List<Double> doubleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_report);

        initialize();
//        searchViewTh = (SearchView) findViewById(R.id.mSearchTh);
//        searchViewW = (SearchView) findViewById(R.id.mSearchW);
//        searchViewL = (SearchView) findViewById(R.id.mSearchL);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        dateFrom.setText("1/12/2019");
        dateTo.setText(df.format(date));

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_to_right);
        textView.startAnimation(animation);

//        fillSpinnerAdapter();

        woodPresenter.getBundlesData(this);
        deleteAll.setOnClickListener(this);
        dateFrom.setOnClickListener(this);
        dateTo.setOnClickListener(this);
        thicknessOrder.setOnClickListener(this);
        widthOrder.setOnClickListener(this);
        lengthOrder.setOnClickListener(this);
        location.setOnItemSelectedListener(this);
        area.setOnItemSelectedListener(this);
        thicknessSpinner.setOnItemSelectedListener(this);
//        ordered.setOnItemSelectedListener(this);
        pList.setOnItemSelectedListener(this);
        grade.setOnItemSelectedListener(this);

        for (int v = 0; v < bundleInfoServer2.size(); v++) {
            BundleInfo fake = new BundleInfo();
            fake = bundleInfoServer2.get(v);
            bundlesForDelete.add(fake);
        }
//        fillTable(bundleInfoServer);

        checkBoxPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxPrint.isChecked()) {
                    for (int i = 0; i < filtered.size(); i++) {
                        filtered.get(i).setChecked(true);
                    }
                } else {
                    for (int i = 0; i < filtered.size(); i++) {
                        filtered.get(i).setChecked(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        printAll.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
//               PrintAll();
                try {
                    bundleInfoForPrint.clear();

                    InventoryReportAdapter obj = new InventoryReportAdapter();
                    List<BundleInfo> selected = obj.getSelectedItems();
                    for (int i = 0; i < selected.size(); i++) {
                        if (selected.get(i).getChecked()) {
                            bundleInfoForPrint.add(selected.get(i));
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

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        pListAll.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                bundleInfoForPList.clear();

                InventoryReportAdapter obj = new InventoryReportAdapter();
                List<BundleInfo> selected = obj.getSelectedItems();
                for (int i = 0; i < selected.size(); i++) {
                    if (selected.get(i).getChecked()) {
                        bundleInfoForPList.add(selected.get(i));
                    }
                }

                Dialog packingListDialog = new Dialog(InventoryReport.this);
                packingListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                packingListDialog.setContentView(R.layout.packing_list_dialog);
                packingListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                EditText packingList = packingListDialog.findViewById(R.id.packingList_dialog_packing_list);
                TextView done = packingListDialog.findViewById(R.id.packingList_dialog_done);
                TextView bundleNo = packingListDialog.findViewById(R.id.packingList_dialog_bundle_no);

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newPackingList = packingList.getText().toString();
                        if (packingList.getText().toString().equals(""))
                            newPackingList = "null";

                        for (int i = 0; i < bundleInfoForPList.size(); i++) {

                            woodPresenter.updatePackingList(InventoryReport.this, bundleInfoForPList.get(i).getBundleNo(), newPackingList, bundleInfoForPList.get(i).getLocation());
                        }
                        adapter.notifyDataSetChanged();
                        packingListDialog.dismiss();

                    }
                });

                packingListDialog.show();


            }

        });
//
       /* searchViewTh.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                f1 = query;
                filters();

                return false;
            }
        });

        searchViewW.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                f2 = query;
                filters();

                return false;
            }
        });

        searchViewL.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                f3 = query;
                filters();

                return false;
            }
        });*/

    }

    void initialize() {
        databaseHandler = new DatabaseHandler(this);
        woodPresenter = new WoodPresenter(this);
        generalSettings = new Settings();
        generalSettings = databaseHandler.getSettings();
        calendar = Calendar.getInstance();
        date = Calendar.getInstance().getTime();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Waiting...");

        thicknessSpinner = findViewById(R.id.inventory_report_thick_spinner);
        containerLayout = findViewById(R.id.inventory_report_container);
//        bundlesTable = findViewById(R.id.inventory_report_table);
        location = findViewById(R.id.inventory_report_location);
        area = findViewById(R.id.inventory_report_area);
        pList = findViewById(R.id.inventory_report_pl);
        grade = findViewById(R.id.inventory_report_grade);
//        ordered = findViewById(R.id.inventory_report_ordered);
        textView = findViewById(R.id.inventory_report_tv);
        dateFrom = findViewById(R.id.inventory_report_from);
        dateTo = findViewById(R.id.inventory_report_to);
        noOfBundles = findViewById(R.id.inventory_report_no_bundles);
        noOfPieces = findViewById(R.id.inventory_report_no_pieces);
        cubicField = findViewById(R.id.inventory_report_cubic);
        deleteAll = findViewById(R.id.inventory_report_delete);
        checkBoxPrint = findViewById(R.id.checkBoxPrint);
        printAll = findViewById(R.id.printAll);
        pListAll = findViewById(R.id.p_list_all);
        fromLength = findViewById(R.id.inventory_report_fromLength);
        toLength = findViewById(R.id.inventory_report_toLength);
        fromWidth = findViewById(R.id.inventory_report_fromWidth);
        toWidth = findViewById(R.id.inventory_report_toWidth);
//        fromThickness = findViewById(R.id.inventory_report_fromThick);
//        toThickness = findViewById(R.id.inventory_report_toThick);
        listView = findViewById(R.id.list);
        thicknessOrder = findViewById(R.id.inventory_report_thick_order);
        widthOrder = findViewById(R.id.inventory_report_width_order);
        lengthOrder = findViewById(R.id.inventory_report_length_order);
        searchPListTextView = findViewById(R.id.inventory_report_search_pList_box);
        searchPListTool = findViewById(R.id.inventory_report_search_pList_tool);
        searchPListTextView.setVisibility(View.GONE);
        searchSerial = findViewById(R.id.inventory_report_search_serial);
        searchSerialTool = findViewById(R.id.inventory_report_search_serial_tool);

        searchSerialTool.setOnClickListener(this);
        searchPListTool.setOnClickListener(this);
        searchPListTextView.addTextChangedListener(new watchTextChange(searchPListTextView));
        fromLength.addTextChangedListener(new watchTextChange(fromLength));
        toLength.addTextChangedListener(new watchTextChange(toLength));
        fromWidth.addTextChangedListener(new watchTextChange(fromWidth));
        toWidth.addTextChangedListener(new watchTextChange(toWidth));
//        fromThickness.addTextChangedListener(new watchTextChange(fromThickness));
//        toThickness.addTextChangedListener(new watchTextChange(toThickness));
    }

    void showLog(String method, String key, String value) {
        Log.e("inventory report", method + "/" + key + "/" + value);
    }

    public void fillSpinnerAdapter(List<String> thicknessList) {

        showLog("fillSpinnerAdapter", "thickness size", "" + thicknessList.size());
        removeDuplicate(thicknessList);
        showLog("fillSpinnerAdapter", "thickness size", "" + thicknessList.size());

        thicknessList.add(0, "All");
        thicknessAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, thicknessList);
        thicknessAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        thicknessSpinner.setAdapter(thicknessAdapter);

        locationList.add("All");
        locationList.add("Amman");
        locationList.add("Kalinovka");
        locationList.add("Rudniya Store");
        locationList.add("Rudniya Sawmill");
        locationAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, locationList);
        locationAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        location.setAdapter(locationAdapter);

        areaList.add("All");
        areaList.add("Zone 1");
        areaList.add("Zone 2");
        areaList.add("Zone 3");
        areaList.add("Zone 4");
        areaList.add("Zone 5");
        areaAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, areaList);
        areaAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        area.setAdapter(areaAdapter);

        plList.add("All");
        plList.add("P_List");
        plList.add("Not P_List");
        plAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, plList);
        plAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);//android.R.layout.simple_spinner_dropdown_item);
        pList.setAdapter(plAdapter);

        gradeList.add("All");
        gradeList.add("Fresh");
        gradeList.add("BS");
        gradeList.add("Reject");
        gradeList.add("KD");
        gradeList.add("KD Blue Stain");
        gradeList.add("Second Sort");
        gradeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, gradeList);
        gradeAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        grade.setAdapter(gradeAdapter);
    }

    void removeDuplicate(List<String> list) {
        Set<String> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
        doubleList = new ArrayList<>();
        for (int n = 0; n< list.size(); n++)
            doubleList.add(Double.valueOf(list.get(n)));

        Collections.sort(doubleList);
        list.clear();
        for (int n = 0; n< doubleList.size(); n++)
            list.add(String.valueOf(doubleList.get(n)));

    }

    class watchTextChange implements TextWatcher {

        private View view;

        public watchTextChange(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (view.getId()) {
                case R.id.inventory_report_search_pList_box:
                    searchPackingList = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    filters();
                    break;
                case R.id.inventory_report_fromLength:
                    fromLengthNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    filters();
                    break;
                case R.id.inventory_report_toLength:
                    toLengthNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    filters();
                    break;
                case R.id.inventory_report_fromWidth:
                    fromWidthNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    filters();
                    break;
                case R.id.inventory_report_toWidth:
                    toWidthNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    filters();
                    break;
//                case R.id.inventory_report_fromThick:
//                    fromThicknessNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
//                    filters();
//                    break;
//                case R.id.inventory_report_toThick:
//                    toThicknessNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
//                    filters();
//                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    String formatDecimalValue(String value) {
        String charOne = String.valueOf(value.charAt(0));
//        String charEnd = String.valueOf(value.charAt(value.length() - 1));

        if (charOne.equals("."))
            return ("0" + value);
//        else if (charEnd.equals(".")) {
//            value = value.substring(0, value.length() - 1);
////            Log.e("checkValidData3", "" +value);
//            return (value);
//        }
        return value;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void PrintAll(File file) {
        PrintManager printManager = (PrintManager) InventoryReport.this.getSystemService(Context.PRINT_SERVICE);
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

        File myFile = new File(pdfFolder + "inventory" + ".pdf");
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

    public void filters() {

        int sumOfBundles = 0;
        double sumOfCubic = 0, sumOfPieces = 0;

        bundleInfoServer.clear();
//        Log.e("inventoryReport", "/bundleInfoServer2/size/" + bundleInfoServer2.size());
        for (int v = 0; v < bundleInfoServer2.size(); v++) {
            BundleInfo fake = new BundleInfo();
            fake = bundleInfoServer2.get(v);
            bundleInfoServer.add(fake);
        }
        String fromDate = dateFrom.getText().toString().trim();
        String toDate = dateTo.getText().toString();
        filtered = new ArrayList<>();
        dateFiltered = new ArrayList<>();

//        Log.e("follow", fromDate + " to " + toDate + " size1 " + bundleInfoServer.size() + " loc: " + loc + "   area: " + areaField + "  ordered: " + orderedField);

        for (int m = 0; m < bundleInfoServer.size(); m++) {
//            JSONObject jsonObject = bundleInfoServer.get(m).getJSONObject();
            if ((formatDate(bundleInfoServer.get(m).getAddingDate()).after(formatDate(fromDate))
                    || formatDate(bundleInfoServer.get(m).getAddingDate()).equals(formatDate(fromDate)))
                    && (formatDate(bundleInfoServer.get(m).getAddingDate()).before(formatDate(toDate))
                    || formatDate(bundleInfoServer.get(m).getAddingDate()).equals(formatDate(toDate))))
                dateFiltered.add(bundleInfoServer.get(m));
        }
        Log.e("follow/", "size2/dateFiltered/ " + dateFiltered.size());

//        Log.e("follow", fromDate + " to " + toDate + " size1 " + bundleInfoServer.size() + " size2 " + dateFiltered.size());

        for (int k = 0; k < dateFiltered.size(); k++) {
//            Log.e("datefiltered", "" + dateFiltered.get(k).getBackingList());
            String dateFiltered2 = String.valueOf(dateFiltered.get(k).getOrdered());
            if (serialString.equals("All") || serialString.equals(String.valueOf(dateFiltered.get(k).getSerialNo())))
                if (loc.equals("All") || loc.equals(dateFiltered.get(k).getLocation())) {
                    if (areaField.equals("All") || areaField.equals(dateFiltered.get(k).getArea())) {
                        if (gradeFeld.equals("All") || gradeFeld.equals(dateFiltered.get(k).getGrade())) {

                            if (plField.equals("All") || (plField.equals("0") && dateFiltered.get(k).getBackingList().equals("null"))
                                    || (plField.equals("1") && !dateFiltered.get(k).getBackingList().equals("null"))) {

                                if (fromLengthNo.equals("") || ((dateFiltered.get(k).getLength() > Double.parseDouble(fromLengthNo))
                                        || dateFiltered.get(k).getLength() == Double.parseDouble(fromLengthNo)))

                                    if (toLengthNo.equals("") || ((dateFiltered.get(k).getLength() < Double.parseDouble(toLengthNo))
                                            || dateFiltered.get(k).getLength() == Double.parseDouble(toLengthNo)))

                                        if (fromWidthNo.equals("") || ((dateFiltered.get(k).getWidth() > Double.parseDouble(fromWidthNo))
                                                || dateFiltered.get(k).getWidth() == Double.parseDouble(fromWidthNo)))

                                            if (toWidthNo.equals("") || ((dateFiltered.get(k).getWidth() < Double.parseDouble(toWidthNo))
                                                    || dateFiltered.get(k).getWidth() == Double.parseDouble(toWidthNo)))

                                                if (thicknessField.equals("All") || thicknessField.equals(String.valueOf(dateFiltered.get(k).getThickness()))) {
//                                            if (fromThicknessNo.equals("") || ((dateFiltered.get(k).getThickness() > Double.parseDouble(fromThicknessNo))
//                                                    || dateFiltered.get(k).getThickness() == Double.parseDouble(fromThicknessNo)))
//
//                                                if (toThicknessNo.equals("") || ((dateFiltered.get(k).getThickness() < Double.parseDouble(toThicknessNo))
//                                                        || dateFiltered.get(k).getThickness() == Double.parseDouble(toThicknessNo)))
                                                    if (searchPackingList.equals("") || ((dateFiltered.get(k).getBackingList().contains(searchPackingList)))) {
                                                        filtered.add(dateFiltered.get(k));

                                                        sumOfBundles = filtered.size();
                                                        sumOfPieces += dateFiltered.get(k).getNoOfPieces();
                                                        sumOfCubic += (dateFiltered.get(k).getLength() * dateFiltered.get(k).getWidth() * dateFiltered.get(k).getThickness() * dateFiltered.get(k).getNoOfPieces());

                                                    }
                                                }
                            }
                        }
                    }
                }
        }
        Log.e("follow/", "size3/filtered/" + filtered.size());

        adapter = new InventoryReportAdapter(InventoryReport.this, filtered);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                serialNumber = filtered.get(position).getSerialNo();
                String bundleNumber = filtered.get(position).getBundleNo();
                String location = filtered.get(position).getLocation();
                Log.e("serialNumber", serialNumber);

                Dialog packingListDialog = new Dialog(InventoryReport.this);
                packingListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                packingListDialog.setContentView(R.layout.packing_list_dialog);
                packingListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                EditText packingList = packingListDialog.findViewById(R.id.packingList_dialog_packing_list);
                TextView done = packingListDialog.findViewById(R.id.packingList_dialog_done);
                TextView bundleNo = packingListDialog.findViewById(R.id.packingList_dialog_bundle_no);

                bundleNo.setText("Bundle: " + bundleNumber);


                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String newPackingList = packingList.getText().toString();
                        if (packingList.getText().toString().equals(""))
                            newPackingList = "null";

                        woodPresenter.updatePackingList(InventoryReport.this, bundleNumber, newPackingList, location);
                        adapter.notifyDataSetChanged();
                        packingListDialog.dismiss();

                    }
                });

                packingListDialog.show();
                return true;

            }
        });

        noOfBundles.setText("" + sumOfBundles);
        noOfPieces.setText("" + String.format("%.3f", sumOfPieces));
        cubicField.setText("" + String.format("%.3f", (sumOfCubic / 1000000000)));
//        fillTable(filtered);

    }

    public void updatedPackingList() {
        snackbar = Snackbar.make(containerLayout, Html.fromHtml("<font color=\"#3167F0\">Updated Successfully</font>"), Snackbar.LENGTH_SHORT);
        View snackbarLayout = snackbar.getView();
        TextView textViewSnackbar = (TextView) snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textViewSnackbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);
//                    textView.setCompoundDrawablePadding(10);//getResources().getDimensionPixelOffset(R.dimen.snackbar_icon_padding
        snackbar.show();
    }

    public Date formatDate(String date) {

//        Log.e("date", date);
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
        Date d = null;
        try {
            d = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public DatePickerDialog.OnDateSetListener openDatePickerDialog(final int flag) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                if (flag == 0)
                    dateFrom.setText(sdf.format(calendar.getTime()));
                else
                    dateTo.setText(sdf.format(calendar.getTime()));

                if (!dateFrom.getText().toString().equals("") && !dateTo.getText().toString().equals("")) {
                    filters();
                }
            }
        };
        return date;
    }

    public Bitmap writeBarcode(String data, String length, String width, String thic, String grades, String pcs) {
        final Dialog dialog = new Dialog(InventoryReport.this);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.inventory_report_location:
                loc = parent.getSelectedItem().toString();
                filters();
                break;
            case R.id.inventory_report_area:
                areaField = parent.getSelectedItem().toString();
                filters();
                break;
            case R.id.inventory_report_pl:
                plField = parent.getSelectedItem().toString();
                if (plField.equals("P_List"))
                    plField = "1";
                else if (plField.equals("Not P_List"))
                    plField = "0";
                Log.e("P_Listpinner", plField);
                filters();
                break;
            case R.id.inventory_report_grade:
                gradeFeld = parent.getSelectedItem().toString();
                filters();
                break;
            case R.id.inventory_report_thick_spinner:
                thicknessField = parent.getSelectedItem().toString();
                filters();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onClick(View v) {
        int flag = 0;
        switch (v.getId()) {
            case R.id.inventory_report_search_pList_tool:
                if (searchPListTextView.getVisibility() == View.VISIBLE) {
                    searchPListTextView.setVisibility(View.GONE);
                    pList.setVisibility(View.VISIBLE);
                } else {
                    searchPListTextView.setVisibility(View.VISIBLE);
                    pList.setVisibility(View.GONE);
                }
                break;
            case R.id.inventory_report_thick_order:
                sortFlag = 0;
                if (isThicknessAsc) {
                    isThicknessAsc = false;
                    thicknessOrder.setBackgroundResource(R.drawable.des);
                    Collections.sort(filtered, new SorterClass());
                } else { // des
                    isThicknessAsc = true;
                    thicknessOrder.setBackgroundResource(R.drawable.asc);
                    Collections.sort(filtered, Collections.reverseOrder(new SorterClass()));
//                    Collections.sort(filtered, Collections.reverseOrder());
//                    listView.setAdapter(adapter);
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.inventory_report_width_order:
                sortFlag = 1;
                if (isWidthAsc) {
                    isWidthAsc = false;
                    widthOrder.setBackgroundResource(R.drawable.des);
                    Collections.sort(filtered, new SorterClass());
                } else {
                    isWidthAsc = true;
                    widthOrder.setBackgroundResource(R.drawable.asc);
                    Collections.sort(filtered, Collections.reverseOrder(new SorterClass()));
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.inventory_report_length_order:
                sortFlag = 2;
                if (isLengthAsc) {
                    isLengthAsc = false;
                    lengthOrder.setBackgroundResource(R.drawable.des);
                    Collections.sort(filtered, new SorterClass());
                } else {
                    isLengthAsc = true;
                    lengthOrder.setBackgroundResource(R.drawable.asc);
                    Collections.sort(filtered, Collections.reverseOrder(new SorterClass()));
                }
                adapter.notifyDataSetChanged();
                break;
            case R.id.inventory_report_from:
                flag = 0;
                new DatePickerDialog(InventoryReport.this, openDatePickerDialog(flag), calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.inventory_report_to:
                flag = 1;
                new DatePickerDialog(InventoryReport.this, openDatePickerDialog(flag), calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.inventory_report_delete:
                Dialog passwordDialog = new Dialog(this);
                passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                passwordDialog.setContentView(R.layout.password_dialog);
                passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextInputEditText password = passwordDialog.findViewById(R.id.password_dialog_password);
                TextView done = passwordDialog.findViewById(R.id.password_dialog_done);

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (password.getText().toString().equals("301190")) {
                            bundleInfoForPrint.clear();

                            InventoryReportAdapter obj = new InventoryReportAdapter();
                            List<BundleInfo> selected = obj.getSelectedItems();
                            for (int i = 0; i < selected.size(); i++) {
                                if (selected.get(i).getChecked()) {
                                    bundleInfoForPrint.add(selected.get(i));
                                    jsonArrayBundles.put(bundleInfoForPrint.get(bundleInfoForPrint.size() - 1).getJSONObject());
                                }
                            }


                            new JSONTask3().execute();
                            passwordDialog.dismiss();
                        } else {
                            Toast.makeText(InventoryReport.this, "Not Authorized!", Toast.LENGTH_SHORT).show();
                            password.setText("");
                        }
                    }
                });
                passwordDialog.show();
                break;
            case R.id.inventory_report_search_serial_tool:
                if (!TextUtils.isEmpty(searchSerial.getText().toString()))
                    serialString = searchSerial.getText().toString();
                else
                    serialString = "All";

                filters();

                break;
        }

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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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

    //    private class JSONTask2 extends AsyncTask<String, String, String> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                String JsonResponse = null;
//                HttpClient client = new DefaultHttpClient();
//                HttpPost request = new HttpPost();
//                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214
//
//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//                nameValuePairs.add(new BasicNameValuePair("DELETE_BUNDLE", "1"));
//                nameValuePairs.add(new BasicNameValuePair("BUNDLE_NO", serialNumber));
//
//                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                HttpResponse response = client.execute(request);
//
//                BufferedReader in = new BufferedReader(new
//                        InputStreamReader(response.getEntity().getContent()));
//
//                StringBuffer sb = new StringBuffer("");
//                String line = "";
//
//                while ((line = in.readLine()) != null) {
//                    sb.append(line);
//                }
//
//                in.close();
//
//                JsonResponse = sb.toString();
//                Log.e("tag", "" + JsonResponse);
//
//                return JsonResponse;
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            Log.e("inventory report", "json 2 " + s);
//            if (s != null) {
//                if (s.contains("DELETE BUNDLE SUCCESS")) {
////                    bundlesTable.removeView(tableRowToDelete);
//                    bundleInfoServer2.remove(index);
////                    databaseHandler.deleteBundle(serialNumber);
//                    Toast.makeText(InventoryReport.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
//                    filters();
//                    Log.e("inventoryReport", "****Success");
//                } else {
//                    Toast.makeText(InventoryReport.this, "Failed to export data!", Toast.LENGTH_SHORT).show();
////                    Log.e("inventoryReport", "****Failed to export data");
//                }
//            } else {
//                Toast.makeText(InventoryReport.this, "No internet connection!", Toast.LENGTH_SHORT).show();
////                Log.e("inventoryReport", "****Failed to export data Please check internet connection");
//            }
//        }
//    }
    class SorterClass implements Comparator<BundleInfo> {
        @Override
        public int compare(BundleInfo one, BundleInfo another) {
            int returnVal = 0;
            switch (sortFlag) {
                case 0: // thickness
                    if (one.getThickness() < another.getThickness()) {
                        returnVal = -1;
                    } else if (one.getThickness() > another.getThickness()) {
                        returnVal = 1;
                    } else if (one.getThickness() == another.getThickness()) {
                        returnVal = 0;
                    }
                    break;

                case 1: // width
                    if (one.getWidth() < another.getWidth()) {
                        returnVal = -1;
                    } else if (one.getWidth() > another.getWidth()) {
                        returnVal = 1;
                    } else if (one.getWidth() == another.getWidth()) {
                        returnVal = 0;
                    }
                    break;

                case 2: // length
                    if (one.getLength() < another.getLength()) {
                        returnVal = -1;
                    } else if (one.getLength() > another.getLength()) {
                        returnVal = 1;
                    } else if (one.getLength() == another.getLength()) {
                        returnVal = 0;
                    }
                    break;
            }
            return returnVal;
        }

    }

    public void showProgressDialog(){
        progressDialog.show();
    }

    public void hideProgressDialog(){
        progressDialog.dismiss();
    }

    // ************************************ DELETE ************************************
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
                nameValuePairs.add(new BasicNameValuePair("DELETE_ALL_BUNDLES", "1"));
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
                Log.e("tag delete", "" + e.getMessage().toString());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("inventory report", "json 3 " + s);
            if (s != null) {
                if (s.contains("DELETE ALL BUNDLES SUCCESS")) {
                    for (int i = 0; i < bundleInfoForPrint.size(); i++) {
//                        databaseHandler.deleteBundle(filtered.get(i).getBundleNo());
                        for (int k = 0; k < bundleInfoServer2.size(); k++)
                            if (bundleInfoServer2.get(k).getBundleNo().equals(bundleInfoForPrint.get(i).getBundleNo())) {
                                bundleInfoServer2.remove(k);
                                k = bundleInfoServer2.size();
                            }
                    }
                    snackbar = Snackbar.make(containerLayout, Html.fromHtml("<font color=\"#3167F0\">Deleted Successfully</font>"), Snackbar.LENGTH_SHORT);
                    View snackbarLayout = snackbar.getView();
                    TextView textViewSnackbar = (TextView) snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
                    textViewSnackbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);
//                    textView.setCompoundDrawablePadding(10);//getResources().getDimensionPixelOffset(R.dimen.snackbar_icon_padding
                    snackbar.show();
                    adapter.notifyDataSetChanged();
                    filters();
                    if (checkBoxPrint.isChecked()) {
                        checkBoxPrint.setChecked(false);
                    }
                    Log.e("tag", "****Success");
                } else {
                    Toast.makeText(InventoryReport.this, "Failed to export data!", Toast.LENGTH_SHORT).show();
//                    Log.e("tag", "****Failed to export data");
                }
            } else {
                Toast.makeText(InventoryReport.this, "No internet connection!", Toast.LENGTH_SHORT).show();
//                Log.e("tag", "****Failed to export data Please check internet connection");
            }
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
