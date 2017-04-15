package com.spiderslab.pick.pickmobile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.spiderslab.pick.pickmobile.notification.NotificationCenter;

public class MainActivity extends AppCompatActivity implements NotificationCenter.Listener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationCenter.INSTANCE.registerListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotificationCenter.INSTANCE.unregisterListener();
    }

    @Override
    public void onOrderReceived() {

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
}
