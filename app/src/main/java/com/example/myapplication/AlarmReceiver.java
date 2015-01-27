package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: 弹出窗口或者通知
        String msg = intent.getStringExtra("AlarmMessage");
        Long id = intent.getLongExtra("ItemID", 0);
//        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        AlarmNotification.notify(context, id, msg, 1);
    }
}
