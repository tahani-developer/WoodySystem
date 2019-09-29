package com.falconssoft.woodysystem;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class AddToInventory extends AppCompatActivity implements View.OnClickListener {

    private EditText thickness, length, width, grade, noOfPieces, bundleNo, location, area;
    private Button addToInventory;
    private TextView textView;
    private BundleInfo newBundle;
    private DatabaseHandler databaseHandler;
    private Animation animation;


    byte[] printIm;
    PrintPic printPic;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_inventory);

        databaseHandler = new DatabaseHandler(this);
        thickness = findViewById(R.id.addToInventory_thickness);
        length = findViewById(R.id.addToInventory_length);
        width = findViewById(R.id.addToInventory_width);
        grade = findViewById(R.id.addToInventory_grade);
        noOfPieces = findViewById(R.id.addToInventory_no_of_pieces);
        bundleNo = findViewById(R.id.addToInventory_bundle_no);
        location = findViewById(R.id.addToInventory_location);
        area = findViewById(R.id.addToInventory_area);
        addToInventory = findViewById(R.id.addToInventory_add_button);
        textView = findViewById(R.id.addToInventory_textView);

        addToInventory.setOnClickListener(this);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_to_right);
        textView.startAnimation(animation);

//        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
//        addToInventory.startAnimation(animation);

    }


    @Override
    public void onClick(View v) {
        if (!TextUtils.isEmpty(thickness.getText().toString())) {
            if (!TextUtils.isEmpty(length.getText().toString())) {
                if (!TextUtils.isEmpty(width.getText().toString())) {
                    if (!TextUtils.isEmpty(grade.getText().toString())) {
                        if (!TextUtils.isEmpty(noOfPieces.getText().toString())) {
                            if (!TextUtils.isEmpty(bundleNo.getText().toString())) {
                                if (!TextUtils.isEmpty(location.getText().toString())) {
                                    if (!TextUtils.isEmpty(area.getText().toString())) {

                                        String data=thickness.getText().toString()+length.getText().toString()+width.getText().toString()
                                                +grade.getText().toString();

                                        Bitmap bitmap=writeBarcode(data);


                                        newBundle = new BundleInfo(Double.parseDouble(thickness.getText().toString())
                                                , Double.parseDouble(length.getText().toString())
                                                , Double.parseDouble(width.getText().toString())
                                                , grade.getText().toString()
                                                , Integer.parseInt(noOfPieces.getText().toString())
                                                , bundleNo.getText().toString()
                                                , location.getText().toString()
                                                , area.getText().toString()
                                                ,BitMapToString(bitmap));
                                        databaseHandler.addNewBundle(newBundle);
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
                    } else {
                        grade.setError("Required!");
                    }
                } else {
                    width.setError("Required!");
                }
            } else {
                length.setError("Required!");
            }
        } else {
            thickness.setError("Required!");
        }

    }


    public Bitmap writeBarcode(String data){
        final Dialog dialog = new Dialog(AddToInventory.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.barcode_dialog);


        ImageView iv=(ImageView)dialog.findViewById(R.id.iv);
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

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;


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
            printIm= printPic.printDraw();
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


    public String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] arr=baos.toByteArray();
        String result= Base64.encodeToString(arr, Base64.DEFAULT);
        return result;
    }



    public Bitmap StringToBitMap(String image){
        if(image!="") {
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




}
