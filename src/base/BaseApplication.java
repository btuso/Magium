package base;

import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import mygame.Main;

/**
 *
 * @author raccoon
 */
public class BaseApplication extends SimpleApplication {

    ScheduledThreadPoolExecutor executor;

    public static void main(String[] args) {
        Main app = new Main();
        app.setShowSettings(Settings.showSettingsScreen);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        if (!Settings.showStats) {
            stateManager.getState(StatsAppState.class).toggleStats();
        }
        executor = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() + 1);
    }

    @Override
    public void destroy() {
        super.destroy();
        executor.shutdown();
    }

    public ScheduledThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void captureMouse() {
        flyCam.setDragToRotate(false);
    }

    public void releaseMouse() {
        flyCam.setDragToRotate(true);
    }
}
