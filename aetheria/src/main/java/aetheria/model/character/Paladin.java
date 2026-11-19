package aetheria.model.character;

import aetheria.model.ability.Healer;
import aetheria.model.ability.PhysicalSkillUser;
import aetheria.model.ability.SpellCaster;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.util.Dice;

/**
 * Paladin: kesatria suci yang menggabungkan smite ilahi, skill fisik, dan penyembuhan.
 * Skill aktif: Divine Smite — memberikan damage ke target dan menyembuhkan diri sendiri.
 */
public class Paladin extends Hero implements SpellCaster, PhysicalSkillUser, Healer {

    private static final String SKILL_NAME = "Divine Smite";

    public Paladin(String name, HeroRace race) {
        super(name, HeroClass.PALADIN, race);
    }

    @Override
    public String getSkillName() { return SKILL_NAME; }

    /**
     * Divine Smite: memberikan damage smite + holy ke target,
     * lalu menyembuhkan diri sendiri berdasarkan level tier.
     * Tier0: (2d8+1d8) dmg, 1d6+CHA heal diri
     * Tier1: (3d8+2d8) dmg, 1d8+CHA heal diri
     * Tier2: (4d8+2d8) dmg, 2d8+CHA heal diri
     */
    @Override
    protected String performSkill(Character target) {
        int tier = levelTier();
        int damage;
        int selfHeal;

        if (tier == 0) {
            damage   = Dice.roll(2, 8) + Dice.roll(1, 8);
            selfHeal = Dice.roll(1, 6) + primaryModifier();
        } else if (tier == 1) {
            damage   = Dice.roll(3, 8) + Dice.roll(2, 8);
            selfHeal = Dice.roll(1, 8) + primaryModifier();
        } else {
            damage   = Dice.roll(4, 8) + Dice.roll(2, 8);
            selfHeal = Dice.roll(2, 8) + primaryModifier();
        }

        int actualDamage = target.takeDamage(damage);
        int actualHeal   = healBy(this, selfHeal);

        return getName() + " menggunakan Divine Smite pada " + target.getName()
             + " — damage: " + actualDamage
             + "; menyembuhkan diri sendiri: +" + actualHeal + " HP";
    }

    /**
     * Melempar mantra biasa: 1d6 + primaryModifier damage ke target.
     */
    @Override
    public String castSpell(Character target) {
        int damage = Dice.roll(1, 6) + primaryModifier();
        int actual = target.takeDamage(damage);
        return getName() + " melempar mantra suci pada " + target.getName()
             + " — damage: " + actual;
    }

    /**
     * Skill fisik: serangan senjata paladin 1d8 + primaryModifier.
     */
    @Override
    public String performPhysicalSkill(Character target) {
        int damage = Dice.roll(1, 8) + primaryModifier();
        int actual = target.takeDamage(damage);
        return getName() + " melakukan serangan fisik suci pada " + target.getName()
             + " — damage: " + actual;
    }

    /**
     * Menyembuhkan target sebesar 1d8 + primaryModifier HP.
     */
    @Override
    public String healTarget(Character target) {
        int amount = Dice.roll(1, 8) + primaryModifier();
        int actual = healBy(target, amount);
        return getName() + " menyembuhkan " + target.getName()
             + " sebesar +" + actual + " HP";
    }

    @Override
    protected void applyLevelUpGrowth() { }
}
