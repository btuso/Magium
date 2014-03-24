package utils;

/**
 *
 * @author raccoon
 */
public class MathUtils {

    /**
     * @return Returns the number with the biggest module. If module is the
     *         same, number is returned.
     */
    public static float getNumberWithBiggestModule(float number, float compared) {
        if (moduleOf(number) > moduleOf(compared)) {
            return number;
        } else if (moduleOf(number) < moduleOf(compared)) {
            return compared;
        }
        return number;
    }

    /**
     * @return Returns the biggest module
     */
    public static float getBiggestModule(float number, float compared) {
        float numberModule = moduleOf(number);
        float comparedModule = moduleOf(compared);

        if (numberModule >= comparedModule) {
            return numberModule;
        } else {
            return comparedModule;
        }
    }

    /**
     * @return Returns the module for x. IE: 1 = 1, 0 = 0, -1 = 1
     */
    public static float moduleOf(float x) {
        return x >= 0 ? x : ((-1) * x);
    }

    /**
     * @return Returns the module of the difference between the module of two
     *         numbers. IE: (1, 0) = 1, (0, -1) = 1, (1, -1) = 0
     */
    public static float moduledDifferenceBetweenModules(float x, float y) {
        return moduleOf(moduleOf(x) - moduleOf(y));
    }
}
