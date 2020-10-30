package sdm.servlets;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import course.java.sdm.engine.controller.ISDMController;
import model.ItemToAddDTO;
import model.request.AddStoreToZoneRequest;

@WebServlet(name = "AddStoreServlet", urlPatterns = { "/pages/addStore" })
public class AddStoreServlet extends BaseServlet {
    @Override
    protected void doPost (HttpServletRequest request, HttpServletResponse response) {
        processRateStore(request, response);
    }

    private void processRateStore (HttpServletRequest request, HttpServletResponse response) {
        AddStoreToZoneRequest addStoreRequest = createAddStoreToZoneRequest(request);
        ISDMController controller = getSDMController();
        controller.addStoreToZone(addStoreRequest);
        response.setStatus(200);
    }

    private AddStoreToZoneRequest createAddStoreToZoneRequest (HttpServletRequest request) {
        UUID storeOwnerId = UUID.fromString(request.getParameter("storeOwnerId"));
        String zoneName = request.getParameter("zoneName");
        String storeName = request.getParameter("storeName");
        int xCoordinate = Integer.parseInt(request.getParameter("xCoordinate"));
        int yCoordinate = Integer.parseInt(request.getParameter("yCoordinate"));
        int deliveryPpk = Integer.parseInt(request.getParameter("deliveryPpk"));
        int itemsCount = Integer.parseInt(request.getParameter("itemsCount"));
        List<ItemToAddDTO> itemsToAdd = new ArrayList<>();
        for (int i = 0; i < itemsCount; i++) {
            int itemId = Integer.parseInt(request.getParameter("storeItems[" + i + "][id]"));
            int itemPrice = Integer.parseInt(request.getParameter("storeItems[" + i + "][price]"));
            itemsToAdd.add(new ItemToAddDTO(itemId, itemPrice));
        }

        return new AddStoreToZoneRequest(storeOwnerId, zoneName, storeName, xCoordinate, yCoordinate, deliveryPpk, itemsToAdd);
    }
}
