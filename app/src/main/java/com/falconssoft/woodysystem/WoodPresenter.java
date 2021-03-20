package com.falconssoft.woodysystem;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.models.Users;
import com.falconssoft.woodysystem.reports.AcceptanceReport;
import com.falconssoft.woodysystem.reports.BundlesReport;
import com.falconssoft.woodysystem.reports.InventoryReport;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static com.falconssoft.woodysystem.reports.InventoryReport.bundleInfoServer;
import static com.falconssoft.woodysystem.reports.InventoryReport.bundleInfoServer2;

public class WoodPresenter {

    private Context context;
    private Settings settings;
    private RequestQueue requestQueue;
    private DatabaseHandler databaseHandler;
    private List<String> thickness = new ArrayList<>();
    private List<String> length = new ArrayList<>();
    private List<String> width = new ArrayList<>();

//    private StringRequest jsonObjectRequest;
//    private String urlImport;
//
//    private StringRequest usersJsonObjectRequest;
//    private String urlUsers;

//    private StringRequest bundlesJsonObjectRequest;
//    private StringRequest printBarcodeObjectRequest; // first report
//    private String urlBundles;

    private StringRequest stringRequest;
    //    private String urlPackingList;
//
//    private StringRequest truckReportJsonObjectRequest;
    private String url;

    private LoginActivity loginActivity;
    private InventoryReport inventoryReport;//= new InventoryReport();
    private BundlesReport bundlesReport;
    private AcceptanceReport truckReport;

    private static String serialNo;
    private static List<BundleInfo> bundleReportList = new ArrayList<>();
//    private String urlImport = "http://" + SettingsFile.ipAddress + "/import.php?FLAG=1";//http://5.189.130.98:8085/import.php?FLAG=1

//    private String urlUsers = "http://" + SettingsFile.ipAddress + "/import.php?FLAG=0";//http://10.0.0.214/WOODY/import.php?FLAG=0

    public WoodPresenter(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
        databaseHandler = new DatabaseHandler(context);
    }

    //------------------------------------------- Get Users ----------------------------------------

    void getUsersData(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
        settings = databaseHandler.getSettings();
        url = "http://" + settings.getIpAddress() + "/import.php?FLAG=0";//http://192.168.2.17:8088/woody/import.php?FLAG=0
        Log.e("presenter/urlUsers ", "" + url);
//        Log.e("presenter:ipUsers ", "" + SettingsFile.ipAddress);
        stringRequest = new StringRequest(Request.Method.GET, url, new UsersResponseClass(), new UsersResponseClass());
        requestQueue.add(stringRequest);
    }

    class UsersResponseClass implements Response.Listener<String>, Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
//            getUsersData();
//            Log.e("presenter/users/err ", "" + settings.getIpAddress() + " *** " + urlUsers + " *** " + error);
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
//
                if (response.indexOf("{") == 3) {
//                    response = response.substring(response.indexOf("{"));
                    response = new String(response.getBytes("ISO-8859-1"), "UTF-8");// cloud
                }
                Log.e("presenter/users/res ", "" + response);

                if (response.contains("the password will expire within 7 days"))
                    Toast.makeText(context, "the password will expire within 7 days!", Toast.LENGTH_SHORT).show();

                JSONObject object = new JSONObject(response.substring(response.indexOf("{")));

//                Log.e("presenter:obj1", "" + object.toString());
                JSONArray array = object.getJSONArray("USERS");
//                Log.e("presenter:obj2", "" + array.toString());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject innerObject = array.getJSONObject(i);
                    String username = innerObject.getString("USER_NAME");
                    String password = innerObject.getString("PASSWORD");
//                    Log.e("presenter:obj3 ", "" +username + password);
                    Users users = new Users(username, password);
                    Log.e("presenter:obj4", "" + innerObject.getString("USER_NAME"));

                    loginActivity.getUsersDataMethod(username, password);
                    SettingsFile.usersList.add(users);
                    databaseHandler.addNewUser(users);
                }
//                Log.e("presenter3: import ", "" + SettingsFile.serialNumber);
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

    //-------------------------------------------- Get Inventory Report Data-----------------------------------------/

    public void getBundlesData(InventoryReport inventoryReport) {
        settings = databaseHandler.getSettings();
        this.inventoryReport = inventoryReport;
        inventoryReport.showProgressDialog();

        url = "http://" + settings.getIpAddress() + "/import.php?FLAG=3";//http://5.189.130.98:8085/import.php?FLAG=3
//        Log.e("presenter/urlUsers ", "" + urlUsers);
//        Log.e("presenter:ipUsers ", "" + SettingsFile.ipAddress);
        stringRequest = new StringRequest(Request.Method.GET, url, new BundlesResponseClass(), new BundlesResponseClass());
        requestQueue.add(stringRequest);
    }

    class BundlesResponseClass implements Response.Listener<String>, Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("presenter/bundle/err ", "" + error);
            inventoryReport.hideProgressDialog();
        }

        @Override
        public void onResponse(String response) {
            try {
                bundleInfoServer.clear();
                bundleInfoServer2.clear();
                thickness.clear();
                length.clear();
                width.clear();

                if (response.indexOf("{") == 3)
                    response = new String(response.getBytes("ISO-8859-1"), "UTF-8");
//                    response = response.substring(response.indexOf("{"));

//                Log.e("presenter/bundle/res ", "" + response);
                JSONObject object = new JSONObject(response);
//                Log.e("presenter:bun1", "" + object.toString());
                JSONArray array = object.getJSONArray("BUNDLE_INFO");
                //Log.e("presenter:bun2", "" + array.length());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject innerObject = array.getJSONObject(i);//ORDERED
                    //Log.e("presenter:bun3 ", "" + innerObject.toString());
                    if (innerObject.getInt("ORDERED") == 0) {
                        BundleInfo bundleInfo = new BundleInfo();
                        bundleInfo.setThickness(innerObject.getDouble("THICKNESS"));
                        bundleInfo.setWidth(innerObject.getDouble("WIDTH"));
                        bundleInfo.setLength(innerObject.getDouble("LENGTH"));
                        bundleInfo.setGrade(innerObject.getString("GRADE"));
                        bundleInfo.setNoOfPieces(innerObject.getDouble("PIECES"));
                        bundleInfo.setBundleNo(innerObject.getString("BUNDLE_NO"));
                        bundleInfo.setLocation(innerObject.getString("LOCATION"));
                        bundleInfo.setArea(innerObject.getString("AREA"));
                        bundleInfo.setBarcode(innerObject.getString("BARCODE"));
                        bundleInfo.setOrdered(innerObject.getInt("ORDERED"));
                        bundleInfo.setDescription(innerObject.getString("DESCRIPTION"));
                        bundleInfo.setAddingDate(innerObject.getString("BUNDLE_DATE"));
                        bundleInfo.setSerialNo(innerObject.getString("B_SERIAL"));
                        bundleInfo.setBackingList(innerObject.getString("BACKING_LIST"));
                        bundleInfo.setCustomer(innerObject.getString("CUSTOMER"));

//                        Log.e("presenter:bun3 ", "" + bundleInfo.getBackingList());

                        bundleInfoServer2.add(bundleInfo);
                        bundleInfoServer.add(bundleInfo);
                        thickness.add(String.valueOf(innerObject.getDouble("THICKNESS")));
                        width.add(String.valueOf(innerObject.getDouble("WIDTH")));
                        length.add(String.valueOf(innerObject.getDouble("LENGTH")));

                    }
                }
//                Log.e("bundleInfoServer", "/size/" + bundleInfoServer.size());
                inventoryReport.filters();
                inventoryReport.fillSpinnerAdapter(thickness, width, length);
                showLog("Get Inventory Report Data", "thickness size", "" + thickness.size());

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

            inventoryReport.hideProgressDialog();
        }
    }

    //-------------------------------------------- Get Print Barcode Data------------------------------------------/

    public void getPrintBarcodeData(BundlesReport bundlesReport) {
        settings = databaseHandler.getSettings();
        this.bundlesReport = bundlesReport;

        url = "http://" + settings.getIpAddress() + "/import.php?FLAG=3";//http://5.189.130.98:8085/import.php?FLAG=3

        stringRequest = new StringRequest(Request.Method.GET, url, new PrintBarcodeResponseClass(), new PrintBarcodeResponseClass());
        requestQueue.add(stringRequest);
    }

    class PrintBarcodeResponseClass implements Response.Listener<String>, Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("presenter/printBarcode", "/err" + error);
        }

        @Override
        public void onResponse(String response) {
            try {
                bundleReportList.clear();

                if (response.indexOf("{") == 3)
                    response = new String(response.getBytes("ISO-8859-1"), "UTF-8");
//                    response = response.substring(response.indexOf("{"));

//                Log.e("presenter/printBarcode", "/res" + response);
                JSONObject object = new JSONObject(response);
//                Log.e("presenter:bun1", "" + object.toString());
                JSONArray array = object.getJSONArray("BUNDLE_INFO");
//                Log.e("presenter:bun2", "" + array.length());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject innerObject = array.getJSONObject(i);//ORDERED
//                    Log.e("presenter:bun3 ", "" + innerObject.toString());
                    if ((innerObject.getString("USER_NO").equals(settings.getUserNo()))
                            && (innerObject.getString("LOCATION").equals(settings.getStore()))
                            && (innerObject.getString("IS_PRINTED").equals("0"))) {
                        BundleInfo bundleInfo = new BundleInfo();
                        bundleInfo.setThickness(innerObject.getDouble("THICKNESS"));
                        bundleInfo.setWidth(innerObject.getDouble("WIDTH"));
                        bundleInfo.setLength(innerObject.getDouble("LENGTH"));
                        bundleInfo.setGrade(innerObject.getString("GRADE"));
                        bundleInfo.setNoOfPieces(innerObject.getDouble("PIECES"));
                        bundleInfo.setBundleNo(innerObject.getString("BUNDLE_NO"));
                        bundleInfo.setLocation(innerObject.getString("LOCATION"));
                        bundleInfo.setArea(innerObject.getString("AREA"));
                        bundleInfo.setBarcode(innerObject.getString("BARCODE"));
                        bundleInfo.setOrdered(innerObject.getInt("ORDERED"));
//                      bundleInfo.setPicture(innerObject.getString("PIC"));
                        bundleInfo.setAddingDate(innerObject.getString("BUNDLE_DATE"));
                        bundleInfo.setUserNo(innerObject.getString("USER_NO"));
                        bundleInfo.setSerialNo(innerObject.getString("B_SERIAL"));
                        bundleInfo.setBackingList(innerObject.getString("BACKING_LIST"));

                        bundleReportList.add(bundleInfo); // for first report
                    }
                }
                bundlesReport.fillTable();
                Log.e("size", "" + bundleReportList.size());
//                inventoryReport.filters();

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

    public List<BundleInfo> getBundleReportList() {
        return bundleReportList;
    }

    //-------------------------------------------- update p.list from inventory report Data------------------------------------------/

    public void updatePackingList(InventoryReport inventoryReport, String bundleNumber, String packingList,
                                  String location, double width, double length, double pieces, double thickness, String oldBackingList) {
        settings = databaseHandler.getSettings();
        this.inventoryReport = inventoryReport;
//export.php?ADD_PACKING_LIST=1&BUNDLE_NO='" + bundleNo + "'&PACKING_LIST='" + packingList + "'");
        url = "http://" + settings.getIpAddress() + "/export.php?ADD_PACKING_LIST=1&BUNDLE_NO=\"" + bundleNumber + "\""
                + "&PACKING_LIST=\"" + packingList + "\""
                + "&OLD_PACKING_LIST=\"" + oldBackingList + "\""
                + "&LOCATION=\"" + location + "\""
                + "&WIDTH=\"" + width + "\""
                + "&LENGTH=\"" + length + "\""
                + "&PIECES=\"" + pieces + "\""
                + "&THICKNESS=\"" + thickness + "\"";//http://5.189.130.98:8085/import.php?FLAG=3
//        Log.e("presenter/ ", "urlPackingList " + urlPackingList);

        stringRequest = new StringRequest(Request.Method.GET, url, new UpdatePackingListClass(), new UpdatePackingListClass());
        requestQueue.add(stringRequest);
    }

    class UpdatePackingListClass implements Response.Listener<String>, Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("presenter/packingList", "/err/" + error);
            if (error.toString().contains("com.android.volley.TimeoutError"))
                Toast.makeText(context, "Please check internet connection!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResponse(String response) {
            Log.e("presenter/packingList", "/res/" + response);
            try {
                if (response.indexOf("{") == 3)
                    response = new String(response.getBytes("ISO-8859-1"), "UTF-8");
//                    response = response.substring(response.indexOf("{"));
                inventoryReport.updatedPackingList();
                getUpdatedData(inventoryReport);
//                Log.e("presenter/packingList", "/res/" + response);
//                JSONObject object = new JSONObject(response);
//                Log.e("presenter:bun1", "" + object.toString());
//                JSONArray array = object.getJSONArray("BUNDLE_INFO");
//                Log.e("presenter:bun2", "" + array.length());
//                inventoryReport.filters();

//                inventoryReport.fillTable(bundleInfoServer);
////                Log.e("presenter3: import ", "" + SettingsFile.serialNumber);
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    //-------------------------------------------- Get Data After Update P List-----------------------------------------/

    public void getUpdatedData(InventoryReport inventoryReport) {
        settings = databaseHandler.getSettings();
        this.inventoryReport = inventoryReport;
        inventoryReport.showProgressDialog();

        url = "http://" + settings.getIpAddress() + "/import.php?FLAG=3";//http://5.189.130.98:8085/import.php?FLAG=3
//        Log.e("presenter/urlUsers ", "" + urlUsers);
//        Log.e("presenter:ipUsers ", "" + SettingsFile.ipAddress);
        stringRequest = new StringRequest(Request.Method.GET, url, new UpdatedBundlesResponseClass(), new UpdatedBundlesResponseClass());
        requestQueue.add(stringRequest);
    }

    class UpdatedBundlesResponseClass implements Response.Listener<String>, Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("presenter/update/err ", "pl" + error);
            inventoryReport.hideProgressDialog();
        }

        @Override
        public void onResponse(String response) {
            try {
                bundleInfoServer.clear();
                bundleInfoServer2.clear();
//                thickness.clear();
                if (response.indexOf("{") == 3)
                    response = new String(response.getBytes("ISO-8859-1"), "UTF-8");
//                    response = response.substring(response.indexOf("{"));

//                Log.e("presenter/bundle/res ", "" + response);
                JSONObject object = new JSONObject(response);
//                Log.e("presenter:bun1", "" + object.toString());
                JSONArray array = object.getJSONArray("BUNDLE_INFO");
                Log.e("presenter:bun2", "" + array.length());
                for (int i = 0; i < array.length(); i++) {
                    JSONObject innerObject = array.getJSONObject(i);//ORDERED
//                    Log.e("presenter:bun3 ", "" + innerObject.toString());
                    if (innerObject.getInt("ORDERED") == 0) {
                        BundleInfo bundleInfo = new BundleInfo();
                        bundleInfo.setThickness(innerObject.getDouble("THICKNESS"));
                        bundleInfo.setWidth(innerObject.getDouble("WIDTH"));
                        bundleInfo.setLength(innerObject.getDouble("LENGTH"));
                        bundleInfo.setGrade(innerObject.getString("GRADE"));
                        bundleInfo.setNoOfPieces(innerObject.getDouble("PIECES"));
                        bundleInfo.setBundleNo(innerObject.getString("BUNDLE_NO"));
                        bundleInfo.setLocation(innerObject.getString("LOCATION"));
                        bundleInfo.setArea(innerObject.getString("AREA"));
                        bundleInfo.setBarcode(innerObject.getString("BARCODE"));
                        bundleInfo.setOrdered(innerObject.getInt("ORDERED"));
//                    bundleInfo.setPicture(innerObject.getString("PIC"));
                        bundleInfo.setAddingDate(innerObject.getString("BUNDLE_DATE"));
                        bundleInfo.setSerialNo(innerObject.getString("B_SERIAL"));
                        bundleInfo.setBackingList(innerObject.getString("BACKING_LIST"));
                        //Log.e("presenter:bun3 ", "" + bundleInfo.getBackingList());

                        bundleInfoServer2.add(bundleInfo);
                        bundleInfoServer.add(bundleInfo);
//                        thickness.add(String.valueOf(innerObject.getDouble("THICKNESS")));
                    }
                }
//                Log.e("bundleInfoServer", "/size/" + bundleInfoServer.size());
                inventoryReport.filters();
//                inventoryReport.fillSpinnerAdapter(thickness);
                showLog("Get Inventory Report Data", "thickness size", "" + thickness.size());

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

            inventoryReport.hideProgressDialog();
        }
    }

    //-------------------------------------------- Get truck report Data -----------------------------------------/

    public void getTruckReportData(AcceptanceReport truckReport) {
        settings = databaseHandler.getSettings();
        this.truckReport = truckReport;

        url = "http://" + settings.getIpAddress() + "/import.php?FLAG=55";//http://5.189.130.98:8085/import.php?FLAG=55
//        Log.e("presenter/urlUsers ", "" + urlUsers);
//        Log.e("presenter:ipUsers ", "" + SettingsFile.ipAddress);
        stringRequest = new StringRequest(Request.Method.GET, url, new TruckReportClass(), new TruckReportClass());
        requestQueue.add(stringRequest);
    }

    class TruckReportClass implements Response.Listener<String>, Response.ErrorListener {

        @Override
        public void onErrorResponse(VolleyError error) {
            Log.e("presenter/truckReport ", "/err" + error);
            inventoryReport.hideProgressDialog();
        }

        @Override
        public void onResponse(String response) {
            try {
                if (response.indexOf("{") == 3)
                    response = new String(response.getBytes("ISO-8859-1"), "UTF-8");
                Log.e("presenter/truckReport ", response);

                if (response != null) {
                    Gson gson = new Gson();
                    NewRowInfo list = gson.fromJson(response, NewRowInfo.class);
                    truckReport.fillData(list);
                } else
                    Toast.makeText(context, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    //-------------------------------------------------------------------------------------/

    void showLog(String method, String key, String value) {
        Log.e("presenter", method + "/" + key + "/" + value);
    }

}
