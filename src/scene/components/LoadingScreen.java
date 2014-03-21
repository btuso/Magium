package scene.components;

import base.BaseApplication;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.niftygui.NiftyJmeDisplay;
import de.lessvoid.nifty.Nifty;
import gui.filepaths.NiftyLayouts;
import gui.controllers.LoadingScreenController;
import gui.filepaths.Images;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 *
 * @author raccoon
 */
public abstract class LoadingScreen extends AbstractAppState {

    private BaseApplication app;
    private List<String> loadingSteps;
    private int totalSteps;
    private int currentStepIndex;
    private Nifty nifty;
    private NiftyJmeDisplay niftyDisplay;
    private LoadingScreenController loadingScreenController;
    private Future currentlyLoadingStep;
    private LoadingListener loadingListener;
    private String backgroundImagePath = Images.DEFAULT_LOADING_SCREEN;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (BaseApplication) app;
        createNiftyLoadingScreen();
        initializeLoadingSteps();
        // TODO make nifty capture and ignore input
        loadingScreenController.setBackgroundImage(backgroundImagePath);
    }

    private void createNiftyLoadingScreen() {
        niftyDisplay = new NiftyJmeDisplay(app.getAssetManager(), app.getInputManager(),
                                           app.getAudioRenderer(), app.getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        nifty.fromXml(NiftyLayouts.LOADING_SCREEN_LAYOUT_PATH, LoadingScreenController.START_SCREEN, loadingScreenController);
        app.getGuiViewPort().addProcessor(niftyDisplay);
    }

    private void initializeLoadingSteps() {
        this.loadingSteps = defineLoadingSteps();
        assert loadingSteps.isEmpty();
        if (loadingSteps == null || loadingSteps.isEmpty()) {
            throw new Error("Loading steps can't be null nor empty.");
        }
        this.totalSteps = loadingSteps.size();
    }

    @Override
    public void update(float tpf) {
        if (currentStepIndex < totalSteps) {
            // TODO extract to method, figure out a good name.
            if (currentlyLoadingStep == null) {
                updateLoadingScreenMessage();
                currentlyLoadingStep = loadNextStep();
            } else if (currentlyLoadingStep.isDone()) {
                currentlyLoadingStep = null;
                currentStepIndex++;
                updateLoadingScreenProgress();
            }
        } else {
            if (loadingScreenController.isAnimationDone()) {
                completeLoading();
            }
        }
    }

    private Future<Integer> loadNextStep() {
        return app.getExecutor().submit(stepLoader);
    }
    private Callable<Integer> stepLoader = new Callable<Integer>() {

        public Integer call() throws Exception {
            loadResourcesByStep(currentStepIndex);
            return currentStepIndex;
        }

    };

    private void updateLoadingScreenMessage() {
        final String message = loadingSteps.get(currentStepIndex);
        loadingScreenController.setCurrentLoadingMessage(message);
    }

    private void updateLoadingScreenProgress() {
        final int currentProgressPercentage = (currentStepIndex * 100) / totalSteps;
        loadingScreenController.updateProgressIndicator(currentProgressPercentage);
    }

    private void completeLoading() {
        onLoadCompleted();
        detachLoadingScreen();
        if (loadingListener != null) {
            loadingListener.onLoadingScreenFinished();
        }
    }

    private void detachLoadingScreen() {
        app.getGuiViewPort().removeProcessor(niftyDisplay);
        nifty.exit();
    }

    public void setLoadingScreenController(LoadingScreenController controller) {
        this.loadingScreenController = controller;
    }

    public boolean hasLoadingScreenController() {
        return loadingScreenController != null;
    }

    public void setLoadingListener(LoadingListener loadingListener) {
        this.loadingListener = loadingListener;
    }

    public void setBackgroundImagePath(String backgroundImagePath) {
        this.backgroundImagePath = backgroundImagePath;
    }

    /**
     * The steps to be shown in the loading screen.
     */
    public abstract List<String> defineLoadingSteps();

    /**
     * This will be called as many times as steps defined, so use a scoped
     * switch to decide what to load.
     * <br/> Do <b>NOT</b> modify the scene from here.
     */
    public abstract void loadResourcesByStep(int currentStep);

    /**
     * You can safely modify the scene from here.
     */
    public abstract void onLoadCompleted();

    public interface LoadingListener {

        void onLoadingScreenFinished();
    }
}
