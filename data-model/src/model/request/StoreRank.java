package model.request;

public class StoreRank {

    private Integer storeId;
    private double rank;
    private String textualFeedback;

    public StoreRank (Integer storeId, double rank, String textualFeedback) {
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
