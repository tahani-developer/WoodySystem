package com.falconssoft.woodysystem;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.models.Users;
import com.falconssoft.woodysystem.reports.InventoryReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static com.falconssoft.woodysystem.reports.InventoryReport.bundleInfoServer;
import static com.falconssoft.woodysystem.reports.InventoryReport.bundleInfoServer2;

public class WoodPresenter implements Response.ErrorListener, Response.Listener<String> {

    private Context context;
    private Settings settings;
    private RequestQueue requestQueue;
    private DatabaseHandler databaseHandler;

    private StringRequest jsonObjectRequest;
    private String urlImport;

    private StringRequest usersJsonObjectRequest;
    private String urlUsers;

    private StringRequest bundlesJsonObjectRequest;
    private String urlBundles;
    private InventoryReport inventoryReport;//= new InventoryReport();
//    private String urlImport = "http://" + SettingsFile.ipAddress + "/import.php?FLAG=1";//http://5.189.130.98:8085/import.php?FLAG=1

//    private String urlUsers = "http://" + SettingsFile.ipAddress + "/import.php?FLAG=0";//http://10.0.0.214/WOODY/import.php?FLAG=0

    public WoodPresenter(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        databaseHandler = new DatabaseHandler(context);
    }

    //------------------------------------------------------------------------------------------------

    void getImportData() {
        settings = databaseHandler.getSettings();
        urlImport = "http://" + settings.getIpAddress() + "/import.php?FLAG=1";//http://5.189.130.98:8085/import.php?FLAG=1
//        Log.e("presenter:ipImport ", "" + SettingsFile.ipAddress);
//        Log.e("presenter:urlImport ", "" + urlImport);
        jsonObjectRequest = new StringRequest(Request.Method.GET, urlImport, this, this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("presenter/import/err ", "" + error);
        SettingsFile.serialNumber = "";
    }

    @Override
    public void onResponse(String response) {
        try {
            response=new String(response.getBytes("ISO-8859-1"), "UTF-8");
//            if (response.indexOf("{") != 0)
//                response = response.substring(response.indexOf("{"));
            Log.e("presenter: import ", "" + response);
            JSONObject object = new JSONObject(response);
            Log.e("presenter1: import ", "" + response);
            JSONObject object2 = object.getJSONArray("Bundles").getJSONObject(0);
            Log.e("presenter2: import ", "" + object2);
            SettingsFile.serialNumber = "";
            SettingsFile.serialNumber = object2.getString("MAX_SERIAL");
            Log.e("presenter3: import ", "" + object2.getString("MAX_SERIAL"));
            Log.e("presenter4: import ", "" + SettingsFile.serialNumber);

        } catch (JSONException e) {
            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    //------------------------------------------------------------------------------------------------

    void getUsersData() {
        settings = databaseHandler.getSettings();
        urlUsers = "http://" + settings.getIpAddress() + "/import.php?FLAG=0";
//        Log.e("presenter/urlUsers ", "" + urlUsers);
//        Log.e("presenter:ipUsers ", "" + SettingsFile.ipAddress);
        usersJsonObjectRequest = new StringRequest(Request.Method.GET, urlUsers, new UsersResponseClass(), new UsersResponseClass());
        requestQueue.add(usersJsonObjectRequest);
    }

    class UsersResponseClass implements Response.Listener<String>, Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("presenter/users/err ", "" + settings.getIpAddress() + " *** " + urlUsers + " *** " + error);
            if (error instanceof NetworkError) {
            } else if (error instanceof ServerError) {
                Log.e("presenter/users/err ", "0");

            } else if (error instanceof AuthFailureError) {
                Log.e("presenter/users/err ", "1");

            } else if (error instanceof ParseError) {
                Log.e("presenter/users/err ", "2");

            } else if (error instanceof NoConnectionError) {
                Log.e("presenter/users/err ", "3");

            } else if (error instanceof TimeoutError) {
                Log.e("presenter/users/err ", "4");

            }

            SettingsFile.usersList.clear();
            SettingsFile.usersList = databaseHandler.getUsers();
//            Log.e("presenter/users/url ", "" + urlUsers);
//            Log.e("presenter/users/err ", "" + error);
        }

        @Override
        public void onResponse(String response) {
            try {
                databaseHandler.deleteUsers();
                SettingsFile.usersList.clear();
//                Log.e("presenter/users/res ", "before " + (response.indexOf("{") != 0));

//                if (response.indexOf("{") != 0)
//                    response = response.substring(response.indexOf("{"));

                response=new String(response.getBytes("ISO-8859-1"), "UTF-8");
                Log.e("presenter/users/res ", "" + response);

                JSONObject object = new JSONObject(response);
                Log.e("presenter:obj1", "" + object.toString());
                JSONArray array = object.getJSONArray("USERS");
                Log.e("presenter:obj2", "" + array.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject innerObject = array.getJSONObject(i);
                    Log.e("presenter:obj3 ", "" + innerObject.toString());
                    Users users = new Users(innerObject.getString("USER_NAME"), innerObject.getString("PASSWORD"));
                    Log.e("presenter:obj4", "" + innerObject.getString("USER_NAME"));
                    SettingsFile.usersList.add(users);
                    databaseHandler.addNewUser(users);
                }
//                Log.e("presenter3: import ", "" + SettingsFile.serialNumber);

            } catch (JSONException e) {
                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    //------------------------------------------------------------------------------------------------

    public void getBundlesData(InventoryReport inventoryReport) {
        settings = databaseHandler.getSettings();
        this.inventoryReport = inventoryReport;

        urlBundles = "http://" + settings.getIpAddress() + "/import.php?FLAG=3";//http://5.189.130.98:8085/import.php?FLAG=3
//        Log.e("presenter/urlUsers ", "" + urlUsers);
//        Log.e("presenter:ipUsers ", "" + SettingsFile.ipAddress);
        bundlesJsonObjectRequest = new StringRequest(Request.Method.GET, urlBundles, new BundlesResponseClass(), new BundlesResponseClass());
        requestQueue.add(bundlesJsonObjectRequest);
    }

    class BundlesResponseClass implements Response.Listener<String>, Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("presenter/bundle/err ", "" + error);
        }

        @Override
        public void onResponse(String response) {
            try {
                bundleInfoServer.clear();
                bundleInfoServer2.clear();
//                if (response.indexOf("{") >= 0)
//                    response = response.substring(response.indexOf("{"));
                response=new String(response.getBytes("ISO-8859-1"), "UTF-8");
                Log.e("presenter/bundle/res ", "" + response);
                JSONObject object = new JSONObject(response);
//                Log.e("presenter:bun1", "" + object.toString());
                JSONArray array = object.getJSONArray("BUNDLE_INFO");
//                Log.e("presenter:bun2", "" + array.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject innerObject = array.getJSONObject(i);//ORDERED
                    Log.e("presenter:bun3 ", "" + innerObject.toString());
                    if (innerObject.getInt("ORDERED") == 0){
                    BundleInfo bundleInfo = new BundleInfo();
                    bundleInfo.setThickness(innerObject.getDouble("THICKNESS"));
                    bundleInfo.setWidth(innerObject.getDouble("WIDTH"));
                    bundleInfo.setLength(innerObject.getDouble("LENGTH"));
                    bundleInfo.setGrade(innerObject.getString("GRADE"));
                    bundleInfo.setNoOfPieces(innerObject.getInt("PIECES"));
                    bundleInfo.setBundleNo(innerObject.getString("BUNDLE_NO"));
                    bundleInfo.setLocation(innerObject.getString("LOCATION"));
                    bundleInfo.setArea(innerObject.getString("AREA"));
                    bundleInfo.setBarcode(innerObject.getString("BARCODE"));
                    bundleInfo.setOrdered(innerObject.getInt("ORDERED"));
//                    bundleInfo.setPicture(innerObject.getString("PIC"));
                    bundleInfo.setAddingDate(innerObject.getString("BUNDLE_DATE"));

                    bundleInfoServer2.add(bundleInfo);
                    bundleInfoServer.add(bundleInfo);
                    }
                }
                Log.e("follow", "" + bundleInfoServer.size());
                inventoryReport.filters();

//                inventoryReport.fillTable(bundleInfoServer);
////                Log.e("presenter3: import ", "" + SettingsFile.serialNumber);
//
            } catch (JSONException e) {
                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }
}
