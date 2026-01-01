# 🏛️ MuseumMu (Museum Muhammadiyah App)

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-039BE5?style=for-the-badge&logo=Firebase&logoColor=white)
![Status](https://img.shields.io/badge/Status-Completed-success?style=for-the-badge)

> **Transformasi Digital Layanan Museum Muhammadiyah UAD Kampus 4**

**MuseumMu** adalah aplikasi Android Native yang dirancang untuk memodernisasi pengalaman berkunjung ke Museum Muhammadiyah. Aplikasi ini mengatasi kendala informasi konvensional dengan menyediakan sistem pemesanan tiket *real-time*, manajemen kuota otomatis, dan informasi lokasi terintegrasi.

---

## 📱 Fitur Utama

Aplikasi ini dikembangkan dengan pendekatan *mobile-first* untuk memastikan kenyamanan pengguna:

### 1. 🎟️ Sistem Tiket & Kuota Real-time
* **Pengecekan Kuota Otomatis:** Menampilkan sisa slot pengunjung per sesi secara langsung (live) dari database.
* **Booking Fleksibel:** Mendukung mode pemesanan **Individu** dan **Rombongan** (Grup).
* **Validasi Cerdas:** Mencegah *overbooking* (pemesanan melebihi kapasitas) menggunakan *Firebase Transactions*.

### 2. 👤 Manajemen Pengguna
* **Secure Auth:** Registrasi dan Login aman menggunakan **Firebase Authentication**.
* **Profile Management:** Edit profil pengguna yang tersinkronisasi ke *cloud*.
* **Order History:** Riwayat pesanan tiket digital lengkap dengan detail status dan harga.

### 3. 🗺️ Informasi & Lokasi
* **Interactive Map:** Integrasi **Google Maps API** (Hybrid Mode) untuk menampilkan lokasi presisi museum.
* **Session Info:** Informasi jadwal sesi operasional museum yang akurat.

---

## 🛠️ Tech Stack & Architecture

Project ini dibangun menggunakan teknologi dan standar pengembangan Android modern:

* **Bahasa:** Kotlin
* **Arsitektur:** MVVM (Model-View-ViewModel)
* **UI Design:** XML Layouts, Material Design Components
* **Database:** Firebase Realtime Database
* **Authentication:** Firebase Auth
* **Maps:** Google Maps SDK for Android
* **Libraries:**
    * *ViewBinding* (Efisiensi akses UI)
    * *Navigation Component* (Navigasi antar-fragment)
    * *Lifecycle & LiveData* (Manajemen data reaktif)

---

## 🚀 Cara Menjalankan Project (Installation)

Ikuti langkah ini untuk menjalankan project di Android Studio:

1.  **Clone Repository**
    ```bash
    git clone [https://github.com/username-kamu/MuseumMu.git](https://github.com/username-kamu/MuseumMu.git)
    ```

2.  **Buka di Android Studio**
    * Buka Android Studio -> File -> Open -> Pilih folder `MuseumMu`.
    * Tunggu proses *Gradle Sync* selesai.

3.  **Konfigurasi Firebase (Penting!)**
    * Buat project baru di [Firebase Console](https://console.firebase.google.com/).
    * Aktifkan **Authentication** (Email/Password).
    * Aktifkan **Realtime Database**.
    * Download file `google-services.json` dari Firebase Console.
    * **Paste** file `google-services.json` ke dalam folder `app/` di project ini.

4.  **Konfigurasi Google Maps API**
    * Buka `local.properties` atau `AndroidManifest.xml`.
    * Masukkan API Key Google Maps kamu pada tag `<meta-data>`:
        ```xml
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="MASUKKAN_API_KEY_KAMU_DISINI" />
        ```

5.  **Run App**
    * Jalankan aplikasi di Emulator atau Device Fisik.

---

## 👨‍💻 Development Team

Project ini dikembangkan oleh tim mahasiswa **Informatika - Universitas Ahmad Dahlan** sebagai bagian dari Tugas Proyek Matakuliah Pemrograman Mobile:

| No | Nama Anggota | NIM |
| :--- | :--- | :--- |
| 1. | **Farhan Muhammad Iqbal** | 2300018164 |
| 2. | **Muhammad Arief Putra Irawan** | 2300018167 |
| 3. | **Fikri Abdi Mubarrak** | 2300018189 |

---

**© 2025 MuseumMu Project.**
---

## 📂 Struktur Project (MVVM)

Project ini diorganisir menggunakan arsitektur **MVVM** untuk memisahkan *Logic*, *Data*, dan *UI* agar kode lebih rapi dan mudah di-*maintain*.

```text
com.projectpmob.museummu
├── data
│   ├── model              # Data Classes (Blueprint Objek)
│   │   ├── Ticket.kt      # Model data tiket transaksi
│   │   └── User.kt        # Model data profil pengguna
│   │
│   └── repository         # Data Source Manager (Firebase)
│       └── TicketRepository.kt
│
├── ui
│   ├── auth               # Fitur Autentikasi
│   │   ├── LoginActivity.kt
│   │   └── RegisterActivity.kt
│   │
│   ├── home               # Halaman Utama
│   │   ├── HomeFragment.kt
│   │   └── HomeViewModel.kt
│   │
│   ├── order              # Fitur Pemesanan Tiket
│   │   ├── OrderFragment.kt      # Parent Fragment (Tab Container)
│   │   ├── OrderPagerAdapter.kt  # Adapter Navigasi Tab (Individu/Grup)
│   │   ├── OrderFormFragment.kt  # Form Input Pemesanan
│   │   └── OrderViewModel.kt     # Logika Validasi & Booking
│   │
│   ├── history            # Fitur Riwayat Transaksi
│   │   ├── HistoryFragment.kt
│   │   ├── HistoryViewModel.kt
│   │   ├── HistoryAdapter.kt     # Adapter RecyclerView List
│   │   └── DetailHistoryFragment.kt
│   │
│   └── profile            # Manajemen Profil User
│       ├── ProfileFragment.kt
│       └── EditProfileActivity.kt
│
├── utils
│   └── SessionManager.kt  # Helper untuk Shared Preferences (Local Storage)
│
└── MainActivity.kt        # Activity Utama (Bottom Navigation Host)

