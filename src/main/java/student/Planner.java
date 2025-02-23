package student;

import java.util.*;
import java.util.stream.Stream;


public class Planner implements IPlanner {
    private List<BoardGame> allGames;
    private List<FilterCondition> activeConditions = new ArrayList<>();
    private GameData currentSortField = GameData.NAME;
    private boolean isAscending = true;

    public Planner(Set<BoardGame> games) {
        // TODO Auto-generated method stub
        this.allGames = new ArrayList<>(games);
    }

    @Override
    public Stream<BoardGame> filter(String filter) {
        // TODO Auto-generated method stub
        return filter(filter, GameData.NAME, true);
    }

    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn) {
        // TODO Auto-generated method stub
        return filter(filter, sortOn, true);
    }

    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn, boolean ascending) {
        // TODO Auto-generated method stub
        parseFilterConditions(filter);
        updateSortParams(sortOn, ascending);
        return processFilteredGames();
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        activeConditions.clear();
        currentSortField = GameData.NAME;
        isAscending = true;
    }

    private void parseFilterConditions(String filter) {
        // return if empty filter
        if (filter == null || filter.trim().isEmpty()) {
            return;
        }
        // split conditions
        String[] conditions = filter.split(",");
        for (String rawCondition : conditions) {
            // trim and replace white space
            String condition = rawCondition.trim().replaceAll("\\s+", "");
            if (condition.isEmpty()) {
                continue;
            }
            // Get three components of condition
            ConditionComponents components = parseConditionComponents(condition);
            activeConditions.add(createCondition(components));
        }
    }

    private ConditionComponents parseConditionComponents(String condition) {
        String[] operators = {">=", "<=", "!=", "==", "~=", ">", "<", "="};
        for (String operator : operators) {
            int index = condition.indexOf(operator);
            if (index != -1) {
                return new ConditionComponents(
                        GameData.fromString(condition.substring(0, index)),
                        operator,
                        condition.substring(index + operator.length())
                );
            }
        }
        throw new IllegalArgumentException("Invalid condition: " + condition);
    }

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

    private FilterCondition handleStringCondition(String operator, String value) {
        return switch (operator) {
            case "~=" -> game -> game.getName().toLowerCase().contains(value.toLowerCase());
            case "=", "==" -> game -> game.getName().equalsIgnoreCase(value);
            case "!=" -> game -> !game.getName().equalsIgnoreCase(value);
            default -> throw new IllegalArgumentException("Invalid condition: " + operator);
        };
    }

    private FilterCondition handleIntegerCondition(GameData field, String operator, String value) {
        int target = Integer.parseInt(value);
        return game -> {
            int actualValue = getIntegerValue(game, field);
            return compareNumbers(actualValue, operator, target);
        };
    }

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

    private FilterCondition handleDoubleCondition(GameData field, String operator, String value) {
        double target = Double.parseDouble(value);
        return game -> {
            double actualValue = getDoubleValue(game, field);
            return compareNumbers(actualValue, operator, target);
        };
    }

    private double getDoubleValue(BoardGame game, GameData field) {
        return switch (field) {
            case RATING -> game.getRating();
            case DIFFICULTY -> game.getDifficulty();
            default -> throw new IllegalArgumentException("Invalid condition: " + field);
        };
    }

    private void updateSortParams(GameData sortOn, boolean ascending) {
        this.currentSortField = sortOn;
        this.isAscending = ascending;
    }

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

    private boolean meetAllConditions(BoardGame game) {
        for (FilterCondition condition : activeConditions) {
            if (!condition.check(game)) {
                return false;
            }
        }
        return true;
    }


}
