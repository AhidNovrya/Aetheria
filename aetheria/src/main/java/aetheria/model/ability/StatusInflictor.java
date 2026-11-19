package aetheria.model.ability;
import aetheria.model.character.Character;
/** Interface untuk hero/monster yang bisa menerapkan efek status. */
public interface StatusInflictor {
    String applyStatus(Character target);
}
