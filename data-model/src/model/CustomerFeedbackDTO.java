package model;

import java.time.LocalDate;

public class CustomerFeedbackDTO {

    private Integer feedbackId;
    private LocalDate orderDate;
    private double rank;
    private String textualFeedback;
    private String customerName;

    public CustomerFeedbackDTO (Integer feedbackId, LocalDate orderDate, double rank, String textualFeedback, String customerName) {
        this.feedbackId = feedbackId;
        this.orderDate = orderDate;
        this.rank = rank;
        this.textualFeedback = textualFeedback;
        this.customerName = customerName;
    }

    public Integer getFeedbackId () {
        return feedbackId;
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

    public String getCustomerName () {
        return customerName;
    }
}
