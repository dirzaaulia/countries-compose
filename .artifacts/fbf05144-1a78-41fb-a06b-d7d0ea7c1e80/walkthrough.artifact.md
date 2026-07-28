# Walkthrough - 3D Globe dengan Fitur Highlight

Fitur 3D Globe kini telah dilengkapi dengan kemampuan untuk menyoroti (highlight) negara tertentu, baik melalui interaksi sentuhan langsung maupun melalui tombol uji coba.

## Perubahan Utama

### 1. State Hoisting untuk Seleksi
Status `selectedCountryId` telah dipindahkan dari `GlobeView` ke `MainActivity`. Hal ini memungkinkan komponen luar (seperti tombol) untuk mengontrol negara mana yang sedang disorot di dalam globe.

### 2. Tombol Uji Coba (Random Highlight)
Menambahkan tombol **"Highlight Random Country"** di bagian bawah layar. Saat diklik, aplikasi akan memilih satu negara secara acak dari database GeoJSON dan menampilkannya dengan warna kuning di globe.

### 3. Tampilan Informasi Negara
Aplikasi kini menampilkan nama negara yang sedang dipilih tepat di atas tombol highlight untuk memudahkan identifikasi.

## Demo Visual

````carousel
![Awal: Memilih N. Cyprus](file:///C:/Users/ASUS/AndroidStudioProjects/Countries2/.artifacts/fbf05144-1a78-41fb-a06b-d7d0ea7c1e80/initial_selection.png)
<!-- slide -->
![Setelah klik tombol: Menyoroti Côte d'Ivoire](file:///C:/Users/ASUS/AndroidStudioProjects/Countries2/.artifacts/fbf05144-1a78-41fb-a06b-d7d0ea7c1e80/random_highlight.png)
````

> [!TIP]
> Anda tetap bisa memutar globe sambil melihat highlight negara yang dipilih. Jika negara yang dipilih berada di sisi belakang bumi, sorotan tidak akan terlihat sampai Anda memutar bumi ke sisi tersebut.

## Verifikasi
- [x] Tombol "Highlight Random Country" berfungsi memilih negara acak.
- [x] Nama negara muncul saat terpilih.
- [x] Highlight warna kuning muncul pada poligon negara yang sesuai.
- [x] Fitur rotasi tetap berfungsi dengan lancar.
