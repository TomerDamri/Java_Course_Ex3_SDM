package course.java.sdm.engine.mapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import course.java.sdm.engine.model.*;
import model.*;
import model.response.GetItemsResponse;
import model.response.GetOrdersResponse;
import model.response.GetStoresResponse;

public class DTOMapper {
    public GetStoresResponse toGetStoresResponse (Map<Integer, SystemStore> systemStores) {
        Map<Integer, StoreDTO> stores = systemStores.values()
                                                    .stream()
                                                    .map(this::toStoreDTO)
                                                    .collect(Collectors.toMap(StoreDTO::getId, storeDTO -> storeDTO));
        return new GetStoresResponse(stores);

    }

    public GetItemsResponse toGetItemsResponse (Map<Integer, SystemItem> systemItems) {
        Map<Integer, SystemItemDTO> items = systemItems.values()
                                                       .stream()
                                                       .map(this::toSystemItemDTO)
                                                       .collect(Collectors.toMap(SystemItemDTO::getId, systemItemDTO -> systemItemDTO));
        return new GetItemsResponse(items);
    }

    public GetOrdersResponse toGetOrdersResponse (Map<UUID, List<SystemOrder>> systemOrders) {
        Map<UUID, List<OrderDTO>> orders = systemOrders.entrySet()
                                                       .stream()
                                                       .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                 entry -> entry.getValue()
                                                                                               .stream()
                                                                                               .map(this::toOrderDTO)
                                                                                               .collect(Collectors.toList())));

        return new GetOrdersResponse(orders);
    }

    private StoreDTO toStoreDTO (SystemStore systemStore) {
        Map<Integer, StoreItemDTO> items = systemStore.getItemIdToStoreItem()
                                                      .values()
                                                      .stream()
                                                      .map(this::toStoreItemDTO)
                                                      .collect(Collectors.toMap(StoreItemDTO::getId, storeItemDTO -> storeItemDTO));
        return new StoreDTO(systemStore.getId(),
                            systemStore.getName(),
                            systemStore.getDeliveryPpk(),
                            systemStore.getLocation().getX(),
                            systemStore.getLocation().getY(),
                            items,
                            systemStore.getOrders().stream().map(this::toStoreOrderDTO).collect(Collectors.toList()),
                            systemStore.getTotalDeliveriesPayment());
    }

    private SystemItemDTO toSystemItemDTO (SystemItem systemItem) {
        return new SystemItemDTO(toItemDTO(systemItem.getItem()),
                                 systemItem.getStoresCount(),
                                 systemItem.getAvgPrice(),
                                 systemItem.getOrdersCount());
    }

    private StoreItemDTO toStoreItemDTO (StoreItem storeItem) {
        return new StoreItemDTO(toPricedItemDTO(storeItem.getPricedItem()), storeItem.getPurchasesCount());
    }

    private PricedItemDTO toPricedItemDTO (PricedItem pricedItem) {
        return new PricedItemDTO(toItemDTO(pricedItem.getItem()), pricedItem.getPrice());
    }

    private ItemDTO toItemDTO (Item item) {
        return new ItemDTO(item.getId(), item.getName(), item.getPurchaseCategory().name());

    }

    private OrderDTO toOrderDTO (SystemOrder systemOrder) {
        Map<Integer, Double> items = systemOrder.getOrderItems()
                                                .keySet()
                                                .stream()
                                                .collect(Collectors.toMap(PricedItem::getId,
                                                                          pricedItem -> systemOrder.getOrderItems().get(pricedItem)));
        return new OrderDTO(systemOrder.getId(),
                            systemOrder.getOrderDate(),
                            systemOrder.getOrderLocation().getX(),
                            systemOrder.getOrderLocation().getY(),
                            items,
                            systemOrder.getNumOfItemTypes(),
                            systemOrder.getAmountOfItems(),
                            systemOrder.getItemsPrice(),
                            systemOrder.getDeliveryPrice(),
                            systemOrder.getTotalPrice(),
                            systemOrder.getStoreName(),
                            systemOrder.getStoreId());

    }

    private StoreOrderDTO toStoreOrderDTO (Order order) {
        Map<PricedItem, Double> pricedItems = order.getPricedItems();
        Map<Integer, Double> items = pricedItems.keySet()
                                                .stream()
                                                .collect(Collectors.toMap(PricedItem::getId, pricedItem -> pricedItems.get(pricedItem)));

        return new StoreOrderDTO(order.getParentId(),
                                 order.getId(),
                                 order.getOrderDate(),
                                 order.getOrderLocation().getX(),
                                 order.getOrderLocation().getY(),
                                 items,
                                 order.getNumOfItemTypes(),
                                 order.getAmountOfItems(),
                                 order.getItemsPrice(),
                                 order.getDeliveryPrice(),
                                 order.getTotalPrice());
    }
}
