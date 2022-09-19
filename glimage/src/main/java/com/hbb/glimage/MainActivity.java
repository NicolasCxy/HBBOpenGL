package com.hbb.glimage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout rlBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rlBase = (RelativeLayout) findViewById(R.id.rlBase);

    }

    public void addGl(View view) {
        Toast.makeText(this, "添加", Toast.LENGTH_SHORT).show();
        MyGLImageView myGLImageView = new MyGLImageView(this);
        rlBase.addView(myGLImageView);

    }
}