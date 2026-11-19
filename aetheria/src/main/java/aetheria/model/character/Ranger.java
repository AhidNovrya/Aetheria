package aetheria.model.character;

import aetheria.model.ability.PhysicalSkillUser;
import aetheria.model.ability.SpellCaster;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.model.enums.StatusType;
import aetheria.util.Dice;

/**
 * Ranger: pemburu jitu yang menggabungkan serangan presisi dan mantra alam.
 * Skill aktif: Precise Shot — memberikan damage + status BURN ke target.
 */
public class Ranger extends Hero implements SpellCaster, PhysicalSkillUser {

    private static final String SKILL_NAME = "Precise Shot";

    public Ranger(String name, HeroRace race) {
        super(name, HeroClass.RANGER, race);
    }

    @Override
    public String getSkillName() { return SKILL_NAME; }

    /**
     * Precise Shot: serangan presisi berbasis DEX dengan efek BURN.
     * Tier0: 2d8+DEX dmg, BURN 2 ronde
     * Tier1: 3d8+DEX dmg, BURN 2 ronde
     * Tier2: 4d8+DEX dmg, BURN 3 ronde
     */
    @Override
    public String useSkill(Character target) {
        try { markSkillUsed(); }
        catch (aetheria.model.exception.SkillAlreadyUsedException e) { return e.getMessage(); }

        int tier = levelTier();
        int[] diceCounts = {2, 3, 4};
        int burnDuration = (tier == 2) ? 3 : 2;

        int damage = Dice.roll(diceCounts[tier], 8) + primaryModifier();
        int actual = target.takeDamage(damage);
        target.addStatus(StatusType.BURN, burnDuration);

        return getName() + " menggunakan Precise Shot pada " + target.getName()
             + " — damage: " + actual
             + "; target terbakar selama " + burnDuration + " ronde";
    }

    /**
     * Melempar mantra alam ringan: 1d6 damage ke target.
     */
    @Override
    public String castSpell(Character target) {
        int damage = Dice.roll(1, 6);
        int actual = target.takeDamage(damage);
        return getName() + " melempar panah sihir pada " + target.getName()
             + " — damage: " + actual;
    }

    /**
     * Skill fisik: serangan cepat ranger 1d6 damage ke target.
     */
    @Override
    public String performPhysicalSkill(Character target) {
        int damage = Dice.roll(1, 6);
        int actual = target.takeDamage(damage);
        return getName() + " melakukan serangan cepat pada " + target.getName()
             + " — damage: " + actual;
    }

    @Override
    protected void applyLevelUpGrowth() { }
}
