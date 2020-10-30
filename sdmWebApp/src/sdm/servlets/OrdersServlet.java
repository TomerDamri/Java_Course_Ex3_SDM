package sdm.servlets;

import course.java.sdm.engine.controller.ISDMController;
import model.request.GetCustomerOrdersRequest;
import model.response.GetCustomerOrdersResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.function.Consumer;

@WebServlet(name = "OrdersServlet", urlPatterns = { "/pages/userOrders" })
public class OrdersServlet extends BaseServlet {

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) {
        Consumer<UUID> getUserBalanceFunc = userId -> processGetUserOrders(response, userId);
        processRequest(request, response, getUserBalanceFunc);
    }

    private void processGetUserOrders (HttpServletResponse response, UUID userId) {
        response.setContentType("application/json");
        ISDMController controller = getSDMController();
        GetCustomerOrdersResponse customerOrders = controller.getCustomerOrders(new GetCustomerOrdersRequest(userId));
        createJsonResponse(response, customerOrders);
    }
}
