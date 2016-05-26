package com.epustovit.myservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    private static final String TAG = "NotificationService";

    private String notification;
    private final IBinder binder = new ServiceBinder();
    private static long Minute = 60000;
    private int id = 1;
    private int interval = CommonConstants.DEFAULT_INTERVAL;
    private Timer timer;
    private TimerTask timerTask;

    public NotificationService() {
    }

    public class ServiceBinder extends Binder{
        NotificationService getService(){
            return NotificationService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        notification = getString(R.string.defaultNotification);
        Log.d(TAG, "onStartCommand() called");
        if (intent != null){
            String receivedNotification = intent.getStringExtra(CommonConstants.NOTIFICATION_TEXT);
            int receivedInterval = intent.getIntExtra(CommonConstants.NOTIFICATION_INTERVAL,CommonConstants.DEFAULT_INTERVAL);
            if (receivedNotification != null && receivedInterval != 0){
                notification = receivedNotification;
                interval = receivedInterval;
            }
        }
        scheduleNotification(notification, interval);

        return START_STICKY;
    }


    private void sendMessage(String text){
        Intent intent = new Intent(this, MainActivity.class);

        intent.putExtra(CommonConstants.SAVED_NOTIFICATION_TEXT, notification);
        intent.putExtra(CommonConstants.SAVED_INTERVAL, interval);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notif = new Notification.Builder(this)
                .setContentText(text)
                .setContentTitle(getString(R.string.Notification))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(id++, notif);

    }

    public void scheduleNotification(final String text, final  int receivedInterval){
        notification = text;
        interval = receivedInterval;
        if(timerTask != null){
            timerTask.cancel();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                sendMessage(text);
            }
        };

        timer.schedule(timerTask, interval * Minute, interval * Minute);
    }

    @Override
    public void onCreate(){
        super.onCreate();
        timer = new Timer();
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(timerTask != null){
            timerTask.cancel();
        }
        Log.d(TAG, "onDestroy() called");
    }





}
