package scene;

/**
 *
 * @author raccoon
 */
interface SceneLifeCycle {

    void onCreateScene();

    void onResumeScene();

    void onPauseScene();

    void onDestroyScene();
}
