package sdm.servlets;

import com.google.gson.Gson;
import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import model.request.PlaceDynamicOrderRequest;
import model.request.PlaceOrderRequest;
import model.response.PlaceDynamicOrderResponse;
import model.response.PlaceOrderResponse;
import sdm.utils.ServletUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet(name = "PlaceOrderServlet", urlPatterns = {"/pages/placeOrder"})
public class PlaceOrderServlet extends HttpServlet {
    @Override

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        SDMControllerImpl sdmController = ServletUtils.getSDMController(getServletContext());
        //parse request
        UUID customerId = UUID.fromString(request.getParameter("customerId"));
        String zoneName = request.getParameter("zoneName");
        int xCoordinate = Integer.parseInt(request.getParameter("xCoordinate"));
        int yCoordinate = Integer.parseInt(request.getParameter("yCoordinate"));
        LocalDate date = LocalDate.parse(request.getParameter("orderDate"));
        int itemsCount = Integer.parseInt(request.getParameter("itemsCount"));
        Map<Integer, Double> orderItemToAmount = new HashMap<>();
        for (int i = 0; i < itemsCount; i++) {
            int itemId = Integer.parseInt(request.getParameter("orderItemToAmount[" + i + "][itemId]"));
            double itemAmount = Double.parseDouble(request.getParameter("orderItemToAmount[" + i + "][amount]"));
            orderItemToAmount.put(itemId, itemAmount);
        }
        if (request.getParameter("storeId") != null) {
            int storeId = Integer.parseInt(request.getParameter("storeId"));
            PlaceOrderRequest placeOrderRequest = new PlaceOrderRequest(zoneName, storeId, customerId, date, xCoordinate, yCoordinate, orderItemToAmount);
            PlaceOrderResponse placeOrderResponse = sdmController.placeStaticOrder(placeOrderRequest);
            response.setStatus(200);
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String json = gson.toJson(placeOrderResponse);
                out.println(json);
                out.flush();
            }

        } else {
            PlaceDynamicOrderRequest placeDynamicOrderRequest = new PlaceDynamicOrderRequest(zoneName, customerId, date, xCoordinate, yCoordinate, orderItemToAmount);
            PlaceDynamicOrderResponse placeDynamicOrderResponse = sdmController.placeDynamicOrder(placeDynamicOrderRequest);
            response.setStatus(200);
            try (PrintWriter out = response.getWriter()) {
                Gson gson = new Gson();
                String json = gson.toJson(placeDynamicOrderResponse);
                out.println(json);
                out.flush();
            }
        }
    }
}

