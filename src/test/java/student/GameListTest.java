package student;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GameListTest {
    private GameList gameList;
    private static Set<BoardGame> games;

    @BeforeAll
    static void setup() {
        games = new LinkedHashSet<>();
        games.add(new BoardGame("17 days", 6, 1, 8, 70, 70, 9.0, 600, 9.0, 2005));
        games.add(new BoardGame("Chess", 7, 2, 2, 10, 20, 10.0, 700, 10.0, 2006));
        games.add(new BoardGame("Go", 1, 2, 5, 30, 30, 8.0, 100, 7.5, 2000));
        games.add(new BoardGame("Go Fish", 2, 2, 10, 20, 120, 3.0, 200, 6.5, 2001));
        games.add(new BoardGame("golang", 4, 2, 7, 50, 55, 7.0, 400, 9.5, 2003));
        games.add(new BoardGame("GoRami", 3, 6, 6, 40, 42, 5.0, 300, 8.5, 2002));
        games.add(new BoardGame("Monopoly", 8, 6, 10, 20, 1000, 1.0, 800, 5.0, 2007));
        games.add(new BoardGame("Tucano", 5, 10, 20, 60, 90, 6.0, 500, 8.0, 2004));
    }

    @BeforeEach
    void init() {
        gameList = new GameList();
    }

    @Test
    void getGameNames() {
        gameList.addToList("all", games.stream());
        List<String> names = gameList.getGameNames();
        assertEquals(List.of(
                "17 days",
                "Chess",
                "Go",
                "Go Fish",
                "golang",
                "GoRami",
                "Monopoly",
                "Tucano"
        ), names);
    }

    @Test
    void clear() {
        gameList.addToList("all", games.stream());
        gameList.clear();
        assertEquals(0, gameList.count());
    }

    @Test
    void count() {
        gameList.addToList("1", games.stream());
        assertEquals(1, gameList.count());
        gameList.addToList("1-2", games.stream());
        assertEquals(2, gameList.count());
    }

    @Test
    void saveGame() {
        GameList gameList = new GameList();
        gameList.addToList("all", games.stream());

        Path tempDir = Paths.get("temp");
        try {
            Files.createDirectories(tempDir);
        } catch (IOException e) {
            fail("Failed to create temp directory: " + e.getMessage());
        }

        String testFile = "temp/games.txt";
        assertDoesNotThrow(() -> gameList.saveGame(testFile));

        try {
            Files.deleteIfExists(Paths.get(testFile));
            Files.deleteIfExists(tempDir);
        } catch (IOException ignored) {
        }
    }

    @Test
    void addAllToList() {
        gameList.addToList("all", games.stream());
        assertEquals(8, gameList.count());
    }

    @Test
    void addValidRange() {
        gameList.addToList("1-3", games.stream());
        assertEquals(3, gameList.count());
    }

    @Test
    void addInvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameList.addToList("3-2", games.stream());
        });
    }

    @Test
    void addByName() {
        gameList.addToList("go", games.stream());
        assertEquals(1, gameList.count());
        assertTrue(gameList.getGameNames().contains("Go"));
        gameList.addToList("Go", games.stream());
        assertEquals(1, gameList.count());
    }

    @Test
    void addInvalidName() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameList.addToList("go2", games.stream());
        });
    }

    @Test
    void addByIndex() {
        gameList.addToList("1", games.stream());
        assertEquals(1, gameList.count());
        assertTrue(gameList.getGameNames().contains("17 days"));
    }

    @Test
    void addByIndexInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            gameList.addToList("-2", games.stream());
        });
    }

    @Test
    void removeAll() {
        gameList.addToList("all", games.stream());
        gameList.removeFromList("all");
        assertEquals(0, gameList.count());
    }

    @Test
    void removeValidRange() {
        gameList.addToList("1-3", games.stream());
        assertEquals(3, gameList.count());
        gameList.removeFromList("2-3");
        assertEquals(1, gameList.count());
    }

    @Test
    void removeInvalidRange() {
        gameList.addToList("1-3", games.stream());
        assertEquals(3, gameList.count());
        assertThrows(IllegalArgumentException.class, () -> {
            gameList.removeFromList("2-8");
        });
    }

    @Test
    void removeByName() {
        gameList.addToList("go", games.stream());
        assertEquals(1, gameList.count());
        gameList.removeFromList("go");
        assertEquals(0, gameList.count());
    }

    @Test
    void removeByInvalidName() {
        gameList.addToList("go", games.stream());
        assertEquals(1, gameList.count());
        assertThrows(IllegalArgumentException.class, () -> {
            gameList.removeFromList("go2");
        });
    }

    @Test
    void removeByIndex() {
        gameList.addToList("all", games.stream());
        assertEquals(8, gameList.count());
        gameList.removeFromList("2");
        assertEquals(7, gameList.count());
    }

    @Test
    void removeByIndexInvalid() {
        gameList.addToList("all", games.stream());
        assertEquals(8, gameList.count());
        assertThrows(IllegalArgumentException.class, () -> {
            gameList.removeFromList("9");
        });
    }
}