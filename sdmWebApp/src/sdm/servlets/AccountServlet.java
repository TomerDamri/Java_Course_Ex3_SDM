package sdm.servlets;

import course.java.sdm.engine.controller.ISDMController;
import model.request.DepositRequest;
import model.request.GetUserBalanceRequest;
import model.response.GetUserBalanceResponse;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
import java.util.function.Consumer;

import static sdm.constants.Constants.AMOUNT;

@WebServlet(name = "AccountServlet", urlPatterns = { "/pages/userAccount" })
public class AccountServlet extends BaseServlet {

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) {
        Consumer<UUID> depositFun = userId -> processDepositRequest(request, response, userId);
        processRequest(request, response, depositFun);
    }

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) {
        Consumer<UUID> getUserBalanceFunc = userId -> processGetUserBalance(response, userId);
        processRequest(request, response, getUserBalanceFunc);
    }

    private void processDepositRequest (HttpServletRequest request, HttpServletResponse response, UUID userId) {
        String amountToDepositStr = request.getParameter(AMOUNT);
        if (amountToDepositStr == null) {
            // TODO- ADD RESPONSE BODY?
            response.setStatus(400);
        }
        else {
            double amountToDeposit = Double.parseDouble(amountToDepositStr);
            ISDMController controller = getSDMController();
            controller.deposit(new DepositRequest(userId, amountToDeposit));
            response.setStatus(200);
        }
    }

    private void processGetUserBalance (HttpServletResponse response, UUID userId) {
        response.setContentType("application/json");
        ISDMController controller = getSDMController();
        GetUserBalanceResponse getUserBalanceResponse = controller.getUserBalance(new GetUserBalanceRequest(userId));
        createJsonResponse(response, getUserBalanceResponse);
    }
}
