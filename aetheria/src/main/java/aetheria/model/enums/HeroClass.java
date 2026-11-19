package aetheria.model.enums;

/**
 * Dua belas class hero D&D 5e yang tersedia di game Aetheria.
 * Setiap class menyimpan: nama tampilan, hit die (sisi dadu HP),
 * kemampuan utama (D&D primary ability), dan urutan prioritas
 * pembagian standard array {15,14,13,12,10,8}.
 *
 * CATATAN PENYIMPANGAN dari puml 01_Enum_dan_Interface:
 * Puml hanya mendeklarasikan getDisplayName(). Method getHitDie(),
 * getPrimaryAbility(), dan getAbilityPriority() ditambahkan karena
 * dibutuhkan oleh sistem D&D 5e (brief §7) dan tidak ada di puml.
 */
public enum HeroClass {

    BARBARIAN("Barbarian", 12, Ability.STR,
            new Ability[]{Ability.STR, Ability.CON, Ability.DEX, Ability.WIS, Ability.CHA, Ability.INT}),

    BARD("Bard", 8, Ability.CHA,
            new Ability[]{Ability.CHA, Ability.DEX, Ability.CON, Ability.WIS, Ability.INT, Ability.STR}),

    CLERIC("Cleric", 8, Ability.WIS,
            new Ability[]{Ability.WIS, Ability.CON, Ability.STR, Ability.CHA, Ability.DEX, Ability.INT}),

    DRUID("Druid", 8, Ability.WIS,
            new Ability[]{Ability.WIS, Ability.CON, Ability.DEX, Ability.INT, Ability.CHA, Ability.STR}),

    FIGHTER("Fighter", 10, Ability.STR,
            new Ability[]{Ability.STR, Ability.CON, Ability.DEX, Ability.WIS, Ability.CHA, Ability.INT}),

    MONK("Monk", 8, Ability.DEX,
            new Ability[]{Ability.DEX, Ability.WIS, Ability.CON, Ability.STR, Ability.INT, Ability.CHA}),

    PALADIN("Paladin", 10, Ability.CHA,
            new Ability[]{Ability.STR, Ability.CHA, Ability.CON, Ability.WIS, Ability.DEX, Ability.INT}),

    RANGER("Ranger", 10, Ability.DEX,
            new Ability[]{Ability.DEX, Ability.WIS, Ability.CON, Ability.STR, Ability.INT, Ability.CHA}),

    ROGUE("Rogue", 8, Ability.DEX,
            new Ability[]{Ability.DEX, Ability.CON, Ability.INT, Ability.CHA, Ability.WIS, Ability.STR}),

    SORCERER("Sorcerer", 6, Ability.CHA,
            new Ability[]{Ability.CHA, Ability.CON, Ability.DEX, Ability.WIS, Ability.INT, Ability.STR}),

    WARLOCK("Warlock", 8, Ability.CHA,
            new Ability[]{Ability.CHA, Ability.CON, Ability.DEX, Ability.WIS, Ability.INT, Ability.STR}),

    WIZARD("Wizard", 6, Ability.INT,
            new Ability[]{Ability.INT, Ability.CON, Ability.DEX, Ability.WIS, Ability.CHA, Ability.STR});

    private final String displayName;
    /** Sisi hit die class ini (d6, d8, d10, atau d12). */
    private final int hitDie;
    /** Kemampuan utama untuk attack bonus dan beberapa skill. */
    private final Ability primaryAbility;
    /**
     * Urutan pembagian standard array {15,14,13,12,10,8}.
     * Index 0 mendapat 15, index 5 mendapat 8.
     */
    private final Ability[] abilityPriority;

    HeroClass(String displayName, int hitDie, Ability primaryAbility, Ability[] abilityPriority) {
        this.displayName     = displayName;
        this.hitDie          = hitDie;
        this.primaryAbility  = primaryAbility;
        this.abilityPriority = abilityPriority;
    }

    public String   getDisplayName()        { return displayName;              }
    public int      getHitDie()             { return hitDie;                   }
    public Ability  getPrimaryAbility()     { return primaryAbility;           }
    public Ability[] getAbilityPriority()   { return abilityPriority.clone();  }
}
