package aetheria.model.ability;
import aetheria.model.character.Character;
/** Interface untuk hero/monster yang bisa menyerap HP target. */
public interface LifeStealer {
    String drainLife(Character target);
}
