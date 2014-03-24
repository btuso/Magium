package scene.gamescenes;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import controls.PlayerMovementControl;
import controls.BasicMovementAnimationControl;
import scene.AbstractSceneState;

/**
 *
 * @author raccoon
 */
public class TestScene extends AbstractSceneState {

    private BulletAppState bulletAppState;
    private Geometry floorGeometry;
    private RigidBodyControl floorRigid;
    private BetterCharacterControl betterCharacterControl;
    private boolean lockView = false;
    private Node player;

    public void onCreateScene() {
        bulletAppState = stateManager.getState(BulletAppState.class);
//        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        app.getFlyByCamera().setMoveSpeed(40f);
        app.getCamera().setLocation(new Vector3f(-8, 21, 37));
        app.getFlyByCamera().setEnabled(false);

        setUpFloor();
        setUpLighting();
        setUpPlayer();
        setupKeys();
        app.getCamera().lookAt(player.getWorldTranslation(), Vector3f.UNIT_Y);
    }

    private void setUpLighting() {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection((new Vector3f(-0.5f, -0.5f, -0.5f)).normalizeLocal());
        sun.setColor(ColorRGBA.White);
        app.getRootNode().addLight(sun);
    }

    private void setUpFloor() {
        floorGeometry = createFloor();
        initFloorPhysics();
        app.getRootNode().attachChild(floorGeometry);
    }

    private Geometry createFloor() {
        Box floorMesh = new Box(30f, 0.1f, 30f);
        floorGeometry = new Geometry("Colored Box", floorMesh);
        Material floorMaterial = createLightedMaterial(ColorRGBA.DarkGray, ColorRGBA.LightGray);
        floorGeometry.setMaterial(floorMaterial);
        return floorGeometry;
    }

    private void initFloorPhysics() {
        floorRigid = new RigidBodyControl(0);
        floorGeometry.addControl(floorRigid);
        bulletAppState.getPhysicsSpace().add(floorRigid);
    }

    private Material createLightedMaterial(ColorRGBA ambientColor, ColorRGBA diffuseColor) {
        Material floorMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        floorMaterial.setBoolean("UseMaterialColors", true);
        floorMaterial.setColor("Ambient", ambientColor);
        floorMaterial.setColor("Diffuse", diffuseColor);
        return floorMaterial;
    }

    private void setupKeys() {
        app.getInputManager().addMapping("Lock View", new KeyTrigger(KeyInput.KEY_RETURN));
        app.getInputManager().addListener(actionsListener, "Lock View");
    }
    ActionListener actionsListener = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Lock View")) {
                if (isPressed && lockView) {
                    lockView = false;
                } else if (isPressed && !lockView) {
                    lockView = true;
                }
                app.getFlyByCamera().setEnabled(!lockView);
//                camNode.setEnabled(lockView);
            }

        }

    };

    public void onSceneUpdate(float tpf) {
    }

    public void onDestroyScene() {
    }

    private void setUpPlayer() {
        createPlayer();
        setUpPlayerPhysics();
        setUpPlayerMovements();
        setUpPlayerAnimations();
    }

    private void createPlayer() {
        player = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        player.setLocalScale(5f);
        player.setLocalTranslation(0, 0.1f, 0);
        player.setUserData("walkingSpeed", PlayerMovementControl.WALK_SPEED);//refa to constant? where should i put it?
        app.getRootNode().attachChild(player);
    }

    private void setUpPlayerPhysics() {
        betterCharacterControl = new BetterCharacterControl(1.5f, 6.7f, 8f);
        betterCharacterControl.setJumpForce(new Vector3f(0, 20f, 0));
        betterCharacterControl.setGravity(new Vector3f(0, 10, 0));
        betterCharacterControl.setJumpForce(new Vector3f(0, 30, 0));
        player.addControl(betterCharacterControl);
        bulletAppState.getPhysicsSpace().add(betterCharacterControl);
    }

    private void setUpPlayerMovements() {
        PlayerMovementControl movementControl = new PlayerMovementControl(inputManager, player);
        player.addControl(movementControl);
    }

    private void setUpPlayerAnimations() {
        BasicMovementAnimationControl walkingAnimationControl = new BasicMovementAnimationControl(player);
        player.addControl(walkingAnimationControl);
    }
}
