# STAT_PROPOSAL — Aetheria (Sistem D&D 5e)

> **Status: MENUNGGU PERSETUJUAN AHID**
> Rumus D&D (skor kemampuan, modifier, HP, attack roll, XP threshold, proficiency bonus)
> sudah **final** di brief §7 dan tidak perlu disetujui. Yang perlu disetujui ada di bawah.

---

## A. Dadu damage / heal per skill (12 hero) — PERLU DISETUJUI

Rentang: **Level 1–4**, **Level 5–10**, **Level 11–20**.
Format dadu: `NdS` (N dadu, S sisi). Heal memakai dadu yang sama, hasilnya ditambahkan ke HP target.

| Hero | Skill | Lv 1–4 | Lv 5–10 | Lv 11–20 | Catatan |
|---|---|---|---|---|---|
| **Barbarian** | Raging Strike | 2d6 + mod STR | 3d6 + mod STR | 4d6 + mod STR | Physical, langsung kena (tidak butuh attack roll) |
| **Bard** | Inspiring Tune | Heal ally: 2d6 + mod CHA | 3d6 + mod CHA | 4d6 + mod CHA | Dapat memilih diri sendiri sebagai target |
| **Cleric** | Healing Word | Heal: 2d8 + mod WIS | 3d8 + mod WIS | 4d8 + mod WIS | Heal jarak jauh, tidak butuh attack roll |
| **Druid** | Wild Shape | Transformasi (lihat §B) + Heal self: 1d8 + mod WIS | 2d8 + mod WIS | 2d8 + mod WIS | Heal diri, lalu transform ke BEAST |
| **Fighter** | Power Strike | 2d8 + mod STR | 3d8 + mod STR | 4d8 + mod STR | Physical, langsung kena |
| **Monk** | Flurry of Blows | 3 serangan @ 1d6 + mod DEX | 3 @ 1d8 + mod DEX | 3 @ 1d10 + mod DEX | Tiap serangan gunakan attack roll d20 |
| **Paladin** | Divine Smite | 2d8 smite + 1d8 holy + Heal self: 1d6 + mod CHA | 3d8 + 1d8 + 1d8 + mod CHA | 4d8 + 2d8 + 2d8 + mod CHA | Smite = damage ke target; holy = damage radiant; heal = pulihkan diri |
| **Ranger** | Precise Shot | 2d8 + mod DEX + BURN 2 ronde | 3d8 + mod DEX + BURN 3 ronde | 4d8 + mod DEX + BURN 3 ronde | Langsung kena, tambahkan BURN |
| **Rogue** | Sneak Attack | 2d6 + mod DEX + POISON 3 ronde | 3d6 + mod DEX + POISON 3 ronde | 4d6 + mod DEX + POISON 3 ronde | Langsung kena, tambahkan POISON |
| **Sorcerer** | Chaos Bolt | 2d8 + mod CHA + BURN 2 ronde | 4d8 + mod CHA + BURN 2 ronde | 6d8 + mod CHA + BURN 2 ronde | Langsung kena, tambahkan BURN |
| **Warlock** | Eldritch Blast | 2d10 + mod CHA | 3d10 + mod CHA | 4d10 + mod CHA | Drain: pulihkan HP = 50% damage yang diberikan |
| **Wizard** | Fireball | 4d6 + mod INT + BURN 3 ronde | 6d6 + mod INT + BURN 3 ronde | 8d6 + mod INT + BURN 3 ronde | Langsung kena, BURN 3 ronde |

> **Modifier**: minimal 0 ditambahkan ke damage (sesuai brief §7, "minimal 0 untuk total").
> Untuk heal, modifier boleh negatif dikurangi menjadi 0 (tidak ada heal negatif).

---

## B. Form Druid BEAST — PERLU DISETUJUI

| Parameter | Usulan |
|---|---|
| Bonus AC saat BEAST | +2 |
| Bonus attack saat BEAST | +2 |
| Durasi form | Hingga `revertForm()` dipanggil (tidak berbatas ronde, dikontrol BattleManager/player) |
| Efek pada skill | Skill Druid tetap dapat digunakan setelah transform (transform adalah bagian dari `useSkill`) |

---

## C. Damage per tick status effects — PERLU DISETUJUI

| Status | Damage per tick (tiap akhir giliran pemilik status) | Durasi default (ronde) |
|---|---|---|
| **BURN** | `1d4` | Lihat tabel skill (umumnya 2–3 ronde) |
| **POISON** | `1d6` | Lihat tabel skill (umumnya 3 ronde) |
| **ROOT** | 0 (tidak damage, hanya lewati giliran) | 1 ronde |

> Durasi diatur oleh skill yang menerapkan status (lihat §A). `tickStatusEffects()` mengurangi durasi 1 dan menghapus bila 0.

---

## D. Pembatasan EXP monster — PERLU DISETUJUI

XP threshold D&D sangat tinggi (level 2 = 300 XP, level 20 = 355.000 XP kumulatif). Tanpa scaling, game akan terasa grinding.

**Usulan:** Tidak membatasi EXP monster dari sisi kode Hero/Character (sesuai kontrak, EXP monster ditentukan Raditya). Cukup mencatat ini sebagai pertanyaan untuk dikoordinasikan.

---

## Pertanyaan untuk Ahid

1. Apakah dadu damage tiap skill di tabel §A sudah sesuai ekspektasi (tema class-nya)?
2. Apakah Paladin `useSkill` menggabungkan smite + heal dalam satu aksi, atau pilih salah satu?
3. Apakah Druid `useSkill` = transform + heal diri, atau hanya transform (heal terpisah)?
4. Apakah bonus AC/ATK form BEAST (`+2/+2`) sudah pas?
5. Apakah BURN `1d4` dan POISON `1d6` per tick terasa sesuai?
6. Koordinasi EXP monster dengan Raditya — apakah perlu dari sisi ini?

---

**Setujui dengan menulis `SETUJU` (atau beri koreksi) agar kode class hero dapat ditulis.**

---
*Catatan Perbaikan P3: primaryAbility Paladin diubah dari CHA ke STR agar attack roll seimbang, sedangkan heal diri dan heal target Paladin tetap memakai modifier CHA.*


