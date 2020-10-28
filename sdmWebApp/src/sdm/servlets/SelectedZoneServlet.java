package sdm.servlets;

import com.google.gson.Gson;
import course.java.sdm.engine.controller.ISDMController;
import model.response.GetZoneResponse;
import sdm.utils.ServletUtils;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static sdm.constants.Constants.ZONE;

@WebServlet(name = "SelectedZoneServlet", urlPatterns = { "/pages/salesAreas/selectedZone" })

public class SelectedZoneServlet extends HttpServlet {


    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        ServletContext servletContext = getServletContext();
        ISDMController controller = ServletUtils.getSDMController(servletContext);
        GetZoneResponse getZoneResponse = controller.getZone(servletContext.getAttribute("selectedZone").toString());
        response.setStatus(200);
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(getZoneResponse);
            out.println(json);
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        String zone = request.getParameter(ZONE);
        if (zone == null || zone.isEmpty()) {
            response.setStatus(400);
            //TODO- ADD RESPONSE BODY?
        } else {
            zone = zone.trim();
            ServletUtils.setSelectedZone(getServletContext(), zone);
            response.setStatus(200);
            response.getOutputStream().println(zone);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(request, response);
    }
}
