package mygame;

import appstate.AppStateListener;
import base.BaseApplication;
import com.jme3.bullet.BulletAppState;
import scene.SceneFactory;
import scene.SceneManager;

/**
 * @author raccoon
 */
public class Main extends BaseApplication {

    private BulletAppState bulletAppState;
    private SceneManager sceneManager;

    @Override
    public void simpleInitApp() {
        super.simpleInitApp();
        bulletAppState = new BulletAppState();
        releaseMouse();

        stateManager.attach(bulletAppState);

        sceneManager = new SceneManager();
        sceneManager.setAppStateListener(new AppStateListener() {

            public void onInitialized() {
                sceneManager.loadScene(SceneFactory.Scenes.TEST);
            }

        });
        stateManager.attach(sceneManager);
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf);
    }
}
