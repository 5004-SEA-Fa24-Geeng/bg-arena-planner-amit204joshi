package student;

public class ConditionComponents {
    final GameData field;
    final String operator;
    final String value;

    ConditionComponents(GameData field, String operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(value + " is not an integer");
        }
    }

    private double parseDouble(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(value + " is not a double");
        }
    }
}
