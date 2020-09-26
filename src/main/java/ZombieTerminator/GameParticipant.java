package ZombieTerminator;

import java.util.Objects;

public class GameParticipant {

    private int xLocation;
    private int yLocation;

    public GameParticipant(int xLocation, int yLocation) {
        this.xLocation = xLocation;
        this.yLocation = yLocation;
    }

    public int getxLocation() {
        return xLocation;
    }

    public void setxLocation(int xLocation) {
        this.xLocation = xLocation;
    }

    public int getyLocation() {
        return yLocation;
    }

    public void setyLocation(int yLocation) {
        this.yLocation = yLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameParticipant that = (GameParticipant) o;
        return xLocation == that.xLocation &&
                yLocation == that.yLocation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(xLocation, yLocation);
    }
}
