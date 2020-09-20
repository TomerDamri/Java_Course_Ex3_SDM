package course.java.sdm.console;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import course.java.sdm.engine.controller.ISDMController;
import course.java.sdm.engine.controller.impl.SDMControllerImpl;
import model.DynamicOrderEntityDTO;
import model.OrderDTO;
import model.StoreDTO;
import model.SystemItemDTO;
import model.request.*;
import model.response.*;

public class Menu {

    private static final int COORDINATE_MIN_VALUE = 1;
    private static final int COORDINATE_MAX_VALUE = 50;
    private static final int MIN_MENU_OPTION = 1;
    private static final int MAX_MENU_OPTION = 7;
    private static final String WEIGHT = "WEIGHT";
    private static final String QUANTITY = "QUANTITY";

    protected boolean quit = false;
    private final Scanner scanner = new Scanner(System.in);
    private final ISDMController controller = new SDMControllerImpl();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm");
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
    private Map<Integer, StoreDTO> stores;
    private Map<Integer, SystemItemDTO> items;

    public void displayMenu () {
        do {
            printMenuOptions();
            // int userChoice = getUserChoice();
            int userChoice = Integer.parseInt(scanner.nextLine());
            if (!quit) {
                handleUserChoice(userChoice);
            }
        }
        while (!quit);
    }

    private void printMenuOptions () {
        System.out.println("\n\nPlease choose one of the following options, or press 'q' to quit:\n1. Load system data from file\n2. Display stores\n3. Display items\n4. Place new order\n5. Display orders history\n6. Save orders history to file\n7. Load orders history from file\n");
    }

    private int getUserChoice () {
        String userInput;
        int userChoice = 0;
        boolean isValidChoice = true;
        do {
            if (!isValidChoice) {
                System.out.println(String.format("Your Choice is not valid, please enter a value between %s-%s or press 'q' to quit",
                                                 MIN_MENU_OPTION,
                                                 MAX_MENU_OPTION));
            }
            userInput = scanner.nextLine();

            if (userInput.equals("q") || userInput.equals("Q")) {
                this.quit = true;
                isValidChoice = true;
            }
            else {
                try {
                    userChoice = Integer.parseInt(userInput);
                    isValidChoice = (userChoice >= MIN_MENU_OPTION && userChoice <= MAX_MENU_OPTION);
                }
                catch (NumberFormatException ex) {
                    isValidChoice = false;
                }
            }
        }
        while (!isValidChoice);
        return userChoice;
    }

    private void handleUserChoice (int userChoice) {
        if (userChoice == 1) {
            handleLoadData();
        }
        else {
            if (!controller.isFileLoaded()) {
                System.out.println("Your request could not be processed, please load system data first");
            }
            else {
                switch (userChoice) {
                case 2:
                    handleDisplayStores();
                    break;
                case 3:
                    handleDisplayItems();
                    break;
                case 4:
                    handlePlaceOrder();
                    break;
                case 5:
                    handleDisplayOrders();
                    break;
                case 6:
                    handleSaveOrdersHistory();
                    break;
                case 7:
                    handleLoadOrdersHistory();
                    break;
                case 8:
                    GetCustomersResponse customers = controller.getCustomers();
                    System.out.println(9);
                    break;
                case 9:
                    GetSystemMappableEntitiesResponse systemMappableEntities = controller.getSystemMappableEntities();
                    System.out.println(systemMappableEntities.getAllSystemMappableEntities());
                    break;
                default:
                    break;
                }
            }
        }
    }

    private void handleLoadOrdersHistory () {
        System.out.println("Please insert the path where you want to load orders history from");
        String userInput = scanner.nextLine();
        try {
            controller.loadOrdersHistoryFromFile(userInput);
            System.out.println("System data loaded from file successfully");
        }
        catch (Exception e) {
            System.out.println("Failed loading from file.\nError message : " + e.getMessage());
        }
    }

    private void handleSaveOrdersHistory () {
        System.out.println("Please insert the path where you want to store the orders history");
        String userInput = scanner.nextLine();
        try {
            controller.saveOrdersHistoryToFile(userInput);
            System.out.println("Orders history saved to file successfully");
        }
        catch (Exception e) {
            System.out.println("Failed saving orders history to file.\n" + e.getMessage());
        }
    }

    private void handleLoadData () {
        System.out.println("Please enter full path of XML data file");
        String userInput = scanner.nextLine();
        try {
            controller.loadFile(userInput);
            System.out.println("File loaded successfully");
        }
        catch (Exception e) {
            System.out.println("Failed loading file.\n" + e.getMessage());
        }
    }

    private void handleDisplayStores () {
        try {
            GetStoresResponse response = controller.getStores();
            this.stores = response.getStores();
            if (this.stores != null && !this.stores.isEmpty()) {
                Iterator<StoreDTO> iterator = this.stores.values().iterator();
                while (iterator.hasNext()) {
                    StoreDTO store = iterator.next();
                    System.out.print("\n{" + store + "}");
                    if (iterator.hasNext()) {
                        System.out.println(",");
                    }
                }
            }
            else {
                System.out.println("No stored have yet been loaded");
            }
        }
        catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void handleDisplayItems () {
        try {
            GetItemsResponse response = controller.getItems();
            this.items = response.getItems();
            if (this.items != null && !this.items.isEmpty()) {
                Iterator<SystemItemDTO> iterator = this.items.values().iterator();
                while (iterator.hasNext()) {
                    SystemItemDTO item = iterator.next();
                    System.out.print("\n{" + item + "}");
                    if (iterator.hasNext()) {
                        System.out.println(",");
                    }
                }
            }
            else {
                System.out.println("No items have yet been loaded");
            }
        }
        catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void handleDisplayOrders () {
        try {
            GetOrdersResponse response = controller.getOrders();
            if (response.getOrders() == null || response.getOrders().isEmpty()) {
                System.out.println("No orders have yet been placed");
                return;
            }

            response.getOrders().forEach( (key, orders) -> {
                Iterator<OrderDTO> iterator = orders.iterator();
                String message = (orders.size() > 1) ? String.format("\nDynamic order id : %s [", key) : "\nStatic Order:";
                System.out.printf(message);

                while (iterator.hasNext()) {
                    OrderDTO order = iterator.next();
                    System.out.print("\n{" + order + "}");
                    if (iterator.hasNext()) {
                        System.out.println(",");
                    }
                }

                if (orders.size() > 1) {
                    System.out.printf("]");
                }
                System.out.print("\n\n");
            });

        }
        catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void handlePlaceOrder () {
        boolean stop;
        do {
            System.out.println("Press 1 for static order or 2 for dynamic order");
            String userInput = scanner.nextLine();
            switch (userInput) {
            case "1":
                stop = true;
                handleStaticOrder();
                break;
            case "2":
                stop = true;
                handleDynamicOrder();
                break;
            default:
                stop = false;
                System.out.println("Invalid choice");
            }
        }
        while (!stop);
    }

    private void handleDynamicOrder () {
        LocalDateTime date = getOrderDate();
        Location location = getOrderLocation();
        Map<Integer, Double> orderItemToAmount = getDynamicOrderItems();
        if (orderItemToAmount.isEmpty()) {
            System.out.println("No items were selected, the order is canceled");
        }
        else {
            PlaceDynamicOrderResponse response = controller.placeDynamicOrder(new PlaceDynamicOrderRequest(1,
                                                                                                           date,
                                                                                                           location.x,
                                                                                                           location.y,
                                                                                                           orderItemToAmount));
            UUID orderId = response.getId();
            Map<Integer, ValidStoreDiscounts> discounts = controller.getDiscounts(orderId);
            controller.addDiscountsToOrder(createAddDiscountsToOrderRequestForPlaceDynamicOrderTest(orderId));
            controller.completeTheOrder(orderId, true);

//            displayDynamicOrderOffer(response);
            System.out.println(("Enter 'Y' to confirm or any other key to cancel the order"));
            String userInput = scanner.nextLine();
            if (userInput.equals("Y") || userInput.equals("y")) {
                controller.completeDynamicOrder(response.getId(), true);
                System.out.println("Order created successfully\nOrder id:" + response.getId());
            }
            else {
                controller.completeDynamicOrder(response.getId(), false);
                System.out.println("Order creation canceled");
            }
        }
    }

    private void displayDynamicOrderOffer (PlaceDynamicOrderResponse response) {
        System.out.println("The order offer:");
        Iterator<DynamicOrderEntityDTO> iterator = response.getDynamicOrderEntity().iterator();
        while (iterator.hasNext()) {
            DynamicOrderEntityDTO orderEntity = iterator.next();
            System.out.print("{" + orderEntity + "}");
            if (iterator.hasNext()) {
                System.out.println(",");
            }
        }
        System.out.println();
    }

    private Map<Integer, Double> getDynamicOrderItems () {
        Map<Integer, Double> orderItemToAmount = new HashMap<>();
        boolean doneSelectingItems = false;
        int itemId;
        double itemAmount;
        do {
            System.out.println("Please select items to order. Press 'q' when done selecting");
            itemId = selectDynamicItem();
            if (itemId == -1) {
                doneSelectingItems = true;
            }
            else {
                itemAmount = getItemAmount(itemId);
                // Updating amount in case the item is already in the order
                if (orderItemToAmount.containsKey(itemId)) {
                    itemAmount = (orderItemToAmount.get(itemId) + itemAmount);
                }
                orderItemToAmount.put(itemId, itemAmount);
            }
        }
        while (!doneSelectingItems);
        System.out.println("Done selecting items");
        return orderItemToAmount;
    }

    private int selectDynamicItem () {
        boolean isValidItem;
        String userInput;
        int itemId = -1;
        do {
            System.out.println("Please enter the id of the item you want to order");
            displayDynamicItems();
            userInput = scanner.nextLine();
            if (userInput.equals("q") || userInput.equals("Q")) {
                itemId = -1;
                isValidItem = true;
            }
            else {
                try {
                    itemId = Integer.parseInt(userInput);
                    isValidItem = this.items.containsKey(itemId);
                    if (!isValidItem) {
                        System.out.println("The item id does not exist");
                    }
                }
                catch (Exception exception) {
                    isValidItem = false;
                    System.out.println("Item id should be an integer");
                }
            }
        }
        while (!isValidItem);

        return itemId;
    }

    private void displayDynamicItems () {
        items = controller.getItems().getItems();
        items.values().forEach(item -> {
            System.out.println("{" + item.getItem() + "}");
        });
    }

    private void handleStaticOrder () {
        int orderStoreId = getOrderStore();
        LocalDateTime date = getOrderDate();
        Location location = getOrderLocation();
        Map<Integer, Double> orderItemToAmount = getOrderItems(orderStoreId);
        if (orderItemToAmount.isEmpty()) {
            System.out.println("No items were selected, the order is canceled");
        }
        else {
            // TODO: 05/09/2020 - add customer ID to request
            PlaceOrderRequest request = new PlaceOrderRequest(1, orderStoreId, date, location.x, location.y, orderItemToAmount);
            printOrderSummary(request);
            System.out.println(("Enter 'Y' to confirm or any other key to cancel the order"));
            String userInput = scanner.nextLine();
            if (userInput.equals("Y") || userInput.equals("y")) {
                try {
                    PlaceOrderResponse response = controller.placeStaticOrder(request);
                    UUID orderId = response.getOrderId();
                    Map<Integer, ValidStoreDiscounts> discounts = controller.getDiscounts(orderId);
                    controller.addDiscountsToOrder(createAddDiscountsToOrderRequestForPlaceStaicOrderTest(orderId));
                    controller.completeTheOrder(orderId, true);

                    System.out.println("Order created successfully\nOrder id:" + orderId);
                }
                catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }
            }
            else {
                System.out.println("Order creation canceled");
            }
        }

    }

    private AddDiscountsToOrderRequest createAddDiscountsToOrderRequestForPlaceDynamicOrderTest (UUID orderId) {
        Map<Integer, List<ChosenItemDiscount>> itemIdToChosenDiscounts1 = new HashMap<>();
        Map<Integer, List<ChosenItemDiscount>> itemIdToChosenDiscounts3 = new HashMap<>();

        ChosenItemDiscount chosenItemDiscount1 = new ChosenItemDiscount("YallA BaLaGaN", 2, Optional.of(1));
        ChosenItemDiscount chosenItemDiscount2 = new ChosenItemDiscount("YallA BaLaGaN", 3, Optional.of(2));
        List<ChosenItemDiscount> chosenItemDiscountList1 = new LinkedList<>();
        chosenItemDiscountList1.add(chosenItemDiscount1);
        chosenItemDiscountList1.add(chosenItemDiscount2);
        itemIdToChosenDiscounts1.put(1, chosenItemDiscountList1);

        ChosenItemDiscount chosenItemDiscount3 = new ChosenItemDiscount("Li Ze Ole Yoter !", 3, Optional.empty());
        List<ChosenItemDiscount> chosenItemDiscountList2 = new LinkedList<>();
        chosenItemDiscountList2.add(chosenItemDiscount3);
        itemIdToChosenDiscounts3.put(5, chosenItemDiscountList2);

        ChosenStoreDiscounts chosenStoreDiscounts1 = new ChosenStoreDiscounts(itemIdToChosenDiscounts1);
        ChosenStoreDiscounts chosenStoreDiscounts3 = new ChosenStoreDiscounts(itemIdToChosenDiscounts3);

        Map<Integer, ChosenStoreDiscounts> storeIdToChosenDiscounts = new HashMap<>();
        storeIdToChosenDiscounts.put(1, chosenStoreDiscounts1);
        storeIdToChosenDiscounts.put(3, chosenStoreDiscounts3);

        AddDiscountsToOrderRequest addDiscountsToOrderRequest = new AddDiscountsToOrderRequest(orderId, storeIdToChosenDiscounts);
        return addDiscountsToOrderRequest;
    }

    private AddDiscountsToOrderRequest createAddDiscountsToOrderRequestForPlaceStaicOrderTest (UUID orderId) {
        Map<Integer, List<ChosenItemDiscount>> itemIdToChosenDiscounts = new HashMap<>();

        ChosenItemDiscount chosenItemDiscount1 = new ChosenItemDiscount("YallA BaLaGaN", 2, Optional.of(1));
        ChosenItemDiscount chosenItemDiscount2 = new ChosenItemDiscount("YallA BaLaGaN", 3, Optional.of(2));
        List<ChosenItemDiscount> chosenItemDiscountList = new LinkedList<>();
        chosenItemDiscountList.add(chosenItemDiscount1);
        chosenItemDiscountList.add(chosenItemDiscount2);
        itemIdToChosenDiscounts.put(1, chosenItemDiscountList);

        Map<Integer, ChosenStoreDiscounts> storeIdToChosenDiscounts = new HashMap<>();
        ChosenStoreDiscounts chosenStoreDiscounts = new ChosenStoreDiscounts(itemIdToChosenDiscounts);
        storeIdToChosenDiscounts.put(1, chosenStoreDiscounts);

        AddDiscountsToOrderRequest addDiscountsToOrderRequest = new AddDiscountsToOrderRequest(orderId, storeIdToChosenDiscounts);
        return addDiscountsToOrderRequest;
    }

    private int getOrderStore () {
        int id = 0;
        boolean validStore = true;
        do {
            if (!validStore) {
                System.out.println("Invalid store id");
            }
            System.out.println("Please enter the id of the store you want to order from:");
            displayStoresBeforePlacingOrder();
            try {
                id = scanner.nextInt();
                scanner.nextLine();
                validStore = validateStoreId(id);
            }
            catch (Exception exception) {
                validStore = false;
                scanner.nextLine();
            }
        }
        while (!validStore);
        return id;
    }

    private void displayStoresBeforePlacingOrder () {
        if (this.stores == null || this.stores.isEmpty()) {
            this.stores = controller.getStores().getStores();
        }
        this.stores.values()
                   .forEach(store -> System.out.println(String.format("{Id: %s\nName: %s\nDelivery PPK: %s},",
                                                                      store.getId(),
                                                                      store.getName(),
                                                                      store.getDeliveryPpk())));
    }

    private boolean validateStoreId (int id) {
        if (this.stores == null || this.stores.isEmpty()) {
            this.stores = controller.getStores().getStores();
        }
        return stores.containsKey(id);
    }

    private LocalDateTime getOrderDate () {
        LocalDateTime localDateTime = null;
        boolean stop;
        do {
            System.out.println(String.format("Please enter the order date in the following format dd/MM-hh:mm"));
            String userInput = scanner.nextLine();
            try {
                String dateWithYear = concatYearToDate(userInput);
                localDateTime = LocalDateTime.parse(dateWithYear, formatter);
                stop = true;
            }
            catch (Exception exception) {
                System.out.println(exception.getMessage());
                stop = false;
            }
        }
        while (!stop);
        return localDateTime;
    }

    private Location getOrderLocation () {
        boolean validLocation;
        int x;
        int y;
        do {
            System.out.println("Please enter your location.\nThe x coordinate:");
            x = getCoordinate();
            System.out.println("Please enter the y coordinate:");
            y = getCoordinate();
            validLocation = controller.isValidLocation(x, y);
            if (!validLocation) {
                System.out.println("Invalid location.This location already populated by store.");
            }
        }
        while (!validLocation);
        return new Location(x, y);
    }

    private int getCoordinate () {
        int coordinate = 0;
        boolean validCoordinate = true;
        do {
            if (!validCoordinate) {
                System.out.println("Invalid coordinate");
            }
            System.out.println("Please enter a coordinate of range [1,50]");
            try {
                coordinate = scanner.nextInt();
                scanner.nextLine();
                validCoordinate = validateCoordinateRange(coordinate);
            }
            catch (Exception exception) {
                validCoordinate = false;
                scanner.nextLine();
            }
        }
        while (!validCoordinate);
        return coordinate;
    }

    private boolean validateCoordinateRange (int coordinate) {
        return (coordinate >= COORDINATE_MIN_VALUE && coordinate <= COORDINATE_MAX_VALUE);
    }

    private Map<Integer, Double> getOrderItems (int storeId) {
        Map<Integer, Double> itemsToAmount = new HashMap<>();
        boolean doneSelectingItems = false;
        int itemId;
        double itemAmount;
        do {
            System.out.println("Please select items to order. Press 'q' when done selecting");
            itemId = staticSelectItem(storeId);
            if (itemId == -1) {
                doneSelectingItems = true;
            }
            else {
                itemAmount = getItemAmount(itemId);
                // Updating amount in case the item is already in the order
                if (itemsToAmount.containsKey(itemId)) {
                    itemAmount = (itemsToAmount.get(itemId) + itemAmount);
                }
                itemsToAmount.put(itemId, itemAmount);
            }
        }
        while (!doneSelectingItems);
        System.out.println("Done selecting items");
        return itemsToAmount;
    }

    private int staticSelectItem (int storeId) {
        boolean isValidItem;
        String userInput;
        int itemId = -1;
        do {
            System.out.println("Please enter the id of the item you want to order");
            displayItemsBeforePlacingOrder(storeId);
            userInput = scanner.nextLine();
            if (userInput.equals("q") || userInput.equals("Q")) {
                itemId = -1;
                isValidItem = true;
            }
            else {
                try {
                    itemId = Integer.parseInt(userInput);
                    isValidItem = validateItemId(itemId, storeId);
                    if (!isValidItem) {
                        System.out.println("The item is not supplied by the selected store");
                    }
                }
                catch (Exception exception) {
                    isValidItem = false;
                    System.out.println("Item id should be an integer");
                }
            }
        }
        while (!isValidItem);

        return itemId;
    }

    private void displayItemsBeforePlacingOrder (int storeId) {
        stores = controller.getStores().getStores();
        items = controller.getItems().getItems();
        StoreDTO store = this.stores.get(storeId);
        items.keySet().forEach(itemId -> {
            if (store.getItems().containsKey(itemId)) {
                System.out.println("{" + store.getItems().get(itemId).getPricedItem().toString() + "}");
            }
            else {
                System.out.print("{" + items.get(itemId).getItem().toString() + ",\nThis item is not supplied in the store}\n");
            }
        });
    }

    private boolean validateItemId (int itemId, int storeId) {
        return this.stores.get(storeId).getItems().containsKey(itemId);
    }

    private double getItemAmount (int itemId) {
        boolean isValidAmount;
        double itemAmount = 0;
        do {
            System.out.println("Please enter the amount that you want to order");
            try {
                itemAmount = scanner.nextDouble();
                scanner.nextLine();
                isValidAmount = validateItemAmount(itemId, itemAmount);
                if (!isValidAmount) {
                    System.out.println("Invalid amount!\nAmount should be positive. For quantity purchase category, amount should also be an integer");
                }
            }
            catch (Exception exception) {
                isValidAmount = false;
                System.out.println("Invalid amount");
                scanner.nextLine();
            }
        }
        while (!isValidAmount);
        return itemAmount;
    }

    private boolean validateItemAmount (int itemId, Double itemAmount) {
        if (itemAmount < 0) {
            return false;
        }
        // Not an integer is ok only if purchase category is weight
        else if (itemAmount.intValue() < itemAmount) {
            return this.items.get(itemId).getPurchaseCategory().equals(WEIGHT);
        }
        return true;
    }

    private void printOrderSummary (PlaceOrderRequest request) {
        System.out.println("Order summary:\nOrder items:");
        Iterator<Integer> iterator = request.getOrderItemToAmount().keySet().iterator();
        double totalItemsPrice = 0;
        while (iterator.hasNext()) {
            Integer itemId = iterator.next();
            SystemItemDTO item = items.get(itemId);
            double amount = request.getOrderItemToAmount().get(itemId);
            int itemPrice = stores.get(request.getStoreId()).getItems().get(itemId).getPrice();
            double totalItemPrice = round(amount * itemPrice, 2);
            System.out.print(String.format("{Item id: %s,\nItem name: %s,\nPurchase category: %s,\nPrice: %s,\nAmount: %s,\nTotal item price: %s}",
                                           itemId,
                                           item.getName(),
                                           item.getPurchaseCategory(),
                                           itemPrice,
                                           amount,
                                           round(itemPrice * amount, 2)));
            if (iterator.hasNext()) {
                System.out.println(",");
            }
            totalItemsPrice += totalItemPrice;
        }
        System.out.println();
        double distance = calculateDistance((this.stores.get(request.getStoreId()).get_X_Coordinate()),
                                            request.getxCoordinate(),
                                            this.stores.get(request.getStoreId()).get_Y_Coordinate(),
                                            request.getyCoordinate());

        int ppk = this.stores.get(request.getStoreId()).getDeliveryPpk();
        double deliveryPrice = round(distance * ppk, 2);
        System.out.println(String.format("Distance form store: %s\nStore PPK: %s\nDelivery price: %s,\nOrder total price: %s ",
                                         distance,
                                         ppk,
                                         deliveryPrice,
                                         deliveryPrice + totalItemsPrice));
    }

    private double calculateDistance (int x1, int x2, int y1, int y2) {
        return round(Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)), 2);
    }

    public static String concatYearToDate (String dateTime) {
        int year = LocalDateTime.now().getYear();
        try {
            String[] date = dateTime.split("-");
            date[0] = date[0] + "/" + year + "-";
            return date[0] + date[1];
        }
        catch (Exception exception) {
            throw new RuntimeException("Invalid date format");
        }

    }

    public static double round (double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private static class Location {
        private int x;
        private int y;

        public Location (int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}