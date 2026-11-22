package aetheria.model.character;

import aetheria.model.ability.Healer;
import aetheria.model.ability.SpellCaster;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.util.Dice;

/** Cleric: penyembuh suci. */
public class Cleric extends Hero implements SpellCaster, Healer {

    private static final String SKILL_NAME = "Healing Word";

    public Cleric(String name, HeroRace race) {
        super(name, HeroClass.CLERIC, race);
    }

    @Override public String getSkillName() { return SKILL_NAME; }

    @Override
    protected String performSkill(Character target) {
        return healTarget(target);
    }

    @Override
    public String castSpell(Character target) {
        int damage = Dice.roll(1, 8) + primaryModifier();
        int actual = target.takeDamage(damage);
        return getName() + " menyerang " + target.getName() + " dengan Sacred Flame - damage: " + actual;
    }

    @Override
    public String healTarget(Character target) {
        int[] counts = {2, 3, 4};
        int amount = Dice.roll(counts[levelTier()], 8) + primaryModifier();
        int actual = healBy(target, amount);
        return getName() + " menggunakan Healing Word pada " + target.getName()
             + " - heal: " + actual;
    }

    @Override
    protected void applyLevelUpGrowth() { }
}
