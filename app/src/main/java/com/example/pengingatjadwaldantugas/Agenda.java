package com.example.pengingatjadwaldantugas;

public class Agenda {

    public static final String JADWAL = "Jadwal";
    public static final String TUGAS = "Tugas";

    // Menit sebelum waktu agenda; -1 = tanpa pengingat
    public static final int[] MENIT = {-1, 0, 5, 15, 30, 60, 1440};

    public static final String[] LABEL = {
            "Tanpa pengingat",
            "Tepat waktu",
            "5 menit sebelum",
            "15 menit sebelum",
            "30 menit sebelum",
            "1 jam sebelum",
            "1 hari sebelum"
    };

    private final int id;
    private final String judul;
    private final String catatan;
    private final String jenis;
    private final long waktu;
    private final int pengingat;
    private final boolean selesai;

    // URI nada alarm khusus agenda
    // null berarti menggunakan nada default
    private final String nadaAlarmUri;

    // Constructor lama agar kompatibel dengan kode sebelumnya
    public Agenda(int id, String judul, String catatan, String jenis,
                  long waktu, int pengingat, boolean selesai) {

        this(id, judul, catatan, jenis, waktu,
                pengingat, selesai, null);
    }

    // Constructor baru dengan nada alarm khusus
    public Agenda(int id, String judul, String catatan, String jenis,
                  long waktu, int pengingat, boolean selesai,
                  String nadaAlarmUri) {

        this.id = id;
        this.judul = judul;
        this.catatan = catatan;
        this.jenis = jenis;
        this.waktu = waktu;
        this.pengingat = pengingat;
        this.selesai = selesai;
        this.nadaAlarmUri = nadaAlarmUri;
    }

    public int getId() {
        return id;
    }

    public String getJudul() {
        return judul;
    }

    public String getCatatan() {
        return catatan;
    }

    public String getJenis() {
        return jenis;
    }

    public long getWaktu() {
        return waktu;
    }

    public int getPengingat() {
        return pengingat;
    }

    public boolean isSelesai() {
        return selesai;
    }

    public String getNadaAlarmUri() {
        return nadaAlarmUri;
    }

    public static int indeksPengingat(int menit) {
        for (int i = 0; i < MENIT.length; i++) {
            if (MENIT[i] == menit) return i;
        }
        return 0;
    }

    public static String labelPengingat(int menit) {
        if (menit < 0) return "Tanpa pengingat";

        return "Pengingat: " + LABEL[indeksPengingat(menit)];
    }
}
