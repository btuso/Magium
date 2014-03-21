package scene;

import scene.components.LoadingScreen;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author raccoon
 */
public class TestScene extends AbstractSceneState {

    private Geometry cube;

    @Override
    public void update(float tpf) {
        if (!paused) {
            cube.rotate(tpf, tpf * 2, tpf / 2);
        }
    }

    public void onCreateScene() {
        setLoadingScreen(new LoadingScreen() {

            @Override
            public List<String> defineLoadingSteps() {
                return Arrays.asList("Creating a cube",
                                     "Waiting 1 second",
                                     "Waiting 1 second again",
                                     "Finishing up");
            }

            @Override
            public void loadResourcesByStep(int currentStep) {
                switch (currentStep) {
                    case 0: {
                        Box boxMesh = new Box(1f, 1f, 1f);
                        Geometry boxGeo = new Geometry("Colored Box", boxMesh);
                        Material boxMat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                        boxMat.setColor("Color", ColorRGBA.Green);
                        boxGeo.setMaterial(boxMat);
                        cube = boxGeo;
                    }
                    break;
                    case 3: {
                    }
                    break;
                    default: {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(TestScene.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            }

            @Override
            public void onLoadCompleted() {
                app.getRootNode().attachChild(cube);
            }

        });
    }

    @Override
    public void onResumeScene() {
        super.onResumeScene();
    }

    @Override
    public void onPauseScene() {
        super.onPauseScene();
    }

    public void onDestroyScene() {
    }
}
