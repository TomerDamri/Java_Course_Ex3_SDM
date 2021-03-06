package course.java.sdm.engine.mapper;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import course.java.sdm.engine.model.*;
import course.java.sdm.engine.model.notifications.base.Notification;
import model.*;
import model.request.AddDiscountsToOrderRequest;
import model.request.ChosenItemDiscount;
import model.request.ChosenStoreDiscounts;
import model.request.ValidStoreDiscountsDTO;
import model.response.*;

public class DTOMapper {

    private static DTOMapper singletonDTOMapper = null;

    private DTOMapper () {
    }

    public static DTOMapper getDTOMapper () {
        if (singletonDTOMapper == null) {
            singletonDTOMapper = new DTOMapper();
        }

        return singletonDTOMapper;
    }

    public GetUserNotificationsResponse toGetUserNotificationsResponse (List<Notification> newNotifications) {
        List<String> notificationsStr = new ArrayList<>();
        newNotifications.forEach(notification -> notificationsStr.add(notification.toString()));

        return new GetUserNotificationsResponse(notificationsStr);
    }

    public GetFeedbackForStoreOwnerResponse createGetFeedbackForStoreOwnerResponse (List<StoreFeedback> storesFeedbacks) {
        List<StoreFeedbackDTO> orderStoreRanks = new ArrayList<>();
        int[] rankCounter = new int[1];
        storesFeedbacks.forEach(storeFeedback -> {
            List<CustomerFeedbackDTO> customersFeedbacks = storeFeedback.getCustomersFeedbacks()
                                                                        .stream()
                                                                        .map(cFeedback -> new CustomerFeedbackDTO(++rankCounter[0],
                                                                                                                  cFeedback.getOrderDate(),
                                                                                                                  cFeedback.getRank(),
                                                                                                                  cFeedback.getTextualFeedback(),
                                                                                                                  cFeedback.getCustomerName()))
                                                                        .collect(Collectors.toList());

            orderStoreRanks.add(new StoreFeedbackDTO(storeFeedback.getStoreId(), storeFeedback.getStoreName(), customersFeedbacks));
        });

        return new GetFeedbackForStoreOwnerResponse(orderStoreRanks);
    }

    public TransactionDTO toTransactionDTO (TransactionType type,
                                            LocalDate date,
                                            double transactionAmount,
                                            double prevBalance,
                                            double newBalance) {
        return new TransactionDTO(transactionTypeDTO(type), date, transactionAmount, prevBalance, newBalance);
    }

    private TransactionTypeDTO transactionTypeDTO (TransactionType transactionType) {
        switch (transactionType) {
        case DEPOSIT:
            return TransactionTypeDTO.DEPOSIT;
        case POSITIVE_TRANSFER:
            return TransactionTypeDTO.POSITIVE_TRANSFER;
        default:
            return TransactionTypeDTO.NEGATIVE_TRANSFER;
        }
    }

    public GetUserTransactionsResponse toGetUserTransactionsResponse (List<TransactionDTO> userTransactions) {
        return new GetUserTransactionsResponse(userTransactions);
    }

    public GetUserBalanceResponse toGetUserBalanceResponse (UUID userId, Double userBalance) {
        return new GetUserBalanceResponse(userId, Utils.round(userBalance, 2));
    }

    public FinalSummaryForOrder createFinalSummaryForOrder (Map<StoreDetails, Order> staticOrders, Map<Integer, SystemStore> systemStores) {
        double[] totalItemsPrice = new double[1], totalDeliveryPrice = new double[1], totalPrice = new double[1];

        List<StoreSummaryForOrder> storesSummaryForOrder = staticOrders.entrySet().stream().map(entry -> {
            Order currOrder = entry.getValue();
            totalItemsPrice[0] += currOrder.getItemsPrice();
            totalDeliveryPrice[0] += currOrder.getDeliveryPrice();
            totalPrice[0] += currOrder.getTotalPrice();

            return toStoreSummaryForOrder(entry.getKey(), currOrder, systemStores.get(entry.getKey().getId()));
        }).collect(Collectors.toList());

        return new FinalSummaryForOrder(Utils.round(totalItemsPrice[0], 2),
                                        Utils.round(totalDeliveryPrice[0], 2),
                                        Utils.round(totalPrice[0], 2),
                                        storesSummaryForOrder);
    }

    private StoreSummaryForOrder toStoreSummaryForOrder (StoreDetails storeDetails, Order order, SystemStore systemStore) {

        List<ItemSummaryForOrder> notPartOfDiscountItemsSummary = getNotPartOfDiscountItemsSummary(order);
        List<ItemSummaryForOrder> partOfDiscountItemsSummary = getPartOfDiscountItemsSummary(order, systemStore);

        List<ItemSummaryForOrder> allItemsSummary = new LinkedList<>();
        allItemsSummary.addAll(notPartOfDiscountItemsSummary);
        allItemsSummary.addAll(partOfDiscountItemsSummary);

        return new StoreSummaryForOrder(storeDetails.getId(),
                                        storeDetails.getName(),
                                        storeDetails.getDeliveryPpk(),
                                        order.getDistanceFromCustomerLocation(),
                                        order.getDeliveryPrice(),
                                        allItemsSummary);
    }

    private List<ItemSummaryForOrder> getNotPartOfDiscountItemsSummary (Order order) {
        return order.getPricedItems()
                    .entrySet()
                    .stream()
                    .map(entry -> getNotPartOfDiscountItemSummary(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList());
    }

    private List<ItemSummaryForOrder> getPartOfDiscountItemsSummary (Order order, SystemStore systemStore) {
        return order.getSelectedOfferToNumOfRealization().entrySet().stream().map(entry -> {
            Offer currOffer = entry.getKey();
            Integer numOfRealizations = entry.getValue();
            StoreItem storeItem = systemStore.getItemIdToStoreItem().get(currOffer.getItemId());

            return getPartOfDiscountItemSummary(storeItem, currOffer, numOfRealizations);
        }).collect(Collectors.toList());
    }

    private ItemSummaryForOrder getPartOfDiscountItemSummary (StoreItem storeItem, Offer currOffer, Integer numOfRealizations) {
        Double itemAmount = currOffer.getQuantity() * numOfRealizations;

        return new ItemSummaryForOrder(storeItem.getId(),
                                       storeItem.getName(),
                                       storeItem.getPurchaseCategory().toString(),
                                       itemAmount,
                                       currOffer.getForAdditional(),
                                       itemAmount * currOffer.getForAdditional(),
                                       true);
    }

    private ItemSummaryForOrder getNotPartOfDiscountItemSummary (PricedItem pricedItem, Double itemAmount) {

        return new ItemSummaryForOrder(pricedItem.getId(),
                                       pricedItem.getName(),
                                       pricedItem.getPurchaseCategory().toString(),
                                       itemAmount,
                                       pricedItem.getPrice(),
                                       itemAmount * pricedItem.getPrice(),
                                       false);
    }

    public GetDiscountsResponse createGetDiscountsResponse (Map<Integer, ValidStoreDiscounts> returnDiscounts,
                                                            Map<Integer, SystemStore> systemStores) {
        List<ValidStoreDiscountsDTO> returnDiscountDTO = returnDiscounts.entrySet().stream().map(entry -> {
            SystemStore systemStore = systemStores.get(entry.getKey());
            return toValidStoreDiscountsDTO(entry.getValue(), systemStore);
        }).collect(Collectors.toList());

        return new GetDiscountsResponse(returnDiscountDTO);
    }

    // public GetMapEntitiesResponse toGetSystemMappableEntitiesResponse(Collection<Mappable>
    // systemMappabels) {
    // List<MapEntity> mappableEntities =
    // systemMappabels.stream().map(this::toMappableEntity).collect(Collectors.toList());
    //
    // return new GetMapEntitiesResponse(mappableEntities);
    // }

    // private MapEntity toMappableEntity(Mappable entity) {
    // if (entity instanceof SystemStore) {
    // SystemStore systemStore = (SystemStore) entity;
    // return new StoreMapEntityDTO(systemStore.getId(),
    // toLocationDTO(systemStore.getLocation()),
    // systemStore.getName(),
    // systemStore.getOrders().size(),
    // systemStore.getDeliveryPpk());
    // } else if (entity instanceof SystemCustomer) {
    // SystemCustomer systemCustomer = (SystemCustomer) entity;
    // return new CustomerMapEntityDTO(systemCustomer.getId(),
    // toLocationDTO(systemCustomer.getLocation()),
    // systemCustomer.getName(),
    // systemCustomer.getNumOfOrders());
    // }
    //
    // throw new RuntimeException("The type of the mappable entity must be SystemCustomer or
    // SystemStore");
    // }

    public Set<User> toGetUsersResponse (Collection<SystemUser> systemUsers) {
        return systemUsers.stream().map(this::toUser).collect(Collectors.toSet());
    }

    public PlaceDynamicOrderResponse createPlaceDynamicOrderResponseV2 (TempOrder tempDynamicOrder) {
        List<DynamicOrderEntityDTO> dynamicOrderEntityDTOS = tempDynamicOrder.getStaticOrders()
                                                                             .entrySet()
                                                                             .stream()
                                                                             .map(entry -> toDynamicOrderEntityDTO(entry.getKey(),
                                                                                                                   entry.getValue()))
                                                                             .collect(Collectors.toList());

        return new PlaceDynamicOrderResponse(tempDynamicOrder.getOrderId(), dynamicOrderEntityDTOS);
    }

    public GetZonesResponse toGetZonesResponse (Collection<Zone> systemZones) {
        List<ZoneDTO> zones = systemZones.stream().map(zone -> {
            Map<UUID, List<SystemOrder>> systemOrders = zone.getSystemOrders();
            double sumOrdersPrice = systemOrders.values()
                                                .stream()
                                                .map(subOrders -> subOrders.stream()
                                                                           .map(SystemOrder::getItemsPrice)
                                                                           .mapToDouble(Double::doubleValue)
                                                                           .sum())
                                                .mapToDouble(Double::doubleValue)
                                                .sum();
            int numOfOrders = systemOrders.size();
            Double avgOrdersPrice = GeneratedDataMapper.round(sumOrdersPrice / numOfOrders, 2);

            return new ZoneDTO(zone.getZoneOwnerName(),
                               zone.getZoneName(),
                               zone.getSystemItems().size(),
                               zone.getSystemStores().size(),
                               numOfOrders,
                               avgOrdersPrice);
        }).collect(Collectors.toList());

        return new GetZonesResponse(zones);
    }

    private User toUser (SystemUser systemUser) {
        return new User(systemUser.getId(), systemUser.getName(), toUserType(systemUser.getUserType()));
    }

    private User.UserType toUserType (SystemUser.UserType type) {
        return type.equals(SystemUser.UserType.CUSTOMER) ? User.UserType.CUSTOMER : User.UserType.STORE_OWNER;
    }

    public GetCustomersResponse toGetCustomersResponse (Map<UUID, SystemCustomer> systemCustomers) {
        List<CustomerDTO> systemCustomersDTO = systemCustomers.values().stream().map(this::toCustomerDTO).collect(Collectors.toList());

        return new GetCustomersResponse(systemCustomersDTO);
    }

    public DynamicOrderEntityDTO toDynamicOrderEntityDTO (StoreDetails storeDetails, Order order) {
        return new DynamicOrderEntityDTO(storeDetails.getId(),
                                         storeDetails.getName(),
                                         toLocationDTO(storeDetails.getLocation()),
                                         order.getDistanceFromCustomerLocation(),
                                         storeDetails.getDeliveryPpk(),
                                         order.getNumOfItemTypes(),
                                         order.getAmountOfItems(),
                                         order.getDeliveryPrice(),
                                         order.getItemsPrice());
    }

    public GetZoneResponse toGetZoneResponse (Zone zone) {
        Map<Integer, SystemItemDTO> items = toDTO(zone.getSystemItems(),
                                                  this::toSystemItemDTO,
                                                  SystemItemDTO::getId,
                                                  systemItemDTO -> systemItemDTO);
        Map<Integer, StoreDTO> stores = toDTO(zone.getSystemStores(), this::toStoreDTO, StoreDTO::getId, storeDTO -> storeDTO);

        return new GetZoneResponse(new ArrayList<>(stores.values()), new ArrayList<>(items.values()));
    }

    public GetStoresResponse toGetStoresResponse (Map<Integer, SystemStore> systemStores) {
        Map<Integer, StoreDTO> stores = toDTO(systemStores, this::toStoreDTO, StoreDTO::getId, storeDTO -> storeDTO);

        return new GetStoresResponse(stores);

    }

    //
    // public GetItemsResponse toGetItemsResponse (Map<Integer, SystemItem> systemItems) {
    // Map<Integer, SystemItemDTO> items = toDTO(systemItems, this::toSystemItemDTO,
    // SystemItemDTO::getId, systemItemDTO -> systemItemDTO);
    //
    // return new GetItemsResponse(items);
    // }
    //
    public GetCustomerOrdersResponse toGetCustomerOrdersResponse (Map<UUID, List<SystemOrder>> systemOrders) {
        List<OrderDTO> orders = systemOrders.entrySet().stream().map(entry -> {
            UUID orderId = entry.getKey();
            List<SubOrderDTO> subOrders = entry.getValue().stream().map(this::toSubOrderDTO).collect(Collectors.toList());
            return new OrderDTO(orderId, subOrders);
        }).collect(Collectors.toList());

        return new GetCustomerOrdersResponse(orders);
    }

    public GetStoreItemsResponse toGetStoreItems (SystemStore systemStore) {
        List<PricedItemDTO> storeItems = toStoreItemsDTO(systemStore);

        return new GetStoreItemsResponse(storeItems);
    }

    private CustomerDTO toCustomerDTO (SystemCustomer systemCustomer) {
        int numOfOrders = systemCustomer.getNumOfOrders();
        double avgItemsPrice = systemCustomer.getTotalItemsPrice() / numOfOrders;
        double avgDeliveryPrice = systemCustomer.getTotalDeliveryPrice() / numOfOrders;

        return new CustomerDTO(systemCustomer.getId(),
                               systemCustomer.getName(),
                               numOfOrders,
                               GeneratedDataMapper.round(avgItemsPrice, 2),
                               GeneratedDataMapper.round(avgDeliveryPrice, 2));
    }

    private LocationDTO toLocationDTO (Location location) {
        return new LocationDTO(location.getX(), location.getY());
    }

    private List<PricedItemDTO> toStoreItemsDTO (SystemStore systemStore) {
        return systemStore.getItemIdToStoreItem()
                          .values()
                          .stream()
                          .map(storeItem -> toPricedItemDTO(storeItem.getPricedItem()))
                          .collect(Collectors.toList());
    }

    private StoreDTO toStoreDTO (SystemStore systemStore) {
        Map<Integer, StoreItemDTO> items = systemStore.getItemIdToStoreItem()
                                                      .values()
                                                      .stream()
                                                      .map(this::toStoreItemDTO)
                                                      .collect(Collectors.toMap(StoreItemDTO::getId, storeItemDTO -> storeItemDTO));

        List<Discount> storeDiscounts = new LinkedList<>();
        systemStore.getStoreDiscounts().values().forEach(storeDiscounts::addAll);
        List<DiscountDTO> storeDiscountsDTO = storeDiscounts.stream()
                                                            .map(discount -> toDiscountDTO(discount, systemStore))
                                                            .collect(Collectors.toList());

        double sumItemsPrice = systemStore.getOrders().stream().map(Order::getItemsPrice).mapToDouble(Double::doubleValue).sum();

        return new StoreDTO(systemStore.getStoreOwnerName(),
                            systemStore.getId(),
                            systemStore.getName(),
                            systemStore.getDeliveryPpk(),
                            toLocationDTO(systemStore.getLocation()),
                            new ArrayList<>(items.values()),
                            systemStore.getOrders().stream().map(this::toStoreOrderDTO).collect(Collectors.toList()),
                            systemStore.getTotalDeliveriesPayment(),
                            storeDiscountsDTO,
                            sumItemsPrice);
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

    public DeleteItemFromStoreResponse createDeleteItemFromStoreResponse (List<Discount> removedDiscounts) {
        List<String> removedDiscountsDTO = null;
        if (removedDiscounts != null) {
            removedDiscountsDTO = removedDiscounts.stream().map(Discount::getName).collect(Collectors.toList());
        }

        return new DeleteItemFromStoreResponse(removedDiscountsDTO);
    }

    public DiscountDTO toDiscountDTO (Discount discount, SystemStore systemStore) {
        Map<Integer, StoreItem> itemIdToStoreItem = systemStore.getItemIdToStoreItem();
        int ifYouBuyItemId = discount.getIfYouBuy().getItemId();
        String ifYouBuyItemName = itemIdToStoreItem.get(ifYouBuyItemId).getName();

        return toDiscountDTO(discount, systemStore, ifYouBuyItemName);
    }

    private DiscountDTO toDiscountDTO (Discount discount, SystemStore systemStore, String ifYouBuyItemName) {
        Map<Integer, StoreItem> itemIdToStoreItem = systemStore.getItemIdToStoreItem();

        String storeName = systemStore.getName();
        IfYouBy ifYouBuy = discount.getIfYouBuy();
        int ifYouBuyItemId = ifYouBuy.getItemId();

        ThenYouGet thenYouGet = discount.getThenYouGet();
        String discountName = discount.getName();
        List<OfferDTO> offers = thenYouGet.getOffers()
                                          .values()
                                          .stream()
                                          .map(offer -> toOfferDTO(offer, itemIdToStoreItem, ifYouBuyItemId, ifYouBuyItemName))
                                          .collect(Collectors.toList());

        return new DiscountDTO(storeName,
                               discountName,
                               ifYouBuyItemId,
                               ifYouBuyItemName,
                               ifYouBuy.getQuantity(),
                               thenYouGet.getOperator().toString(),
                               offers);
    }

    public ValidStoreDiscountsDTO toValidStoreDiscountsDTO (ValidStoreDiscounts validStoreDiscounts, SystemStore systemStore) {
        Map<Integer, List<Discount>> itemIdToValidStoreDiscounts = validStoreDiscounts.getItemIdToValidStoreDiscounts();

        List<ValidItemDiscountsDTO> validDiscounts = itemIdToValidStoreDiscounts.entrySet().stream().map(entry -> {
            Integer itemId = entry.getKey();
            String itemName = systemStore.getItemIdToStoreItem().get(itemId).getName();
            List<DiscountDTO> itemValidDiscounts = entry.getValue()
                                                        .stream()
                                                        .map(discount -> toDiscountDTO(discount, systemStore))
                                                        .collect(Collectors.toList());

            return new ValidItemDiscountsDTO(itemId, itemName, itemValidDiscounts);
        }).collect(Collectors.toList());

        return new ValidStoreDiscountsDTO(systemStore.getName(), systemStore.getId(), validDiscounts);
    }

    public Map<Integer, ChosenStoreDiscounts> createChosenDiscountsMapFromRequest (AddDiscountsToOrderRequest request) {
        Map<Integer, ChosenStoreDiscounts> storeIdToChosenDiscounts = new HashMap<>();
        request.getChosenDiscounts().forEach(chosenDiscountDTO -> {
            addChosenDiscountToMap(storeIdToChosenDiscounts, chosenDiscountDTO);
        });
        return storeIdToChosenDiscounts;
    }

    private void addChosenDiscountToMap (Map<Integer, ChosenStoreDiscounts> storeIdToChosenDiscounts,
                                         model.ChosenDiscountDTO chosenDiscountDTO) {
        ChosenStoreDiscounts chosenStoreDiscounts;
        List<ChosenItemDiscount> chosenItemDiscounts;
        Map<Integer, List<ChosenItemDiscount>> itemIdToChosenDiscounts;
        Optional<Integer> orOfferIdOpt = chosenDiscountDTO.getOrOfferId() != null ? Optional.of(chosenDiscountDTO.getOrOfferId())
                    : Optional.empty();
        ChosenItemDiscount newDiscount = new ChosenItemDiscount(chosenDiscountDTO.getDiscountName(),
                                                                chosenDiscountDTO.getNumOfRealizations(),
                                                                orOfferIdOpt);
        Integer storeId = chosenDiscountDTO.getStoreId();
        Integer itemId = chosenDiscountDTO.getItemId();

        if (storeIdToChosenDiscounts.containsKey(storeId)) {
            chosenStoreDiscounts = storeIdToChosenDiscounts.get(storeId);
            itemIdToChosenDiscounts = chosenStoreDiscounts.getItemIdToChosenDiscounts();
            chosenItemDiscounts = itemIdToChosenDiscounts.containsKey(itemId) ? itemIdToChosenDiscounts.get(itemId) : new ArrayList<>();
            chosenItemDiscounts.add(newDiscount);
            itemIdToChosenDiscounts.put(itemId, chosenItemDiscounts);
        }
        else {
            itemIdToChosenDiscounts = new HashMap<>();
            chosenItemDiscounts = new ArrayList<>();
            chosenItemDiscounts.add(newDiscount);
            itemIdToChosenDiscounts.put(itemId, chosenItemDiscounts);

            chosenStoreDiscounts = new ChosenStoreDiscounts(itemIdToChosenDiscounts);
        }
        storeIdToChosenDiscounts.put(storeId, chosenStoreDiscounts);
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

    private OfferDTO toOfferDTO (Offer offer, Map<Integer, StoreItem> itemIdToStoreItem, int ifYouBuyItemId, String ifYouBuyItemName) {
        int offerItemId = offer.getItemId();
        String offerItemName = (offerItemId == ifYouBuyItemId) ? ifYouBuyItemName : itemIdToStoreItem.get(offerItemId).getName();

        return new OfferDTO(offer.getId(), offer.getQuantity(), offerItemId, offerItemName, offer.getForAdditional());
    }

    private PricedItemDTO toPricedItemDTO (PricedItem pricedItem) {
        Item item = pricedItem.getItem();
        return new PricedItemDTO(item.getId(), item.getName(), item.getPurchaseCategory().toString(), pricedItem.getPrice());
    }

    private ItemDTO toItemDTO (Item item) {
        return new ItemDTO(item.getId(), item.getName(), item.getPurchaseCategory().name());

    }

    private SubOrderDTO toSubOrderDTO (SystemOrder systemOrder) {
        Map<Integer, Double> items = systemOrder.getOrderItems()
                                                .keySet()
                                                .stream()
                                                .collect(Collectors.toMap(PricedItem::getId,
                                                                          pricedItem -> systemOrder.getOrderItems().get(pricedItem)));
        return new SubOrderDTO(systemOrder.getZoneName(),
                               systemOrder.getId(),
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
