package student;

import java.util.*;
import java.util.stream.Stream;


public class Planner implements IPlanner {
    /**
     * Master list of all available games
     */
    private final List<BoardGame> allGames;

    /**
     * Active filter conditions
     */
    private final List<FilterCondition> activeConditions = new ArrayList<>();

    /**
     * Current sorting field (default: NAME)
     */
    private GameData currentSortField = GameData.NAME;

    /**
     * Sorting direction (default: ascending)
     */
    private boolean isAscending = true;

    /**
     * Initializes the planner with a collection of board games.
     *
     * @param games The original set of board games
     */
    public Planner(Set<BoardGame> games) {
        this.allGames = new ArrayList<>(games);
    }

    /**
     * Filters games using the provided filter string and default sorting (name, ascending).
     *
     * @param filter The filter string (e.g., "minPlayers>4,rating>=7.5")
     * @return A stream of filtered and sorted games
     */
    @Override
    public Stream<BoardGame> filter(String filter) {
        return filter(filter, GameData.NAME, true);
    }

    /**
     * Filters games using the provided filter string and specified sorting field (ascending).
     *
     * @param filter The filter string
     * @param sortOn The field to sort by
     * @return A stream of filtered and sorted games
     */
    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn) {
        return filter(filter, sortOn, true);
    }

    /**
     * Filters games using the provided filter string, sorting field, and sorting direction.
     *
     * @param filter    The filter string
     * @param sortOn    The field to sort by
     * @param ascending The sorting direction (true for ascending, false for descending)
     * @return A stream of filtered and sorted games
     */
    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn, boolean ascending) {
        parseFilterConditions(filter); // Parse and add new conditions
        updateSortParams(sortOn, ascending); // Update sorting parameters
        return processFilteredGames(); // Apply filters and sorting
    }

    /**
     * Resets the planner to its initial state (no filters, default sorting).
     */
    @Override
    public void reset() {
        activeConditions.clear();
        currentSortField = GameData.NAME;
        isAscending = true;
    }

    /**
     * Parses a filter string into individual conditions and adds them to the active conditions list.
     *
     * @param filter The filter string
     */
    private void parseFilterConditions(String filter) {
        // return if empty filter
        if (filter == null || filter.trim().isEmpty()) {
            return;
        }
        // split conditions
        String[] conditions = filter.split(",");
        for (String rawCondition : conditions) {
            // trim and replace white space
            String condition = rawCondition.trim();
            if (condition.isEmpty()) {
                continue;
            }
            // Get three components of condition
            ConditionComponents components = parseConditionComponents(condition);
            activeConditions.add(createCondition(components));
        }
    }

    /**
     * Parses a single condition into its components (field, operator, value).
     *
     * @param condition The condition string
     * @return A ConditionComponents object containing the parsed components
     * @throws IllegalArgumentException If the condition is invalid
     */
    private ConditionComponents parseConditionComponents(String condition) {
        String[] operators = {">=", "<=", "!=", "==", "~=", ">", "<", "="};
        for (String operator : operators) {
            int index = condition.indexOf(operator);
            if (index != -1) {
                String fieldPart = condition.substring(0, index).trim();
                String value = condition.substring(index + operator.length()).trim();
                try {
                    GameData field = GameData.fromString(fieldPart);
                    return new ConditionComponents(field, operator, value);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid field: " + fieldPart);
                }
            }
        }
        throw new IllegalArgumentException("Invalid condition: " + condition);
    }

    /**
     * Creates a FilterCondition based on the parsed components.
     *
     * @param components The parsed condition components
     * @return A FilterCondition that can evaluate the condition
     * @throws IllegalArgumentException If the field or operator is invalid
     */
    private FilterCondition createCondition(ConditionComponents components) {
        GameData field = components.field;
        String operator = components.operator;
        String value = components.value;

        return switch (field) {
            case NAME -> handleStringCondition(operator, value);
            case MIN_PLAYERS, MAX_PLAYERS, MIN_TIME, MAX_TIME, YEAR, RANK ->
                    handleIntegerCondition(field, operator, value);
            case RATING, DIFFICULTY -> handleDoubleCondition(field, operator, value);
            default -> throw new IllegalArgumentException("Invalid condition: " + field);
        };
    }

    /**
     * Handles conditions for text fields (e.g., name).
     *
     * @param operator The operator (e.g., "~=", "==", "!=")
     * @param value    The value to compare against
     * @return A FilterCondition for text fields
     * @throws IllegalArgumentException If the operator is invalid
     */
    private FilterCondition handleStringCondition(String operator, String value) {
        return switch (operator) {
            case "~=" -> game -> game.getName().toLowerCase().contains(value.toLowerCase());
            case "=", "==" -> game -> game.getName().equalsIgnoreCase(value);
            case "!=" -> game -> !game.getName().equalsIgnoreCase(value);
            default -> throw new IllegalArgumentException("Invalid condition: " + operator);
        };
    }

    /**
     * Handles conditions for integer fields
     *
     * @param field    The field to compare
     * @param operator The operator
     * @param value    The value to compare against
     * @return A FilterCondition for integer fields
     * @throws IllegalArgumentException If the value is not a valid integer
     */
    private FilterCondition handleIntegerCondition(GameData field, String operator, String value) {
        int target = Integer.parseInt(value);
        return game -> {
            int actualValue = getIntegerValue(game, field);
            return compareNumbers(actualValue, operator, target);
        };
    }

    /**
     * Retrieves the integer value for a specific field from a game.
     *
     * @param game  The board game
     * @param field The field to retrieve
     * @return The integer value of the field
     * @throws IllegalArgumentException If the field is invalid
     */
    private int getIntegerValue(BoardGame game, GameData field) {
        return switch (field) {
            case MAX_PLAYERS -> game.getMaxPlayers();
            case MIN_PLAYERS -> game.getMinPlayers();
            case MIN_TIME -> game.getMinPlayTime();
            case MAX_TIME -> game.getMaxPlayTime();
            case YEAR -> game.getYearPublished();
            case RANK -> game.getRank();
            default -> throw new IllegalArgumentException("Invalid condition: " + field);
        };
    }

    /**
     * Compares two numbers using the specified operator.
     *
     * @param actual   The actual value
     * @param operator The comparison operator
     * @param target   The target value
     * @return true if the condition is satisfied, false otherwise
     * @throws IllegalArgumentException If the operator is invalid
     */
    private boolean compareNumbers(Number actual, String operator, Number target) {
        double actualValue = actual.doubleValue();
        double targetValue = target.doubleValue();

        return switch (operator) {
            case ">" -> actualValue > targetValue;
            case "<" -> actualValue < targetValue;
            case ">=" -> actualValue >= targetValue;
            case "<=" -> actualValue <= targetValue;
            case "=", "==" -> actualValue == targetValue;
            case "!=" -> actualValue != targetValue;
            default -> throw new IllegalArgumentException("Invalid condition: " + operator);
        };
    }

    /**
     * Handles conditions for double fields
     *
     * @param field    The field to compare
     * @param operator The operator
     * @param value    The value to compare against
     * @return A FilterCondition for double fields
     * @throws IllegalArgumentException If the value is not a valid double
     */
    private FilterCondition handleDoubleCondition(GameData field, String operator, String value) {
        double target = Double.parseDouble(value);
        return game -> {
            double actualValue = getDoubleValue(game, field);
            return compareNumbers(actualValue, operator, target);
        };
    }

    /**
     * Retrieves the double value for a specific field from a game.
     *
     * @param game  The board game
     * @param field The field to retrieve
     * @return The double value of the field
     * @throws IllegalArgumentException If the field is invalid
     */
    private double getDoubleValue(BoardGame game, GameData field) {
        return switch (field) {
            case RATING -> game.getRating();
            case DIFFICULTY -> game.getDifficulty();
            default -> throw new IllegalArgumentException("Invalid condition: " + field);
        };
    }

    /**
     * Updates the current sorting parameters.
     *
     * @param sortOn    The field to sort by
     * @param ascending The sorting direction
     */
    private void updateSortParams(GameData sortOn, boolean ascending) {
        this.currentSortField = sortOn;
        this.isAscending = ascending;
    }

    /**
     * Processes the filtered games by applying all active conditions and sorting.
     *
     * @return A stream of filtered and sorted games
     */
    private Stream<BoardGame> processFilteredGames() {
        List<BoardGame> filteredGames = new ArrayList<>();
        for (BoardGame game : allGames) {
            if (meetAllConditions(game)) {
                filteredGames.add(game);
            }
        }

        Comparator<BoardGame> comparator = buildComparator();
        filteredGames.sort(comparator);

        return filteredGames.stream();
    }

    /**
     * Builds a comparator based on the current sorting field and direction.
     *
     * @return A comparator for sorting games
     * @throws IllegalArgumentException If the sorting field is invalid
     */
    private Comparator<BoardGame> buildComparator() {
        Comparator<BoardGame> comparator = switch (currentSortField) {
            case NAME -> Comparator.comparing(BoardGame::getName, String.CASE_INSENSITIVE_ORDER);
            case MIN_PLAYERS -> Comparator.comparing(BoardGame::getMinPlayers);
            case MAX_PLAYERS -> Comparator.comparing(BoardGame::getMaxPlayers);
            case MIN_TIME -> Comparator.comparing(BoardGame::getMinPlayTime);
            case MAX_TIME -> Comparator.comparing(BoardGame::getMaxPlayTime);
            case YEAR -> Comparator.comparing(BoardGame::getYearPublished);
            case RANK -> Comparator.comparing(BoardGame::getRank);
            case RATING -> Comparator.comparing(BoardGame::getRating);
            case DIFFICULTY -> Comparator.comparing(BoardGame::getDifficulty);
            default -> throw new IllegalArgumentException("Invalid sort field: " + currentSortField);
        };
        return isAscending ? comparator : comparator.reversed();
    }

    /**
     * Checks if a game meets all active filter conditions.
     *
     * @param game The board game to check
     * @return true if all conditions are satisfied, false otherwise
     */
    private boolean meetAllConditions(BoardGame game) {
        for (FilterCondition condition : activeConditions) {
            if (!condition.check(game)) {
                return false;
            }
        }
        return true;
    }
}
