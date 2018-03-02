package com.mohit.mohit.music;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.crashlytics.android.answers.Answers;
import io.fabric.sdk.android.Fabric;



public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers());

        Intent intent = new Intent(this, Permission.class);
        startActivity(intent);
        finish();
    }
}
