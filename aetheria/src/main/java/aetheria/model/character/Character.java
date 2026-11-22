package aetheria.model.character;

import aetheria.model.ability.Damageable;
import aetheria.model.enums.Ability;
import aetheria.model.enums.FormType;
import aetheria.model.enums.StatusType;
import aetheria.util.DndRules;
import aetheria.util.Dice;
import aetheria.util.Validator;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Kelas dasar abstrak untuk semua karakter (hero dan monster) di Aetheria.
 *
 * CATATAN PENYIMPANGAN dari puml 02_Character_dan_Hero:
 * 1. Constructor diubah dari Character(name,hp,atk,def) menjadi
 *    Character(name, abilityScores, maxHp) agar mendukung sistem D&D 5e.
 * 2. Field attackPower dan defense dari puml tetap ada tapi bersifat derived.
 * 3. Method getArmorClass(), getAttackBonus(), rollDamage(boolean) ditambahkan
 *    karena diperlukan kontrak §8 brief (dipanggil BattleManager).
 * 4. Ini tidak ada di puml tapi dibutuhkan sistem D&D.
 */
public abstract class Character implements Damageable {

    private final String name;
    private int hp;
    private int maxHp;

    /** Skor kemampuan D&D (STR, DEX, CON, INT, WIS, CHA). */
    private final EnumMap<Ability, Integer> abilityScores;

    /** Status aktif beserta sisa durasi (ronde). */
    private final EnumMap<StatusType, Integer> statuses;

    /** Form saat ini (NORMAL, BEAST, OBJECT). */
    private FormType form;

    /** Bonus attack saat dalam form non-NORMAL. */
    private int formAttackBonus;

    /** Bonus armor class saat dalam form non-NORMAL. */
    private int formDefenseBonus;

    /**
     * Constructor utama Character.
     *
     * @param name         nama karakter (tidak boleh blank)
     * @param abilityScores skor kemampuan D&D (tidak boleh null)
     * @param maxHp        HP maksimum awal (tidak boleh negatif)
     */
    protected Character(String name,
                        EnumMap<Ability, Integer> abilityScores,
                        int maxHp) {
        this.name          = Validator.requireNonBlank(name, "name");
        Validator.requireNonNull(abilityScores, "abilityScores");
        this.abilityScores = new EnumMap<>(abilityScores);
        this.maxHp         = Validator.requireNonNegative(maxHp, "maxHp");
        this.hp            = maxHp;
        this.statuses      = new EnumMap<>(StatusType.class);
        this.form          = FormType.NORMAL;
        this.formAttackBonus  = 0;
        this.formDefenseBonus = 0;
    }

    // ─── Getter dasar (dari puml) ───────────────────────────────────────────

    public String getName()       { return name; }
    public int    getHp()         { return hp; }
    public int    getMaxHp()      { return maxHp; }
    public FormType getForm()     { return form; }

    /** Mengembalikan skor kemampuan tertentu (default 10 bila tidak ada). */
    public int getAbilityScore(Ability ability) {
        return abilityScores.getOrDefault(ability, 10);
    }

    /** Mengembalikan modifier kemampuan D&D. */
    public int getModifier(Ability ability) {
        return DndRules.modifier(getAbilityScore(ability));
    }

    /** Mengembalikan proficiency bonus karakter. */
    public abstract int getProficiencyBonus();

    /** Mengembalikan Map skor kemampuan yang tidak dapat dimodifikasi. */
    public Map<Ability, Integer> getAbilityScoreMap() {
        return Collections.unmodifiableMap(new EnumMap<>(abilityScores));
    }

    /** Menyediakan akses ke salinan abilityScores (untuk subkelas). */
    protected EnumMap<Ability, Integer> getAbilityScores() {
        return new EnumMap<>(abilityScores);
    }

    protected int getFormAttackBonus()  { return formAttackBonus; }
    protected int getFormDefenseBonus() { return formDefenseBonus; }

    // ─── Method D&D 5e (kontrak §8 dengan BattleManager) ───────────────────

    /**
     * Mengembalikan Armor Class karakter ini.
     * Implementasi berbeda antara hero (10 + DEX mod + form bonus)
     * dan monster (AC tetap + form bonus).
     */
    public abstract int getArmorClass();

    /**
     * Mengembalikan bonus serangan karakter ini (untuk roll d20).
     * Hero: proficiency + mod kemampuan utama + bonus weapon.
     * Monster: nilai tetap dari field.
     */
    public abstract int getAttackBonus();

    /**
     * Melempar dadu damage.
     * @param critical true bila natural 20 (jumlah dadu digandakan, bukan modifier)
     * @return total damage sebelum dikurangi pertahanan target
     */
    public abstract int rollDamage(boolean critical);

    // ─── Damageable interface ───────────────────────────────────────────────

    @Override
    public boolean isAlive() { return hp > 0; }

    /**
     * Mengurangi HP sebesar damage (minimum 0).
     * @param damage damage yang diterima (tidak boleh negatif)
     * @return damage aktual yang diterima
     */
    @Override
    public int takeDamage(int damage) {
        Validator.requireNonNegative(damage, "damage");
        int actualDamage = Math.min(damage, hp);
        hp -= actualDamage;
        return actualDamage;
    }

    // ─── Method pertarungan ─────────────────────────────────────────────────

    /**
     * Melakukan serangan terhadap target menggunakan mekanisme D&D 5e.
     * Natural 1 = selalu meleset. Natural 20 = selalu kena + critical.
     * Kena bila roll + attackBonus >= target.getArmorClass().
     *
     * @param target target yang diserang
     * @return damage aktual yang diberikan (0 bila meleset)
     */
    public int attack(Character target) {
        int roll = Dice.d20();
        if (roll == 1) return 0; // natural 1: selalu meleset
        boolean critical = (roll == 20);
        boolean hit = critical || (roll + getAttackBonus() >= target.getArmorClass());
        if (!hit) return 0;
        return target.takeDamage(rollDamage(critical));
    }

    /**
     * Menyembuhkan HP sebesar amount (tidak melebihi maxHp).
     * @param amount jumlah heal (tidak boleh negatif)
     * @return jumlah HP yang benar-benar dipulihkan
     */
    public int heal(int amount) {
        Validator.requireNonNegative(amount, "amount");
        int actual = Math.min(amount, maxHp - hp);
        hp += actual;
        return actual;
    }

    /**
     * Meningkatkan maxHp (dan hp saat ini) - alias untuk level-up HP.
     * Disediakan agar subkelas Hero bisa memanggil dengan nama yang jelas.
     */
    protected void increaseMaxHp(int hpBonus) {
        Validator.requireNonNegative(hpBonus, "hpBonus");
        maxHp += hpBonus;
        hp    += hpBonus;
    }

    /** Skill aktif karakter. Diimplementasikan masing-masing subkelas. */
    public abstract String useSkill(Character target) throws aetheria.model.exception.SkillAlreadyUsedException;

    // ─── Efek status ────────────────────────────────────────────────────────

    /**
     * Menambahkan atau memperbarui efek status.
     * @param type   jenis status (BURN, POISON, ROOT)
     * @param turns  durasi dalam ronde
     */
    public void addStatus(StatusType type, int turns) {
        statuses.put(type, turns);
    }

    /** Mengecek apakah karakter memiliki status tertentu yang masih aktif. */
    public boolean hasStatus(StatusType type) {
        return statuses.containsKey(type) && statuses.get(type) > 0;
    }

    /**
     * Memproses efek status di akhir giliran pemilik.
     * BURN: 1d4 damage per tick.
     * POISON: 1d6 damage per tick.
     * ROOT: tidak damage, hanya mengurangi durasi.
     * Status yang habis durasinya dihapus (pakai Iterator).
     *
     * @return total damage yang diterima dari efek status tick ini
     */
    public int tickStatusEffects() {
        int totalDamage = 0;
        Iterator<Map.Entry<StatusType, Integer>> it = statuses.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<StatusType, Integer> entry = it.next();
            StatusType type = entry.getKey();
            int remaining   = entry.getValue();

            // Proses damage efek
            if (type == StatusType.BURN) {
                int dmg = Dice.roll(1, 4);
                takeDamage(dmg);
                totalDamage += dmg;
            } else if (type == StatusType.POISON) {
                int dmg = Dice.roll(1, 6);
                takeDamage(dmg);
                totalDamage += dmg;
            }
            // ROOT: tidak damage, durasi saja berkurang

            // Kurangi durasi; hapus bila habis
            if (remaining <= 1) {
                it.remove();
            } else {
                entry.setValue(remaining - 1);
            }
        }
        return totalDamage;
    }

    /**
     * Membersihkan semua efek status dan mengembalikan form ke NORMAL.
     * Dipanggil di akhir battle oleh BattleManager.
     */
    public void clearBattleEffects() {
        statuses.clear();
        form              = FormType.NORMAL;
        formAttackBonus   = 0;
        formDefenseBonus  = 0;
    }

    // ─── Form (Shapeshifter) ─────────────────────────────────────────────────

    /**
     * Mengubah form karakter beserta bonus attack dan defense.
     * @param newForm    form baru
     * @param atkBonus   bonus attack saat form aktif
     * @param defBonus   bonus AC saat form aktif
     */
    protected void setForm(FormType newForm, int atkBonus, int defBonus) {
        this.form             = newForm;
        this.formAttackBonus  = atkBonus;
        this.formDefenseBonus = defBonus;
    }

    /**
     * Helper heal bersama untuk Bard/Cleric/Druid/Paladin.
     * Menyembuhkan target sebesar base HP (tidak ada dadu di sini;
     * dadu sudah diroll oleh subkelas sebelum memanggil ini).
     *
     * @param target target yang disembuhkan
     * @param base   jumlah HP yang dipulihkan
     * @return jumlah HP yang benar-benar dipulihkan
     */
    protected int healBy(Character target, int base) {
        return target.heal(Math.max(0, base));
    }

    @Override
    public String toString() {
        return name + " [HP: " + hp + "/" + maxHp + "]";
    }
}
