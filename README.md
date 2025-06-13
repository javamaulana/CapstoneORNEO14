# Currency Converter Pro 💰

Aplikasi Android modern yang dirancang sebagai Capstone Project untuk mengkonversi mata uang secara *real-time*. 
Dibangun sepenuhnya menggunakan Kotlin dan Jetpack Compose dengan arsitektur MVVM, aplikasi ini tidak hanya fungsional 
tetapi juga memiliki antarmuka yang menarik dan pengalaman pengguna yang dinamis.


## ✨ Fitur Utama
Aplikasi ini dilengkapi dengan serangkaian fitur lengkap yang mencakup fungsionalitas inti dan pengalaman pengguna yang kaya:

* **Splash Screen**: Memeriksa status sesi pengguna saat aplikasi dimulai. 
* **Otentikasi Pengguna**:
    * Sistem **Registrasi** dan **Login** berbasis data lokal. 
    * Validasi input, termasuk format email, untuk memastikan integritas data. 
    * Data pengguna (termasuk nama, email, dan gender) disimpan dengan aman di database **Room**. 
* **Manajemen Sesi**: Status login dan preferensi pengguna (seperti mata uang terakhir yang digunakan) disimpan secara persisten menggunakan **Jetpack DataStore**. 
* **Konverter Interaktif**:
    * Konversi mata uang secara *real-time* menggunakan data dari Frankfurter API. 
    * Animasi "count-up" yang halus saat hasil konversi ditampilkan.
    * Ukuran teks adaptif untuk menangani angka konversi yang sangat besar.
    * Menampilkan kurs dua arah (misal: 1 USD -> IDR dan 1 IDR -> USD).
    * Tombol **Swap** untuk menukar mata uang "Dari" dan "Ke" dengan cepat.
    * Input jumlah yang diformat secara otomatis dengan pemisah ribuan.
* **Manajemen Favorit**:
    * Menyimpan dan menghapus pasangan mata uang favorit, spesifik untuk setiap pengguna. 
    * Menampilkan kurs terkini langsung di daftar favorit. 
    * Mencegah penambahan favorit yang duplikat (termasuk kebalikannya, misal: USD-IDR dan IDR-USD).
* **Katalog & Detail Mata Uang**:
    * Halaman **katalog** yang menampilkan semua mata uang yang tersedia beserta benderanya.
    * Halaman **detail** dengan **grafik interaktif** untuk melihat riwayat kurs.
    * Fitur pemilih rentang tanggal (*Date Picker*) untuk analisa historis.
    * Pilihan mata uang basis yang dinamis untuk perbandingan.
* **Profil Pengguna**:
    * Halaman profil yang menampilkan data pengguna seperti nama, email, dan jenis kelamin. 
    * Avatar yang berubah sesuai dengan jenis kelamin pengguna.
    * Fungsi **Logout** yang aman. 

## 🛠️ Tumpukan Teknologi & Arsitektur

Proyek ini dibangun dengan mengikuti praktik terbaik pengembangan Android modern.

* **Arsitektur**: Model-View-ViewModel (MVVM) 
* **UI**: 100% Jetpack Compose
* **Asynchronous**: Kotlin Coroutines & StateFlow 
* **Database Lokal**: Room 
* **Penyimpanan Preferensi**: Jetpack DataStore 
* **Networking**: Retrofit2 & Gson 
* **Memuat Gambar**: Coil
* **Grafik**: YCharts

## 🚀 Cara Menjalankan Proyek

1.  **Clone** repositori ini.
2.  Buka proyek menggunakan versi terbaru Android Studio.
3.  Pastikan Anda memiliki **dua gambar avatar** di dalam folder `app/src/main/res/drawable` dengan nama:
    * `ic_avatar_male.png` (atau .xml)
    * `ic_avatar_female.png` (atau .xml)
4.  Lakukan **Sync Project with Gradle Files**.
5.  **Build** dan **Run** aplikasi pada emulator atau perangkat fisik.

## 🔗 Referensi API
Aplikasi ini menggunakan API gratis dari [**Frankfurter.app**](https://www.frankfurter.app/docs/) untuk mendapatkan data kurs mata uang. 
Bendera diambil dari [**Flagpedia.net**](https://flagpedia.net) via FlagCDN.

---
*Dibuat oleh [**Javamaulana**](https://github.com/javamaulana)*
