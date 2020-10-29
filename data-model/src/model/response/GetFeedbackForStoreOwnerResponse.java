package model.response;

import java.util.List;

import model.StoreFeedbackDTO;

public class GetFeedbackForStoreOwnerResponse {

    List<StoreFeedbackDTO> orderStoreRanks;

    public GetFeedbackForStoreOwnerResponse (List<StoreFeedbackDTO> orderStoreRanks) {
        this.orderStoreRanks = orderStoreRanks;
    }

    public List<StoreFeedbackDTO> getOrderStoreRanks () {
        return orderStoreRanks;
    }
}
