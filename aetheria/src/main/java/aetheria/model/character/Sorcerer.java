package aetheria.model.character;

import aetheria.model.ability.SpellCaster;
import aetheria.model.ability.StatusInflictor;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.model.enums.StatusType;
import aetheria.util.Dice;

/**
 * Sorcerer: penyihir khaos yang memanfaatkan energi tidak terprediksi.
 * Skill aktif: Chaos Bolt - memberikan damage khaos + status BURN ke target.
 */
public class Sorcerer extends Hero implements SpellCaster, StatusInflictor {

    private static final String SKILL_NAME = "Chaos Bolt";

    public Sorcerer(String name, HeroRace race) {
        super(name, HeroClass.SORCERER, race);
    }

    @Override
    public String getSkillName() { return SKILL_NAME; }

    /**
     * Chaos Bolt: ledakan energi khaos berbasis CHA dengan efek BURN 2 ronde.
     * Tier0: 2d8+CHA dmg
     * Tier1: 4d8+CHA dmg
     * Tier2: 6d8+CHA dmg
     */
    @Override
    protected String performSkill(Character target) {
        int[] diceCounts = {2, 4, 6};
        int damage = Dice.roll(diceCounts[levelTier()], 8) + primaryModifier();
        int actual = target.takeDamage(damage);
        target.addStatus(StatusType.BURN, 2);

        return getName() + " menggunakan Chaos Bolt pada " + target.getName()
             + " - damage: " + actual
             + "; target terbakar selama 2 ronde";
    }

    /**
     * Melempar mantra sihir biasa: 2d6 + primaryModifier damage ke target.
     */
    @Override
    public String castSpell(Character target) {
        int damage = Dice.roll(2, 6) + primaryModifier();
        int actual = target.takeDamage(damage);
        return getName() + " melempar mantra khaos pada " + target.getName()
             + " - damage: " + actual;
    }

    /**
     * Menerapkan BURN 2 ronde ke target tanpa damage tambahan.
     */
    @Override
    public String applyStatus(Character target) {
        target.addStatus(StatusType.BURN, 2);
        return getName() + " menyulut api pada " + target.getName()
             + " - target terbakar selama 2 ronde";
    }

    @Override
    protected void applyLevelUpGrowth() { }
}
