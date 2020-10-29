package sdm.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import course.java.sdm.engine.controller.ISDMController;
import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import model.request.AddDiscountsToOrderRequest;
import model.response.FinalSummaryForOrder;
import model.response.GetDiscountsResponse;
import sdm.utils.ServletUtils;

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
        AddDiscountsToOrderRequest addDiscountsToOrderRequest = createRequestFromString(request.getParameter("request"),
                AddDiscountsToOrderRequest.class);
        ISDMController controller = getSDMController();
        FinalSummaryForOrder finalSummaryForOrder = controller.addDiscountsToOrder(addDiscountsToOrderRequest);
        createJsonResponse(response, finalSummaryForOrder);
    }
}
