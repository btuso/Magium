package scene;

import scene.components.LoadingScreen;
import base.BaseApplication;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import gui.controllers.DefaultLoadingScreenController;

/**
 *
 * @author raccoon
 */
public abstract class AbstractSceneState extends AbstractAppState implements SceneLifeCycle {

    protected BaseApplication app;
    protected AppStateManager stateManager;
    protected AssetManager assetManager;
    protected InputManager inputManager;
    protected boolean paused = true;
    private boolean disposable = true;
    private boolean wasCreated = false;
    private LoadingScreen loadingScreen;

    @Override
    public void initialize(final AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (BaseApplication) app;
        this.stateManager = stateManager;
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();

        if (!wasCreated) {
            onCreateScene();
            wasCreated = true;
        }
        if (loadingScreen == null) {
            onResumeScene();
        } else {
            initializeLoadingScreen();
            stateManager.attach(loadingScreen);
        }
    }

    private void initializeLoadingScreen() {
        if (loadingScreen.hasLoadingScreenController() == false) {
            loadingScreen.setLoadingScreenController(new DefaultLoadingScreenController());
        }
        loadingScreen.setLoadingListener(new LoadingScreen.LoadingListener() {

            public void onLoadingScreenFinished() {
                stateManager.detach(loadingScreen);
                loadingScreen = null;
                onResumeScene();
            }

        });
    }

    @Override
    public void update(float tpf) {
        if (!paused) {
            onSceneUpdate(tpf);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();
        if (wasCreated) {
            onPauseScene();
        }
    }

    public void onResumeScene() {
        paused = false;
//      setEnabled(true);
    }

    public void onPauseScene() {
        paused = true;
//      setEnabled(false);
    }

    /**
     * A disposable scene won't be kept by the SceneManager once it's detached,
     * unless you tell the manager to store it.
     */
    public void setDisposable(boolean disposable) {
        this.disposable = disposable;
    }

    public boolean isDisposable() {
        return disposable;
    }

    public void setLoadingScreen(LoadingScreen loadingScreen) {
        this.loadingScreen = loadingScreen;
    }
}
