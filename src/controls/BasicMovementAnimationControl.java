package controls;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import utils.MathUtils;

/**
 *
 * @author raccoon
 */
public class BasicMovementAnimationControl extends AbstractControl {

    private static final String WALKING_ANIMATION = "Walk";
    private static final String IDLE_ANIMATION = "Idle";
    private static final String RUNNING_ANIMATION = "Run";
    private static final String JUMP_START_ANIMATION = "JumpStart";
    private static final String JUMPING_ANIMATION = "Jumping";
    private static final String JUMP_END_ANIMATION = "JumpEnd";//  TODO
    private static final String DUCK_START_ANIMATION = "JumpStart";//TODO duck animation
    private static final String DUCK_WALK_ANIMATION = "Jumping";//TODO duck
    private static final String DUCK_IDLE_ANIMATION = "Run";//TODO duck
    private static final String DUCK_END_ANIMATION = "JumpEnd";//TODO duck
    private static final float BLEND_TIME = 0.2f;
    private float walkingSpeed;
    private boolean isDucking, isAnimating;
    private BetterCharacterControl characterControl;
    private AnimControl animControl;
    private AnimChannel movementChannel;

    public BasicMovementAnimationControl(Spatial character) {
        walkingSpeed = character.getUserData("walkingSpeed");//refa to constant
        characterControl = character.getControl(BetterCharacterControl.class);
        animControl = character.getControl(AnimControl.class);
        movementChannel = animControl.createChannel();
        animControl.addListener(isAnimatingListener);
    }

    @Override
    protected void controlUpdate(float tpf) {
        Vector3f walkingDirectionSpeed = characterControl.getWalkDirection();

        if (isAnimating) {
            return;
        }

        if (!characterControl.isOnGround()) {
            startAnimationIfPossible(JUMPING_ANIMATION);
            return;
        }

        if (isDucking) {
            resolveDuckAnimation(walkingDirectionSpeed);
            return;
        }

        if (canStartIdleAnimation(walkingDirectionSpeed)) {
            startAnimationIfPossible(IDLE_ANIMATION);
            return;
        }

        walkOrRun(walkingDirectionSpeed);
    }

    private void startAnimationIfPossible(String animationName) {
        startAnimationIfPossible(animationName, LoopMode.Loop);
    }

    private void startAnimationIfPossible(String animationName, LoopMode loopMode) {
        if (!animationName.equals(movementChannel.getAnimationName())) {//refa?
            movementChannel.setAnim(animationName, BLEND_TIME);
            movementChannel.setLoopMode(loopMode);
        }
    }

    private void resolveDuckAnimation(Vector3f walkingDirectionSpeed) {
        if (canStartIdleAnimation(walkingDirectionSpeed)) {
            startAnimationIfPossible(DUCK_IDLE_ANIMATION);
        } else if (characterHasUnDucked()) {
            isDucking = false;
            isAnimating = true;
            startAnimationIfPossible(DUCK_END_ANIMATION, LoopMode.DontLoop);
        } else {
            startAnimationIfPossible(DUCK_WALK_ANIMATION);
        }
    }

    private boolean canStartIdleAnimation(Vector3f walkingDirectionSpeed) {
        return Vector3f.ZERO.equals(walkingDirectionSpeed);
    }

    private boolean characterHasUnDucked() {
        return isDucking == true && characterControl.isDucked() == false;
    }

    private void walkOrRun(Vector3f walkingDirectionSpeed) {
        if (isRunning(walkingDirectionSpeed)) {
            startAnimationIfPossible(RUNNING_ANIMATION);
        } else {
            startAnimationIfPossible(WALKING_ANIMATION);
        }
    }

    private boolean isRunning(Vector3f speed) {
        float topSpeed = MathUtils.getBiggestModule(speed.x, speed.z);
        return topSpeed > (walkingSpeed * 1.3);//refactor filter into user data for running
    }

    public void startJumpAnimation() {
        isAnimating = true;
        startAnimationIfPossible(JUMP_START_ANIMATION, LoopMode.DontLoop);
    }

    public void startDuckAnimation() {
        isDucking = true;
        isAnimating = true;
        startAnimationIfPossible(DUCK_START_ANIMATION, LoopMode.DontLoop);
    }
    AnimEventListener isAnimatingListener = new AnimEventListener() {

        public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
            if (DUCK_START_ANIMATION.equals(animName)
                    || DUCK_END_ANIMATION.equals(animName)
                    || JUMP_START_ANIMATION.equals(animName)) {
                //Refactor
                isAnimating = false;
            }
        }

        public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
        }

    };

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
}
