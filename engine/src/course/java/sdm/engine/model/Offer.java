package course.java.sdm.engine.model;

import java.util.Objects;

public class Offer {

    private double quantity;
    private int itemId;
    private int forAdditional;

    public Offer (double quantity, int itemId, int forAdditional) {
        this.quantity = quantity;
        this.itemId = itemId;
        this.forAdditional = forAdditional;
    }

    public double getQuantity () {
        return quantity;
    }

    public int getItemId () {
        return itemId;
    }

    public int getForAdditional () {
        return forAdditional;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Offer))
            return false;
        Offer offer = (Offer) o;
        return Double.compare(offer.getQuantity(), getQuantity()) == 0 && getItemId() == offer.getItemId()
                    && getForAdditional() == offer.getForAdditional();
    }

    @Override
    public int hashCode () {
        return Objects.hash(getQuantity(), getItemId(), getForAdditional());
    }
}