package com.example.pengingatjadwaldantugas;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements AgendaAdapter.Listener {

    private DatabaseHelper db;
    private AgendaAdapter adapter;

    private TabLayout tabFilter;

    private TextView tvBelum;
    private TextView tvTerlewat;
    private TextView tvTanggalHeader;
    private TextView tvJumlahAgenda;

    private LinearLayout layoutKosong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        mintaIzinNotifikasi();

        tabFilter = findViewById(R.id.tabFilter);

        tvBelum = findViewById(R.id.tvBelum);
        tvTerlewat = findViewById(R.id.tvTerlewat);

        tvTanggalHeader = findViewById(R.id.tvTanggalHeader);
        tvJumlahAgenda = findViewById(R.id.tvJumlahAgenda);

        layoutKosong = findViewById(R.id.layoutKosong);

        RecyclerView rv = findViewById(R.id.rvAgenda);

        adapter = new AgendaAdapter(this);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        tampilkanTanggal();

        tabFilter.addOnTabSelectedListener(
                new TabLayout.OnTabSelectedListener() {

                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        muatData();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {
                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {
                        muatData();
                    }
                }
        );

        findViewById(R.id.fabTambah).setOnClickListener(
                v -> {
                    Intent intent = new Intent(
                            MainActivity.this,
                            FormAgendaActivity.class
                    );

                    startActivity(intent);
                }
        );
    }

    private void tampilkanTanggal() {

        SimpleDateFormat format = new SimpleDateFormat(
                "EEEE, d MMMM yyyy",
                new Locale("id", "ID")
        );

        tvTanggalHeader.setText(
                format.format(new Date())
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        tampilkanTanggal();
        muatData();
    }

    private void mintaIzinNotifikasi() {

        if (Build.VERSION.SDK_INT >= 33
                && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.POST_NOTIFICATIONS
                    },
                    100
            );
        }
    }

    private void muatData() {

        String filter = null;

        int posisi = tabFilter.getSelectedTabPosition();

        if (posisi == 1) {
            filter = Agenda.JADWAL;
        } else if (posisi == 2) {
            filter = Agenda.TUGAS;
        }

        // Memuat data sesuai tab yang dipilih.
        List<Agenda> list = db.getAll(filter);

        adapter.setData(list);

        // Menampilkan empty state hanya jika daftar kosong.
        layoutKosong.setVisibility(
                list.isEmpty() ? View.VISIBLE : View.GONE
        );

        // Statistik seluruh agenda, tidak terpengaruh tab.
        List<Agenda> semuaAgenda = db.getAll(null);

        int belum = 0;
        int terlewat = 0;

        long sekarang = System.currentTimeMillis();

        for (Agenda agenda : semuaAgenda) {

            if (!agenda.isSelesai()) {

                belum++;

                if (agenda.getWaktu() < sekarang) {
                    terlewat++;
                }
            }
        }

        tvBelum.setText(String.valueOf(belum));

        tvTerlewat.setText(String.valueOf(terlewat));

        tvJumlahAgenda.setText(
                list.size() + (list.size() == 1
                        ? " agenda"
                        : " agenda")
        );
    }

    @Override
    public void onToggle(Agenda agenda, boolean selesai) {

        db.setSelesai(agenda.getId(), selesai);

        if (selesai) {

            AlarmScheduler.batalkan(
                    this,
                    agenda.getId()
            );

        } else {

            Agenda terbaru = db.getById(agenda.getId());

            if (terbaru != null) {
                AlarmScheduler.jadwalkan(this, terbaru);
            }
        }

        muatData();
    }

    @Override
    public void onEdit(Agenda agenda) {

        Intent intent = new Intent(
                this,
                FormAgendaActivity.class
        );

        intent.putExtra(
                FormAgendaActivity.EXTRA_ID,
                agenda.getId()
        );

        startActivity(intent);
    }

    @Override
    public void onHapus(Agenda agenda) {

        new AlertDialog.Builder(this)
                .setTitle("Hapus agenda")
                .setMessage(
                        "Apakah kamu yakin ingin menghapus \""
                                + agenda.getJudul() + "\"?"
                )
                .setPositiveButton("Hapus", (dialog, which) -> {

                    AlarmScheduler.batalkan(
                            this,
                            agenda.getId()
                    );

                    db.hapus(agenda.getId());

                    muatData();
                })
                .setNegativeButton("Batal", null)
                .show();
    }
}
