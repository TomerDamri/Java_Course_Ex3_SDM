package sdm.servlets;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import course.java.sdm.engine.controller.ISDMController;
import model.response.GetZonesResponse;
import sdm.utils.ServletUtils;

@WebServlet(name = "ZonesListServlet", urlPatterns = { "/pages/sellingZones/zonesList" })
public class ZonesListServlet extends BaseServlet {

    protected void processRequest (HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/json");
            ServletContext servletContext = getServletContext();
            ISDMController controller = ServletUtils.getSDMController(servletContext);
            GetZonesResponse getZonesResponse = controller.getZones();
            createJsonResponse(response, getZonesResponse);
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
