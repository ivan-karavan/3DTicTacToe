package game.model;

/**
 * The game object in cells, not abstract for all in the game
 *
 * @author Ivan
 */
public abstract class GameObject {
    protected final Coordinate coordinate;

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public GameObject(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public GameObject(int x, int y, int z) {
        coordinate = new Coordinate(x, y, z);
    }

    /**
     * initialize object with (0, 0, 0) coordinates
     */
    public GameObject() {
        coordinate = new Coordinate(0, 0, 0);
    }
}
