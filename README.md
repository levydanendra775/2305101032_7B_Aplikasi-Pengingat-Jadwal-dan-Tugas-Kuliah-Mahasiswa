# Pengumpulan UTS Pemrograman Bergerak

## Identitas Mahasiswa

| Keterangan | Identitas |
|---|---|
| Nama | Levy Danendra Fahriza Zani |
| NIM | 2305101032 |
| Kelas | 7B |
| Program Studi | Teknik Informatika |
| Mata Kuliah | Pemrograman Bergerak |
| Tahun Akademik | 2026/2027 |

## Deskripsi

Repository ini berisi source code, dokumentasi, dan hasil implementasi aplikasi mobile untuk pengerjaan Ujian Tengah Semester (UTS) mata kuliah Pemrograman Bergerak.

Aplikasi yang dikembangkan bernama **JadwalKu – Pengingat Jadwal dan Tugas**, yaitu aplikasi Android yang dirancang untuk membantu pengguna mengelola agenda, mencatat tugas, serta mengatur pengingat berdasarkan tanggal dan waktu yang ditentukan.

Aplikasi ini dikembangkan menggunakan Android Studio dengan bahasa pemrograman Java dan XML. Penyimpanan data menggunakan SQLite sehingga data agenda dapat dikelola secara lokal pada perangkat.

## Deskripsi Pengerjaan UTS

Pada pengerjaan UTS Pemrograman Bergerak ini, dikembangkan aplikasi Android bernama **JadwalKu**. Aplikasi ini memiliki fungsi utama untuk membantu pengguna mengatur jadwal kegiatan dan tugas agar lebih terorganisasi.

Pengguna dapat menambahkan agenda dengan menentukan judul, catatan, jenis agenda, tanggal, jam, dan waktu pengingat. Aplikasi juga menyediakan fitur notifikasi pengingat menggunakan AlarmManager sehingga pengguna memperoleh pemberitahuan sesuai waktu yang telah ditentukan.

Selain itu, aplikasi dilengkapi dengan pilihan nada alarm default maupun nada khusus untuk setiap agenda. Data agenda disimpan menggunakan database SQLite agar dapat diakses kembali ketika aplikasi dibuka.

Tampilan aplikasi dirancang menggunakan kombinasi warna navy, putih, dan aksen emas dengan antarmuka yang sederhana, modern, dan mudah digunakan.

## Teknologi yang Digunakan

| Teknologi | Keterangan |
|---|---|
| Bahasa Pemrograman | Java |
| Layout Antarmuka | XML |
| Framework | Android SDK |
| IDE | Android Studio |
| Database | SQLite |
| Komponen Antarmuka | Material Components, RecyclerView, dan CardView |
| Sistem Pengingat | AlarmManager |
| Notifikasi | NotificationManager dan NotificationChannel |
| Penyimpanan Pengaturan | SharedPreferences |
| Manajemen Versi | Git dan GitHub |

## Fitur yang Diimplementasikan

1. **Tambah Agenda**  
   Menambahkan agenda baru dengan informasi judul, catatan, jenis agenda, tanggal, dan waktu.

2. **Kategori Jadwal dan Tugas**  
   Memisahkan agenda berdasarkan jenisnya, yaitu Jadwal dan Tugas.

3. **Daftar Agenda**  
   Menampilkan agenda yang telah disimpan menggunakan RecyclerView.

4. **Filter Agenda**  
   Menampilkan seluruh agenda, agenda Jadwal, atau agenda Tugas.

5. **Edit Agenda**  
   Mengubah informasi agenda yang telah dibuat.

6. **Hapus Agenda**  
   Menghapus agenda yang tidak diperlukan.

7. **Status Penyelesaian**  
   Menandai agenda atau tugas yang telah selesai.

8. **Pengingat Agenda**  
   Mengatur pengingat sesuai waktu yang dipilih sebelum agenda dimulai.

9. **Notifikasi Pengingat**  
   Menampilkan notifikasi ketika waktu pengingat tiba.

10. **Pilihan Nada Alarm**  
    Menggunakan nada default atau memilih nada khusus untuk agenda tertentu.

11. **Preview Nada Alarm**  
    Mendengarkan nada alarm sebelum digunakan.

12. **Statistik Agenda**  
    Menampilkan jumlah agenda yang belum selesai dan agenda yang terlewat.

13. **Penyimpanan Offline**  
    Menyimpan data agenda secara lokal menggunakan SQLite.

## Struktur Folder

Struktur utama proyek Android Studio:

```text
2305101032_7B_Aplikasi-Pengingat-Jadwal-dan-Tugas/
│
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/example/pengingatjadwaldantugas/
│   │       │       ├── Agenda.java
│   │       │       ├── AgendaAdapter.java
│   │       │       ├── AlarmReceiver.java
│   │       │       ├── AlarmScheduler.java
│   │       │       ├── BootReceiver.java
│   │       │       ├── DatabaseHelper.java
│   │       │       ├── FormAgendaActivity.java
│   │       │       └── MainActivity.java
│   │       │
│   │       ├── res/
│   │       │   ├── drawable/
│   │       │   ├── layout/
│   │       │   ├── mipmap/
│   │       │   └── values/
│   │       │
│   │       └── AndroidManifest.xml
│   │
│   └── build.gradle
│
├── gradle/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── screenshots/
│   ├── halaman_utama.png
│   ├── tambah_agenda.png
│   ├── daftar_agenda.png
│   └── notifikasi.png
│
└── README.md
```

*Catatan: Folder dan nama screenshot di atas merupakan susunan dokumentasi yang perlu disesuaikan dengan file yang benar-benar tersedia di repository.*

## Persyaratan Menjalankan Aplikasi

Perangkat lunak dan kebutuhan yang diperlukan:

- Android Studio.
- Android SDK.
- JDK yang sesuai dengan konfigurasi Gradle proyek.
- Perangkat Android atau emulator.
- Sistem operasi Windows, Linux, atau macOS.
- Koneksi internet untuk proses awal sinkronisasi Gradle dan pengunduhan dependensi.

## Cara Menjalankan Aplikasi

1. Clone repository dari GitHub:

   ```bash
   git clone https://github.com/levydanendra775/2305101032_7B_Aplikasi-Pengingat-Jadwal-dan-Tugas-Kuliah-Mahasiswa.git
   ```

2. Buka Android Studio.
3. Pilih **Open** dan arahkan ke folder repository yang telah diunduh.
4. Tunggu proses Gradle Sync sampai selesai.
5. Pastikan Android SDK dan dependensi proyek sudah tersedia.
6. Hubungkan perangkat Android melalui USB Debugging atau jalankan emulator.
7. Pilih perangkat pada daftar device Android Studio.
8. Klik tombol **Run** untuk menjalankan aplikasi.
9. Tunggu proses instalasi hingga aplikasi JadwalKu terbuka pada perangkat.

## Screenshot Hasil Implementasi

Dokumentasi screenshot hasil implementasi aplikasi disimpan pada folder:

`screenshots/`

Screenshot yang didokumentasikan meliputi:

- Halaman utama aplikasi JadwalKu.
- Halaman tambah agenda.
- Halaman daftar Jadwal dan Tugas.
- Tampilan pemilihan nada alarm.
- Tampilan notifikasi pengingat.

## File APK

File APK hasil build aplikasi dapat ditemukan pada direktori:

`app/build/outputs/apk/debug/app-debug.apk`

Untuk menghasilkan APK melalui Android Studio:

1. Buka menu **Build**.
2. Pilih **Build Bundle(s) / APK(s)**.
3. Klik **Build APK(s)**.
4. Tunggu proses build selesai.
5. Klik **Locate** untuk membuka lokasi file APK.

## Referensi

1. [Android Developers – Android SDK](https://developer.android.com/)
2. [Android Developers – SQLite](https://developer.android.com/training/data-storage/sqlite)
3. [Android Developers – AlarmManager](https://developer.android.com/develop/background-work/services/alarms)
4. [Android Developers – Notifications](https://developer.android.com/develop/ui/views/notifications)
5. [Android Developers – RecyclerView](https://developer.android.com/develop/ui/views/layout/recyclerview)

---

**Nama Aplikasi:** JadwalKu – Pengingat Jadwal dan Tugas  
**Platform:** Android  
**Mata Kuliah:** Pemrograman Bergerak
