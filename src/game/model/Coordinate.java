package game.model;

/**
 * Class for represention 3-x dimension coordinates
 *
 * @author Ivan
 */
public class Coordinate {
    private final int x;
    private final int y;
    private final int z;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public Coordinate(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates new instance with sum of coordinates of first and second
     *
     * @param first
     * @param second
     */
    public Coordinate(Coordinate first, Coordinate second) {
        x = first.getX() + second.getX();
        y = first.getY() + second.getY();
        z = first.getZ() + second.getZ();
    }

    /**
     * checks coordinate's equality
     *
     * @param c coordinate
     * @return true if objects are the same
     */
    @Override
    public boolean equals(Object c) {
        Coordinate coordinate = (Coordinate) c;
        if (x != coordinate.getX()) {
            return false;
        }
        if (y != coordinate.getY()) {
            return false;
        }
        if (z != coordinate.getZ()) {
            return false;
        }
        return true;
    }
}

