package student;

public interface FilterCondition {
    /**
     * Evaluates the condition for a specific game.
     *
     * @param game The board game to evaluate
     * @return true if the condition is satisfied, false otherwise
     */
    boolean check(BoardGame game);
}
