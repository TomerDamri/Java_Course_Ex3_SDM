package sdm.servlets;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import course.java.sdm.engine.controller.ISDMController;
import model.request.GetCustomerOrdersRequest;
import model.response.GetCustomerOrdersResponse;

@WebServlet(name = "OrdersServlet", urlPatterns = { "/pages/userOrders" })
public class OrdersServlet extends BaseServlet {

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Consumer<UUID> getUserBalanceFunc = userId -> processGetUserOrders(response, userId);
            processRequest(request, response, getUserBalanceFunc);
        }
        catch (Exception ex) {
            response.setStatus(400);
            response.getWriter().println(ex.getMessage());
        }
    }

    private void processGetUserOrders (HttpServletResponse response, UUID userId) {
        response.setContentType("application/json");
        ISDMController controller = getSDMController();
        GetCustomerOrdersResponse customerOrders = controller.getCustomerOrders(new GetCustomerOrdersRequest(userId));
        createJsonResponse(response, customerOrders);
    }
}
