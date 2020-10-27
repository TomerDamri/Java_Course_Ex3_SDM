package sdm.servlets;

import com.google.gson.Gson;
import course.java.sdm.engine.controller.ISDMController;
import model.request.GetStoreItemsRequest;
import model.response.GetStoreItemsResponse;
import sdm.utils.ServletUtils;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "StoreItemsServlets", urlPatterns = {"/pages/storeItems"})
public class StoreItemsServlets extends HttpServlet {
    private static final String ZONE_NAME = "zoneName";
    private static final String STORE_ID = "storeId";

    //returns the items of a given store in zone
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        ServletContext servletContext = getServletContext();
        ISDMController controller = ServletUtils.getSDMController(servletContext);
        String zoneName = request.getParameter(ZONE_NAME);
        int storeId = Integer.parseInt(request.getParameter(STORE_ID));
        GetStoreItemsResponse getStoreItemsResponse = controller.getStoreItems(new GetStoreItemsRequest(zoneName, storeId));
        response.setStatus(200);
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(getStoreItemsResponse);
            out.println(json);
            out.flush();
        }
    }
}

