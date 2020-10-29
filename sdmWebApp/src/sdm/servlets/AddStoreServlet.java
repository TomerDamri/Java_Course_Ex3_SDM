package sdm.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import course.java.sdm.engine.controller.ISDMController;
import model.request.AddStoreToZoneRequest;

@WebServlet(name = "AddStoreServlet", urlPatterns = { "/pages/addStore" })
public class AddStoreServlet extends BaseServlet {
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) {
       processRateStore(request, response);
    }

    private void processRateStore (HttpServletRequest request, HttpServletResponse response) {
        AddStoreToZoneRequest addStoreRequest = createRequestFromString(request.getParameter("addStoreRequest"),
                                                                       AddStoreToZoneRequest.class);
        ISDMController controller = getSDMController();
        controller.addStoreToZone(addStoreRequest);
        response.setStatus(200);
    }

}
