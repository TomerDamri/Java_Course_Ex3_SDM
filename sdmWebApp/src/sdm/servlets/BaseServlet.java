package sdm.servlets;

import com.google.gson.Gson;
import course.java.sdm.engine.controller.ISDMController;
import sdm.utils.ServletUtils;
import sdm.utils.SessionUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class BaseServlet extends HttpServlet {

    private Gson gson = new Gson();

    protected void processRequest (HttpServletRequest request, HttpServletResponse response, Consumer<UUID> func) {
        String userIdStr = SessionUtils.getUserId(request);

        if (userIdStr == null) {
            response.setStatus(404);
        }
        else {
            UUID userId = ServletUtils.tryParse(userIdStr, UUID::fromString, "user id", UUID.class);
            func.accept(userId);
        }
    }

    protected ISDMController getSDMController () {
        ServletContext servletContext = getServletContext();
        return ServletUtils.getSDMController(servletContext);
    }

    protected <T> void createJsonResponse (HttpServletResponse response, T writeToResponse) {
        try {
            response.setStatus(200);
            try (PrintWriter out = response.getWriter()) {
                String json = gson.toJson(writeToResponse);
                out.println(json);
                out.flush();
            }
        }
        catch (IOException e) {
            response.setStatus(500);
        }
    }
}
