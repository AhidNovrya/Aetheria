package aetheria.model.character;

import aetheria.model.ability.Healer;
import aetheria.model.ability.SpellCaster;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.util.Dice;

/** Bard: pendukung yang bisa menyerang dan menyembuhkan. */
public class Bard extends Hero implements SpellCaster, Healer {

    private static final String SKILL_NAME = "Inspiring Tune";

    public Bard(String name, HeroRace race) {
        super(name, HeroClass.BARD, race);
    }

    @Override public String getSkillName() { return SKILL_NAME; }

    @Override
    protected String performSkill(Character target) {
        return healTarget(target);
    }

    @Override
    public String castSpell(Character target) {
        int damage = Dice.roll(1, 6) + primaryModifier();
        int actual = target.takeDamage(damage);
        return getName() + " menyerang " + target.getName() + " dengan sihir Bard — damage: " + actual;
    }

    @Override
    public String healTarget(Character target) {
        int[] counts = {2, 3, 4};
        int amount = Dice.roll(counts[levelTier()], 6) + primaryModifier();
        int actual = healBy(target, amount);
        return getName() + " menggunakan Inspiring Tune pada " + target.getName()
             + " — heal: " + actual;
    }

    @Override
    protected void applyLevelUpGrowth() { }
}
