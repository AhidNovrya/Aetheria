package aetheria.model.character;

import aetheria.model.ability.PhysicalSkillUser;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.util.Dice;

/** Fighter: prajurit serbaguna. */
public class Fighter extends Hero implements PhysicalSkillUser {

    private static final String SKILL_NAME = "Power Strike";

    public Fighter(String name, HeroRace race) {
        super(name, HeroClass.FIGHTER, race);
    }

    @Override public String getSkillName() { return SKILL_NAME; }

    @Override
    public String useSkill(Character target) {
        try { markSkillUsed(); }
        catch (aetheria.model.exception.SkillAlreadyUsedException e) {
            return e.getMessage();
        }
        return performPhysicalSkill(target);
    }

    @Override
    public String performPhysicalSkill(Character target) {
        int[] counts = {2, 3, 4};
        int damage = Dice.roll(counts[levelTier()], 8) + primaryModifier();
        int actual = target.takeDamage(damage);
        return getName() + " menggunakan Power Strike pada " + target.getName()
             + " — damage: " + actual;
    }

    @Override
    protected void applyLevelUpGrowth() { }
}
