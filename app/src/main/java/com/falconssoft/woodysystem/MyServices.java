package com.falconssoft.woodysystem;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.falconssoft.woodysystem.models.SupplierInfo;
import com.falconssoft.woodysystem.stage_two.AddNewSupplier;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MyServices extends Service {
    private static final String TAG = "BackgroundUpdateServiceS";
    int i = 0;
    private DatabaseHandler DHandler=new DatabaseHandler(MyServices.this);
    Timer T;
    public static int approveAdmin = -1;
    String userNo = "0";
    String ipAddress="";

    public IBinder onBind(Intent arg0) {
        //Log.e(TAG, "onBind()" );
        return null;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate() {
        super.onCreate();
//
        DHandler=new DatabaseHandler(MyServices.this);
        try {
            ipAddress = DHandler.getSettings().getIpAddress();
            Log.e(TAG,"onCreate Setting in dataBase"+ipAddress+"   ");

        }catch (Exception e){
            Log.e(TAG,"onCreate no Setting in dataBase"+ipAddress+"   ");
        }
//        userNo= db.getAllUserNo();
//        if (settings.size() != 0) {
//            approveAdmin= settings.get(0).getApproveAdmin();
//            Log.e(TAG,"spical"+approveAdmin+"   ");
//        }
//
//        Log.e(TAG, "onCreate() , service started...");

    }

    @SuppressLint("LongLogTag")
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            ipAddress = DHandler.getSettings().getIpAddress();
            Log.e(TAG," Setting in dataBase"+ipAddress+"   ");

        }catch (Exception e){
            Log.e(TAG,"no Setting in dataBase"+ipAddress+"   ");
        }
//        userNo= db.getAllUserNo();
//        if (settings.size() != 0) {
//            approveAdmin= settings.get(0).getApproveAdmin();
//            Log.e(TAG,"spical"+approveAdmin+"   ");
//        }

        Timer();

        return START_STICKY;

    }

    void Timer() {
        if (T == null) {
            T = new Timer();
        }

        T.scheduleAtFixedRate(new TimerTask() {
            @SuppressLint("LongLogTag")
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                Log.e(TAG, "onStartCommand() , service started..." + i++);

                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        public void run() {
                            Log.e(TAG, "in 123123");
                            Handler h = new Handler(Looper.getMainLooper());
                            h.post(new Runnable() {
                                public void run() {

                                  new  UpdatePackingList().execute();

                                }
                            });
                        }
                    });


            }
        }, 10, 10000);
    }

    @SuppressLint("LongLogTag")
    public IBinder onUnBind(Intent arg0) {
        Log.e(TAG, "onUnBind()");
        return null;
    }

    @Override
    public boolean stopService(Intent name) {
        // TODO Auto-generated method stub
        T.cancel();

        return super.stopService(name);

    }

    @SuppressLint("LongLogTag")
    public void onPause() {
        Log.e(TAG, "onPause()");
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onDestroy() {
//        player.stop();
//        player.release();
//        Toast.makeText(this, "Service stopped...", Toast.LENGTH_SHORT).show();
        Timer();

        Log.e(TAG, "onCreate() , service stopped...");
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onLowMemory() {
        Log.e(TAG, "onLowMemory()");
    }

    @SuppressLint("LongLogTag")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        //  onStop();
//        startForegroundService(new Intent(MyService.this,MyService.class));
// URL url = new URL("http://" + DHandler.getSettings().getIpAddress() + "/import.php?FLAG=17");
        Log.e(TAG, "In onTaskRemoved");
    }

    private class UpdatePackingList extends AsyncTask<String, String, String> {
        private String JsonResponse = null;
        private HttpURLConnection urlConnection = null;
        private BufferedReader reader = null;

        @Override
        protected void onPreExecute() {


            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {///GetModifer?compno=736&compyear=2019
            try {
//                final List<MainSetting>mainSettings=dbHandler.getAllMainSetting();
//                String ip="";
//                if(mainSettings.size()!=0) {
//                    ip= mainSettings.get(0).getIP();
//                }

//
                URL url = new URL("http://" + ipAddress.trim() + "/import.php?FLAG=17");

                Log.e("urlString = ",""+url.toString());

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");


//
//                DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
//                wr.writeBytes(data);
//                wr.flush();
//                wr.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                StringBuffer stringBuffer = new StringBuffer();

                while ((JsonResponse = bufferedReader.readLine()) != null) {
                    stringBuffer.append(JsonResponse + "\n");
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                Log.e("tag", "ItemOCode -->" + stringBuffer.toString());

                return stringBuffer.toString();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("tag", "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String JsonResponse) {
            super.onPostExecute(JsonResponse);

            if (JsonResponse != null && JsonResponse.contains("ItemOCode")) {
                Log.e("tag_ItemOCode", "****Success");
            }

        }
    }
}