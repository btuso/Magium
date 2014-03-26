package scene.gamescenes;

import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import controls.BasicFollowCameraControl;
import controls.PlayerMovementControl;
import controls.BasicMovementAnimationControl;
import scene.AbstractSceneState;

/**
 *
 * @author raccoon
 */
public class TestScene extends AbstractSceneState {

    private BulletAppState bulletAppState;
    private Spatial sceneModel;
    private RigidBodyControl landscape;
    private Node playerNode;
    private Node playerModel;
    private BetterCharacterControl betterCharacterControl;

    public void onCreateScene() {
        app.getFlyByCamera().setEnabled(false);
        bulletAppState = stateManager.getState(BulletAppState.class);
//        bulletAppState.getPhysicsSpace().enableDebug(assetManager);

        setUpTown();
        setUpLighting();
        setUpPlayer();
        setupKeys();
        setUpCamera();
    }

    private void setUpLighting() {
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        app.getRootNode().addLight(al);

        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        app.getRootNode().addLight(dl);
    }

    private void setUpTown() {
        assetManager.registerLocator("town.zip", ZipLocator.class);
        sceneModel = assetManager.loadModel("main.scene");
        sceneModel.setLocalScale(2f);
        initTownPhysics();
        app.getRootNode().attachChild(sceneModel);
    }

    private void initTownPhysics() {
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) sceneModel);
        landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        bulletAppState.getPhysicsSpace().add(landscape);
    }

    private Material createLightedMaterial(ColorRGBA ambientColor, ColorRGBA diffuseColor) {
        Material floorMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        floorMaterial.setBoolean("UseMaterialColors", true);
        floorMaterial.setColor("Ambient", ambientColor);
        floorMaterial.setColor("Diffuse", diffuseColor);
        return floorMaterial;
    }

    private void setupKeys() {
        app.getInputManager().addMapping("Custom", new KeyTrigger(KeyInput.KEY_RETURN));
        app.getInputManager().addListener(actionsListener, "Lock View");
    }
    ActionListener actionsListener = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Custom")) {
                System.err.println("Custom");
            }

        }

    };

    private void setUpPlayer() {
        playerNode = new Node("PlayerNode");
        createPlayer();
        setUpPlayerPhysics();
        setUpPlayerMovements();
        setUpPlayerAnimations();
        app.getRootNode().attachChild(playerNode);
    }

    private void createPlayer() {
        playerModel = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        playerModel.setLocalScale(5f);
        playerModel.setLocalTranslation(0, 0.1f, 0);
        playerModel.setUserData("walkingSpeed", PlayerMovementControl.WALK_SPEED);//refa to constant? where should i put it?
        playerNode.attachChild(playerModel);
    }

    private void setUpPlayerPhysics() {
        betterCharacterControl = new BetterCharacterControl(1.5f, 6.7f, 8f);
        betterCharacterControl.setJumpForce(new Vector3f(0, 20f, 0));
        betterCharacterControl.setGravity(new Vector3f(0, 10, 0));
        betterCharacterControl.setJumpForce(new Vector3f(0, 80, 0));
        playerNode.addControl(betterCharacterControl);
        bulletAppState.getPhysicsSpace().add(betterCharacterControl);
    }

    private void setUpPlayerMovements() {
        PlayerMovementControl movementControl = new PlayerMovementControl(inputManager, playerModel);
        playerModel.addControl(movementControl);
    }

    private void setUpPlayerAnimations() {
        BasicMovementAnimationControl walkingAnimationControl = new BasicMovementAnimationControl(playerModel);
        playerModel.addControl(walkingAnimationControl);
    }

    private void setUpCamera() {
        BasicFollowCameraControl cameraControl = new BasicFollowCameraControl(inputManager, playerNode, app.getCamera());
        playerNode.addControl(cameraControl);
    }

    public void onSceneUpdate(float tpf) {
    }

    public void onDestroyScene() {
    }
}
