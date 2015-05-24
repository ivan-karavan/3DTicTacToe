package game.model;

import game.AI;
import game.Player;

import java.util.ArrayList;


/**
 * Current game with current state of table
 *
 * @author Ivan
 */
public class Game {
    /**
     * table with empty cells and cells filled by cross or nought
     */
    private Table table;
    /**
     * length of the same game objects on one line
     */
    private final int LENGTH_FOR_WIN;
    /**
     * cross or nought added by player, who made last move
     */
    private GameObject lastAddedObject;
    /**
     * The first player in the game
     */
    private Player crossPlayer;
    /**
     * The second player in the game
     */
    private Player noughtPlayer;

    private ArrayList<Coordinate> winnersConsequence;
    /**
     * All pairs of possible dimensions from every cell
     * 13 pairs : (3*3*3-1)/2
     */
    private Pair[] deltas = {
            new Pair(new Coordinate(0, 0, 1), new Coordinate(0, 0, -1)),
            new Pair(new Coordinate(0, 1, 1), new Coordinate(0, -1, -1)),
            new Pair(new Coordinate(0, 1, 0), new Coordinate(0, -1, 0)),
            new Pair(new Coordinate(0, 1, -1), new Coordinate(0, -1, 1)),
            new Pair(new Coordinate(1, -1, 0), new Coordinate(-1, 1, 0)),
            new Pair(new Coordinate(1, 0, 0), new Coordinate(-1, 0, 0)),
            new Pair(new Coordinate(1, 1, 0), new Coordinate(-1, -1, 0)),
            new Pair(new Coordinate(-1, 1, 1), new Coordinate(1, -1, -1)),
            new Pair(new Coordinate(-1, -1, -1), new Coordinate(1, 1, 1)),
            new Pair(new Coordinate(-1, 1, -1), new Coordinate(1, -1, 1)),
            new Pair(new Coordinate(-1, -1, 1), new Coordinate(1, 1, -1)),
            new Pair(new Coordinate(-1, 0, -1), new Coordinate(1, 0, 1)),
            new Pair(new Coordinate(-1, 0, 1), new Coordinate(1, 0, -1))
    };

    /**
     * @return the table
     */
    public Table getTable() {
        return table;
    }

    /**
     * @return the crossPlayer
     */
    public Player getCrossPlayer() {
        return crossPlayer;
    }

    /**
     * @return the noughtPlayer
     */
    public Player getNoughtPlayer() {
        return noughtPlayer;
    }

    /**
     * @return the winnersConsequence
     */
    public ArrayList<Coordinate> getWinnersConsequence() {
        return winnersConsequence;
    }

    /**
     * Pair of coordinates
     * JDK has not generic class Pair like this one in C++
     */
    private class Pair {
        private Coordinate first;
        private Coordinate second;

        public Coordinate getFirst() {
            return first;
        }

        public Coordinate getSecond() {
            return second;
        }

        public Pair(Coordinate first, Coordinate second) {
            this.first = first;
            this.second = second;
        }
    }

    /**
     * @param table        commonly table is empty
     * @param lengthForWin commonly 4
     */
    public Game(Table table, int lengthForWin) {
        this.table = table;
        LENGTH_FOR_WIN = lengthForWin;
        crossPlayer = new Player(Cross.class, this);
        //noughtPlayer = new Player(Nought.class, this);
        noughtPlayer = new AI(Nought.class, this);
    }

    public Game(Table table, int lengthForWin, int numberOfPlayers) {
        this.table = table;
        LENGTH_FOR_WIN = lengthForWin;
        crossPlayer = new Player(Cross.class, this);
        noughtPlayer = new Player(Nought.class, this);
        //noughtPlayer = new AI(Nought.class, this);
    }

    /**
     * @param x         first dimension
     * @param y         second dimension
     * @param z         third dimension
     * @param newObject added object
     */
    public void updateTable(int x, int y, int z, GameObject newObject) {
        getTable().add(x, y, z, newObject);
        lastAddedObject = newObject;
    }

    /**
     * @param delta      next step will be to this direction
     * @param ourClass   Nought or Cross
     * @param coordinate current coordinate
     * @return length of the same objects on that direction
     */
    private int recursiveCall(final Coordinate delta, final Class ourClass, Coordinate coordinate) {
        if (!isCorrect(coordinate)) {
            return 0;
        }
        if (getTable().get(coordinate.getX(), coordinate.getY(), coordinate.getZ()) == null) {
            return 0;
        }

        if (getTable().get(coordinate.getX(), coordinate.getY(), coordinate.getZ()).
                getClass() == ourClass) {
            Coordinate newCoordinate = new Coordinate(coordinate, delta);
            return 1 + recursiveCall(delta, ourClass, newCoordinate);
        } else {
            return 0;
        }
    }

    /**
     * Checks the existence of the winner
     *
     * @return winner is found
     */
    public boolean checkForWinner() {
        // adding to @code{LENGTH_FOR_WIN}, because we count lastAddedObject twice
        for (Pair dimensionPair : deltas) {
            if (recursiveCall(dimensionPair.getFirst(), lastAddedObject.getClass(), lastAddedObject.getCoordinate()) +
                    recursiveCall(dimensionPair.getSecond(), lastAddedObject.getClass(), lastAddedObject.getCoordinate())
                    == LENGTH_FOR_WIN + 1) {
                doWinnersConsequence(dimensionPair);
                return true;
            }
        }
        return false;
    }

    private boolean isCorrect(Coordinate coordinate, Coordinate delta) {
        return !(coordinate.getX() + delta.getX() < 0 || coordinate.getY() + delta.getY() < 0
                || coordinate.getZ() + delta.getZ() < 0 || coordinate.getX() + delta.getX() > 3
                || coordinate.getY() + delta.getY() > 3 || coordinate.getZ() + delta.getZ() > 3);
    }

    /**
     * Checks coordinates by correctness (borders)
     *
     * @param coordinate checking value
     * @return true if all values are correct
     */
    private boolean isCorrect(Coordinate coordinate) {
        return !(coordinate.getX() < 0 || coordinate.getX() > 3 ||
                coordinate.getY() < 0 || coordinate.getY() > 3 ||
                coordinate.getZ() < 0 || coordinate.getZ() > 3);
    }

    private void doWinnersConsequence(Pair dimensionPair) {
        winnersConsequence = new ArrayList<>(4);
        getWinnersConsequence().add(lastAddedObject.getCoordinate());

        Coordinate delta = dimensionPair.getFirst();
        while (isCorrect(lastAddedObject.getCoordinate(), delta)) {
            getWinnersConsequence().add(new Coordinate(lastAddedObject.getCoordinate(), delta));
            delta = new Coordinate(delta, dimensionPair.getFirst());
        }

        delta = dimensionPair.getSecond();
        while (isCorrect(lastAddedObject.getCoordinate(), delta)) {
            getWinnersConsequence().add(new Coordinate(lastAddedObject.getCoordinate(), delta));
            delta = new Coordinate(delta, dimensionPair.getSecond());
        }
    }
}
