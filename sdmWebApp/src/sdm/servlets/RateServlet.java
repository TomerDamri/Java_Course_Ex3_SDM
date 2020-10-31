package sdm.servlets;

import course.java.sdm.engine.controller.ISDMController;
import model.request.GetFeedbackForStoreOwnerRequest;
import model.request.RankOrderStoresRequest;
import model.request.StoreRank;
import model.response.GetFeedbackForStoreOwnerResponse;
import sdm.utils.ServletUtils;
import sdm.utils.SessionUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static sdm.constants.Constants.ZONE_NAME;

@WebServlet(name = "RateServlet", urlPatterns = { "/pages/rateStore" })
public class RateServlet extends BaseServlet {

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        try {
            Consumer<UUID> getUserBalanceFunc = userId -> processGetStoreOwnerFeedbackByZone(request, response, userId);
            processRequest(request, response, getUserBalanceFunc);
        }
        catch (Exception ex) {
            response.setStatus(400);
            response.getWriter().println(ex.getMessage());
        }
    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            processRateStore(request, response);
        }
        catch (Exception ex) {
            response.setStatus(400);
            response.getWriter().println(ex.getMessage());
        }
    }

    private void processRateStore (HttpServletRequest request, HttpServletResponse response) {
        RankOrderStoresRequest rateStoreRequest = createRankOrderStoresRequest(request);
        ISDMController controller = getSDMController();
        controller.rankOrderStores(rateStoreRequest);
        response.setStatus(200);
    }

    private void processGetStoreOwnerFeedbackByZone (HttpServletRequest request, HttpServletResponse response, UUID storeOwnerId) {
        response.setContentType("application/json");
        String zoneName = request.getParameter(ZONE_NAME);
        if (zoneName == null) {
            // TODO- ADD RESPONSE BODY?
            response.setStatus(404);
        }
        else {
            ISDMController controller = getSDMController();
            GetFeedbackForStoreOwnerResponse feedbackForStoreOwner = controller.getFeedbackForStoreOwner(new GetFeedbackForStoreOwnerRequest(zoneName,
                                                                                                                                             storeOwnerId));
            createJsonResponse(response, feedbackForStoreOwner);
        }
    }

    protected RankOrderStoresRequest createRankOrderStoresRequest (HttpServletRequest request) {
        UUID orderId = ServletUtils.tryParse(request.getParameter("orderId"), UUID::fromString, "order Id", UUID.class);
        UUID customerId = ServletUtils.tryParse(SessionUtils.getUserId(request), UUID::fromString, "customer Id", UUID.class);
        String zoneName = request.getParameter("zoneName");
        Integer ranksCount = ServletUtils.tryParse(request.getParameter("ranksCount"), Integer::parseInt, "ranks count", Integer.class);
        List<StoreRank> storeRanks = new ArrayList<>();
        for (int i = 0; i < ranksCount; i++) {
            Integer storeId = ServletUtils.tryParse(request.getParameter("storeRanks[" + i + "][storeId]"),
                                                    Integer::parseInt,
                                                    "store Id",
                                                    Integer.class);
            Double rank = ServletUtils.tryParse(request.getParameter("storeRanks[" + i + "][rank]"),
                                                Double::parseDouble,
                                                "rank",
                                                Double.class);
            String textualFeedback = request.getParameter("chosenDiscounts[" + i + "][textualFeedback]");

            storeRanks.add(new StoreRank(storeId, rank, textualFeedback));
        }

        return new RankOrderStoresRequest(zoneName, orderId, customerId, storeRanks);
    }
}
