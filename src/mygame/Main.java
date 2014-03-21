package mygame;

import appstate.AppStateListener;
import base.BaseApplication;
import scene.SceneFactory;
import scene.SceneManager;

/**
 * @author raccoon
 */
public class Main extends BaseApplication {

    protected SceneManager sceneManager;

    @Override
    public void simpleInitApp() {
        super.simpleInitApp();
        releaseMouse();
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
