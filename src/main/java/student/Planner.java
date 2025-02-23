package student;

import java.util.*;
import java.util.stream.Stream;


public class Planner implements IPlanner {


    public Planner(Set<BoardGame> games) {
        // TODO Auto-generated method stub

    }

    @Override
    public Stream<BoardGame> filter(String filter) {
        // TODO Auto-generated method stub
        filter(filter,GameData.fromString("name"));
    }

    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn) {
        // TODO Auto-generated method stub
        filter(filter,sortOn,true);
    }

    @Override
    public Stream<BoardGame> filter(String filter, GameData sortOn, boolean ascending) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reset'");
    }




}
