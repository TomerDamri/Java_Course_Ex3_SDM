package sdm.servlets;

import static sdm.constants.Constants.USER_ID;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Consumer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import course.java.sdm.engine.controller.ISDMController;
import sdm.utils.ServletUtils;

public abstract class BankServlet extends HttpServlet {

    protected void processRequest (HttpServletRequest request, HttpServletResponse response, Consumer<UUID> func) {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        if (session == null) {
            // TODO- ADD RESPONSE BODY?
            response.setStatus(404);
        }
        else {
            Object userIdAttribute = session.getAttribute(USER_ID);
            if (userIdAttribute == null) {
                // TODO- ADD RESPONSE BODY?
                response.setStatus(404);
            }
            else {
                UUID userId = UUID.fromString(userIdAttribute.toString());
                func.accept(userId);
            }
        }
    }

    protected <T> void createJsonResponse (HttpServletResponse response, T writeToResponse) throws IOException {
        response.setStatus(200);
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(writeToResponse);
            out.println(json);
            out.flush();
        }
    }

    protected ISDMController getSDMController () {
        ServletContext servletContext = getServletContext();
        return ServletUtils.getSDMController(servletContext);
    }
}
