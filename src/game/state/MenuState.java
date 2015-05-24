package game.state;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import game.MainApp;

/**
 * Class with initial screen which player see at the beginning
 *
 * @author Ivan
 */
public class MenuState extends AbstractAppState {
    private MenuState thisState = this;
    private SimpleApplication app;
    private Node guiNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private Quad box = new Quad(90, 90);
    private Quad quad = new Quad(300, 80);
    private Material materialForButton1;
    private Material materialForButton2;
    private ViewPort viewPort;

    private final static Trigger TRIGGER_ON_BUTTON_CLICK = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
    private final static String MAPPING_ON_BUTTON_CLICK = "ButtonClick";
    private ActionListener onButtonClickActionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals(MAPPING_ON_BUTTON_CLICK)) {
                CollisionResults results = new CollisionResults();
                Vector2f location = inputManager.getCursorPosition();
                Vector3f origin = new Vector3f(location.getX(), location.getY(), 0);
                Vector3f dir = new Vector3f(0f, 0f, 1f);
                Ray ray = new Ray(origin, dir);
                guiNode.collideWith(ray, results);
                if (results.size() > 0) {
                    Geometry target = results.getClosestCollision().getGeometry();
                    String targetsName = target.getName();
                    if (targetsName.equals("PCButton")) {
                        if (isPressed) {
                            Texture button1Tex = assetManager.loadTexture("Textures/pcOn1.png");
                            materialForButton1.setTexture("ColorMap", button1Tex);
                        }
                        if (!isPressed) {
                            Texture button1Tex = assetManager.loadTexture("Textures/pcOff1.png");
                            materialForButton1.setTexture("ColorMap", button1Tex);
                            PCvsHumanAppState gameState = new PCvsHumanAppState();
                            stateManager.detach(thisState);
                            stateManager.attach(gameState);
                        }
                    }
                    if (targetsName.equals("HumanButton")) {
                        if (isPressed) {
                            Texture button2Tex = assetManager.loadTexture("Textures/humanOn1.png");
                            materialForButton2.setTexture("ColorMap", button2Tex);
                        }
                        if (!isPressed) {
                            Texture button2Tex = assetManager.loadTexture("Textures/humanOff1.png");
                            materialForButton2.setTexture("ColorMap", button2Tex);
                            HumanVsHumanAppState gameState = new HumanVsHumanAppState();
                            stateManager.detach(thisState);
                            stateManager.attach(gameState);
                        }
                    }
                    if (targetsName.equals("ExitButton") && !isPressed) {
                        app.stop();
                    }
//                    if (targetsName.equals("ToMenuButton") && !isPressed) {
//                        System.out.println("ya najat2");
//                        stateManager.detach(thisState);
//                        //HumanVsHumanAppState state = new HumanVsHumanAppState();
//                        MenuState state = new MenuState();
//                        stateManager.attach(state);
//                    }
//                    if (targetsName.equals("ResetButton") && !isPressed) {
//                        System.out.println("ya najat1");
//                    }                    
                }
            }
        }
    };

    @Override
    public void update(float tpf) {

    }

    @Override
    public void cleanup() {
        guiNode.detachAllChildren();
        inputManager.deleteMapping(MAPPING_ON_BUTTON_CLICK);
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (SimpleApplication) app;
        this.guiNode = this.app.getGuiNode();
        this.assetManager = this.app.getAssetManager();
        this.inputManager = this.app.getInputManager();
        this.stateManager = this.app.getStateManager();
        this.viewPort = this.app.getViewPort();


        viewPort.setBackgroundColor(ColorRGBA.Black);
        inputManager.clearMappings();

        drawMenu();
        inputManager.addMapping(MAPPING_ON_BUTTON_CLICK, TRIGGER_ON_BUTTON_CLICK);
        inputManager.addListener(onButtonClickActionListener, MAPPING_ON_BUTTON_CLICK);
    }

    private void drawMenu() {
        Geometry vsPCButton = new Geometry("PCButton", box);
        materialForButton1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture button1Tex = assetManager.loadTexture("Textures/pcOff1.png");
        materialForButton1.setTexture("ColorMap", button1Tex);
        vsPCButton.setMaterial(materialForButton1);
        vsPCButton.setLocalTranslation(new Vector3f(MainApp.WIDTH * 2 / 3 - 45, MainApp.HEIGHT / 2, 0));
        guiNode.attachChild(vsPCButton);

        Geometry vsHumanButton = new Geometry("HumanButton", box);
        materialForButton2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture button2Tex = assetManager.loadTexture("Textures/humanOff1.png");
        materialForButton2.setTexture("ColorMap", button2Tex);
        vsHumanButton.setMaterial(materialForButton2);
        vsHumanButton.setLocalTranslation(new Vector3f(MainApp.WIDTH / 3 - 45, MainApp.HEIGHT / 2, 0));
        guiNode.attachChild(vsHumanButton);

        Geometry menuTextGeom = new Geometry("MenuText", quad);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture menuText = assetManager.loadTexture("Textures/menuText.png");
        mat.setTexture("ColorMap", menuText);
        menuTextGeom.setMaterial(mat);
        menuTextGeom.setLocalTranslation(MainApp.WIDTH / 2 - 150, MainApp.HEIGHT - 80, 0);
        guiNode.attachChild(menuTextGeom);

        Geometry menuExitButton = new Geometry("ExitButton", new Quad(50, 20));
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture menuExit = assetManager.loadTexture("Textures/exitButton.png");
        mat1.setTexture("ColorMap", menuExit);
        menuExitButton.setMaterial(mat1);
        menuExitButton.setLocalTranslation(MainApp.WIDTH / 2 - 25, 100, 0);
        guiNode.attachChild(menuExitButton);
    }
}
