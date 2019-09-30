package com.falconssoft.woodysystem;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.BundleInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AddToInventory extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private EditText thickness, length, width, noOfPieces, bundleNo, location, area;
    private Spinner gradeSpinner;
    private Button addToInventory, newBundleButton;
    private TableLayout bundlesTable;
    private TextView textView;
    private BundleInfo newBundle;
    private DatabaseHandler databaseHandler;
    private Animation animation;
    private PrintPic printPic;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread workerThread;
    private byte[] printIm, readBuffer;
    private int readBufferPosition;
    volatile boolean stopWorker;
    private List<String> gradeList = new ArrayList<>();
    private ArrayAdapter<String> gradeAdapter;
    private String gradeText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_inventory);

        databaseHandler = new DatabaseHandler(this);
        thickness = findViewById(R.id.addToInventory_thickness);
        length = findViewById(R.id.addToInventory_length);
        width = findViewById(R.id.addToInventory_width);
        gradeSpinner = findViewById(R.id.addToInventory_grade);
        noOfPieces = findViewById(R.id.addToInventory_no_of_pieces);
        bundleNo = findViewById(R.id.addToInventory_bundle_no);
        location = findViewById(R.id.addToInventory_location);
        area = findViewById(R.id.addToInventory_area);
        addToInventory = findViewById(R.id.addToInventory_add_button);
        newBundleButton = findViewById(R.id.addToInventory_new_button);
        textView = findViewById(R.id.addToInventory_textView);
        bundlesTable = findViewById(R.id.addToInventory_table);

        addToInventory.setOnClickListener(this);
        newBundleButton.setOnClickListener(this);

        gradeList.add("Fresh");
        gradeList.add("BS");
        gradeList.add("Reject");
        gradeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gradeList);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);
        gradeSpinner.setOnItemSelectedListener(this);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_to_right);
        textView.startAnimation(animation);

//        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
//        addToInventory.startAnimation(animation);
    }

    @Override
    public void onClick(View v) {
        String thicknessText = thickness.getText().toString();
        String lengthText = length.getText().toString();
        String widthText = width.getText().toString();
        String noOfPiecesText = noOfPieces.getText().toString();
        String bundleNOText = bundleNo.getText().toString();
        String locationText = location.getText().toString();
        String areaText = area.getText().toString();

        switch (v.getId()) {
            case R.id.addToInventory_add_button:
                if (!TextUtils.isEmpty(thickness.getText().toString())) {
                    if (!TextUtils.isEmpty(length.getText().toString())) {
                        if (!TextUtils.isEmpty(width.getText().toString())) {
//                    if (!TextUtils.isEmpty(grade.getText().toString())) {
                            if (!TextUtils.isEmpty(noOfPieces.getText().toString())) {
                                if (!TextUtils.isEmpty(bundleNo.getText().toString())) {
                                    if (!TextUtils.isEmpty(location.getText().toString())) {
                                        if (!TextUtils.isEmpty(area.getText().toString())) {

                                            String data = thicknessText + lengthText + widthText;//+ gradeText;
//                                            Bitmap bitmap = writeBarcode(data);
                                            newBundle = new BundleInfo(Double.parseDouble(thicknessText)
                                                    , Double.parseDouble(lengthText)
                                                    , Double.parseDouble(widthText)
                                                    , "fresh"
                                                    , Integer.parseInt(noOfPiecesText)
                                                    , bundleNOText
                                                    , locationText
                                                    , areaText
                                                    , 1
                                                    , 53
                                                    , 5
                                                    , "june");
                                            databaseHandler.addNewBundle(newBundle);

                                            TableRow tableRow = new TableRow(this);
                                            for (int i = 0; i < 8; i++) {
                                                TextView textView = new TextView(this);
                                                textView.setBackgroundResource(R.color.light_orange);
                                                TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                                                textViewParam.setMargins(1, 5, 1, 1);
                                                textView.setTextSize(15);
                                                textView.setTextColor(ContextCompat.getColor(this, R.color.gray_dark_one));
                                                textView.setLayoutParams(textViewParam);
                                                switch (i) {
                                                    case 0:
                                                        textView.setText(bundleNOText);
                                                        break;
                                                    case 1:
                                                        textView.setText(lengthText);
//                    textView.setText("65");

                                                        break;
                                                    case 2:
                                                        textView.setText(widthText);
//                    textView.setText("65");
                                                        break;
                                                    case 3:
                                                        textView.setText(thicknessText);
//                    textView.setText("65");
                                                        break;
                                                    case 4:
                                                        textView.setText(gradeText);
//                                                textView.setText("new");
                                                        break;
                                                    case 5:
                                                        textView.setText(noOfPiecesText);
//                    textView.setText("200");
                                                        break;
                                                    case 6:
                                                        textView.setText(locationText);
//                    textView.setText("amman");
                                                        break;
                                                    case 7:
                                                        textView.setText(areaText);
//                    textView.setText("zone1");
                                                        break;
                                                }
                                                tableRow.addView(textView);
                                            }
                                            bundlesTable.addView(tableRow);
                                            Toast.makeText(this, "Added Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            area.setError("Required!");
                                        }
                                    } else {
                                        location.setError("Required!");
                                    }
                                } else {
                                    bundleNo.setError("Required!");
                                }
                            } else {
                                noOfPieces.setError("Required!");
                            }
//                    } //else {
//                        grade.setError("Required!");
//                    }
                        } else {
                            width.setError("Required!");
                        }
                    } else {
                        length.setError("Required!");
                    }
                } else {
                    thickness.setError("Required!");
                }
                break;
            case R.id.addToInventory_new_button:
                thickness.setText("");
                length.setText("");
                width.setText("");
                noOfPieces.setText("");
                bundleNo.setText("");
                location.setText("");
                area.setText("");
                gradeSpinner.setSelection(0);
                break;
        }
    }

    public Bitmap writeBarcode(String data) {
        final Dialog dialog = new Dialog(AddToInventory.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.barcode_dialog);

        ImageView iv = (ImageView) dialog.findViewById(R.id.iv);
        // barcode data
        String barcode_data = data;

        Bitmap bitmap = null;//  AZTEC -->QR
        try { 

            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.CODE_128, 600, 300);
            iv.setImageBitmap(bitmap);

            try {
                findBT();
                openBT(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }


        } catch (WriterException e) {
            e.printStackTrace();
        }

        dialog.show();

        return bitmap;

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

    void findBT() {
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
//                myLabel.setText("No bluetooth adapter available");
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // MP300 is the name of the bluetooth printer device07-28 13:20:10.946  10461-10461/com.example.printer E/sex﹕ C4:73:1E:67:29:6C
                    /*07-28 13:20:10.946  10461-10461/com.example.printer E/sex﹕ E8:99:C4:FF:B1:AC
                    07-28 13:20:10.946  10461-10461/com.example.printer E/sex﹕ 0C:A6:94:35:11:27*/

                    /*Log.e("sex",device.getName());*/
//                    if (device.getName().equals("mobile printer")) { // PR3-30921446556
                    /*Log.e("sex1",device.getAddress());*/
                    mmDevice = device;
//                        break;
//                    }
                }
            }
//            myLabel.setText("Bluetooth Device Found");

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tries to open a connection to the bluetooth printer device
    void openBT(Bitmap bitmap) throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();


            sendData(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // After opening a connection to bluetooth printer device,
    // we have to listen and check if a data were sent to be printed.
    void beginListenForData() throws IOException {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
//                                                myLabel.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e) {
            closeBT();
            e.printStackTrace();
        } catch (Exception e) {
            closeBT();
            e.printStackTrace();
        }
    }

    /*
     * This will send data to be printed by the bluetooth printer
     */
    void sendData(Bitmap bitmap) throws IOException {
        try {
            printPic = PrintPic.getInstance();
            printPic.init(bitmap);
            printIm = printPic.printDraw();
            mmOutputStream.write(printIm);

        } catch (NullPointerException e) {
            closeBT();
            e.printStackTrace();
        } catch (Exception e) {
            closeBT();
            e.printStackTrace();
        }
    }

    // Close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            workerThread.stop();
//            myLabel.setText("Bluetooth Closed");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] arr = baos.toByteArray();
        String result = Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }

    public Bitmap StringToBitMap(String image) {
        if (image != "") {
            try {
                byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                return bitmap;
            } catch (Exception e) {
                e.getMessage();
                return null;
            }
        }
        return null;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        gradeText = parent.getItemAtPosition(position).toString();
        Log.e("item", gradeText);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        gradeText = "Fresh";
    }
}
