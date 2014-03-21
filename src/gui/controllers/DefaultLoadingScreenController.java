package gui.controllers;

import de.lessvoid.nifty.EndNotify;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.Effect;
import de.lessvoid.nifty.effects.EffectEventId;
import de.lessvoid.nifty.effects.EffectProperties;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import gui.effects.Resize;
import java.util.List;

/**
 * The default loading screen controller, features a progress bar, a loading
 * message and a custom background image.
 *
 * @author raccoon
 */
public class DefaultLoadingScreenController implements LoadingScreenController {

    private static final String PROGRESS_BAR = "progresBar";
    private static final String LOADING_MESSAGE = "loadingMessage";
    private static final String BACKGROUND_IMAGE = "backgroundImage";
    private static final String EFFECT_CUSTOM_KEY = "progressUpdate";
    private Nifty nifty;
    private Screen screen;
    private Element progressBar;
    private boolean animationDone = false;

    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }

    public void setBackgroundImage(String imagePath) {
        NiftyImage backgroundImage = nifty.getRenderEngine().createImage(screen, imagePath, false);
        screen.findElementByName(BACKGROUND_IMAGE).getRenderer(ImageRenderer.class).setImage(backgroundImage);
    }

    public void setCurrentLoadingMessage(String message) {
        screen.findElementByName(LOADING_MESSAGE).getRenderer(TextRenderer.class).setText(message);
    }

    public void updateProgressIndicator(int currentProgressPercentage) {
        progressBar = screen.findElementByName(PROGRESS_BAR);
        int screenWidth = screen.getRootElement().getWidth();
        int targetWidth = (currentProgressPercentage * screenWidth) / 100;
        modifyEffectProperties(targetWidth);
        EndNotify animationFinishedNotify = null;
        if (currentProgressPercentage == 100) {
            animationFinishedNotify = makeAnimationEndNotify();
        }
        progressBar.startEffect(EffectEventId.onCustom, animationFinishedNotify, EFFECT_CUSTOM_KEY);
    }

    private void modifyEffectProperties(Integer finalWidth) {
        Effect effect = findProgressBarEffect();
        EffectProperties effectProperties = effect.getParameters();
        effectProperties.put(Resize.FINAL_WIDTH, finalWidth.toString());
    }

    private Effect findProgressBarEffect() {
        List<Effect> appliedResizeEffects = progressBar.getEffects(EffectEventId.onCustom, Resize.class);
        for (Effect effect : appliedResizeEffects) {
            if (EFFECT_CUSTOM_KEY.equals(effect.getCustomKey())) {
                return effect;
            }
        }
        return null;
    }

    private EndNotify makeAnimationEndNotify() {
        return new EndNotify() {

            public void perform() {
                animationDone = true;
            }

        };
    }

    public boolean isAnimationDone() {
        return animationDone;
    }
}
