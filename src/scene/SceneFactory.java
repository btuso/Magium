package scene;

import java.util.logging.Level;
import java.util.logging.Logger;
import scene.gamescenes.TestScene;
import scene.gamescenes.WelcomeScene;

/**
 *
 * @author raccoon
 */
public class SceneFactory {

    public static enum Scenes {

        WELCOME(WelcomeScene.class, true),
        TEST(TestScene.class, true);
        //
        private Class<? extends AbstractSceneState> sceneClass;
        private boolean disposable;

        private Scenes(Class<? extends AbstractSceneState> sceneClass, boolean disposable) {
            this.sceneClass = sceneClass;
            this.disposable = disposable;
        }

        public Class<? extends AbstractSceneState> getSceneClass() {
            return sceneClass;
        }

        public boolean isDisposable() {
            return disposable;
        }
    }

    public static <T extends AbstractSceneState> T makeScene(SceneFactory.Scenes scene) {
        try {
            T sceneState = (T) scene.getSceneClass().newInstance();
            sceneState.setDisposable(scene.isDisposable());
            return sceneState;
        } catch (InstantiationException ex) {
            Logger.getLogger(SceneFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(SceneFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
