package aetheria.model.character;

import aetheria.model.ability.PhysicalSkillUser;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.util.Dice;

/** Barbarian: warrior bertenaga tinggi dengan Raging Strike. */
public class Barbarian extends Hero implements PhysicalSkillUser {

    private static final String SKILL_NAME = "Raging Strike";

    public Barbarian(String name, HeroRace race) {
        super(name, HeroClass.BARBARIAN, race);
    }

    @Override public String getSkillName() { return SKILL_NAME; }

    @Override
    protected String performSkill(Character target) {
        return performPhysicalSkill(target);
    }

    @Override
    public String performPhysicalSkill(Character target) {
        int[] counts = {2, 3, 4};
        int damage = Dice.roll(counts[levelTier()], 6) + primaryModifier();
        int actual = target.takeDamage(damage);
        return getName() + " menggunakan Raging Strike pada " + target.getName()
             + " — damage: " + actual;
    }

    @Override
    protected void applyLevelUpGrowth() { }
}
