package model;

import java.util.List;

public class DiscountDTO {
    private final String storeName;
    private final String discountName;
    private final Integer ifYouBuyItemId;
    private final String ifYouBuyItemName;
    private final Double ifYouBuyQuantity;
    protected final DiscountDTO.DiscountType operator;
    protected final List<OfferDTO> offers;

    public DiscountDTO (String storeName,
                        String discountName,
                        int ifYouBuyItemId,
                        String ifYouBuyItemName,
                        double ifYouBuyQuantity,
                        String operator,
                        List<OfferDTO> offers) {
        this.storeName = storeName;
        this.discountName = discountName;
        this.ifYouBuyItemId = ifYouBuyItemId;
        this.ifYouBuyItemName = ifYouBuyItemName;
        this.ifYouBuyQuantity = ifYouBuyQuantity;
        this.operator = DiscountType.toOperatorDTO(operator);
        this.offers = offers;
    }

    public String getDiscountName () {
        return discountName;
    }

    public Integer getIfYouBuyItemId () {
        return ifYouBuyItemId;
    }

    public Double getIfYouBuyQuantity () {
        return ifYouBuyQuantity;
    }

    public DiscountType getOperator () {
        return operator;
    }

    public List<OfferDTO> getOffers () {
        return offers;
    }

    public String getStoreName () {
        return storeName;
    }

    public String getIfYouBuyItemName () {
        return ifYouBuyItemName;
    }

    public enum DiscountType {
        IRRELEVANT, ONE_OF, ALL_OR_NOTHING;

        private static final String OR = "OR";
        public static final String AND = "AND";

        private static DiscountDTO.DiscountType toOperatorDTO (String operator) {

            switch (operator) {
            case OR:
                return ONE_OF;
            case AND:
                return ALL_OR_NOTHING;
            default:
                return IRRELEVANT;
            }
        }
    }
}
