package aetheria.util;
/** Konstanta global game Aetheria. */
public final class GameConstants {
    public static final int MAX_INVENTORY     = 10;
    public static final int MAX_LEVEL         = 20;
    public static final int MAX_ACTIVE_QUEST  = 1;
    public static final double DROP_CHANCE    = 0.3;
    /** Standard array D&D 5e untuk distribusi skor kemampuan (urutan tertinggi ke terendah). */
    public static final int[] STANDARD_ARRAY  = {15, 14, 13, 12, 10, 8};

    private GameConstants() { /* tidak bisa diinstansiasi */ }
}
