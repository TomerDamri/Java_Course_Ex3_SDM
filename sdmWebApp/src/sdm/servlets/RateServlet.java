package sdm.servlets;

import static sdm.constants.Constants.ZONE_NAME;

import java.util.UUID;
import java.util.function.Consumer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import course.java.sdm.engine.controller.ISDMController;
import model.request.GetFeedbackForStoreOwnerRequest;
import model.request.RankOrderStoresRequest;
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
        RankOrderStoresRequest rateStoreRequest = createRequestFromString(request.getParameter("rateStoreRequest"),
                                                                         RankOrderStoresRequest.class);
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
}
