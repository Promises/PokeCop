package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.specs.Directions;
import inf112.skeleton.common.utility.Tools;

public class MovementPacket implements PacketData {
    Directions direction;
    int lenght;

    public MovementPacket(Directions direction, int lenght) {
        this.direction = direction;
        this.lenght = lenght;
    }

    public Directions getDirection() {
        return direction;
    }

    public void setDirection(Directions direction) {
        this.direction = direction;
    }

    public int getLenght() {
        return lenght;
    }

    public void setLenght(int lenght) {
        this.lenght = lenght;
    }


    public static MovementPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), MovementPacket.class);
    }

}
