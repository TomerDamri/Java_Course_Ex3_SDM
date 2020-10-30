package sdm.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import course.java.sdm.engine.controller.ISDMController;
import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import model.ChosenDiscountDTO;
import model.request.AddDiscountsToOrderRequest;
import model.response.FinalSummaryForOrder;
import model.response.GetDiscountsResponse;
import sdm.utils.ServletUtils;

@WebServlet(name = "DiscountsServlet", urlPatterns = { "/pages/discounts" })
public class DiscountsServlet extends BaseServlet {

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {

            response.setContentType("application/json");
            SDMControllerImpl sdmController = ServletUtils.getSDMController(getServletContext());
            GetDiscountsResponse getDiscountsResponse = sdmController.getDiscounts(ServletUtils.tryParse(request.getParameter("orderId"),
                                                                                                         UUID::fromString,
                                                                                                         UUID.class));
            createJsonResponse(response, getDiscountsResponse);
        }
        catch (Exception ex) {
            response.setStatus(400);
            response.getWriter().println(ex.getMessage());
        }
    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/json");
            AddDiscountsToOrderRequest addDiscountsToOrderRequest = createAddDiscountsToOrderRequest(request);
            ISDMController controller = getSDMController();
            FinalSummaryForOrder finalSummaryForOrder = controller.addDiscountsToOrder(addDiscountsToOrderRequest);
            createJsonResponse(response, finalSummaryForOrder);
        }
        catch (Exception ex) {
            response.setStatus(400);
            response.getWriter().println(ex.getMessage());
        }
    }

    private AddDiscountsToOrderRequest createAddDiscountsToOrderRequest (HttpServletRequest request) {
        UUID orderId = ServletUtils.tryParse(request.getParameter("orderId"), UUID::fromString, UUID.class);
        ;
        Integer discountsCount = ServletUtils.tryParse(request.getParameter("discountsCount"), Integer::parseInt, Integer.class);
        List<ChosenDiscountDTO> chosenDiscounts = new ArrayList<>();
        for (int i = 0; i < discountsCount; i++) {
            Integer storeId = ServletUtils.tryParse(request.getParameter("chosenDiscounts[" + i + "][storeId]"),
                                                    Integer::parseInt,
                                                    Integer.class);
            Integer itemId = ServletUtils.tryParse(request.getParameter("chosenDiscounts[" + i + "][itemId]"),
                                                   Integer::parseInt,
                                                   Integer.class);
            String discountName = request.getParameter("chosenDiscounts[" + i + "][discountName]");
            Integer numOfRealizations = ServletUtils.tryParse(request.getParameter("chosenDiscounts[" + i + "][numOfRealizations]"),
                                                              Integer::parseInt,
                                                              Integer.class);
            String orOfferIdStr = request.getParameter("chosenDiscounts[" + i + "][orOfferId]");
            Integer orOfferId = orOfferIdStr != null ? ServletUtils.tryParse(orOfferIdStr, Integer::parseInt, Integer.class) : null;

            chosenDiscounts.add(new ChosenDiscountDTO(storeId, itemId, discountName, numOfRealizations, orOfferId));
        }

        return new AddDiscountsToOrderRequest(orderId, chosenDiscounts);
    }
}
