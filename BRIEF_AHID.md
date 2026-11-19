# BRIEF: Aetheria - Bagian Ahid (Character & Leveling, aturan D&D 5e)

Kamu adalah agent yang membantu menulis kode Java untuk bagian **Character & Leveling** pada tugas besar PBO "Aetheria" (RPG fantasy berbasis Java console, berdasarkan Dungeons & Dragons 5e). Baca seluruh brief ini sebelum menulis kode.

## 1. Sumber kebenaran

Folder berisi file `.puml` adalah **satu-satunya acuan struktur class**. Jika ada dokumen lain (misalnya laporan PDF lama) yang berbeda, ikuti file `.puml`. Aturan angka (HP, stat, level) mengikuti bagian 7 brief ini.

File yang WAJIB dibaca:

| File | Alasan |
|---|---|
| `02_Character_dan_Hero.puml` | Class `Character` dan `Hero` (milik kamu) |
| `03_Dua_Belas_Hero.puml` | 12 subclass hero (milik kamu) |
| `01_Enum_dan_Interface.puml` | `HeroClass`, `HeroRace`, `Ability`, `StatusType`, `FormType`, dan 7 interface |
| `07_Exception_dan_Util.puml` | Exception, `GameConstants`, `Validator`, `DndRules`, `Dice` |

File yang cukup dibaca sekilas untuk memahami tipe yang kamu rujuk (jangan implementasikan isinya):
`00_Ringkasan_Package.puml`, `05_Inventory_dan_Item.puml` (`Inventory`, `Weapon` dengan dadu damage), `04_Monster.puml` (`Monster` juga mewarisi `Character`).

Tipe abu-abu bertanda `<<reference>>` di diagram berarti tipe itu dijelaskan penuh di diagram lain. `skin.iuml` hanya urusan tampilan, abaikan.

## 2. Ruang lingkup

**Kamu tulis:**
- `Character` (abstract), `Hero` (abstract)
- 12 hero: Barbarian, Bard, Cleric, Druid, Fighter, Monk, Paladin, Ranger, Rogue, Sorcerer, Warlock, Wizard
- Enum `HeroClass`, `HeroRace`, `Ability`, `StatusType`, `FormType`
- 7 interface (`Damageable`, `SpellCaster`, `PhysicalSkillUser`, `Healer`, `Shapeshifter`, `LifeStealer`, `StatusInflictor`), karena dibutuhkan agar kode dapat dikompilasi
- Util: `GameConstants`, `Validator`, `DndRules`, `Dice`
- Exception yang dipakai bagianmu: `GameException`, `InsufficientGoldException`, `SkillAlreadyUsedException`

**Kamu TIDAK tulis** (dikerjakan anggota lain, buat stub minimal hanya jika perlu untuk kompilasi, dan beri komentar `// STUB - dikerjakan anggota lain`):
`Monster` dan 5 monster, `Item`/`Weapon`/`Potion`/`Inventory`, `Quest`/`QuestManager`, `BattleManager`, `EncounterManager`, View, Controller, `Main`.

## 3. Struktur package

Project memakai **Maven**. Kode utama berada di `src/main/java/aetheria/`, tes di `src/test/java/aetheria/`. Struktur package sudah disiapkan (lihat `BRIEF_SETUP_STRUKTUR.md`); jangan membuat package baru di luar daftar ini:

```
src/main/java/aetheria/
  model/
    character/   Character, Hero, 12 hero
    enums/       HeroClass, HeroRace, Ability, StatusType, FormType
    ability/     7 interface
    exception/   GameException dan turunannya
  util/          GameConstants, Validator, DndRules, Dice
```

## 4. Aturan wajib (materi kuliah)

1. **Encapsulation**: semua atribut `private`. `protected` hanya untuk constructor dan method yang memang dipakai subclass. `hp` hanya berubah lewat `takeDamage()` dan `heal()`, tidak pernah di bawah 0 atau di atas `maxHp`.
2. **Inheritance**: `Hero extends Character`, 12 hero `extends Hero`. Logika heal yang sama (Bard, Cleric, Druid, Paladin) ditaruh di helper `Character.healBy(target, base)`, jangan ditulis ulang di tiap class.
3. **Abstract class & interface**: `Character`, `Hero` abstract. Interface diimplementasikan sesuai tabel di bawah. Tidak ada method interface yang dikosongkan tanpa alasan.
4. **Static & Collection**: konstanta di `GameConstants` (`static final`), rumus D&D di `DndRules` (static), lemparan dadu di `Dice` (static). Skor kemampuan dan status memakai `EnumMap`. Penghapusan status yang habis memakai `Iterator`.
5. **Exception**: `Hero.spendGold()` melempar `InsufficientGoldException`; `Hero.markSkillUsed()` melempar `SkillAlreadyUsedException`. Validasi constructor memakai `Validator` dan melempar `IllegalArgumentException` untuk input tidak valid. Jangan mengembalikan `boolean` sebagai tanda gagal.
6. **MVC**: model **tidak boleh** memakai `Scanner`, `System.out`, atau `System.in`. Method seperti `useSkill()` mengembalikan `String` log, View yang mencetak.
7. Jangan memanggil method yang bisa di-override dari dalam constructor.
8. Semua subclass hero memanggil `super(name, heroClass, race)`. Skor kemampuan akhir (dasar + bonus ras) dihitung **satu kali** di constructor `Hero` lewat `buildAbilityScores()` (private), lalu diberikan ke constructor `Character`.

## 5. Interface per hero

| Hero | Interface |
|---|---|
| Barbarian | PhysicalSkillUser |
| Bard | SpellCaster, Healer |
| Cleric | SpellCaster, Healer |
| Druid | SpellCaster, Healer, Shapeshifter |
| Fighter | PhysicalSkillUser |
| Monk | PhysicalSkillUser |
| Paladin | SpellCaster, PhysicalSkillUser, Healer |
| Ranger | SpellCaster, PhysicalSkillUser |
| Rogue | PhysicalSkillUser, StatusInflictor |
| Sorcerer | SpellCaster, StatusInflictor |
| Warlock | SpellCaster, LifeStealer |
| Wizard | SpellCaster, StatusInflictor |

Semua hero juga `Damageable` lewat `Character`.

## 6. Perilaku yang harus benar

- `useSkill(target)` memilih dan menjalankan skill sesuai class, mengembalikan `String`. Satu pemakaian = satu aksi, tidak memberi giliran tambahan. Damage skill dinyatakan dengan dadu (lewat `Dice.roll`).
- Skill hero dibatasi **satu kali per battle** lewat `skillUsed` di `Hero`; `resetSkill()` dipanggil di awal battle oleh `BattleManager` (milik anggota lain).
- Status: `BURN`, `POISON`, `ROOT` disimpan beserta durasi. `tickStatusEffects()` memproses efek di akhir giliran pemilik dan mengembalikan total damage status. `ROOT` membuat aksi dilewati (ditangani `BattleManager` lewat `hasStatus`).
- Form: `Druid.transform()` mengubah form ke `BEAST` dengan bonus lewat `setForm()`, `revertForm()` kembali ke `NORMAL`. `clearBattleEffects()` membersihkan semua status dan form.
- `gainExp()` dapat menaikkan beberapa level sekaligus, dibatasi `GameConstants.MAX_LEVEL`. Setiap level-up menghitung HP baru dan memanggil `applyLevelUpGrowth()` milik subclass (hook untuk fitur class, misalnya skill yang damage dadunya bertambah).

## 7. Aturan D&D 5e yang dipakai

Aturan ini menggantikan angka HP/ATK/DEF lama. Rumus ditulis di `DndRules` dan dibaca oleh `Character`/`Hero`.

**Skor kemampuan (6):** STR, DEX, CON, INT, WIS, CHA (enum `Ability`). Modifier = `floor((skor - 10) / 2)` (pakai `Math.floorDiv`, hati-hati dengan bilangan negatif). Skor dasar memakai *standard array* **15, 14, 13, 12, 10, 8** (`GameConstants.STANDARD_ARRAY`), dibagikan menurut urutan prioritas class:

| Class | Urutan prioritas (skor 15 -> 8) |
|---|---|
| Barbarian | STR, CON, DEX, WIS, CHA, INT |
| Bard | CHA, DEX, CON, WIS, INT, STR |
| Cleric | WIS, CON, STR, CHA, DEX, INT |
| Druid | WIS, CON, DEX, INT, CHA, STR |
| Fighter | STR, CON, DEX, WIS, CHA, INT |
| Monk | DEX, WIS, CON, STR, INT, CHA |
| Paladin | STR, CHA, CON, WIS, DEX, INT |
| Ranger | DEX, WIS, CON, STR, INT, CHA |
| Rogue | DEX, CON, INT, CHA, WIS, STR |
| Sorcerer | CHA, CON, DEX, WIS, INT, STR |
| Warlock | CHA, CON, DEX, WIS, INT, STR |
| Wizard | INT, CON, DEX, WIS, CHA, STR |

Urutan ini usulan; kemampuan utama (`HeroClass.getPrimaryAbility()`) = kemampuan prioritas pertama kecuali Monk/Ranger/Rogue = DEX, Cleric/Druid = WIS, Bard/Sorcerer/Warlock/Paladin = CHA, Wizard = INT, Barbarian/Fighter = STR.

**Bonus ras (versi PHB 2014):**

| Ras | Bonus |
|---|---|
| Human | +1 ke semua kemampuan |
| Elf | +2 DEX |
| Dwarf | +2 CON |

Bonus diterapkan satu kali saat hero dibuat. (Aturan 2024 memindahkan bonus ini ke background; project ini sengaja memakai versi 2014 agar ras punya efek.)

**Hit die per class:** Barbarian d12; Fighter, Paladin, Ranger d10; Bard, Cleric, Druid, Monk, Rogue, Warlock d8; Sorcerer, Wizard d6.

**HP:**
- Level 1: `hitDie (nilai maksimum) + modifier CON`.
- Tiap naik level: `rata-rata hit die + modifier CON`, dengan rata-rata = `hitDie/2 + 1` (d12=7, d10=6, d8=5, d6=4), minimal 1 HP per level. `maxHp` bertambah dan `hp` ikut bertambah sebesar kenaikan itu.

**Level, EXP, dan proficiency bonus:**
- Batas EXP kumulatif untuk mencapai level 2 s.d. 20: 300, 900, 2700, 6500, 14000, 23000, 34000, 48000, 64000, 85000, 100000, 120000, 140000, 165000, 195000, 225000, 265000, 305000, 355000. Simpan sebagai `DndRules.XP_THRESHOLDS`. Level maksimum 20 (`GameConstants.MAX_LEVEL`).
- Proficiency bonus: +2 (level 1-4), +3 (5-8), +4 (9-12), +5 (13-16), +6 (17-20).
- `Hero.getExpToNextLevel()` = sisa EXP sampai ambang level berikutnya (0 di level maksimum).

**Pertarungan:**
- `armorClass` = `10 + modifier DEX` (belum ada armor item) + bonus form bila ada.
- Serangan: lempar `d20 + attackBonus` melawan `armorClass` target. Kena bila hasil >= AC. Natural 20 = selalu kena dan **critical** (dadu damage digandakan). Natural 1 = selalu meleset.
- `Hero.getAttackBonus()` = proficiency bonus + modifier kemampuan utama + `bonusAttack` weapon (dari `Inventory`).
- `Hero.rollDamage(critical)` = dadu weapon yang dipasang (dari `Inventory.getEquippedDamageDice()`; tanpa weapon = 1d4) + modifier kemampuan utama (minimal 0 untuk total). Critical menggandakan jumlah dadu, bukan modifier.
- `Character.attack(target)` melakukan lemparan di atas dan mengembalikan damage yang benar-benar diberikan (0 bila meleset). Pesan log disusun oleh `BattleManager`.
- Skill: damage dinyatakan dengan dadu (misalnya Fireball 8d6 pada level tinggi dan lebih kecil di level rendah) dan boleh langsung mengenai tanpa lemparan serangan. Kamu menyusun tabel dadu per skill dan per level, lihat di bawah.

**Pengacak harus bisa diuji:** semua lemparan dadu lewat `Dice` yang dapat di-seed (`Dice.setSeed(long)`) agar pengujian deterministik.

**Yang kamu usulkan lalu minta persetujuan (jangan menebak diam-diam):**
1. Tabel dadu damage/heal untuk 12 skill, per rentang level (misalnya level 1-4, 5-10, 11+).
2. Bonus AC/serangan untuk form Druid (`BEAST`), durasi dan damage per tick untuk `BURN` dan `POISON`.
3. Apakah ada pembatasan EXP monster agar leveling tidak terlalu lambat (EXP monster ditentukan Raditya; ambang level D&D cukup tinggi).

Tulis semuanya di `STAT_PROPOSAL.md`, lalu **berhenti dan minta persetujuan** Ahid sebelum menulis kode yang memakai angka-angka itu. Rumus pada bagian ini sudah final dan tidak perlu disetujui ulang.

## 8. Kontrak dengan bagian lain (jangan ubah signature)

- `Hero.getInventory(): Inventory`, `Inventory.getWeaponBonus(): int`, dan `Inventory.getEquippedDamageDice(): int[]` (urutan `{jumlah, sisi}`) dari Lutfi.
- `Hero.gainExp(int)` dan `Hero.addGold(int)` dipanggil `GameController.handleVictory()` dan `Quest.claimReward()`.
- `Hero.isSkillUsed()`, `markSkillUsed()`, `resetSkill()` dipanggil `BattleManager`.
- `Character.attack`, `takeDamage(int): int`, `heal(int): int`, `addStatus`, `hasStatus`, `tickStatusEffects`, `clearBattleEffects`, `getArmorClass`, `getAttackBonus`, `rollDamage(boolean)` dipanggil `BattleManager`.
- `Monster` (Raditya) mengimplementasikan `getAttackBonus()` dan `rollDamage(boolean)` dari field di diagram 04.
- Method interface semuanya `public` dan mengembalikan `String`.

Kalau menurutmu signature di `.puml` salah atau kurang, **jangan diam-diam mengubahnya**. Catat di bagian "Pertanyaan" pada laporan akhir.

## 9. Pengujian

Tulis pengujian JUnit 5 di `src/test/java/aetheria/model/character/` (dependency JUnit sudah disiapkan di `pom.xml`; jalankan dengan `mvn test` atau dari Test Explorer VS Code). Pakai `Dice.setSeed` untuk tes yang melibatkan acak.
- Hero dibuat untuk **semua 36 kombinasi** class x ras tanpa error.
- `DndRules.modifier` benar untuk skor 1, 8, 9, 10, 11, 15, 20 (hasil -5, -1, -1, 0, 0, +2, +5).
- HP level 1 sama dengan hit die maksimum + modifier CON untuk tiap class (contoh: Barbarian Dwarf = 12 + modifier CON akhir).
- Skor kemampuan akhir = standard array menurut class + bonus ras, dan hanya dihitung sekali (membuat dua hero berturut-turut tidak menumpuk bonus).
- Level-up menambah HP sebesar rata-rata hit die + modifier CON; `gainExp` besar menaikkan beberapa level dan berhenti di level 20; proficiency bonus benar di batas level 4/5, 8/9, 12/13, 16/17.
- `takeDamage` tidak membuat HP di bawah 0; `heal` tidak melebihi `maxHp`.
- Natural 1 selalu meleset, natural 20 selalu kena dan menggandakan dadu damage (uji dengan seed atau `Dice` yang dapat disubstitusi).
- Skill kedua dalam satu battle melempar `SkillAlreadyUsedException`; setelah `resetSkill()` dapat dipakai lagi.
- `spendGold` melebihi saldo melempar `InsufficientGoldException`.
- Status kedaluwarsa terhapus; `clearBattleEffects()` mengembalikan form ke `NORMAL`.
- `useSkill` tiap hero mengembalikan `String` non-kosong dan tidak memanggil `System.out`.

## 10. Kriteria selesai

- [ ] Semua file di bagian 2 ("Kamu tulis") ada dan **dapat dikompilasi** (`mvn -q compile`).
- [ ] Tidak ada `Scanner`, `System.out`, atau `System.in` di `model/`.
- [ ] Setiap hero mengimplementasikan seluruh method interface yang dimilikinya (lihat bagian 5).
- [ ] Rumus bagian 7 diimplementasikan di `DndRules` dan semua pengujian bagian 9 lolos (`mvn -q test`).
- [ ] `STAT_PROPOSAL.md` sudah disetujui.
- [ ] Laporan akhir singkat: file yang dibuat, penyimpangan dari `.puml` (jika ada, beserta alasan), dan daftar "Pertanyaan".

## 11. Gaya kerja

- Kerjakan bertahap: enum + interface + exception + util, lalu `Character`, `Hero`, satu hero contoh (`Paladin`, karena punya tiga interface), jalankan uji, baru 11 hero lainnya.
- Jangan membuat fitur di luar `.puml` dan brief ini (database, save/load, GUI, multiplayer tidak termasuk). Aturan D&D di luar bagian 7 (advantage, saving throw, spell slot, armor, multiclass) tidak dipakai.
- Komentar kode dalam bahasa Indonesia, nama identifier dalam bahasa Inggris, mengikuti `.puml`.
