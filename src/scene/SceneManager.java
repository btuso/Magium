package scene;

import appstate.AppStateListener;
import base.BaseApplication;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import java.util.ArrayList;

/**
 * Switches the scenes of the game. It has support for both disposable and
 * indisposable scenes.
 *
 * <br/>Use the AppStateListener to avoid trying to set a scene before the state
 * is initialized.
 *
 * @author raccoon
 */
public class SceneManager extends AbstractAppState {

    private BaseApplication app;
    private AppStateManager stateManager;
    private ArrayList<AbstractSceneState> scenes;
    private AbstractSceneState currentScene;
    private AbstractSceneState storedScene;
    private AppStateListener appStateListener; // super this to an abstract? figure out just how often listeners will be used for appStates

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (BaseApplication) app;
        this.stateManager = stateManager;
        scenes = new ArrayList<AbstractSceneState>();
        appStateListener.onInitialized();
    }

    @Override
    public void cleanup() {
        super.cleanup();
        scenes.clear();
    }

    //TODO stop depending on the factory enum
    public void loadScene(SceneFactory.Scenes scene) {
        loadScene(scene, false);
    }

    public void loadScene(SceneFactory.Scenes scene, boolean storePreviousScene) {
        AbstractSceneState tempScene = null;
        for (AbstractSceneState sceneState : scenes) {
            if (scene.getSceneClass() == sceneState.getClass()) {
                tempScene = sceneState;
            }
        }
        if (tempScene == null) {
            tempScene = SceneFactory.makeScene(scene);
        }
        setScene(tempScene, storePreviousScene);
    }

    private void setScene(AbstractSceneState scene, boolean storePreviousScene) {
        boolean markAsDestroyed = false;
        if (currentScene != null) {
            if (!currentScene.isDisposable()) {
                scenes.add(currentScene);
            } else if (storePreviousScene) {
                storedScene = currentScene;
            } else {
                markAsDestroyed = true;
            }
            stateManager.detach(currentScene);
        }
        stateManager.attach(scene);
        if (markAsDestroyed) {
            currentScene.onDestroyScene();
        }
        scenes.remove(scene);
        currentScene = scene;
    }

    public void loadStoredScene() {
        setScene(storedScene, false);
    }

    public AbstractSceneState getCurrentScene() {
        return currentScene;
    }

    public AbstractSceneState getSavedScene() {
        return storedScene;
    }

    public void setAppStateListener(AppStateListener appStateListener) {
        this.appStateListener = appStateListener;
    }
}
