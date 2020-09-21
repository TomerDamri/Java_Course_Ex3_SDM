package model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocationDTO)) return false;
        LocationDTO that = (LocationDTO) o;
        return getxCoordinate().equals(that.getxCoordinate()) &&
                getyCoordinate().equals(that.getyCoordinate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getxCoordinate(), getyCoordinate());
    }
}