package game.model;

/**
 * Game table with cells
 *
 * @author Ivan
 */
public class Table {
    // the easiest way is using common array
    private GameObject[][][] table;
    private final int dimensionSize;

    public Table(int dimSize) {
        dimensionSize = dimSize;
        this.table = new GameObject[dimensionSize][dimensionSize][dimensionSize];
    }

    /**
     * returns object from table with this coordinates
     *
     * @param x dimension 1
     * @param y dimension 2
     * @param z dimension 3
     * @return object in table with coordinates x, y, z in table
     */
    public GameObject get(int x, int y, int z) {
        return table[x][y][z];
    }

    /**
     * returns object from table with this coordinate
     *
     * @param coordinate coordinate of the nessessary object
     * @return
     */
    public GameObject get(Coordinate coordinate) {
        return table[coordinate.getX()][coordinate.getY()][coordinate.getZ()];
    }


    public void set(GameObject[][][] readyTable) {
        // is exist deep array copy for 3x-array?
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++) {
                    table[i][j][k] = readyTable[i][j][k];
                }
            }
        }
    }

    /**
     * adds new GameObject to the table
     *
     * @param x      dimension 1
     * @param y      dimension 2
     * @param z      dimension 3
     * @param newObj put newObj in table's cell with adjusted coordinates
     */
    public void add(int x, int y, int z, GameObject newObj) {
        table[x][y][z] = newObj;
    }
}
