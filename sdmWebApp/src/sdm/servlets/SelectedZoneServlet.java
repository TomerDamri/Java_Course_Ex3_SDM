package sdm.servlets;

import course.java.sdm.engine.controller.ISDMController;
import model.response.GetZoneResponse;
import sdm.utils.ServletUtils;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static sdm.constants.Constants.ZONE;

@WebServlet(name = "SelectedZoneServlet", urlPatterns = { "/pages/sellingZones/selectedZone" })

public class SelectedZoneServlet extends BaseServlet {

    protected void processRequest (HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/json");
            ServletContext servletContext = getServletContext();
            ISDMController controller = ServletUtils.getSDMController(servletContext);
            GetZoneResponse getZoneResponse = controller.getZone(servletContext.getAttribute("selectedZone").toString());
            createJsonResponse(response, getZoneResponse);
        }
        catch (Exception ex) {
            response.setStatus(400);
            response.getWriter().println(ex.getMessage());
        }
    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("text/plain;charset=UTF-8");
            String zone = request.getParameter(ZONE);
            if (zone == null || zone.isEmpty()) {
                response.setStatus(400);
            }
            else {
                zone = zone.trim();
                ServletUtils.setSelectedZone(getServletContext(), zone);
                response.setStatus(200);
                response.getOutputStream().println(zone);
            }
        }
        catch (Exception ex) {
            response.setStatus(400);
            response.getWriter().println(ex.getMessage());
        }
    }

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(request, response);
    }
}
