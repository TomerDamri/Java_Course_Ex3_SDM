package course.java.sdm.engine.model;

import java.util.Map;
import java.util.Objects;

public class ThenYouGet {

    protected Map<Integer, Offer> offers;
    protected DiscountType operator;

    public ThenYouGet (Map<Integer, Offer> offers, String operator) {
        this.offers = offers;
        this.operator = DiscountType.createDiscountType(operator);
    }

    public Map<Integer, Offer> getOffers () {
        return offers;
    }

    public DiscountType getOperator () {
        return operator;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ThenYouGet))
            return false;
        ThenYouGet that = (ThenYouGet) o;
        return getOffers().equals(that.getOffers()) && getOperator() == that.getOperator();
    }

    @Override
    public int hashCode () {
        return Objects.hash(getOffers(), getOperator());
    }

    public enum DiscountType {
        IRRELEVANT, OR, AND;

        public static final String ALL_OR_NOTHING = "ALL-OR-NOTHING";
        private static final String ONE_OF = "ONE-OF";

        private static DiscountType createDiscountType (String operator) {
            switch (operator) {
            case ONE_OF:
                return OR;
            case ALL_OR_NOTHING:
                return AND;
            default:
                return IRRELEVANT;
            }
        }
    }
}