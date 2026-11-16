package com.example.pengingatjadwaldantugas;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmScheduler {

    public static void jadwalkan(Context ctx, Agenda a) {

        batalkan(ctx, a.getId());

        if (a.isSelesai() || a.getPengingat() < 0) return;

        long pemicu = a.getWaktu() - a.getPengingat() * 60_000L;

        if (pemicu <= System.currentTimeMillis()) return;

        Intent i = new Intent(ctx, AlarmReceiver.class);

        i.putExtra("id", a.getId());
        i.putExtra("judul", a.getJudul());
        i.putExtra("jenis", a.getJenis());
        i.putExtra("waktu", a.getWaktu());

        // Mengirim URI nada khusus agenda.
        // Jika null, AlarmReceiver menggunakan nada default.
        i.putExtra("nada_alarm_uri", a.getNadaAlarmUri());

        PendingIntent pi = PendingIntent.getBroadcast(
                ctx,
                a.getId(),
                i,
                PendingIntent.FLAG_UPDATE_CURRENT
                        | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager am = (AlarmManager)
                ctx.getSystemService(Context.ALARM_SERVICE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                    && !am.canScheduleExactAlarms()) {

                am.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        pemicu,
                        pi
                );

            } else {

                am.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        pemicu,
                        pi
                );
            }

        } catch (SecurityException e) {

            am.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    pemicu,
                    pi
            );
        }
    }

    public static void batalkan(Context ctx, int id) {

        Intent i = new Intent(ctx, AlarmReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(
                ctx,
                id,
                i,
                PendingIntent.FLAG_NO_CREATE
                        | PendingIntent.FLAG_IMMUTABLE
        );

        if (pi != null) {

            AlarmManager am = (AlarmManager)
                    ctx.getSystemService(Context.ALARM_SERVICE);

            am.cancel(pi);
            pi.cancel();
        }
    }
}
