package com.epustovit.myservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private EditText editNotification;
    private EditText editInterval;
    private NotificationService notificationService;
    private ServiceConnection connection;
    boolean Bound = false;
    private Intent serviceIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        createServiceConnection();
        serviceIntent = new Intent(this, NotificationService.class);
        bindNotificationService();

        editNotification = (EditText)findViewById(R.id.messageText);
        editInterval = (EditText)findViewById(R.id.timeText);

        Intent intent = getIntent();
        String savedText = (intent.getStringExtra(CommonConstants.SAVED_NOTIFICATION_TEXT));
        int savedInterval = (intent.getIntExtra(CommonConstants.SAVED_INTERVAL, CommonConstants.DEFAULT_INTERVAL));
        if(savedText != null && savedInterval != 0){
            editNotification.setText(savedText);
            editInterval.setText(Integer.toString(savedInterval));
        }

    }

    private void bindNotificationService() {
        bindService(serviceIntent, connection, 0);
    }

    private void createServiceConnection() {

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                NotificationService.ServiceBinder binder = (NotificationService.ServiceBinder)service;
                notificationService = binder.getService();
                Bound = true;

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Bound = false;

            }
        };
    }

    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.startButton:
                Intent intent = new Intent(this, NotificationService.class);
                intent.putExtra(CommonConstants.NOTIFICATION_TEXT, editNotification.getText().toString());
                intent.putExtra(CommonConstants.NOTIFICATION_INTERVAL, Integer.parseInt(String.valueOf(editInterval.getText())));
                startService(intent);
                bindNotificationService();
                break;
            case R.id.stopButton:

                stopService(new Intent(this, NotificationService.class));
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (Bound){
            unbindService(connection);
        }
        Log.d(TAG, "onDestroy() called");
    }
}
