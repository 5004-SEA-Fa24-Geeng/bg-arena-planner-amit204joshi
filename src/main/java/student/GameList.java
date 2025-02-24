package student;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameList implements IGameList {

    private Set<BoardGame> games;

    /**
     * Constructor for the GameList.
     */
    public GameList() {
        games = new LinkedHashSet<>();
    }

    /**
     * Retrieves game names sorted alphabetically with case-insensitive ordering.
     *
     * @return Unmodifiable list of names in alphabetical order
     */
    @Override
    public List<String> getGameNames() {
        return games.stream()
                .map(BoardGame::getName)
                .sorted(String::compareToIgnoreCase)
                .collect(Collectors.toList());
    }

    /**
     * Removes all games from the collection.
     */
    @Override
    public void clear() {
        games.clear();
    }

    /**
     * Gets the current number of games in the collection.
     *
     * @return Total count of stored games
     */
    @Override
    public int count() {
        return games.size();
    }

    /**
     * Exports sorted game names to a text file.
     *
     * @param filename Target file path for saving
     * @throws RuntimeException if file operations fail, wrapping the original IOException
     */
    @Override
    public void saveGame(String filename) {
        List<String> names = getGameNames();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            names.forEach(writer::println);
        } catch (IOException e) {
            throw new RuntimeException("File save failed: " + e.getMessage());
        }
    }

    /**
     * Adds games from a filtered stream based on selection pattern.
     *
     * @param str      Selection pattern ("all", "X-Y" range, index number, or name)
     * @param filtered Pre-filtered stream of BoardGame objects
     * @throws IllegalArgumentException if:
     *                                  - Filtered stream is empty
     *                                  - Invalid range format
     *                                  - Index out of bounds
     *                                  - Name not found
     */
    @Override
    public void addToList(String str, Stream<BoardGame> filtered) throws IllegalArgumentException {
        List<BoardGame> filteredList = filtered.toList();
        if (filteredList.isEmpty()) {
            throw new IllegalArgumentException("Cannot add from empty filtered list");
        }

        // Handle 'all' special case
        if (ADD_ALL.equalsIgnoreCase(str)) {
            filteredList.forEach(this::safeAddGame);
            return;
        }

        // Process range selection
        if (str.contains("-")) {
            handleRangeAddition(str, filteredList);
            return;
        }

        // Try numeric index or name match
        try {
            int index = Integer.parseInt(str) - 1; // Convert to 0-based index
            addByIndex(index, filteredList);
        } catch (NumberFormatException e) {
            addByName(str, filteredList);
        }
    }

    /**
     * Removes games based on selection pattern using alphabetically sorted view.
     *
     * @param str Selection pattern ("all", "X-Y" range, index number, or name)
     * @throws IllegalArgumentException if:
     *                                  - Invalid range format
     *                                  - Index out of bounds
     *                                  - Name not found
     */
    @Override
    public void removeFromList(String str) throws IllegalArgumentException {
        // Handle 'all' special case
        if (ADD_ALL.equalsIgnoreCase(str)) {
            clear();
            return;
        }
        List<BoardGame> sortedList = getSortedView();
        if (str.contains("-")) {
            // Process range selection
            handleRangeRemoval(str, sortedList);
        } else {
            // Try numeric index or name match
            try {
                int index = Integer.parseInt(str) - 1; // Convert to 0-based
                removeByIndex(index, sortedList);
            } catch (NumberFormatException e) {
                removeByName(str);
            }
        }
    }

    // Internal Helper Methods

    /**
     * Safely adds a game to the collection avoiding duplicates
     *
     * @param game The board game to add
     */
    private void safeAddGame(BoardGame game) {
        if (!games.contains(game)) {
            games.add(game);
        }
    }

    /**
     * Validates index boundaries for list operations
     *
     * @param index The 0-based index to validate
     * @param max   The maximum allowed index (exclusive)
     * @throws IllegalArgumentException If index is out of [0, max-1] range
     */
    private void validateIndex(int index, int max) {
        if (index < 0 || index >= max) {
            throw new IllegalArgumentException("Index " + (index + 1) + " out of valid range 1-" + max);
        }
    }

    /**
     * Validates range parameters for selection operations
     *
     * @param start 0-based starting index
     * @param end   0-based ending index
     * @param max   Maximum allowed index (exclusive)
     * @throws IllegalArgumentException If:
     *                                  - start < 0
     *                                  - end >= max
     *                                  - start > end
     */
    private void validateRange(int start, int end, int max) {
        if (start < 0 || end >= max || start > end) {
            throw new IllegalArgumentException("Invalid range [" + (start + 1) + "-" + (end + 1) + "]");
        }
    }

    // Addition Operations

    /**
     * Processes range addition pattern ("X-Y" format)
     *
     * @param range      The range string (e.g., "2-5")
     * @param sourceList Filtered list to select from
     * @throws IllegalArgumentException If:
     *                                  - Invalid range format
     *                                  - Non-numeric values
     *                                  - Invalid range boundaries
     */
    private void handleRangeAddition(String range, List<BoardGame> sourceList) {
        String[] parts = range.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range format: " + range);
        }

        try {
            int start = Integer.parseInt(parts[0].trim()) - 1;
            int end = Integer.parseInt(parts[1].trim()) - 1;
            validateRange(start, end, sourceList.size());

            for (int i = start; i <= end; i++) {
                safeAddGame(sourceList.get(i));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Non-numeric range values: " + range);
        }
    }

    /**
     * Adds a single game from filtered list by index
     *
     * @param index      0-based index in filtered list
     * @param sourceList Pre-filtered game list
     */
    private void addByIndex(int index, List<BoardGame> sourceList) {
        validateIndex(index, sourceList.size());
        safeAddGame(sourceList.get(index));
    }

    /**
     * Adds games by case-insensitive name matching
     *
     * @param name       Target game name (case-insensitive)
     * @param sourceList Filtered list to search
     * @throws IllegalArgumentException If no matching game found
     */
    private void addByName(String name, List<BoardGame> sourceList) {
        boolean found = sourceList.stream()
                .anyMatch(game -> {
                    if (game.getName().equalsIgnoreCase(name)) {
                        safeAddGame(game);
                        return true;
                    }
                    return false;
                });

        if (!found) {
            throw new IllegalArgumentException("No game found with name: " + name);
        }
    }

    // Removal Operations

    /**
     * Generates alphabetically sorted view for removal operations
     *
     * @return List sorted by name (case-insensitive)
     */
    private List<BoardGame> getSortedView() {
        return games.stream()
                .sorted(Comparator.comparing(BoardGame::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    /**
     * Processes range removal pattern ("X-Y" format)
     *
     * @param range      The range string (e.g., "3-7")
     * @param sortedList Alphabetically sorted game list
     * @throws IllegalArgumentException If:
     *                                  - Invalid range format
     *                                  - Non-numeric values
     *                                  - Invalid range boundaries
     */
    private void handleRangeRemoval(String range, List<BoardGame> sortedList) {
        String[] parts = range.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range format: " + range);
        }

        try {
            int start = Integer.parseInt(parts[0].trim()) - 1;
            int end = Integer.parseInt(parts[1].trim()) - 1;
            validateRange(start, end, sortedList.size());

            List<BoardGame> toRemove = sortedList.subList(start, end + 1);
            games.removeAll(toRemove);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Non-numeric range values: " + range);
        }
    }

    /**
     * Removes game by index from sorted view
     *
     * @param index      0-based index in sorted list
     * @param sortedList Alphabetically sorted game list
     * @throws IllegalArgumentException If game not found in collection
     */
    private void removeByIndex(int index, List<BoardGame> sortedList) {
        validateIndex(index, sortedList.size());
        BoardGame target = sortedList.get(index);
        if (!games.contains(target)) {
            throw new IllegalArgumentException("Game at index " + (index + 1) + " not found");
        }
        games.remove(target);
    }

    /**
     * Removes game by case-insensitive name match
     *
     * @param name Target game name (case-insensitive)
     * @throws IllegalArgumentException If no matching game found
     */
    private void removeByName(String name) {
        boolean removed = games.removeIf(game ->
                game.getName().equalsIgnoreCase(name));
        if (!removed) {
            throw new IllegalArgumentException("No game found with name: " + name);
        }
    }
}
