package com.spiderslab.pick.pickmobile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int LOCATION_ACCESS_CODE = 101;

    private Button startBtn = null;
    private boolean permissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initialize();
    }

    private void initialize() {
        checkPermissions();
        startBtn = (Button) findViewById(R.id.start_btn);
        startBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        tryToStartNextActivity();
    }

    private void tryToStartNextActivity() {
        if (isNetworkConnected()) {
            if (permissionGranted) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        } else {
            Toast.makeText(this, "Not connected to the network", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_ACCESS_CODE);
        } else {
            permissionGranted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (LOCATION_ACCESS_CODE != requestCode) {
            return;
        }
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGranted = true;
        } else {
            Toast.makeText(this, "Don't have location access", Toast.LENGTH_LONG).show();
            permissionGranted = false;
        }
    }
}
