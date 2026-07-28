# Implementasi 3D Globe Interaktif dengan Pemilihan Negara

Rencana ini akan membangun sebuah Globe 3D interaktif menggunakan Jetpack Compose `Canvas`. Pengguna dapat memutar globe dengan gerakan *drag* dan memilih negara untuk di-highlight dengan cara mengetuknya.

## Ringkasan Teknis

- **Data**: Menggunakan file GeoJSON (resolusi 110m) untuk batas-batas negara.
- **Rendering**: Proyeksi matematika kustom dari koordinat Geografis (Lat/Lon) ke 3D (X, Y, Z) lalu ke 2D (Layar).
- **Interaksi**: Gestur *drag* untuk rotasi (Yaw & Pitch) dan *tap* untuk deteksi negara menggunakan algoritma "Point-in-Polygon".
- **Visual**: Efek bayangan gradien untuk memberikan kedalaman 3D pada bola dunia.

## Perubahan yang Diusulkan

### Dependensi & Aset

#### [MODIFY] [build.gradle.kts](file:///C:/Users/ASUS/AndroidStudioProjects/Countries2/app/build.gradle.kts)
- Menambahkan dependensi `kotlinx-serialization-json` untuk memproses data GeoJSON.

#### [NEW] `assets/countries.geojson`
- Mengunduh dan menyimpan data peta dunia yang ringan (~600KB).

### Data Layer

#### [NEW] `GeoJsonModels.kt`
- Definisi data class untuk struktur GeoJSON (Feature, Geometry, Polygon).

#### [NEW] `GlobeRepository.kt`
- Logika untuk memuat file GeoJSON dari assets dan mem-parsingnya menjadi objek Kotlin.

### Logic & Rendering Layer

#### [NEW] `GlobeMath.kt`
- Fungsi untuk proyeksi koordinat dan matriks rotasi.
- Implementasi algoritma "Point in Polygon" (Ray Casting).

### UI Layer

#### [NEW] `GlobeView.kt`
- Composable utama yang menggunakan `Canvas` untuk menggambar globe.
- Penanganan gestur `pointerInput` untuk rotasi dan seleksi.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/ASUS/AndroidStudioProjects/Countries2/app/src/main/java/com/dirzaaulia/countries/MainActivity.kt)
- Mengganti tampilan default dengan `GlobeView`.

## Rencana Verifikasi

### Tes Otomatis
- Unit test untuk fungsi proyeksi matematika.
- Unit test untuk logika "Point in Polygon".

### Verifikasi Manual
1. Menjalankan aplikasi di emulator atau device.
2. Mencoba memutar globe ke segala arah.
3. Mengetuk beberapa negara (misal: Indonesia, Brasil, Rusia) dan memastikan highlight muncul dengan benar.
4. Memastikan negara di sisi belakang globe tidak terlihat atau tidak dapat diklik.

## Pertanyaan Terbuka
- Apakah ada warna khusus yang diinginkan untuk globe atau highlight negara? (Default: Biru untuk laut, Hijau/Abu untuk daratan, Kuning untuk seleksi).
- Apakah perlu menampilkan nama negara yang dipilih dalam bentuk teks/label?
