package com.falconssoft.woodysystem.stage_two;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.ExportToPDF;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.SpinnerCustomAdapter;
import com.falconssoft.woodysystem.models.PlannedPL;
import com.falconssoft.woodysystem.models.SpinnerModel;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PlannedUnplanned extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    //Planned Unplanned Report
    private DatabaseHandler databaseHandler;
    private Calendar myCalendar;
    private boolean mState = false;
    private final String STATE_VISIBILITY = "state-visibility";

    private PlannedUnPlannedAdapter adapter2;

    private TextView thick, thicknessOrder, widthOrder, lengthOrder,piecesOrder, cubicOrder, noBundlesOrder,noBundles, totalCBM, export;
    private RecyclerView recycler;
    ;
    private TableLayout tableLayout, headerTableLayout;
    private TableRow tableRow;
    private List<PlannedPL> PLList = new ArrayList<>();
    private List<PlannedPL> plannedList = new ArrayList<>();
    private List<PlannedPL> unPlannedList = new ArrayList<>();
    private List<PlannedPL> PLListFiltered = new ArrayList<>();
    private List<SpinnerModel> thicknessChecked = new ArrayList<>();
    private List<SpinnerModel> lengthChecked = new ArrayList<>();
    private List<SpinnerModel> widthChecked = new ArrayList<>();

    private ArrayList<String> gradeList = new ArrayList<>();
    private ArrayAdapter<String> gradeAdapter;

    private ArrayList<String> typeList = new ArrayList<>();
    private ArrayAdapter<String> typeAdapter;
    private Spinner typeSpinner, thicknessSpinner, widthSpinner, lengthSpinner, gradeSpinner;
    private String typeText = "Planned", gradeText = "All", today;

    private List<Double> doubleList;

    private SimpleDateFormat sdf, dfReport;

    private List<String> thickness = new ArrayList<>();
    private List<String> length = new ArrayList<>();
    private List<String> width = new ArrayList<>();

    private SpinnerCustomAdapter thicknessAdapter;
    private SpinnerCustomAdapter widthAdapter;
    private SpinnerCustomAdapter lengthAdapter;

    private EditText fromLength, toLength, fromWidth, toWidth;

    private String gradeFeld = "All", thicknessField = "All", widthField = "All", lengthField = "All";
    private String fromLengthNo = "", toLengthNo = "", fromWidthNo = "", toWidthNo = "";

    private int sortFlag = 0;
    private boolean isThicknessAsc = true, isWidthAsc = true, isLengthAsc = true,isPiecesAsc=true,isCubicAsc=true ,isNoBundelAsc=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planned_unplanned_packing_list);

        init();

        databaseHandler = new DatabaseHandler(PlannedUnplanned.this);
        myCalendar = Calendar.getInstance();

        String myFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        today = sdf.format(myCalendar.getTime());
        dfReport = new SimpleDateFormat("yyyyMMdd_hhmmss");

        thicknessOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortFlag = 0;
                if (isThicknessAsc) {
                    isThicknessAsc = false;
                    thicknessOrder.setBackgroundResource(R.drawable.des);
                    Collections.sort(PLListFiltered, new SorterClass());
                } else { // des
                    isThicknessAsc = true;
                    thicknessOrder.setBackgroundResource(R.drawable.asc);
                    Collections.sort(PLListFiltered, Collections.reverseOrder(new SorterClass()));
                }
                adapter2.notifyDataSetChanged();
            }
        });

        widthOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortFlag = 1;
                if (isWidthAsc) {
                    isWidthAsc = false;
                    widthOrder.setBackgroundResource(R.drawable.des);
                    Collections.sort(PLListFiltered, new SorterClass());
                } else {
                    isWidthAsc = true;
                    widthOrder.setBackgroundResource(R.drawable.asc);
                    Collections.sort(PLListFiltered, Collections.reverseOrder(new SorterClass()));
                }
                adapter2.notifyDataSetChanged();
            }
        });

        lengthOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortFlag = 2;
                if (isLengthAsc) {
                    isLengthAsc = false;
                    lengthOrder.setBackgroundResource(R.drawable.des);
                    Collections.sort(PLListFiltered, new SorterClass());
                } else {
                    isLengthAsc = true;
                    lengthOrder.setBackgroundResource(R.drawable.asc);
                    Collections.sort(PLListFiltered, Collections.reverseOrder(new SorterClass()));
                }
                adapter2.notifyDataSetChanged();
            }
        });


        piecesOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortFlag = 3;
                if (isPiecesAsc) {
                    isPiecesAsc = false;
                    piecesOrder.setBackgroundResource(R.drawable.des);
                    Collections.sort(PLListFiltered, new SorterClass());
                } else {
                    isPiecesAsc = true;
                    piecesOrder.setBackgroundResource(R.drawable.asc);
                    Collections.sort(PLListFiltered, Collections.reverseOrder(new SorterClass()));
                }
                adapter2.notifyDataSetChanged();
            }
        });


        cubicOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortFlag = 4;
                if (isCubicAsc) {
                    isCubicAsc = false;
                    cubicOrder.setBackgroundResource(R.drawable.des);
                    Collections.sort(PLListFiltered, new SorterClass());
                } else {
                    isCubicAsc = true;
                    cubicOrder.setBackgroundResource(R.drawable.asc);
                    Collections.sort(PLListFiltered, Collections.reverseOrder(new SorterClass()));
                }
                adapter2.notifyDataSetChanged();
            }
        });

        noBundlesOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortFlag = 5;
                if (isNoBundelAsc) {
                    isNoBundelAsc = false;
                    noBundlesOrder.setBackgroundResource(R.drawable.des);
                    Collections.sort(PLListFiltered, new SorterClass());
                } else {
                    isNoBundelAsc = true;
                    noBundlesOrder.setBackgroundResource(R.drawable.asc);
                    Collections.sort(PLListFiltered, Collections.reverseOrder(new SorterClass()));
                }
                adapter2.notifyDataSetChanged();
            }
        });

        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExportToPDF obj = new ExportToPDF(PlannedUnplanned.this);
                obj.exportPlannedUnplanned(PLListFiltered, dfReport.format(Calendar.getInstance().getTime()), today,noBundles.getText().toString(),totalCBM.getText().toString());
            }
        });

        thicknessSpinner.setOnItemSelectedListener(this);
        widthSpinner.setOnItemSelectedListener(this);
        lengthSpinner.setOnItemSelectedListener(this);
        gradeSpinner.setOnItemSelectedListener(this);
        typeSpinner.setOnItemSelectedListener(this);

        new JSONTask2().execute();

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.grade:
                gradeText = parent.getItemAtPosition(position).toString();
                filters();
                break;
            case R.id.planned_unplanned:
                typeText = parent.getItemAtPosition(position).toString();

                PLList.clear();
                if (typeText.equals("Planned"))
                    PLList.addAll(plannedList);
                else
                    PLList.addAll(unPlannedList);

                filters();
                break;
            case R.id.inventory_report_thick_spinner:
                thicknessField = parent.getSelectedItem().toString();
                filters();
                break;
            case R.id.inventory_report_width_spinner:
                widthField = parent.getSelectedItem().toString();
                filters();
                break;
            case R.id.inventory_report_length_spinner:
                lengthField = parent.getSelectedItem().toString();
                filters();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

                case R.id.inventory_report_fromLength:
                    fromLengthNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    if (fromLengthNo.length() >= 3)
                        filters();
                    break;
                case R.id.inventory_report_toLength:
                    toLengthNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    if (toLengthNo.length() >= 3)
                        filters();
                    break;
                case R.id.inventory_report_fromWidth:
                    fromWidthNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    if (fromWidthNo.length() >= 2)
                        filters();
                    break;
                case R.id.inventory_report_toWidth:
                    toWidthNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    if (fromWidthNo.length() >= 2)
                        filters();
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public void filters() {

        PLListFiltered.clear();
        for (int i = 0; i < PLList.size(); i++) {
//
//            if (thicknessField.equals("All") || thicknessField.equals(String.valueOf(PLList.get(i).getThickness())))
//                if (widthField.equals("All") || widthField.equals(String.valueOf(PLList.get(i).getWidth())))
//                    if (lengthField.equals("All") || lengthField.equals(String.valueOf(PLList.get(i).getLength())))
//            if (PLList.get(i).getPackingList().startsWith(paclingList.getText().toString()) || paclingList.getText().toString().equals("")) {
//                if (PLList.get(i).getDestination().startsWith(dest.getText().toString()) || dest.getText().toString().equals("")) {
//                    if (PLList.get(i).getOrderNo().startsWith(orderNo.getText().toString()) || orderNo.getText().toString().equals("")) {
            if (fromLengthNo.equals("") || (PLList.get(i).getLength() >= Double.parseDouble(fromLengthNo)))
                if (toLengthNo.equals("") || (PLList.get(i).getLength() <= Double.parseDouble(toLengthNo)))
                    if (fromWidthNo.equals("") || (PLList.get(i).getWidth() >= Double.parseDouble(fromWidthNo)))
                        if (toWidthNo.equals("") || (PLList.get(i).getWidth() <= Double.parseDouble(toWidthNo)))
                            if ((thicknessChecked.size() == 0) || checkIfItemChecked(thicknessChecked, String.valueOf(PLList.get(i).getThickness())))
                                if ((widthChecked.size() == 0) || checkIfItemChecked(widthChecked, String.valueOf(PLList.get(i).getWidth())))
                                    if ((lengthChecked.size() == 0) || checkIfItemChecked(lengthChecked, String.valueOf(PLList.get(i).getLength())))
                                        if (gradeText.equals("All") || PLList.get(i).getGrade().startsWith(gradeText)) {

                                            PLListFiltered.add(PLList.get(i));
                                        }

        }

        adapter2 = new PlannedUnPlannedAdapter(this, PLListFiltered);
        recycler.setAdapter(adapter2);

        calculateTotal();
    }

    boolean checkIfItemChecked(List<SpinnerModel> list, String value) {
        for (int i = 0; i < list.size(); i++)
            if (value.equals(list.get(i).getTitle()))
                return true;

        return false;
    }

    public void sendOtherLists(List<SpinnerModel> list, int flag) {
        switch (flag) {
            case 1://thickness
                if (list != null || list.size() != 0) {
                    thicknessChecked = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++)
                        if (list.get(i).isChecked())
                            thicknessChecked.add(list.get(i));
                    Log.e("follow/", "size3/thicknessChecked/" + thicknessChecked.size());
                    filters();
                }
                break;
            case 2://width
                if (list != null || list.size() != 0) {
                    widthChecked = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++)
                        if (list.get(i).isChecked())
                            widthChecked.add(list.get(i));
                    Log.e("follow/", "size3/widthChecked/" + widthChecked.size());
                    filters();
                }
                break;
            case 3://length
                if (list != null || list.size() != 0) {
                    lengthChecked = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++)
                        if (list.get(i).isChecked())
                            lengthChecked.add(list.get(i));
                    Log.e("follow/", "size3/lengthChecked/" + lengthChecked.size());
                    filters();
                }
                break;
        }


    }

    @Override
    public void onClick(View v) {

    }

    class SorterClass implements Comparator<PlannedPL> {
        @Override
        public int compare(PlannedPL one, PlannedPL another) {
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

                case 3: // pieces
                    if (one.getNoOfPieces() < another.getNoOfPieces()) {
                        returnVal = -1;
                    } else if (one.getNoOfPieces() > another.getNoOfPieces()) {
                        returnVal = 1;
                    } else if (one.getNoOfPieces() == another.getNoOfPieces()) {
                        returnVal = 0;
                    }
                    break;

                case 4: // cubic
                    if (one.getCubic() < another.getCubic()) {
                        returnVal = -1;
                    } else if (one.getCubic() > another.getCubic()) {
                        returnVal = 1;
                    } else if (one.getCubic() == another.getCubic()) {
                        returnVal = 0;
                    }
                    break;

                case 5: // bundle
                    if (one.getNoOfCopies() < another.getNoOfCopies()) {
                        returnVal = -1;
                    } else if (one.getNoOfCopies() > another.getNoOfCopies()) {
                        returnVal = 1;
                    } else if (one.getNoOfCopies() == another.getNoOfCopies()) {
                        returnVal = 0;
                    }
                    break;

            }
            return returnVal;
        }

    }

    public void fillSpinnerAdapter(List<String> thicknessList, List<String> widthList, List<String> lengthList) {

        removeDuplicate(thicknessList);
        removeDuplicate(widthList);
        removeDuplicate(lengthList);

        List<SpinnerModel> thicknessCheckList = new ArrayList<>();
        List<SpinnerModel> widthCheckList = new ArrayList<>();
        List<SpinnerModel> lengthCheckList = new ArrayList<>();

        for (int i = 0; i < lengthList.size(); i++) {
            SpinnerModel spinnerModel = new SpinnerModel();
            spinnerModel.setTitle(lengthList.get(i));
            spinnerModel.setChecked(false);
            lengthCheckList.add(spinnerModel);
        }
        lengthCheckList.add(0, new SpinnerModel("       ", false));
        lengthAdapter = new SpinnerCustomAdapter(PlannedUnplanned.this, 0, lengthCheckList, 3, 2);
        lengthSpinner.setAdapter(lengthAdapter);
//        lengthList.add(0, "All");
//        lengthAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, lengthList);
//        lengthAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
//        lengthSpinner.setAdapter(lengthAdapter);

        for (int i = 0; i < widthList.size(); i++) {
            SpinnerModel spinnerModel = new SpinnerModel();
            spinnerModel.setTitle(widthList.get(i));
            spinnerModel.setChecked(false);
            widthCheckList.add(spinnerModel);
        }
        widthCheckList.add(0, new SpinnerModel("       ", false));
        widthAdapter = new SpinnerCustomAdapter(PlannedUnplanned.this, 0, widthCheckList, 2, 2);
        widthSpinner.setAdapter(widthAdapter);
//        widthList.add(0, "All");
//        widthAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, widthList);
//        widthAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
//        widthSpinner.setAdapter(widthAdapter);

        for (int i = 0; i < thicknessList.size(); i++) {
            SpinnerModel spinnerModel = new SpinnerModel();
            spinnerModel.setTitle(thicknessList.get(i));
            spinnerModel.setChecked(false);
            thicknessCheckList.add(spinnerModel);
        }
        thicknessCheckList.add(0, new SpinnerModel("       ", false));
        thicknessAdapter = new SpinnerCustomAdapter(PlannedUnplanned.this, 0, thicknessCheckList, 1, 2);
        thicknessSpinner.setAdapter(thicknessAdapter);
//        thicknessList.add(0, "All");
//        thicknessAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, thicknessList);
//        thicknessAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
//        thicknessSpinner.setAdapter(thicknessAdapter);


        gradeList.clear();
        gradeList.add("All");
        gradeList.add("Fresh");
        gradeList.add("KD");
        gradeList.add("BS");
        gradeList.add("Blue Stain");
        gradeList.add("KD Blue Stain");
        gradeList.add("AST");
        gradeList.add("AST Blue Stain");
        gradeList.add("Second Sort");
        gradeList.add("Reject");
        gradeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, gradeList);
        gradeAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        gradeSpinner.setAdapter(gradeAdapter);

        typeList.clear();
        typeList.add("Planned");
        typeList.add("Unplanned");
        typeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, typeList);
        typeAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        typeSpinner.setAdapter(typeAdapter);
    }

    void removeDuplicate(List<String> list) {
        Set<String> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
        doubleList = new ArrayList<>();
        for (int n = 0; n < list.size(); n++)
            doubleList.add(Double.valueOf(list.get(n)));

        Collections.sort(doubleList);
        list.clear();
        for (int n = 0; n < doubleList.size(); n++)
            list.add(String.valueOf(doubleList.get(n)));

    }

    // ************************************** SEARCH *******************************
    private class JSONTask2 extends AsyncTask<String, String, String> {  // check

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + databaseHandler.getSettings().getIpAddress() + "/import.php?FLAG=13"));

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                //nameValuePairs.add(new BasicNameValuePair("CUSTOMER", searchCustomer.getText().toString()));
                //nameValuePairs.add(new BasicNameValuePair("PACKING_LIST", paclingList.getText().toString()));

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

                JSONObject parentObject = new JSONObject(JsonResponse);

                plannedList.clear();
                unPlannedList.clear();
                PLList.clear();
                PLListFiltered.clear();

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("PLANNED");

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        PlannedPL planned = new PlannedPL();
                        planned.setGrade(innerObject.getString("GRADE"));
                        planned.setNoOfPieces(innerObject.getDouble("PIECES"));
                        planned.setLength(innerObject.getDouble("LENGTH"));
                        planned.setWidth(innerObject.getDouble("WIDTH"));
                        planned.setThickness(innerObject.getDouble("THICKNESS"));
                        planned.setNoOfCopies(innerObject.getInt("COUNT"));

                        float cub = Float.parseFloat(innerObject.getString("CUBIC"));
                        double c = cub * planned.getNoOfCopies() / 1000000000;
                        planned.setCubic(c);

                        Log.e("***1**", "" + cub + " * " + planned.getNoOfCopies() + " / " + "1000000000 = " + (cub * planned.getNoOfCopies() / 1000000000));

                        PLList.add(planned);
                        plannedList.add(planned);

                        thickness.add(String.valueOf(innerObject.getDouble("THICKNESS")));
                        width.add(String.valueOf(innerObject.getDouble("WIDTH")));
                        length.add(String.valueOf(innerObject.getDouble("LENGTH")));

                    }


                } catch (JSONException e) {
                    Log.e("Import Data2", e.getMessage().toString());
                }

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("UN_PLANNED");

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        PlannedPL planned = new PlannedPL();
                        planned.setGrade(innerObject.getString("GRADE"));
                        planned.setNoOfPieces(innerObject.getDouble("PIECES"));
                        planned.setLength(innerObject.getDouble("LENGTH"));
                        planned.setWidth(innerObject.getDouble("WIDTH"));
                        planned.setThickness(innerObject.getDouble("THICKNESS"));
                        planned.setNoOfCopies(innerObject.getInt("COUNT"));

                        float cub = Float.parseFloat(innerObject.getString("CUBIC"));
                        double c = cub * planned.getNoOfCopies() / 1000000000;
                        planned.setCubic(c);

                        Log.e("***1**", "" + cub + " * " + planned.getNoOfCopies() + " / " + "1000000000 = " + (cub * planned.getNoOfCopies() / 1000000000));

                        unPlannedList.add(planned);

                        thickness.add(String.valueOf(innerObject.getDouble("THICKNESS")));
                        width.add(String.valueOf(innerObject.getDouble("WIDTH")));
                        length.add(String.valueOf(innerObject.getDouble("LENGTH")));

                    }


                } catch (JSONException e) {
                    Log.e("Import Data2", e.getMessage().toString());
                }
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

                if (PLList.size() > 0) {

                    //PLList = clustering(PLList);
                    PLListFiltered.addAll(PLList);

                    adapter2.notifyDataSetChanged();

                    fillSpinnerAdapter(thickness, width, length);

                    calculateTotal();
                }
            } else {
                Toast.makeText(PlannedUnplanned.this, "No data found!", Toast.LENGTH_SHORT).show();
//                Toast.makeText(UnloadPackingList.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void calculateTotal() {
        int sumOfBundles = 0;
        double totalCbm = 0;
        for (int i = 0; i < PLListFiltered.size(); i++) {
            sumOfBundles += PLListFiltered.get(i).getNoOfCopies();
            //totalCbm += (PLListFiltered.get(i).getThickness() * PLListFiltered.get(i).getWidth() * PLListFiltered.get(i).getLength() * PLListFiltered.get(i).getNoOfPieces() * PLListFiltered.get(i).getNoOfCopies());
            totalCbm += PLListFiltered.get(i).getCubic();
        }

        noBundles.setText("" + sumOfBundles);
        totalCBM.setText("" + String.format("%.3f", (totalCbm)));
    }

    void init() {

        tableLayout = findViewById(R.id.addNewRaw_table);
        headerTableLayout = findViewById(R.id.addNewRaw_table_header);
        noBundles = findViewById(R.id.no_bundles);
        totalCBM = findViewById(R.id.total_cbm);
        gradeSpinner = findViewById(R.id.grade);
        typeSpinner = findViewById(R.id.planned_unplanned);
        thicknessSpinner = findViewById(R.id.inventory_report_thick_spinner);
        lengthSpinner = findViewById(R.id.inventory_report_length_spinner);
        widthSpinner = findViewById(R.id.inventory_report_width_spinner);

        thicknessOrder = findViewById(R.id.inventory_report_thick_order);
        widthOrder = findViewById(R.id.inventory_report_width_order);
        lengthOrder = findViewById(R.id.inventory_report_length_order);
        piecesOrder=findViewById(R.id.inventory_report_pieces_order);
        cubicOrder=findViewById(R.id.inventory_report_cubic_order);
        noBundlesOrder=findViewById(R.id.inventory_report_no_bundles_order);

        fromLength = findViewById(R.id.inventory_report_fromLength);
        toLength = findViewById(R.id.inventory_report_toLength);
        fromWidth = findViewById(R.id.inventory_report_fromWidth);
        toWidth = findViewById(R.id.inventory_report_toWidth);
        export = findViewById(R.id.export);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new PlannedUnPlannedAdapter(this, PLListFiltered);
        recycler.setAdapter(adapter2);


        fromLength.addTextChangedListener(new watchTextChange(fromLength));
        toLength.addTextChangedListener(new watchTextChange(toLength));
        fromWidth.addTextChangedListener(new watchTextChange(fromWidth));
        toWidth.addTextChangedListener(new watchTextChange(toWidth));

    }
}
