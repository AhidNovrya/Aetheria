package aetheria.model.character;

import aetheria.model.ability.PhysicalSkillUser;
import aetheria.model.ability.StatusInflictor;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.model.enums.StatusType;
import aetheria.util.Dice;

/**
 * Rogue: pencuri bayangan yang menyerang dari kegelapan dengan racun mematikan.
 * Skill aktif: Sneak Attack — memberikan damage + status POISON ke target.
 */
public class Rogue extends Hero implements PhysicalSkillUser, StatusInflictor {

    private static final String SKILL_NAME = "Sneak Attack";

    public Rogue(String name, HeroRace race) {
        super(name, HeroClass.ROGUE, race);
    }

    @Override
    public String getSkillName() { return SKILL_NAME; }

    /**
     * Sneak Attack: serangan gelap berbasis DEX dengan efek POISON 3 ronde.
     * Tier0: 2d6+DEX dmg
     * Tier1: 3d6+DEX dmg
     * Tier2: 4d6+DEX dmg
     */
    @Override
    protected String performSkill(Character target) {
        int[] diceCounts = {2, 3, 4};
        int damage = Dice.roll(diceCounts[levelTier()], 6) + primaryModifier();
        int actual = target.takeDamage(damage);
        target.addStatus(StatusType.POISON, 3);

        return getName() + " menggunakan Sneak Attack pada " + target.getName()
             + " — damage: " + actual
             + "; target diracuni selama 3 ronde";
    }

    /**
     * Skill fisik: serangan tikaman cepat 1d6 damage ke target.
     */
    @Override
    public String performPhysicalSkill(Character target) {
        int damage = Dice.roll(1, 6);
        int actual = target.takeDamage(damage);
        return getName() + " melakukan tikaman cepat pada " + target.getName()
             + " — damage: " + actual;
    }

    /**
     * Menerapkan POISON 3 ronde ke target tanpa damage tambahan.
     */
    @Override
    public String applyStatus(Character target) {
        target.addStatus(StatusType.POISON, 3);
        return getName() + " mengoleskan racun pada " + target.getName()
             + " — target diracuni selama 3 ronde";
    }

    @Override
    protected void applyLevelUpGrowth() { }
}
