package com.example.pengingatjadwaldantugas;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String PREF_NAME = "pengaturan_alarm";
    private static final String KEY_NADA_DEFAULT = "nada_default_uri";

    // Versi baru agar Android membuat channel suara yang baru.
    public static final String CHANNEL_ID = "pengingat_agenda_v2";

    private static Uri ambilNadaDefault(Context context) {

        SharedPreferences pref = context.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);

        String uri = pref.getString(KEY_NADA_DEFAULT, null);

        if (uri != null && !uri.isEmpty()) {
            return Uri.parse(uri);
        }

        Uri nada = RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_ALARM);

        if (nada == null) {
            nada = RingtoneManager.getDefaultUri(
                    RingtoneManager.TYPE_NOTIFICATION);
        }

        return nada;
    }

    private static Uri tentukanNada(Context context, Intent intent) {

        String nadaKhusus = intent.getStringExtra("nada_alarm_uri");

        if (nadaKhusus != null && !nadaKhusus.isEmpty()) {
            return Uri.parse(nadaKhusus);
        }

        return ambilNadaDefault(context);
    }

    private static String buatChannel(Context context, Uri suara) {

        String identitas = suara != null
                ? suara.toString()
                : "default";

        String channelId = "alarm_v2_"
                + Integer.toHexString(identitas.hashCode());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationManager manager =
                    context.getSystemService(NotificationManager.class);

            if (manager != null
                    && manager.getNotificationChannel(channelId) == null) {

                NotificationChannel channel =
                        new NotificationChannel(
                                channelId,
                                "Nada Alarm JadwalKu",
                                NotificationManager.IMPORTANCE_HIGH
                        );

                channel.setDescription(
                        "Pengingat jadwal dan tugas JadwalKu");

                channel.enableVibration(true);

                AudioAttributes audioAttributes =
                        new AudioAttributes.Builder()
                                .setUsage(
                                        AudioAttributes.USAGE_NOTIFICATION)
                                .setContentType(
                                        AudioAttributes.CONTENT_TYPE_SONIFICATION)
                                .build();

                channel.setSound(suara, audioAttributes);

                manager.createNotificationChannel(channel);
            }
        }

        return channelId;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= 33
                && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        int id = intent.getIntExtra("id", 0);
        String judul = intent.getStringExtra("judul");
        String jenis = intent.getStringExtra("jenis");
        long waktu = intent.getLongExtra("waktu", 0);

        if (judul == null) {
            judul = "Pengingat JadwalKu";
        }

        if (jenis == null) {
            jenis = "Agenda";
        }

        // Mengambil suara sesuai agenda atau default.
        Uri suara = tentukanNada(context, intent);

        // Membuat channel baru sesuai URI suara.
        String channelId = buatChannel(context, suara);

        Intent buka = new Intent(context, MainActivity.class);

        buka.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
        );

        PendingIntent pi = PendingIntent.getActivity(
                context,
                id,
                buka,
                PendingIntent.FLAG_IMMUTABLE
                        | PendingIntent.FLAG_UPDATE_CURRENT
        );

        String waktuTeks = new SimpleDateFormat(
                "EEE, d MMM · HH:mm",
                new Locale("id", "ID")
        ).format(new Date(waktu));

        NotificationCompat.Builder b =
                new NotificationCompat.Builder(context, channelId)

                        .setSmallIcon(
                                android.R.drawable.ic_lock_idle_alarm)

                        .setContentTitle(judul)

                        .setContentText(jenis + " · " + waktuTeks)

                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH)

                        .setCategory(
                                NotificationCompat.CATEGORY_REMINDER)

                        .setAutoCancel(true)

                        .setContentIntent(pi);

        // Android sebelum versi 8.0.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

            if (suara != null) {
                b.setSound(suara);
            }

            b.setDefaults(NotificationCompat.DEFAULT_VIBRATE);
        }

        NotificationManagerCompat.from(context).notify(
                id, b.build());
    }
}
