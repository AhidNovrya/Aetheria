package aetheria.model.character;

import aetheria.model.enums.Ability;
import aetheria.model.enums.FormType;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.model.enums.StatusType;
import aetheria.model.exception.InsufficientGoldException;
import aetheria.model.exception.SkillAlreadyUsedException;
import aetheria.util.Dice;
import aetheria.util.DndRules;
import aetheria.util.GameConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterAndHeroTest {

    @BeforeEach
    void setUp() {
        Dice.setSeed(42L);
    }

    @Test
    @DisplayName("Semua 36 kombinasi HeroClass x HeroRace dapat dibuat tanpa error")
    void testAllThirtySixCombinations() {
        HeroRace[] races = HeroRace.values();
        HeroClass[] classes = HeroClass.values();
        int count = 0;

        for (HeroClass hc : classes) {
            for (HeroRace race : races) {
                Hero hero = createHeroByClass(hc, "Hero_" + hc.name() + "_" + race.name(), race);
                assertNotNull(hero);
                assertEquals(hc, hero.getHeroClass());
                assertEquals(race, hero.getRace());
                assertTrue(hero.getHp() > 0);
                assertEquals(hero.getMaxHp(), hero.getHp());
                assertTrue(hero.isAlive());
                assertEquals(1, hero.getLevel());
                count++;
            }
        }
        assertEquals(36, count);
    }

    private Hero createHeroByClass(HeroClass hc, String name, HeroRace race) {
        return switch (hc) {
            case BARBARIAN -> new Barbarian(name, race);
            case BARD -> new Bard(name, race);
            case CLERIC -> new Cleric(name, race);
            case DRUID -> new Druid(name, race);
            case FIGHTER -> new Fighter(name, race);
            case MONK -> new Monk(name, race);
            case PALADIN -> new Paladin(name, race);
            case RANGER -> new Ranger(name, race);
            case ROGUE -> new Rogue(name, race);
            case SORCERER -> new Sorcerer(name, race);
            case WARLOCK -> new Warlock(name, race);
            case WIZARD -> new Wizard(name, race);
        };
    }

    @Test
    @DisplayName("DndRules.modifier menghitung dengan tepat sesuai D&D 5e")
    void testDndRulesModifier() {
        assertEquals(-5, DndRules.modifier(1));
        assertEquals(-1, DndRules.modifier(8));
        assertEquals(-1, DndRules.modifier(9));
        assertEquals(0, DndRules.modifier(10));
        assertEquals(0, DndRules.modifier(11));
        assertEquals(2, DndRules.modifier(15));
        assertEquals(5, DndRules.modifier(20));
    }

    @Test
    @DisplayName("HP level 1 sesuai hit die maksimal + CON modifier akhir")
    void testStartingHpCalculation() {
        // Barbarian Dwarf: hitDie 12. Barbarian priority: STR(15), CON(14), DEX(13)...
        // Dwarf +2 CON -> CON akhir = 14 + 2 = 16 -> mod +3
        // HP = 12 + 3 = 15
        Barbarian barbDwarf = new Barbarian("Bruenor", HeroRace.DWARF);
        assertEquals(15, barbDwarf.getMaxHp());
        assertEquals(15, barbDwarf.getHp());

        // Wizard Elf: hitDie 6. Wizard priority: INT(15), CON(14), DEX(13)...
        // Elf +2 DEX -> CON tetap 14 -> mod +2
        // HP = 6 + 2 = 8
        Wizard wizElf = new Wizard("Elrond", HeroRace.ELF);
        assertEquals(8, wizElf.getMaxHp());
    }

    @Test
    @DisplayName("Bonus ras hanya diterapkan sekali dan tidak menumpuk saat membuat beberapa hero")
    void testRaceBonusesAppliedOnce() {
        Barbarian b1 = new Barbarian("B1", HeroRace.HUMAN);
        Barbarian b2 = new Barbarian("B2", HeroRace.HUMAN);
        assertEquals(b1.getMaxHp(), b2.getMaxHp());
        assertEquals(b1.getAttackBonus(), b2.getAttackBonus());
        assertEquals(b1.getArmorClass(), b2.getArmorClass());
    }

    @Test
    @DisplayName("Level-up menambah HP sebesar averageHitDie + CON modifier, proficiency naik di tier tepat")
    void testLevelUpAndProficiencyBonus() {
        Fighter fighter = new Fighter("Arthur", HeroRace.HUMAN);
        int initialMaxHp = fighter.getMaxHp();
        int initialLevel = fighter.getLevel();
        assertEquals(1, initialLevel);

        // Ambang level 2 = 300 EXP
        fighter.gainExp(300);
        assertEquals(2, fighter.getLevel());
        assertTrue(fighter.getMaxHp() > initialMaxHp);

        // Tes proficiency bonus di batas tier
        assertEquals(2, DndRules.proficiencyBonus(1));
        assertEquals(2, DndRules.proficiencyBonus(4));
        assertEquals(3, DndRules.proficiencyBonus(5));
        assertEquals(3, DndRules.proficiencyBonus(8));
        assertEquals(4, DndRules.proficiencyBonus(9));
        assertEquals(4, DndRules.proficiencyBonus(12));
        assertEquals(5, DndRules.proficiencyBonus(13));
        assertEquals(5, DndRules.proficiencyBonus(16));
        assertEquals(6, DndRules.proficiencyBonus(17));
        assertEquals(6, DndRules.proficiencyBonus(20));

        // EXP sangat besar langsung menaikkan ke level 20 (MAX_LEVEL)
        fighter.gainExp(1_000_000);
        assertEquals(GameConstants.MAX_LEVEL, fighter.getLevel());
        assertEquals(0, fighter.getExpToNextLevel());
    }

    @Test
    @DisplayName("takeDamage tidak membuat HP di bawah 0 dan heal tidak melebihi maxHp")
    void testTakeDamageAndHealBounds() {
        Paladin paladin = new Paladin("Uther", HeroRace.HUMAN);
        int maxHp = paladin.getMaxHp();

        // Damage 5 poin
        int damageTaken = paladin.takeDamage(5);
        assertEquals(5, damageTaken);
        assertEquals(maxHp - 5, paladin.getHp());
        assertTrue(paladin.isAlive());

        // Heal 3 poin
        int healed = paladin.heal(3);
        assertEquals(3, healed);
        assertEquals(maxHp - 2, paladin.getHp());

        // Overheal (melebihi maxHp)
        int overheal = paladin.heal(50);
        assertEquals(2, overheal);
        assertEquals(maxHp, paladin.getHp());

        // Damage fatal melebihi total HP
        int fatalDamage = paladin.takeDamage(maxHp + 100);
        assertEquals(maxHp, fatalDamage);
        assertEquals(0, paladin.getHp());
        assertFalse(paladin.isAlive());

        // Angka negatif melempar IllegalArgumentException dan HP tidak berubah
        Paladin p2 = new Paladin("Arthur", HeroRace.HUMAN);
        int hpBefore = p2.getHp();
        assertThrows(IllegalArgumentException.class, () -> p2.takeDamage(-1));
        assertEquals(hpBefore, p2.getHp());
        assertThrows(IllegalArgumentException.class, () -> p2.heal(-1));
        assertEquals(hpBefore, p2.getHp());
    }

    @Test
    @DisplayName("Skill kedua dalam satu battle melempar/menolak pemakaian, resetSkill memungkinkan penggunaan ulang")
    void testSkillAlreadyUsedAndReset() {
        Fighter fighter = new Fighter("Garen", HeroRace.HUMAN);
        Paladin dummy = new Paladin("Dummy", HeroRace.DWARF);

        assertFalse(fighter.isSkillUsed());
        assertDoesNotThrow(() -> {
            String log1 = fighter.useSkill(dummy);
            assertNotNull(log1);
        });
        assertTrue(fighter.isSkillUsed());

        // Percobaan pemakaian kedua melempar SkillAlreadyUsedException
        assertThrows(SkillAlreadyUsedException.class, () -> fighter.useSkill(dummy));

        // markSkillUsed langsung melempar SkillAlreadyUsedException
        assertThrows(SkillAlreadyUsedException.class, fighter::markSkillUsed);

        // Reset
        fighter.resetSkill();
        assertFalse(fighter.isSkillUsed());
        assertDoesNotThrow(() -> {
            String log3 = fighter.useSkill(dummy);
            assertNotNull(log3);
        });
        assertTrue(fighter.isSkillUsed());
    }

    @Test
    @DisplayName("spendGold melebihi saldo melempar InsufficientGoldException")
    void testSpendGoldException() {
        Rogue rogue = new Rogue("Valeera", HeroRace.ELF);
        assertEquals(0, rogue.getGold());

        assertThrows(InsufficientGoldException.class, () -> rogue.spendGold(10));

        rogue.addGold(50);
        assertEquals(50, rogue.getGold());

        assertDoesNotThrow(() -> rogue.spendGold(30));
        assertEquals(20, rogue.getGold());

        assertThrows(InsufficientGoldException.class, () -> rogue.spendGold(25));
        assertEquals(20, rogue.getGold());
    }

    @Test
    @DisplayName("Status kedaluwarsa terhapus dan clearBattleEffects mengembalikan form ke NORMAL")
    void testStatusExpiryAndClearBattleEffects() throws Exception {
        Druid druid = new Druid("Malfurion", HeroRace.ELF);
        Paladin target = new Paladin("Target", HeroRace.HUMAN);

        // Druid transform ke BEAST
        druid.useSkill(target);
        assertEquals(FormType.BEAST, druid.getForm());

        // Tambah status BURN 2 ronde
        druid.addStatus(StatusType.BURN, 2);
        assertTrue(druid.hasStatus(StatusType.BURN));

        // Tick 1
        druid.tickStatusEffects();
        assertTrue(druid.hasStatus(StatusType.BURN));

        // Tick 2: kedaluwarsa
        druid.tickStatusEffects();
        assertFalse(druid.hasStatus(StatusType.BURN));

        // clearBattleEffects
        druid.addStatus(StatusType.POISON, 3);
        druid.clearBattleEffects();
        assertFalse(druid.hasStatus(StatusType.POISON));
        assertEquals(FormType.NORMAL, druid.getForm());
    }

    @Test
    @DisplayName("useSkill tiap hero mengembalikan String non-kosong")
    void testUseSkillReturnsNonEmptyStringForAllHeroes() {
        HeroRace race = HeroRace.HUMAN;
        Hero[] heroes = {
            new Barbarian("Barb", race),
            new Bard("Bard", race),
            new Cleric("Cleric", race),
            new Druid("Druid", race),
            new Fighter("Fighter", race),
            new Monk("Monk", race),
            new Paladin("Paladin", race),
            new Ranger("Ranger", race),
            new Rogue("Rogue", race),
            new Sorcerer("Sorcerer", race),
            new Warlock("Warlock", race),
            new Wizard("Wizard", race)
        };

        Paladin dummyTarget = new Paladin("Dummy", HeroRace.DWARF);

        for (Hero h : heroes) {
            assertDoesNotThrow(() -> {
                String log = h.useSkill(dummyTarget);
                assertNotNull(log);
                assertFalse(log.isBlank(), "useSkill() dari " + h.getHeroClass() + " tidak boleh blank");
                assertFalse(log.contains("—"), "Log tidak boleh mengandung em dash: " + log);
            });
        }
    }

    @Test
    @DisplayName("Setelah rangkaian takeDamage dan heal acak, 0 <= hp <= maxHp selalu terpenuhi")
    void testRandomDamageAndHealBounds() {
        Dice.setSeed(999L);
        Paladin paladin = new Paladin("RandomTest", HeroRace.HUMAN);
        int maxHp = paladin.getMaxHp();

        for (int i = 0; i < 100; i++) {
            int dmg = Dice.roll(1, 20);
            paladin.takeDamage(dmg);
            assertTrue(paladin.getHp() >= 0 && paladin.getHp() <= maxHp);

            int healAmt = Dice.roll(1, 15);
            paladin.heal(healAmt);
            assertTrue(paladin.getHp() >= 0 && paladin.getHp() <= maxHp);
        }
    }

    @Test
    @DisplayName("Paladin primaryAbility adalah STR dan heal diri memakai modifier CHA")
    void testPaladinPrimaryAndHeal() throws Exception {
        Paladin p = new Paladin("Pally", HeroRace.HUMAN);
        assertEquals(Ability.STR, p.getHeroClass().getPrimaryAbility());

        // Human: Standard array STR 15 + 1 = 16 (mod +3). CHA 14 + 1 = 15 (mod +2).
        // Hit die Paladin d10. CON 13 + 1 = 14 (mod +2). HP awal = 10 + 2 = 12.
        p.takeDamage(10); // HP jadi 2/12
        assertEquals(2, p.getHp());

        // Tier 0 performSkill:
        // 1) damage = Dice.roll(2, 8) + Dice.roll(1, 8)
        // 2) selfHeal = Dice.roll(1, 6) + modifierOf(CHA)
        // Hitung roll berurutan dengan seed tetap:
        Dice.setSeed(100L);
        Dice.roll(2, 8); // smite roll
        Dice.roll(1, 8); // holy roll
        int healRoll = Dice.roll(1, 6); // selfHeal roll

        Dice.setSeed(100L); // reset seed agar pemanggilan di useSkill identik
        p.useSkill(new Barbarian("Dummy", HeroRace.DWARF));
        int expectedHeal = healRoll + 2; // mod CHA = +2
        assertEquals(Math.min(12, 2 + expectedHeal), p.getHp());
    }

    @Test
    @DisplayName("Hero dengan race null atau heroClass null melempar IllegalArgumentException")
    void testNullHeroClassAndRace() {
        assertThrows(IllegalArgumentException.class, () -> new Fighter("X", null));
        assertThrows(IllegalArgumentException.class, () -> new Hero("X", null, HeroRace.HUMAN) {
            @Override public String getSkillName() { return "test"; }
            @Override protected String performSkill(Character t) { return "test"; }
            @Override protected void applyLevelUpGrowth() {}
        });
    }

    @Test
    @DisplayName("gainExp(-5) melempar IllegalArgumentException bahkan di level maksimum")
    void testGainNegativeExp() {
        Fighter f = new Fighter("F", HeroRace.HUMAN);
        assertThrows(IllegalArgumentException.class, () -> f.gainExp(-5));

        f.gainExp(1_000_000);
        assertEquals(GameConstants.MAX_LEVEL, f.getLevel());
        assertThrows(IllegalArgumentException.class, () -> f.gainExp(-5));
    }

    @Test
    @DisplayName("getModifier, getProficiencyBonus, dan getAbilityScoreMap valid dan map tidak dapat dimodifikasi")
    void testCharacterViewAccessors() {
        Character c = new Fighter("ViewTester", HeroRace.HUMAN);
        assertEquals(2, c.getProficiencyBonus());
        // Human Fighter: STR 15 + 1 = 16 -> mod +3
        assertEquals(3, c.getModifier(Ability.STR));
        assertEquals(16, c.getAbilityScore(Ability.STR));

        var map = c.getAbilityScoreMap();
        assertNotNull(map);
        assertEquals(16, map.get(Ability.STR));
        assertThrows(UnsupportedOperationException.class, () -> map.put(Ability.STR, 20));
    }
}
