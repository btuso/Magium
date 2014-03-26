package controls;

import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.CameraControl.ControlDirection;

/**
 *
 * @author raccoon
 */
public class BasicFollowCameraControl extends AbstractControl implements AnalogListener {

    private static final String ZOOM_IN = "Zoom In";
    private static final String ZOOM_OUT = "Zoom Out";
    private static final float CAMERA_VIEW_ANGLE = 30f;
    private static final float ZOOM_FACTOR = 15f;
    private Node characterParentNode;
    private BetterCharacterControl characterControl;
    private CameraNode cameraNode;
    private Vector3f initialUpVector;
    private float characterHeight;
    private float currentZoom = 0.5f;

    public BasicFollowCameraControl(InputManager inputManager, Node characterParentNode, Camera camera) {
        this.characterParentNode = characterParentNode;
        this.characterControl = characterParentNode.getControl(BetterCharacterControl.class);
        initialUpVector = camera.getUp();
        createCamera(camera);
        setUpCameraInitialSettings();
        setUpCameraZoomInput(inputManager);
    }

    private void createCamera(Camera camera) {
        cameraNode = new CameraNode("CameraNode", camera);
        cameraNode.setControlDir(ControlDirection.SpatialToCamera);
        characterParentNode.attachChild(cameraNode);
    }

    private void setUpCameraInitialSettings() {
        updateCharacterHeightCameraOffset();
        updateCameraPosition();
        updateCameraLookAtPosition();
    }

    public void updateCharacterHeightCameraOffset() {
        BoundingBox characterBoundingBox = (BoundingBox) characterParentNode.getWorldBound();
        characterHeight = characterBoundingBox.getYExtent();
    }

    private void updateCameraPosition() {
        Vector3f viewDirection = characterControl.getViewDirection();
        float cameraY = viewDirection.y + characterHeight + calculateCameraPositionOnAxis() * ZOOM_FACTOR;
        float cameraZ = viewDirection.z + calculateCameraPositionOnAxis() * -ZOOM_FACTOR;
        Vector3f cameraPosition = new Vector3f(viewDirection.x, cameraY, cameraZ);
        cameraNode.setLocalTranslation(cameraPosition);
    }

    private float calculateCameraPositionOnAxis() {
        return FastMath.cos(FastMath.PI + currentZoom * FastMath.PI) + 1;
    }

    private void updateCameraLookAtPosition() {
        Vector3f parentNodeWorldTranslation = characterParentNode.getWorldTranslation();
        Vector3f cameraViewPosition = characterControl.getViewDirection().clone();
        cameraViewPosition.multLocal(CAMERA_VIEW_ANGLE);
        cameraViewPosition.addLocal(parentNodeWorldTranslation);
        cameraNode.lookAt(cameraViewPosition, initialUpVector);
    }

    private void setUpCameraZoomInput(InputManager inputManager) {
        //TODO Maybe move input manager to a control? That way it's easier to pause player input.
        //If input manager is null, avoid mappings, but attach the camera anyway.
        inputManager.addMapping(ZOOM_IN, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping(ZOOM_OUT, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addListener(this, ZOOM_IN, ZOOM_OUT);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (!enabled) {
            return;
        }

        updateCameraPosition();
        updateCameraLookAtPosition();
    }

    public void onAnalog(String name, float value, float tpf) {
        if (!enabled) {
            return;
        }

        if (ZOOM_IN.equals(name)) {
            if (currentZoom > 0.1) {
                currentZoom -= 0.1f;
            }
        } else if (ZOOM_OUT.equals(name)) {
            if (currentZoom < 1) {
                currentZoom += 0.1f;
            }
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
