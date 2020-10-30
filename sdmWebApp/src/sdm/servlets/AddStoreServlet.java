package sdm.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import course.java.sdm.engine.controller.ISDMController;
import model.ItemToAddDTO;
import model.request.AddStoreToZoneRequest;
import sdm.utils.ServletUtils;

@WebServlet(name = "AddStoreServlet", urlPatterns = { "/pages/addStore" })
public class AddStoreServlet extends BaseServlet {
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
        AddStoreToZoneRequest addStoreRequest = createAddStoreToZoneRequest(request);
        ISDMController controller = getSDMController();
        controller.addStoreToZone(addStoreRequest);
        response.setStatus(200);
    }

    private AddStoreToZoneRequest createAddStoreToZoneRequest (HttpServletRequest request) {
        UUID storeOwnerId = ServletUtils.tryParse(request.getParameter("storeOwnerId"), UUID::fromString, UUID.class);
        String zoneName = request.getParameter("zoneName");
        String storeName = request.getParameter("storeName");
        Integer xCoordinate = ServletUtils.tryParse(request.getParameter("xCoordinate"), Integer::parseInt, Integer.class);
        Integer yCoordinate = ServletUtils.tryParse(request.getParameter("yCoordinate"), Integer::parseInt, Integer.class);
        Integer deliveryPpk = ServletUtils.tryParse(request.getParameter("deliveryPpk"), Integer::parseInt, Integer.class);
        Integer itemsCount = ServletUtils.tryParse(request.getParameter("itemsCount"), Integer::parseInt, Integer.class);
        List<ItemToAddDTO> itemsToAdd = new ArrayList<>();
        for (int i = 0; i < itemsCount; i++) {
            Integer itemId = ServletUtils.tryParse(request.getParameter("storeItems[" + i + "][id]"), Integer::parseInt, Integer.class);
            Integer itemPrice = ServletUtils.tryParse(request.getParameter("storeItems[" + i + "][price]"),
                                                      Integer::parseInt,
                                                      Integer.class);
            itemsToAdd.add(new ItemToAddDTO(itemId, itemPrice));
        }

        return new AddStoreToZoneRequest(storeOwnerId, zoneName, storeName, xCoordinate, yCoordinate, deliveryPpk, itemsToAdd);
    }
}
