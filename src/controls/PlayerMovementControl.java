package controls;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author raccoon
 */
public class PlayerMovementControl extends AbstractControl {

    public static final float WALK_SPEED = 10;
    private static final float RUN_SPEED = 18;
    private static final float rotationSpeed = 3f;

    private enum ActionMappings {// move this to its own class, along with input register

        WALK_FORWARDS("Walk Forward", new KeyTrigger(KeyInput.KEY_W)),
        WALK_BACKWARDS("Walk Backwards", new KeyTrigger(KeyInput.KEY_S)),
        STRAFE_LEFT("Strafe Left", new KeyTrigger(KeyInput.KEY_A)),
        STRAFE_RIGHT("Strafe Right", new KeyTrigger(KeyInput.KEY_D)),
        ROTATE_LEFT("Rotate Left", new KeyTrigger(KeyInput.KEY_Q)),
        ROTATE_RIGHT("Rotate Right", new KeyTrigger(KeyInput.KEY_E)),
        MOUSE_ROTATE_LEFT("Rotate Left", new MouseAxisTrigger(MouseInput.AXIS_X, true)),
        MOUSE_ROTATE_RIGHT("Rotate Right", new MouseAxisTrigger(MouseInput.AXIS_X, false)),
        RUN("Run", new KeyTrigger(KeyInput.KEY_LSHIFT)),
        JUMP("Jump", new KeyTrigger(KeyInput.KEY_SPACE)),
        DUCK("Duck", new KeyTrigger(KeyInput.KEY_LCONTROL));
        private String mappingName;
        private Trigger trigger;

        private ActionMappings(String mappingName, Trigger trigger) {
            this.mappingName = mappingName;
            this.trigger = trigger;
        }
    }
    private InputManager inputManager;
    private Spatial character;
    private BetterCharacterControl characterControl;
    private BasicMovementAnimationControl animationControl;
    private boolean forward = false, backward = false;
    private boolean leftStrafe = false, rightStrafe = false;
    private boolean isRunning = false;
    private float rotationValue;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Vector3f viewDirection = new Vector3f(0, 0, 1);

    public PlayerMovementControl(InputManager inputManager, Node character) {
        this.inputManager = inputManager;
        this.character = character;
        findCharacterControl(character);
        animationControl = character.getControl(BasicMovementAnimationControl.class);
        setUpMovementMappings();
    }

    private void findCharacterControl(Node character) {
        characterControl = getCharacterControlForNode(character);
        if (characterControl == null) {
            characterControl = getCharacterControlForNode(character.getParent());//refactor into abstract
        }
    }

    private BetterCharacterControl getCharacterControlForNode(Node character) {
        return character.getControl(BetterCharacterControl.class);
    }

    private void setUpMovementMappings() {
        for (ActionMappings mapping : ActionMappings.values()) {
            addKeyMapping(mapping);
        }
        addListenerMappings(basicMovementListener, ActionMappings.WALK_FORWARDS, ActionMappings.WALK_BACKWARDS);
        addListenerMappings(basicMovementListener, ActionMappings.STRAFE_LEFT, ActionMappings.STRAFE_RIGHT);
        addListenerMappings(basicMovementListener, ActionMappings.JUMP, ActionMappings.RUN, ActionMappings.DUCK);
        addListenerMappings(basicMovementListener, ActionMappings.ROTATE_LEFT, ActionMappings.ROTATE_RIGHT);
        addListenerMappings(mouseActionListener, ActionMappings.MOUSE_ROTATE_LEFT, ActionMappings.MOUSE_ROTATE_RIGHT);
    }

    private void addKeyMapping(ActionMappings keyMapping) {
        inputManager.addMapping(keyMapping.mappingName, keyMapping.trigger);
    }

    private void addListenerMappings(InputListener listener, ActionMappings... keyMappings) {
        String[] names = new String[keyMappings.length];
        for (int i = 0; i < keyMappings.length; i++) {
            names[i] = keyMappings[i].mappingName;
        }
        inputManager.addListener(listener, names);
    }
    final ActionListener basicMovementListener = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {
            ActionMappings mapping = getActionMappingForMappingName(name);
            if (mapping == null || !isEnabled()) {//maybe introduce a "NOT_DEFINED" mapping to avoid nulls
                return;
            }
            switch (mapping) {
                case WALK_FORWARDS:
                    forward = isPressed;
                    break;
                case WALK_BACKWARDS:
                    backward = isPressed;
                    break;
                case STRAFE_LEFT:
                    leftStrafe = isPressed;
                    break;
                case STRAFE_RIGHT:
                    rightStrafe = isPressed;
                    break;
                case ROTATE_LEFT:
                    rotationValue = tpf;
                    break;
                case ROTATE_RIGHT:
                    rotationValue = -tpf;
                    break;
                case RUN:
                    isRunning = isPressed;
                    break;
                case JUMP:
                    if (isPressed == true && characterControl.isOnGround()) {
                        characterControl.setDucked(false);
                        characterControl.jump();
                        if (animationControl != null) {
                            animationControl.startJumpAnimation();
                        }
                    }
                    break;
                case DUCK:
                    isRunning = false;
                    characterControl.setDucked(isPressed);
                    if (isPressed == true) {
                        if (animationControl != null) {
                            animationControl.startDuckAnimation();
                        }
                    }
                    break;
            }
        }

    };
    AnalogListener mouseActionListener = new AnalogListener() {

        public void onAnalog(String name, float value, float tpf) {
            ActionMappings mapping = getActionMappingForMappingName(name);
            if (mapping == null || !isEnabled()) {//maybe introduce a "NOT_DEFINED" mapping to avoid nulls
                return;
            }
            switch (mapping) {
                case MOUSE_ROTATE_LEFT:
                    rotationValue = value;
                    break;
                case MOUSE_ROTATE_RIGHT:
                    rotationValue = -value;
            }
        }

    };

    private ActionMappings getActionMappingForMappingName(String mappingName) {
        ActionMappings foundMapping = null;
        for (ActionMappings mapping : ActionMappings.values()) {
            if (mapping.mappingName.equals(mappingName)) {
                foundMapping = mapping;
            }
        }
        return foundMapping;
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (!enabled) {
            return;
        }
        Vector3f modelForwardDirection = character.getWorldRotation().mult(Vector3f.UNIT_Z);
        Vector3f modelLeftDirection = character.getWorldRotation().mult(Vector3f.UNIT_X);
        walkDirection.set(0, 0, 0);

        applyStrafeToWalkDirection(modelLeftDirection);
        applyForwardsOrBackwardsToWalkDirection(modelForwardDirection);
        applyRotateToViewDirection();

        characterControl.setWalkDirection(walkDirection);
        characterControl.setViewDirection(viewDirection);
    }

    private void applyStrafeToWalkDirection(Vector3f leftDirection) {
        if (leftStrafe) {
            walkDirection.addLocal(leftDirection.mult(WALK_SPEED));
        } else if (rightStrafe) {
            walkDirection.addLocal(leftDirection.negate().multLocal(WALK_SPEED));
        }
    }

    private void applyForwardsOrBackwardsToWalkDirection(Vector3f modelForwardDir) {
        if (forward) {
            float speed = isRunning ? RUN_SPEED : WALK_SPEED;
            walkDirection.addLocal(modelForwardDir.mult(speed));
        } else if (backward) {
            walkDirection.addLocal(modelForwardDir.negate().multLocal(WALK_SPEED));
        }
    }

    private void applyRotateToViewDirection() {
        if (rotationValue != 0) {
            Quaternion rotate = new Quaternion().fromAngleAxis(FastMath.PI * rotationValue, Vector3f.UNIT_Y);
            rotate.multLocal(viewDirection);
            rotationValue = 0;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void removeKeyMappings() {//Break this method in to two parts, so that input can be temporally paused. (Write an unpause method too)
        for (ActionMappings keyMapping : ActionMappings.values()) {
            inputManager.deleteMapping(keyMapping.mappingName);
        }
        inputManager.removeListener(basicMovementListener);
    }
}
