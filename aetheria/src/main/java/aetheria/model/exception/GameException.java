package aetheria.model.exception;
/** Exception checked sebagai basis semua exception game Aetheria. */
public class GameException extends Exception {
    public GameException(String message) {
        super(message);
    }
}
