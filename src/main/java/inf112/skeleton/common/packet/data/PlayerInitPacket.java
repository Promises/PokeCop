package inf112.skeleton.common.packet.data;


import com.badlogic.gdx.math.Vector2;
import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.Direction;
import inf112.skeleton.common.utility.Tools;

public class PlayerInitPacket implements PacketData {
    Vector2 pos;
    Vector2 movingTo;
    int health;
    int slot;
    String name;
    Direction facing;
    String uuid;

    public PlayerInitPacket(String uuid, String name, Vector2 pos, int health, int slot, Direction facing) {
        this.uuid = uuid;
        this.name = name;
        this.pos = pos;
        this.movingTo = new Vector2(pos.x, pos.y);
        this.health = health;
        this.slot = slot;
        this.facing = facing;
    }


    public String getName() {
        return name;
    }
    public String getUUID() {
        return uuid;
    }


    public Vector2 getPos() {
        return pos;
    }


    public int getHealth() {
        return health;
    }


    public int getSlot() {
        return slot;
    }

    public Direction getFacing() {
        return facing;
    }


    public static PlayerInitPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), PlayerInitPacket.class);
    }

}
