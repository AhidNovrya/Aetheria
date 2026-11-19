package aetheria.model.character;

import aetheria.model.ability.LifeStealer;
import aetheria.model.ability.SpellCaster;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.util.Dice;

/**
 * Warlock: penyihir perjanjian gelap yang menyerap tenaga hidup musuh.
 * Skill aktif: Eldritch Blast — memberikan damage + menyembuhkan diri 50% dari damage.
 */
public class Warlock extends Hero implements SpellCaster, LifeStealer {

    private static final String SKILL_NAME = "Eldritch Blast";

    public Warlock(String name, HeroRace race) {
        super(name, HeroClass.WARLOCK, race);
    }

    @Override
    public String getSkillName() { return SKILL_NAME; }

    /**
     * Eldritch Blast: ledakan energi gelap yang menyerap HP target.
     * Mendelegasikan ke drainLife dengan logika tier penuh.
     */
    @Override
    public String useSkill(Character target) {
        try { markSkillUsed(); }
        catch (aetheria.model.exception.SkillAlreadyUsedException e) { return e.getMessage(); }

        return drainLife(target);
    }

    /**
     * Melempar mantra biasa: 1d8 + primaryModifier damage ke target.
     */
    @Override
    public String castSpell(Character target) {
        int damage = Dice.roll(1, 8) + primaryModifier();
        int actual = target.takeDamage(damage);
        return getName() + " melempar mantra gelap pada " + target.getName()
             + " — damage: " + actual;
    }

    /**
     * Eldritch Blast: damage ke target, heal diri 50% dari damage (rounded down).
     * Tier0: 2d10+CHA dmg
     * Tier1: 3d10+CHA dmg
     * Tier2: 4d10+CHA dmg
     */
    @Override
    public String drainLife(Character target) {
        int[] diceCounts = {2, 3, 4};
        int damage = Dice.roll(diceCounts[levelTier()], 10) + primaryModifier();
        int actual = target.takeDamage(damage);
        int selfHeal = actual / 2; // 50% dari damage aktual, rounded down
        healBy(this, selfHeal);

        return getName() + " menggunakan Eldritch Blast pada " + target.getName()
             + " — damage: " + actual
             + "; menyerap tenaga hidup: +" + selfHeal + " HP";
    }

    @Override
    protected void applyLevelUpGrowth() { }
}
