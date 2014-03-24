package scene;

/**
 *
 * @author raccoon
 */
interface SceneLifeCycle {

    void onCreateScene();

    void onResumeScene();

    void onSceneUpdate(float tpf);

    void onPauseScene();

    void onDestroyScene();
}
