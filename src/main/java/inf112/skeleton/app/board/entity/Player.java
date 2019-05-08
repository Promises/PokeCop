package inf112.skeleton.app.board.entity;

import com.badlogic.gdx.math.Vector2;
import inf112.skeleton.common.specs.Direction;

public class Player {
    private final String uuid;
    int slot;
    public String name;
    Vector2 initialPos;
    Direction initialDirection;

    /**
     * @param uuid      Unique id of owner
     * @param name
     * @param pos
     * @param slot
     * @param direction
     */
    public Player(String uuid, String name, Vector2 pos, int slot, Direction direction) {
        this.uuid = uuid;
        this.name = name;
        this.slot = slot;
        this.initialPos = pos;
        this.initialDirection = direction;
    }

    /**
     * If robot is not yet created for player it should create it.
     */
    public void update() {

    }

    /**
     * Get the unique id of the player
     *
     * @return unique id
     */
    public String getUUID() {
        return uuid;
    }
}
