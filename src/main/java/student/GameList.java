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

    @Override
    public List<String> getGameNames() {
        // TODO Auto-generated method stub
        return games.stream().map(BoardGame::getName).sorted(String::compareToIgnoreCase).collect(Collectors.toList());
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
        games.clear();
    }

    @Override
    public int count() {
        // TODO Auto-generated method stub
        return games.size();
    }

    @Override
    public void saveGame(String filename) {
        // TODO Auto-generated method stub
        List<String> names = getGameNames();
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (String name : names) {
                writer.println(name);
            }
        } catch (IOException e) {
            throw new RuntimeException("Saving error:" + e.getMessage());
        }
    }

    @Override
    public void addToList(String str, Stream<BoardGame> filtered) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        List<BoardGame> filteredList = filtered.toList();
        if (filteredList.isEmpty()) {
            throw new IllegalArgumentException("List is empty");
        }

        if (ADD_ALL.equalsIgnoreCase(str)) {
            filteredList.forEach(this::addGameIfAbsent);
            return;
        }

        if (str.contains("-")) {
            addRange(str, filteredList);
            return;
        }

        try {
            int index = Integer.parseInt(str) - 1;
            addByIndex(index, filteredList);
        } catch (NumberFormatException e) {
            addByName(str, filteredList);
        }
    }

    private void addGameIfAbsent(BoardGame game) {
        if (!games.contains(game)) {
            games.add(game);
        }
    }

    private void addRange(String range, List<BoardGame> filteredList) {
        String[] parts = range.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range");
        }

        try {
            int start = Integer.parseInt(parts[0].trim()) - 1;
            int end = Integer.parseInt(parts[1].trim()) - 1;
            validateRange(start, end, filteredList.size());
            for (int i = start; i <= end; i++) {
                addGameIfAbsent(filteredList.get(i));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid range");
        }
    }

    private void validateRange(int start, int end, int max) {
        if (start < 0 || end >= max || start > end) {
            throw new IllegalArgumentException("Invalid range");
        }
    }

    private void addByIndex(int index, List<BoardGame> filteredList) {
        validateIndex(index, filteredList.size());
        addGameIfAbsent(filteredList.get(index));
    }

    private void validateIndex(int index, int max) {
        if (index < 0 || index >= max) {
            throw new IllegalArgumentException("Invalid index");
        }
    }

    private void addByName(String name, List<BoardGame> filteredList) {
        boolean found = filteredList.stream().anyMatch(game -> {
            if (game.getName().equalsIgnoreCase(name)) {
                addGameIfAbsent(game);
                return true;
            }
            return false;
        });

        if (!found) {
            throw new IllegalArgumentException("Invalid name");
        }
    }

    @Override
    public void removeFromList(String str) throws IllegalArgumentException {
        // TODO Auto-generated method stub
        if (ADD_ALL.equalsIgnoreCase(str)) {
            clear();
            return;
        }

        List<BoardGame> sortedList = getSortedList();

        if (str.contains("-")) {
            removeRange(str, sortedList);
        } else {
            try {
                int index = Integer.parseInt(str) - 1;
                removeByIndex(index, sortedList);
            } catch (NumberFormatException e) {
                removeByName(str);
            }
        }
    }

    private List<BoardGame> getSortedList() {
        return games.stream()
                .sorted(Comparator.comparing(BoardGame::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    private void removeRange(String range, List<BoardGame> sortedList) {
        String[] parts = range.split("-");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid range");
        }

        try {
            int start = Integer.parseInt(parts[0].trim()) - 1;
            int end = Integer.parseInt(parts[1].trim()) - 1;
            validateRange(start, end, sortedList.size());
            List<BoardGame> toRemove = sortedList.subList(start, end);
            games.removeAll(toRemove);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid range");
        }
    }

    private void removeByIndex(int index, List<BoardGame> sortedList) {
        validateIndex(index, sortedList.size());
        BoardGame gameToRemove = sortedList.get(index);
        if (!games.contains(gameToRemove)) {
            throw new IllegalArgumentException("Invalid index");
        }
        games.remove(gameToRemove);
    }

    private void removeByName(String name) {
        boolean removed = games.removeIf(game -> game.getName().equalsIgnoreCase(name));
        if (!removed) {
            throw new IllegalArgumentException("Invalid name");
        }
    }
}
