package course.java.sdm.engine.model;

import java.util.List;
import java.util.Objects;

public class ThenYouGet {

    protected List<Offer> Offers;
    protected DiscountType operator;

    public ThenYouGet (List<Offer> offers, String operator) {
        Offers = offers;
        this.operator = DiscountType.createDiscountType(operator);
    }

    public List<Offer> getOffers () {
        return Offers;
    }

    public DiscountType getOperator () {
        return operator;
    }

    public enum DiscountType {
        IRRELEVENT, OR, AND;

        private static final String ONE_OF = "ONE-OF";
        public static final String ALL_OR_NOTHING = "ALL-OR-NOTHING";

        private static DiscountType createDiscountType (String operator) {
            switch (operator) {
            case ONE_OF:
                return OR;
            case ALL_OR_NOTHING:
                return AND;
            default:
                return IRRELEVENT;
            }
        }
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
}