package com.example.pengingatjadwaldantugas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FormAgendaActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "id";

    private static final String PREF_NAME = "pengaturan_alarm";
    private static final String KEY_NADA_DEFAULT = "nada_default_uri";

    private static final String STATE_NADA_URI = "state_nada_uri";
    private static final String STATE_WAKTU = "state_waktu";
    private static final String STATE_PENGINGAT = "state_pengingat";

    private DatabaseHelper db;

    private EditText etJudul, etCatatan, etTanggal, etJam;
    private AutoCompleteTextView acPengingat;
    private RadioButton rbJadwal, rbTugas;
    private TextView tvNadaTerpilih;

    private final Calendar kal = Calendar.getInstance();

    private final SimpleDateFormat fmtTgl =
            new SimpleDateFormat(
                    "EEEE, d MMMM yyyy",
                    new Locale("id", "ID")
            );

    private final SimpleDateFormat fmtJam =
            new SimpleDateFormat("HH:mm", Locale.getDefault());

    private int idAgenda = -1;
    private int indeksPengingat = 3;

    // Null berarti menggunakan nada default.
    private String nadaAlarmUri = null;

    private Ringtone ringtonePreview;

    private ActivityResultLauncher<Intent> pemilihNada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_agenda);

        db = new DatabaseHelper(this);

        etJudul = findViewById(R.id.etJudul);
        etCatatan = findViewById(R.id.etCatatan);
        etTanggal = findViewById(R.id.etTanggal);
        etJam = findViewById(R.id.etJam);

        acPengingat = findViewById(R.id.acPengingat);

        rbJadwal = findViewById(R.id.rbJadwal);
        rbTugas = findViewById(R.id.rbTugas);

        TextView tvJudulForm = findViewById(R.id.tvJudulForm);
        tvNadaTerpilih = findViewById(R.id.tvNadaTerpilih);

        // =====================================
        // PEMILIH NADA ANDROID
        // =====================================

        pemilihNada = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null) {

                        Intent data = result.getData();
                        Uri uri = null;

                        if (Build.VERSION.SDK_INT >= 33) {
                            uri = data.getParcelableExtra(
                                    RingtoneManager.EXTRA_RINGTONE_PICKED_URI,
                                    Uri.class
                            );
                        } else {
                            uri = data.getParcelableExtra(
                                    RingtoneManager.EXTRA_RINGTONE_PICKED_URI
                            );
                        }

                        if (uri != null) {

                            nadaAlarmUri = uri.toString();

                            perbaruiTampilanNada();

                            Toast.makeText(
                                    this,
                                    "Nada khusus berhasil dipilih",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                }
        );

        findViewById(R.id.btnKembali)
                .setOnClickListener(v -> finish());

        // =====================================
        // PENGATURAN WAKTU PENGINGAT
        // =====================================

        ArrayAdapter<String> adapterPengingat =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        Agenda.LABEL
                );

        acPengingat.setAdapter(adapterPengingat);
        acPengingat.setThreshold(0);

        acPengingat.setOnClickListener(v ->
                acPengingat.showDropDown()
        );

        acPengingat.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                acPengingat.post(() ->
                        acPengingat.showDropDown()
                );
            }
        });

        acPengingat.setOnItemClickListener(
                (parent, view, pos, id) ->
                        indeksPengingat = pos
        );

        // =====================================
        // MEMUAT DATA AGENDA
        // =====================================

        idAgenda = getIntent().getIntExtra(EXTRA_ID, -1);

        Agenda lama = idAgenda != -1
                ? db.getById(idAgenda) : null;

        if (lama != null) {

            tvJudulForm.setText("Edit Agenda");

            etJudul.setText(lama.getJudul());
            etCatatan.setText(lama.getCatatan());

            kal.setTimeInMillis(lama.getWaktu());

            indeksPengingat =
                    Agenda.indeksPengingat(lama.getPengingat());

            nadaAlarmUri = lama.getNadaAlarmUri();

            if (Agenda.TUGAS.equals(lama.getJenis())) {
                rbTugas.setChecked(true);
            } else {
                rbJadwal.setChecked(true);
            }

        } else {

            idAgenda = -1;

            kal.add(Calendar.HOUR_OF_DAY, 1);
            kal.set(Calendar.MINUTE, 0);
            kal.set(Calendar.SECOND, 0);
            kal.set(Calendar.MILLISECOND, 0);
        }

        // Pulihkan data jika Activity dibuat ulang.
        if (savedInstanceState != null) {

            nadaAlarmUri = savedInstanceState.getString(
                    STATE_NADA_URI
            );

            kal.setTimeInMillis(
                    savedInstanceState.getLong(
                            STATE_WAKTU,
                            kal.getTimeInMillis()
                    )
            );

            indeksPengingat = savedInstanceState.getInt(
                    STATE_PENGINGAT,
                    indeksPengingat
            );
        }

        // Pastikan indeks pengingat valid.
        if (indeksPengingat < 0
                || indeksPengingat >= Agenda.LABEL.length) {
            indeksPengingat = 3;
        }

        acPengingat.setText(
                Agenda.LABEL[indeksPengingat],
                false
        );

        perbaruiTampilanNada();
        tampilkanWaktu();

        etTanggal.setOnClickListener(v -> pilihTanggal());
        etJam.setOnClickListener(v -> pilihJam());

        // =====================================
        // PENGATURAN NADA ALARM
        // =====================================

        findViewById(R.id.btnPilihNada)
                .setOnClickListener(v -> pilihNada());

        findViewById(R.id.btnPreviewNada)
                .setOnClickListener(v -> previewNada());

        findViewById(R.id.btnNadaDefault)
                .setOnClickListener(v -> {

                    nadaAlarmUri = null;

                    hentikanPreview();
                    perbaruiTampilanNada();

                    Toast.makeText(
                            this,
                            "Menggunakan nada default JadwalKu",
                            Toast.LENGTH_SHORT
                    ).show();
                });

        findViewById(R.id.btnSimpan)
                .setOnClickListener(v -> simpan());
    }

    // =====================================
    // MENYIMPAN STATE ACTIVITY
    // =====================================

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(
                STATE_NADA_URI,
                nadaAlarmUri
        );

        outState.putLong(
                STATE_WAKTU,
                kal.getTimeInMillis()
        );

        outState.putInt(
                STATE_PENGINGAT,
                indeksPengingat
        );
    }

    // =====================================
    // MEMILIH NADA
    // =====================================

    private void pilihNada() {

        Intent intent = new Intent(
                RingtoneManager.ACTION_RINGTONE_PICKER
        );

        intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_TYPE,
                RingtoneManager.TYPE_ALARM
        );

        intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_TITLE,
                "Pilih Nada Alarm"
        );

        intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT,
                false
        );

        intent.putExtra(
                RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT,
                false
        );

        if (nadaAlarmUri != null) {

            intent.putExtra(
                    RingtoneManager.EXTRA_RINGTONE_EXISTING_URI,
                    Uri.parse(nadaAlarmUri)
            );
        }

        hentikanPreview();

        pemilihNada.launch(intent);
    }

    // =====================================
    // MENAMPILKAN NAMA NADA
    // =====================================

    private void perbaruiTampilanNada() {

        if (nadaAlarmUri == null) {

            tvNadaTerpilih.setText(
                    "Menggunakan nada default JadwalKu"
            );

            return;
        }

        Uri uri = Uri.parse(nadaAlarmUri);

        String nama = cariNamaNada(uri);

        tvNadaTerpilih.setText(
                "Nada terpilih: " + nama
        );
    }

    // =====================================
    // MENCARI NAMA NADA
    // =====================================



    private String cariNamaNada(Uri uri) {

        if (uri == null) {
            return "Nada default";
        }

        Cursor cursor = null;
        String namaFile = null;

        try {

            cursor = getContentResolver().query(
                    uri,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null && cursor.moveToFirst()) {

                // Ambil nama file dari metadata OPPO.
                String[] kolomNama = {
                        "_display_name",
                        "display_name",
                        "title",
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.TITLE
                };

                for (String kolom : kolomNama) {

                    int index = cursor.getColumnIndex(kolom);

                    if (index >= 0) {

                        String nilai = cursor.getString(index);

                        if (nilai != null && !nilai.trim().isEmpty()) {

                            namaFile = nilai.trim();

                            android.util.Log.d(
                                    "JADWALKU_NADA",
                                    "Nama file ditemukan: " + namaFile
                            );

                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {

            android.util.Log.e(
                    "JADWALKU_NADA",
                    "Gagal membaca nama file nada",
                    e
            );

        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

        // Jika metadata tidak tersedia, ambil nama dari URI.
        if (namaFile == null) {
            namaFile = uri.getLastPathSegment();
        }

        if (namaFile == null) {
            return "Nama nada tidak tersedia";
        }

        // Hilangkan ekstensi file.
        int titik = namaFile.lastIndexOf(".");

        if (titik > 0) {
            namaFile = namaFile.substring(0, titik);
        }

        namaFile = namaFile.trim();

        // Pemetaan nama nada bawaan OPPO.
        switch (namaFile.toLowerCase(Locale.ROOT)) {

            case "ringtone_007":
                return "Kebahagiaan";

            default:
                // Nada lain tetap menggunakan nama file aslinya.
                return namaFile;
        }
    }

    // =====================================
    // MEMBERSIHKAN NAMA NADA
    // =====================================

    private String bersihkanNamaNada(String nama) {

        if (nama == null) {
            return null;
        }

        nama = nama.trim();

        if (nama.isEmpty()) {
            return null;
        }

        int titik = nama.lastIndexOf(".");

        if (titik > 0) {
            nama = nama.substring(0, titik);
        }

        return nama.trim();
    }

    // =====================================
    // VALIDASI NAMA NADA
    // =====================================


    private boolean validasiNamaNada(String nama) {

        if (nama == null || nama.trim().isEmpty()) {
            return false;
        }

        String nilai = nama.trim().toLowerCase(Locale.ROOT);

        // Menolak nama yang sebenarnya hanya ID internal.
        if (nilai.matches("ringtone[_-]?\\d+")
                || nilai.matches("\\d+")) {
            return false;
        }

        return true;
    }

    // =====================================
    // PREVIEW NADA
    // =====================================

    private Uri ambilUriPreview() {

        if (nadaAlarmUri != null) {
            return Uri.parse(nadaAlarmUri);
        }

        SharedPreferences pref =
                getSharedPreferences(
                        PREF_NAME,
                        MODE_PRIVATE
                );

        String uriDefault = pref.getString(
                KEY_NADA_DEFAULT,
                null
        );

        if (uriDefault != null && !uriDefault.isEmpty()) {
            return Uri.parse(uriDefault);
        }

        Uri uri = RingtoneManager.getDefaultUri(
                RingtoneManager.TYPE_ALARM
        );

        if (uri == null) {

            uri = RingtoneManager.getDefaultUri(
                    RingtoneManager.TYPE_NOTIFICATION
            );
        }

        return uri;
    }

    private void previewNada() {

        hentikanPreview();

        Uri uri = ambilUriPreview();

        if (uri == null) {

            Toast.makeText(
                    this,
                    "Nada dering tidak tersedia",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        ringtonePreview =
                RingtoneManager.getRingtone(this, uri);

        if (ringtonePreview != null) {

            ringtonePreview.play();

        } else {

            Toast.makeText(
                    this,
                    "Nada tidak dapat diputar",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void hentikanPreview() {

        if (ringtonePreview != null
                && ringtonePreview.isPlaying()) {

            ringtonePreview.stop();
        }

        ringtonePreview = null;
    }

    // =====================================
    // TANGGAL DAN JAM
    // =====================================

    private void tampilkanWaktu() {

        etTanggal.setText(
                fmtTgl.format(kal.getTime())
        );

        etJam.setText(
                fmtJam.format(kal.getTime())
        );
    }

    private void pilihTanggal() {

        new DatePickerDialog(
                this,
                (view, y, m, d) -> {

                    kal.set(Calendar.YEAR, y);
                    kal.set(Calendar.MONTH, m);
                    kal.set(Calendar.DAY_OF_MONTH, d);

                    tampilkanWaktu();

                },
                kal.get(Calendar.YEAR),
                kal.get(Calendar.MONTH),
                kal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void pilihJam() {

        new TimePickerDialog(
                this,
                (view, jam, menit) -> {

                    kal.set(Calendar.HOUR_OF_DAY, jam);
                    kal.set(Calendar.MINUTE, menit);
                    kal.set(Calendar.SECOND, 0);
                    kal.set(Calendar.MILLISECOND, 0);

                    tampilkanWaktu();

                },
                kal.get(Calendar.HOUR_OF_DAY),
                kal.get(Calendar.MINUTE),
                true
        ).show();
    }

    // =====================================
    // SIMPAN AGENDA
    // =====================================

    private void simpan() {

        String judul =
                etJudul.getText().toString().trim();

        if (judul.isEmpty()) {

            etJudul.setError("Judul wajib diisi");
            etJudul.requestFocus();

            return;
        }

        String catatan =
                etCatatan.getText().toString().trim();

        String jenis = rbTugas.isChecked()
                ? Agenda.TUGAS
                : Agenda.JADWAL;

        int menit = Agenda.MENIT[indeksPengingat];

        long waktu = kal.getTimeInMillis();

        Agenda a;

        if (idAgenda == -1) {

            Agenda sementara = new Agenda(
                    0,
                    judul,
                    catatan,
                    jenis,
                    waktu,
                    menit,
                    false,
                    nadaAlarmUri
            );

            long idBaru = db.tambah(sementara);

            if (idBaru == -1) {

                Toast.makeText(
                        this,
                        "Gagal menyimpan agenda",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            a = new Agenda(
                    (int) idBaru,
                    judul,
                    catatan,
                    jenis,
                    waktu,
                    menit,
                    false,
                    nadaAlarmUri
            );

        } else {

            Agenda lama = db.getById(idAgenda);

            boolean selesai =
                    lama != null && lama.isSelesai();

            a = new Agenda(
                    idAgenda,
                    judul,
                    catatan,
                    jenis,
                    waktu,
                    menit,
                    selesai,
                    nadaAlarmUri
            );

            db.ubah(a);
        }

        AlarmScheduler.jadwalkan(this, a);

        hentikanPreview();

        if (menit >= 0
                && waktu - menit * 60_000L
                <= System.currentTimeMillis()) {

            Toast.makeText(
                    this,
                    "Waktu pengingat sudah lewat, notifikasi tidak akan muncul",
                    Toast.LENGTH_LONG
            ).show();

        } else {

            Toast.makeText(
                    this,
                    "Agenda disimpan",
                    Toast.LENGTH_SHORT
            ).show();
        }

        finish();
    }

    // =====================================
    // MENUTUP PREVIEW SAAT ACTIVITY DITUTUP
    // =====================================

    @Override
    protected void onDestroy() {

        hentikanPreview();

        super.onDestroy();
    }
}
