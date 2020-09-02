package course.java.sdm.engine.utils.systemUpdater;

import java.util.Map;
import java.util.UUID;

import course.java.sdm.engine.model.DynamicOrder;

public class SystemUpdaterValidator {

    public void validateDynamicOrderExist (UUID dynamicOrderId, Map<UUID, DynamicOrder> dynamicOrders) {
        if (!dynamicOrders.containsKey(dynamicOrderId)) {
            throw new RuntimeException(String.format("There is no order with ID: %s", dynamicOrderId));
        }
    }

    public void validateDynamicOrderNotConfirmedYet (UUID dynamicOrderId, DynamicOrder dynamicOrder) {
        if (dynamicOrder.isConfirmed()) {
            throw new RuntimeException(String.format("The dynamic order with ID: %s already completed", dynamicOrderId));
        }
    }
}