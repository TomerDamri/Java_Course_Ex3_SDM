package course.java.sdm.engine.model;

import java.time.LocalDate;

public class CustomerFeedback {

    public static final int MIN_RANK = 1;
    public static final int MAX_RANK = 5;

    private Integer storeId;
    private String customerName;
    private LocalDate orderDate;
    private double rank;
    private String textualFeedback;

    public CustomerFeedback (Integer storeId, String customerName, LocalDate orderDate, double rank, String textualFeedback) {
        setRank(rank);
        this.storeId = storeId;
        this.customerName = customerName;
        this.orderDate = orderDate;
        this.textualFeedback = textualFeedback;
    }

    public Integer getStoreId () {
        return storeId;
    }

    public String getCustomerName () {
        return customerName;
    }

    public LocalDate getOrderDate () {
        return orderDate;
    }

    public double getRank () {
        return rank;
    }

    public String getTextualFeedback () {
        return textualFeedback;
    }

    private void validateRank (double rank) {
        if (rank < MIN_RANK || rank > MAX_RANK) {
            throw new RuntimeException(String.format("The rank '%s' is invalid!\nThe rank has to be between '%s-%s'",
                                                     rank,
                                                     MIN_RANK,
                                                     MAX_RANK));
        }
    }

    private void setRank (double rank) {
        validateRank(rank);
        this.rank = rank;
    }

}
