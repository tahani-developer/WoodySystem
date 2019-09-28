package com.falconssoft.woodysystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.BundleInfo;

public class AddToInventory extends AppCompatActivity implements View.OnClickListener {

    private EditText thickness, length, width, grade, noOfPieces, bundleNo, location, area;
    private Button addToInventory;
    private TextView textView;
    private BundleInfo newBundle;
    private DatabaseHandler databaseHandler;
    private Animation animation;


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

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        addToInventory.startAnimation(animation);

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
                                        newBundle = new BundleInfo(Double.parseDouble(thickness.getText().toString())
                                                , Double.parseDouble(length.getText().toString())
                                                , Double.parseDouble(width.getText().toString())
                                                , grade.getText().toString()
                                                , Integer.parseInt(noOfPieces.getText().toString())
                                                , bundleNo.getText().toString()
                                                , location.getText().toString()
                                                , area.getText().toString());
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
}
