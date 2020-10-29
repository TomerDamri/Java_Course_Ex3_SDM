package model;

import java.util.List;

public class StoreFeedbackDTO {

    private Integer storeId;
    private String storeName;
    private List<CustomerFeedbackDTO> customersFeedbacks;

    public StoreFeedbackDTO (Integer storeId, String storeName, List<CustomerFeedbackDTO> customersFeedbacks) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.customersFeedbacks = customersFeedbacks;
    }

    public Integer getStoreId () {
        return storeId;
    }

    public List<CustomerFeedbackDTO> getCustomersFeedbacks () {
        return customersFeedbacks;
    }

    public String getStoreName () {
        return storeName;
    }
}
