package aetheria.util;
import java.util.Random;
/**
 * Kelas utilitas untuk lemparan dadu D&D.
 * Semua lemparan lewat sini agar deterministik saat pengujian (setSeed).
 */
public final class Dice {
    private static Random random = new Random();

    private Dice() { /* tidak bisa diinstansiasi */ }

    /** Melempar dadu sebanyak count kali dengan sides sisi, mengembalikan total. */
    public static int roll(int count, int sides) {
        int total = 0;
        for (int i = 0; i < count; i++) {
            total += random.nextInt(sides) + 1;
        }
        return total;
    }

    /** Melempar satu dadu d20, mengembalikan 1–20. */
    public static int d20() {
        return random.nextInt(20) + 1;
    }

    /** Menetapkan seed agar lemparan bersifat deterministik (untuk pengujian). */
    public static void setSeed(long seed) {
        random = new Random(seed);
    }
}
