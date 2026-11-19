package aetheria.model.character;

import aetheria.model.ability.SpellCaster;
import aetheria.model.ability.StatusInflictor;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.model.enums.StatusType;
import aetheria.util.Dice;

/**
 * Wizard: penyihir buku sihir yang menguasai ilmu api dahsyat.
 * Skill aktif: Fireball — memberikan damage api + status BURN 3 ronde ke target.
 */
public class Wizard extends Hero implements SpellCaster, StatusInflictor {

    private static final String SKILL_NAME = "Fireball";

    public Wizard(String name, HeroRace race) {
        super(name, HeroClass.WIZARD, race);
    }

    @Override
    public String getSkillName() { return SKILL_NAME; }

    /**
     * Fireball: bola api dahsyat berbasis INT dengan efek BURN 3 ronde.
     * Tier0: 4d6+INT dmg
     * Tier1: 6d6+INT dmg
     * Tier2: 8d6+INT dmg
     */
    @Override
    protected String performSkill(Character target) {
        int[] diceCounts = {4, 6, 8};
        int damage = Dice.roll(diceCounts[levelTier()], 6) + primaryModifier();
        int actual = target.takeDamage(damage);
        target.addStatus(StatusType.BURN, 3);

        return getName() + " menggunakan Fireball pada " + target.getName()
             + " — damage: " + actual
             + "; target terbakar selama 3 ronde";
    }

    /**
     * Melempar mantra biasa: 2d6 + primaryModifier damage ke target.
     */
    @Override
    public String castSpell(Character target) {
        int damage = Dice.roll(2, 6) + primaryModifier();
        int actual = target.takeDamage(damage);
        return getName() + " melempar mantra sihir pada " + target.getName()
             + " — damage: " + actual;
    }

    /**
     * Menerapkan BURN 3 ronde ke target tanpa damage tambahan.
     */
    @Override
    public String applyStatus(Character target) {
        target.addStatus(StatusType.BURN, 3);
        return getName() + " menyalakan api pada " + target.getName()
             + " — target terbakar selama 3 ronde";
    }

    @Override
    protected void applyLevelUpGrowth() { }
}
