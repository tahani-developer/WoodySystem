package com.falconssoft.woodysystem;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.SystemClock;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.stage_one.AddNewRaw;
import com.falconssoft.woodysystem.stage_one.StageOne;
import com.falconssoft.woodysystem.stage_two.PlannedPackingList;
import com.falconssoft.woodysystem.stage_two.StageTwo;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout relativeLayout;
    private TextView stage1, stage2, stage3;
    private ScaleAnimation scale;
    private ImageView pictureOne, pictureTwo, pictureThree;
    private long mLastClickTime = 0, mLastClickTime3 = 0;
    private Dialog passwordDialog;

//    private Animation animation;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        stage1 = (TextView) findViewById(R.id.s1);
        stage2 = (TextView) findViewById(R.id.s2);
        stage3 = (TextView) findViewById(R.id.s3);
//        passwordDialog = new Dialog(this);

        callAnimation();

        stage1.setOnClickListener(this);
        stage2.setOnClickListener(this);
        stage3.setOnClickListener(this);

    }

    public void callAnimation() {
        scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.INFINITE, .8f, ScaleAnimation.RELATIVE_TO_SELF, .8f);
        scale.setStartOffset(300);
        scale.setDuration(700);
        scale.setInterpolator(new OvershootInterpolator());
        stage1.startAnimation(scale);

        scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        scale.setStartOffset(300);
        scale.setDuration(600);
        scale.setInterpolator(new OvershootInterpolator());
        stage2.startAnimation(scale);

        scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RESTART, .7f, ScaleAnimation.RELATIVE_TO_SELF, .7f);
        scale.setStartOffset(300);
        scale.setDuration(800);
        scale.setInterpolator(new OvershootInterpolator());
        stage3.startAnimation(scale);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.s1:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    break;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showPasswordDialog();
                break;
            case R.id.s2:
                if (SystemClock.elapsedRealtime() - mLastClickTime3 < 1000) {
                    break;
                }
                mLastClickTime3 = SystemClock.elapsedRealtime();
                Intent intent = new Intent(MainActivity.this , StageTwo.class);
                startActivity(intent);
                break;
            case R.id.s3:
                if (SystemClock.elapsedRealtime() - mLastClickTime3 < 1000) {
                    break;
                }
                mLastClickTime3 = SystemClock.elapsedRealtime();
                Intent intent2 = new Intent(MainActivity.this, Stage3.class);
                startActivity(intent2);
//                setSlideAnimation();
                break;
        }
    }

    void showPasswordDialog() {
        passwordDialog = new Dialog(this);
        passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        passwordDialog.setContentView(R.layout.password_dialog);

        TextInputEditText password = passwordDialog.findViewById(R.id.password_dialog_password);
        TextView done = passwordDialog.findViewById(R.id.password_dialog_done);

        done.setText(getResources().getString(R.string.done));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().equals("301190")) {
                    passwordDialog.dismiss();
                    Intent intent1 = new Intent(MainActivity.this, StageOne.class);
                    startActivity(intent1);
                } else
                    Toast.makeText(MainActivity.this, "Password is not correct!", Toast.LENGTH_SHORT).show();

            }
        });

        passwordDialog.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //    public void setSlideAnimation() {
//        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
//    }

//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(intent);
//        setSlideAnimation();
//        finish();
//    }

//    @Override
//    public void finish() {
//        super.finish();
//        setSlideAnimation();
//    }

}
