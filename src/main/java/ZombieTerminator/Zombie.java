package ZombieTerminator;

public class Zombie extends GameParticipant{

    private int hp = 2;
    private boolean isMovable = true;

    public Zombie(int xLocation, int yLocation) {
        super(xLocation, yLocation);
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public boolean isMovable() {
        return isMovable;
    }

    public void setMovable(boolean movable) {
        isMovable = movable;
    }
}
