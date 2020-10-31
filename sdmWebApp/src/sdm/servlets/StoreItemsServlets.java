package sdm.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import course.java.sdm.engine.controller.ISDMController;
import model.request.GetStoreItemsRequest;
import model.response.GetStoreItemsResponse;
import sdm.utils.ServletUtils;

@WebServlet(name = "StoreItemsServlets", urlPatterns = { "/pages/storeItems" })
public class StoreItemsServlets extends BaseServlet {
    private static final String ZONE_NAME = "zoneName";
    private static final String STORE_ID = "storeId";

    // returns the items of a given store in zone
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/json");
            ISDMController controller = getSDMController();
            String zoneName = request.getParameter(ZONE_NAME);
            Integer storeId = ServletUtils.tryParse(request.getParameter(STORE_ID), Integer::parseInt, "store Id", Integer.class);
            GetStoreItemsResponse getStoreItemsResponse = controller.getStoreItems(new GetStoreItemsRequest(zoneName, storeId));
            createJsonResponse(response, getStoreItemsResponse);
        }
        catch (Exception ex) {
            response.setStatus(400);
            response.getWriter().println(ex.getMessage());
        }
    }
}
