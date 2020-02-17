package com.falconssoft.woodysystem;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.Settings;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddToInventory extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;
    private Snackbar snackbar;
    private EditText thickness, length, width, noOfPieces, serialNo;
    private Spinner gradeSpinner, areaSpinner, descriptionSpinner, gradeSpinner2, areaSpinner2, descriptionSpinner2;//, locationSpinner
    private TableLayout bundlesTable, updatedTable;
    private LinearLayout linearLayoutView;
    private TextView textView, addToInventory;
    private BundleInfo newBundle;
    private ArrayList<BundleInfo> bundleInfoList;
    private DatabaseHandler databaseHandler;
    private Animation animation;
    private List<String> gradeList = new ArrayList<>();
    private List<String> areaList = new ArrayList<>();
    private List<String> descriptionList = new ArrayList<>();
    private ArrayAdapter<String> gradeAdapter, areaAdapter, descriptionaAdapter;//, locationAdapter
    private String gradeText = "Fresh", areaText = "Zone 1", descriptionText = "Ukrainian Wood", locationText;//, locationText = "Loc 1" ===> used for fill from adapter
    private JSONArray jsonArrayBundles;
    private boolean mState = false;
    private final String STATE_VISIBILITY = "state-visibility";
    private Settings generalSettings;
    private TableRow publicTableRow = null, tableRow1;
    private String locationString = null, gradeString = null, detailString = null, generateDate, bundleNumber, oldBundleNoString, newBundleNoString, bundleNoString, packingListText = "null";
    private Date date;
    private SimpleDateFormat simpleDateFormat;
    private Dialog dialog;
    private String takeThick, takeWidth, takeLength, takeNoOfPieces; // used in choose action dialog
    private RelativeLayout coordinatorLayout;
//    private List<TableRow> tableRowList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_inventory);

        databaseHandler = new DatabaseHandler(this);
//        presenter = new WoodPresenter(this);
        generalSettings = new Settings();
        generalSettings = databaseHandler.getSettings();
        bundleInfoList = new ArrayList<>();
        coordinatorLayout = findViewById(R.id.addToInventory_container);

        thickness = findViewById(R.id.addToInventory_thickness);
        length = findViewById(R.id.addToInventory_length);
        width = findViewById(R.id.addToInventory_width);
        noOfPieces = findViewById(R.id.addToInventory_no_of_pieces);
        gradeSpinner = findViewById(R.id.addToInventory_grade);
        serialNo = findViewById(R.id.addToInventory_serial_no);
//        locationSpinner = findViewById(R.id.addToInventory_location);
        areaSpinner = findViewById(R.id.addToInventory_area);
        descriptionSpinner = findViewById(R.id.addToInventory_description);
        addToInventory = findViewById(R.id.addToInventory_add_button);
//        newBundleButton = findViewById(R.id.addToInventory_new_button);
        textView = findViewById(R.id.addToInventory_textView);
        bundlesTable = findViewById(R.id.addToInventory_table);
        linearLayoutView = findViewById(R.id.addToInventory_table_view);
        linearLayoutView.setVisibility(View.GONE);

        date = Calendar.getInstance().getTime();
        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        generateDate = simpleDateFormat.format(date);

        addToInventory.setOnClickListener(this);

        fillSpinnerAdapter(gradeSpinner, areaSpinner, descriptionSpinner);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_to_right);
        textView.startAnimation(animation);

    }

    @Override
    public void onClick(View v) {
        String serialNoText = serialNo.getText().toString();
        String thicknessText = thickness.getText().toString();
        String lengthText = length.getText().toString();
        String widthText = width.getText().toString();
        String noOfPiecesText = noOfPieces.getText().toString();
        locationText = generalSettings.getStore();
//        int orderedText = 0;

        switch (v.getId()) {
            case R.id.addToInventory_add_button:
                if (!TextUtils.isEmpty(serialNoText)) {
                    if ((!TextUtils.isEmpty(thicknessText)) && (!checkValidData(thicknessText))) {
                        if ((!TextUtils.isEmpty(widthText)) && (!checkValidData(widthText))) {
                            if ((!TextUtils.isEmpty(lengthText)) && (!checkValidData(lengthText))) {
                                if ((!TextUtils.isEmpty(noOfPiecesText)) && (!checkValidData(noOfPiecesText))) {

                                    chooseSpinnersContent();

                                    thicknessText = formatDecimalValue(thicknessText);
                                    widthText = formatDecimalValue(widthText);
                                    lengthText = formatDecimalValue(lengthText);
                                    noOfPiecesText = formatDecimalValue(noOfPiecesText);

                                    thicknessText = isContainValueAfterDot(thicknessText);
                                    widthText = isContainValueAfterDot(widthText);
                                    lengthText = isContainValueAfterDot(lengthText);
                                    noOfPiecesText = isContainValueAfterDot(noOfPiecesText);

                                    bundleNoString = "" + gradeString
                                            + locationString
                                            + thicknessText
                                            + "." + widthText
                                            + "." + lengthText
                                            + "." + noOfPiecesText
                                            + "." + serialNoText;//presenter.getSerialNo();//SettingsFile.serialNumber

//                                    if (!foundBarcode) {
                                    jsonArrayBundles = new JSONArray();
                                    if (bundlesTable.getChildCount() == 0) {
                                        linearLayoutView.setVisibility(View.VISIBLE);
                                        mState = true;
                                        addTableHeader(bundlesTable);
                                    }

                                    newBundle = new BundleInfo(Double.parseDouble(thicknessText)
                                            , Double.parseDouble(lengthText)
                                            , Double.parseDouble(widthText)
                                            , gradeText
                                            , Double.parseDouble(noOfPiecesText)
                                            , bundleNoString
                                            , generalSettings.getStore()
                                            , areaText
                                            , generateDate
                                            , 0
                                            , descriptionText
                                            , serialNoText
                                            , generalSettings.getUserNo()
                                            , "null"
                                            , 0);//presenter.getSerialNo());//SettingsFile.serialNumber

                                    bundleInfoList.add(newBundle);
//                                    Log.e("date is", generateDate);

                                    TableRow tableRow = new TableRow(this);
                                    editTableRow(tableRow, bundleNoString, lengthText, widthText, thicknessText
                                            , noOfPiecesText, generalSettings.getStore());
//                                    tableRowList.add(tableRow);

                                    jsonArrayBundles.put(newBundle.getJSONObject());
                                    publicTableRow = tableRow;
//                                      bundlesTable.addView(tableRow);
                                    new JSONTask().execute();

                                    tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            publicTableRow = tableRow;
                                            showOperationDialog(tableRow);
                                            return false;
                                        }
                                    });
                                    serialNo.requestFocus();

                                } else {
                                    noOfPieces.setError("Required!");
                                }
                            } else {
                                length.setError("Required!");
                            }
                        } else {
                            width.setError("Required!");
                        }
                    } else {
                        thickness.setError("Required!");
                    }
                } else {
                    serialNo.setError("Required!");
                }
                break;
        }
    }

    String isContainValueAfterDot(String string) {
        String isConten = "";
        String afterDot = string.substring(string.indexOf(".") + 1, string.length());
        Log.e("afterDot", "" + afterDot + "      " + string);
        if (!(Integer.parseInt(afterDot) > 0)) {
            isConten = string.substring(0, string.indexOf("."));
        } else {
            isConten = string;
        }
        Log.e("afterDotreturn", "" + afterDot + "      " + isConten);

        return isConten;

    }

    boolean checkValidData(String word) {
        Log.e("checkValidData4", word + ((word.length() == 1)));
        Log.e("checkValidData4", word + ((word.contains("."))));
        Log.e("checkValidData4", word + ((word.length() == 1) && (word.equals("."))));
        if ((word.length() == 1) && (word.contains(".")))
            return true;
        return false;
    }

    String formatDecimalValue(String value) {
        String charOne = String.valueOf(value.charAt(0));
        String charEnd = String.valueOf(value.charAt(value.length() - 1));

        if (charOne.equals("."))
            return ("0" + value);
        else if (charEnd.equals(".")) {
            value = value.substring(0, value.length() - 1);
//            Log.e("checkValidData3", "" +value);
            return (value);
        }
        return value;
    }

    void addTableHeader(TableLayout tableLayout) {
        TableRow tableRow = new TableRow(this);
        for (int i = 0; i < 9; i++) {
            TextView textView = new TextView(this);
            textView.setBackgroundResource(R.color.orange);
            TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
//            textViewParam.setMargins(1, 5, 1, 1);
            textView.setTextSize(15);
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
            textView.setLayoutParams(textViewParam);
            switch (i) {
                case 0:
                    TableRow.LayoutParams param = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
//                    param.setMargins(1, 5, 1, 1);
                    textView.setLayoutParams(param);
                    textView.setText("Bundle#");
                    break;
                case 1:
                    textView.setText("Thic");
                    break;
                case 2:
                    textView.setText("Width");
                    break;
                case 3:
                    textView.setText("Length");
                    break;
                case 4:
                    textView.setText("Pieces");
                    break;
                case 5:
                    textView.setText("Grade");
                    break;
                case 6:
                    textView.setText("#Loc");
                    break;
                case 7:
                    textView.setText("Area");
                    break;
                case 8:
                    textView.setText("Details");
                    break;
            }
            tableRow.addView(textView);
        }
        tableLayout.addView(tableRow);
//        bundlesTable.addView(tableRow);
    }

    void showOperationDialog(TableRow tableRow) {

        dialog = new Dialog(AddToInventory.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choose_operation_dialog);

        oldBundleNoString = ((TextView) tableRow.getChildAt(0)).getText().toString(); ///////////////////
        String serialString = oldBundleNoString.substring(oldBundleNoString.lastIndexOf(".") + 1);
        String thicknessString = ((TextView) tableRow.getChildAt(1)).getText().toString();
        String widthString = ((TextView) tableRow.getChildAt(2)).getText().toString();
        String lengthString = ((TextView) tableRow.getChildAt(3)).getText().toString();
        String noOfPiecesString = ((TextView) tableRow.getChildAt(4)).getText().toString();
        String gradeString2 = ((TextView) tableRow.getChildAt(5)).getText().toString();
        String locationString2 = ((TextView) tableRow.getChildAt(6)).getText().toString();
        String areaString2 = ((TextView) tableRow.getChildAt(7)).getText().toString();
        String detailsString2 = ((TextView) tableRow.getChildAt(8)).getText().toString();

        RadioGroup radioGroup = dialog.findViewById(R.id.choose_operation_radioGroup);
        RadioButton delete = dialog.findViewById(R.id.choose_operation_delete_rb);
        LinearLayout deleteLinear = dialog.findViewById(R.id.choose_operation_delete_linear);
        TextView bundleNo = dialog.findViewById(R.id.choose_operation_delete_bundle_no);
        TextView deleteYes = dialog.findViewById(R.id.choose_operation_delete_yes);
        TextView deleteCancel = dialog.findViewById(R.id.choose_operation_delete_cancel);

        RadioButton edit = dialog.findViewById(R.id.choose_operation_edit_rb);
        LinearLayout editLinear = dialog.findViewById(R.id.choose_operation_edit_linear);
        tableRow1 = new TableRow(this);//dialog.findViewById(R.id.choose_operation_edit_tableRow);
        updatedTable = dialog.findViewById(R.id.choose_operation_edit_tableLayout);
        EditText thickness = dialog.findViewById(R.id.choose_operation_thickness);
        EditText length = dialog.findViewById(R.id.choose_operation_length);
        EditText width = dialog.findViewById(R.id.choose_operation_width);
        EditText noOfPieces = dialog.findViewById(R.id.choose_operation_no_of_pieces);
        TextView serialNo = dialog.findViewById(R.id.choose_operation_serial_no);
        gradeSpinner2 = dialog.findViewById(R.id.choose_operation_grade);
        areaSpinner2 = dialog.findViewById(R.id.choose_operation_area);
        descriptionSpinner2 = dialog.findViewById(R.id.choose_operation_description);
        TextView update = dialog.findViewById(R.id.choose_operation_update);
        TextView editSave = dialog.findViewById(R.id.choose_operation_edit_save);
        fillSpinnerAdapter(gradeSpinner2, areaSpinner2, descriptionSpinner2);

        deleteLinear.setVisibility(View.GONE);
        editLinear.setVisibility(View.GONE);

        setSpinnerSelectionPosition(gradeSpinner2, areaSpinner2, descriptionSpinner2, gradeString2, areaString2, detailsString2);
//        int index = gradeList.indexOf(gradeString2);
//        gradeText = gradeAdapter.getItem(index);
//        gradeSpinner.setSelection(index);
//
//        int index2 = areaList.indexOf(areaString2);
//        areaText = areaAdapter.getItem(index2);
//        areaSpinner.setSelection(index2);
//
//        int index3 = descriptionList.indexOf(detailsString2);
//        descriptionText = descriptionaAdapter.getItem(index3);
//        descriptionSpinner.setSelection(index3);

//     chooseSpinnersContent();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.choose_operation_delete_rb) {
                    deleteLinear.setVisibility(View.VISIBLE);
                    editLinear.setVisibility(View.GONE);
                    bundleNo.setText(oldBundleNoString);

                    deleteYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                                                                bundlesTable.removeView(tableRow);
                            publicTableRow = tableRow;
                            bundleNumber = oldBundleNoString;
                            new JSONTask2().execute();
                            dialog.dismiss();
                        }
                    });

                    deleteCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });


                } else if (checkedId == R.id.choose_operation_edit_rb) {
                    editLinear.setVisibility(View.VISIBLE);
                    deleteLinear.setVisibility(View.GONE);

                    serialNo.setText(serialString);
                    thickness.setText(thicknessString);
                    width.setText(widthString);
                    length.setText(lengthString);
                    noOfPieces.setText(noOfPiecesString);

                    update.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            takeThick = thickness.getText().toString();
                            takeWidth = width.getText().toString();
                            takeLength = length.getText().toString();
                            takeNoOfPieces = noOfPieces.getText().toString();

                            if ((!TextUtils.isEmpty(takeThick)) && (!checkValidData(takeThick))) {
                                if ((!TextUtils.isEmpty(takeWidth)) && (!checkValidData(takeWidth))) {
                                    if ((!TextUtils.isEmpty(takeLength)) && (!checkValidData(takeLength))) {
                                        if ((!TextUtils.isEmpty(takeNoOfPieces)) && (!checkValidData(takeNoOfPieces))) {

                                            updatedTable.removeAllViews();
                                            addTableHeader(updatedTable);
                                            String newBundleNoString;
                                            chooseSpinnersContent();

                                            takeThick = formatDecimalValue(takeThick);
                                            takeWidth = formatDecimalValue(takeWidth);
                                            takeLength = formatDecimalValue(takeLength);
                                            takeNoOfPieces = formatDecimalValue(takeNoOfPieces);

                                            takeThick = isContainValueAfterDot(takeThick);
                                            takeWidth = isContainValueAfterDot(takeWidth);
                                            takeLength = isContainValueAfterDot(takeLength);
                                            takeNoOfPieces = isContainValueAfterDot(takeNoOfPieces);

                                            newBundleNoString = "" + gradeString
                                                    + locationString
                                                    + takeThick
                                                    + "." + takeWidth
                                                    + "." + takeLength
                                                    + "." + takeNoOfPieces
                                                    + "." + serialString;

                                            tableRow1.removeAllViews();
                                            editTableRow(tableRow1, newBundleNoString, takeLength, takeWidth, takeThick
                                                    , takeNoOfPieces, locationString);
                                            updatedTable.addView(tableRow1);

                                        } else {
                                            noOfPieces.setError("Required!");
                                        }
                                    } else {
                                        length.setError("Required!");
                                    }
                                } else {
                                    width.setError("Required!");
                                }
                            } else {
                                thickness.setError("Required!");
                            }
                        }
                    });

                    editSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            takeThick = thickness.getText().toString();
                            takeWidth = width.getText().toString();
                            takeLength = length.getText().toString();
                            takeNoOfPieces = noOfPieces.getText().toString();

                            if ((!TextUtils.isEmpty(takeThick)) && (!checkValidData(takeThick))) {
                                if ((!TextUtils.isEmpty(takeWidth)) && (!checkValidData(takeWidth))) {
                                    if ((!TextUtils.isEmpty(takeLength)) && (!checkValidData(takeLength))) {
                                        if ((!TextUtils.isEmpty(takeNoOfPieces)) && (!checkValidData(takeNoOfPieces))) {

                                            updatedTable.removeAllViews();
                                            addTableHeader(updatedTable);
                                            newBundleNoString = "";
                                            chooseSpinnersContent();

                                            takeThick = formatDecimalValue(takeThick);
                                            takeWidth = formatDecimalValue(takeWidth);
                                            takeLength = formatDecimalValue(takeLength);
                                            takeNoOfPieces = formatDecimalValue(takeNoOfPieces);

                                            takeThick = isContainValueAfterDot(takeThick);
                                            takeWidth = isContainValueAfterDot(takeWidth);
                                            takeLength = isContainValueAfterDot(takeLength);
                                            takeNoOfPieces = isContainValueAfterDot(takeNoOfPieces);

                                            newBundleNoString = "" + gradeString
                                                    + locationString
                                                    + takeThick
                                                    + "." + takeWidth
                                                    + "." + takeLength
                                                    + "." + takeNoOfPieces
                                                    + "." + serialString;

                                            tableRow1.removeAllViews();
                                            editTableRow(tableRow1, newBundleNoString, takeLength, takeWidth, takeThick
                                                    , takeNoOfPieces, locationString);
                                            updatedTable.addView(tableRow1);

                                            newBundle = new BundleInfo(Double.parseDouble(takeThick)
                                                    , Double.parseDouble(takeLength)
                                                    , Double.parseDouble(takeWidth)
                                                    , gradeText
                                                    , Double.parseDouble(takeNoOfPieces)
                                                    , newBundleNoString
                                                    , generalSettings.getStore()
                                                    , areaText
                                                    , generateDate
                                                    , 0
                                                    , descriptionText
                                                    , serialString
                                                    , generalSettings.getUserNo()
                                                    , packingListText
                                                    , 0);//presenter.getSerialNo());//SettingsFile.serialNumber

//                                            publicTableRow = tableRow1;
                                            new JSONTask4().execute();
//                                            oldBundleNoString = newBundleNoString;
                                        } else {
                                            noOfPieces.setError("Required!");
                                        }
                                    } else {
                                        length.setError("Required!");
                                    }
                                } else {
                                    width.setError("Required!");
                                }
                            } else {
                                thickness.setError("Required!");
                            }
                        }
                    });
                }
            }
        });
        dialog.show();

    }

    void setSpinnerSelectionPosition(Spinner gradeSpinner, Spinner areaSpinner, Spinner descriptionSpinner, String grade, String area, String details) {
        int index = gradeList.indexOf(grade);
        gradeText = gradeAdapter.getItem(index);
        gradeSpinner.setSelection(index, true);

        int index2 = areaList.indexOf(area);
        areaText = areaAdapter.getItem(index2);
        areaSpinner.setSelection(index2, true);

        int index3 = descriptionList.indexOf(details);
        descriptionText = descriptionaAdapter.getItem(index3);
        descriptionSpinner.setSelection(index3, true);
    }

    void fillSpinnerAdapter(Spinner gradeSpinner, Spinner areaSpinner, Spinner descriptionSpinner) {
        gradeList.clear();
        areaList.clear();
        descriptionList.clear();

        gradeList.add("Fresh");
        gradeList.add("BS");
        gradeList.add("Reject");
        gradeList.add("KD");
        gradeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gradeList);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);
        gradeSpinner.setOnItemSelectedListener(this);

        areaList.add("Zone 1");
        areaList.add("Zone 2");
        areaList.add("Zone 3");
        areaList.add("Zone 4");
        areaList.add("Zone 5");
        areaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, areaList);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(areaAdapter);
        areaSpinner.setOnItemSelectedListener(this);

        descriptionList.add("Ukrainian Wood");
        descriptionList.add("Bundle Origin");
        descriptionList.add("Finland Wood");
        descriptionList.add("Decore Wood");
        descriptionList.add("German Wood");
        descriptionList.add("Romanian Wood");
        descriptionList.add("Russian Wood");
        descriptionList.add("Canadian Wood");
        descriptionList.add("Swedian Wood");
        descriptionList.add("Latvian Wood");
        descriptionaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, descriptionList);
        descriptionaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        descriptionSpinner.setAdapter(descriptionaAdapter);
        descriptionSpinner.setOnItemSelectedListener(this);
    }

    void chooseSpinnersContent() {
        switch (gradeText) {
            case "Fresh":
                gradeString = "FR";
                break;
            case "BS":
                gradeString = "BS";
                break;
            case "Reject":
                gradeString = "RJ";
                break;
            case "KD":
                gradeString = "KD";
                break;
        }

        switch (locationText) {//generalSettings.getStore()) {
            case "Amman":
                locationString = "Amm";
                break;
            case "Kalinovka":
                locationString = "Kal";
                break;
            case "Rudniya Store":
                locationString = "Rud";
                break;
            case "Rudniya Sawmill":
                locationString = "RLP";
                break;
        }

        switch (descriptionText) {
            case "Bundle Origin":
                detailString = "Origin";
                break;
            case "Finland Wood":
                detailString = "FIN";
                break;
            case "Decore Wood":
                detailString = "Decore";
                break;
            case "German Wood":
                detailString = "DEU";
                break;
            case "Romanian Wood":
                detailString = "ROU";
                break;
            case "Russian Wood":
                detailString = "RUS";
                break;
            case "Ukrainian Wood":
                detailString = "UKR";
                break;
            case "Canadian Wood":
                detailString = "CAN";
                break;
            case "Swedian Wood":
                detailString = "SWE";
                break;
            case "Latvian Wood":
                detailString = "LVA";
                break;
        }
    }

    void editTableRow(TableRow tableRow, String bundleNoString, String lengthText, String widthText, String thicknessText
            , String noOfPiecesText, String storeText) {
        for (int i = 0; i < 9; i++) {
            TextView textView = new TextView(this);
            textView.setBackgroundResource(R.color.light_orange);
            TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
            textViewParam.setMargins(1, 5, 1, 1);
            textView.setPadding(0, 10, 0, 10);
            textView.setTextSize(15);
            textView.setTextColor(ContextCompat.getColor(this, R.color.gray_dark_one));
            textView.setLayoutParams(textViewParam);
            switch (i) {
                case 0:
                    TableRow.LayoutParams param = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                    param.setMargins(1, 5, 1, 1);
                    textView.setPadding(0, 10, 0, 10);
                    textView.setLayoutParams(param);
                    textView.setText(bundleNoString);
                    break;
                case 1:
                    textView.setText(thicknessText);
                    break;
                case 2:
                    textView.setText(widthText);
                    break;
                case 3:
                    textView.setText(lengthText);
                    break;
                case 4:
                    textView.setText(noOfPiecesText);
                    break;
                case 5:
                    textView.setText(gradeText);
                    break;
                case 6:
                    textView.setText(storeText);
                    break;
                case 7:
                    textView.setText(areaText);
                    break;
                case 8:
                    textView.setText(descriptionText);
                    break;
            }
            tableRow.addView(textView);
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.addToInventory_grade:
            case R.id.choose_operation_grade:
                gradeText = parent.getItemAtPosition(position).toString();
                break;
            case R.id.addToInventory_area:
            case R.id.choose_operation_area:
                areaText = parent.getItemAtPosition(position).toString();
                break;
            case R.id.addToInventory_description:
            case R.id.choose_operation_description:
                descriptionText = parent.getItemAtPosition(position).toString();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
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
                nameValuePairs.add(new BasicNameValuePair("BUNDLE_INFO", jsonArrayBundles.toString().trim()));

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
            if (s != null) {
                if (s.contains("BUNDLE_INFO SUCCESS")) {
//                    databaseHandler.addNewBundle(newBundle);
//                    presenter.getImportData();
                    int i = bundlesTable.getChildCount();
                    publicTableRow.setTag("" + i);
                    Log.e("addToInventory", "" + publicTableRow + "   " + i + "      " + publicTableRow.getTag().toString());

                    bundlesTable.addView(publicTableRow);

                    thickness.setText("");
                    length.setText("");
                    width.setText("");
                    noOfPieces.setText("");
                    serialNo.setText("");
//                    areaSpinner.setSelection(0);
//                    gradeSpinner.setSelection(0);
//                    descriptionSpinner.setSelection(0);
                    gradeText = "Fresh";
                    areaText = "Zone 1";
                    descriptionText = "Ukrainian Wood";

                    setSpinnerSelectionPosition(gradeSpinner, areaSpinner, descriptionSpinner, gradeText, areaText, descriptionText);

                    Log.e("tag", "****Success");
                } else {
//                    presenter.setSerialNo("");
//                    SettingsFile.serialNumber = "";
                    Log.e("tag", "****Failed to export data");
                    Toast.makeText(AddToInventory.this, "Failed to export data Please check internet connection", Toast.LENGTH_LONG).show();
                }
            } else {
//                presenter.setSerialNo("");
//                SettingsFile.serialNumber = "";
                Log.e("tag", "****Failed to export data Please check internet connection");
                Toast.makeText(AddToInventory.this, "Failed to export data Please check internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class JSONTask2 extends AsyncTask<String, String, String> {

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
                nameValuePairs.add(new BasicNameValuePair("DELETE_BUNDLE", "1"));
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
            if (s != null) {
                if (s.contains("DELETE BUNDLE SUCCESS")) {
//                    tableRowList.remove(publicTableRow);
//                    fillBundlesTable();
                    bundlesTable.removeView(publicTableRow);
                    snackbar = Snackbar.make(coordinatorLayout, Html.fromHtml("<font color=\"#3167F0\">Deleted Successfully</font>"), Snackbar.LENGTH_SHORT);//Updated Successfully
                    View snackbarLayout = snackbar.getView();
                    TextView textViewSnackbar = (TextView) snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
                    textViewSnackbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);
//                    textView.setCompoundDrawablePadding(10);//getResources().getDimensionPixelOffset(R.dimen.snackbar_icon_padding
                    snackbar.show();
//                    databaseHandler.deleteBundle(bundleNumber);
                    Log.e("tag", "****Success");
                } else {
                    Log.e("tag", "****Failed to export data");
                }
            } else {
                Log.e("tag", "****Failed to export data Please check internet connection");
            }
        }
    }// used to export data

    private class JSONTask4 extends AsyncTask<String, String, String> { // used to update bundle info

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
                nameValuePairs.add(new BasicNameValuePair("UPDATE_BUNDLE", "1"));
                nameValuePairs.add(new BasicNameValuePair("BUNDLE_NO", oldBundleNoString)); // the old bundle number
                nameValuePairs.add(new BasicNameValuePair("OBJECT_INFO", newBundle.getJSONObject().toString())); // the updated bundle info

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
            if (s != null) {
                if (s.contains("UPDATE BUNDLE SUCCESS")) {
                    oldBundleNoString = newBundle.getBundleNo();
                    gradeText = "Fresh";
                    areaText = "Zone 1";
                    descriptionText = "Ukrainian Wood";
                    setSpinnerSelectionPosition(gradeSpinner2, areaSpinner2, descriptionSpinner2, gradeText, areaText, descriptionText);
                    setSpinnerSelectionPosition(gradeSpinner, areaSpinner, descriptionSpinner, gradeText, areaText, descriptionText);
                    dialog.dismiss();
                    snackbar = Snackbar.make(coordinatorLayout, Html.fromHtml("<font color=\"#3167F0\">Updated Successfully</font>"), Snackbar.LENGTH_SHORT);//Updated Successfully
                    View snackbarLayout = snackbar.getView();
                    TextView textViewSnackbar = (TextView) snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
                    textViewSnackbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);
//                    textView.setCompoundDrawablePadding(10);//getResources().getDimensionPixelOffset(R.dimen.snackbar_icon_padding
                    snackbar.show();
//                    Log.e("addNewToInventory", "" + "   " + "      " + publicTableRow.getTag().toString());
                    for (int i = 0; i < 9; i++) {
                        TextView textView = (TextView) publicTableRow.getChildAt(i);
                        switch (i) {
                            case 0:
                                textView.setText(newBundle.getBundleNo());
                                break;
                            case 1:
                                textView.setText(isContainValueAfterDot("" + newBundle.getThickness()));
                                break;
                            case 2:
                                textView.setText(isContainValueAfterDot("" + newBundle.getWidth()));
                                break;
                            case 3:
                                textView.setText(isContainValueAfterDot("" + newBundle.getLength()));
                                break;
                            case 4:
                                textView.setText(isContainValueAfterDot("" + newBundle.getNoOfPieces()));
                                break;
                            case 5:
                                textView.setText("" + newBundle.getGrade());
                                break;
                            case 6:
                                textView.setText("" + newBundle.getLocation());
                                break;
                            case 7:
                                textView.setText("" + newBundle.getArea());
                                break;
                            case 8:
                                textView.setText("" + newBundle.getDescription());
                                break;
                        }
                    }
                    areaSpinner.setSelection(0);
                    gradeSpinner.setSelection(0);
                    descriptionSpinner.setSelection(0);

                    Log.e("tag", "updated bundle raw/Success");
                } else {
                    Log.e("tag", "updated bundle raw/Failed to export data");
                }
            } else {
                Log.e("tag", "updated bundle raw/Failed to export data Please check internet connection");
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        linearLayoutView.getVisibility();
        outState.putBoolean(STATE_VISIBILITY, mState);

        List<TableRow> tableRows = new ArrayList<>();
        int rowcount = bundlesTable.getChildCount();
        for (int i = 0; i < rowcount; i++) {
            TableRow row = (TableRow) bundlesTable.getChildAt(i);
            tableRows.add(row);
        }
        outState.putSerializable("table", (Serializable) tableRows);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        // Restore state members from saved instance
        mState = savedInstanceState.getBoolean(STATE_VISIBILITY);
        linearLayoutView.setVisibility(mState ? View.VISIBLE : View.GONE);
//        presenter.getImportData();
        List<TableRow> tableRows = (List<TableRow>) savedInstanceState.getSerializable("table");
        for (int i = 0; i < tableRows.size(); i++) {
            if (tableRows.get(i).getParent() != null) {
                ((ViewGroup) tableRows.get(i).getParent()).removeView(tableRows.get(i)); // <- fix
            }
            bundlesTable.addView(tableRows.get(i));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        Intent intent = new Intent(AddToInventory.this , Stage3.class);
//        startActivity(intent);
        finish();
    }
}
