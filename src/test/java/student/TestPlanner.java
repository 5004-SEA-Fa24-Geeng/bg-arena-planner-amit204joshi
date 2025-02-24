package student;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


/**
 * JUnit test for the Planner class.
 * <p>
 * Just a sample test to get you started, also using
 * setup to help out.
 */
public class TestPlanner {
    static IPlanner planner;
    static Set<BoardGame> games;

    @BeforeAll
    public static void setup() {
        games = new HashSet<>();
        games.add(new BoardGame("17 days", 6, 1, 8, 70, 70, 9.0, 600, 9.0, 2005));
        games.add(new BoardGame("Chess", 7, 2, 2, 10, 20, 10.0, 700, 10.0, 2006));
        games.add(new BoardGame("Go", 1, 2, 5, 30, 30, 8.0, 100, 7.5, 2000));
        games.add(new BoardGame("Go Fish", 2, 2, 10, 20, 120, 3.0, 200, 6.5, 2001));
        games.add(new BoardGame("golang", 4, 2, 7, 50, 55, 7.0, 400, 9.5, 2003));
        games.add(new BoardGame("GoRami", 3, 6, 6, 40, 42, 5.0, 300, 8.5, 2002));
        games.add(new BoardGame("Monopoly", 8, 6, 10, 20, 1000, 1.0, 800, 5.0, 2007));
        games.add(new BoardGame("Tucano", 5, 10, 20, 60, 90, 6.0, 500, 8.0, 2004));
        planner = new Planner(games);
    }

    @BeforeEach
    public void reset() {
        planner.reset();
    }

    // helper method
    private void assertStreamContains(Stream<BoardGame> stream, String[] expectedNames) {
        List<String> actual = stream.map(BoardGame::getName).toList();
        assertArrayEquals(
                expectedNames,
                actual.toArray()
        );
    }

    @Test
    void testFilterByName() {
        Stream<BoardGame> result = planner.filter("name == Go Fish");
        String[] expected = {"Go Fish"};
        assertStreamContains(result, expected);
    }

    @Test
    void testFilterByNameContains() {
        Stream<BoardGame> result = planner.filter("name ~= Go");
        String[] expected = {"Go", "Go Fish", "golang", "GoRami"};
        assertStreamContains(result, expected);
    }

    @Test
    void testFilterByMinPlayers() {
        Stream<BoardGame> result = planner.filter("minPlayers>=6");
        String[] expected = {"GoRami", "Monopoly", "Tucano"};
        assertStreamContains(result, expected);
    }

    @Test
    void testFilterByMaxPlayersRange() {
        Stream<BoardGame> result = planner.filter("maxPlayers>5,maxPlayers<=7");
        String[] expected = {"golang", "GoRami"};
        assertStreamContains(result, expected);
    }

    @Test
    void testFilterByRating() {
        Stream<BoardGame> result = planner.filter("rating>=9.5");
        String[] expected = {"Chess", "golang"};
        assertStreamContains(result, expected);
    }

    @Test
    void testFilterByYear() {
        Stream<BoardGame> result = planner.filter("year>2005,year!=2006");
        String[] expected = {"Monopoly"};
        assertStreamContains(result, expected);
    }

    @Test
    void testSortByNameByDefault() {
        Stream<BoardGame> result = planner.filter("");
        assertEquals("17 days", result.findFirst().get().getName());
    }

    @Test
    void testSortByYearAscending() {
        Stream<BoardGame> result = planner.filter("", GameData.YEAR);
        assertEquals("Go", result.findFirst().get().getName());
    }

    @Test
    void testSortByDifficultyDescending() {
        Stream<BoardGame> result = planner.filter("", GameData.DIFFICULTY,false);
        assertEquals("Chess", result.findFirst().get().getName());
    }

    @Test
    void combineTest(){
        Stream<BoardGame> result = planner.filter("minPlayers>=6,maxPlayTime<=100",GameData.YEAR,false);
        String[] expected = {"Tucano", "GoRami"};
    }

    @Test
    void testEmptyFilter() {
        assertEquals(games.size(), planner.filter("").count());
    }

    @Test
    void testInvalidOperator() {
        assertThrows(IllegalArgumentException.class,
                () -> planner.filter("name#Go")
        );
    }

    @Test
    void testNonExistingGameName() {
        Stream<BoardGame> result = planner.filter("name==???");
        assertEquals(0, result.count());
    }

    @Test
    void testInvalidField() {
        assertThrows(IllegalArgumentException.class,
                () -> planner.filter("Popularity<=2")
        );
    }

    @Test
    void testResetFilters() {
        planner.filter("name~=go");
        planner.reset();
        assertEquals(games.size(), planner.filter("").count());
    }
}