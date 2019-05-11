package inf112.skeleton.common.packet.data;

import com.google.gson.JsonObject;
import inf112.skeleton.common.utility.Tools;

public class KeyPacket implements PacketData {
    private int key;
    private boolean down;

    public KeyPacket(int key, boolean down) {
        this.key = key;
        this.down = down;
    }


    public int getKey() {
        return key;
    }

    public boolean isDown() {
        return down;
    }

    public static KeyPacket parseJSON(JsonObject jsonObject) {
        return Tools.GSON.fromJson(jsonObject.get("data"), KeyPacket.class);
    }
}
