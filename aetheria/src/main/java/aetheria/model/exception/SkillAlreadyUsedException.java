package aetheria.model.exception;
/** Dilempar Hero.markSkillUsed() bila skill sudah dipakai dalam battle ini. */
public class SkillAlreadyUsedException extends GameException {
    private static final long serialVersionUID = 1L;

    public SkillAlreadyUsedException(String skillName) {
        super("Skill '" + skillName + "' sudah dipakai dalam battle ini.");
    }
}
