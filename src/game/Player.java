package game;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import game.model.Cross;
import game.model.Game;
import game.model.Nought;
import game.state.HumanVsHumanAppState;
import game.state.PCvsHumanAppState;

/**
 * Player can made moves and have his own type of cells
 *
 * @author Ivan
 */
public class Player {
    /**
     * Nought or Cross
     */
    protected Class gameClass;
    protected final Game game;

    public Player(Class gameClass, Game game) {
//        if (gameClass.getClass() == GameObject.class) {
//            throw new Exception("");
//        }
        this.gameClass = gameClass;
        this.game = game;
    }

    // TODO: divide this method on doMove() and isMoveDone()

    /**
     * Player fills any empty cell in a table
     *
     * @param inputManager
     * @param assetManager
     * @param cam
     * @param cubeNode
     * @param state
     * @return true if move has done
     */
    public boolean doMove(InputManager inputManager,
                          AssetManager assetManager, Camera cam, Node cubeNode, PCvsHumanAppState state) {
        boolean isChanged = false;
        // for 3D objects
        CollisionResults results = new CollisionResults();
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(
                new Vector2f(click2d.getX(), click2d.getY()), 0f);
        Vector3f dir = cam.getWorldCoordinates(
                new Vector2f(click2d.getX(), click2d.getY()), 1f).
                subtractLocal(click3d);
        Ray ray = new Ray(click3d, dir);
        cubeNode.collideWith(ray, results);
        if (results.size() > 0) {
            String name = "";
            Geometry target = results.getClosestCollision().getGeometry();
            try {
                name = target.getName().substring(0, 7);
            } catch (IndexOutOfBoundsException e) {
                //System.out.println("IndexOutOfBounds: " + target.getName());
                //name = "Nothing";
                name = "";
            }
            if (name.equals("Nothing")) {
                Vector3f location = target.getLocalTranslation();
                int x = (int) (location.getX() + 6) / 4;
                int y = (int) (location.getY() + 6) / 4;
                int z = (int) (location.getZ() + 6) / 4;
                if (getGameClass().equals(Nought.class)) {
                    game.updateTable(x, y, z, new Nought(x, y, z));
                    isChanged = true;
                    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    Texture cube1Tex = assetManager.loadTexture("Textures/texture1.jpg");
                    mat.setTexture("ColorMap", cube1Tex);
                    Geometry nought = state.createNought("Nought" + x + y + z, location, mat);
                    cubeNode.detachChild(target);
                    cubeNode.attachChild(nought);
                } else {
                    game.updateTable(x, y, z, new Cross(x, y, z));
                    isChanged = true;
                    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    Texture cube1Tex = assetManager.loadTexture("Textures/texture1.jpg");
                    mat.setTexture("ColorMap", cube1Tex);
                    Node cross = state.createCross("Cross" + x + y + z, location, mat);
                    cubeNode.detachChild(target);
                    cubeNode.attachChild(cross);
                }
            }
        }
        return isChanged;
    }

    public boolean doMove(InputManager inputManager,
                          AssetManager assetManager, Camera cam, Node cubeNode, HumanVsHumanAppState state) {
        boolean isChanged = false;
        CollisionResults results = new CollisionResults();
        Vector2f click2d = inputManager.getCursorPosition();
        Vector3f click3d = cam.getWorldCoordinates(
                new Vector2f(click2d.getX(), click2d.getY()), 0f);
        Vector3f dir = cam.getWorldCoordinates(
                new Vector2f(click2d.getX(), click2d.getY()), 1f).
                subtractLocal(click3d);
        Ray ray = new Ray(click3d, dir);
        cubeNode.collideWith(ray, results);
        if (results.size() > 0) {
            String name;
            Geometry target = results.getClosestCollision().getGeometry();
            try {
                name = target.getName().substring(0, 7);
            } catch (IndexOutOfBoundsException e) {
                //System.out.println("IndexOutOfBounds: " + target.getName());
                //name = "Nothing";
                name = "";
            }
            if (name.equals("Nothing")) {
                Vector3f location = target.getLocalTranslation();
                // getX() = 4*x - 6, see this.makeScene()
                int x = (int) (location.getX() + 6) / 4;
                int y = (int) (location.getY() + 6) / 4;
                int z = (int) (location.getZ() + 6) / 4;
                if (getGameClass().equals(Nought.class)) {
                    game.updateTable(x, y, z, new Nought(x, y, z));
                    isChanged = true;
                    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    Texture cube1Tex = assetManager.loadTexture("Textures/texture2.jpg");
                    mat.setTexture("ColorMap", cube1Tex);
                    Geometry nought = state.createNought("Nought" + x + y + z, location, mat);
                    cubeNode.detachChild(target);
                    cubeNode.attachChild(nought);
                } else {
                    game.updateTable(x, y, z, new Cross(x, y, z));
                    isChanged = true;
                    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    Texture cube1Tex = assetManager.loadTexture("Textures/texture1.jpg");
                    mat.setTexture("ColorMap", cube1Tex);
                    Node cross = state.createCross("Cross" + x + y + z, location, mat);
                    cubeNode.detachChild(target);
                    cubeNode.attachChild(cross);
                }
            }
        }
        return isChanged;
    }

    /**
     * @return the gameClass of the player
     */
    public Class getGameClass() {
        return gameClass;
    }
}
