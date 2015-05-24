package game.model;

/**
 * GameObject of the second player
 *
 * @author Ivan
 */
public class Nought extends GameObject {

    public Nought() {
        super();
    }

    public Nought(int x, int y, int z) {
        super(x, y, z);
    }

    public Nought(Coordinate coordinate) {
        super(coordinate);
    }
}
