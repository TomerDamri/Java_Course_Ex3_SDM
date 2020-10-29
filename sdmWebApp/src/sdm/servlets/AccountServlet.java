package sdm.servlets;

import static sdm.constants.Constants.AMOUNT;
import static sdm.constants.Constants.USER_ID;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;
import java.util.function.Consumer;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import course.java.sdm.engine.controller.ISDMController;
import model.request.DepositRequest;
import model.request.GetUserBalanceRequest;
import model.response.GetUserBalanceResponse;
import sdm.utils.ServletUtils;

@WebServlet(name = "AccountServlet", urlPatterns = { "/pages/userAccount" })
public class AccountServlet extends HttpServlet {

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) {
        Consumer<UUID> depositFun = userId -> processDepositRequest(request, response, userId);
        processRequest(request, response, depositFun);
    }

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) {
        Consumer<UUID> getUserBalanceFunc = userId -> {
            try {
                processGetUserBalance(request, response, userId);
            }
            catch (IOException e) {
                response.setStatus(500);
            }
        };
        processRequest(request, response, getUserBalanceFunc);
    }

    private void processRequest (HttpServletRequest request, HttpServletResponse response, Consumer<UUID> func) {
        response.setContentType("application/json");
        HttpSession session = request.getSession(false);
        if (session == null) {
            // TODO- ADD RESPONSE BODY?
            response.setStatus(404);
        }
        else {
            Object userIdAttribute = session.getAttribute(USER_ID);
            if (userIdAttribute == null) {
                // TODO- ADD RESPONSE BODY?
                response.setStatus(404);
            }
            else {
                UUID userId = UUID.fromString(userIdAttribute.toString());
                func.accept(userId);
            }
        }
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

    private void processGetUserBalance (HttpServletRequest request, HttpServletResponse response, UUID userId) throws IOException {
        ISDMController controller = getSDMController();
        GetUserBalanceResponse getUserBalanceResponse = controller.getUserBalance(new GetUserBalanceRequest(userId));
        response.setStatus(200);
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(getUserBalanceResponse);
            out.println(json);
            out.flush();
        }
    }

    private ISDMController getSDMController () {
        ServletContext servletContext = getServletContext();
        return ServletUtils.getSDMController(servletContext);
    }
}
