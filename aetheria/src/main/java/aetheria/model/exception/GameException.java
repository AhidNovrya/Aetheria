package aetheria.model.exception;
/** Exception checked sebagai basis semua exception game Aetheria. */
public class GameException extends Exception {
    private static final long serialVersionUID = 1L;

    public GameException(String message) {
        super(message);
    }
}
