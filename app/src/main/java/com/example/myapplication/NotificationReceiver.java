package com.example.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationReceiver extends BroadcastReceiver {
    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();
        switch (action) {
            case "DISMISS":
                AlarmNotification.cancel(context);
                break;
            case "VIEW":
                long item_id = intent.getLongExtra("ItemID", 0);
                Log.d("notification", String.valueOf(item_id));

//                Intent intentView = new Intent(".EDIT_ITEM");
//                ItemDAO dao = new ItemDAO(context);
//                Item item = dao.get(itemID);
//                intentView.putExtra(".Item", item);
                break;
        }
    }
}
