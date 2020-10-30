package sdm.servlets;

import static sdm.constants.Constants.ZONE_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import course.java.sdm.engine.controller.ISDMController;
import model.request.GetFeedbackForStoreOwnerRequest;
import model.request.RankOrderStoresRequest;
import model.request.StoreRank;
import model.response.GetFeedbackForStoreOwnerResponse;

@WebServlet(name = "RateServlet", urlPatterns = { "/pages/rateStore" })
public class RateServlet extends BaseServlet {

    @Override
    protected void doGet (HttpServletRequest request, HttpServletResponse response) {
        Consumer<UUID> getUserBalanceFunc = userId -> processGetStoreOwnerFeedbackByZone(request, response, userId);
        processRequest(request, response, getUserBalanceFunc);
    }

    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) {
        processRateStore(request, response);
    }

    private void processRateStore (HttpServletRequest request, HttpServletResponse response) {
        RankOrderStoresRequest rateStoreRequest = createRankOrderStoresRequest(request);
        ISDMController controller = getSDMController();
        controller.rankOrderStores(rateStoreRequest);
        response.setStatus(200);
    }

    private void processGetStoreOwnerFeedbackByZone (HttpServletRequest request, HttpServletResponse response, UUID storeOwnerId) {
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
        UUID orderId = UUID.fromString(request.getParameter("orderId"));
        UUID customerId = UUID.fromString(request.getParameter("customerId"));
        String zoneName = request.getParameter("zoneName");
        int ranksCount = Integer.parseInt(request.getParameter("ranksCount"));
        List<StoreRank> storeRanks = new ArrayList<>();
        for (int i = 0; i < ranksCount; i++) {
            int storeId = Integer.parseInt(request.getParameter("storeRanks[" + i + "][storeId]"));
            double rank = Double.parseDouble(request.getParameter("storeRanks[" + i + "][rank]"));
            String textualFeedback = request.getParameter("chosenDiscounts[" + i + "][textualFeedback]");

            storeRanks.add(new StoreRank(storeId, rank, textualFeedback));
        }

        return new RankOrderStoresRequest(zoneName, orderId, customerId, storeRanks);
    }
}
