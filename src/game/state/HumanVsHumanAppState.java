/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package game.state;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.*;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Torus;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import com.jme3.util.SkyFactory;
import game.MainApp;
import game.Player;
import game.model.Coordinate;
import game.model.Cross;
import game.model.Game;
import game.model.Table;

import java.util.ArrayList;

/**
 * Game state which describe behavior of the human vs human game
 *
 * @author Ivan
 */
public class HumanVsHumanAppState extends AbstractAppState {
    private HumanVsHumanAppState thisState = this;
    private SimpleApplication app;
    private Node rootNode;
    private Node guiNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private Camera cam;
    private InputManager inputManager;
    private Box box = new Box(0.5f, 0.5f, 0.5f);
    private Box crossPart = new Box(1, 0.2f, 0.3f);
    private Torus torus = new Torus(20, 10, 0.2f, 0.5f);
    private Game game;
    private final static int LENGTH_FOR_WIN = 4;
    private Player currentPlayer;
    private int camDistanceZ = 25;
    private Node cubeNode;
    private BitmapFont guiFont;
    private Picture hud;


    private final static Trigger TRIGGER_DO_MOVE = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static String MAPPING_DO_MOVE = "DoMove";
    private ActionListener doMoveActionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_DO_MOVE) && !isPressed) {
                if (currentPlayer.doMove(inputManager, assetManager, cam, cubeNode, thisState)) {
                    try {
                        if (game.checkForWinner()) {
                            showWinner(currentPlayer.getGameClass());
                            showWinnersConsequence();
                            stopGame();
                        } else {
                            changePlayer();
                        }
                    } catch (NullPointerException e) {
                        System.out.println("There are no objects in table");
                    }
                }
            }
        }
    };

    private final static Trigger TRIGGER_TO_MENU = new KeyTrigger(KeyInput.KEY_SPACE);
    private final static String MAPPING_TO_MENU = "SetBaseLocation";
    private ActionListener setBaseLocationActionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_TO_MENU)) {
                MenuState menuState = new MenuState();
                stateManager.detach(thisState);
                stateManager.attach(menuState);
            }
        }
    };

    private final static Trigger TRIGGER_ROTATION_LEFT = new KeyTrigger(KeyInput.KEY_LEFT);
    private final static String MAPPING_ROTATION_LEFT = "rotation_left";
    private final static Trigger TRIGGER_ROTATION_RIGHT = new KeyTrigger(KeyInput.KEY_RIGHT);
    private final static String MAPPING_ROTATION_RIGHT = "rotation_right";
    private final static Trigger TRIGGER_ROTATION_UP = new KeyTrigger(KeyInput.KEY_UP);
    private final static String MAPPING_ROTATION_UP = "rotation_up";
    private final static Trigger TRIGGER_ROTATION_DOWN = new KeyTrigger(KeyInput.KEY_DOWN);
    private final static String MAPPING_ROTATION_DOWN = "rotation_down";
    private float rotateDelta = 1.3f;
    private AnalogListener rotationLeftAnalogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals(MAPPING_ROTATION_LEFT)) {
//                alfa += delta;
//                cam.setLocation(new Vector3f((float)Math.sin(alfa)* (-camDistanceZ), 0, 
//                        (float)Math.cos(alfa) * camDistanceZ));
//                cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
                cubeNode.rotate(0, -rotateDelta * tpf, 0);
            }
        }
    };
    private AnalogListener rotationRightAnalogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals(MAPPING_ROTATION_RIGHT)) {
                cubeNode.rotate(0, rotateDelta * tpf, 0);
            }
        }

    };
    private AnalogListener rotationUpAnalogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals(MAPPING_ROTATION_UP)) {
                cubeNode.rotate(-rotateDelta * tpf, 0, 0);
            }
        }
    };
    private AnalogListener rotationDownAnalogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals(MAPPING_ROTATION_DOWN)) {
                cubeNode.rotate(rotateDelta * tpf, 0, 0);
            }
        }
    };

    @Override
    public void update(float tpf) {

    }

    @Override
    public void cleanup() {
        rootNode.detachAllChildren();
        guiNode.detachAllChildren();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.cam = this.app.getCamera();
        this.rootNode = this.app.getRootNode();
        this.guiNode = this.app.getGuiNode();
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.stateManager = this.app.getStateManager();
        this.guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");

        cubeNode = new Node();
        rootNode.attachChild(cubeNode);
        game = new Game(makeScene(), LENGTH_FOR_WIN, 2);

        currentPlayer = game.getCrossPlayer();
        rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/skys3.jpg", true));

        hud = new Picture("HUD Picture");
        hud.setImage(assetManager, "Textures/cross.png", true);
        hud.setWidth(100);
        hud.setHeight(100);
        hud.setPosition(0, 0);
        guiNode.attachChild(hud);
        //
        cam.lookAt(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
        cam.setLocation(new Vector3f(0, 0, camDistanceZ));

        inputManager.addMapping(MAPPING_DO_MOVE, TRIGGER_DO_MOVE);
        inputManager.addListener(doMoveActionListener, MAPPING_DO_MOVE);
        // rotation by arrows
        inputManager.addMapping(MAPPING_ROTATION_LEFT, TRIGGER_ROTATION_LEFT);
        inputManager.addListener(rotationLeftAnalogListener, MAPPING_ROTATION_LEFT);
        inputManager.addMapping(MAPPING_ROTATION_RIGHT, TRIGGER_ROTATION_RIGHT);
        inputManager.addListener(rotationRightAnalogListener, MAPPING_ROTATION_RIGHT);
//        inputManager.addMapping(MAPPING_ROTATION_UP, TRIGGER_ROTATION_UP);
//        inputManager.addListener(rotationUpAnalogListener, MAPPING_ROTATION_UP);
//        inputManager.addMapping(MAPPING_ROTATION_DOWN, TRIGGER_ROTATION_DOWN);
//        inputManager.addListener(rotationDownAnalogListener, MAPPING_ROTATION_DOWN);

        inputManager.addMapping(MAPPING_TO_MENU, TRIGGER_TO_MENU);
        inputManager.addListener(setBaseLocationActionListener, MAPPING_TO_MENU);
    }

    /**
     * Places cubes on the scene
     *
     * @return empty game table
     */
    private Table makeScene() {
        Table table = new Table(LENGTH_FOR_WIN);
        for (int x = 0; x < LENGTH_FOR_WIN; x++) {
            for (int y = 0; y < LENGTH_FOR_WIN; y++) {
                for (int z = 0; z < LENGTH_FOR_WIN; z++) {
                    // subtract 1.5f from x,y,z to make them samely distanced from (0,0,0)
                    Geometry geom = createCube("Nothing" + x + y + z,
                            new Vector3f((x - 1.5f) * 4, (y - 1.5f) * 4, (z - 1.5f) * 4));
                    table.add(x, y, z, null);
                    cubeNode.attachChild(geom);
                }
            }
        }
//        Quad quad = new Quad(50, 30);
//        Geometry exitButton = new Geometry("ExitButton", quad);
//        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        Texture buttonTexture = assetManager.loadTexture("Textures/exitButton.png");
//        mat1.setTexture("ColorMap", buttonTexture);
//        exitButton.setMaterial(mat1);
//        exitButton.setLocalTranslation(MainApp.WIDTH/2 + 100, MainApp.HEIGHT - 30, 0);
//        guiNode.attachChild(exitButton);
//        
//        Geometry toMenuButton = new Geometry("ToMenuButton", quad);
//        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        buttonTexture = assetManager.loadTexture("Textures/menuButton.png");
//        mat2.setTexture("ColorMap", buttonTexture);
//        toMenuButton.setMaterial(mat2);
//        toMenuButton.setLocalTranslation(MainApp.WIDTH/2 - 25, MainApp.HEIGHT - 30, 0);
//        guiNode.attachChild(toMenuButton);
//        
//        Geometry resetButton = new Geometry("ResetButton", quad);
//        Material mat3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        buttonTexture = assetManager.loadTexture("Textures/restart.png");
//        mat3.setTexture("ColorMap", buttonTexture);
//        resetButton.setMaterial(mat3);
//        resetButton.setLocalTranslation(MainApp.WIDTH/2 - 150, MainApp.HEIGHT - 30, 0);
//        guiNode.attachChild(resetButton);
        return table;
    }

    private Geometry createCube(String name, Vector3f location) {
        Geometry geom = new Geometry(name, box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture cube1Tex;
        if ((location.getX() + 6) / 4 > 0 && (location.getX() + 6) / 4 < 3 &&
                (location.getY() + 6) / 4 > 0 && (location.getY() + 6) / 4 < 3 &&
                (location.getZ() + 6) / 4 > 0 && (location.getZ() + 6) / 4 < 3) {
            cube1Tex = assetManager.loadTexture("Textures/6.png");
        } else {
            cube1Tex = assetManager.loadTexture("Textures/5.png");
        }
        mat.setTexture("ColorMap", cube1Tex);
        geom.setMaterial(mat);
        geom.setLocalTranslation(location);
        return geom;
    }

    public Geometry createNought(String name, Vector3f location, Material mat) {
        Geometry geom = new Geometry(name, torus);
        geom.setMaterial(mat);
        geom.setLocalTranslation(location);
        return geom;
    }

    public Node createCross(String name, Vector3f location, Material mat) {
        Node cross = new Node(name);
        Geometry geom1 = new Geometry("Part1", crossPart);
        geom1.setMaterial(mat);
        Quaternion roll45 = new Quaternion();
        roll45.fromAngleAxis(45 * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
        geom1.setLocalRotation(roll45);
        Geometry geom2 = new Geometry("Part2", crossPart);
        geom2.setMaterial(mat);
        roll45.fromAngleAxis(-45 * FastMath.DEG_TO_RAD, Vector3f.UNIT_Z);
        geom2.setLocalRotation(roll45);
        cross.attachChild(geom1);
        cross.attachChild(geom2);
        cross.setLocalTranslation(location);
        return cross;
    }

    private void showWinner(Class winner) {
        BitmapText hudText = new BitmapText(guiFont, false);
        hudText.setSize(guiFont.getCharSet().getRenderedSize() * 1.2f);
        hudText.setColor(ColorRGBA.Red);
        if (winner.equals(Cross.class)) {
            hudText.setText("Cross has won!<Press Space to return to menu>");

        } else {
            hudText.setText("Nought has won!<Press Space to return to menu>");
        }
        hudText.setLocalTranslation(MainApp.WIDTH / 2 - 170, hudText.getLineHeight(), 0);
        guiNode.attachChild(hudText);
    }

    private void showWinnersConsequence() {
        ArrayList<Coordinate> winnersConsequence = game.getWinnersConsequence();
        int x, y, z;
        String nameOfSpatial;
        Spatial spatial;
        Node cross;
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture cube2Tex = assetManager.loadTexture("Textures/texture3.jpg");
        mat.setTexture("ColorMap", cube2Tex);
        if (currentPlayer == game.getCrossPlayer()) {
            nameOfSpatial = "Cross";
        } else {
            nameOfSpatial = "Nought";
        }
        for (Coordinate coordinate : winnersConsequence) {
            x = coordinate.getX();
            y = coordinate.getY();
            z = coordinate.getZ();
            String name = nameOfSpatial + x + y + z;
            if (currentPlayer == game.getCrossPlayer()) {
                cross = (Node) cubeNode.getChild(name);
                spatial = cross.getChild("Part1");
                spatial.setMaterial(mat);
                spatial = cross.getChild("Part2");
                spatial.setMaterial(mat);
            } else {
                spatial = cubeNode.getChild(name);
                spatial.setMaterial(mat);
            }
        }
    }

    public void changePlayer() {
        if (currentPlayer == game.getCrossPlayer()) {
            currentPlayer = game.getNoughtPlayer();
            hud.setImage(assetManager, "Textures/round.png", true);
        } else {
            currentPlayer = game.getCrossPlayer();
            hud.setImage(assetManager, "Textures/cross.png", true);
        }
    }

    public void stopGame() {
        inputManager.deleteMapping(MAPPING_DO_MOVE);
        guiNode.detachChild(hud);
    }
}
