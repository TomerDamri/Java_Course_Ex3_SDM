package sdm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import course.java.sdm.engine.controller.ISDMController;
import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import model.ChosenDiscountDTO;
import model.request.AddDiscountsToOrderRequest;
import model.response.FinalSummaryForOrder;
import model.response.GetDiscountsResponse;
import sdm.utils.ServletUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@WebServlet(name = "DiscountsServlet", urlPatterns = { "/pages/discounts" })
public class DiscountsServlet extends BaseServlet {

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        SDMControllerImpl sdmController = ServletUtils.getSDMController(getServletContext());
        GetDiscountsResponse getDiscountsResponse = sdmController.getDiscounts(UUID.fromString(request.getParameter("orderId")));
        response.setStatus(200);
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            String json = gson.toJson(getDiscountsResponse);
            out.println(json);
            out.flush();
        }
    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        AddDiscountsToOrderRequest addDiscountsToOrderRequest = createAddDiscountsToOrderRequest(request);
        ISDMController controller = getSDMController();
        FinalSummaryForOrder finalSummaryForOrder = controller.addDiscountsToOrder(addDiscountsToOrderRequest);
        createJsonResponse(response, finalSummaryForOrder);
    }

    private AddDiscountsToOrderRequest createAddDiscountsToOrderRequest (HttpServletRequest request) {
        UUID orderId = UUID.fromString(request.getParameter("orderId"));
        int discountsCount = Integer.parseInt(request.getParameter("discountsCount"));
        List<ChosenDiscountDTO> chosenDiscounts = new ArrayList<>();
        for (int i = 0; i < discountsCount; i++) {
            int storeId = Integer.parseInt(request.getParameter("chosenDiscounts[" + i + "][storeId]"));
            int itemId = Integer.parseInt(request.getParameter("chosenDiscounts[" + i + "][itemId]"));
            String discountName = request.getParameter("chosenDiscounts[" + i + "][discountName]");
            int numOfRealizations = Integer.parseInt(request.getParameter("chosenDiscounts[" + i + "][numOfRealizations]"));
            String parameter = request.getParameter("chosenDiscounts[" + i + "][orOfferId]");
            Integer orOfferId = parameter != null ? Integer.parseInt(parameter) : null;

            chosenDiscounts.add(new ChosenDiscountDTO(storeId, itemId, discountName, numOfRealizations, orOfferId));
        }

        return new AddDiscountsToOrderRequest(orderId, chosenDiscounts);
    }
}
