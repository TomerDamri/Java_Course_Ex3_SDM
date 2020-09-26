package model;

import java.util.Map;

public class DiscountDTO {
    private final String name;
    private final Integer ifYouBuyItemId;
    private final Double ifYouBuyQuantity;
    protected final DiscountDTO.DiscountType operator;
    protected final Map<Integer, OfferDTO> offers;


    public DiscountDTO(String name, int ifYouBuyItemId, double ifYouBuyQuantity, String operator, Map<Integer, OfferDTO> offers) {
        this.name = name;
        this.ifYouBuyItemId = ifYouBuyItemId;
        this.ifYouBuyQuantity = ifYouBuyQuantity;
        this.operator = DiscountType.toOperatorDTO(operator);
        this.offers = offers;
    }

    public String getName() {
        return name;
    }

    public Integer getIfYouBuyItemId() {
        return ifYouBuyItemId;
    }

    public Double getIfYouBuyQuantity() {
        return ifYouBuyQuantity;
    }

    public DiscountType getOperator() {
        return operator;
    }

    public Map<Integer, OfferDTO> getOffers() {
        return offers;
    }

    public enum DiscountType {
        IRRELEVANT, ONE_OF, ALL_OR_NOTHING;

        private static final String OR = "OR";
        public static final String AND = "AND";

        private static DiscountDTO.DiscountType toOperatorDTO(String operator) {

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
