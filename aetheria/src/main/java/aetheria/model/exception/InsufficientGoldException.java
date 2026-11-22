package aetheria.model.exception;
/** Dilempar Hero.spendGold() bila gold tidak cukup. */
public class InsufficientGoldException extends GameException {
    private static final long serialVersionUID = 1L;

    private final int required;
    private final int owned;

    public InsufficientGoldException(int required, int owned) {
        super("Gold tidak cukup: diperlukan " + required + ", dimiliki " + owned);
        this.required = required;
        this.owned = owned;
    }

    public int getRequired() { return required; }
    public int getOwned()    { return owned;    }
}
