package sdm.utils;


import course.java.sdm.engine.chat.ChatManager;
import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import course.java.sdm.engine.users.UserManager;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import static sdm.constants.Constants.INT_PARAMETER_ERROR;

public class ServletUtils {

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String CHAT_MANAGER_ATTRIBUTE_NAME = "chatManager";
    private static final String SDM_CONTROLLER_ATTRIBUTE_NAME = "sdmController";

    /*
    Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
    the actual fetch of them is remained un-synchronized for performance POV
     */
    private static final Object userManagerLock = new Object();
    private static final Object chatManagerLock = new Object();
    private static final Object sdmControllerLock = new Object();

    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static ChatManager getChatManager(ServletContext servletContext) {
        synchronized (chatManagerLock) {
            if (servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(CHAT_MANAGER_ATTRIBUTE_NAME, new ChatManager());
            }
        }
        return (ChatManager) servletContext.getAttribute(CHAT_MANAGER_ATTRIBUTE_NAME);
    }

    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return INT_PARAMETER_ERROR;
    }

    public static SDMControllerImpl getsdmController(ServletContext servletContext) {
        synchronized (sdmControllerLock) {
            if (servletContext.getAttribute(SDM_CONTROLLER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(SDM_CONTROLLER_ATTRIBUTE_NAME, new SDMControllerImpl());
            }
        }
        return (SDMControllerImpl) servletContext.getAttribute(SDM_CONTROLLER_ATTRIBUTE_NAME);
    }
}
