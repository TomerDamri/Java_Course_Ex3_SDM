package sdm.servlets;

import java.util.UUID;
import java.util.function.Consumer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import course.java.sdm.engine.controller.ISDMController;
import model.request.GetUserTransactionsRequest;
import model.response.GetUserTransactionsResponse;

@WebServlet(name = "TransactionsServlet", urlPatterns = { "/pages/userTransactions" })
public class TransactionsServlet extends BaseServlet {
    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) {
        Consumer<UUID> getUserBalanceFunc = userId -> processGetUserTransaction(response, userId);
        processRequest(request, response, getUserBalanceFunc);
    }

    private void processGetUserTransaction (HttpServletResponse response, UUID userId) {
        ISDMController controller = getSDMController();
        GetUserTransactionsResponse userTransactions = controller.getUserTransactions(new GetUserTransactionsRequest(userId));
        createJsonResponse(response, userTransactions);
    }
}
