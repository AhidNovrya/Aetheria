package aetheria.model.ability;
import aetheria.model.character.Character;
/** Interface untuk hero/monster yang bisa menyembuhkan. */
public interface Healer {
    String healTarget(Character target);
}
