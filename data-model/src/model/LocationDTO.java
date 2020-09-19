package model;

public class LocationDTO {
    private final Integer xCoordinate;
    private final Integer yCoordinate;

    public LocationDTO (int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }

    public Integer getxCoordinate () {
        return xCoordinate;
    }

    public Integer getyCoordinate () {
        return yCoordinate;
    }

    @Override
    public String toString () {
        return new StringBuilder("(").append(xCoordinate).append(",").append(yCoordinate).append(")").toString();
    }
}