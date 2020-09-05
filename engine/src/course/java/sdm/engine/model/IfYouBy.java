package course.java.sdm.engine.model;

import java.util.Objects;

public class IfYouBy {
    private double quantity;
    private int itemId;

    public IfYouBy (int itemId, double quantity) {
        this.quantity = quantity;
        this.itemId = itemId;
    }

    public double getQuantity () {
        return quantity;
    }

    public int getItemId () {
        return itemId;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o)
            return true;
        if (!(o instanceof IfYouBy))
            return false;
        IfYouBy ifYouBy = (IfYouBy) o;
        return Double.compare(ifYouBy.getQuantity(), getQuantity()) == 0 && getItemId() == ifYouBy.getItemId();
    }

    @Override
    public int hashCode () {
        return Objects.hash(getQuantity(), getItemId());
    }
}