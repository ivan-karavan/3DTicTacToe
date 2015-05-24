package game;

import com.jme3.app.SimpleApplication;
import com.jme3.renderer.RenderManager;
import com.jme3.system.AppSettings;
import game.state.MenuState;

import java.io.IOException;

/**
 * From this class application starts
 *
 * @author Ivan
 */
public class MainApp extends SimpleApplication {

    public static int WIDTH;
    public static int HEIGHT;

    public static void main(String[] args) throws IOException {
        MainApp app = new MainApp();

        app.setDisplayFps(false);
        app.setDisplayStatView(false);
        AppSettings settings = new AppSettings(true);
        settings.setTitle("Tic Tac Toe 3D");
        settings.setSettingsDialogImage("Textures/menu.png");
        app.setSettings(settings);

        app.start();
//                Toolkit toolkit=Toolkit.getDefaultToolkit();
//        u=app.getClass().getResource("/resources/1.png");
//        Image image;
//        image=ImageIO.read(u);
//        AppSettings set=new AppSettings(true);
//        BufferedImage[]   b =new BufferedImage[2];
//        b[0]=convert(image);
//         b[1]=convert(image);
//                set.setIcons(b);
//            app.setSettings(set);
    }

    //
//    private static URL u;
//    private static BufferedImage convert(Image image) {
//        if (image instanceof BufferedImage)
//            if (image instanceof BufferedImage) {
//                return (BufferedImage) image;
//            }
//    BufferedImage bimage = new BufferedImage(image.getWidth(null),image.getHeight(null),
//            BufferedImage.TYPE_INT_ARGB);
//    Graphics2D bGr = bimage.createGraphics();
//    bGr.drawImage(image, 0, 0,null);
//    bGr.dispose();
//    return bimage;
//    }
    //
    @Override
    public void simpleInitApp() {
        HEIGHT = settings.getHeight();
        WIDTH = settings.getWidth();

        flyCam.setDragToRotate(true);
        inputManager.setCursorVisible(true);
        flyCam.setMoveSpeed(10f);

        MenuState gameState = new MenuState();
        stateManager.attach(gameState);
    }

    @Override
    public void simpleUpdate(float tpf) {

    }

    @Override
    public void simpleRender(RenderManager rm) {
    }
}
