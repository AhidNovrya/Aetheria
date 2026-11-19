package aetheria.util;
/**
 * Metode statis validasi input untuk konstruktor dan method model.
 * Melempar IllegalArgumentException bila validasi gagal.
 */
public final class Validator {
    private Validator() { /* tidak bisa diinstansiasi */ }

    /** Memastikan String tidak null dan tidak kosong/blank. */
    public static String requireNonBlank(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Field '" + field + "' tidak boleh kosong.");
        }
        return value;
    }

    /** Memastikan nilai integer tidak negatif. */
    public static int requireNonNegative(int value, String field) {
        if (value < 0) {
            throw new IllegalArgumentException("Field '" + field + "' tidak boleh negatif: " + value);
        }
        return value;
    }

    /** Memastikan nilai tidak null. */
    public static <T> T requireNonNull(T value, String field) {
        if (value == null) {
            throw new IllegalArgumentException("Field '" + field + "' tidak boleh null.");
        }
        return value;
    }

    /** Membatasi nilai dalam rentang [min, max]. */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
