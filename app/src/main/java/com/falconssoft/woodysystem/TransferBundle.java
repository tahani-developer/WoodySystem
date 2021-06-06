package com.falconssoft.woodysystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.PrintManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.reports.InventoryReport;
import com.falconssoft.woodysystem.reports.InventoryReportAdapter;
import com.falconssoft.woodysystem.reports.TransferAdapter;
import com.falconssoft.woodysystem.stage_one.AddNewRaw;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.ls.LSException;

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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class TransferBundle extends AppCompatActivity {

    Button transferLocationButton;
    TextView fromLocation, toLocation, scanBarcode, transfer, print, thickness, width, length, grade, location, pieces, bundleNo;
    EditText transferBundleNo;
    int flagTransfer = 0;
    String barcodeValue = "";
    DatabaseHandler databaseHandler;
    String ipAddress = "";
    TransferAdapter transferAdapter;
    ListView transferList;
    List<BundleInfo> bundleInfoForPrint;
    List<BundleInfo> bundleInfoList;
    private SimpleDateFormat df;
    String DateString = "",maxSerial="";
    private Calendar calendar;
    private Date date;
    private RelativeLayout coordinatorLayout;
    private Snackbar snackbar;
    private Settings generalSettings;
    ProgressDialog progressDialog ;

    BundleInfo bundleInfoG=new BundleInfo();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transfer_bundle);

        initialization();


    }

    private void initialization() {

        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please Waiting...");
        progressDialog.setCanceledOnTouchOutside(false);


        transferLocationButton = findViewById(R.id.transferLocationButton);
        thickness = findViewById(R.id.thickness_edit);
        width = findViewById(R.id.width_edit);
        length = findViewById(R.id.length_edit);
        grade = findViewById(R.id.grade);
        location = findViewById(R.id.location);
        pieces = findViewById(R.id.pieces_edit);
        bundleNo = findViewById(R.id.bundleNo);
        databaseHandler = new DatabaseHandler(TransferBundle.this);
        fromLocation = findViewById(R.id.fromLocation);
        toLocation = findViewById(R.id.toLocation);
        transferBundleNo = findViewById(R.id.transfer_search_bundleNo);
        scanBarcode = findViewById(R.id.scanBarcode);
        transfer = findViewById(R.id.transfer);
        print = findViewById(R.id.print);
        transferList=findViewById(R.id.transferList);
        bundleInfoForPrint=new ArrayList<>();
        bundleInfoList=new ArrayList<>();
        generalSettings = new Settings();
        //coordinatorLayout = findViewById(R.id.addNewRow_coordinator);
        try {
            generalSettings = databaseHandler.getSettings();
        }catch (Exception E){

        }
        calendar = Calendar.getInstance();
        date = Calendar.getInstance().getTime();

        df = new SimpleDateFormat("dd/MM/yyyy");
        DateString = df.format(date);



        try {
            ipAddress = databaseHandler.getSettings().getIpAddress();
        } catch (Exception e) {
            ipAddress = "";
        }
        transferLocationButton.setOnClickListener(onClickListener);
        scanBarcode.setOnClickListener(onClickListener);
        transfer.setOnClickListener(onClickListener);
        print.setOnClickListener(onClickListener);

        fillTransferAdapter();

        transferBundleNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!transferBundleNo.getText().toString().equals("")) {
                    new getBundle(transferBundleNo.getText().toString()).execute();
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });





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
                    Bitmap bitmap = writeBarcode(String.valueOf(bundleInfoForPrint.get(i).getNewBundleNo()), String.valueOf(bundleInfoForPrint.get(i).getLength()), String.valueOf(bundleInfoForPrint.get(i).getWidth()),
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

        File myFile = new File(pdfFolder + "transfer" + ".pdf");
        return myFile;
    }




    public Bitmap writeBarcode(String data, String length, String width, String thic, String grades, String pcs) {
        final Dialog dialog = new Dialog(TransferBundle.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.barcode_design);
        TextView companyName, bundelNo, TLW, pcsNo, grade, DatePrint;

        companyName = (TextView) dialog.findViewById(R.id.companyName);
        bundelNo = (TextView) dialog.findViewById(R.id.bundelNo);
        TLW = (TextView) dialog.findViewById(R.id.TLW);
        pcsNo = (TextView) dialog.findViewById(R.id.pcsNo);
        grade = (TextView) dialog.findViewById(R.id.grade);
        ImageView iv = (ImageView) dialog.findViewById(R.id.barcode);
        DatePrint = dialog.findViewById(R.id.DatePrint);

        DatePrint.setText("" + DateString);
        try {
            companyName.setText(generalSettings.getCompanyName().toUpperCase());
        }catch (Exception e ){}
        bundelNo.setText(data);

        String thicNoDot = thic.substring(0, thic.indexOf("."));
        Log.e("thicNoDot", thicNoDot);
        String widthNoDot = width.substring(0, width.indexOf("."));
        Log.e("widthNoDot", widthNoDot);
        String lengthNoDot = length.substring(0, length.indexOf("."));
        Log.e("lengthNoDot", lengthNoDot);
        TLW.setText(thicNoDot + " X " + widthNoDot + " X " + lengthNoDot);


        String pcsNoDot = pcs.substring(0, pcs.indexOf("."));
        Log.e("pcsNoDot", pcsNoDot);
        pcsNo.setText(pcsNoDot);

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



    void fillTransferAdapter(){
        transferAdapter=new TransferAdapter(TransferBundle.this,bundleInfoList);
        transferList.setAdapter(transferAdapter);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.transfer:
                    transferBundle();
                    break;
                case R.id.print:
                    printBarCode();
                    break;
                case R.id.transferLocationButton:
                    switchLocation();
                    break;
                case R.id.scanBarcode:
                    scanBarcode();
                    break;
            }

        }
    };

    void switchLocation() {

        if (flagTransfer == 0) {
            flagTransfer = 1;
            fromLocation.setText("Rudniya Store");
            toLocation.setText("Kalinovka");
        } else {
            flagTransfer = 0;
            fromLocation.setText("Kalinovka");
            toLocation.setText("Rudniya Store");
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void printBarCode() {

        try {
            bundleInfoForPrint.clear();

            TransferAdapter obj = new TransferAdapter();
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


    void scanBarcode() {

        IntentIntegrator intentIntegrator = new IntentIntegrator(TransferBundle.this);
        intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setPrompt("SCAN");
        intentIntegrator.setBarcodeImageEnabled(false);
        intentIntegrator.initiateScan();

    }

    void transferBundle() {

        if (!transferBundleNo.getText().toString().equals("")) {
            if (!bundleNo.getText().toString().equals("")) {
                if (!thickness.getText().toString().equals("")) {
                    if (!width.getText().toString().equals("")) {
                        if (!length.getText().toString().equals("")) {
                            if (!pieces.getText().toString().equals("")) {
                                if (!location.getText().toString().equals("")) {
                                    if (!grade.getText().toString().equals("")) {

                                        if (!toLocation.getText().toString().equals(location.getText().toString())) {

                                            new JSONTask3().execute();


//                                            bundleInfoForPrint.add();

                                        } else {
                                            Toast.makeText(this, "can not Transfer The Bundle To Same Location", Toast.LENGTH_LONG).show();
                                        }

                                    } else {
                                        grade.setError("Required");
                                    }
                                } else {
                                    location.setError("Required");
                                }
                            } else {
                                pieces.setError("Required");
                            }
                        } else {
                            length.setError("Required");
                        }
                    } else {
                        width.setError("Required");
                    }
                } else {
                    thickness.setError("Required");
                }
            } else {
                bundleNo.setError("Required");
            }
        } else {
            transferBundleNo.setError("Required");
        }


    }

    @SuppressLint("SetTextI18n")
    private void fillText(BundleInfo bundleInfo) {

        thickness.setText("" + bundleInfo.getThickness());
        width.setText("" + bundleInfo.getWidth());
        length.setText("" + bundleInfo.getLength());
        grade.setText("" + bundleInfo.getGrade());
        location.setText("" + bundleInfo.getLocation());
        pieces.setText("" + bundleInfo.getNoOfPieces());
        bundleNo.setText("" + bundleInfo.getBundleNo());
    }

    void clearText() {

        thickness.setText("");
        width.setText("");
        length.setText("");
        grade.setText("");
        location.setText("");
        pieces.setText("");
        bundleNo.setText("");
        bundleInfoG=null;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void PrintAll(File file) {
        PrintManager printManager = (PrintManager) TransferBundle.this.getSystemService(Context.PRINT_SERVICE);
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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult Result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (Result != null) {
            if (Result.getContents() == null) {
                Log.d("MainActivity", "cancelled scan");
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
                barcodeValue = "cancelled";
                transferBundleNo.setText("");
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned -> " + Result.getContents(), Toast.LENGTH_SHORT).show();

                barcodeValue = Result.getContents();
                transferBundleNo.setText(barcodeValue);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private class getBundle extends AsyncTask<String, String, String> {

        private String bundleNo = "", URL_TO_HIT = "";

        public getBundle(String bundleNo) {
            this.bundleNo = bundleNo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {

            try {

                URL_TO_HIT = "http://" + ipAddress.trim() + "/import.php?FLAG=18&Bundle_no=" + bundleNo;

            } catch (Exception e) {

            }

            try {

                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(URL_TO_HIT));

//

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
                Log.e("tag_allcheques", "JsonResponse\t" + JsonResponse);

                return JsonResponse;


            }//org.apache.http.conn.HttpHostConnectException: Connection to http://10.0.0.115 refused
            catch (HttpHostConnectException ex) {
                ex.printStackTrace();
//                progressDialog.dismiss();

                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {

                        Toast.makeText(TransferBundle.this, "Ip Connection Failed AccountStatment", Toast.LENGTH_LONG).show();
                    }
                });


                return null;
            } catch (Exception e) {
                e.printStackTrace();
//                progressDialog.dismiss();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject result = null;
            String impo = "";
            if (s != null) {
                if (s.contains("OLD_BUNDLE")) {

                    try {
                        JSONObject jsonObject = new JSONObject(s);

                        JSONArray jsonArray = jsonObject.getJSONArray("OLD_BUNDLE");
                        BundleInfo bundleInfo;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            if (jsonObject1.getString("ORDERED").equals("0")) {
                                if (!jsonObject1.getString("LOCATION").equals("Amman")) {
                                bundleInfo = new BundleInfo();
                                bundleInfo.setThickness(jsonObject1.getDouble("THICKNESS"));
                                bundleInfo.setWidth(jsonObject1.getDouble("WIDTH"));
                                bundleInfo.setLength(jsonObject1.getDouble("LENGTH"));
                                bundleInfo.setGrade(jsonObject1.getString("GRADE"));
                                bundleInfo.setNoOfPieces(jsonObject1.getDouble("PIECES"));
                                bundleInfo.setBundleNo(jsonObject1.getString("BUNDLE_NO"));
                                bundleInfo.setLocation(jsonObject1.getString("LOCATION"));
                                bundleInfo.setArea(jsonObject1.getString("AREA"));
                                bundleInfo.setBarcode(jsonObject1.getString("BARCODE"));
                                bundleInfo.setOrdered(jsonObject1.getInt("ORDERED"));
                                bundleInfo.setDateOfLoad(jsonObject1.getString("BUNDLE_DATE"));
                                bundleInfo.setDescription(jsonObject1.getString("DESCRIPTION"));
                                bundleInfo.setSerialNo(jsonObject1.getString("B_SERIAL"));
                                bundleInfo.setUserNo(jsonObject1.getString("USER_NO"));
                                bundleInfo.setIsPrinted(jsonObject1.getInt("IS_PRINTED"));
                                bundleInfo.setBackingList(jsonObject1.getString("BACKING_LIST"));
                                bundleInfo.setCustomer(jsonObject1.getString("CUSTOMER"));
                                    bundleInfoG=new BundleInfo();
                                bundleInfoG=bundleInfo;
                                fillText(bundleInfo);

                                break;
                                } else {
                                    clearText();
                                    Toast.makeText(TransferBundle.this, "can Not Transfer Bundle From Or To Amman", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                clearText();
                                Toast.makeText(TransferBundle.this, "The Bundle Is Ordered", Toast.LENGTH_LONG).show();
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else if (s.contains("noBundleFound")) {
                    clearText();
                    Toast.makeText(TransferBundle.this, "The Bundle Not Found", Toast.LENGTH_LONG).show();

                }
            }
        }

    }

    // ******************************************** max serial ***************************************************
    private class JSONTask3 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
//                http://10.0.0.22/woody/import.php?FLAG=2

                URL url = new URL(("http://" + ipAddress + "/import.php?FLAG=14&LOCATION=" + toLocation.getText().toString().trim().replace(" ","%20")));

                Log.e("JSONTask3", url.toString());
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                String finalJson = sb.toString();
                Log.e("JSONTask3", finalJson);

                JSONObject object = new JSONObject(finalJson);
                maxSerial = object.getJSONArray("MAX_SERIAL").getJSONObject(0).getString("MAX");
                Log.e("JSONTask3 ", "maxSerial:" + maxSerial);

            } catch (MalformedURLException e) {
                Log.e("Customer", "********ex1");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Customer", e.getMessage().toString());
                e.printStackTrace();

            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                Log.e("Customer", "********finally");
                if (connection != null) {
                    Log.e("Customer", "********ex4");
                    // connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return maxSerial;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null)
                Log.e("tag", "JSONTask3/Failed to export data Please check internet connection");
            else
               saveTransfer();

        }
    }

    private void saveTransfer() {

        BundleInfo bundleInfo=new BundleInfo();
        Log.e("maxSerial", "hh ="+maxSerial);
        bundleInfo=bundleInfoG;
        bundleInfo.setNewSerial(maxSerial);
        String newBundles="";
        if(toLocation.getText().toString().equals("Kalinovka")){
            newBundles=bundleInfo.getBundleNo().replace("Rud","Kal");
            newBundles=newBundles.substring(0,newBundles.lastIndexOf("."));
            newBundles=newBundles+"."+maxSerial;
            Log.e("newBundles", "hh ="+newBundles);
        }else {
            newBundles=bundleInfo.getBundleNo().replace("Kal","Rud");
            newBundles=newBundles.substring(0,newBundles.lastIndexOf("."));
            newBundles=newBundles+"."+maxSerial;
            Log.e("newBundles", "hh ="+newBundles);

        }


        bundleInfo.setNewBundleNo(newBundles);
        bundleInfo.setNewLocation(toLocation.getText().toString());



        new SaveTransfer().execute();
       // clearText();


    }


    //-------------------------------------SAVE -----------------------------------

    private class SaveTransfer extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
          progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
//https://5.189.130.98/WOODY/export.php

                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("TRANSFER_BUNDLE", bundleInfoG.getJSONObjectTransfer().toString().trim())); // json object

                Log.e("TRANSFER_BUNDLE/", "save " + bundleInfoG.getJSONObjectTransfer().toString().trim());
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
                Log.e("tag", "save " + JsonResponse);

                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("tag of row info", "" + s);
            if (s != null) {
                if (s.contains("TRANSFER_BUNDLE_SUCCESS")) {

                    //showSnackbar("Added Successfully", true);
                    bundleInfoList.add(bundleInfoG);
                    // transferAdapter.notifyDataSetChanged();
                    fillTransferAdapter();
                    clearText();
                    progressDialog.dismiss();
                    Log.e("tag", "save Success");
                } else {
                    progressDialog.dismiss();
                    Log.e("tag", "****Failed to export data");
                }
            } else {
                progressDialog.dismiss();
                Log.e("tag", "****Failed to export data Please check internet connection");
                Toast.makeText(TransferBundle.this, "Failed to export data Please check internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }


    void showSnackbar(String text, boolean showImage) {
        snackbar = Snackbar.make(coordinatorLayout, Html.fromHtml("<font color=\"#3167F0\">" + text + "</font>"), Snackbar.LENGTH_SHORT);//Updated Successfully
        View snackbarLayout = snackbar.getView();
        TextView textViewSnackbar = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
        if (showImage)
            textViewSnackbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);
        snackbar.show();
    }


}
