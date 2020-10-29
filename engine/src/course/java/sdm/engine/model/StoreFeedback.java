package course.java.sdm.engine.model;

import java.util.List;

public class StoreFeedback {

    private Integer storeId;
    private String storeName;
    private List<CustomerFeedback> customersFeedbacks;

    public StoreFeedback (Integer storeId, String storeName, List<CustomerFeedback> customersFeedbacks) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.customersFeedbacks = customersFeedbacks;
    }

    public Integer getStoreId () {
        return storeId;
    }

    public List<CustomerFeedback> getCustomersFeedbacks () {
        return customersFeedbacks;
    }

    public String getStoreName () {
        return storeName;
    }
}
