package model.response;

import java.util.List;

import model.OrderStoreRankDTO;

public class GetFeedbackForStoreOwnerResponse {

    List<OrderStoreRankDTO> orderStoreRanks;

    public GetFeedbackForStoreOwnerResponse (List<OrderStoreRankDTO> orderStoreRanks) {
        this.orderStoreRanks = orderStoreRanks;
    }

    public List<OrderStoreRankDTO> getOrderStoreRanks () {
        return orderStoreRanks;
    }
}
