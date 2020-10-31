package sdm.utils;

import java.util.function.Function;

import javax.servlet.ServletContext;

import course.java.sdm.engine.controller.impl.SDMControllerImpl;

public class ServletUtils {

    private static final String SDM_CONTROLLER_ATTRIBUTE_NAME = "sdmController";
    private static final String SELECTED_ZONE_ATTRIBUTE_NAME = "selectedZone";

    private static final Object selectedZoneLock = new Object();
    private static final Object sdmControllerLock = new Object();

    public static SDMControllerImpl getSDMController (ServletContext servletContext) {
        synchronized (sdmControllerLock) {
            if (servletContext.getAttribute(SDM_CONTROLLER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(SDM_CONTROLLER_ATTRIBUTE_NAME, new SDMControllerImpl());
            }
        }
        return (SDMControllerImpl) servletContext.getAttribute(SDM_CONTROLLER_ATTRIBUTE_NAME);
    }

    public static void setSelectedZone (ServletContext servletContext, String zone) {
        synchronized (selectedZoneLock) {
            servletContext.setAttribute(SELECTED_ZONE_ATTRIBUTE_NAME, zone);
        }
    }

    public static <T> T tryParse (String strToParse, Function<String, T> parseFunc, String propertyName, Class<T> klass) {
        try {
            return parseFunc.apply(strToParse);
        }
        catch (Exception ex) {
            throw new RuntimeException(String.format("The value: '%s' is invalid for the property '%s'.\nYou have to enter a valid '%s'.",
                                                     strToParse,
                                                     propertyName,
                                                     klass.getSimpleName()));
        }
    }
}
