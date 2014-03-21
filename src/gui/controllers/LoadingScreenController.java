package gui.controllers;

import de.lessvoid.nifty.screen.ScreenController;

/**
 * Implement to make your own custom loading scene controllers
 *
 * @author raccoon
 */
public interface LoadingScreenController extends ScreenController {

    public static final String START_SCREEN = "start";

    void setCurrentLoadingMessage(String message);

    void updateProgressIndicator(int currentProgressPercentage);

    void setBackgroundImage(String imagePath);

    boolean isAnimationDone();
}
