package sdm.servlets;

import com.google.gson.Gson;
import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import model.response.GetDiscountsResponse;
import sdm.utils.ServletUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

@WebServlet(name = "DiscountsServlet", urlPatterns = {"/pages/discounts"})
public class DiscountsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
}
