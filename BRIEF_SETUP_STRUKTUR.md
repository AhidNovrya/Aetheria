# BRIEF: Setup Struktur Project Aetheria (Maven + MVC)

Tugasmu hanya **menyiapkan kerangka folder dan konfigurasi**. Jangan menulis logika game, class model, atau fitur apa pun.

## 1. Kondisi saat ini

```
D:.
├── aetheria/                  (project Maven)
│   ├── pom.xml
│   ├── src/main/java/aetheria/Main.java
│   ├── src/main/resources/
│   ├── src/test/java/
│   └── target/                (hasil build, abaikan)
└── UML tubes/                 (11 file: 00-09 .puml + skin.iuml)
```

## 2. Target struktur

```
aetheria/
├── pom.xml
├── .gitignore
├── BRIEF_AHID.md              (dan BRIEF anggota lain bila ada)
├── docs/
│   └── uml/                   <- pindahan dari "UML tubes" (semua 11 file, tetap satu folder)
└── src/
    ├── main/java/aetheria/
    │   ├── Main.java
    │   ├── model/
    │   │   ├── character/     Character, Hero, 12 hero, Monster, 5 monster
    │   │   ├── enums/         HeroClass, HeroRace, MonsterType, StatusType, FormType
    │   │   ├── ability/       7 interface kemampuan
    │   │   ├── exception/     GameException dan turunannya
    │   │   ├── item/          Item, Weapon, Potion, Inventory
    │   │   └── quest/         Quest, QuestManager
    │   ├── service/           BattleManager, EncounterManager
    │   ├── view/              ConsoleView
    │   ├── controller/        GameController, BattleController, InventoryController, QuestController
    │   └── util/              GameConstants, Validator
    └── test/java/aetheria/
        ├── model/character/
        ├── model/item/
        ├── model/quest/
        └── service/
```

Catatan: `Monster` dan 5 monster ditaruh di `model.character` (sama dengan Character dan Hero) agar tidak ada package tambahan di luar diagram.

## 3. Langkah kerja

1. **Baca `pom.xml` yang ada.** Catat versi Java di `maven.compiler.release` / `source` / `target`. **Jangan mengubah versi Java.**
2. **Buat package** di `src/main/java/aetheria/` sesuai bagian 2. Cara yang disukai (pilih yang tersedia):
   - Lewat ekstensi VS Code Project Manager for Java / Extension Pack for Java (New Package di Java Projects view), atau
   - Langsung membuat folder lewat terminal.
   Hasil akhirnya sama, yang penting folder dan `package` deklarasi konsisten.
3. **Agar folder kosong tidak hilang di Git**, di tiap package buat `package-info.java` berisi hanya deklarasi package dan satu baris komentar tujuan package. Contoh:
   ```java
   /** Class dasar karakter: Character, Hero, Monster, dan turunannya. */
   package aetheria.model.character;
   ```
   Lakukan juga untuk package di `src/test/java` bila perlu (cukup `.gitkeep`).
4. **Perbarui `pom.xml`**: tambahkan dependency JUnit 5 (scope `test`) dan `maven-surefire-plugin` versi terbaru yang kompatibel dengan JUnit 5. Gunakan versi stabil terkini yang kamu verifikasi dari Maven Central, jangan menebak dari ingatan. Setelah itu pastikan `mvn -q test` berjalan (boleh dengan nol tes).
5. **Pindahkan folder `UML tubes` ke `aetheria/docs/uml/`** hanya jika Ahid sudah mengonfirmasi (folder itu berada di luar project saat ini). Jangan ganti nama file dan jangan memisahkan `skin.iuml` dari `.puml`.
6. **Buat `.gitignore`** berisi minimal: `target/`, `.vscode/` (kecuali bila tim sepakat membagikannya), `*.class`, `.idea/`, `*.iml`.
7. **Pastikan `Main.java`** tetap di `aetheria/` dan project dapat dikompilasi: `mvn -q compile`.

## 4. Verifikasi

- [ ] `mvn -q compile` berhasil.
- [ ] `mvn -q test` berhasil (tidak ada tes boleh, tapi tidak boleh error konfigurasi).
- [ ] Tiap package di bagian 2 ada dan memiliki `package-info.java` dengan nama package yang cocok dengan path foldernya.
- [ ] Tidak ada class game yang dibuat selain `package-info.java` dan `Main.java`.
- [ ] Versi Java di `pom.xml` tidak berubah.

## 5. Laporan akhir

Tulis singkat: perubahan pada `pom.xml` (versi dependency yang dipakai dan sumber verifikasinya), daftar package yang dibuat, dan hal yang butuh keputusan Ahid (misalnya pemindahan folder UML).
