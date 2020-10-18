package sdm.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import sdm.constants.Constants;

public class SessionUtils {

    public static String getUsername (HttpServletRequest request) {
        return getChosenSessionAttribute(request, Constants.USERNAME);
    }

    public static String getUserId (HttpServletRequest request) {
        return getChosenSessionAttribute(request, Constants.USER_ID);
    }

    public static String getUserType (HttpServletRequest request) {
        return getChosenSessionAttribute(request, Constants.USER_TYPE);
    }

    private static String getChosenSessionAttribute (HttpServletRequest request, String chosenAttribute) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(chosenAttribute) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static void clearSession (HttpServletRequest request) {
        request.getSession().invalidate();
    }
}