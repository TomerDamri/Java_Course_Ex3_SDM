package sdm.servlets;

import com.google.gson.Gson;
import course.java.sdm.engine.controller.ISDMController;
import model.response.GetZonesResponse;
import sdm.utils.ServletUtils;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "ZonesListServlet", urlPatterns = { "/pages/sellingZones/zonesList" })
public class ZonesListServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        ServletContext servletContext = getServletContext();
        ISDMController controller = ServletUtils.getSDMController(servletContext);
        GetZonesResponse getZonesResponse = controller.getZones();
        response.setStatus(200);
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(getZonesResponse);
            out.println(json);
            out.flush();
        }

    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        processRequest(request, response);
    }

}
