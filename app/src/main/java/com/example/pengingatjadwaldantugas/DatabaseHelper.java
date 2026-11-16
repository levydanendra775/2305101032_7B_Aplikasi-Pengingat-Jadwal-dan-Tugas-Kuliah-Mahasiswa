package com.example.pengingatjadwaldantugas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "agenda.db";
    private static final int DB_VERSION = 2;
    private static final String TABEL = "agenda";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABEL + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "judul TEXT NOT NULL, "
                + "catatan TEXT, "
                + "jenis TEXT NOT NULL, "
                + "waktu INTEGER NOT NULL, "
                + "pengingat INTEGER NOT NULL DEFAULT -1, "
                + "selesai INTEGER NOT NULL DEFAULT 0, "
                + "nada_alarm_uri TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {

        if (oldV < 2) {
            db.execSQL("ALTER TABLE " + TABEL
                    + " ADD COLUMN nada_alarm_uri TEXT");
        }
    }

    private ContentValues nilai(Agenda a) {
        ContentValues cv = new ContentValues();

        cv.put("judul", a.getJudul());
        cv.put("catatan", a.getCatatan());
        cv.put("jenis", a.getJenis());
        cv.put("waktu", a.getWaktu());
        cv.put("pengingat", a.getPengingat());
        cv.put("selesai", a.isSelesai() ? 1 : 0);
        cv.put("nada_alarm_uri", a.getNadaAlarmUri());

        return cv;
    }

    public long tambah(Agenda a) {
        return getWritableDatabase().insert(
                TABEL, null, nilai(a));
    }

    public int ubah(Agenda a) {
        return getWritableDatabase().update(
                TABEL,
                nilai(a),
                "id=?",
                new String[]{String.valueOf(a.getId())});
    }

    public int hapus(int id) {
        return getWritableDatabase().delete(
                TABEL,
                "id=?",
                new String[]{String.valueOf(id)});
    }

    public void setSelesai(int id, boolean selesai) {
        ContentValues cv = new ContentValues();
        cv.put("selesai", selesai ? 1 : 0);

        getWritableDatabase().update(
                TABEL,
                cv,
                "id=?",
                new String[]{String.valueOf(id)});
    }

    public Agenda getById(int id) {
        Cursor c = getReadableDatabase().query(
                TABEL,
                null,
                "id=?",
                new String[]{String.valueOf(id)},
                null, null, null);

        try {
            return c.moveToFirst() ? dariCursor(c) : null;
        } finally {
            c.close();
        }
    }

    /**
     * jenis = null untuk semua.
     * Agenda belum selesai tampil lebih dahulu.
     */
    public List<Agenda> getAll(String jenis) {
        List<Agenda> hasil = new ArrayList<>();

        Cursor c = getReadableDatabase().query(
                TABEL,
                null,
                jenis == null ? null : "jenis=?",
                jenis == null ? null : new String[]{jenis},
                null,
                null,
                "selesai ASC, waktu ASC");

        try {
            while (c.moveToNext()) {
                hasil.add(dariCursor(c));
            }
        } finally {
            c.close();
        }

        return hasil;
    }

    /**
     * Agenda belum selesai yang memiliki pengingat.
     */
    public List<Agenda> getUntukPengingat() {
        List<Agenda> hasil = new ArrayList<>();

        Cursor c = getReadableDatabase().query(
                TABEL,
                null,
                "selesai=0 AND pengingat>=0",
                null,
                null,
                null,
                null);

        try {
            while (c.moveToNext()) {
                hasil.add(dariCursor(c));
            }
        } finally {
            c.close();
        }

        return hasil;
    }

    private Agenda dariCursor(Cursor c) {
        return new Agenda(
                c.getInt(c.getColumnIndexOrThrow("id")),
                c.getString(c.getColumnIndexOrThrow("judul")),
                c.getString(c.getColumnIndexOrThrow("catatan")),
                c.getString(c.getColumnIndexOrThrow("jenis")),
                c.getLong(c.getColumnIndexOrThrow("waktu")),
                c.getInt(c.getColumnIndexOrThrow("pengingat")),
                c.getInt(c.getColumnIndexOrThrow("selesai")) == 1,
                c.getString(c.getColumnIndexOrThrow("nada_alarm_uri"))
        );
    }
}
