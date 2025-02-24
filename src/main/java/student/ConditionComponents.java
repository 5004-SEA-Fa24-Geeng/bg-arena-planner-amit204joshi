package student;

public class ConditionComponents {
    /**
     * The field to filter on (e.g., GameData.MIN_PLAYERS, GameData.RATING).
     */
    private final GameData field;

    /**
     * The operator to use for comparison (e.g., ">", "<=", "~=").
     */
    private final String operator;

    /**
     * The value to compare against (e.g., "4", "7.5", "Pandemic").
     */
    private final String value;

    /**
     * Constructs a new ConditionComponents object with the specified field, operator, and value.
     *
     * @param field    The field to filter on (e.g., GameData.MIN_PLAYERS)
     * @param operator The comparison operator (e.g., ">", "<=")
     * @param value    The value to compare against (e.g., "4", "7.5")
     */
    ConditionComponents(GameData field, String operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    /**
     * Parses a string value into an integer.
     *
     * @param value The string to parse (e.g., "4")
     * @return The parsed integer value
     * @throws IllegalArgumentException If the string cannot be parsed as an integer
     */
    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(value + " is not an integer");
        }
    }

    /**
     * Parses a string value into a double.
     *
     * @param value The string to parse (e.g., "7.5")
     * @return The parsed double value
     * @throws IllegalArgumentException If the string cannot be parsed as a double
     */
    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(value + " is not a double");
        }
    }

    /**
     * Get the field of the condition.
     *
     * @return the field of the condition
     */
    public GameData getField() {
        return field;
    }

    /**
     * Get the operator of the condition.
     *
     * @return the operator of the condition
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Get the value of the condition.
     *
     * @return the value of the condition
     */
    public String getValue() {
        return value;
    }
}
