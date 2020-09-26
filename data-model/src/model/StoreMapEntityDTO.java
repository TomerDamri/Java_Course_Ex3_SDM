package model;

public class StoreMapEntityDTO extends MapEntity {

    private final Integer ppk;

    public StoreMapEntityDTO (Integer id, LocationDTO location, String name, Integer numOfOrders, Integer ppk) {
        super(id, location, name, numOfOrders);
        this.ppk = ppk;
    }

    public Integer getPpk () {
        return ppk;
    }

    @Override
    public String toString () {
        return new StringBuilder().append("Store details:\n\n").append("id= ")
                                  .append(id)
                                  .append(",\nname= '")
                                  .append(name)
                                  .append('\'')
                                  .append(",\nlocation= ")
                                  .append(location)
                                  .append(",\nnumOfOrders= ")
                                  .append(numOfOrders)
                                  .append(",\nppk=")
                                  .append(ppk)
                                  .toString();
    }
}