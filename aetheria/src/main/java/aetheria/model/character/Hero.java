package aetheria.model.character;

import aetheria.model.enums.Ability;
import aetheria.model.enums.FormType;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.model.exception.InsufficientGoldException;
import aetheria.model.exception.SkillAlreadyUsedException;
import aetheria.model.item.Inventory;
import aetheria.util.DndRules;
import aetheria.util.Dice;
import aetheria.util.GameConstants;
import aetheria.util.Validator;

import java.util.EnumMap;

/**
 * Kelas abstrak untuk semua hero yang bisa dimainkan di Aetheria.
 * Mengimplementasikan sistem leveling D&D 5e.
 *
 * CATATAN PENYIMPANGAN dari puml 02_Character_dan_Hero:
 * 1. Constructor Hero(name,heroClass,race) tanpa hp/atk/def; stat dihitung
 *    dari buildAbilityScores() dan hit die class.
 * 2. buildAbilityScores() di puml disebut applyRaceBonuses() dan private;
 *    di implementasi ini nama metode diubah menjadi buildAbilityScores
 *    (private static) agar bisa dipanggil dari super() chain.
 * 3. getArmorClass(), getAttackBonus(), rollDamage() ditambahkan
 *    per kontrak §8 brief.
 */
public abstract class Hero extends Character {

    private int level;
    private int exp;
    private int gold;
    private boolean skillUsed;

    private final HeroClass heroClass;
    private final HeroRace  race;
    private final Inventory inventory;

    // ─── Constructor chain ───────────────────────────────────────────────────

    /**
     * Constructor utama (dipanggil subkelas: super(name, HeroClass.X, race)).
     * Membangun skor kemampuan dari standard array + bonus ras, lalu
     * menghitung HP level-1 (hit die maks + CON modifier).
     */
    protected Hero(String name, HeroClass heroClass, HeroRace race) {
        // Gunakan constructor private helper agar buildAbilityScores
        // hanya dipanggil sekali
        this(name, heroClass, race,
             buildAbilityScores(heroClass, race));
    }

    /** Constructor chaining private — menerima scores yang sudah dibangun. */
    private Hero(String name, HeroClass heroClass, HeroRace race,
                 EnumMap<Ability, Integer> scores) {
        super(name, scores, computeStartingHp(heroClass, scores));
        Validator.requireNonBlank(name, "name");
        Validator.requireNonNull(heroClass, "heroClass");
        Validator.requireNonNull(race, "race");
        this.heroClass  = heroClass;
        this.race       = race;
        this.level      = 1;
        this.exp        = 0;
        this.gold       = 0;
        this.skillUsed  = false;
        this.inventory  = new Inventory();
    }

    // ─── Pembangun skor kemampuan (private static, aman di chain super()) ───

    /**
     * Membangun EnumMap skor kemampuan dari standard array + bonus ras.
     * Standard array D&D {15,14,13,12,10,8} dibagikan berdasarkan
     * prioritas class (heroClass.getAbilityPriority()).
     * Bonus ras diterapkan sekali di sini (tidak diulangi di tempat lain).
     */
    private static EnumMap<Ability, Integer> buildAbilityScores(HeroClass heroClass,
                                                                  HeroRace race) {
        EnumMap<Ability, Integer> scores = new EnumMap<>(Ability.class);
        int[]     arr      = GameConstants.STANDARD_ARRAY;
        Ability[] priority = heroClass.getAbilityPriority();

        // Distribusikan standard array sesuai prioritas class
        for (int i = 0; i < priority.length; i++) {
            scores.put(priority[i], arr[i]);
        }

        // Terapkan bonus ras satu kali
        for (Ability a : Ability.values()) {
            int bonus = race.getBonusFor(a);
            if (bonus != 0) {
                scores.merge(a, bonus, Integer::sum);
            }
        }
        return scores;
    }

    /**
     * Menghitung HP awal level 1.
     * Rumus D&D: nilai maksimum hit die + CON modifier (minimal 1 total).
     */
    private static int computeStartingHp(HeroClass heroClass,
                                          EnumMap<Ability, Integer> scores) {
        int conMod = DndRules.modifier(scores.getOrDefault(Ability.CON, 10));
        return Math.max(1, heroClass.getHitDie() + conMod);
    }

    // ─── Method abstract yang harus diimplementasikan subkelas ──────────────

    /** Nama skill aktif hero. */
    public abstract String getSkillName();

    /**
     * Hook pertumbuhan level-up per subkelas.
     * Dipanggil di setiap level-up. Subkelas bisa menambah stat khusus
     * atau meningkatkan dice skill sesuai level.
     */
    protected abstract void applyLevelUpGrowth();

    // ─── Implementasi D&D 5e (Character abstract) ───────────────────────────

    /**
     * Armor Class hero = 10 + DEX modifier + bonus form aktif.
     */
    @Override
    public int getArmorClass() {
        return 10 + DndRules.modifier(getAbilityScore(Ability.DEX)) + getFormDefenseBonus();
    }

    /**
     * Bonus serangan hero = proficiency bonus + modifier kemampuan utama
     * + bonus attack weapon dari inventory.
     */
    @Override
    public int getAttackBonus() {
        return DndRules.proficiencyBonus(level)
             + DndRules.modifier(getAbilityScore(heroClass.getPrimaryAbility()))
             + inventory.getWeaponBonus()
             + getFormAttackBonus();
    }

    /**
     * Melempar dadu damage hero.
     * Dadu diambil dari weapon yang terpasang (atau 1d4 tanpa weapon).
     * Critical menggandakan jumlah dadu (bukan modifier).
     * Modifier kemampuan utama ditambahkan (minimal 0).
     */
    @Override
    public int rollDamage(boolean critical) {
        int[] dice  = inventory.getEquippedDamageDice(); // {count, sides}
        int count   = critical ? dice[0] * 2 : dice[0];
        int sides   = dice[1];
        int mod     = Math.max(0, DndRules.modifier(getAbilityScore(heroClass.getPrimaryAbility())));
        return Dice.roll(count, sides) + mod;
    }

    /**
     * Attack power hero = total attack bonus (untuk kompatibilitas puml).
     */
    @Override
    public int getAttackPower() {
        return getAttackBonus();
    }

    // ─── Leveling ────────────────────────────────────────────────────────────

    /**
     * Menambahkan XP ke hero. Dapat menaikkan beberapa level sekaligus.
     * Dibatasi MAX_LEVEL. Tiap level-up memanggil levelUp() (private).
     *
     * @param amount XP yang diperoleh
     * @return level setelah XP ditambahkan
     */
    public int gainExp(int amount) {
        if (level >= GameConstants.MAX_LEVEL) return level;
        exp += Validator.requireNonNegative(amount, "amount");

        // Naikkan level selama XP mencukupi dan belum max
        while (level < GameConstants.MAX_LEVEL
               && exp >= DndRules.expForLevel(level + 1)) {
            levelUp();
        }
        return level;
    }

    /**
     * Menaikkan level satu tingkat dan menambah HP sesuai rata-rata hit die + CON mod.
     * HP tambahan minimal 1. Lalu memanggil hook applyLevelUpGrowth().
     */
    private void levelUp() {
        level++;
        int conMod   = DndRules.modifier(getAbilityScore(Ability.CON));
        int hpGain   = Math.max(1, DndRules.averageHitDie(heroClass.getHitDie()) + conMod);
        increaseMaxHp(hpGain);
        applyLevelUpGrowth();
    }

    // ─── Gold ────────────────────────────────────────────────────────────────

    /**
     * Menambahkan gold ke hero.
     * @param amount jumlah gold yang ditambahkan (tidak boleh negatif)
     */
    public void addGold(int amount) {
        gold += Validator.requireNonNegative(amount, "amount");
    }

    /**
     * Mengurangi gold hero.
     * @throws InsufficientGoldException bila gold tidak cukup
     */
    public void spendGold(int amount) throws InsufficientGoldException {
        Validator.requireNonNegative(amount, "amount");
        if (gold < amount) {
            throw new InsufficientGoldException(amount, gold);
        }
        gold -= amount;
    }

    public int getGold() { return gold; }

    // ─── Skill management ────────────────────────────────────────────────────

    public boolean isSkillUsed() { return skillUsed; }

    /**
     * Menandai skill sudah dipakai dalam battle ini.
     * @throws SkillAlreadyUsedException bila skill sudah dipakai sebelumnya
     */
    public void markSkillUsed() throws SkillAlreadyUsedException {
        if (skillUsed) {
            throw new SkillAlreadyUsedException(getSkillName());
        }
        skillUsed = true;
    }

    /** Mereset flag skill (dipanggil BattleManager di awal battle). */
    public void resetSkill() { skillUsed = false; }

    // ─── Getter lain ─────────────────────────────────────────────────────────

    public int       getLevel()          { return level;    }
    public int       getExp()            { return exp;      }
    public HeroClass getHeroClass()      { return heroClass; }
    public HeroRace  getRace()           { return race;     }
    public Inventory getInventory()      { return inventory; }

    /**
     * Sisa XP untuk level berikutnya. Mengembalikan 0 bila sudah MAX_LEVEL.
     */
    public int getExpToNextLevel() {
        if (level >= GameConstants.MAX_LEVEL) return 0;
        return DndRules.expForLevel(level + 1) - exp;
    }

    // ─── Helper damage skill (dipakai subkelas) ──────────────────────────────

    /**
     * Mengembalikan modifier kemampuan utama class ini (minimal 0).
     * Dipakai subkelas saat menghitung damage skill.
     */
    protected int primaryModifier() {
        return Math.max(0, DndRules.modifier(getAbilityScore(heroClass.getPrimaryAbility())));
    }

    /** Rentang level sebagai int: 0=lv1-4, 1=lv5-10, 2=lv11+. */
    protected int levelTier() {
        if (level <= 4)  return 0;
        if (level <= 10) return 1;
        return 2;
    }

    @Override
    public String toString() {
        return getHeroClass().getDisplayName() + " " + getName()
             + " Lv." + level
             + " [HP: " + getHp() + "/" + getMaxHp() + "]"
             + " [EXP: " + exp + "]"
             + " [Gold: " + gold + "]";
    }
}
