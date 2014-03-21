package gui.effects;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectImpl;
import de.lessvoid.nifty.effects.EffectProperties;
import de.lessvoid.nifty.effects.Falloff;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.render.NiftyRenderEngine;

/**
 *
 * @author raccoon
 */
public class Resize implements EffectImpl {

    public static final String FINAL_HEIGHT = "finalHeight";
    public static final String FINAL_WIDTH = "finalWidth";
    private Element element;
    private EffectProperties properties;
    private int finalHeight;
    private int finalWidth;
    private int initialHeight;
    private int initialWidth;

    public void activate(Nifty nifty, Element element, EffectProperties parameter) {
        this.element = element;
        this.properties = parameter;
        setInitialSizes();
        loadResizeProperties();
    }

    private void setInitialSizes() {
        initialHeight = element.getHeight();
        initialWidth = element.getWidth();
    }

    private void loadResizeProperties() {
        //TODO read sizeValues
        finalHeight = loadFinalSizeProperty(FINAL_HEIGHT, initialHeight);
        finalWidth = loadFinalSizeProperty(FINAL_WIDTH, initialWidth);
    }

    public void execute(Element element, float effectTime, Falloff falloff, NiftyRenderEngine r) {
        final int currentWidthDifference = calculateSizeDifference(effectTime, initialWidth, finalWidth);
        final int currentHeightDifference = calculateSizeDifference(effectTime, initialHeight, finalHeight);
        resizeElement(initialWidth + currentWidthDifference, initialHeight + currentHeightDifference);
    }

    private int calculateSizeDifference(float effectTime, int initialSize, int finalSize) {
        int sizeDifference = finalSize - initialSize;
        return (int) (sizeDifference * effectTime);
    }

    private void resizeElement(final int width, final int height) {
        element.setWidth(width);
        element.setHeight(height);
    }

    public void deactivate() {
        resizeElement(finalWidth, finalHeight);
    }

    private Integer loadFinalSizeProperty(String propertyName, int defaultSize) {
        String defaultPropertyValue = String.valueOf(defaultSize);
        String propertyValue = properties.getProperty(propertyName, defaultPropertyValue);
        return Integer.valueOf(propertyValue);
    }
}
