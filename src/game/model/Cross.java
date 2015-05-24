/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game.model;

/**
 * GameObject of the first player
 *
 * @author Ivan
 */
public class Cross extends GameObject {
    public Cross() {
        super();
    }

    public Cross(int x, int y, int z) {
        super(x, y, z);
    }

    public Cross(Coordinate coordinate) {
        super(coordinate);
    }
}
