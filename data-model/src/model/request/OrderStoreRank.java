package model.request;

public class OrderStoreRank {

    private Integer storeId;
    private double rank;
    private String textualFeedback;

    public OrderStoreRank (Integer storeId, double rank, String textualFeedback) {
        this.storeId = storeId;
        this.rank = rank;
        this.textualFeedback = textualFeedback;
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
