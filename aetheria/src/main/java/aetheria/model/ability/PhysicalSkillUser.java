package aetheria.model.ability;
import aetheria.model.character.Character;
/** Interface untuk hero/monster yang menggunakan skill fisik. */
public interface PhysicalSkillUser {
    String performPhysicalSkill(Character target);
}
