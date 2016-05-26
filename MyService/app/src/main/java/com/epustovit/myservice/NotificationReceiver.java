package com.epustovit.myservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by User on 26.05.2016.
 */
public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent){
        context.startService(new Intent(context, NotificationService.class));
    }
}
