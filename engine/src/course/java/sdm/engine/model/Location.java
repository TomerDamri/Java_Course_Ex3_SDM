package course.java.sdm.engine.model;

import java.io.Serializable;
import java.util.Objects;

public class Location implements Serializable {
    private final Integer MIN_VALUE = 0;
    private final Integer MAX_VALUE = 50;
    private int y;
    private int x;

    public Location (int x, int y) {
        if (!isValidLocation(x, y)) {
            throw new IndexOutOfBoundsException("The Location (" + x + "," + y + ") is not valid. Location should be of range [" + MIN_VALUE
                        + ", " + MAX_VALUE + "]");
        }
        this.x = x;
        this.y = y;
    }

    public int getY () {
        return y;
    }

    public int getX () {
        return x;
    }

    @Override
    public boolean equals (Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Location location = (Location) o;
        return y == location.y && x == location.x;
    }

    @Override
    public int hashCode () {
        return Objects.hash(MIN_VALUE, MAX_VALUE, y, x);
    }

    private boolean isValidLocation (int x, int y) {
        return isaValidValue(x) && isaValidValue(y);
    }

    private boolean isaValidValue (int z) {
        return z >= MIN_VALUE && z <= MAX_VALUE;
    }

    @Override
    public String toString () {
        return new StringBuilder("(").append(x).append(",").append(y).append(")").toString();
    }
}
