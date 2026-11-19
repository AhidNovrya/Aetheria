package aetheria.model.ability;
/** Interface untuk karakter yang bisa menerima damage. */
public interface Damageable {
    int takeDamage(int damage);
    boolean isAlive();
}
