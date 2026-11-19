package aetheria.model.enums;

/**
 * Tiga ras hero yang tersedia: Human, Elf, Dwarf.
 * Bonus ras mengikuti D&D 5e PHB 2014:
 *   Human  : +1 ke semua kemampuan
 *   Elf    : +2 DEX
 *   Dwarf  : +2 CON
 *
 * CATATAN PENYIMPANGAN dari puml 01_Enum_dan_Interface:
 * Puml mendefinisikan bonusHp/bonusAttack/bonusDefense (sistem lama).
 * Brief §7 menggantinya dengan bonus per-ability D&D 5e.
 * Method getBonusFor(Ability) menggantikan getBonusHp/getBonusAttack/getBonusDefense.
 */
public enum HeroRace {

    HUMAN("Human") {
        @Override
        public int getBonusFor(Ability ability) {
            return 1; // +1 ke semua kemampuan
        }
    },
    ELF("Elf") {
        @Override
        public int getBonusFor(Ability ability) {
            return ability == Ability.DEX ? 2 : 0; // +2 DEX
        }
    },
    DWARF("Dwarf") {
        @Override
        public int getBonusFor(Ability ability) {
            return ability == Ability.CON ? 2 : 0; // +2 CON
        }
    };

    private final String displayName;

    HeroRace(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }

    /**
     * Mengembalikan bonus ras untuk kemampuan tertentu.
     * @param ability kemampuan yang dicek
     * @return nilai bonus (0 jika tidak ada bonus untuk kemampuan itu)
     */
    public abstract int getBonusFor(Ability ability);
}
