package com.falconssoft.woodysystem;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

public class WoodPresenter implements Response.ErrorListener, Response.Listener<String>{

    private Context context;
    private RequestQueue requestQueue;
    private StringRequest jsonObjectRequest;
    private String URLImport = "http://10.0.0.214/WOODY/import.php?FLAG=1";

    public WoodPresenter(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context);
    }

    void getImportData(){
        jsonObjectRequest = new StringRequest(Request.Method.GET, URLImport, this, this);
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Log.e("presenter/import/err ", "" + error);
        SettingsFile.serialNumber = "";
    }

    @Override
    public void onResponse(String response) {
        Log.e("presenter: import ", "" + response);
        try {
            JSONObject object = new JSONObject(response);
            JSONObject object2 = object.getJSONArray("Bundles").getJSONObject(0);
            SettingsFile.serialNumber = "";
            SettingsFile.serialNumber = object2.getString("MAX_SERIAL");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
