package aetheria.model.ability;
import aetheria.model.character.Character;
/** Interface untuk hero/monster yang bisa melempar mantra. */
public interface SpellCaster {
    String castSpell(Character target);
}
