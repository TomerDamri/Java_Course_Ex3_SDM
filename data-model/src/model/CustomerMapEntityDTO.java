package model;

public class CustomerMapEntityDTO extends MapEntity {
    public CustomerMapEntityDTO (Integer id, LocationDTO location, String name, Integer numOfOrders) {
        super(id, location, name, numOfOrders);
    }

    @Override
    public String toString () {
        return new StringBuilder().append("Customer details:\n\n")
                                  .append("id= ")
                                  .append(id)
                                  .append(",\nname= '")
                                  .append(name)
                                  .append('\'')
                                  .append(",\nlocation= ")
                                  .append(location)
                                  .append(",\nnumOfOrders= ")
                                  .append(numOfOrders)
                                  .toString();
    }
}