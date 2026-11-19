package aetheria.model.character;

import aetheria.model.ability.PhysicalSkillUser;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.util.Dice;

/** Monk: petarung gesit dengan tiga serangan cepat. */
public class Monk extends Hero implements PhysicalSkillUser {

    private static final String SKILL_NAME = "Flurry of Blows";

    public Monk(String name, HeroRace race) {
        super(name, HeroClass.MONK, race);
    }

    @Override public String getSkillName() { return SKILL_NAME; }

    @Override
    protected String performSkill(Character target) {
        return performPhysicalSkill(target);
    }

    @Override
    public String performPhysicalSkill(Character target) {
        int[] sides = {6, 8, 10};
        int dieSides = sides[levelTier()];
        StringBuilder log = new StringBuilder(getName() + " menggunakan Flurry of Blows:");
        int totalDamage = 0;
        for (int i = 1; i <= 3; i++) {
            int damage = Dice.roll(1, dieSides) + primaryModifier();
            int actual = target.takeDamage(damage);
            totalDamage += actual;
            log.append(" [Serangan ").append(i).append(": ").append(actual).append("]");
        }
        log.append(" Total: ").append(totalDamage);
        return log.toString();
    }

    @Override
    protected void applyLevelUpGrowth() { }
}
