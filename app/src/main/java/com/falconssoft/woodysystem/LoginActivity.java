package com.falconssoft.woodysystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.Users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout linearLayout;
    private EditText username, password;
    private Button login;
    private ImageView logoImage;
    private DatabaseHandler databaseHandler;
    private List<Users> usersList = new ArrayList<>();
    private final int IMAGE_CODE = 5;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHandler = new DatabaseHandler(this);
        username = findViewById(R.id.login_username);
        password = findViewById(R.id.login_password);
        logoImage = findViewById(R.id.login_logo);
        login = findViewById(R.id.login_login_btn);
        linearLayout = findViewById(R.id.login_linearLayout);

        login.setOnClickListener(this);
        logoImage.setOnClickListener(this);

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
            case R.id.login_login_btn:
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();

                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                setSlideAnimation();

//                if (!usernameText.equals("") || !usernameText.equals(null)){
//                    if (!passwordText.equals("") || !passwordText.equals(null)){
//                        usersList = databaseHandler.getUsers();
//                        for (int i = 0; i<usersList.size(); i++)
//                            if (usernameText.equals(usersList.get(i).getUsername())
//                                    && passwordText.equals(usersList.get(i).getPassword())){
//                                i = usersList.size();
//                                Intent intent = new Intent(this, MainActivity.class);
//                                startActivity(intent);
//                            }
//                    }
//                }

                break;
            case R.id.login_logo:
                Intent getImage = new Intent(Intent.ACTION_PICK);
                getImage.setType("image/*");
                startActivityForResult(getImage, IMAGE_CODE);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                logoImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSlideAnimation() {
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }


}
