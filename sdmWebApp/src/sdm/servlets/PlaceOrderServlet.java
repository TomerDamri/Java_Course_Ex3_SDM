package sdm.servlets;

import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import model.request.PlaceDynamicOrderRequest;
import model.request.PlaceOrderRequest;
import model.response.PlaceDynamicOrderResponse;
import model.response.PlaceOrderResponse;
import sdm.utils.ServletUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet(name = "PlaceOrderServlet", urlPatterns = { "/pages/placeOrder" })
public class PlaceOrderServlet extends BaseServlet {
    // Used as post to complete the order and confirm its creation
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) {
        // response.setContentType("application/json");
        SDMControllerImpl sdmController = ServletUtils.getSDMController(getServletContext());
        UUID orderId = ServletUtils.tryParse(request.getParameter("orderId"), UUID::fromString, UUID.class);
        boolean confirmOrder = ServletUtils.tryParse(request.getParameter("confirmOrder"), Boolean::parseBoolean, Boolean.class);
        // Boolean.getBoolean(request.getParameter("confirmOrder"));
        sdmController.completeTheOrder(orderId, confirmOrder);
        response.setStatus(200);
    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/json");
            SDMControllerImpl sdmController = ServletUtils.getSDMController(getServletContext());
            // parse request
            UUID customerId = ServletUtils.tryParse(request.getParameter("customerId"), UUID::fromString, UUID.class);
            String zoneName = request.getParameter("zoneName");
            Integer xCoordinate = ServletUtils.tryParse(request.getParameter("xCoordinate"), Integer::parseInt, Integer.class);
            Integer yCoordinate = ServletUtils.tryParse(request.getParameter("yCoordinate"), Integer::parseInt, Integer.class);
            LocalDate date = ServletUtils.tryParse(request.getParameter("orderDate"), LocalDate::parse, LocalDate.class);
            Integer itemsCount = ServletUtils.tryParse(request.getParameter("itemsCount"), Integer::parseInt, Integer.class);
            Map<Integer, Double> orderItemToAmount = new HashMap<>();
            for (int i = 0; i < itemsCount; i++) {
                Integer itemId = ServletUtils.tryParse(request.getParameter("orderItemToAmount[" + i + "][itemId]"),
                                                       Integer::parseInt,
                                                       Integer.class);
                Double itemAmount = ServletUtils.tryParse(request.getParameter("orderItemToAmount[" + i + "][amount]"),
                                                          Double::parseDouble,
                                                          Double.class);
                orderItemToAmount.put(itemId, itemAmount);
            }
            if (request.getParameter("storeId") != null) {
                PlaceOrderRequest placeOrderRequest = createPlaceStaticOrderRequest(request,
                                                                                    customerId,
                                                                                    zoneName,
                                                                                    xCoordinate,
                                                                                    yCoordinate,
                                                                                    date,
                                                                                    orderItemToAmount);
                PlaceOrderResponse placeOrderResponse = sdmController.placeStaticOrder(placeOrderRequest);
                createJsonResponse(response, placeOrderResponse);

            }
            else {
                PlaceDynamicOrderRequest placeDynamicOrderRequest = createPlaceDynamicOrderRequest(customerId,
                                                                                                   zoneName,
                                                                                                   xCoordinate,
                                                                                                   yCoordinate,
                                                                                                   date,
                                                                                                   orderItemToAmount);
                PlaceDynamicOrderResponse placeDynamicOrderResponse = sdmController.placeDynamicOrder(placeDynamicOrderRequest);
                createJsonResponse(response, placeDynamicOrderResponse);
            }
        }
        catch (Exception ex) {
            response.setStatus(400);
            response.getWriter().println(ex.getMessage());
        }
    }

    private PlaceDynamicOrderRequest createPlaceDynamicOrderRequest (UUID customerId,
                                                                     String zoneName,
                                                                     Integer xCoordinate,
                                                                     Integer yCoordinate,
                                                                     LocalDate date,
                                                                     Map<Integer, Double> orderItemToAmount) {
        PlaceDynamicOrderRequest placeDynamicOrderRequest = new PlaceDynamicOrderRequest(zoneName,
                                                                                         customerId,
                                                                                         date,
                                                                                         xCoordinate,
                                                                                         yCoordinate,
                                                                                         orderItemToAmount);
        return placeDynamicOrderRequest;
    }

    private PlaceOrderRequest createPlaceStaticOrderRequest (HttpServletRequest request,
                                                             UUID customerId,
                                                             String zoneName,
                                                             Integer xCoordinate,
                                                             Integer yCoordinate,
                                                             LocalDate date,
                                                             Map<Integer, Double> orderItemToAmount) {
        Integer storeId = ServletUtils.tryParse(request.getParameter("storeId"), Integer::parseInt, Integer.class);
        PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest(zoneName,
                                                                    storeId,
                                                                    customerId,
                                                                    date,
                                                                    xCoordinate,
                                                                    yCoordinate,
                                                                    orderItemToAmount);
        return placeOrderRequest;
    }
}
