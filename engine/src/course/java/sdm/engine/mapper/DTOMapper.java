package course.java.sdm.engine.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import course.java.sdm.engine.model.*;
import model.*;
import model.request.ValidStoreDiscountsDTO;
import model.response.*;

public class DTOMapper {

    public GetDiscountsResponse createGetDiscountsResponse (Map<Integer, ValidStoreDiscounts> returnDiscounts) {
        Map<Integer, ValidStoreDiscountsDTO> returnDiscountDTO = returnDiscounts.entrySet()
                                                                                .stream()
                                                                                .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                          entry -> toValidStoreDiscountsDTO(entry.getValue())));

        return new GetDiscountsResponse(returnDiscountDTO);
    }

    public GetMapEntitiesResponse toGetSystemMappableEntitiesResponse (Collection<Mappable> systemMappabels) {
        List<MapEntity> mappableEntities = systemMappabels.stream().map(this::toMappableEntity).collect(Collectors.toList());

        return new GetMapEntitiesResponse(mappableEntities);
    }

    private MapEntity toMappableEntity (Mappable entity) {
        if (entity instanceof SystemStore) {
            SystemStore systemStore = (SystemStore) entity;
            return new StoreMapEntityDTO(systemStore.getId(),
                                              toLocationDTO(systemStore.getLocation()),
                                              systemStore.getName(),
                                              systemStore.getOrders().size(),
                                              systemStore.getDeliveryPpk());
        }
        else if (entity instanceof SystemCustomer) {
            SystemCustomer systemCustomer = (SystemCustomer) entity;
            return new CustomerMapEntityDTO(systemCustomer.getId(),
                                                 toLocationDTO(systemCustomer.getLocation()),
                                                 systemCustomer.getName(),
                                                 systemCustomer.getNumOfOrders());
        }

        throw new RuntimeException("The type of the mappable entity must be SystemCustomer or SystemStore");
    }

    public GetCustomersResponse toGetCustomersResponse (Map<Integer, SystemCustomer> systemCustomer) {
        Map<Integer, CustomerDTO> systemCustomersDTO = toDTO(systemCustomer,
                                                             this::toCustomerDTO,
                                                             CustomerDTO::getId,
                                                             customerDTO -> customerDTO);

        return new GetCustomersResponse(systemCustomersDTO);
    }

    public DynamicOrderEntityDTO toDynamicOrderEntityDTO (StoreDetails storeDetails, Order order) {
        return new DynamicOrderEntityDTO(storeDetails.getId(),
                                         storeDetails.getName(),
                                         toLocationDTO(storeDetails.getLocation()),
                                         order.getDistanceFromCustomerLocation(),
                                         storeDetails.getDeliveryPpk(),
                                         order.getNumOfItemTypes(),
                                         order.getDeliveryPrice(),
                                         order.getItemsPrice());
    }

    public GetStoresResponse toGetStoresResponse (Map<Integer, SystemStore> systemStores) {
        Map<Integer, StoreDTO> stores = toDTO(systemStores, this::toStoreDTO, StoreDTO::getId, storeDTO -> storeDTO);

        return new GetStoresResponse(stores);

    }

    public GetItemsResponse toGetItemsResponse (Map<Integer, SystemItem> systemItems) {
        Map<Integer, SystemItemDTO> items = toDTO(systemItems, this::toSystemItemDTO, SystemItemDTO::getId, systemItemDTO -> systemItemDTO);

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

    private CustomerDTO toCustomerDTO (SystemCustomer systemCustomer) {
        int numOfOrders = systemCustomer.getNumOfOrders();
        double avgItemsPrice = systemCustomer.getTotalItemsPrice() / numOfOrders;
        double avgDeliveryPrice = systemCustomer.getTotalDeliveryPrice() / numOfOrders;

        return new CustomerDTO(systemCustomer.getId(),
                               systemCustomer.getName(),
                               toLocationDTO(systemCustomer.getLocation()),
                               numOfOrders,
                               GeneratedDataMapper.round(avgItemsPrice, 2),
                               GeneratedDataMapper.round(avgDeliveryPrice, 2));
    }

    private LocationDTO toLocationDTO (Location location) {
        return new LocationDTO(location.getX(), location.getY());
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
                            toLocationDTO(systemStore.getLocation()),
                            items,
                            systemStore.getOrders().stream().map(this::toStoreOrderDTO).collect(Collectors.toList()),
                            systemStore.getTotalDeliveriesPayment());
    }

    private SystemItemDTO toSystemItemDTO (SystemItem systemItem) {
        Item item = systemItem.getItem();
        return new SystemItemDTO(item.getId(),
                                 item.getName(),
                                 item.getPurchaseCategory().toString(),
                                 systemItem.getStoresCount(),
                                 systemItem.getAvgPrice(),
                                 systemItem.getOrdersCount(),
                                 systemItem.getDiscountOrdersCount());
    }

    public DiscountDTO toDiscountDTO (Discount discount) {
        IfYouBy ifYouBuy = discount.getIfYouBuy();
        ThenYouGet thenYouGet = discount.getThenYouGet();
        String name = discount.getName();
        Map<Integer, OfferDTO> offers = thenYouGet.getOffers()
                                                  .values()
                                                  .stream()
                                                  .map(this::toOfferDTO)
                                                  .collect(Collectors.toMap(OfferDTO::getId, offerDTO -> offerDTO));

        return new DiscountDTO(name, ifYouBuy.getItemId(), ifYouBuy.getQuantity(), thenYouGet.getOperator().toString(), offers);
    }

    public ValidStoreDiscountsDTO toValidStoreDiscountsDTO (ValidStoreDiscounts validStoreDiscounts) {
        Map<Integer, List<Discount>> itemIdToValidStoreDiscounts = validStoreDiscounts.getItemIdToValidStoreDiscounts();

        return new ValidStoreDiscountsDTO(itemIdToValidStoreDiscounts.entrySet()
                                                                     .stream()
                                                                     .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                               integerListEntry -> integerListEntry.getValue()
                                                                                                                                   .stream()
                                                                                                                                   .map(this::toDiscountDTO)
                                                                                                                                   .collect(Collectors.toList()))));
    }

    private StoreItemDTO toStoreItemDTO (StoreItem storeItem) {
        PricedItem pricedItem = storeItem.getPricedItem();
        return new StoreItemDTO(pricedItem.getId(),
                                pricedItem.getName(),
                                pricedItem.getPurchaseCategory().toString(),
                                pricedItem.getPrice(),
                                storeItem.getPurchasesCount(),
                                storeItem.getDiscountPurchasesCount());
    }

    private OfferDTO toOfferDTO (Offer offer) {
        return new OfferDTO(offer.getId(), offer.getQuantity(), offer.getItemId(), offer.getForAdditional());
    }

    private PricedItemDTO toPricedItemDTO (PricedItem pricedItem) {
        Item item = pricedItem.getItem();
        return new PricedItemDTO(item.getId(), item.getName(), item.getPurchaseCategory().toString(), pricedItem.getPrice());
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
                            toLocationDTO(systemOrder.getOrderLocation()),
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
        Map<Integer, Double> items = pricedItems.keySet().stream().collect(Collectors.toMap(PricedItem::getId, pricedItems::get));

        return new StoreOrderDTO(order.getParentId(),
                                 order.getId(),
                                 order.getOrderDate(),
                                 toLocationDTO(order.getOrderLocation()),
                                 items,
                                 order.getNumOfItemTypes(),
                                 order.getAmountOfItems(),
                                 order.getItemsPrice(),
                                 order.getDeliveryPrice(),
                                 order.getTotalPrice());
    }

    private <K, PV, NV> Map<K, NV> toDTO (Map<K, PV> prevMap,
                                          Function<PV, NV> toDTOFunction,
                                          Function<NV, K> getKeyFunction,
                                          Function<NV, NV> hetValueFunction) {
        Map<K, NV> newMap = prevMap.values().stream().map(toDTOFunction).collect(Collectors.toMap(getKeyFunction, hetValueFunction));

        return newMap;
    }
}
