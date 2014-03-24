package controls;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.InputListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author raccoon
 */
public class PlayerMovementControl extends AbstractControl {

    public static final float WALK_SPEED = 8;
    private static final float RUN_SPEED = 16;

    private enum ActionMappings {// move this to its own class?

        WALK_FORWARDS("Walk Forward", KeyInput.KEY_W),
        WALK_BACKWARDS("Walk Backwards", KeyInput.KEY_S),
        ROTATE_LEFT("Rotate Left", KeyInput.KEY_A),
        ROTATE_RIGHT("Rotate Right", KeyInput.KEY_D),
        STRAFE_LEFT("Strafe Left", KeyInput.KEY_Q),
        STRAFE_RIGHT("Strafe Right", KeyInput.KEY_E),
        RUN("Run", KeyInput.KEY_LSHIFT),
        JUMP("Jump", KeyInput.KEY_SPACE),
        DUCK("Duck", KeyInput.KEY_LCONTROL);
        private String mappingName;
        private int keyInput;

        private ActionMappings(String mappingName, int keyInput) {
            this.mappingName = mappingName;
            this.keyInput = keyInput;
        }
    }
    private InputManager inputManager;
    private Spatial character;
    private BetterCharacterControl characterControl;
    private boolean controlStrated = false;
    private boolean forward = false, backward = false;
    private boolean leftStrafe = false, rightStrafe = false;
    private boolean leftRotate = false, rightRotate = false;
    private boolean isRunning = false;
    private Vector3f walkDirection = new Vector3f(0, 0, 0);
    private Vector3f viewDirection = new Vector3f(0, 0, 1);

    public PlayerMovementControl(InputManager inputManager, Spatial character) {
        this.inputManager = inputManager;
        this.character = character;
        this.characterControl = character.getControl(BetterCharacterControl.class);
        setUpKeys();
    }

    private void setUpKeys() {
        for (ActionMappings mapping : ActionMappings.values()) {
            addKeyMapping(mapping);
        }
        addListenerMappings(basicMovementListener, ActionMappings.STRAFE_LEFT, ActionMappings.STRAFE_RIGHT);
        addListenerMappings(basicMovementListener, ActionMappings.ROTATE_LEFT, ActionMappings.ROTATE_RIGHT);
        addListenerMappings(basicMovementListener, ActionMappings.WALK_FORWARDS, ActionMappings.WALK_BACKWARDS);
        addListenerMappings(basicMovementListener, ActionMappings.JUMP, ActionMappings.RUN, ActionMappings.DUCK);
    }

    private void addKeyMapping(ActionMappings keyMapping) {
        inputManager.addMapping(keyMapping.mappingName, new KeyTrigger(keyMapping.keyInput));
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
            if (mapping == null || !controlStrated) {//maybe introduce a "NOT_DEFINED" mapping to avoid nulls
                return;
            }
            switch (mapping) {
                case WALK_FORWARDS:
                    forward = isPressed;
                    break;
                case WALK_BACKWARDS:
                    backward = isPressed;
                    break;
                case ROTATE_LEFT:
                    leftRotate = isPressed;
                    break;
                case ROTATE_RIGHT:
                    rightRotate = isPressed;
                    break;
                case STRAFE_LEFT:
                    leftStrafe = isPressed;
                    break;
                case STRAFE_RIGHT:
                    rightStrafe = isPressed;
                    break;
                case RUN:
                    isRunning = isPressed;
                    break;
                case JUMP:
                    if (isPressed == true && characterControl.isOnGround()) {
                        characterControl.setDucked(false);
                        characterControl.jump();
                        character.getControl(BasicMovementAnimationControl.class).startJumpAnimation();
                    }
                    break;
                case DUCK:
                    isRunning = false;
                    characterControl.setDucked(isPressed);
                    if (isPressed == true) {
                        character.getControl(BasicMovementAnimationControl.class).startDuckAnimation();
                    }
                    break;
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

    public void removeKeyMappings() {//Break this method in to two parts, so that input can be temporally paused. (Write an unpause method too)
        for (ActionMappings keyMapping : ActionMappings.values()) {
            inputManager.deleteMapping(keyMapping.mappingName);
        }
        inputManager.removeListener(basicMovementListener);
    }

    @Override
    protected void controlUpdate(float tpf) {//refactor all of this
        controlStrated = true;
        Vector3f modelForwardDir = character.getWorldRotation().mult(Vector3f.UNIT_Z);
        Vector3f modelLeftDir = character.getWorldRotation().mult(Vector3f.UNIT_X);
        walkDirection.set(0, 0, 0);

        if (leftStrafe) {
            walkDirection.addLocal(modelLeftDir.mult(WALK_SPEED));
        } else if (rightStrafe) {
            walkDirection.addLocal(modelLeftDir.negate().multLocal(WALK_SPEED));
        }

        if (forward) {
            float speed = isRunning ? RUN_SPEED : WALK_SPEED;
            walkDirection.addLocal(modelForwardDir.mult(speed));
        } else if (backward) {
            walkDirection.addLocal(modelForwardDir.negate().multLocal(WALK_SPEED));
        }
        characterControl.setWalkDirection(walkDirection);

        if (leftRotate) {
            Quaternion rotateL = new Quaternion().fromAngleAxis(FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateL.multLocal(viewDirection);
        } else if (rightRotate) {
            Quaternion rotateR = new Quaternion().fromAngleAxis(-FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateR.multLocal(viewDirection);
        }
        characterControl.setViewDirection(viewDirection);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
