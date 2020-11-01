package sdm.servlets;

import course.java.sdm.engine.controller.ISDMController;
import model.request.GetUserNotificationsRequest;
import model.response.GetUserNotificationsResponse;
import sdm.utils.ServletUtils;
import sdm.utils.SessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@WebServlet(name = "AlertsServlet", urlPatterns = { "/pages/alerts" })
public class AlertsServlet extends BaseServlet {
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) {
        String userIdStr = SessionUtils.getUserId(request);
        response.setContentType("application/json");

        if (userIdStr == null) {
            response.setStatus(404);
        }
        else {
            UUID userId = ServletUtils.tryParse(userIdStr, UUID::fromString, "user id", UUID.class);
            ISDMController controller = getSDMController();
            GetUserNotificationsResponse getUserNotificationsResponse = controller.getUserNotifications(new GetUserNotificationsRequest(userId));
            createJsonResponse(response, getUserNotificationsResponse);
        }
    }
}
