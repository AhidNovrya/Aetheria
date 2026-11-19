package aetheria.model.item;
// STUB - dikerjakan anggota lain (Lutfi)
/**
 * Stub Inventory agar kode Hero dapat dikompilasi.
 * Implementasi lengkap dikerjakan Lutfi.
 */
public class Inventory {
    /** Mengembalikan bonus attack dari weapon yang terpasang (0 = tidak ada weapon). */
    public int getWeaponBonus() {
        return 0; // STUB
    }

    /**
     * Mengembalikan damage dice weapon yang terpasang.
     * Format: {jumlah_dadu, jumlah_sisi}. Default 1d4 bila tidak ada weapon.
     */
    public int[] getEquippedDamageDice() {
        return new int[]{1, 4}; // STUB: default 1d4 tanpa weapon
    }
}
