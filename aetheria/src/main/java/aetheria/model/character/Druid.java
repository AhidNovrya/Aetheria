package aetheria.model.character;

import aetheria.model.ability.Healer;
import aetheria.model.ability.Shapeshifter;
import aetheria.model.ability.SpellCaster;
import aetheria.model.enums.FormType;
import aetheria.model.enums.HeroClass;
import aetheria.model.enums.HeroRace;
import aetheria.util.Dice;

/** Druid: penjaga alam yang bisa berubah bentuk dan menyembuhkan. */
public class Druid extends Hero implements SpellCaster, Healer, Shapeshifter {

    private static final String SKILL_NAME = "Wild Shape";

    public Druid(String name, HeroRace race) {
        super(name, HeroClass.DRUID, race);
    }

    @Override public String getSkillName() { return SKILL_NAME; }

    @Override
    public String useSkill(Character target) {
        try { markSkillUsed(); }
        catch (aetheria.model.exception.SkillAlreadyUsedException e) {
            return e.getMessage();
        }
        String transformLog = transform();
        int[] counts = {1, 2, 2};
        int amount = Dice.roll(counts[levelTier()], 8) + primaryModifier();
        int actual = healBy(this, amount);
        return transformLog + " | " + getName() + " memulihkan " + actual + " HP";
    }

    @Override
    public String castSpell(Character target) {
        int damage = Dice.roll(1, 8) + primaryModifier();
        int actual = target.takeDamage(damage);
        return getName() + " menyerang " + target.getName() + " dengan Thorn Whip — damage: " + actual;
    }

    @Override
    public String healTarget(Character target) {
        int amount = Dice.roll(1, 8) + primaryModifier();
        int actual = healBy(target, amount);
        return getName() + " menyembuhkan " + target.getName() + " — heal: " + actual;
    }

    @Override
    public String transform() {
        setForm(FormType.BEAST, 2, 2);
        return getName() + " berubah menjadi BEAST form! (+2 ATK, +2 AC)";
    }

    @Override
    public String revertForm() {
        setForm(FormType.NORMAL, 0, 0);
        return getName() + " kembali ke form NORMAL.";
    }

    @Override
    protected void applyLevelUpGrowth() { }
}
