package aetheria.util;
/** Kumpulan rumus statis D&D 5e yang dipakai seluruh game. */
public final class DndRules {

    /**
     * Ambang XP kumulatif untuk mencapai level 1–20.
     * Index = level (1-based). XP_THRESHOLDS[1]=0 (level 1 mulai dari 0).
     */
    public static final int[] XP_THRESHOLDS = {
        0,       // index 0, unused
        0,       // level 1
        300,     // level 2
        900,     // level 3
        2700,    // level 4
        6500,    // level 5
        14000,   // level 6
        23000,   // level 7
        34000,   // level 8
        48000,   // level 9
        64000,   // level 10
        85000,   // level 11
        100000,  // level 12
        120000,  // level 13
        140000,  // level 14
        165000,  // level 15
        195000,  // level 16
        225000,  // level 17
        265000,  // level 18
        305000,  // level 19
        355000   // level 20
    };

    private DndRules() { /* tidak bisa diinstansiasi */ }

    /**
     * Menghitung ability modifier dari skor kemampuan.
     * Rumus: floor((score - 10) / 2). Memakai Math.floorDiv agar negatif benar.
     */
    public static int modifier(int score) {
        return Math.floorDiv(score - 10, 2);
    }

    /**
     * Mengembalikan proficiency bonus berdasarkan level hero.
     * Level 1-4 = +2, 5-8 = +3, 9-12 = +4, 13-16 = +5, 17-20 = +6.
     */
    public static int proficiencyBonus(int level) {
        return 2 + (level - 1) / 4;
    }

    /**
     * Mengembalikan level yang dicapai berdasarkan total XP yang dimiliki.
     * Dibatasi MAX_LEVEL.
     */
    public static int levelForExp(int exp) {
        int level = 1;
        for (int lv = GameConstants.MAX_LEVEL; lv >= 2; lv--) {
            if (exp >= XP_THRESHOLDS[lv]) {
                level = lv;
                break;
            }
        }
        return level;
    }

    /**
     * Mengembalikan total XP yang dibutuhkan untuk mencapai suatu level.
     * @param level antara 1 dan MAX_LEVEL
     */
    public static int expForLevel(int level) {
        if (level < 1) return 0;
        if (level > GameConstants.MAX_LEVEL) return XP_THRESHOLDS[GameConstants.MAX_LEVEL];
        return XP_THRESHOLDS[level];
    }

    /**
     * Menghitung rata-rata hit die (dibulatkan ke bawah, minimal 1).
     * Rumus D&D: sides/2 + 1 (d12=7, d10=6, d8=5, d6=4).
     */
    public static int averageHitDie(int sides) {
        return sides / 2 + 1;
    }
}
