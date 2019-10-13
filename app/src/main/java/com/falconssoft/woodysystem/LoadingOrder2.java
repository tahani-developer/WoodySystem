package com.falconssoft.woodysystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.Orders;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LoadingOrder2 extends AppCompatActivity {

    HorizontalListView listView;
    ListView listView2;
    private EditText placingNo, orderNo, containerNo, dateOfLoad, destination;
    private Button done;
    private TextView textView;
    private Orders order;
    private DatabaseHandler databaseHandler;
    private List<BundleInfo> bundles;
    private Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_order2);

        init();
        databaseHandler = new DatabaseHandler(this);

        ItemsListAdapter obj = new ItemsListAdapter();
        bundles = obj.getSelectedItems();

        ItemsListAdapter2 adapter = new ItemsListAdapter2(LoadingOrder2.this, bundles);
        listView.setAdapter(adapter);
        listView2.setAdapter(adapter);

        myCalendar = Calendar.getInstance();

        dateOfLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LoadingOrder2.this, openDatePickerDialog(0), myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(placingNo.getText().toString())) {
                    if (!TextUtils.isEmpty(orderNo.getText().toString())) {
                        if (!TextUtils.isEmpty(containerNo.getText().toString())) {
                            if (!TextUtils.isEmpty(dateOfLoad.getText().toString())) {
                                if (!TextUtils.isEmpty(destination.getText().toString())) {


                                    for (int i = 0; i < bundles.size(); i++) {
                                        order = new Orders(bundles.get(i).getThickness()
                                                , bundles.get(i).getWidth()
                                                , bundles.get(i).getLength()
                                                , bundles.get(i).getGrade()
                                                , bundles.get(i).getNoOfPieces()
                                                , bundles.get(i).getBundleNo()
                                                , bundles.get(i).getLocation()
                                                , bundles.get(i).getArea()
                                                , placingNo.getText().toString()
                                                , orderNo.getText().toString()
                                                , containerNo.getText().toString()
                                                , dateOfLoad.getText().toString()
                                                , destination.getText().toString());
                                        databaseHandler.addOrder(order);
                                    }
                                    Toast.makeText(LoadingOrder2.this, "Saved !", Toast.LENGTH_LONG).show();

                                    placingNo.setText("");
                                    orderNo.setText("");
                                    containerNo.setText("");
                                    dateOfLoad.setText("");
                                    destination.setText("");

                                } else {
                                    destination.setError("Required!");
                                }
                            } else {
                                dateOfLoad.setError("Required!");
                            }
                        } else {
                            containerNo.setError("Required!");
                        }
                    } else {
                        orderNo.setError("Required!");
                    }
                } else {
                    placingNo.setError("Required!");
                }

            }
        });

    }

    public DatePickerDialog.OnDateSetListener openDatePickerDialog(final int flag) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                dateOfLoad.setText(sdf.format(myCalendar.getTime()));
            }

        };
        return date;
    }

    void init() {

        listView = findViewById(R.id.listview);
        listView2 = findViewById(R.id.verticalListView);
        placingNo = findViewById(R.id.placing_no);
        orderNo = findViewById(R.id.order_no);
        containerNo = findViewById(R.id.container_no);
        dateOfLoad = findViewById(R.id.date_of_load);
        destination = findViewById(R.id.destination);
        done = findViewById(R.id.done);
        textView = findViewById(R.id.loading_order_textView);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_to_right);
        textView.startAnimation(animation);

    }
}
