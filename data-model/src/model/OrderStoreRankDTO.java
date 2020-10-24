package model;

import java.time.LocalDate;

public class OrderStoreRankDTO {

    private String zoneName;
    private Integer storeId;
    private LocalDate orderDate;
    private double rank;
    private String textualFeedback;
    private String customerName;

    public OrderStoreRankDTO (String zoneName, Integer storeId, String customerName,LocalDate orderDate,  double rank, String textualFeedback) {
        this.zoneName = zoneName;
        this.storeId = storeId;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.rank = rank;
        this.textualFeedback = textualFeedback;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public String getCustomerName () {
        return customerName;
    }

    public String getZoneName () {
        return zoneName;
    }

    public Integer getStoreId () {
        return storeId;
    }

    public double getRank () {
        return rank;
    }

    public String getTextualFeedback () {
        return textualFeedback;
    }
}
