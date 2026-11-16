package com.example.pengingatjadwaldantugas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            DatabaseHelper db = new DatabaseHelper(context);
            for (Agenda a : db.getUntukPengingat()) {
                AlarmScheduler.jadwalkan(context, a);
            }
        }
    }
}