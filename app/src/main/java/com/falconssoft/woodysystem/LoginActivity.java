package com.falconssoft.woodysystem;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.Orders;
import com.falconssoft.woodysystem.models.Pictures;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.models.Users;
import com.falconssoft.woodysystem.reports.LoadingOrderReport;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.falconssoft.woodysystem.SettingsFile.usersList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout linearLayout;
    private EditText username, password, ipAddress, companyName;
    private TextView aboutDevelopers, saveSettings,macAddress;
    private Button login;
    private ImageView  settings;//logoImage
    private DatabaseHandler databaseHandler;
    private final int IMAGE_CODE = 5;
    private Animation animation;
    private Spinner storesSpinner;
    private List<String> storesList = new ArrayList<>();
    private ArrayAdapter<String> storesAdapter;
    private WoodPresenter woodPresenter;
    private Settings generalSettings;
    private String localCompanyName, localIpAddress, localStore;
    private String globalUsername, globalPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHandler = new DatabaseHandler(this);
//        generalSettings = new Settings();
        generalSettings = new DatabaseHandler(this).getSettings();
//        generalSettings = databaseHandler.getSettings();


        localCompanyName = generalSettings.getCompanyName();
        localIpAddress = generalSettings.getIpAddress();
        localStore = generalSettings.getStore();

        //new JSONTask().execute();
        woodPresenter = new WoodPresenter(LoginActivity.this);
        woodPresenter.getUsersData(LoginActivity.this);

        Log.e("bool", "" + (!(localIpAddress == null)));
//        if (!(localIpAddress == null) && (!(localCompanyName == null))) {
//            if ((!localIpAddress.equals("")) && (!localCompanyName.toString().equals(""))) {
//                woodPresenter.getUsersData(LoginActivity.this);
//            } else {
//            }
//        } else {
//            Toast.makeText(this, "Please fill settings!", Toast.LENGTH_SHORT).show();
//        }
        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
//        logoImage = findViewById(R.id.login_logo);
        login = findViewById(R.id.login_login_btn);
        settings = findViewById(R.id.login_settings);
        linearLayout = findViewById(R.id.login_linearLayout);
        aboutDevelopers = findViewById(R.id.login_about_developers);

        login.setOnClickListener(this);
//        logoImage.setOnClickListener(this);
        settings.setOnClickListener(this);
        aboutDevelopers.setOnClickListener(this);

        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.move_to_right);
        login.startAnimation(animation);

        animation = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.bounce);
        linearLayout.startAnimation(animation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_about_developers:
                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.about_developers_dialog);

                TextView webTextView = dialog.findViewById(R.id.textView21);
                TextView phoneTextView = dialog.findViewById(R.id.textView22);
                TextView emailTextView = dialog.findViewById(R.id.textView23);
                TextView locationTextView = dialog.findViewById(R.id.textView25);

                Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_down);
                slide_down.setStartOffset(100);
                webTextView.startAnimation(slide_down);

                Animation slide_down1 = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_down);
                slide_down1.setStartOffset(200);
                phoneTextView.startAnimation(slide_down1);

                Animation slide_down2 = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_down);
                slide_down2.setStartOffset(300);
                emailTextView.startAnimation(slide_down2);

                Animation slide_down3 = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.slide_down);
                slide_down3.setStartOffset(400);
                locationTextView.startAnimation(slide_down3);

                dialog.show();
                break;
            case R.id.login_login_btn:
                generalSettings = databaseHandler.getSettings();
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();
                boolean found = false;
                localCompanyName = generalSettings.getCompanyName();
                localIpAddress = generalSettings.getIpAddress();
                localStore = generalSettings.getStore();

                if (usernameText.equals(globalUsername)//usersList.get(i).getUsername()
                        && passwordText.equals(globalPassword)) { //usersList.get(i).getPassword()
                    found = true;
//                    i = usersList.size();
                    databaseHandler.updateSettingsUserNo(usernameText);
//                                    databaseHandler.addSettingsUserNo(usernameText);
                    startService(new Intent(LoginActivity.this, MyServices.class));
                    Intent intent2 = new Intent(this, MainActivity.class);
                    startActivity(intent2);

                }

                if (!found) {
                    Toast.makeText(this, "Username or password is wrong or check settings! ", Toast.LENGTH_SHORT).show();}
//                Intent intent = new Intent(this, MainActivity.class);
//                startActivity(intent);
//                setSlideAnimation();
//                if (!(localIpAddress == null) && (!(localCompanyName == null))) {
//                    if ((!localIpAddress.equals("")) && (!localCompanyName.equals(""))) {
////                            usersList = databaseHandler.getUsers();
//                        if (usersList.size() > 0) {
//                            for (int i = 0; i < usersList.size(); i++)
//                                if (usernameText.equals(globalUsername)//usersList.get(i).getUsername()
//                                        && passwordText.equals(globalPassword)) { //usersList.get(i).getPassword()
//                                    found = true;
//                                    i = usersList.size();
//                                    databaseHandler.updateSettingsUserNo(usernameText);
////                                    databaseHandler.addSettingsUserNo(usernameText);
//                                    Intent intent2 = new Intent(this, MainActivity.class);
//                                    startActivity(intent2);
//                                }
//
//                            if (!found) {
//                                Toast.makeText(this, "Username or password is wrong or check settings! ", Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(this, "Please check internet connection!!!!", Toast.LENGTH_SHORT).show();
//                        }
//
//                    } else {
//                        Toast.makeText(this, "Please fill settings first!!!!", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    Toast.makeText(this, "Please fill settings first!!!!", Toast.LENGTH_SHORT).show();
//                }

                break;
            case R.id.login_logo:
                Intent getImage = new Intent(Intent.ACTION_PICK);
                getImage.setType("image/*");
                startActivityForResult(getImage, IMAGE_CODE);
                break;
            case R.id.login_settings:
                Settings settings = new Settings();
                generalSettings = databaseHandler.getSettings();
                Dialog settingDialog = new Dialog(this);
                settingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                settingDialog.setContentView(R.layout.settings_dialog_layout);
                companyName = settingDialog.findViewById(R.id.settings_company_name);
                ipAddress = settingDialog.findViewById(R.id.settings_ipAddress);
                storesSpinner = settingDialog.findViewById(R.id.settings_stores);
                saveSettings = settingDialog.findViewById(R.id.settings_save);
                macAddress= settingDialog.findViewById(R.id.macAddress);
                localCompanyName = generalSettings.getCompanyName();
                localIpAddress = generalSettings.getIpAddress();
                localStore = generalSettings.getStore();

                String mac=getMacAddr();
                macAddress.setText(""+mac);
//                Window window = settingDialog.getWindow();
//                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                storesList.clear();
                storesList.add("Amman");
                storesList.add("Kalinovka");
                storesList.add("Rudniya Store");
                storesList.add("Rudniya Sawmill");
                storesAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout_two, storesList);
                storesAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
                storesSpinner.setAdapter(storesAdapter);
//                if (!(localIpAddress == null) && (!(localCompanyName == null))) {
//                    if ((!localIpAddress.equals("")) && (!localCompanyName.equals(""))) {
                companyName.setText(localCompanyName);
                ipAddress.setText(localIpAddress);
                if (localStore == null) {
                    storesSpinner.setSelection(0);
                } else {
                    switch (localStore) {
                        case "Amman":
                            storesSpinner.setSelection(0);
                            break;
                        case "Kalinovka":
                            storesSpinner.setSelection(1);
                            break;
                        case "Rudniya Store":
                            storesSpinner.setSelection(2);
                            break;
                        case "Rudniya Sawmill":
                            storesSpinner.setSelection(3);
                            break;
                        default:
                            storesSpinner.setSelection(0);
                    }
                }

                storesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        settings.setStore(parent.getItemAtPosition(position).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
//                        settings.setStore("Amman");
                    }
                });

                saveSettings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!TextUtils.isEmpty(companyName.getText().toString())) {
                            if (!TextUtils.isEmpty(ipAddress.getText().toString())) {
                                settings.setCompanyName(companyName.getText().toString());
                                settings.setIpAddress(ipAddress.getText().toString());
                                databaseHandler.deleteSettings();
                                databaseHandler.addSettings(settings);
//                                woodPresenter.getUsersData(LoginActivity.this);
                                Toast.makeText(LoginActivity.this, "Saved Successfully", Toast.LENGTH_SHORT).show();

                                generalSettings = new DatabaseHandler(LoginActivity.this).getSettings();
//                                woodPresenter = new WoodPresenter(LoginActivity.this);
                                woodPresenter.getUsersData(LoginActivity.this);
//                                new JSONTask().execute();
                                settingDialog.dismiss();
                            } else {
                                ipAddress.setError("Required");
                            }
                        } else {
                            companyName.setError("Required");
                        }
                    }
                });
                settingDialog.show();
                break;
        }

    }

    public void getUsersDataMethod(String username, String password) {
        this.globalUsername = username;
        this.globalPassword = password;

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            Uri selectedImage = data.getData();
//            try {
//                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
//                logoImage.setImageBitmap(bitmap);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private class JSONTask extends AsyncTask<String, String, List<Orders>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<Orders> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {

                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=0");

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
//                Log.e("finalJson*********", finalJson);

                JSONObject parentObject = new JSONObject(finalJson);

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("USERS");

//                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject finalObject = parentArrayOrders.getJSONObject(0);

//                        Orders order = new Orders();
                    globalUsername = finalObject.getString("USER_NAME");
                    globalPassword = finalObject.getString("PASSWORD") ;
//                        orders.add(order);
//                    }
                } catch (JSONException e) {
//                    Log.e("Import Data2", e.getMessage().toString());
                }

            } catch (MalformedURLException e) {
//                Log.e("Customer", "********ex1");
                e.printStackTrace();
            } catch (IOException e) {
//                Log.e("Customer", e.getMessage().toString());
                e.printStackTrace();

            } catch (JSONException e) {
//                Log.e("Customer", "********ex3  " + e.toString());
                e.printStackTrace();
            } finally {
//                Log.e("Customer", "********finally");
                if (connection != null) {
//                    Log.e("Customer", "********ex4");
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
            return null;
        }


        @Override
        protected void onPostExecute(final List<Orders> result) {
            super.onPostExecute(result);

            if (result != null) {
//                Log.e("result", "*****************" + orders.size());
//                fillTable(orders);
//                storeInDatabase();
            } else {
//                Toast.makeText(LoginActivity.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }


//    public void setSlideAnimation() {
//        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
//    }



    @SuppressLint("LongLogTag")
    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                Log.e("LogIn","mac "+res1.toString());
                return res1.toString();
            }
        } catch (Exception ex) {

            Log.e("ExceptionMac",""+ex.toString());

        }
        return "02:00:00:00:00:00";
    }

}
