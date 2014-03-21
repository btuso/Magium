package gui.controllers;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 *
 * @author raccoon
 */
public abstract class AbstractScreenController implements ScreenController {

    private static final String START = "start";
    private static final String END = "end";
    protected Nifty nifty;

    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }

    public void quit() {
        nifty.gotoScreen(END);
    }
;
}
