package sdm.servlets;

import static sdm.constants.Constants.AMOUNT;

import java.io.IOException;
import java.util.UUID;
import java.util.function.Consumer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import course.java.sdm.engine.controller.ISDMController;
import model.request.DepositRequest;
import model.request.GetUserBalanceRequest;
import model.response.GetUserBalanceResponse;
import sdm.utils.ServletUtils;

@WebServlet(name = "AccountServlet", urlPatterns = { "/pages/userAccount" })
public class AccountServlet extends BaseServlet {

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Consumer<UUID> depositFun = userId -> processDepositRequest(request, response, userId);
            processRequest(request, response, depositFun);
        }
        catch (Exception ex) {
            response.setStatus(400);
            response.getWriter().println(ex.getMessage());
        }
    }

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Consumer<UUID> getUserBalanceFunc = userId -> processGetUserBalance(response, userId);
            processRequest(request, response, getUserBalanceFunc);
        }
        catch (Exception ex) {
            response.setStatus(400);
            response.getWriter().println(ex.getMessage());
        }
    }

    private void processDepositRequest (HttpServletRequest request, HttpServletResponse response, UUID userId) {
        String amountToDepositStr = request.getParameter(AMOUNT);
        if (amountToDepositStr == null) {
            // TODO- ADD RESPONSE BODY?
            response.setStatus(400);
        }
        else {
            Double amountToDeposit = ServletUtils.tryParse(amountToDepositStr, Double::parseDouble, "amount to deposit", Double.class);
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
