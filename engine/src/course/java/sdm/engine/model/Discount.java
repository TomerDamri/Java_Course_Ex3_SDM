package course.java.sdm.engine.model;

import java.util.Objects;

public class Discount {

    protected String name;
    protected IfYouBy ifYouBuy;
    protected ThenYouGet thenYouGet;

    public Discount (String name, IfYouBy ifYouBuy, ThenYouGet thenYouGet) {
        this.name = name;
        this.ifYouBuy = ifYouBuy;
        this.thenYouGet = thenYouGet;
    }

    public String getName () {
        return name;
    }

    public int getDiscountItemId () {
        return ifYouBuy.getItemId();
    }

    public IfYouBy getIfYouBuy () {
        return ifYouBuy;
    }

    public ThenYouGet getThenYouGet () {
        return thenYouGet;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Discount))
            return false;
        Discount discount = (Discount) o;
        return getName().equals(discount.getName()) && getIfYouBuy().equals(discount.getIfYouBuy())
                    && getThenYouGet().equals(discount.getThenYouGet());
    }

    @Override
    public int hashCode () {
        return Objects.hash(getName(), getIfYouBuy(), getThenYouGet());
    }
}