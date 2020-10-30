package model;

public class ItemToAddDTO {
    private final Integer price;
    private final Integer id;

    public ItemToAddDTO (Integer price, Integer id) {
        this.price = price;
        this.id = id;
    }

    public Integer getPrice () {
        return price;
    }

    public Integer getId () {
        return id;
    }
}
