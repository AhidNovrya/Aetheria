package aetheria.model.exception;
/** Dilempar Hero.markSkillUsed() bila skill sudah dipakai dalam battle ini. */
public class SkillAlreadyUsedException extends GameException {
    public SkillAlreadyUsedException(String skillName) {
        super("Skill '" + skillName + "' sudah dipakai dalam battle ini.");
    }
}
